package com.tedkim.android.permission.interfaces;

/**
 * Permission listener
 * Created by Ted
 */
public interface OnPermissionListener {

    void onAllowPermission();
    void onDenyPermission(String[] permissions);

}
