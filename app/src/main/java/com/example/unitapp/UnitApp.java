package com.example.unitapp;

import android.app.Application;

import com.example.unitapp.repository.MapsRepository;
import com.example.unitapp.repository.RideRepository;
import com.example.unitapp.repository.UserRepository;

public class UnitApp extends Application {
    UnitAppPreferences preferences;
    UserRepository userRepository;
    RideRepository rideRepository;
    MapsRepository mapsRepository;

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public RideRepository getRideRepository() {
        return rideRepository;
    }

    public UnitAppPreferences getPreferences() {
        return preferences;
    }

    public MapsRepository getMapsRepository() {
        return mapsRepository;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        preferences = new UnitAppPreferences(this);
        userRepository = new UserRepository(this);
        rideRepository = new RideRepository(this);
        mapsRepository = new MapsRepository(this);
    }
}
