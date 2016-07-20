package com.tedkim.android.permission;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.tedkim.android.permission.broadcast.PermissionReceiver;

import java.util.ArrayList;

/**
 * Permission Activity
 * Created by Ted
 */
@TargetApi(Build.VERSION_CODES.M)
public class TPermissionActivity extends AppCompatActivity {

    public static final int REQUEST_PERMISSION = 1000;
    public static final int REQUEST_SETTING = 1001;

    public static final String PERMISSIONS = "PERMISSIONS";
    public static final String DESCRIPTION_MESSAGE = "DESCRIPTION_MESSAGE";
    public static final String DENY_MESSAGE = "DENY_MESSAGE";
    public static final String PACKAGE_NAME = "PACKAGE_NAME";
    public static final String CONFIRM_TEXT = "CONFIRM_TEXT";
    public static final String CANCEL_TEXT = "CANCEL_TEXT";
    public static final String SHOW_SETTING = "SHOW_SETTING";
    public static final String SETTING_TEXT = "SETTING_TEXT";

    private ArrayList<String> mDenyPermissions;
    private boolean isShowSetting;
    private String[] mPermissions;
    private String mDescriptionMessage;
    private String mDenyMessage;
    private String mPackageName;
    private String mConfirmText;
    private String mCancelText;
    private String mSettingText;

    private PermissionReceiver mPermissionReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDenyPermissions = new ArrayList<>();

        initData();
        checkPermissions(true);
    }

    private void initData() {
        Intent intent = getIntent();
        mPermissions = intent.getStringArrayExtra(TPermissionActivity.PERMISSIONS);
        mDescriptionMessage = intent.getStringExtra(TPermissionActivity.DESCRIPTION_MESSAGE);
        mDenyMessage = intent.getStringExtra(TPermissionActivity.DENY_MESSAGE);
        mPackageName = intent.getStringExtra(TPermissionActivity.PACKAGE_NAME);
        mConfirmText = intent.getStringExtra(TPermissionActivity.CONFIRM_TEXT);
        mCancelText = intent.getStringExtra(TPermissionActivity.CANCEL_TEXT);
        isShowSetting = intent.getBooleanExtra(TPermissionActivity.SHOW_SETTING, false);
        mSettingText = intent.getStringExtra(TPermissionActivity.SETTING_TEXT);

        if (TextUtils.isEmpty(mConfirmText))
            mConfirmText = getString(R.string.confirm);

        if (TextUtils.isEmpty(mCancelText))
            mCancelText = getString(R.string.cancel);

        if (TextUtils.isEmpty(mSettingText))
            mSettingText = getString(R.string.setting);

        mPermissionReceiver = new PermissionReceiver();
        registerReceiver(mPermissionReceiver, new IntentFilter());
    }

    private void checkPermissions(boolean isFirst) {
        ArrayList<String> permissions = new ArrayList<>();

        for (String permission : mPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(permission);
            }
        }

        // allow permission
        if (permissions.isEmpty()) {
            allowPermission();
            return;
        }

        // first & deny permission && description message not empty
        if (isFirst && !permissions.isEmpty() && !TextUtils.isEmpty(mDescriptionMessage)) {
            showDescriptionDialog(permissions);
            return;
        }

        // enter setting screen & deny permission
        if (!isFirst && !permissions.isEmpty()) {
            denyPermission();
            return;
        }

        // request permissions
        requestPermissions(permissions);
    }

    private void showDescriptionDialog(final ArrayList<String> needPermissions) {
        new AlertDialog.Builder(this)
                .setMessage(mDescriptionMessage)
                .setCancelable(false)
                .setNegativeButton(mConfirmText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestPermissions(needPermissions);
                    }
                })
                .show();
    }

    public void showSettingDialog() {
        if (isShowSetting) {
            if (TextUtils.isEmpty(mDenyMessage))
                mDenyMessage = getString(R.string.setting_deny_message);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(mDenyMessage)
                    .setCancelable(false)
                    .setNegativeButton(mCancelText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            denyPermission();
                        }
                    });

            builder.setPositiveButton(mSettingText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                .setData(Uri.parse("package:" + mPackageName));
                        startActivityForResult(intent, REQUEST_SETTING);
                    } catch (ActivityNotFoundException e) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                        startActivityForResult(intent, REQUEST_SETTING);
                    }
                }
            });
            builder.show();
        } else {
            denyPermission();
        }
    }

    public void requestPermissions(ArrayList<String> permissions) {
        ActivityCompat.requestPermissions(this, permissions.toArray(new String[permissions.size()]), REQUEST_PERMISSION);
    }

    private void allowPermission() {
        Intent intent = new Intent();
        intent.setAction(getString(R.string.permission_action));
        intent.putExtra(PermissionReceiver.PERMISSION_ALLOW, true);

        unregisterReceiver(mPermissionReceiver);
        sendBroadcast(intent);
        finish();
        overridePendingTransition(0, 0);
    }

    private void denyPermission() {
        Intent intent = new Intent();
        intent.setAction(getString(R.string.permission_action));
        intent.putExtra(PermissionReceiver.PERMISSION_ALLOW, false);

        String[] permissions = new String[mDenyPermissions.size()];
        for (int i = 0; i < mDenyPermissions.size(); i++) {
            permissions[i] = mDenyPermissions.get(i);
        }
        intent.putExtra(PermissionReceiver.PERMISSIONS, permissions);

        unregisterReceiver(mPermissionReceiver);
        sendBroadcast(intent);
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                mDenyPermissions.add(permission);
            }
        }

        if (mDenyPermissions.isEmpty()) {
            allowPermission();
        } else {
            showSettingDialog();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SETTING:
                checkPermissions(false);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
