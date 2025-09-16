package org.example.request;

/**
 * Represents the first line of an HTTP request containing the method, target, and version.
 *
 * @param method the HTTP method (e.g., "GET", "POST", "PUT", "DELETE")
 * @param requestTarget the request target path (e.g., "/", "/coffee", "/api/users")
 * @param httpVersion the HTTP version number (e.g., "1.1" for HTTP/1.1)
 */
public record RequestLine(String method, String requestTarget, String httpVersion) {}
