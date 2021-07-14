package com.example.unitapp.api;

import android.util.Log;

import com.example.unitapp.api.model.Error;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class ApiResponse<T> {

    private T data;
    private Error error;

    public T getData() {
        return data;
    }

    public Error getError() {
        return error;
    }

    public ApiResponse(Response<T> response) {
        parseResponse(response);
    }

    public ApiResponse(Throwable throwable) {
        this.error = buildError(throwable.getMessage());
    }

    private void parseResponse(Response<T> response) {
        if (response.isSuccessful()) {
            this.data = response.body();
            return;
        }

        if (response.errorBody() == null) {
            this.error = buildError("Missing error body");
            return;
        }

        String message;

        try {
            message = response.errorBody().string();
        } catch (IOException exception) {
            Log.e("API", exception.toString());
            this.error = buildError(exception.getMessage());
            return;
        }

        if (message.trim().length() > 0) {
            Gson gson = new Gson();
            this.error =  gson.fromJson(message, new TypeToken<Error>() {}.getType());
        }
    }

    private static Error buildError(String message) {
        return new Error();
    }
}
