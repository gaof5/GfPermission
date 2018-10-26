package com.gaof.premission.helper;

import android.app.Activity;
import android.support.v4.app.ActivityCompat;

import java.util.List;

class ActivityPermissionHelper extends PermissionHelper {

    public ActivityPermissionHelper(Activity activity) {
        super(activity);
    }

    @Override
    public void requestPermissions(String rationale, String positionButton, String negativeButton, int requestCode, String... perms) {
        ActivityCompat.requestPermissions(getHost(),perms,requestCode);
    }

    @Override
    public boolean somePermissionPermanentlyDenied(List<String> perms) {
        //拒绝的权限组遍历
        for (String perm: perms) {
            if(!ActivityCompat.shouldShowRequestPermissionRationale(getHost(),perm)){
                return true;
            }
        }
        return false;
    }
}
