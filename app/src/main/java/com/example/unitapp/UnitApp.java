package com.example.unitapp;

import android.app.Application;

import com.example.unitapp.repository.UserRepository;

public class UnitApp extends Application {
    UnitAppPreferences preferences;
    UserRepository userRepository;

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public UnitAppPreferences getPreferences() {
        return preferences;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        preferences = new UnitAppPreferences(this);
        userRepository = new UserRepository(this);
    }
}
