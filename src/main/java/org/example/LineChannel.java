package org.example;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LineChannel {

    private static final String EOF = "__EOF__";

    public static void main(String[] args) {

        BlockingQueue<String> channel = null;

        try {
            channel = getLinesChannel(new FileInputStream("src/messages.txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        for (;;) {
            String line = null;
            try {
                line = channel.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            if (EOF.equals(line)) break;
            System.out.printf("read: %s%n", line);
        }
    }

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

                if (currentLine.length() > 0) {
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
