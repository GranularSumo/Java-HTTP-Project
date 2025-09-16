package org.example.request;

import java.io.IOException;

import static org.example.request.RequestParser.parseRequestLine;

/**
 * Represents an HTTP request.
 * This class can incrementally parse HTTP request data from byte streams,
 * maintaining parsing state between calls to handle partial data.
 *
 * The parser progresses through states: INITIALISED → DONE
 * Currently supports parsing the HTTP request line only.
 */
public class Request {
    RequestLine requestLine;
    private Status status;
    /**
     * Represents the current state of the HTTP request parser.
     */
    public enum Status {
        INITIALISED,
        DONE
    }

    /**
     * Creates a new Request instance in the INITIALISED state.
     */
    public Request() {
        this.status = Status.INITIALISED;
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

        if (this.status == Status.DONE) {
            return 0;
        }

        RequestLineParseResult result = RequestParser.parseRequestLine(data);

        if (result.getRequestLine() == null) {
            return 0;
        }

        this.requestLine = result.getRequestLine();
        this.status = Status.DONE;
        return result.getBytesConsumed();
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
     * @return the current Status (INITIALISED or DONE)
     */
    public Status getStatus() {
        return this.status;
    }



}



