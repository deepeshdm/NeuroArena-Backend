package com.neuroarena.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {
    private String status;  // "success" or "error"
    private T data;
    private String message;
    
    private ApiResponse(String status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }
    
    // Success response with data
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("success", data, null);
    }
    
    // Success response with message (no data)
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>("success", null, message);
    }
    
    // Success response with data and message
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>("success", data, message);
    }
    
    // Error response
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("error", null, message);
    }
    
    // Error response with data
    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>("error", data, message);
    }
}