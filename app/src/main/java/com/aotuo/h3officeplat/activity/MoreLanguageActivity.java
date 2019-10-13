package com.aotuo.h3officeplat.activity;

import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aotuo.h3officeplat.R;
import com.aotuo.h3officeplat.bean.MessageEvent;
import com.aotuo.h3officeplat.utils.LanguageUtil;
import com.aotuo.h3officeplat.utils.SharedPreferencesHelper;
import com.aotuo.h3officeplat.view.TitleView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;

import static com.aotuo.h3officeplat.utils.SharedPreferencesHelper.KEY_APP_USE_LANGUAGE;
import static com.aotuo.h3officeplat.utils.SharedPreferencesHelper.KEY_APP_USE_LANGUAGE_EN;
import static com.aotuo.h3officeplat.utils.SharedPreferencesHelper.KEY_APP_USE_LANGUAGE_ZH;

public class MoreLanguageActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.title_view)
    TitleView title_view;
    @BindView(R.id.rl_language_chinese)
    RelativeLayout rl_language_chinese;
    @BindView(R.id.tv_language_chinese)
    TextView tv_language_chinese;
    @BindView(R.id.iv_chinese_selected)
    ImageView iv_chinese_selected;
    @BindView(R.id.rl_language_english)
    RelativeLayout rl_language_english;
    @BindView(R.id.tv_language_english)
    TextView tv_language_english;
    @BindView(R.id.iv_english_selected)
    ImageView iv_english_selected;

    private int selectedPosition;


    @Override
    protected int getLayout() {
        return R.layout.activity_more_language;
    }

    @Override
    protected void initView() {
        String language = SharedPreferencesHelper.getInstance().getAppData(KEY_APP_USE_LANGUAGE, KEY_APP_USE_LANGUAGE_ZH);
        if (language.equals(KEY_APP_USE_LANGUAGE_ZH)) {
            selectedPosition = 0;
        } else {
            selectedPosition = 1;
        }
        refreshUI();

        title_view.getBackBtn().setOnClickListener(this);
        title_view.getRightTitle().setOnClickListener(this);
        rl_language_chinese.setOnClickListener(this);
        rl_language_english.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right_title:
                // 完成
                if (selectedPosition == 0) {
                    SharedPreferencesHelper.getInstance().setAppData(KEY_APP_USE_LANGUAGE, KEY_APP_USE_LANGUAGE_ZH);
                    EventBus.getDefault().post(new MessageEvent("LANGUAGE_ZH"));
                } else {
                    SharedPreferencesHelper.getInstance().setAppData(KEY_APP_USE_LANGUAGE, KEY_APP_USE_LANGUAGE_EN);
                    EventBus.getDefault().post(new MessageEvent("LANGUAGE_EN"));
                }
                changeLanguage();
                break;
            case R.id.rl_language_chinese:
                selectedPosition = 0;
                refreshUI();
                break;
            case R.id.rl_language_english:
                selectedPosition = 1;
                refreshUI();
                break;
        }
    }

    private void refreshUI() {
        if (selectedPosition == 0) {
            tv_language_chinese.setTextColor(getResources().getColor(R.color.color_38ADFF));
            iv_chinese_selected.setVisibility(View.VISIBLE);
            tv_language_english.setTextColor(getResources().getColor(R.color.color_2C3038));
            iv_english_selected.setVisibility(View.GONE);
        } else {
            tv_language_english.setTextColor(getResources().getColor(R.color.color_38ADFF));
            iv_english_selected.setVisibility(View.VISIBLE);
            tv_language_chinese.setTextColor(getResources().getColor(R.color.color_2C3038));
            iv_chinese_selected.setVisibility(View.GONE);
        }
    }


    /**
     * 如果是7.0以下，我们需要调用changeAppLanguage方法
     * 如果是7.0及以上系统，直接把我们想要切换的语言类型保存在SharedPreferences中,然后重新启动SettingActivity即可
     */
    private void changeLanguage() {
        String language = SharedPreferencesHelper.getInstance().getAppData(KEY_APP_USE_LANGUAGE, KEY_APP_USE_LANGUAGE_ZH);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            LanguageUtil.changeAppLanguage(this, language);
        }
        Intent intent = new Intent(this, SettingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}
