package com.m.freemovie.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.m.freemovie.Activity.Download_videoActivity;
import com.m.freemovie.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DownloadService extends Service {
    private static final String TAG = "DownloadService";
    private static final String CHANNEL_ID = "download_channel";
    private static final int NOTIFICATION_ID = 1;

    private NotificationManager notificationManager;
    private final IBinder binder = new DownloadBinder();
    private Call currentCall;
    private boolean isDownloading = false; // ✅ Prevent multiple tasks

    public class DownloadBinder extends Binder {
        public DownloadService getService() {
            return DownloadService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Video Downloads",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Notifications for video downloads");
            notificationManager.createNotificationChannel(channel);
        }
    }

    /** ✅ Only allow one download at a time */
    public synchronized void startDownload(String videoUrl, File destinationFile, DownloadCallback callback) {
        if (isDownloading) {
            Log.w(TAG, "A download is already in progress — rejecting new task.");
            if (callback != null) {
                callback.onFailure(new IllegalStateException("A download is already in progress."));
            }
            return;
        }

        isDownloading = true;
        updateNotification("Starting download...", 0);
        Toast.makeText(getApplicationContext(),"Downloading start", Toast.LENGTH_SHORT).show();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(videoUrl).build();

        currentCall = client.newCall(request);
        currentCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Download failed", e);
                updateNotification("Download failed", -1);
                isDownloading = false;
                if (callback != null) callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    updateNotification("Download failed", -1);
                    isDownloading = false;
                    if (callback != null)
                        callback.onFailure(new IOException("Server error: " + response.code()));
                    return;
                }

                ResponseBody body = response.body();
                if (body == null) {
                    updateNotification("Download failed", -1);
                    isDownloading = false;
                    if (callback != null)
                        callback.onFailure(new IOException("Empty response"));
                    return;
                }

                long contentLength = body.contentLength();
                InputStream inputStream = body.byteStream();
                FileOutputStream outputStream = new FileOutputStream(destinationFile);

                byte[] buffer = new byte[4096];
                int bytesRead;
                long totalBytesRead = 0;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;

                    if (contentLength > 0) {
                        int progress = (int) ((totalBytesRead * 100) / contentLength);
                        updateNotification("Downloading video...", progress);
                    }
                }

                outputStream.flush();
                outputStream.close();
                inputStream.close();

                updateNotification("Download complete", 100);
                isDownloading = false;

                if (callback != null) callback.onSuccess(destinationFile);
            }
        });
    }

    /** ✅ Adds PendingIntent and handles completion cleanly */
    private static final int FOREGROUND_ID = 1;
    private static final int COMPLETED_ID = 2;

    private void updateNotification(String message, int progress) {
        Intent intent = new Intent(this, Download_videoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Manyakol Video Download")
                .setContentText(message)
                .setSmallIcon(R.mipmap.app_logo)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        if (progress >= 0 && progress < 100) {
            builder.setOngoing(true)
                    .setProgress(100, progress, false);
            Notification notification = builder.build();
            startForeground(FOREGROUND_ID, notification);
        } else {
            builder.setOngoing(false)
                    .setAutoCancel(true)
                    .setProgress(0, 0, false)
                    .setContentText(progress == -1 ? "Download failed" : "Download complete. Tap to view.");

            Notification completedNotification = builder.build();

            new Handler(getMainLooper()).post(() -> {
                try {
                    stopForeground(true);
                } catch (Exception ignored) {}

                notificationManager.notify(COMPLETED_ID, completedNotification);
                new Handler(getMainLooper()).postDelayed(() -> {
                    stopSelf();
                }, 1000);
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (currentCall != null && !currentCall.isCanceled()) {
            currentCall.cancel();
        }
        isDownloading = false;
    }

    public interface DownloadCallback {
        void onSuccess(File file);
        void onFailure(Exception e);
    }
}
