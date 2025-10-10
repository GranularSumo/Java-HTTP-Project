package org.example.server;

import org.example.headers.Headers;
import org.example.request.Request;
import org.example.response.ResponseWriter;
import org.example.response.StatusCode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class VideoHandler implements Handler {
    @Override
    public void handle(ResponseWriter responseWriter, Request request) throws IOException {
        byte[] htmlContent = Files.readAllBytes(Path.of("./src/main/resources/assets/video.mp4"));
        responseWriter.writeStatusLine(StatusCode.OK);
        Headers headers = new Headers();
        headers.put("Content-Type", "video/mp4");
        headers.put("Content-Length", String.valueOf(htmlContent.length));
        responseWriter.writeHeaders(headers);
        responseWriter.writeBody(htmlContent);
    }
}
