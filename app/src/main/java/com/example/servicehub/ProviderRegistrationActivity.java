package com.example.servicehub;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProviderRegistrationActivity extends AppCompatActivity {

    CheckBox cbElectrician,
            cbPlumber,
            cbPainter,
            cbCarpenter,
            cbMechanic,
            cbACTechnician,
            cbHouseCleaner,
            cbApplianceRepair;

    EditText etPhone, etBio;
    Button btnSubmit, btnBack;

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_registration);

        cbElectrician = findViewById(R.id.cbElectrician);
        cbPlumber = findViewById(R.id.cbPlumber);
        cbPainter = findViewById(R.id.cbPainter);
        cbCarpenter = findViewById(R.id.cbCarpenter);
        cbMechanic = findViewById(R.id.cbMechanic);
        cbACTechnician = findViewById(R.id.cbACTechnician);
        cbHouseCleaner = findViewById(R.id.cbHouseCleaner);
        cbApplianceRepair = findViewById(R.id.cbApplianceRepair);

        etPhone = findViewById(R.id.etPhone);
        etBio = findViewById(R.id.etBio);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnBack = findViewById(R.id.btnBack);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnBack.setOnClickListener(v -> finish());

        btnSubmit.setOnClickListener(v -> {

            String uid = mAuth.getCurrentUser().getUid();

            db.collection("providerRequests")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        if (documentSnapshot.exists()) {

                            Toast.makeText(
                                    ProviderRegistrationActivity.this,
                                    "Application already submitted",
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        ArrayList<String> services = new ArrayList<>();

                        if (cbElectrician.isChecked())
                            services.add("Electrician");

                        if (cbPlumber.isChecked())
                            services.add("Plumber");

                        if (cbPainter.isChecked())
                            services.add("Painter");

                        if (cbCarpenter.isChecked())
                            services.add("Carpenter");

                        if (cbMechanic.isChecked())
                            services.add("Mechanic");

                        if (cbACTechnician.isChecked())
                            services.add("AC Technician");

                        if (cbHouseCleaner.isChecked())
                            services.add("House Cleaner");

                        if (cbApplianceRepair.isChecked())
                            services.add("Appliance Repair");

                        String phone = etPhone.getText().toString().trim();
                        String bio = etBio.getText().toString().trim();

                        if (services.isEmpty()) {

                            Toast.makeText(
                                    ProviderRegistrationActivity.this,
                                    "Select at least one service",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        if (phone.isEmpty() || bio.isEmpty()) {

                            Toast.makeText(
                                    ProviderRegistrationActivity.this,
                                    "Please fill all fields",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        if (!phone.matches("\\d{10}")) {

                            Toast.makeText(
                                    ProviderRegistrationActivity.this,
                                    "Enter a valid 10-digit phone number",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        if (bio.length() < 20) {

                            Toast.makeText(
                                    ProviderRegistrationActivity.this,
                                    "Bio should contain at least 20 characters",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        db.collection("users")
                                .document(uid)
                                .get()
                                .addOnSuccessListener(userDoc -> {

                                    String name =
                                            userDoc.getString("name");

                                    String email =
                                            userDoc.getString("email");

                                    Map<String, Object> providerRequest =
                                            new HashMap<>();

                                    providerRequest.put("userUid", uid);
                                    providerRequest.put("name", name);
                                    providerRequest.put("email", email);
                                    providerRequest.put("services", services);
                                    providerRequest.put("phone", phone);
                                    providerRequest.put("bio", bio);
                                    providerRequest.put("verificationStatus", "PENDING");
                                    providerRequest.put("availabilityStatus", "AVAILABLE");
                                    providerRequest.put("createdAt", Timestamp.now());

                                    db.collection("providerRequests")
                                            .document(uid)
                                            .set(providerRequest)
                                            .addOnSuccessListener(unused -> {

                                                Toast.makeText(
                                                        ProviderRegistrationActivity.this,
                                                        "Application Submitted Successfully",
                                                        Toast.LENGTH_LONG
                                                ).show();

                                                finish();

                                            })
                                            .addOnFailureListener(e -> {

                                                Toast.makeText(
                                                        ProviderRegistrationActivity.this,
                                                        e.getMessage(),
                                                        Toast.LENGTH_LONG
                                                ).show();

                                            });

                                })
                                .addOnFailureListener(e -> {

                                    Toast.makeText(
                                            ProviderRegistrationActivity.this,
                                            "Failed to fetch user details",
                                            Toast.LENGTH_LONG
                                    ).show();

                                });

                    });

        });
    }
}