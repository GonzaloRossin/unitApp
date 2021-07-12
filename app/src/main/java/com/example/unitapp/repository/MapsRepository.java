package com.example.unitapp.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.unitapp.UnitApp;
import com.example.unitapp.api.ApiResponse;
import com.example.unitapp.api.GoogleApiClient;
import com.example.unitapp.api.model.DirectionResponse;
import com.example.unitapp.api.service.ApiMapsService;

import org.jetbrains.annotations.NotNull;

public class MapsRepository {
    private final ApiMapsService apiMapsService;

    public MapsRepository(UnitApp app) {
        this.apiMapsService = GoogleApiClient.create(app, ApiMapsService.class);
    }

    public LiveData<Resource<DirectionResponse>> getDirections(String origin,
                                                               String destination,
                                                               String key) {
        return new NetworkBoundResource<DirectionResponse, DirectionResponse>() {
            @NonNull
            @NotNull
            @Override
            protected LiveData<ApiResponse<DirectionResponse>> createCall() {
                return apiMapsService.getDirections(origin, destination, key);
            }
        }.asLiveData();
    }
}
