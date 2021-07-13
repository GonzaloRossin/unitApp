package com.example.unitapp.api.service;

import androidx.lifecycle.LiveData;

import com.example.unitapp.api.ApiResponse;
import com.example.unitapp.api.model.LoginCredentials;
import com.example.unitapp.api.model.ProfileResponse;
import com.example.unitapp.api.model.RegisterCredentials;
import com.example.unitapp.api.model.LoginResponse;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiUserService {
    @POST("user/register")
    LiveData<ApiResponse<LoginResponse>> register(@Body RegisterCredentials registerCredentials);

    @POST("user/login")
    LiveData<ApiResponse<LoginResponse>> login(@Body LoginCredentials loginCredentials);

    @GET("user/current")
    LiveData<ApiResponse<ProfileResponse>> getCurrentUser();

}
