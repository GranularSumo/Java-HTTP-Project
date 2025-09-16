package org.example.chunkReader;

import java.io.IOException;
import java.io.InputStream;

public class chunkReader extends InputStream {
    private final byte[] data;
    private final int numBytesPerRead;
    private int position;

    public chunkReader(String data, int numBytesPerRead) {
        this.data = data.getBytes();
        this.numBytesPerRead = numBytesPerRead;
        this.position = 0;
    }

    public chunkReader(byte[] data, int numBytesPerRead) {
        this.data = data;
        this.numBytesPerRead = numBytesPerRead;
        this.position = 0;
    }

    @Override
    public int read() throws IOException {
        if (position >= data.length) {
            return -1;
        }
        return Byte.toUnsignedInt(data[position++]);
    }

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
