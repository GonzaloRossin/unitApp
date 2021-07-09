package com.example.unitapp;

import android.content.Context;
import android.content.SharedPreferences;

public class UnitAppPreferences {
    private final String AUTH_TOKEN = "auth_token";

    private final SharedPreferences sharedPreferences;

    public UnitAppPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
    }

    public void setAuthToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AUTH_TOKEN, token);
        editor.apply();
    }

    public String getAuthToken() {
        return sharedPreferences.getString(AUTH_TOKEN, null);
    }
}
