package com.example.security.dto;
import java.time.Instant;
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private Instant timestamp = Instant.now();
    public ErrorResponse(int status, String error, String message) {
        this.status = status; this.error = error; this.message = message;
    }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public Instant getTimestamp() { return timestamp; }
}
