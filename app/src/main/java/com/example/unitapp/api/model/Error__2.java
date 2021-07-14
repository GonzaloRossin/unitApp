package com.example.unitapp.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Error__2 {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("domain")
    @Expose
    private String domain;
    @SerializedName("reason")
    @Expose
    private String reason;

    /**
     * No args constructor for use in serialization
     *
     */
    public Error__2() {
    }

    /**
     *
     * @param reason
     * @param domain
     * @param message
     */
    public Error__2(String message, String domain, String reason) {
        super();
        this.message = message;
        this.domain = domain;
        this.reason = reason;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
