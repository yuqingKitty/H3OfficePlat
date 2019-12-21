package com.aotuo.h3officeplat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;

import com.aotuo.h3officeplat.R;

import butterknife.BindView;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class ScanCodeActivity extends BaseActivity implements QRCodeView.Delegate {
    public static final String ResultDataKey = "RESULT";

    @BindView(R.id.zxing_view)
    ZXingView mZXingView;

    @Override
    protected int getLayout() {
        return R.layout.activity_scan_code;
    }

    @Override
    protected void initView() {
        mZXingView.setDelegate(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mZXingView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
        mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
    }

    @Override
    protected void onStop() {
        mZXingView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mZXingView.onDestroy(); // 销毁二维码扫描控件
        super.onDestroy();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    /**
     * 处理扫描结果
     *
     * @param result 摄像头扫码时只要回调了该方法 result 就一定有值，不会为 null。解析本地图片或 Bitmap 时 result 可能为 null
     */
    @Override
    public void onScanQRCodeSuccess(String result) {
        vibrate();
        mZXingView.startSpot(); // 开始识别
        Intent intent = new Intent();
        intent.putExtra(ResultDataKey, result);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 摄像头环境亮度发生变化
     *
     * @param isDark 是否变暗
     */
    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
//        String tipText = mZXingView.getScanBoxView().getTipText();
//        String ambientBrightnessTip = "\n环境过暗，请打开闪光灯";
//        if (isDark) {
//            if (!tipText.contains(ambientBrightnessTip)) {
//                mZXingView.getScanBoxView().setTipText(tipText + ambientBrightnessTip);
//            }
//        } else {
//            if (tipText.contains(ambientBrightnessTip)) {
//                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip));
//                mZXingView.getScanBoxView().setTipText(tipText);
//            }
//        }
    }

    /**
     * 处理打开相机出错
     */
    @Override
    public void onScanQRCodeOpenCameraError() {
        showToast(getString(R.string.fail_open_camera));
    }

}



