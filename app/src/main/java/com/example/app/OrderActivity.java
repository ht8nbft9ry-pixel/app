package com.example.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class OrderActivity extends AppCompatActivity {

    private TextView tvOrderSummary, tvGrandTotal;
    private Button btnConfirmFinal;
    private DatabaseReference mDatabase;
    private final String dbUrl = "https://apporder123w-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        mDatabase = FirebaseDatabase.getInstance(dbUrl).getReference();

        tvOrderSummary = findViewById(R.id.tv_order_summary);
        tvGrandTotal = findViewById(R.id.tv_grand_total);
        btnConfirmFinal = findViewById(R.id.btn_confirm_final);

        int grandTotal = getIntent().getIntExtra("grandTotal", 0);
        ArrayList<String> orderSummary = getIntent().getStringArrayListExtra("orderSummary");

        StringBuilder summaryBuilder = new StringBuilder();
        if (orderSummary != null) {
            for (String item : orderSummary) {
                summaryBuilder.append(item).append("\n");
            }
        }
        tvOrderSummary.setText(summaryBuilder.toString());
        tvGrandTotal.setText("₹" + grandTotal);

        btnConfirmFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Generate a random token number for this order
                String tokenNumber = String.format("%03d", new Random().nextInt(1000));
                
                saveOrderToFirebase(orderSummary, grandTotal, tokenNumber);
                saveOrderToHistory(summaryBuilder.toString(), grandTotal);
                
                // Navigate to Success Page with Token
                Intent intent = new Intent(OrderActivity.this, SuccessActivity.class);
                intent.putExtra("tokenNumber", tokenNumber);
                startActivity(intent);
                finish();
            }
        });
    }

    private void saveOrderToFirebase(ArrayList<String> summary, int total, String token) {
        SharedPreferences prefs = getSharedPreferences("CanteenPrefs", MODE_PRIVATE);
        String userId = prefs.getString("userId", "Unknown");
        String userName = prefs.getString("userName", "Unknown");
        String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

        String orderId = mDatabase.child("orders").push().getKey();
        if (orderId == null) orderId = String.valueOf(System.currentTimeMillis());

        Map<String, Object> orderData = new HashMap<>();
        orderData.put("orderId", orderId);
        orderData.put("userId", userId);
        orderData.put("userName", userName);
        orderData.put("items", summary);
        orderData.put("totalAmount", total);
        orderData.put("timestamp", date);
        orderData.put("status", "Pending");
        orderData.put("tokenNumber", token);

        mDatabase.child("orders").child(orderId).setValue(orderData);
    }

    private void saveOrderToHistory(String summary, int total) {
        SharedPreferences prefs = getSharedPreferences("CanteenPrefs", MODE_PRIVATE);
        String existingHistory = prefs.getString("orderHistory", "");
        String date = new SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(new Date());
        String newEntry = "Date: " + date + "\n" + summary + "Total: ₹" + total + "\n--------------------\n";
        
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("orderHistory", newEntry + existingHistory);
        editor.apply();
    }
}