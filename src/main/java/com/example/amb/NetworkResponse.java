package com.example.ambulanceapp;


public class NetworkResponse {
    public boolean success;
    public String message;

    public NetworkResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
