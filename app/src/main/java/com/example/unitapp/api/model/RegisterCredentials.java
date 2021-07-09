package com.example.unitapp.api.model;

public class RegisterCredentials {
    String email, password;
    Integer phone;
    public RegisterCredentials(String email, String password, Integer phone) {
        this.email = email;
        this.password = password;
        this.phone = phone;
    }
}
