package org.example.request;

/**
 * Represents the result of parsing an HTTP request line.
 * Contains the parsed RequestLine, any remaining unparsed data, and the number of bytes consumed.
 * This class is used by RequestParser to return both the parsing result and information
 * needed for continued parsing of the remaining HTTP request data.
 */
public class RequestLineParseResult {
    private final RequestLine requestLine;
    private final byte[] restOfMessage;
    private final int bytesConsumed;


    /**
     * Creates a new RequestLineParseResult.
     *
     * @param requestLine   the parsed RequestLine, or null if parsing was incomplete
     * @param restOfMessage the remaining unparsed data after the request line
     * @param bytesConsumed the number of bytes consumed from the original input during parsing
     */
    public RequestLineParseResult(RequestLine requestLine, byte[] restOfMessage, int bytesConsumed) {
        this.requestLine = requestLine;
        this.restOfMessage = restOfMessage;
        this.bytesConsumed = bytesConsumed;
    }

    /**
     * Returns the parsed HTTP request line.
     *
     * @return the RequestLine object, or null if parsing was incomplete
     */
    public RequestLine getRequestLine() {
        return requestLine;
    }

    /**
     * Returns the remaining unparsed data after the request line.
     * This data typically contains HTTP headers and possibly a message body.
     *
     * @return byte array containing the rest of the HTTP message
     */
    public byte[] getRestOfMessage() {
        return restOfMessage;
    }

    /**
     * Returns the number of bytes consumed from the original input during request line parsing.
     * This includes the request line content plus the CRLF terminator.
     *
     * @return the number of bytes consumed
     */
    public int getBytesConsumed() {
        return bytesConsumed;
    }

}
