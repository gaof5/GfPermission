package com.gaof.gfpermission;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.gaof.premission.GfPermission;
import com.gaof.premission.Permission;
import com.gaof.premission.PermissionManager;
import com.gaof.premission.annotation.IPermission;
import com.gaof.premission.listener.PermissionCallback;
import com.gaof.premission.listener.RationaleCallback;

import java.util.List;

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
        //方法1 注解、实现PermissionCallback配合使用 先判断是已申请此权限
        if(PermissionManager.hasPermission(this, Manifest.permission.CAMERA)){
            Toast.makeText(this,"相机权限拿到拍照",Toast.LENGTH_SHORT).show();
        }else {//没有权限申请
            PermissionManager.requestPermissions(this,"需要相机权限拍照",CAMERA_CODE,Manifest.permission.CAMERA);
        }
    }

    public void locationContacts(View view) {
        //方法1 注解、实现PermissionCallback配合使用 先判断是已申请此权限
//        locationContacts();
        //方法2 无需继承基类实现PermissionCallback，无需判断是已申请此权限，直接在回调处理结果
        String[] perms=new String[]{Permission.ACCESS_FINE_LOCATION,Permission.READ_CONTACTS};
        GfPermission.with(this)
                .setPermissions("需要定位、联系人权限发送位置",LOCATION_CONTACTS_CODE,perms)
//                .rationale(new RationaleCallback() {
//                    @Override
//                    public void onPermissionDenied(int requestCode, List<String> perms) {
//                        Toast.makeText(MainActivity.this,"不再询问处理",Toast.LENGTH_SHORT).show();
//                    }
//                })
                .setPermissionCallback(new PermissionCallback() {
                    @Override
                    public void onPermissionGranted(int requestCode, List<String> perms) {
                        Toast.makeText(MainActivity.this,"定位、联系人权限拿到发送位置",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionDenied(int requestCode, List<String> perms) {
                        Toast.makeText(MainActivity.this,"拒绝权限",Toast.LENGTH_SHORT).show();
                    }
                }).request();
    }

    @IPermission(LOCATION_CONTACTS_CODE)
    private void locationContacts() {
        String[] perms=new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_CONTACTS};
        if(PermissionManager.hasPermission(this, perms)){
            Toast.makeText(this,"定位、联系人拿到发送位置",Toast.LENGTH_SHORT).show();
        }else {//没有权限申请
            PermissionManager.requestPermissions(this,"需要定位、联系人权限发送位置",LOCATION_CONTACTS_CODE,perms);
        }
    }
}
