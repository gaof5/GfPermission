package com.gaof.premission.listener;

import java.util.List;

/**
 * 回调结果 用户点击不再询问，后的单独处理
 */
public interface RationaleCallback {

    void onPermissionDenied(int requestCode, List<String> perms);

}
