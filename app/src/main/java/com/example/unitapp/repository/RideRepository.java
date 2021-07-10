package com.example.unitapp.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.unitapp.UnitApp;
import com.example.unitapp.api.ApiClient;
import com.example.unitapp.api.ApiResponse;
import com.example.unitapp.api.model.DriverResponse;
import com.example.unitapp.api.service.ApiRideService;

import org.jetbrains.annotations.NotNull;

public class RideRepository {
    private final ApiRideService apiRideService;
    public RideRepository(UnitApp app) {
        this.apiRideService = ApiClient.create(app, ApiRideService.class);
    }

    public LiveData<Resource<DriverResponse>> getAvailableDrivers(Double latitude_orig,
                                                                  Double latitude_dest,
                                                                  Double longitude_orig,
                                                                  Double longitude_dest) {
        return new NetworkBoundResource<DriverResponse, DriverResponse>() {
            @NonNull
            @NotNull
            @Override
            protected LiveData<ApiResponse<DriverResponse>> createCall() {
                return apiRideService.getAvailableDrivers(latitude_orig, latitude_dest, longitude_orig, longitude_dest);
            }
        }.asLiveData();
    }
}
