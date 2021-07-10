package com.example.unitapp.api.service;

import androidx.lifecycle.LiveData;

import com.example.unitapp.api.ApiResponse;
import com.example.unitapp.api.model.DriverResponse;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiRideService {
    @GET("drivers")
    LiveData<ApiResponse<DriverResponse>> getAvailableDrivers(@Query("latitude_orig") Double latitude_orig,
                                                              @Query("latitude_dest") Double latitude_dest,
                                                              @Query("longitude_orig") Double longitude_orig,
                                                              @Query("longitude_dest") Double longitude_dest);

}
