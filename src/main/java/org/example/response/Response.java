package org.example.response;

import org.example.headers.Headers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Response {
    StatusCode statusCode;
    private Headers headers;
    private byte[] body;

    public Response() throws IOException {
        this.headers = new Headers();
        this.statusCode = StatusCode.OK;
        setDefaultHeaders(0);
        this.body = new byte[0];
    }

    public enum StatusCode {
        OK(200, "OK"),
        BAD_REQUEST(400, "Bad Request"),
        INTERNAL_SERVER_ERROR(500, "Internal Server Error");

        private final int code;
        private final String reason;
        StatusCode(int code, String reason) {
            this.code = code; this.reason = reason;
        }
    }

    public void writeStatusLine(OutputStream output) throws IOException {
        output.write(("HTTP/1.1 " + this.statusCode.code + " " + this.statusCode.reason + "\r\n").getBytes(StandardCharsets.UTF_8));
    }

    public void setDefaultHeaders(int contentLength) throws IOException {
        headers.put("Content-Length", String.valueOf(contentLength));
        headers.put("Connection", "close");
        headers.put("Content-Type", "text/plain");
    }

    public void writeHeaders(OutputStream output) throws IOException {
        headers.getHeaderMap().forEach((key, value) -> {
            String header = key + ": " + value + "\r\n";
            try {
                output.write(header.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        output.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public StatusCode convertIntToStatusCode(int code) {
        return switch (code) {
            case 200 -> StatusCode.OK;
            case 400 -> StatusCode.BAD_REQUEST;
            case 500 -> StatusCode.INTERNAL_SERVER_ERROR;
            default -> null;
        };
    }

    public void setBody(String body) throws IOException {
        this.body = body.getBytes();
        headers.put("content-length", String.valueOf(this.body.length));
    }

    public void writeTo(OutputStream output) throws IOException {
        writeStatusLine(output);
        writeHeaders(output);
        output.write(this.body);
    }
}
