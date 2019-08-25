package com.aotuo.h3officeplat.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aotuo.h3officeplat.R;
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


    @Override
    protected int getLayout() {
        return R.layout.activity_more_language;
    }

    @Override
    protected void initView() {
        title_view.getBackBtn().setOnClickListener(this);
        title_view.getRightTitle().setOnClickListener(this);
        rl_language_chinese.setOnClickListener(this);
        rl_language_english.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right_title:
                // 完成
                break;
            case R.id.rl_language_chinese:

                break;
            case R.id.rl_language_english:
                break;
        }
    }

}
