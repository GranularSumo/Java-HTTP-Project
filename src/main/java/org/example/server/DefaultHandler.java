package org.example.server;

import org.example.request.Request;
import org.example.response.StatusCode;
import org.example.response.ResponseWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class DefaultHandler implements Handler {
    @Override
    public void handle(ResponseWriter responseWriter, Request request) throws IOException {
        String path = request.getRequestLine().requestTarget();

        if (path.equals("/")) {
            String htmlContent = Files.readString(Path.of("./src/main/resources/index.html"));
            responseWriter.writeHTMLResponse(StatusCode.OK, htmlContent);
        }

        else if (path.endsWith(".css")) {
            String cssContent = Files.readString(Path.of("./src/main/resources" + path));
            responseWriter.writeResponse(StatusCode.OK, cssContent, "text/css");
        }

        else if (path.endsWith(".js")) {
            String cssContent = Files.readString(Path.of("./src/main/resources" + path));
            responseWriter.writeResponse(StatusCode.OK, cssContent, "text/javascript");
        }

        else if (Objects.equals(request.getRequestLine().method(), "POST")) {
            String htmlContent = Files.readString(Path.of("./src/main/resources/bad-post.html"));
            responseWriter.writeHTMLResponse(StatusCode.BAD_REQUEST, htmlContent);
        }

        else {
            String htmlContent = Files.readString(Path.of("./src/main/resources/404.html"));
            responseWriter.writeHTMLResponse(StatusCode.NOT_FOUND, htmlContent);
        }

    }
}
