package com.example.servicehub;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {

                Intent intent =
                        new Intent(
                                SplashActivity.this,
                                HomeActivity.class
                        );

                startActivity(intent);

            } else {

                Intent intent =
                        new Intent(
                                SplashActivity.this,
                                MainActivity.class
                        );

                startActivity(intent);
            }

            finish();

        }, 2000); // 2 seconds splash screen
    }
}