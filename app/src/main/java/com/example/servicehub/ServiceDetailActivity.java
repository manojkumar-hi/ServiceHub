package com.example.servicehub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ServiceDetailActivity extends AppCompatActivity {

    private ImageView ivServiceImage;
    private TextView tvServiceName, tvPrice, tvDescription;
    private Button btnBookNow;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_service_detail);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        handleIntentData();

        btnBack.setOnClickListener(v -> finish());
        btnBookNow.setOnClickListener(v -> {
            Toast.makeText(this, "Booking feature coming soon!", Toast.LENGTH_SHORT).show();
            // In a real app, this would navigate to a booking confirmation or payment screen
        });
    }

    private void initViews() {
        ivServiceImage = findViewById(R.id.ivServiceImage);
        tvServiceName = findViewById(R.id.tvServiceName);
        tvPrice = findViewById(R.id.tvPrice);
        tvDescription = findViewById(R.id.tvDescription);
        btnBookNow = findViewById(R.id.btnBookNow);
        btnBack = findViewById(R.id.btnBack);
    }

    private void handleIntentData() {
        String serviceName = getIntent().getStringExtra("service_name");
        
        if (serviceName != null) {
            tvServiceName.setText(serviceName);
            setServiceDetails(serviceName);
        }
    }

    private void setServiceDetails(String serviceName) {
        switch (serviceName) {
            case "Electrician":
                tvPrice.setText("$50 - $150");
                tvDescription.setText("Professional electrical services including wiring, repairs, and installations. Our licensed electricians ensure safety and quality work.");
                ivServiceImage.setImageResource(R.drawable.electrician);
                break;
            case "Plumber":
                tvPrice.setText("$60 - $200");
                tvDescription.setText("Expert plumbing solutions for leaks, pipe repairs, and fixture installations. Available for emergency repairs and routine maintenance.");
                ivServiceImage.setImageResource(R.drawable.plumber);
                break;
            case "Cleaner":
                tvPrice.setText("$30 - $100");
                tvDescription.setText("Deep cleaning services for homes and offices. Our experienced cleaners use eco-friendly products to leave your space sparkling.");
                ivServiceImage.setImageResource(R.drawable.cleaner);
                break;
            case "Carpenter":
                tvPrice.setText("$45 - $180");
                tvDescription.setText("Skilled carpentry for furniture repair, custom woodwork, and general home improvements. Quality craftsmanship guaranteed.");
                ivServiceImage.setImageResource(R.drawable.carpenter);
                break;
            case "Mechanic":
                tvPrice.setText("$80 - $300");
                tvDescription.setText("Mobile vehicle repairs and maintenance. From oil changes to engine diagnostics, our mechanics come to you.");
                ivServiceImage.setImageResource(R.drawable.mechanic);
                break;
            case "AC Technician":
                tvPrice.setText("$55 - $170");
                tvDescription.setText("Air conditioning installation, servicing, and repair. Keep your home cool with our expert HVAC technicians.");
                ivServiceImage.setImageResource(R.drawable.ac_technician);
                break;
            default:
                tvPrice.setText("Price on Request");
                tvDescription.setText("High-quality service provided by our network of skilled professionals. Contact us for a detailed quote.");
                break;
        }
    }
}
