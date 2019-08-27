package com.aotuo.h3officeplat.activity;

import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.aotuo.h3officeplat.R;
import com.aotuo.h3officeplat.utils.SharedPreferencesHelper;

import butterknife.BindView;
import butterknife.OnClick;

public class SplashActivity extends BaseActivity implements GestureDetector.OnGestureListener {
    static final int TIME_OUT = 1000;
    static final int MSG_TIME_OUT = 1;

    @BindView(R.id.iv_splash)
    ImageView iv_splash;
    @BindView(R.id.tv_jump)
    TextView tv_jump;
    @BindView(R.id.vf_welcome)
    ViewFlipper vf_welcome;
    @BindView(R.id.tv_next)
    TextView tv_next;

    private GestureDetector gestureDetector;
    Animation leftInAnimation, leftOutAnimation, rightInAnimation, rightOutAnimation;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TIME_OUT:
                    todoNext();
                    return true;
            }
            return false;
        }
    });

    @Override
    protected int getLayout() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {
        if (isFirstUse()) {
            iv_splash.setVisibility(View.GONE);
            vf_welcome.setVisibility(View.VISIBLE);
            tv_jump.setVisibility(View.VISIBLE);
        } else {
            handler.sendEmptyMessageDelayed(MSG_TIME_OUT, TIME_OUT);
        }

        gestureDetector = new GestureDetector(this);
        leftInAnimation = AnimationUtils.loadAnimation(this, R.anim.left_in);
        leftOutAnimation = AnimationUtils.loadAnimation(this, R.anim.left_out);
        rightInAnimation = AnimationUtils.loadAnimation(this, R.anim.right_in);
        rightOutAnimation = AnimationUtils.loadAnimation(this, R.anim.right_out);
    }

    private boolean isFirstUse() {
        return SharedPreferencesHelper.getInstance().getAppData(SharedPreferencesHelper.KEY_APP_FIRST_USE, true);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1.getX() - e2.getX() > 120) {
            if (vf_welcome.getDisplayedChild() < vf_welcome.getChildCount() - 1) {
                vf_welcome.setInAnimation(leftInAnimation);
                vf_welcome.setOutAnimation(leftOutAnimation);
                vf_welcome.showNext();//向右滑动

                if (vf_welcome.getDisplayedChild() == vf_welcome.getChildCount() - 1){
                    tv_jump.setVisibility(View.GONE);
                } else {
                    tv_jump.setVisibility(View.VISIBLE);
                }
            }
            return true;
        } else if (e1.getX() - e2.getY() < -120) {
            if (vf_welcome.getDisplayedChild() > 0) {
                vf_welcome.setInAnimation(rightInAnimation);
                vf_welcome.setOutAnimation(rightOutAnimation);
                vf_welcome.showPrevious();//向左滑动
                tv_jump.setVisibility(View.VISIBLE);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @OnClick({R.id.tv_jump, R.id.tv_next})
    void click(View view) {
        SharedPreferencesHelper.getInstance().setAppData(SharedPreferencesHelper.KEY_APP_FIRST_USE, false);
        switch (view.getId()) {
            case R.id.tv_jump:
            case R.id.tv_next:
                // 跳转到服务器配置地址
                changeView(ConfigServerAddressActivity.class);
                finish();
                break;
        }
    }

    private void todoNext() {
        // 跳转至登录页面
        changeView(ConfigServerAddressActivity.class);
//        changeView(SettingActivity.class);
        finish();
    }

}
