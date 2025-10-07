package org.example.server;

import org.example.request.Request;

import java.io.OutputStream;

public interface Handler {
    HandleError handle(OutputStream output, Request request);
}
