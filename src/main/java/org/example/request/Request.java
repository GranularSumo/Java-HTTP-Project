package org.example.request;

/**
 * Represents an HTTP request containing a request line.
 * This record encapsulates the parsed components of an HTTP request.
 *
 * @param requestLine the parsed HTTP request line containing method, target, and version
 */
public record Request(RequestLine requestLine) {}



