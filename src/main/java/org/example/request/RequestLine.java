package org.example.request;

public record RequestLine(String method, String requestTarget, String httpVersion) {}
