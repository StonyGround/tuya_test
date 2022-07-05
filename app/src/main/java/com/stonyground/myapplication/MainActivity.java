package com.stonyground.myapplication;

import android.Manifest.permission;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission;
import androidx.appcompat.app.AppCompatActivity;
import com.tuya.smartai.iot_sdk.DPEvent;
import com.tuya.smartai.iot_sdk.IoTSDKManager;

public class MainActivity extends AppCompatActivity {

  public static final String TAG = "MainActivity";
  String mPid;
  String mUid;
  String mAk;
  IoTSDKManager ioTSDKManager;

  private final ActivityResultLauncher<String> requestPermissionLauncher =
      registerForActivityResult(new RequestPermission(), isGranted -> {
        if (isGranted) {
          // Permission is granted. Continue the action or workflow in your
          // app.
          initSDK();
        } else {
          // Explain to the user that the feature is unavailable because the
          // features requires a permission that the user has denied. At the
          // same time, respect the user's decision. Don't link to system
          // settings in an effort to convince the user to change their
          // decision.
        }
      });

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mPid = BuildConfig.PID;
    mUid = BuildConfig.UUID;
    mAk = BuildConfig.AUTHOR_KEY;

    requestPermissionLauncher.launch(permission.WRITE_EXTERNAL_STORAGE);
  }

  private void initSDK() {

    com.tuya.smartai.iot_sdk.Log.init(this, "/sdcard/tuya_log/iot_demo/", 3);

    ioTSDKManager = new IoTSDKManager(this) {
      @Override
      protected boolean isOffline() {
        //实现自定义网络监测
        com.tuya.smartai.iot_sdk.Log.d(TAG, "isOffline: " + super.isOffline());
        return super.isOffline();
      }
    };

    //注意：这里的pid等配置读取自local.properties文件，不能直接使用。请填写你自己的配置！
    ioTSDKManager.initSDK("/sdcard/tuya_iot/", mPid
        , mUid, mAk, BuildConfig.VERSION_NAME, new IoTSDKManager.IoTCallback() {

          @Override
          public void onDpEvent(DPEvent event) {
            if (event != null) {

            }
          }

          @Override
          public void onReset() {

            getSharedPreferences("event_cache", MODE_PRIVATE).edit().clear().commit();

          }

          @Override
          public void onShorturl(String urlJson) {
            output("shorturl: " + urlJson);

          }

          @Override
          public void onActive() {
            output("onActive: devId-> " + ioTSDKManager.getDeviceId());

          }

          @Override
          public void onFirstActive() {
            output("onFirstActive");
          }

          @Override
          public void onMQTTStatusChanged(int status) {
            output("onMQTTStatusChanged: " + status);

            switch (status) {
              case IoTSDKManager.STATUS_OFFLINE:
                // 设备网络离线
                break;
              case IoTSDKManager.STATUS_MQTT_OFFLINE:
                // 网络在线MQTT离线
                break;
              case IoTSDKManager.STATUS_MQTT_ONLINE:
                // 网络在线MQTT在线

                SharedPreferences sp = getSharedPreferences("event_cache", MODE_PRIVATE);

                DPEvent[] events = ioTSDKManager.getEvents();

                if (events != null) {
                  for (DPEvent event : events) {
                    if (event != null) {
                    }
                  }
                }
                break;
            }
          }
        });
  }

  private void output(String text) {
    com.tuya.smartai.iot_sdk.Log.d(TAG, text);
  }
}