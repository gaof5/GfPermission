package com.gaof.premission.helper;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v4.app.ActivityCompat;

import com.gaof.premission.dialog.AppSettingDialog;

import java.util.List;

class ActivityPermissionHelper extends PermissionHelper {

    private String rationale;
    private String positiveButton;
    private String negativeButton;

    ActivityPermissionHelper(Activity activity) {
        super(activity);
    }

    @Override
    public void requestPermissions(String rationale, String positiveButton, String negativeButton, int requestCode, String... perms) {
        this.rationale=rationale;
        this.positiveButton=positiveButton;
        this.negativeButton=negativeButton;
        ActivityCompat.requestPermissions(getHost(),perms,requestCode);
    }

    @Override
    public boolean somePermissionPermanentlyDenied(List<String> perms) {
        //拒绝的权限组遍历
        for (String perm: perms) {
            if(!ActivityCompat.shouldShowRequestPermissionRationale(getHost(),perm)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void showDialogRemind() {
        super.showDialogRemind();
        //显示一个对话框告诉开启
        new AppSettingDialog.Builder(getHost()).setTitle("权限申请")
                .setRationale(rationale)
                .setNegativeButton(negativeButton)
                .setPositiveButton(positiveButton)
                .setCancelListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).build().show();
    }
}
