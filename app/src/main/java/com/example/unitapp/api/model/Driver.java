package com.example.unitapp.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Driver implements Parcelable {

    @SerializedName("name")
    @Expose
    String name;
    @SerializedName("latitude")
    @Expose
    double latitude;
    @SerializedName("longitude")
    @Expose
    double longitude;
    @SerializedName("capacity")
    @Expose
    int capacity;
    @SerializedName("plate")
    @Expose
    String plate;
    @SerializedName("estimated_pickup")
    @Expose
    double estimatedPickup;
    @SerializedName("estimated_price")
    @Expose
    int estimatedPrice;
    @SerializedName("estimated_arrival")
    @Expose
    double estimatedArrival;
    @SerializedName("service_id")
    @Expose
    int serviceId;

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
     * @param capacity
     */
    public Driver(String name, double latitude, double longitude, int capacity, String plate, double estimatedPickup, int estimatedPrice, double estimatedArrival, int serviceId) {
        super();
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.capacity = capacity;
        this.plate = plate;
        this.estimatedPickup = estimatedPickup;
        this.estimatedPrice = estimatedPrice;
        this.estimatedArrival = estimatedArrival;
        this.serviceId = serviceId;
    }

    protected Driver(Parcel in) {
        name = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        capacity = in.readInt();
        plate = in.readString();
        estimatedPickup = in.readDouble();
        estimatedPrice = in.readInt();
        estimatedArrival = in.readDouble();
        serviceId = in.readInt();
    }

    public static final Creator<Driver> CREATOR = new Creator<Driver>() {
        @Override
        public Driver createFromParcel(Parcel in) {
            return new Driver(in);
        }

        @Override
        public Driver[] newArray(int size) {
            return new Driver[size];
        }
    };

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

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
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

    public void setEstimatedPickup(int estimatedPickup) {
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeInt(capacity);
        parcel.writeString(plate);
        parcel.writeDouble(estimatedPickup);
        parcel.writeInt(estimatedPrice);
        parcel.writeDouble(estimatedArrival);
        parcel.writeInt(serviceId);
    }
}