package org.example.server;

import org.example.request.Request;

import java.io.IOException;
import java.io.OutputStream;

public class HTTPServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        Handler handler = (output, request) -> {
            String path = request.getRequestLine().requestTarget();

            if (path.equals("/yourproblem")) {
                return new HandleError(400, "Your problem is not my problem\n");
            }

            if (path.equals("/myproblem")) {
                return new HandleError(500, "Woopsie, my bad\n");
            }

            try {
                output.write("All good, frfr\n".getBytes());
            } catch (IOException e) {
                return new HandleError(500, "Failed to write");
            }
            return null;
        };

        int port = 9001;
        Server server = new Server(port, handler);
        server.start();

        System.out.println("Server started on port " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.close();
            System.out.println("Server gracefully stopped");
        }));

        Thread.currentThread().join();
    }
}
