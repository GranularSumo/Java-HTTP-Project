package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * TCP server that listens for client connections and processes incoming data line by line.
 */
public class TCPListener {

    private static final String EOF = "__EOF__";

    /**
     * Main server loop that accepts client connections on port 9001.
     * For each client, creates a line channel and processes incoming lines.
     *
     * @param args command line arguments (unused)
     * @throws IOException if the server socket cannot be created or bound
     */
    public static void main(String[] args) throws IOException {

        try (ServerSocket server = new ServerSocket(9001)) {
            while (true) {
                try {
                    Socket client = server.accept();
                    try (client) {
                        BlockingQueue<String> channel = getLinesChannel(client.getInputStream());

                        for (; ; ) {
                            String line = channel.take();
                            if (EOF.equals(line)) break;
                            System.out.printf("read: %s%n", line);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error handling client connection: " + e.getMessage());
                } catch (InterruptedException e) {
                    System.err.println("Thread interrupted while reading from client");
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling server socket: " + e.getMessage());
        }
    }

    /**
     * Creates a blocking queue that reads lines from an InputStream.
     * Spawns a producer thread that reads the stream in small chunks, handles line splitting,
     * and puts complete lines into the queue. The queue is terminated with an EOF marker.
     *
     * @param stream the input stream to read from
     * @return a BlockingQueue that will contain lines read from the stream
     */
    public static BlockingQueue<String> getLinesChannel(InputStream stream) {
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();

        Thread t = new Thread(() -> {
            byte[] buffer = new byte[8];
            StringBuilder currentLine = new StringBuilder();

            try {
                int bytesRead;
                while ((bytesRead = stream.read(buffer)) != -1) {
                    String chunk = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
                    chunk = chunk.replace("\r", "");
                    String[] chunks = chunk.split("\\n", -1);

                    for (int i = 0; i < chunks.length - 1; i++) {
                        currentLine.append(chunks[i]);
                        queue.put(currentLine.toString());
                        currentLine.setLength(0);
                    }

                    currentLine.append(chunks[chunks.length - 1]);
                }

                if (!currentLine.isEmpty()) {
                    queue.put(currentLine.toString());
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                try { stream.close(); } catch (IOException ignored) {}
                try { queue.put(EOF); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
            }
        }, "line-producer");

        t.start();
        return queue;
    }

}
