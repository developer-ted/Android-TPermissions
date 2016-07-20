package com.tedkim.android.permission;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.tedkim.android.permission.interfaces.OnPermissionListener;
import com.tedkim.android.permission.utils.PermissionUtils;

/**
 * Permission Manager
 * Created by Ted
 */
public class TPermission {

    public static TPermission mInstance;
    private Context mContext;

    private boolean isShowSetting;
    private String[] mPermissions;
    private String mDescriptionMessage;
    private String mDenyMessage;
    private String mConfirmText;
    private String mCancelText;
    private String mSettingText;

    private OnPermissionListener mOnPermissionListener;

    public static TPermission getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TPermission();
        }
        mInstance.initTPermission(context);
        return mInstance;
    }

    private void initTPermission(Context context) {
        this.mContext = context;
        this.isShowSetting = false;
    }

    public TPermission setPermissions(String... permissions) {
        this.mPermissions = permissions;
        return this;
    }

    public TPermission setListener(OnPermissionListener listener) {
        this.mOnPermissionListener = listener;
        return this;
    }

    public TPermission setDescriptionMessage(String message) {
        this.mDescriptionMessage = message;
        return this;
    }

    public TPermission setDenyMessage(String message) {
        this.mDenyMessage = message;
        return this;
    }

    public TPermission setConfirmText(String text) {
        this.mConfirmText = text;
        return this;
    }

    public TPermission setCancelText(String text) {
        this.mCancelText = text;
        return this;
    }

    public TPermission setShowSetting(boolean isShow) {
        this.isShowSetting = isShow;
        return this;
    }

    public TPermission setSettingText(String text) {
        this.mSettingText = text;
        return this;
    }

    public void build() {
        if (mOnPermissionListener == null) {
            throw new NullPointerException("You must setListener()");
        } else if (PermissionUtils.isEmpty(mPermissions)) {
            throw new NullPointerException("You must setPermissions()");
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            allowPermission();
        } else {
            intentPermissionActivity();
        }
    }

    private void intentPermissionActivity() {
        Intent intent = new Intent(mContext, TPermissionActivity.class);
        intent.putExtra(TPermissionActivity.PERMISSIONS, mPermissions);
        intent.putExtra(TPermissionActivity.DESCRIPTION_MESSAGE, mDescriptionMessage);
        intent.putExtra(TPermissionActivity.DENY_MESSAGE, mDenyMessage);
        intent.putExtra(TPermissionActivity.PACKAGE_NAME, mContext.getPackageName());
        intent.putExtra(TPermissionActivity.CONFIRM_TEXT, mConfirmText);
        intent.putExtra(TPermissionActivity.CANCEL_TEXT, mCancelText);
        intent.putExtra(TPermissionActivity.SHOW_SETTING, isShowSetting);
        intent.putExtra(TPermissionActivity.SETTING_TEXT, mSettingText);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public void allowPermission() {
        mOnPermissionListener.onAllowPermission();

        if (mInstance != null)
            mInstance = null;
    }

    public void denyPermission(String[] permissions) {
        mOnPermissionListener.onDenyPermission(permissions);

        if (mInstance != null)
            mInstance = null;
    }
}
