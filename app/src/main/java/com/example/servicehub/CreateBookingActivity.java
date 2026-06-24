package com.example.servicehub;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Collects booking details and saves a new document to the Firestore bookings collection.
 */
public class CreateBookingActivity extends AppCompatActivity {

    private TextView tvProviderName, tvServiceName;
    private TextInputEditText etBookingDate, etBookingTime, etAddress, etNotes;
    private Button btnConfirmBooking;
    private MaterialToolbar toolbar;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private String providerId;
    private String providerName;
    private String serviceName;

    private final Calendar selectedDate = Calendar.getInstance();
    private final Calendar selectedTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_booking);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        providerId = getIntent().getStringExtra("providerId");
        providerName = getIntent().getStringExtra("providerName");
        serviceName = getIntent().getStringExtra("serviceName");

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        initViews();
        setupToolbar();
        populateBookingSummary();
        setupDateTimePickers();
        setupConfirmButton();
    }

    private void initViews() {
        tvProviderName = findViewById(R.id.tvProviderName);
        tvServiceName = findViewById(R.id.tvServiceName);
        etBookingDate = findViewById(R.id.etBookingDate);
        etBookingTime = findViewById(R.id.etBookingTime);
        etAddress = findViewById(R.id.etAddress);
        etNotes = findViewById(R.id.etNotes);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void populateBookingSummary() {
        tvProviderName.setText(providerName != null ? providerName : "Provider");
        tvServiceName.setText(serviceName != null ? serviceName : "Service");
    }

    private void setupDateTimePickers() {
        etBookingDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        selectedDate.set(year, month, dayOfMonth);
                        SimpleDateFormat dateFormat =
                                new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                        etBookingDate.setText(dateFormat.format(selectedDate.getTime()));
                    },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH)
            );
            dialog.getDatePicker().setMinDate(System.currentTimeMillis());
            dialog.show();
        });

        etBookingTime.setOnClickListener(v -> {
            TimePickerDialog dialog = new TimePickerDialog(
                    this,
                    (view, hourOfDay, minute) -> {
                        selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedTime.set(Calendar.MINUTE, minute);
                        SimpleDateFormat timeFormat =
                                new SimpleDateFormat("hh:mm a", Locale.getDefault());
                        etBookingTime.setText(timeFormat.format(selectedTime.getTime()));
                    },
                    selectedTime.get(Calendar.HOUR_OF_DAY),
                    selectedTime.get(Calendar.MINUTE),
                    false
            );
            dialog.show();
        });
    }

    private void setupConfirmButton() {
        btnConfirmBooking.setOnClickListener(v -> saveBooking());
    }

    /**
     * Creates a booking document in Firestore with status Pending.
     */
    private void saveBooking() {
        String bookingDate = etBookingDate.getText() != null
                ? etBookingDate.getText().toString().trim() : "";
        String bookingTime = etBookingTime.getText() != null
                ? etBookingTime.getText().toString().trim() : "";
        String address = etAddress.getText() != null
                ? etAddress.getText().toString().trim() : "";
        String notes = etNotes.getText() != null
                ? etNotes.getText().toString().trim() : "";

        if (TextUtils.isEmpty(bookingDate)) {
            etBookingDate.setError("Select a date");
            return;
        }
        if (TextUtils.isEmpty(bookingTime)) {
            etBookingTime.setError("Select a time");
            return;
        }
        if (TextUtils.isEmpty(address)) {
            etAddress.setError("Enter your address");
            return;
        }

        String customerId = auth.getUid();
        if (customerId == null) {
            Toast.makeText(this, "Please log in to book", Toast.LENGTH_SHORT).show();
            return;
        }

        btnConfirmBooking.setEnabled(false);

        Map<String, Object> booking = new HashMap<>();
        booking.put("customerId", customerId);
        booking.put("providerId", providerId);
        booking.put("providerName", providerName);
        booking.put("serviceName", serviceName);
        booking.put("bookingDate", bookingDate);
        booking.put("bookingTime", bookingTime);
        booking.put("address", address);
        booking.put("notes", notes);
        booking.put("status", "Pending");
        booking.put("createdAt", Timestamp.now());

        db.collection("bookings")
                .add(booking)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Booking confirmed!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, BookingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnConfirmBooking.setEnabled(true);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
