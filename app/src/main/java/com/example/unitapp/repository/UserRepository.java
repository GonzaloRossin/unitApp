package com.example.unitapp.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.unitapp.UnitApp;
import com.example.unitapp.api.ApiClient;
import com.example.unitapp.api.ApiResponse;
import com.example.unitapp.api.model.LoginCredentials;
import com.example.unitapp.api.model.LoginResponse;
import com.example.unitapp.api.model.RegisterCredentials;
import com.example.unitapp.api.service.ApiUserService;

import org.jetbrains.annotations.NotNull;

public class UserRepository {
    private final ApiUserService apiService;
    public UserRepository(UnitApp app) {
        this.apiService = ApiClient.create(app, ApiUserService.class);
    }

    public LiveData<Resource<Void>> register(RegisterCredentials registerCredentials) {
        return new NetworkBoundResource<Void, Void>() {
            @NonNull
            @NotNull
            @Override
            protected LiveData<ApiResponse<Void>> createCall() {
                return apiService.register(registerCredentials);
            }
        }.asLiveData();
    }

    public LiveData<Resource<LoginResponse>> login(LoginCredentials loginCredentials) {
        return new NetworkBoundResource<LoginResponse, LoginResponse>() {
            @NonNull
            @NotNull
            @Override
            protected LiveData<ApiResponse<LoginResponse>> createCall() {
                return apiService.login(loginCredentials);
            }
        }.asLiveData();
    }
}
