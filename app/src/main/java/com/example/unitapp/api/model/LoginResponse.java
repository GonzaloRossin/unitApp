package com.example.unitapp.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("uber_id")
    @Expose
    private int uberId;
    @SerializedName("cabify_id")
    @Expose
    private int cabifyId;

    /**
     * No args constructor for use in serialization
     *
     */
    public LoginResponse() {
    }


    public LoginResponse(String token, int uberId, int cabifyId) {
        super();
        this.token = token;
        this.uberId = uberId;
        this.cabifyId = cabifyId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getUberId() {
        return uberId;
    }

    public void setUberId(int uberId) {
        this.uberId = uberId;
    }

    public int getCabifyId() {
        return cabifyId;
    }

    public void setCabifyId(int cabifyId) {
        this.cabifyId = cabifyId;
    }

}
