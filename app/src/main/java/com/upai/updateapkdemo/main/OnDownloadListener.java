package com.upai.updateapkdemo.main;

public interface OnDownloadListener {

    void onDownloadSuccess();

    void onDownloading(int progress);

    void onDownloadFail(Exception e);

}
