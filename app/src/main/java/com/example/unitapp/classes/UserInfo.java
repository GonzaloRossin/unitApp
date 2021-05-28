package com.example.unitapp.classes;

import android.graphics.drawable.Drawable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class UserInfo {

    private String name;
    private String lastName;
    private Long birthdate;
    private String phone;
    private String avatarUrl;
    private Drawable profileImg;

    public UserInfo(String name, String lastName, Long birthdate, String phone, String avatarUrl) {
        this.name = name;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBirthdate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        return dateFormat.format(birthdate);
    }

    public String getPhone() {
        return phone;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
    public void setProfileImg(Drawable profileImg) {
        this.profileImg = profileImg;
    }
}
