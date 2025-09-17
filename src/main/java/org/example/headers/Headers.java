package org.example.headers;

import org.example.request.RequestParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents HTTP headers and provides functionality to parse HTTP header data.
 * This class can incrementally parse header data from byte arrays, maintaining
 * state between parsing calls to handle partial data efficiently.
 *
 * Headers are parsed according to HTTP/1.1 specification, where each header line
 * consists of a field name followed by a colon, optional whitespace, field value,
 * and terminated by CRLF. Multiple headers with the same name are combined with
 * comma separation.
 */
public class Headers {
    private Map<String, String> headerMap;
    private boolean isDone;

    /**
     * Creates a new Headers instance initialized with an empty header map
     * and ready to begin parsing.
     */
    public Headers() {
        this.headerMap = new HashMap<>();
        this.isDone = false;
    }

    /**
     * Parses HTTP header data from the provided byte array.
     * Continues parsing until all headers are consumed or incomplete data is encountered.
     * Parsing is complete when an empty line (CRLF only) is found, indicating the end
     * of the header section.
     *
     * @param data the byte array containing HTTP header data to parse
     * @return the total number of bytes consumed from the input data, or -1 if more data is needed
     * @throws IOException if the header data is malformed or contains invalid syntax
     */
    public int parse(byte[] data) throws IOException {
        int totalBytesParsed = 0;
        while (!this.isDone) {
            int result = parseSingle(data, totalBytesParsed);

            if (result == -1) {
                break;
            }

            totalBytesParsed += result;
        }
        return totalBytesParsed;
    }

    /**
     * Parses a single HTTP header line from the provided byte array starting at the given position.
     * Handles header field validation, duplicate header combination, and end-of-headers detection.
     *
     * @param data the byte array containing HTTP header data
     * @param position the starting position in the byte array to begin parsing
     * @return the number of bytes consumed for this header line, or -1 if incomplete data
     * @throws IOException if the header format is invalid, contains illegal characters,
     *                     or has improper spacing around the colon separator
     */
    public int parseSingle(byte[] data, int position) throws IOException {
        int crlfIndex = RequestParser.findCRLF(data, position);

        if (crlfIndex == -1) {
            return -1;
        }

        if (crlfIndex == position) {
            this.isDone = true;
            return 2;
        }

        int separatorIndex = position;

        for (int i = position; i < data.length - 1; i++) {
            if (data[i] == ':' && data[i - 1] == ' ') {
                throw new IOException("Invalid spacing in headers at byte index: " + i);
            } else if (data[i] == ':') {
                separatorIndex = i;
                break;
            }
        }

        if (separatorIndex == position) {
            return -1;
        }

        String key = new String(data, position, separatorIndex - position, StandardCharsets.UTF_8);
        key =  key.trim().toLowerCase();
        String value = new String(data, separatorIndex + 1, crlfIndex - (separatorIndex + 1), StandardCharsets.UTF_8);
        value = value.trim();

        for (char c : key.toCharArray()) {
            if (!isValidHeaderChar(c)) {
                throw new IOException("Invalid character in header name: " + c);
            }
        }

        if (headerMap.containsKey(key)) {
            String combinedValue = headerMap.get(key) + ", " + value;
            headerMap.put(key, combinedValue);
        } else {
            headerMap.put(key, value);
        }

        return crlfIndex + 2;
    }

    /**
     * Returns the complete header map containing all parsed headers.
     * Header names are stored in lowercase, and duplicate headers are combined with comma separation.
     *
     * @return a Map containing header names as keys and header values as values
     */
    public Map<String, String> getHeaderMap() {
        return this.headerMap;
    }

    /**
     * Retrieves the value for a specific header by name.
     * Header name lookup is case-insensitive as names are stored in lowercase.
     *
     * @param key the header name to look up
     * @return the header value, or null if the header is not present
     */
    public String getValue(String key) {
        return this.headerMap.get(key);
    }

    /**
     * Checks if header parsing is complete.
     * Parsing is considered done when an empty line (CRLF only) is encountered,
     * indicating the end of the HTTP header section.
     *
     * @return true if all headers have been parsed, false otherwise
     */
    public boolean isDone() {
        return this.isDone;
    }


    private boolean isValidHeaderChar(char c) {
        String validCharacters = "!#$%&'*+-.^_`|~";
        if (Character.isLetterOrDigit(c)) return true;
        else return validCharacters.contains(String.valueOf(c));
    }

}
