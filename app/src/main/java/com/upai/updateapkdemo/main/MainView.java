package com.upai.updateapkdemo.main;

public interface MainView {

    void updateNotification(int progress);

    void showNotification();

    void showToast(String msg);

    void installApk();

}
