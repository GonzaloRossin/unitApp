package com.example.unitapp.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Driver {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("latitude")
    @Expose
    private double latitude;
    @SerializedName("longitude")
    @Expose
    private double longitude;
    @SerializedName("plate")
    @Expose
    private String plate;
    @SerializedName("estimated_pickup")
    @Expose
    private double estimatedPickup;
    @SerializedName("estimated_price")
    @Expose
    private int estimatedPrice;
    @SerializedName("estimated_arrival")
    @Expose
    private double estimatedArrival;
    @SerializedName("service_id")
    @Expose
    private int serviceId;

    /**
     * No args constructor for use in serialization
     *
     */
    public Driver() {
    }

    /**
     *
     * @param estimatedPrice
     * @param latitude
     * @param name
     * @param plate
     * @param estimatedArrival
     * @param estimatedPickup
     * @param serviceId
     * @param longitude
     */
    public Driver(String name, double latitude, double longitude, String plate, double estimatedPickup, int estimatedPrice, double estimatedArrival, int serviceId) {
        super();
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.plate = plate;
        this.estimatedPickup = estimatedPickup;
        this.estimatedPrice = estimatedPrice;
        this.estimatedArrival = estimatedArrival;
        this.serviceId = serviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public double getEstimatedPickup() {
        return estimatedPickup;
    }

    public void setEstimatedPickup(double estimatedPickup) {
        this.estimatedPickup = estimatedPickup;
    }

    public int getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(int estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }

    public double getEstimatedArrival() {
        return estimatedArrival;
    }

    public void setEstimatedArrival(double estimatedArrival) {
        this.estimatedArrival = estimatedArrival;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

}
