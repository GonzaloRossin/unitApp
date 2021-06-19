package com.example.unitapp.model;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {
    private final SharedPreferences sharedPreferences;
    private static final String PREFERENCE_NAME = "my-preferences";
    private static final String AUTH_TOKEN = "auth_token";

    public AppPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE); //solo yo voy a poder acceder a las sharedPreferences
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
