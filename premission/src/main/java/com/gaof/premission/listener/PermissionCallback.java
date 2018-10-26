package com.gaof.premission.listener;

import java.util.List;

/**
 * 回调结果
 */
public interface PermissionCallback {

    void onPermissionGranted(int requestCode,List<String> perms);

    void onPermissionDenied(int requestCode, List<String> perms);

}
