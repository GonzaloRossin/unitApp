package com.example.unitapp.api.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DriverResponse {

    @SerializedName("drivers")
    @Expose
    private List<Driver> drivers = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public DriverResponse() {
    }

    /**
     *
     * @param drivers
     */
    public DriverResponse(List<Driver> drivers) {
        super();
        this.drivers = drivers;
    }

    public List<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<Driver> drivers) {
        this.drivers = drivers;
    }

}
