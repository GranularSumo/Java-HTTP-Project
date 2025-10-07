package org.example.request;

import org.example.headers.Headers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Represents an HTTP request.
 * This class can incrementally parse HTTP request data from byte streams,
 * maintaining parsing state between calls to handle partial data.
 * The parser progresses through states: INITIALISED → PARSING_HEADERS → DONE
 * Supports parsing both the HTTP request line and headers.
 */
public class Request {
    RequestLine requestLine;
    private Status status;
    private final Headers headers;
    private byte[] body;


    /**
     * Represents the current state of the HTTP request parser.
     */
    public enum Status {
        INITIALISED,
        PARSING_HEADERS,
        PARSING_BODY,
        DONE
    }

    /**
     * Creates a new Request instance in the INITIALISED state.
     */
    public Request() {
        this.status = Status.INITIALISED;
        this.headers = new Headers();
        this.body = new byte[0];
    }

    /**
     * Attempts to parse HTTP request data from the provided byte array.
     * This method can be called multiple times with incremental data until
     * a complete request line is found.
     *
     * @param data the byte array containing HTTP request data to parse
     * @return the number of bytes consumed from the input data, or 0 if more data is needed
     * @throws IOException if the request data is malformed or contains invalid HTTP syntax
     */
    public int parse(byte[] data) throws IOException {

        switch (this.status) {
            case INITIALISED:
                RequestLineParseResult result = RequestParser.parseRequestLine(data);
                if (result.getRequestLine() == null) {
                    return 0;
                }
                this.requestLine = result.getRequestLine();
                this.status = Status.PARSING_HEADERS;

                int headerBytes = this.headers.parse(result.getRestOfMessage());

                if (this.headers.isDone()) {
                    if (this.headers.contentLengthExists()) {
                        this.status = Status.PARSING_BODY;   
                    } else {
                        this.status = Status.DONE;
                    }
                }

                if (headerBytes == -1) {
                    return result.getBytesConsumed();
                }

                return result.getBytesConsumed() + headerBytes;
            case PARSING_HEADERS:
                headerBytes = this.headers.parse(data);
                if (this.headers.isDone()) {
                    if (this.headers.contentLengthExists()) {
                        this.status = Status.PARSING_BODY;
                    } else {
                        this.status = Status.DONE;
                    }
                }
                if (headerBytes == -1) {
                    return 0;
                }
                return headerBytes;
            case PARSING_BODY:
                int contentLength = Integer.parseInt(this.headers.getValue("content-length"));
                if (this.body.length + data.length > contentLength) {
                    throw new IOException("Request body exceeds stated content length");
                }
                byte[] newBody = Arrays.copyOf(this.body, this.body.length + data.length);
                System.arraycopy(data, 0, newBody, this.body.length, data.length);
                this.body = newBody;

                if (this.body.length == contentLength) {
                    this.status = Status.DONE;
                }
                return data.length;
            case DONE:
                return 0;
            default:
                throw new IllegalStateException("Invalid parsing state: " + this.status);
        }
    }

    /**
     * Returns the parsed HTTP request line.
     * Only available after the parser reaches DONE status.
     *
     * @return the parsed RequestLine object, or null if parsing is not complete
     */
    public RequestLine getRequestLine() {
        return this.requestLine;
    }

    /**
     * Returns the current parsing status of this Request.
     *
     * @return the current Status (INITIALISED, PARSING_HEADERS, or DONE)
     */
    public Status getStatus() {
        return this.status;
    }

    /**
     * Returns the parsed HTTP headers.
     * The Headers object contains all header field-value pairs parsed from the request,
     * with header names normalized to lowercase and duplicate headers combined.
     *
     * @return the Headers object containing parsed HTTP headers
     */
    public Headers getHeaders() {
        return this.headers;
    }

    public String getBodyAsString() {
        return new String(this.body, 0, this.body.length, StandardCharsets.UTF_8);
    }

    public byte[] getBody() {
        return this.body;
    }
}



