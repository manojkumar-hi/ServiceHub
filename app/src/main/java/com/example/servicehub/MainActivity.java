package com.example.servicehub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvRegister;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        mAuth = FirebaseAuth.getInstance();

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null && user.isEmailVerified()) {

                                Toast.makeText(
                                        MainActivity.this,
                                        "Login Successful",
                                        Toast.LENGTH_SHORT
                                ).show();

                                Intent intent = new Intent(
                                        MainActivity.this,
                                        HomeActivity.class
                                );
                                startActivity(intent);
                                finish();

                            } else {

                                Toast.makeText(
                                        MainActivity.this,
                                        "Please verify your email first",
                                        Toast.LENGTH_LONG
                                ).show();

                                mAuth.signOut();
                            }

                        } else {

                            Toast.makeText(
                                    MainActivity.this,
                                    "Invalid Email or Password",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
        });
    }
}