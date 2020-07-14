package com.upai.updateapkdemo.main;

import com.upai.updateapkdemo.util.HttpUtil;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

class MainModel {

    private static final String downloadUrl = "http://upuai.oss-cn-hangzhou.aliyuncs.com/app/androidHj/1.12.apk";

    private static final String TAG = "MainModel";

    void updateApkNotification(final File dir, final OnDownloadListener listener) {
        HttpUtil.downloadApkFromServer(downloadUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onDownloadFail(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    BufferedSource source = response.body().source();
                    File file = new File(dir, "app.apk");
                    BufferedSink sink = Okio.buffer(Okio.sink(file));
                    long contentLength = response.body().contentLength();
                    long totalRead = 0;
                    long read = 0;
                    while ((read = source.read(sink.buffer(), 10 * 1024)) != -1) {
                        totalRead += read;
//                        Log.d(TAG, "totalRead是: " + totalRead);
                        int progress = (int) ((totalRead * 100) / contentLength);
                        listener.onDownloading(progress);
                    }
                    sink.writeAll(source);
                    sink.flush();
                    sink.close();
                    listener.onDownloadSuccess();
                }
            }
        });
    }

}
