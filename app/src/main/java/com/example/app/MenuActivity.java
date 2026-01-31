package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {

    private int[] qtys = new int[8];
    private String[] names = {"Veg Burger", "Cheese Pizza", "Paneer Sandwich", "French Fries", "Samosa", "Cold Coffee", "Masala Tea", "Hot Coffee"};
    private int[] prices = {50, 120, 80, 60, 30, 40, 20, 30};
    
    private TextView[] tvQtys = new TextView[8];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Initialize TextViews and Buttons for 8 items
        for (int i = 0; i < 8; i++) {
            final int index = i;
            int qtyId = getResources().getIdentifier("tv_qty" + (i + 1), "id", getPackageName());
            int plusId = getResources().getIdentifier("btn_plus" + (i + 1), "id", getPackageName());
            int minusId = getResources().getIdentifier("btn_minus" + (i + 1), "id", getPackageName());

            tvQtys[i] = findViewById(qtyId);
            
            findViewById(plusId).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    qtys[index]++;
                    tvQtys[index].setText(String.valueOf(qtys[index]));
                }
            });

            findViewById(minusId).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (qtys[index] > 0) {
                        qtys[index]--;
                        tvQtys[index].setText(String.valueOf(qtys[index]));
                    }
                }
            });
        }

        findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int grandTotal = 0;
                ArrayList<String> orderSummary = new ArrayList<>();
                
                for (int i = 0; i < 8; i++) {
                    if (qtys[i] > 0) {
                        int itemTotal = qtys[i] * prices[i];
                        grandTotal += itemTotal;
                        orderSummary.add(names[i] + " x" + qtys[i] + " = ₹" + itemTotal);
                    }
                }

                if (grandTotal > 0) {
                    Intent intent = new Intent(MenuActivity.this, OrderActivity.class);
                    intent.putExtra("grandTotal", grandTotal);
                    intent.putStringArrayListExtra("orderSummary", orderSummary);
                    startActivity(intent);
                } else {
                    Toast.makeText(MenuActivity.this, "Please select at least one item", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}