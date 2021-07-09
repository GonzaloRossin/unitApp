package com.example.unitapp;

import android.content.Context;
import android.content.SharedPreferences;

public class UnitAppPreferences {
    private final String AUTH_TOKEN = "auth_token";
    private final String CABIFY_TOKEN = "auth_cabify";
    private final String UBER_TOKEN = "auth_uber";
    private final SharedPreferences sharedPreferences;

    public UnitAppPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
    }

    public void setAuthToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AUTH_TOKEN, token);
        editor.apply();
    }

    public void setCabifyToken(Integer token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(CABIFY_TOKEN, token);
        editor.apply();
    }

    public void setUberToken(Integer token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(UBER_TOKEN, token);
        editor.apply();
    }

    public String getAuthToken() {
        return sharedPreferences.getString(AUTH_TOKEN, null);
    }

    public Integer getUberToken() { return sharedPreferences.getInt(UBER_TOKEN, -1); }

    public Integer getCabify() { return sharedPreferences.getInt(CABIFY_TOKEN, -1); }
}
