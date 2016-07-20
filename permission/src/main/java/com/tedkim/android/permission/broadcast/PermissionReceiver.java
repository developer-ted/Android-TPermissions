package com.tedkim.android.permission.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tedkim.android.permission.TPermission;

/**
 * Permission broadcast receiver
 */
public class PermissionReceiver extends BroadcastReceiver {

    public static final String PERMISSION_ALLOW = "PERMISSION_ALLOW";
    public static final String PERMISSIONS = "PERMISSIONS";

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isAllow = intent.getBooleanExtra(PERMISSION_ALLOW, false);
        if (isAllow) {
            TPermission.getInstance(context).allowPermission();
        } else {
            String[] permissions = intent.getStringArrayExtra(PERMISSIONS);
            TPermission.getInstance(context).denyPermission(permissions);
        }
    }
}