package org.example.server;

public class HandleError {
    private final int statusCode;
    private final String message;

    public HandleError(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
