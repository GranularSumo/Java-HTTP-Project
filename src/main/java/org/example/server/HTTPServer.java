package org.example.server;

import java.io.IOException;

public class HTTPServer {

    public static void main(String[] args) throws IOException, InterruptedException {

        int port = 9001;
        Server server = new Server(port, 50000);
        server.start();
        server.setProxy("/jsonplaceholder/", "https://jsonplaceholder.typicode.com/");

        System.out.println("Server started on port " + port);
        System.out.println("Access the hosted content at: http://localhost:9001/");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.close();
            System.out.println("Server gracefully stopped");
        }));

        Thread.currentThread().join();
    }
}
