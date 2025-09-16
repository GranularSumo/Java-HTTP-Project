package org.example.chunkReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * A custom InputStream implementation that reads data in controlled chunks.
 */
public class chunkReader extends InputStream {

    private final byte[] data;
    private final int numBytesPerRead;
    private int position;

    /**
     * Creates a new chunkReader from a string.
     *
     * @param data the string data to read from
     * @param numBytesPerRead maximum bytes to read per operation
     */
    public chunkReader(String data, int numBytesPerRead) {
        this.data = data.getBytes();
        this.numBytesPerRead = numBytesPerRead;
        this.position = 0;
    }

    /**
     * Reads a single byte from the data.
     *
     * @return the next byte as an unsigned int (0-255), or -1 if end of stream
     * @throws IOException if an I/O error occurs
     */
    @Override
    public int read() throws IOException {
        if (position >= data.length) {
            return -1;
        }
        return Byte.toUnsignedInt(data[position++]);
    }

    /**
     * Reads up to numBytesPerRead bytes into the provided buffer.
     *
     * @param buffer the buffer to read data into
     * @return the number of bytes actually read, or -1 if end of stream
     * @throws IOException if an I/O error occurs
     */
    @Override
    public int read(byte[] buffer) throws IOException {

        if (position >= data.length) {
            return -1;
        }

        int endIndex = position + numBytesPerRead;

        if (endIndex > data.length) {
            endIndex = data.length;
        }

        int bytesToCopy = endIndex - position;

        if (bytesToCopy > buffer.length) {
            bytesToCopy = buffer.length;
        }

        System.arraycopy(data, position, buffer, 0, bytesToCopy);

        position += bytesToCopy;

        return bytesToCopy;
    }
}
