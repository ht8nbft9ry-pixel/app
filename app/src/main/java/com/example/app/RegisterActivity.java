package com.example.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etId, etOtp;
    private TextInputLayout tilOtp;
    private Button btnRegister, btnSendOtp;
    private DatabaseReference mDatabase;
    
    // Correct Firebase URL for your region
    private final String dbUrl = "https://apporder123w-default-rtdb.asia-southeast1.firebasedatabase.app";

    private static final Map<String, String[]> USER_DATA = new HashMap<>();
    static {
        USER_DATA.put("001", new String[]{"Pathan nawazkhan", "1111"});
        USER_DATA.put("002", new String[]{"Krishnaditya gohil", "1222"});
        USER_DATA.put("003", new String[]{"Dwij Pandya", "1333"});
        USER_DATA.put("004", new String[]{"Meet Pandya", "1444"});
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Database with specific URL
        try {
            mDatabase = FirebaseDatabase.getInstance(dbUrl).getReference();
        } catch (Exception e) {
            Toast.makeText(this, "Firebase Init Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        etName = findViewById(R.id.et_name);
        etId = findViewById(R.id.et_id);
        etOtp = findViewById(R.id.et_otp);
        tilOtp = findViewById(R.id.til_otp);
        btnRegister = findViewById(R.id.btn_register);
        btnSendOtp = findViewById(R.id.btn_send_otp);

        btnSendOtp.setOnClickListener(v -> {
            String id = etId.getText().toString().trim();
            if (id.isEmpty()) {
                etId.setError("Enter ID first");
            } else if (!USER_DATA.containsKey(id)) {
                etId.setError("Invalid ID. Use 001, 002, 003, or 004");
            } else {
                String[] data = USER_DATA.get(id);
                etName.setText(data[0]);
                tilOtp.setVisibility(View.VISIBLE);
                btnSendOtp.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, "OTP sent to your registered ID", Toast.LENGTH_SHORT).show();
            }
        });

        btnRegister.setOnClickListener(v -> {
            String id = etId.getText().toString().trim();
            String name = etName.getText().toString().trim();
            String enteredOtp = etOtp.getText().toString().trim();

            if (id.isEmpty() || !USER_DATA.containsKey(id)) {
                Toast.makeText(RegisterActivity.this, "Please enter a valid ID", Toast.LENGTH_SHORT).show();
                return;
            }

            if (tilOtp.getVisibility() == View.GONE) {
                Toast.makeText(RegisterActivity.this, "Please send OTP first", Toast.LENGTH_SHORT).show();
                return;
            }

            String correctOtp = USER_DATA.get(id)[1];
            if (enteredOtp.equals(correctOtp)) {
                // Save user info in Firebase with listener to debug
                User user = new User(name, id);
                mDatabase.child("users").child(id).setValue(user)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Synced with Firebase!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Firebase Sync Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                // Save locally
                SharedPreferences prefs = getSharedPreferences("CanteenPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("userName", name);
                editor.putString("userId", id);
                editor.apply();

                Toast.makeText(RegisterActivity.this, "Verification Successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                etOtp.setError("Invalid OTP for this ID");
            }
        });
    }

    public static class User {
        public String name;
        public String id;
        public User() {}
        public User(String name, String id) {
            this.name = name;
            this.id = id;
        }
    }
}