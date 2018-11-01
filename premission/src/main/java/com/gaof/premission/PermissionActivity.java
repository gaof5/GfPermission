package com.gaof.premission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;

import com.gaof.premission.dialog.RuntimeSettingPage;
import com.gaof.premission.source.ContextSource;


/**
 * <p>
 * Request permission.
 * </p>
 */
public final class PermissionActivity extends Activity {

    private static final String KEY_INPUT_OPERATION = "KEY_INPUT_OPERATION";
    private static final int VALUE_INPUT_PERMISSION = 1;
    private static final int VALUE_INPUT_PERMISSION_SETTING = 2;
    private static final int VALUE_INPUT_INSTALL = 3;

    private static final String KEY_INPUT_PERMISSIONS = "KEY_INPUT_PERMISSIONS";

    private static RequestListener sRequestListener;

    /**
     * Request for permissions.
     */
    public static void requestPermission(Context context, String[] permissions, RequestListener requestListener) {
        PermissionActivity.sRequestListener = requestListener;

        Intent intent = new Intent(context, PermissionActivity.class);
        intent.putExtra(KEY_INPUT_OPERATION, VALUE_INPUT_PERMISSION);
        intent.putExtra(KEY_INPUT_PERMISSIONS, permissions);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Request for setting.
     */
    public static void permissionSetting(Context context, RequestListener requestListener) {
        PermissionActivity.sRequestListener = requestListener;

        Intent intent = new Intent(context, PermissionActivity.class);
        intent.putExtra(KEY_INPUT_OPERATION, VALUE_INPUT_PERMISSION_SETTING);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Request for package install.
     */
    public static void requestInstall(Context context, RequestListener requestListener) {
        PermissionActivity.sRequestListener = requestListener;

        Intent intent = new Intent(context, PermissionActivity.class);
        intent.putExtra(KEY_INPUT_OPERATION, VALUE_INPUT_INSTALL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int operation = intent.getIntExtra(KEY_INPUT_OPERATION, 0);
        switch (operation) {
            case VALUE_INPUT_PERMISSION: {
                String[] permissions = intent.getStringArrayExtra(KEY_INPUT_PERMISSIONS);
                if (permissions != null && sRequestListener != null) {
                    requestPermissions(permissions, VALUE_INPUT_PERMISSION);
                } else {
                    finish();
                }
                break;
            }
            case VALUE_INPUT_PERMISSION_SETTING: {
                if (sRequestListener != null) {
                    RuntimeSettingPage setting = new RuntimeSettingPage(new ContextSource(this));
                    setting.start(VALUE_INPUT_PERMISSION_SETTING);
                } else {
                    finish();
                }
                break;
            }
            case VALUE_INPUT_INSTALL: {
                if (sRequestListener != null) {
                    Intent manageIntent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                    manageIntent.setData(Uri.fromParts("package", getPackageName(), null));
                    startActivityForResult(manageIntent, VALUE_INPUT_INSTALL);
                } else {
                    finish();
                }
                break;
            }
            default: {
                throw new AssertionError("This should not be the case.");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (sRequestListener != null) {
            sRequestListener.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (sRequestListener != null) {
            sRequestListener.onActivityResult(requestCode,resultCode,data);
        }
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        sRequestListener = null;
        super.finish();
    }

    /**
     * permission callback.
     */
    public interface RequestListener {
        void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }
}