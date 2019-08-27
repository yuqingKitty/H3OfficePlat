package com.aotuo.h3officeplat.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aotuo.h3officeplat.R;
import com.aotuo.h3officeplat.utils.SharedPreferencesHelper;
import com.aotuo.h3officeplat.view.TitleView;

import butterknife.BindView;

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
        String language = SharedPreferencesHelper.getInstance().getAppData(SharedPreferencesHelper.KEY_APP_USE_LANGUAGE, getString(R.string.language_chinese));
        if (language.equals(getString(R.string.language_chinese))) {
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
                    SharedPreferencesHelper.getInstance().setAppData(SharedPreferencesHelper.KEY_APP_USE_LANGUAGE, getString(R.string.language_chinese));
                } else {
                    SharedPreferencesHelper.getInstance().setAppData(SharedPreferencesHelper.KEY_APP_USE_LANGUAGE, getString(R.string.language_english));
                }
                finish();
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

}
