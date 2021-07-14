package com.example.unitapp.api.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Error__1 {

    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("errors")
    @Expose
    private List<Error__2> errors = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public Error__1() {
    }

    /**
     *
     * @param code
     * @param message
     * @param errors
     */
    public Error__1(int code, String message, List<Error__2> errors) {
        super();
        this.code = code;
        this.message = message;
        this.errors = errors;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Error__2> getErrors() {
        return errors;
    }

    public void setErrors(List<Error__2> errors) {
        this.errors = errors;
    }

}
