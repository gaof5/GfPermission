package com.gaof.premission;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.gaof.premission.dialog.RuntimeSettingPage;
import com.gaof.premission.listener.PermissionCallback;
import com.gaof.premission.source.Source;

import java.util.ArrayList;
import java.util.List;

public class PermissionManagerEasy {

    private Source mSource;
    private PermissionCallback permissionCallback;
    private String rationale;
    private int requestCode;
    private String[] perms;

    PermissionManagerEasy(Source source) {
        this.mSource = source;
    }

    /**
     * 检查所请求的权限是否授予
     * @param perms 所请求的权限
     * @return 如果所有权限都被授予返回true，否则false
     */
    private boolean hasPermission(@NonNull String... perms) {
        //如果请求权限低于6.0
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }
        for (String perm:perms) {
            if(ContextCompat.checkSelfPermission(mSource.getContext(),perm)!= PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    /**
     * 设置请求权限
     * @param rationale 权限请求原因
     * @param requestCode 请求标识码
     * @param perms 权限组
     * @return 权限管理类
     */
    public PermissionManagerEasy setPermissions(String rationale, int requestCode, String... perms){
        this.rationale=rationale;
        this.requestCode=requestCode;
        this.perms=perms;
        return this;
    }

    /**
     * 设置结果回调接口
     * @param permissionCallback 回调接口
     * @return
     */
    public PermissionManagerEasy setPermissionCallback(PermissionCallback permissionCallback){
        this.permissionCallback=permissionCallback;
        return this;
    }
    /**
     * 申请权限
     */
    public void request() {
        //请求权限前，检查权限状态
        if(hasPermission(perms)){
            notifyHasPermissions(requestCode,perms);
            return;
        }
        //如果请求权限低于6.0
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            notifyHasPermissions(requestCode,perms);
            return;
        }
        //进行权限申请
        PermissionActivity.requestPermission(mSource.getContext(),perms,requestListener);
    }

    /**
     * 如果全部都已被授权，则进入 onRequestPermissionResult方法执行回调
     * @param requestCode 请求标识
     * @param perms 授权通过的权限组
     */
    private void notifyHasPermissions(int requestCode, String[] perms) {
        int[] grantResults=new int[perms.length];
        for (int i = 0; i < perms.length; i++) {
            grantResults[i] = PackageManager.PERMISSION_GRANTED;
        }
        onRequestPermissionResult(requestCode,perms,grantResults);
    }

    /**
     * 处理请求结果的方法
     * 如果授权或者拒绝任何权限，将通过 PermissionCallback 回调接受结果
     * 运行带有@IPermission注解的方法
     *
     * @param requestCode 回调请求标识码
     * @param perms 回调权限组
     * @param grantResults 回调授权结果
     */
    private void onRequestPermissionResult(int requestCode, String[] perms, int[] grantResults) {
        List<String> granted=new ArrayList<>();
        List<String> denied=new ArrayList<>();
        for (int i = 0; i < perms.length; i++) {
            String perm=perms[i];
            if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                granted.add(perm);
            }else {
                denied.add(perm);
            }
        }

        //回调授权拒绝
        if(!denied.isEmpty()){
            if(permissionCallback!=null){
                permissionCallback.onPermissionDenied(requestCode,granted);
                if(somePermissionPermanentlyDenied(perms)){
                    //显示一个对话框告诉开启
                    showSettingDialog();
                }
            }
        }
        //如果全部授权执行
        if(!granted.isEmpty()&&denied.isEmpty()){
            if(permissionCallback!=null){
                permissionCallback.onPermissionGranted(requestCode,granted);
            }
        }
    }

    private void showSettingDialog() {
        new AlertDialog.Builder(mSource.getContext()).setTitle("权限申请")
                .setCancelable(false)
                .setMessage(rationale)
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RuntimeSettingPage runtimeSettingPage = new RuntimeSettingPage(mSource);
                        runtimeSettingPage.start(PermissionManagerEasy.this.requestCode);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }


    /**
     * 检查权限申请被拒绝，并且点击了 不再询问
     * @param perms 被拒绝的权限组
     * @return
     */
    private boolean somePermissionPermanentlyDenied(String... perms) {
        //拒绝的权限组遍历
        for (String perm: perms) {
            if(mSource.isShowRationalePermission(perm)){
                return true;
            }
        }
        return false;
    }

    //权限请求回调
    private PermissionActivity.RequestListener requestListener=new PermissionActivity.RequestListener() {
        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            onRequestPermissionResult(requestCode, permissions, grantResults);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {

        }
    };

}
