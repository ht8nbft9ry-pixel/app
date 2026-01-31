package com.example.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView tvName = findViewById(R.id.tv_profile_name);
        TextView tvId = findViewById(R.id.tv_profile_id);
        TextView tvOrderHistory = findViewById(R.id.tv_order_history);
        EditText etFeedback = findViewById(R.id.et_feedback);
        Button btnSubmitFeedback = findViewById(R.id.btn_submit_feedback);
        Button btnLogout = findViewById(R.id.btn_logout);

        SharedPreferences prefs = getSharedPreferences("CanteenPrefs", MODE_PRIVATE);
        String name = prefs.getString("userName", "User Name");
        String id = prefs.getString("userId", "000");
        String orderHistory = prefs.getString("orderHistory", "No orders yet");

        tvName.setText(name);
        tvId.setText("ID: " + id);
        tvOrderHistory.setText(orderHistory);

        btnSubmitFeedback.setOnClickListener(v -> {
            String feedback = etFeedback.getText().toString().trim();
            if (!feedback.isEmpty()) {
                Toast.makeText(this, "Thank you for your feedback!", Toast.LENGTH_SHORT).show();
                etFeedback.setText("");
            } else {
                Toast.makeText(this, "Please enter feedback first", Toast.LENGTH_SHORT).show();
            }
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(ProfileActivity.this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}