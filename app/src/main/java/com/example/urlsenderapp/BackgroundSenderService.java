package com.example.urlsenderapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BackgroundSenderService extends Service {

    private static final String TAG = "BackgroundSenderService";
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 11007;
    private static final long INTERVAL = 30_000L;
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "background_sender_channel";

    private final List<String> urls = new ArrayList<>();
    private int index = 0;
    private Timer timer;

    private Handler mainHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mainHandler = new Handler(Looper.getMainLooper());
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, buildNotification());
        showToast("BackgroundSenderService is running in background");
        Log.d(TAG, "Service created and foreground notification started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showToast("BackgroundSenderService started");
        Log.d(TAG, "Service onStartCommand called");

        if (urls.isEmpty()) {
            loadUrls();
            startSending();
        }

        return START_STICKY;
    }

    private void loadUrls() {
        urls.clear();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getAssets().open("urls.txt")))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    urls.add(line);
                }
            }
            Log.d(TAG, "Loaded " + urls.size() + " URLs");
        } catch (IOException e) {
            Log.e(TAG, "Failed to read urls.txt", e);
            showToast("Failed to load URLs");
        }
    }

    private void startSending() {
        if (urls.isEmpty()) return;

        sendUrl(urls.get(index % urls.size()));
        index++;

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String url = urls.get(index % urls.size());
                index++;
                sendUrl(url);
            }
        }, INTERVAL, INTERVAL);
    }


    private void sendUrl(String url) {
        new Thread(() -> {
            try (Socket socket = new Socket(HOST, PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                out.println(url);
                Log.d(TAG, "Sent URL: " + url);

            } catch (IOException e) {
                Log.e(TAG, "Error sending URL: " + url, e);
                showToast("Connection failed. Check IP/port or server status.");
            }
        }).start();
    }


    private void showToast(final String message) {
        mainHandler.post(() -> Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());
    }

    private Notification buildNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("URL Sender Service")
                .setContentText("Sending URLs in background")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setOngoing(true)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Background Sender Channel";
            String description = "Channel for Background URL Sender Service";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        Log.d(TAG, "Service destroyed and timer cancelled");
        showToast("BackgroundSenderService stopped");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
