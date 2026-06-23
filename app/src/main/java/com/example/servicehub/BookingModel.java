package com.example.servicehub;

public class BookingModel {
    private String bookingId;
    private String customerId;
    private String serviceName;
    private String providerId;
    private String providerName;
    private String date;
    private String time;
    private String price;
    private String status;

    public BookingModel() {
        // Required for Firebase
    }

    public BookingModel(String bookingId, String customerId, String serviceName, String providerId, String providerName, String date, String time, String price, String status) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.serviceName = serviceName;
        this.providerId = providerId;
        this.providerName = providerName;
        this.date = date;
        this.time = time;
        this.price = price;
        this.status = status;
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

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
