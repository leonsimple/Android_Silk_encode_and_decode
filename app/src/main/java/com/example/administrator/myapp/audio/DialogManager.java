package com.example.administrator.myapp.audio;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.myapp.R;


public class DialogManager {

    /**
     * 以下为dialog的初始化控件，包括其中的布局文件
     */

    private Dialog mDialog;

    private RelativeLayout imgBg;

    private TextView tipsTxt;
    private RelativeLayout imgBg2;
    private ImageView imgV;

    private TextView tipsTxt2;
    private Context mContext;

    public DialogManager(Context context) {
        mContext = context;
    }

    public void showRecordingDialog() {
        mDialog = new Dialog(mContext, R.style.Theme_audioDialog);
        // 用layoutinflater来引用布局
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_manager, null);
        mDialog.setContentView(view);
        imgBg = (RelativeLayout) view.findViewById(R.id.dm_rl_bg);
        imgV = (ImageView) view.findViewById(R.id.iv_voice);
        tipsTxt = (TextView) view.findViewById(R.id.dm_tv_txt);
        imgBg2 = (RelativeLayout) view.findViewById(R.id.dm_rl_bg2);
        tipsTxt2 = (TextView) view.findViewById(R.id.dm_tv_txt2);

        mDialog.show();

    }

    /**
     * 设置正在录音时的dialog界面
     */
    public void recording() {
        if (mDialog != null && mDialog.isShowing()) {
            imgBg.setVisibility(View.VISIBLE);
            tipsTxt.setVisibility(View.VISIBLE);
            imgBg2.setVisibility(View.GONE);
            tipsTxt2.setVisibility(View.GONE);

            imgV.setImageResource(R.drawable.v1);
            tipsTxt.setText(R.string.up_for_cancel);
        }
    }

    /**
     * 取消界面
     */
    public void wantToCancel() {
        if (mDialog != null && mDialog.isShowing()) {
            imgBg.setVisibility(View.GONE);
            tipsTxt.setVisibility(View.GONE);
            imgBg2.setVisibility(View.VISIBLE);
            tipsTxt2.setVisibility(View.VISIBLE);
            tipsTxt2.setText(R.string.want_to_cancle);
            tipsTxt2.setBackgroundColor(Color.RED);
        }

    }

    // 时间过短
    public void tooShort() {
        if (mDialog != null && mDialog.isShowing()) {
            imgBg2.setVisibility(View.VISIBLE);
            tipsTxt2.setVisibility(View.VISIBLE);
            imgBg.setVisibility(View.GONE);
            tipsTxt.setVisibility(View.GONE);
            tipsTxt2.setText(R.string.time_too_short);
        }

    }

    // 隐藏dialog
    public void dismissDialog() {

        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }

    }

    public void updateVoiceLevel(int level) {
        if (level < 0 ) {
            level = 1;
        } else if (level > 9){
            level = 9;
        }
        if (mDialog != null && mDialog.isShowing()) {

            //通过level来找到图片的id，也可以用switch来寻址，但是代码可能会比较长
            int resId = mContext.getResources().getIdentifier("v" + level,"drawable", mContext.getPackageName());
            imgV.setImageResource(resId);
        }

    }

    public TextView getTipsTxt() {
        return tipsTxt;
    }

    public void setTipsTxt(TextView tipsTxt) {
        this.tipsTxt = tipsTxt;
    }
}
