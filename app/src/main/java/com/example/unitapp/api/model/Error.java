package com.example.unitapp.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Error {

    @SerializedName("error")
    @Expose
    private Error__1 error;

    /**
     * No args constructor for use in serialization
     *
     */
    public Error() {
    }

    /**
     *
     * @param error
     */
    public Error(Error__1 error) {
        super();
        this.error = error;
    }

    public Error__1 getError() {
        return error;
    }

    public void setError(Error__1 error) {
        this.error = error;
    }

}
