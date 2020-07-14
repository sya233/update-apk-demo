package com.upai.updateapkdemo.main;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.upai.updateapkdemo.R;

import java.io.File;

public class MainActivity extends AppCompatActivity implements MainView {

    private MainPresenter mainPresenter;

    private Button btnUpdate1;
    private Button btnUpdate2;
    private File fdownloadDir;

    private static final String TAG = "MainActivity";

    // 通知栏
    private NotificationManager manager;
    private NotificationCompat.Builder builder;
    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化
        init();
        // 响应点击事件
        respondToClick();
    }

    private void init() {
        // 初始化控件id
        btnUpdate1 = findViewById(R.id.btn_update1);
        btnUpdate2 = findViewById(R.id.btn_update2);
        // 初始化Presenter
        mainPresenter = new MainPresenter(this, new MainModel());
        // 初始化File
        fdownloadDir = this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        // 初始化Notification
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(this, "0")
                .setContentTitle("更新中")
                .setContentText("下载进度")
                .setProgress(100, 0, false)
                .setSmallIcon(R.mipmap.ic_launcher);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("2", "mine", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
            builder.setChannelId("2");
        }
    }

    private void respondToClick() {
        btnUpdate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] permissions = {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                };
                if (hasPermissions(permissions)) {
                    mainPresenter.updateApkNotification(fdownloadDir);
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, permissions, 0);
                }
            }
        });
        btnUpdate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mainPresenter.updateApkNotification(fdownloadDir);
                } else {
                    Toast.makeText(this, "权限不足", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1:
                install();
                break;
            default:
                break;
        }
    }

    private boolean hasPermissions(String... permissions) {
        boolean has = true;
        if (permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    has = false;
                }
            }
        }
        return has;
    }

    @Override
    public void updateNotification(int progress) {
        Log.d(TAG, "传来的progress是: " + progress);
        builder.setContentText("下载进度" + progress + "%");
        builder.setProgress(100, progress, false);
        manager.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public void showNotification() {
        manager.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void installApk() {
        if (Build.VERSION.SDK_INT >= 26) {
            boolean hasInstallPermission = getPackageManager().canRequestPackageInstalls();
            if (!hasInstallPermission) {
                //请求安装未知应用来源的权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, 1);
            } else {
                install();
            }
        }
    }

    private void install() {
        File apkFile = new File(fdownloadDir, "app.apk");
        Log.d(TAG, "文件路径: " + apkFile.toString());
        Uri uri = FileProvider.getUriForFile(this, "com.upai.updateapkdemo.fileprovider", apkFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(intent);
    }
}
