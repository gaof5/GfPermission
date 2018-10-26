package com.gaof.premission.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

/**
 * 对话框 跳转设置
 */
public class AppSettingDialog implements DialogInterface.OnClickListener{

    //跳转设置监听回调标识
    public final static int SETTING_CODE=234;
    private Activity activity;
    private String title;
    private String rationale;
    private String positiveButton;
    private String negativeButton;
    private DialogInterface.OnClickListener cancelListener;
    private int requestCode=-1;

    private AppSettingDialog(Activity activity, String title, String rationale, String positiveButton, String negativeButton, DialogInterface.OnClickListener cancelListener, int requestCode) {
        this.activity = activity;
        this.title = title;
        this.rationale = rationale;
        this.positiveButton = positiveButton;
        this.negativeButton = negativeButton;
        this.cancelListener = cancelListener;
        this.requestCode = requestCode;
    }

    public void show(){
        if(cancelListener!=null){
            new AlertDialog.Builder(activity).setTitle(title)
                    .setCancelable(false)
                    .setMessage(rationale)
                    .setPositiveButton(positiveButton,this)
                    .setNegativeButton(negativeButton, cancelListener)
                    .create()
                    .show();
        }else {
            throw new IllegalArgumentException("对话框取消监听不能为空");
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri=Uri.fromParts("package",activity.getPackageName(),null);
        intent.setData(uri);
        activity.startActivityForResult(intent,requestCode);
    }

    public static class Builder{
        private Activity activity;
        private String title;
        private String rationale;
        private String positiveButton;
        private String negativeButton;
        private DialogInterface.OnClickListener cancelListener;
        private int requestCode=-1;

        public Builder(Activity activity){
            this.activity=activity;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setRationale(String rationale) {
            this.rationale = rationale;
            return this;
        }

        public Builder setPositiveButton(String positiveButton) {
            this.positiveButton = positiveButton;
            return this;
        }

        public Builder setNegativeButton(String negativeButton) {
            this.negativeButton = negativeButton;
            return this;
        }

        public Builder setCancelListener(DialogInterface.OnClickListener cancelListener) {
            this.cancelListener = cancelListener;
            return this;
        }

        public Builder setRequestCode(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public AppSettingDialog build(){
            return new AppSettingDialog(activity,title,rationale,positiveButton,negativeButton, cancelListener,requestCode);
        }

    }

}
