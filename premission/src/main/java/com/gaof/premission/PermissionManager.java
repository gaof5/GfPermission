package com.gaof.premission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.IpPrefix;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.gaof.premission.annotation.IPermission;
import com.gaof.premission.dialog.AppSettingDialog;
import com.gaof.premission.helper.PermissionHelper;
import com.gaof.premission.listener.PermissionCallback;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class PermissionManager {

    /**
     * 检查所请求的权限是否授予
     * @param activity 当前activity
     * @param perms 所请求的权限
     * @return 如果所有权限都被授予返回true，否则false
     */
    public static boolean hasPermission(Activity activity, @NonNull String... perms) {
        if(activity==null){
            throw new IllegalArgumentException("参数activity不能为空");
        }
        //如果请求权限低于6.0
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }
        for (String perm:perms) {
            if(ContextCompat.checkSelfPermission(activity,perm)!= PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    /**
     * 申请权限
     * @param activity 当前activity
     * @param rationale 请求的原因
     * @param requestCode 请求标识码
     * @param perms 需要请求的权限
     */
    public static void requestPermissions(Activity activity, String rationale, int requestCode, String... perms) {
        //请求权限前，检查权限状态
        if(hasPermission(activity,perms)){
            notifyHasPermissions(activity,requestCode,perms);
            return;
        }
        //如果请求权限低于6.0
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            notifyHasPermissions(activity,requestCode,perms);
            return;
        }
        //进行权限申请
        PermissionHelper helper= PermissionHelper.newInstance(activity);
        helper.requestPermissions(rationale,activity.getString(android.R.string.ok),activity.getString(android.R.string.cancel),requestCode,perms);
    }

    /**
     * 如果全部都已被授权，则进入 onRequestJPermissionResult方法执行回调
     * @param activity 当前activity
     * @param requestCode 请求标识
     * @param perms 授权通过的权限组
     */
    private static void notifyHasPermissions(Activity activity, int requestCode, String[] perms) {
        int[] grantResults=new int[perms.length];
        for (int i = 0; i < perms.length; i++) {
            grantResults[i] = PackageManager.PERMISSION_GRANTED;
        }
        onRequestPermissionResult(requestCode,perms,grantResults,activity);
    }

    /**
     * 处理请求结果的方法
     * 如果授权或者拒绝任何权限，将通过 PermissionCallback 回调接受结果
     * 运行带有@IPermission注解的方法
     *
     * @param requestCode 回调请求标识码
     * @param perms 回调权限组
     * @param grantResults 回调授权结果
     * @param activity 拥有回调 PermissionCallback 或 @IPermission 注解的对象
     */
    public static void onRequestPermissionResult(int requestCode, String[] perms, int[] grantResults, Activity activity) {
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

        //回调授权通过的结果
        if(!granted.isEmpty()){
            if(activity instanceof PermissionCallback){
                ((PermissionCallback) activity).onPermissionGranted(requestCode,granted);
            }
        }
        //回调授权拒绝
        if(!denied.isEmpty()){
            if(activity instanceof PermissionCallback){
                ((PermissionCallback) activity).onPermissionDenied(requestCode,denied);
            }
        }
        //如果全部授权执行@IPermission注解方法
        if(!granted.isEmpty()&&denied.isEmpty()){
            reflectAnnotationMethod(activity,requestCode);
        }
    }

    /**
     * 找到activity含有注解的方法，对应执行
     * @param activity
     * @param requestCode
     */
    private static void reflectAnnotationMethod(Activity activity, int requestCode) {
        Class<? extends Activity> clazz=activity.getClass();
        Method[] methods=clazz.getDeclaredMethods();
        for (Method method : methods) {
            if(method.isAnnotationPresent(IPermission.class)){
                IPermission iPermission = method.getAnnotation(IPermission.class);
                if(iPermission.value()==requestCode){
                    if(method.getParameterTypes().length>0){
                        throw new RuntimeException("方法返回值必须为void");
                    }
                    if(!method.isAccessible())method.setAccessible(true);//如果私有设为可访问
                    try {
                        method.invoke(activity);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 检查权限申请被拒绝，并且点击了 不再询问
     * @param activity 当前activity
     * @param perms 被拒绝的权限组
     * @return
     */
    public static boolean somePermissionPermanentlyDenied(Activity activity, List<String> perms) {
        return PermissionHelper.newInstance(activity).somePermissionPermanentlyDenied(perms);
    }
}
