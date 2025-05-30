package com.example.urlsenderapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toast.makeText(this, "Sender app running in background", Toast.LENGTH_SHORT).show();

        Intent serviceIntent = new Intent(this, BackgroundSenderService.class);
        startService(serviceIntent);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            finish();
        }, 2000);
    }
}
