package org.example.request;

import java.io.IOException;
import java.io.Reader;
import java.util.Set;

public class RequestParser {
    public static Request requestFromReader(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[1024];
        int bytesRead;
        
        while ((bytesRead = reader.read(buffer)) != -1) {
            sb.append(buffer, 0, bytesRead);
        }
        
        String rawRequest = sb.toString();
        String[] lines = rawRequest.split("\r\n");
        
        if (lines.length == 0) {
            throw new IOException("Empty request");
        }

        RequestLine requestLine = parseRequestLine(lines[0]);
        
        return new Request(requestLine);
    }

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
