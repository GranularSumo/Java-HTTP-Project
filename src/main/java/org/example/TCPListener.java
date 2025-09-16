package org.example;

import org.example.request.Request;
import org.example.request.RequestParser;

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
                        Request request = RequestParser.requestFromReader(client.getInputStream());

                        System.out.println("Request line:");
                        System.out.println("- Method: " + request.getRequestLine().method());
                        System.out.println("- Target: " + request.getRequestLine().requestTarget());
                        System.out.println("- Version: " + request.getRequestLine().httpVersion());
                    }
                } catch (IOException e) {
                    System.err.println("Error handling client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling server socket: " + e.getMessage());
        }
    }

}
