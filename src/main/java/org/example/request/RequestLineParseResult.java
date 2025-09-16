package org.example.request;

public class RequestLineParseResult {
    private final RequestLine requestLine;
    private final String restOfMessage;
    private final int bytesConsumed;


    public RequestLineParseResult(RequestLine requestLine, String restOfMessage, int bytesConsumed) {
        this.requestLine = requestLine;
        this.restOfMessage = restOfMessage;
        this.bytesConsumed = bytesConsumed;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public String getRestOfMessage() {
        return restOfMessage;
    }

    public int getBytesConsumed() {
        return bytesConsumed;
    }

}
