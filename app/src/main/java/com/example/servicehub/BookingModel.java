package com.example.servicehub;

import com.google.firebase.Timestamp;

public class BookingModel {
    private String bookingId;
    private String customerId;
    private String serviceName;
    private String providerId;
    private String providerName;
    private String bookingDate;
    private String bookingTime;
    private String address;
    private String notes;
    private String status;
    private Timestamp createdAt;

    // Legacy fields kept for older booking documents
    private String date;
    private String time;
    private String price;

    public BookingModel() {
        // Required for Firebase
    }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public String getBookingDate() {
        return bookingDate != null ? bookingDate : date;
    }

    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }

    public String getBookingTime() {
        return bookingTime != null ? bookingTime : time;
    }

    public void setBookingTime(String bookingTime) { this.bookingTime = bookingTime; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
}
