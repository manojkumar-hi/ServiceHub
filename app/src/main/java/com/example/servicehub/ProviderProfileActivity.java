package com.example.servicehub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Displays full provider details before the customer creates a booking.
 */
public class ProviderProfileActivity extends AppCompatActivity {

    private CircleImageView ivProfileImage;
    private TextView tvProviderName, tvExperience, tvBio, tvRating, tvCompletedJobs,
            tvServiceCharge, tvPhone;
    private Button btnBookProvider;
    private MaterialToolbar toolbar;

    private FirebaseFirestore db;
    private String providerId;
    private String serviceName;
    private String providerName;
    private String serviceCharge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_provider_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        providerId = getIntent().getStringExtra("providerId");
        serviceName = getIntent().getStringExtra("serviceName");

        db = FirebaseFirestore.getInstance();
        initViews();
        setupToolbar();
        btnBookProvider.setEnabled(false);
        loadProviderProfile();
    }

    private void initViews() {
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvProviderName = findViewById(R.id.tvProviderName);
        tvExperience = findViewById(R.id.tvExperience);
        tvBio = findViewById(R.id.tvBio);
        tvRating = findViewById(R.id.tvRating);
        tvCompletedJobs = findViewById(R.id.tvCompletedJobs);
        tvServiceCharge = findViewById(R.id.tvServiceCharge);
        tvPhone = findViewById(R.id.tvPhone);
        btnBookProvider = findViewById(R.id.btnBookProvider);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadProviderProfile() {
        if (providerId == null) {
            Toast.makeText(this, "Provider not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db.collection("providers").document(providerId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        Toast.makeText(this, "Provider not found", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    ProviderModel provider = documentSnapshot.toObject(ProviderModel.class);
                    if (provider == null) {
                        return;
                    }

                    providerName = provider.getName();
                    serviceCharge = provider.getServiceCharge();

                    tvProviderName.setText(provider.getName());
                    tvExperience.setText(provider.getExperience() + " experience");
                    tvBio.setText(provider.getBio());
                    tvRating.setText(String.format("⭐ %.1f", provider.getRating()));
                    tvCompletedJobs.setText(provider.getCompletedJobs() + " jobs completed");
                    tvServiceCharge.setText(provider.getServiceCharge());
                    tvPhone.setText(provider.getPhone());

                    if (provider.getProfileImageUrl() != null
                            && !provider.getProfileImageUrl().isEmpty()) {
                        Glide.with(this)
                                .load(provider.getProfileImageUrl())
                                .placeholder(R.drawable.profile_placeholder)
                                .into(ivProfileImage);
                    }

                    btnBookProvider.setEnabled(true);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        btnBookProvider.setOnClickListener(v -> {
            if (providerName == null) {
                Toast.makeText(this, "Loading provider details...", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, CreateBookingActivity.class);
            intent.putExtra("providerId", providerId);
            intent.putExtra("providerName", providerName);
            intent.putExtra("serviceName", serviceName);
            intent.putExtra("serviceCharge", serviceCharge);
            startActivity(intent);
        });
    }
}
