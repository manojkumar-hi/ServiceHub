package com.example.servicehub;

/**
 * Firestore model for approved service providers in the "providers" collection.
 */
public class ProviderModel {

    private String providerId;
    private String name;
    private String category;
    private double rating;
    private String serviceCharge;
    private double latitude;
    private double longitude;
    private String experience;
    private String bio;
    private int completedJobs;
    private String phone;
    private String profileImageUrl;

    // Transient field calculated on the client from user location
    private double distanceKm;

    public ProviderModel() {
        // Required for Firestore
    }

    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getServiceCharge() { return serviceCharge; }
    public void setServiceCharge(String serviceCharge) { this.serviceCharge = serviceCharge; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public int getCompletedJobs() { return completedJobs; }
    public void setCompletedJobs(int completedJobs) { this.completedJobs = completedJobs; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }
}
