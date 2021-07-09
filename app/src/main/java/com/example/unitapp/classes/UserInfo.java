package com.example.unitapp.classes;

import android.graphics.drawable.Drawable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class UserInfo {

    private String username;
    private String phone;
    private String avatarUrl;
    private Drawable profileImg;

    public UserInfo(String username,String phone, String avatarUrl) {
        this.username = username;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
    }

    public String getUsername() {
        return username;
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
