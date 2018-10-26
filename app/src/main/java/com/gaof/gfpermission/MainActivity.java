package com.gaof.gfpermission;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.gaof.premission.PermissionManager;
import com.gaof.premission.annotation.IPermission;

public class MainActivity extends BaseActivity {

    private static final int CAMERA_CODE=20;
    private static final int LOCATION_CONTACTS_CODE=22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void camera(View view) {
        cameraTask();
    }

    @IPermission(CAMERA_CODE)
    private void cameraTask() {
        if(PermissionManager.hasPermission(this, Manifest.permission.CAMERA)){
            Toast.makeText(this,"相机权限拿到拍照",Toast.LENGTH_SHORT).show();
        }else {//没有权限申请
            PermissionManager.requestPermissions(this,"需要相机权限拍照",CAMERA_CODE,Manifest.permission.CAMERA);
        }
    }

    public void locationContacts(View view) {
        locationContacts();
    }

    @IPermission(LOCATION_CONTACTS_CODE)
    private void locationContacts() {
        String[] perms=new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_CONTACTS};
        if(PermissionManager.hasPermission(this, perms)){
            Toast.makeText(this,"相机权限拿到拍照",Toast.LENGTH_SHORT).show();
        }else {//没有权限申请
            PermissionManager.requestPermissions(this,"需要相机权限拍照",LOCATION_CONTACTS_CODE,perms);
        }
    }
}
