package com.example.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check if user is already logged in
                SharedPreferences prefs = getSharedPreferences("CanteenPrefs", MODE_PRIVATE);
                String userId = prefs.getString("userId", null);

                Intent intent;
                if (userId != null) {
                    // User already registered, go to Main Page
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    // New user, go to Register Page
                    intent = new Intent(SplashActivity.this, RegisterActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 3000); // 3 seconds delay
    }
}