package com.aotuo.h3officeplat.view;

import android.app.AlertDialog;
import android.content.Context;
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
            case R.id.tv_dialog_commit:
                dialog.dismiss();
                break;
        }
    }

    private AlertDialog dialog;
    private View mRootview;
    private TextView mCommit;


    public CommonDialog(Context context){
        dialog = new AlertDialog.Builder(context).create();
        dialog.show();
        Window window = dialog.getWindow();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setGravity(Gravity.CENTER);
        mRootview = LayoutInflater.from(context).inflate(R.layout.dialog_common, null);
        mCommit = (TextView) mRootview.findViewById(R.id.tv_dialog_commit);
        mCommit.setOnClickListener(this);

        window.setContentView(mRootview);
    }

}
