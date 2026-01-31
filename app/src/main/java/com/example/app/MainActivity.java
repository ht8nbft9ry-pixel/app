package com.example.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private int[] qtys = new int[10];
    private String[] names = {
        "Veg Burger", "Cheese Pizza", "Paneer Sandwich", "Cold Coffee", 
        "Samosa", "Masala Tea", "Hot Coffee", "Maggi", "Maggi", "Pasta"
    };
    private int[] prices = {50, 120, 80, 40, 30, 20, 30, 60, 40, 90};
    
    private int[] images = {
        R.drawable.img_3, // Veg Burger photo
        R.drawable.img_4,
        R.drawable.img_5,
        R.drawable.img_6,
        R.drawable.img_7,
        R.drawable.img_2,
        R.drawable.img_9,
        R.drawable.img_8,
       // R.drawable.im, // Maggi photo
        R.drawable.img_10
    };
    
    private TextView[] tvQtys = new TextView[10];
    private CardView cardCheckout;
    private TextView tvCartCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardCheckout = findViewById(R.id.card_checkout);
        tvCartCount = findViewById(R.id.tv_cart_count);
        TextView tvMainUsername = findViewById(R.id.tv_main_username);

        SharedPreferences prefs = getSharedPreferences("CanteenPrefs", MODE_PRIVATE);
        String name = prefs.getString("userName", "User");
        tvMainUsername.setText(name);

        ImageButton btnProfile = findViewById(R.id.btn_profile_top);
        btnProfile.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProfileActivity.class)));

        setupMenuItems();

        cardCheckout.setOnClickListener(v -> {
            int grandTotal = 0;
            ArrayList<String> orderSummary = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                if (qtys[i] > 0) {
                    int itemTotal = qtys[i] * prices[i];
                    grandTotal += itemTotal;
                    orderSummary.add(names[i] + " x" + qtys[i] + " = ₹" + itemTotal);
                }
            }
            Intent intent = new Intent(MainActivity.this, OrderActivity.class);
            intent.putExtra("grandTotal", grandTotal);
            intent.putStringArrayListExtra("orderSummary", orderSummary);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (int i = 0; i < 10; i++) {
            qtys[i] = 0;
        }
        updateCartUI();
    }

    private void setupMenuItems() {
        for (int i = 0; i < 10; i++) {
            final int index = i;
            int foodLayoutId = getResources().getIdentifier("food_" + (index + 1), "id", getPackageName());
            View foodView = findViewById(foodLayoutId);
            
            if (foodView != null) {
                TextView tvName = foodView.findViewById(R.id.tv_food_name);
                TextView tvPrice = foodView.findViewById(R.id.tv_food_price);
                ImageView ivFood = foodView.findViewById(R.id.iv_food_img);
                tvQtys[index] = foodView.findViewById(R.id.tv_qty);
                View btnPlus = foodView.findViewById(R.id.btn_plus);
                View btnMinus = foodView.findViewById(R.id.btn_minus);

                tvName.setText(names[index]);
                tvPrice.setText("₹" + prices[index]);
                ivFood.setImageResource(images[index]);

                btnPlus.setOnClickListener(v -> {
                    qtys[index]++;
                    updateCartUI();
                });

                btnMinus.setOnClickListener(v -> {
                    if (qtys[index] > 0) {
                        qtys[index]--;
                        updateCartUI();
                    }
                });
            }
        }
    }

    private void updateCartUI() {
        int totalItems = 0;
        int totalAmount = 0;
        for (int i = 0; i < 10; i++) {
            if (tvQtys[i] != null) {
                tvQtys[i].setText(String.valueOf(qtys[i]));
            }
            totalItems += qtys[i];
            totalAmount += qtys[i] * prices[i];
        }

        if (totalItems > 0) {
            cardCheckout.setVisibility(View.VISIBLE);
            tvCartCount.setText(totalItems + " Item" + (totalItems > 1 ? "s" : "") + " | ₹" + totalAmount);
        } else {
            cardCheckout.setVisibility(View.GONE);
        }
    }
}