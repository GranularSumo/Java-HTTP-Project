package org.example.utils;

public class HttpUtils {
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
