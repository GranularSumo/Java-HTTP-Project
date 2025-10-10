package org.example.server;

import org.example.request.Request;
import org.example.response.ResponseWriter;

import java.io.IOException;

public interface Handler {
    void handle(ResponseWriter responseWriter, Request request) throws IOException;
}
