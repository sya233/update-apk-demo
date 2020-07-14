package com.upai.updateapkdemo.main;

import android.util.Log;

import java.io.File;

public class MainPresenter implements OnDownloadListener {

    private MainView mainView;
    private MainModel mainModel;

    public MainPresenter(MainView view, MainModel model) {
        mainView = view;
        mainModel = model;
    }

    public void updateApkNotification(File dir) {
        mainView.showNotification();
        mainModel.updateApkNotification(dir, this);
    }

    @Override
    public void onDownloadSuccess() {
        Log.d("MainPresenter", "onDownloadSuccess");
        mainView.installApk();
    }

    @Override
    public void onDownloading(int progress) {
        mainView.updateNotification(progress);
    }

    @Override
    public void onDownloadFail(Exception e) {
        Log.d("MainPresenter", "onDownloadFail: " + e);
        mainView.showToast("下载失败");
    }
}
