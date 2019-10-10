package com.aotuo.h3officeplat.view;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.aotuo.h3officeplat.R;

public class CommonDialog implements View.OnClickListener{
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_dialog_confirm:
                dialog.dismiss();
                break;
        }
    }

    private AlertDialog dialog;
    private View mRootview;
    private TextView tv_dialog_title, tv_dialog_content, tv_dialog_confirm;

    public CommonDialog(Context context, String title, String content, String confirm){
        dialog = new AlertDialog.Builder(context).create();
        dialog.show();
        Window window = dialog.getWindow();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setGravity(Gravity.CENTER);
        mRootview = LayoutInflater.from(context).inflate(R.layout.dialog_common, null);
        tv_dialog_title = (TextView) mRootview.findViewById(R.id.tv_dialog_title);
        tv_dialog_content = (TextView) mRootview.findViewById(R.id.tv_dialog_content);
        tv_dialog_confirm = (TextView) mRootview.findViewById(R.id.tv_dialog_confirm);
        tv_dialog_confirm.setOnClickListener(this);

        if (TextUtils.isEmpty(title)){
            tv_dialog_title.setVisibility(View.GONE);
        } else {
            tv_dialog_title.setVisibility(View.VISIBLE);
            tv_dialog_title.setText(title);
        }
        tv_dialog_content.setText(content);
        tv_dialog_confirm.setText(confirm);
        window.setContentView(mRootview);
    }

}
