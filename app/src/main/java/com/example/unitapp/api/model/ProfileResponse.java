package com.example.unitapp.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileResponse {

    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("emailVerified")
    @Expose
    private boolean emailVerified;
    @SerializedName("phone")
    @Expose
    private int phone;

    /**
     * No args constructor for use in serialization
     *
     */
    public ProfileResponse() {
    }

    /**
     *
     * @param emailVerified
     * @param phone
     * @param email
     */
    public ProfileResponse(String email, boolean emailVerified, int phone) {
        super();
        this.email = email;
        this.emailVerified = emailVerified;
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

}
