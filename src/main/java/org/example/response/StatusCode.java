package org.example.response;

public enum StatusCode {
    OK(200, "OK"),
    BAD_REQUEST(400, "Bad Request"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NOT_FOUND(404, "Not Found");

    private final int code;
    private final String reason;
    StatusCode(int code, String reason) {
        this.code = code; this.reason = reason;
    }
    public int getCode() { return this.code; }
    public String getReason() { return this.reason; }

    public static StatusCode convertIntToStatusCode(int code) {
        return switch (code) {
            case 200 -> StatusCode.OK;
            case 404 -> StatusCode.NOT_FOUND;
            case 500 -> StatusCode.INTERNAL_SERVER_ERROR;
            default -> StatusCode.BAD_REQUEST;
        };
    }
}
