package org.example;

import org.example.request.Request;
import org.example.request.RequestParser;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TCP server that listens for client connections and processes HTTP requests.
 * Parses incoming HTTP request lines and headers, then outputs the parsed information.
 * Uses RequestParser for incremental parsing of HTTP request data.
 */
public class TCPListener {

    private static final String EOF = "__EOF__";

    /**
     * Main server loop that accepts client connections on port 9001.
     * For each connection, parses the HTTP request and prints the request line
     * (method, target, version) and all headers to standard output.
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
                        System.out.println("Headers:");
                        for (String key : request.getHeaders().getHeaderMap().keySet()) {
                            System.out.println("- " + key + ": " + request.getHeaders().getValue(key));
                        }
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
