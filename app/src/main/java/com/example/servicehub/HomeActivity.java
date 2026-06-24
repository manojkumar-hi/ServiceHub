package com.example.servicehub;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST = 100;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;

    MaterialCardView cardElectrician;
    MaterialCardView cardPlumber;
    MaterialCardView cardCarpenter;
    MaterialCardView cardMechanic;
    MaterialCardView cardAc;
    MaterialCardView cardRepair;
    MaterialCardView cardCleaner;
    MaterialCardView cardDriver;

    TextView tvDrawerName;
    TextView tvDrawerEmail;

    BottomNavigationView bottomNav;

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawerLayout);

        navigationView = findViewById(R.id.navigationView);

        View headerView =
                navigationView.getHeaderView(0);

        tvDrawerName =
                headerView.findViewById(R.id.tvDrawerName);

        tvDrawerEmail =
                headerView.findViewById(R.id.tvDrawerEmail);

        bottomNav = findViewById(R.id.bottomNav);

        toolbar = findViewById(R.id.toolbar);

        cardElectrician = findViewById(R.id.cardElectrician);
        cardPlumber = findViewById(R.id.cardPlumber);
        cardCarpenter = findViewById(R.id.cardCarpenter);
        cardMechanic = findViewById(R.id.cardMechanic);
        cardAc = findViewById(R.id.cardAc);
        cardRepair = findViewById(R.id.cardRepair);
        cardCleaner = findViewById(R.id.cardCleaner);
        cardDriver = findViewById(R.id.cardDriver);



        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();



        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);

        setupToolbar();
        setupDrawerMenu();
        setupServiceCards();
        setupBottomNavigation();

        loadDrawerUserInfo();
    }

    private void setupToolbar() {
        ImageView ivMenu = findViewById(R.id.ivMenu);
        if (ivMenu != null) {
            ivMenu.setOnClickListener(v ->
                    drawerLayout.openDrawer(GravityCompat.START)
            );
        }
    }

    private void setupDrawerMenu() {

        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_profile) {

                startActivity(
                        new Intent(
                                HomeActivity.this,
                                ProfileActivity.class
                        )
                );

            } else if (id == R.id.nav_provider) {

                startActivity(
                        new Intent(
                                HomeActivity.this,
                                ProviderRegistrationActivity.class
                        )
                );

            } else if (id == R.id.nav_location) {

                requestLocationUpdate();

            } else if (id == R.id.nav_logout) {

                FirebaseAuth.getInstance().signOut();

                Intent intent =
                        new Intent(
                                HomeActivity.this,
                                MainActivity.class
                        );

                startActivity(intent);
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);

            return true;
        });
    }

    private void setupServiceCards() {
        cardElectrician.setOnClickListener(v -> openServiceDetail("Electrician"));
        cardPlumber.setOnClickListener(v -> openServiceDetail("Plumber"));
        cardCarpenter.setOnClickListener(v -> openServiceDetail("Carpenter"));
        cardMechanic.setOnClickListener(v -> openServiceDetail("Mechanic"));
        cardAc.setOnClickListener(v -> openServiceDetail("AC Technician"));
        cardRepair.setOnClickListener(v -> openServiceDetail("Appliance Repair"));
        cardCleaner.setOnClickListener(v -> openServiceDetail("Cleaner"));
        cardDriver.setOnClickListener(v -> openServiceDetail("Driver"));
    }

    private void openServiceDetail(String serviceName) {
        Intent intent = new Intent(HomeActivity.this, ServiceDetailActivity.class);
        // Pass selected service name to the detail screen for the booking workflow
        intent.putExtra("serviceName", serviceName);
        startActivity(intent);
    }

    private void setupBottomNavigation() {

        bottomNav.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {

                return true;

            }
            else if (id == R.id.nav_bookings) {

            Intent intent = new Intent(HomeActivity.this, BookingActivity.class);
            startActivity(intent);

            return true;
        }else if (id == R.id.nav_reviews) {

                Toast.makeText(
                        this,
                        "Reviews Screen Coming Soon",
                        Toast.LENGTH_SHORT
                ).show();

                return true;
            }

            return false;
        });
    }


    private void loadDrawerUserInfo() {

        FirebaseUser currentUser =
                FirebaseAuth.getInstance()
                        .getCurrentUser();

        if (currentUser == null)
            return;

        db.collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        String name =
                                documentSnapshot.getString("name");

                        String email =
                                documentSnapshot.getString("email");

                        if (name != null)
                            tvDrawerName.setText(name);

                        if (email != null)
                            tvDrawerEmail.setText(email);
                    }
                });
    }
    private void requestLocationUpdate() {

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
                                "Enable GPS and try again",
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