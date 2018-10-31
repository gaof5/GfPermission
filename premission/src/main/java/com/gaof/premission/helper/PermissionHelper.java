package com.gaof.premission.helper;

import android.app.Activity;
import android.os.Build;

import java.util.List;

/**
 * 辅助器
 */
public abstract class PermissionHelper {

    private Activity activity;

    PermissionHelper(Activity activity){
        this.activity=activity;
    }

    public Activity getHost() {
        return activity;
    }

    public static PermissionHelper newInstance(Activity activity) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return new LowApiPermissionHelper(activity);
        }
        return new ActivityPermissionHelper(activity);
    }

    /**
     * 申请权限
     * @param rationale 说明需要权限的原因
     * @param positionButton 确定按钮
     * @param negativeButton 取消按钮
     * @param requestCode 请求码
     * @param perms 请求权限组
     */
    public abstract void requestPermissions(String rationale, String positionButton, String negativeButton,
                                            int requestCode,String... perms);

    public abstract boolean somePermissionPermanentlyDenied(List<String> perms);

    public void showDialogRemind(){}

}
