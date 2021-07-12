package com.example.unitapp.api.service;

import androidx.lifecycle.LiveData;

import com.example.unitapp.api.ApiResponse;
import com.example.unitapp.api.model.DirectionResponse;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiMapsService {
    @GET("maps/api/directions/json")
    LiveData<ApiResponse<DirectionResponse>> getDirections(@Query("origin") String origin,
                                                           @Query("destination") String destination,
                                                           @Query("key") String key);
}
