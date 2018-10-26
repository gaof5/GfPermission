package com.gaof.gfpermission;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.gaof.premission.PermissionManager;
import com.gaof.premission.dialog.AppSettingDialog;
import com.gaof.premission.listener.PermissionCallback;

import java.util.List;

public class BaseActivity extends AppCompatActivity implements PermissionCallback{
    private static final String TAG = "BaseActivity";
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.onRequestPermissionResult(requestCode,permissions,grantResults,this);
    }

    @Override
    public void onPermissionGranted(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionGranted: "+requestCode);
        //检查用户是否拒绝过权限，并且点击了 不再询问
        if(PermissionManager.somePermissionPermanentlyDenied(this,perms)){
            //显示一个对话框告诉开启
            new AppSettingDialog.Builder(this).setTitle("权限申请")
                    .setRationale("需要请求")
                    .setNegativeButton("取消")
                    .setPositiveButton("设置")
                    .setCancelListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).build().show();
        }
    }

    @Override
    public void onPermissionDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionDenied: "+requestCode);
        //用户点击的拒绝，并勾选了 不再询问
    }
}
