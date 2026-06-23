package com.example.servicehub;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    TextView tvName, tvEmail, tvJoined;
    ImageView imgProfile;
    Button btnBack, btnEditProfile;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvJoined = findViewById(R.id.tvJoined);
        imgProfile = findViewById(R.id.imgProfile);

        btnBack = findViewById(R.id.btnBack);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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

                            String email =
                                    documentSnapshot.getString("email");

                            tvName.setText(name);
                            tvEmail.setText(email);

                            String imageUrl = documentSnapshot.getString("profileImage");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Glide.with(ProfileActivity.this)
                                        .load(imageUrl)
                                        .placeholder(R.mipmap.ic_launcher_round)
                                        .into(imgProfile);
                            }

                            Date createdAt =
                                    documentSnapshot
                                            .getTimestamp("createdAt")
                                            .toDate();

                            String formattedDate =
                                    new SimpleDateFormat(
                                            "dd MMM yyyy",
                                            Locale.getDefault()
                                    ).format(createdAt);

                            tvJoined.setText(formattedDate);
                        }
                    });
        }

        btnBack.setOnClickListener(v -> finish());

        btnEditProfile.setOnClickListener(v -> {
            startActivity(new android.content.Intent(ProfileActivity.this, EditProfileActivity.class));
        });
    }
}