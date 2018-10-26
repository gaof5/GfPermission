package com.gaof.premission.helper;

import android.app.Activity;

import java.util.List;

class LowApiPermissionHelper extends PermissionHelper {

    LowApiPermissionHelper(Activity activity) {
        super(activity);
    }

    @Override
    public void requestPermissions(String rationale, String positionButton, String negativeButton, int requestCode, String... perms) {

    }

    @Override
    public boolean somePermissionPermanentlyDenied(List<String> perms) {
        return false;
    }
}
