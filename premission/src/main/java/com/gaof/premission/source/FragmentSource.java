package com.gaof.premission.source;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * <p>android.app.Fragment Wrapper.</p>
 */
public class FragmentSource extends Source {
    private static final String TAG = "FragmentSource";
    private Fragment mFragment;

    public FragmentSource(Fragment fragment) {
        this.mFragment = fragment;
    }

    @Override
    public Context getContext() {
        return mFragment.getActivity();
    }

    @Override
    public void startActivity(Intent intent) {
        mFragment.startActivity(intent);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        mFragment.startActivityForResult(intent, requestCode);
    }

    @Override
    public boolean isShowRationalePermission(String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false;
        Log.d(TAG, "isShowRationalePermission: "+!mFragment.shouldShowRequestPermissionRationale(permission));
        return ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED && !mFragment.shouldShowRequestPermissionRationale(permission);
    }
}