package com.example.unitapp.api.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class EndLocation__1 {

    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("lng")
    @Expose
    private double lng;

    /**
     * No args constructor for use in serialization
     *
     */
    public EndLocation__1() {
    }

    /**
     *
     * @param lng
     * @param lat
     */
    public EndLocation__1(double lat, double lng) {
        super();
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

}
