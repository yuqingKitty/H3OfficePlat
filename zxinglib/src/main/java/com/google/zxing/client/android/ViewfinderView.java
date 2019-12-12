/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.camera.CameraManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {

  private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
  private static final long ANIMATION_DELAY = 80L;
  private static final int CURRENT_POINT_OPACITY = 0xA0;
  private static final int MAX_RESULT_POINTS = 20;
  private static final int POINT_SIZE = 6;

  private CameraManager cameraManager;
  private final Paint paint;
  private Bitmap resultBitmap;
  private final int maskColor;
  private final int resultColor;
  private final int laserColor;
  private final int resultPointColor;
  private int scannerAlpha;
  private List<ResultPoint> possibleResultPoints;
  private List<ResultPoint> lastPossibleResultPoints;

  // This constructor is used when the class is built from an XML resource.
  public ViewfinderView(Context context, AttributeSet attrs) {
    super(context, attrs);

    // Initialize these once for performance rather than calling them every time in onDraw().
    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Resources resources = getResources();
    maskColor = resources.getColor(R.color.viewfinder_mask);
    resultColor = resources.getColor(R.color.result_view);
    laserColor = resources.getColor(R.color.viewfinder_laser);
    resultPointColor = resources.getColor(R.color.possible_result_points);
    scannerAlpha = 0;
    possibleResultPoints = new ArrayList<>(5);
    lastPossibleResultPoints = null;
  }

  public void setCameraManager(CameraManager cameraManager) {
    this.cameraManager = cameraManager;
  }

  @SuppressLint("DrawAllocation")
  @Override
  public void onDraw(Canvas canvas) {
    if (cameraManager == null) {
      return; // not ready yet, early draw before done configuring
    }
    Rect frame = cameraManager.getFramingRect();
    Rect previewFrame = cameraManager.getFramingRectInPreview();
    if (frame == null || previewFrame == null) {
      return;
    }
    int width = canvas.getWidth();
    int height = canvas.getHeight();

    // Draw the exterior (i.e. outside the framing rect) darkened
    // 绘制聚焦框外的暗色透明层
    paint.setColor(resultBitmap != null ? resultColor : maskColor);
    canvas.drawRect(0, 0, width, frame.top, paint);
    canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
    canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
    canvas.drawRect(0, frame.bottom + 1, width, height, paint);

    //绘制扫码框4个边角弧形
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      //左上角
      Path ltPath1 = new Path();
      Path ltPath2 = new Path();
      ltPath1.addRect(frame.left, frame.top, frame.left + 20, frame.top + 20, Path.Direction.CCW);
      ltPath2.addCircle(frame.left + 20, frame.top + 20, 20, Path.Direction.CCW);
      ltPath1.op(ltPath2, Path.Op.DIFFERENCE);
      canvas.drawPath(ltPath1, paint);
      //右上角
      Path rtPath1 = new Path();
      Path rtPath2 = new Path();
      rtPath1.addRect(frame.right - 20, frame.top, frame.right + 1, frame.top + 20, Path.Direction.CCW);
      rtPath2.addCircle(frame.right - 20, frame.top + 20, 20, Path.Direction.CCW);
      rtPath1.op(rtPath2, Path.Op.DIFFERENCE);
      canvas.drawPath(rtPath1, paint);
      //左下角
      Path lbPath1 = new Path();
      Path lbPath2 = new Path();
      lbPath1.addRect(frame.left, frame.bottom - 20, frame.left + 20, frame.bottom + 1, Path.Direction.CCW);
      lbPath2.addCircle(frame.left + 20, frame.bottom - 20, 20, Path.Direction.CCW);
      lbPath1.op(lbPath2, Path.Op.DIFFERENCE);
      canvas.drawPath(lbPath1, paint);
      //右下角
      Path rbPath1 = new Path();
      Path rbPath2 = new Path();
      rbPath1.addRect(frame.right - 20, frame.bottom - 20, frame.right + 1, frame.bottom + 1, Path.Direction.CCW);
      rbPath2.addCircle(frame.right - 20, frame.bottom - 20, 20, Path.Direction.CCW);
      rbPath1.op(rbPath2, Path.Op.DIFFERENCE);
      canvas.drawPath(rbPath1, paint);
    }

    if (resultBitmap != null) {
      // Draw the opaque result bitmap over the scanning rectangle
      // 如果扫描结果不为空，则把扫描的结果填充到聚焦框中
      paint.setAlpha(CURRENT_POINT_OPACITY);
      canvas.drawBitmap(resultBitmap, null, frame, paint);
    } else {
      // 画一根红色的激光线表示二维码解码正在进行
      // Draw a red "laser scanner" line through the middle to show decoding is active
      paint.setColor(laserColor);
      paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
      scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
      int middle = frame.height() / 2 + frame.top;
      canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);

      float scaleX = frame.width() / (float) previewFrame.width();
      float scaleY = frame.height() / (float) previewFrame.height();

      List<ResultPoint> currentPossible = possibleResultPoints;
      List<ResultPoint> currentLast = lastPossibleResultPoints;
      int frameLeft = frame.left;
      int frameTop = frame.top;
      // 绘制解析过程中可能扫描到的关键点，使用黄色小圆点表示
      if (currentPossible.isEmpty()) {
        lastPossibleResultPoints = null;
      } else {
        possibleResultPoints = new ArrayList<>(5);
        lastPossibleResultPoints = currentPossible;
        paint.setAlpha(CURRENT_POINT_OPACITY);
        paint.setColor(resultPointColor);
        synchronized (currentPossible) {
          for (ResultPoint point : currentPossible) {
            canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX),
                              frameTop + (int) (point.getY() * scaleY),
                              POINT_SIZE, paint);
          }
        }
      }
      if (currentLast != null) {
        paint.setAlpha(CURRENT_POINT_OPACITY / 2);
        paint.setColor(resultPointColor);
        synchronized (currentLast) {
          float radius = POINT_SIZE / 2.0f;
          for (ResultPoint point : currentLast) {
            canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX),
                              frameTop + (int) (point.getY() * scaleY),
                              radius, paint);
          }
        }
      }

      // Request another update at the animation interval, but only repaint the laser line,
      // not the entire viewfinder mask.
      // 重绘聚焦框里的内容，不需要重绘整个界面。
      postInvalidateDelayed(ANIMATION_DELAY,
                            frame.left - POINT_SIZE,
                            frame.top - POINT_SIZE,
                            frame.right + POINT_SIZE,
                            frame.bottom + POINT_SIZE);
    }
  }

  public void drawViewfinder() {
    Bitmap resultBitmap = this.resultBitmap;
    this.resultBitmap = null;
    if (resultBitmap != null) {
      resultBitmap.recycle();
    }
    invalidate();
  }

  /**
   * Draw a bitmap with the result points highlighted instead of the live scanning display.
   *
   * @param barcode An image of the decoded barcode.
   */
  public void drawResultBitmap(Bitmap barcode) {
    resultBitmap = barcode;
    invalidate();
  }

  public void addPossibleResultPoint(ResultPoint point) {
    List<ResultPoint> points = possibleResultPoints;
    synchronized (points) {
      points.add(point);
      int size = points.size();
      if (size > MAX_RESULT_POINTS) {
        // trim it
        points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
      }
    }
  }

}
