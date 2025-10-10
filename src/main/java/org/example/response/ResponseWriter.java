package org.example.response;

import org.example.headers.Headers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ResponseWriter {

    private final OutputStream output;
    private WriterState state;

    public enum WriterState {
        WRITING_STATUS_LINE,
        WRITING_HEADERS,
        WRITING_BODY,
        WRITING_TRAILERS,
        DONE;
    }

    public ResponseWriter(OutputStream output) {
        this.output = output;
        this.state = WriterState.WRITING_STATUS_LINE;
    }

    public void writeStatusLine(StatusCode statusCode) throws IOException {
        if (this.state != WriterState.WRITING_STATUS_LINE) {
            throw new IOException("Response writer is being called in the wrong order");
        }
        output.write(("HTTP/1.1 " + statusCode.getCode() + " " + statusCode.getReason() + "\r\n").getBytes(StandardCharsets.UTF_8));
        this.state = WriterState.WRITING_HEADERS;
    }

    public void writeHeaders(Headers headers) throws IOException {
        if (this.state != WriterState.WRITING_HEADERS && this.state != WriterState.WRITING_TRAILERS) {
            throw new IOException("Response writer is being called in the wrong order");
        }
        headers.getHeaderMap().forEach((key, value) -> {
            String header = key + ": " + value + "\r\n";
            try {
                output.write(header.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        output.write("\r\n".getBytes(StandardCharsets.UTF_8));
        boolean isTrailer = (this.state == WriterState.WRITING_TRAILERS);
        this.state = isTrailer ? WriterState.DONE : WriterState.WRITING_BODY;
    }

    public void writeBody(byte[] p) throws IOException {
        if (this.state != WriterState.WRITING_BODY) {
            throw new IOException("Response writer is being called in the wrong order");
        }
        output.write(p);
        this.state = WriterState.DONE;
    }

    public void writeChunkedBody(byte[] chunk) throws IOException {
        if (this.state != WriterState.WRITING_BODY) {
            throw new IOException("Response writer is being called in the wrong order");
        }
        if (chunk.length <= 0) {
            throw new IOException("The chunk failed to write any data.");
        }

        String chunkLength = Integer.toHexString(chunk.length);

        String chunkSizeLine = chunkLength + "\r\n";
        output.write(chunkSizeLine.getBytes());
        output.write(chunk);
        output.write("\r\n".getBytes());
        output.flush();
    }

    public void writeChunkedBodyDone() throws IOException {
        output.write("0\r\n".getBytes());
        this.state = WriterState.WRITING_TRAILERS;
    }


    public void writeHTMLResponse(StatusCode code, String htmlBody) throws IOException {
        Headers headers = new Headers();
        headers.put("Content-Length", String.valueOf(htmlBody.getBytes().length));
        headers.put("Connection", "close");
        headers.put("Content-Type", "text/html");
        writeStatusLine(code);
        writeHeaders(headers);
        writeBody(htmlBody.getBytes(StandardCharsets.UTF_8));
    }

    public void writeResponse(StatusCode code, String content, String contentType) throws IOException {
        Headers headers = new Headers();
        headers.put("Content-Length", String.valueOf(content.getBytes().length));
        headers.put("Connection", "close");
        headers.put("Content-Type", contentType);
        writeStatusLine(code);
        writeHeaders(headers);
        writeBody(content.getBytes(StandardCharsets.UTF_8));
    }

    public void writeChunkedResponseHeaders(StatusCode code) throws IOException {
        Headers headers = new Headers();
        headers.put("Connection", "close");
        headers.put("Transfer-Encoding", "chunked");
        headers.put("Trailer", "X-Content-SHA256, X-Content-Length");
        writeStatusLine(code);
        writeHeaders(headers);
    }

    public void writeResponseTrailers(byte[] hash, String messageLength) throws IOException {
        Headers trailers = new Headers();
        trailers.put("X-Content-SHA256", convertHashbytes(hash));
        trailers.put("X-Content-Length", messageLength);
        writeHeaders(trailers);
        this.state = WriterState.DONE;
    }

    public String convertHashbytes(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
