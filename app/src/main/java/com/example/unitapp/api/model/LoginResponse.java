package com.example.unitapp.api.model;

public class LoginResponse {
    String token;
    int uberId, cabifyId;

    public LoginResponse(String token, Integer uberId, Integer cabifyId) {
        this.token = token;
        this.uberId = uberId;
        this.cabifyId = cabifyId;
    }

    public String getToken() {
        return token;
    }

    public int getUberId() {
        return uberId;
    }

    public int getCabifyId() {
        return cabifyId;
    }
}
