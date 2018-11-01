package com.gaof.premission.source;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.List;

/**
 * <p>Context Wrapper.</p>
 */
public class ContextSource extends Source {
    private static final String TAG = "ContextSource";
    private Context mContext;

    public ContextSource(Context context) {
        this.mContext = context;
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public void startActivity(Intent intent) {
        if (mContext instanceof Activity) {
            mContext.startActivity(intent);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            activity.startActivityForResult(intent, requestCode);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }

    @Override
    public boolean isShowRationalePermission(String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false;

        if (mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            Log.d(TAG, "isShowRationalePermission2: " + ActivityCompat.shouldShowRequestPermissionRationale(activity, permission));
            Log.d(TAG, "isShowRationalePermission: " + !activity.shouldShowRequestPermissionRationale(permission));
            return ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED && !activity.shouldShowRequestPermissionRationale(permission);
        }

        PackageManager packageManager = mContext.getPackageManager();
        Class<?> pkManagerClass = packageManager.getClass();
        try {
            Method method = pkManagerClass.getMethod("shouldShowRequestPermissionRationale", String.class);
            if (!method.isAccessible()) method.setAccessible(true);
            return !(boolean) method.invoke(packageManager, permission);
        } catch (Exception ignored) {
            return false;
        }
    }

}
