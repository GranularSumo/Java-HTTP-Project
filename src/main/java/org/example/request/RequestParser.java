package org.example.request;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class RequestParser {

    /**
     * Parses an HTTP request from an InputStream.
     * Reads the entire request content and extracts the request line.
     *
     * @param inputStream the input stream containing the HTTP request data
     * @return a Request object containing the parsed request line
     * @throws IOException if the request is empty, malformed, or cannot be read
     */
    public static Request requestFromReader(InputStream inputStream) throws IOException {

        String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        String[] lines = content.split("\r\n", 2);
        
        if (lines.length == 0) {
            throw new IOException("Empty request");
        }
        
        return new Request(parseRequestLine(lines[0]));
    }

    /**
     * Parses an HTTP request line string into a RequestLine object.
     * Validates the format, HTTP method, request target, and HTTP version.
     *
     * @param s the request line string (e.g., "GET /path HTTP/1.1")
     * @return a RequestLine object with parsed method, target, and version
     * @throws IOException if the request line format is invalid, contains an unsupported method,
     *                     invalid request target, or unsupported HTTP version
     */
    public static RequestLine parseRequestLine(String s) throws IOException {
        String[] parts = s.split(" ");
        if (parts.length != 3) {
            throw new IOException("Invalid request line format");
        }

        Set<String> validMethods = Set.of("GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS");

        if (!validMethods.contains(parts[0])) {
            throw new IOException("Invalid HTTP method: " + parts[0]);
        }
        
        String method = parts[0];

        if (!parts[1].startsWith("/") && !(method.equals("OPTIONS") && parts[1].startsWith("*"))) {
            throw new IOException("Invalid request target: " + parts[1]);
        }

        String requestTarget = parts[1];

        if (!parts[2].startsWith("HTTP/")) {
            throw new IOException("Invalid HTTP version format: " + parts[2]);
        }

        String httpVersion = parts[2].replace("HTTP/", "");

        if (!httpVersion.equals("1.1")) {
            throw new IOException("Unsupported HTTP version");
        }
        
        return new RequestLine(method, requestTarget, httpVersion);
    }
}
