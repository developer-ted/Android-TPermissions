package com.tedkim.android.sample;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.tedkim.android.permission.TPermission;
import com.tedkim.android.permission.interfaces.OnPermissionListener;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity
        implements OnPermissionListener, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnNo).setOnClickListener(this);
        findViewById(R.id.btnDescription).setOnClickListener(this);
        findViewById(R.id.btnSetting).setOnClickListener(this);
        findViewById(R.id.btnDescriptionAndSetting).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNo:
                TPermission.getInstance(getApplicationContext())
                        .setPermissions(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .setListener(this)
                        .build();
                break;

            case R.id.btnDescription:
                TPermission.getInstance(getApplicationContext())
                        .setPermissions(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .setListener(this)
                        .setDescriptionMessage("This app need permission")
                        .setConfirmText("Confirm")
                        .build();
                break;

            case R.id.btnSetting:
                TPermission.getInstance(getApplicationContext())
                        .setPermissions(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .setListener(this)
                        .setDenyMessage("If you reject permission, this service you want is not available\\nPlease turn on permissions at [Setting] > [Permission]")
                        .setShowSetting(true)
                        .setSettingText("Setting")
                        .build();
                break;

            case R.id.btnDescriptionAndSetting:
                TPermission.getInstance(getApplicationContext())
                        .setPermissions(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .setListener(this)
                        .setDescriptionMessage("This app need permission")
                        .setConfirmText("Confirm")
                        .setCancelText("Cancel")
                        .setDenyMessage("If you reject permission, this service you want is not available\\nPlease turn on permissions at [Setting] > [Permission]")
                        .setShowSetting(true)
                        .setSettingText("Setting")
                        .build();
                break;
        }
    }

    @Override
    public void onAllowPermission() {
        Log.d(TAG, "[onAllowPermission]");
        Toast.makeText(getApplicationContext(), "onAllowPermission", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDenyPermission(String[] permissions) {
        Log.d(TAG, "[onDenyPermission] permissions : " + Arrays.toString(permissions));
        Toast.makeText(getApplicationContext(), "onDenyPermission", Toast.LENGTH_SHORT).show();
    }
}
