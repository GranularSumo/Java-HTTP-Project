package org.example.request;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;

public class RequestParser {

    /**
     * Parses an HTTP request from an InputStream using incremental buffering.
     * This method reads data in chunks, accumulating it in a growing buffer until
     * a complete request line is found. Uses a stateful Request object to track
     * parsing progress and handle partial data efficiently.
     *
     * @param inputStream the input stream containing the HTTP request data
     * @return a Request object containing the parsed request line
     * @throws IOException if the stream ends before a complete request is found,
     *                     or if the request data is malformed
     */
    public static Request requestFromReader(InputStream inputStream) throws IOException {
        Request request = new Request();

        byte[] buffer = new byte[8];
        int totalBytesInBuffer = 0;

        while (request.getStatus() != Request.Status.DONE) {
            int bytesRead = inputStream.read(buffer, totalBytesInBuffer, buffer.length - totalBytesInBuffer);

            if (bytesRead == -1) {
                throw new IOException("Stream ended before complete request");
            }

            totalBytesInBuffer += bytesRead;

            byte[] parseData = Arrays.copyOfRange(buffer, 0, totalBytesInBuffer);

            int bytesParsed = request.parse(parseData);

            if (bytesParsed > 0) {
                System.arraycopy(buffer, bytesParsed, buffer, 0, totalBytesInBuffer - bytesParsed);
                totalBytesInBuffer -= bytesParsed;
            }

            if (totalBytesInBuffer >= buffer.length - 1) {
                buffer = Arrays.copyOf(buffer, buffer.length * 2);
            }

        }

        return request;

    }

    /**
     * Parses HTTP request line data from a byte array and returns parsing results.
     * Searches for a complete request line ending with \\r\\n, validates the HTTP format,
     * and returns both the parsed RequestLine and any remaining unparsed data.
     *
     * @param rawData the byte array containing HTTP request data to parse
     * @return a RequestLineParseResult containing the parsed RequestLine (or null if incomplete),
     * remaining unparsed data, and the number of bytes consumed
     * @throws IOException if the request line format is invalid, contains an unsupported method,
     *                     invalid request target, or unsupported HTTP version
     */
    public static RequestLineParseResult parseRequestLine(byte[] rawData) throws IOException {

        if (rawData == null || rawData.length < 2) {
            return new RequestLineParseResult(null, null, 0);
        }

        int crlfindex = findCRLF(rawData);

        if (crlfindex == -1) {
            return new RequestLineParseResult(null, null, 0);
        }

        int bytesConsumed = crlfindex + 2;

        String requestLine = new String(rawData, 0, crlfindex, StandardCharsets.UTF_8);
        byte[] restOfMessage = new byte[rawData.length - bytesConsumed];
        System.arraycopy(rawData, bytesConsumed, restOfMessage, 0, rawData.length - bytesConsumed);

        String[] parts = requestLine.split(" ");

        if (parts.length != 3) {
            throw new IOException("Invalid request line format");
        }

        String method = parts[0];
        String requestTarget = parts[1];

        if (!parts[2].startsWith("HTTP/")) {
            throw new IOException("Invalid HTTP version format: " + parts[2]);
        }

        String httpVersion = parts[2].replace("HTTP/", "");

        Set<String> validMethods = Set.of("GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS");

        if (!validMethods.contains(method)) {
            throw new IOException("Invalid HTTP method: " + parts[0]);
        }

        if (!requestTarget.startsWith("/") && !(method.equals("OPTIONS") && parts[1].startsWith("*"))) {
            throw new IOException("Invalid request target: " + parts[1]);
        }

        if (!httpVersion.equals("1.1")) {
            throw new IOException("Unsupported HTTP version");
        }

        return new RequestLineParseResult(new RequestLine(method, requestTarget, httpVersion), restOfMessage, bytesConsumed);
    }

    /**
     * Searches for the first occurrence of CRLF (\r\n) sequence in a byte array.
     * This is used to identify the end of an HTTP request line.
     *
     * @param data the byte array to search
     * @return the index of the \r character if CRLF is found, -1 otherwise
     */
    public static int findCRLF(byte[] data) {
        for (int i = 0; i < data.length - 1; i++) {
            if (data[i] == '\r' && data[i + 1] == '\n') {
                return i;
            }
        }
        return -1;
    }

    /**
     * Searches for the first occurrence of CRLF (\r\n) sequence in a byte array
     * starting from the specified position.
     * This overloaded version allows parsing to continue from a specific offset,
     * useful for parsing multiple lines or continuing from a previous position.
     *
     * @param data     the byte array to search
     * @param position the starting position in the byte array to begin searching
     * @return the index of the \r character if CRLF is found, -1 otherwise
     */
    public static int findCRLF(byte[] data, int position) {
        for (int i = position; i < data.length - 1; i++) {
            if (data[i] == '\r' && data[i + 1] == '\n') {
                return i;
            }
        }
        return -1;
    }

}
