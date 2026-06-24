package com.example.servicehub;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Shows nearby providers on a map and in a RecyclerView for the selected service category.
 */
public class NearbyProvidersActivity extends AppCompatActivity
        implements OnMapReadyCallback, ProviderAdapter.OnProviderInteractionListener {

    private static final int LOCATION_PERMISSION_REQUEST = 2001;

    private String serviceName;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseFirestore db;

    private RecyclerView rvProviders;
    private LinearLayout llEmptyState;
    private ProgressBar progressBar;
    private MaterialToolbar toolbar;

    private final List<ProviderModel> providerList = new ArrayList<>();
    private ProviderAdapter adapter;
    private final Map<String, Marker> providerMarkers = new HashMap<>();

    private double userLatitude = 0;
    private double userLongitude = 0;
    private Marker selectedMarker;
    private boolean providersLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nearby_providers);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        serviceName = getIntent().getStringExtra("serviceName");
        if (serviceName == null) {
            serviceName = getIntent().getStringExtra("service_name");
        }

        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initViews();
        setupRecyclerView();
        setupToolbar();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        requestUserLocation();
    }

    private void initViews() {
        rvProviders = findViewById(R.id.rvProviders);
        llEmptyState = findViewById(R.id.llEmptyState);
        progressBar = findViewById(R.id.progressBar);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
        if (serviceName != null) {
            toolbar.setSubtitle(serviceName);
        }
    }

    private void setupRecyclerView() {
        adapter = new ProviderAdapter(providerList, this);
        rvProviders.setLayoutManager(new LinearLayoutManager(this));
        rvProviders.setAdapter(adapter);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }

        if (userLatitude != 0 || userLongitude != 0) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(userLatitude, userLongitude), 12f));
        }

        loadProviders();
    }

    private void requestUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST
            );
            return;
        }
        fetchUserLocation();
    }

    private void fetchUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            loadProviders();
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this::onUserLocationReady)
                .addOnFailureListener(e -> loadProviders());
    }

    private void onUserLocationReady(Location location) {
        if (location != null) {
            userLatitude = location.getLatitude();
            userLongitude = location.getLongitude();
            if (googleMap != null) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(userLatitude, userLongitude), 12f));
            }
        }
        loadProviders();
    }

    /**
     * Loads providers from Firestore filtered by the selected service category.
     */
    private void loadProviders() {
        if (providersLoaded) {
            return;
        }

        if (serviceName == null) {
            progressBar.setVisibility(View.GONE);
            showEmptyState();
            Toast.makeText(this, "Service not selected", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        db.collection("providers")
                .whereEqualTo("category", serviceName)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    providersLoaded = true;
                    if (!task.isSuccessful()) {
                        Toast.makeText(this,
                                "Error loading providers: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        showEmptyState();
                        return;
                    }

                    providerList.clear();
                    providerMarkers.clear();
                    if (googleMap != null) {
                        googleMap.clear();
                    }

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        ProviderModel provider = document.toObject(ProviderModel.class);
                        provider.setProviderId(document.getId());

                        if (userLatitude != 0 || userLongitude != 0) {
                            provider.setDistanceKm(DistanceUtils.calculateDistanceKm(
                                    userLatitude, userLongitude,
                                    provider.getLatitude(), provider.getLongitude()));
                        }

                        providerList.add(provider);
                    }

                    Collections.sort(providerList,
                            (a, b) -> Double.compare(a.getDistanceKm(), b.getDistanceKm()));

                    if (providerList.isEmpty()) {
                        showEmptyState();
                    } else {
                        showProviderList();
                        addProviderMarkers();
                    }

                    adapter.notifyDataSetChanged();
                });
    }

    private void addProviderMarkers() {
        if (googleMap == null) {
            return;
        }

        for (ProviderModel provider : providerList) {
            LatLng position = new LatLng(provider.getLatitude(), provider.getLongitude());
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(provider.getName())
                    .snippet(provider.getServiceCharge()));
            if (marker != null) {
                providerMarkers.put(provider.getProviderId(), marker);
            }
        }
    }

    private void showEmptyState() {
        llEmptyState.setVisibility(View.VISIBLE);
        rvProviders.setVisibility(View.GONE);
    }

    private void showProviderList() {
        llEmptyState.setVisibility(View.GONE);
        rvProviders.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProviderSelected(ProviderModel provider, int position) {
        adapter.setSelectedPosition(position);
        rvProviders.smoothScrollToPosition(position);

        Marker marker = providerMarkers.get(provider.getProviderId());
        if (marker != null && googleMap != null) {
            if (selectedMarker != null) {
                selectedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_RED));
            }
            selectedMarker = marker;
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_AZURE));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 14f));
        }
    }

    @Override
    public void onViewProfile(ProviderModel provider) {
        Intent intent = new Intent(this, ProviderProfileActivity.class);
        intent.putExtra("providerId", provider.getProviderId());
        intent.putExtra("serviceName", serviceName);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (googleMap != null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                }
            }
            fetchUserLocation();
        } else {
            loadProviders();
        }
    }
}
