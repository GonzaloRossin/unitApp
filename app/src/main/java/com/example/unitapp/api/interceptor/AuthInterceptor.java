package com.example.unitapp.api.interceptor;

import androidx.annotation.NonNull;

import com.example.unitapp.UnitApp;
import com.example.unitapp.UnitAppPreferences;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final UnitAppPreferences preferences;

    public AuthInterceptor(UnitApp application) {
        preferences = application.getPreferences();
    }

    @NotNull
    @Override
    public Response intercept(@NonNull Interceptor.Chain chain) throws IOException {
        Request.Builder request = chain.request().newBuilder();
        if (preferences.getAuthToken() != null) {
            request.addHeader("Authorization", "Bearer " + preferences.getAuthToken());
        }
        return chain.proceed(request.build());
    }
}
