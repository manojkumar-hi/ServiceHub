package com.example.servicehub;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST = 100;

    TextView tvWelcome;

    Button btnProfile,
            btnBecomeProvider,
            btnUpdateLocation,
            btnLogout;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvWelcome = findViewById(R.id.tvWelcome);

        btnProfile = findViewById(R.id.btnProfile);
        btnBecomeProvider = findViewById(R.id.btnBecomeProvider);
        btnUpdateLocation = findViewById(R.id.btnUpdateLocation);
        btnLogout = findViewById(R.id.btnLogout);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            String uid = currentUser.getUid();

            db.collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        if (documentSnapshot.exists()) {

                            String name =
                                    documentSnapshot.getString("name");

                            tvWelcome.setText(
                                    "Welcome, " + name
                            );
                        }
                    });
        }

        btnProfile.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            HomeActivity.this,
                            ProfileActivity.class
                    );

            startActivity(intent);

        });

        btnBecomeProvider.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            HomeActivity.this,
                            ProviderRegistrationActivity.class
                    );

            startActivity(intent);

        });

        btnUpdateLocation.setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION
                        },
                        LOCATION_PERMISSION_REQUEST
                );

            } else {

                updateLocation();

            }

        });

        btnLogout.setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();

            Intent intent =
                    new Intent(
                            HomeActivity.this,
                            MainActivity.class
                    );

            startActivity(intent);

            finish();

        });
    }

    private void updateLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {

                    if (location == null) {

                        Toast.makeText(
                                this,
                                "Location is turned off. Please enable GPS and try again.",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    String uid =
                            FirebaseAuth.getInstance()
                                    .getCurrentUser()
                                    .getUid();

                    Map<String, Object> locationData =
                            new HashMap<>();

                    locationData.put(
                            "latitude",
                            location.getLatitude()
                    );

                    locationData.put(
                            "longitude",
                            location.getLongitude()
                    );

                    locationData.put(
                            "locationUpdatedAt",
                            Timestamp.now()
                    );

                    db.collection("users")
                            .document(uid)
                            .update(locationData)
                            .addOnSuccessListener(unused -> {

                                Toast.makeText(
                                        this,
                                        "Location Updated Successfully",
                                        Toast.LENGTH_LONG
                                ).show();

                            })
                            .addOnFailureListener(e -> {

                                Toast.makeText(
                                        this,
                                        e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();

                            });

                });
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == LOCATION_PERMISSION_REQUEST) {

            if (grantResults.length > 0
                    && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {

                updateLocation();

            } else {

                Toast.makeText(
                        this,
                        "Location permission denied",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }
}