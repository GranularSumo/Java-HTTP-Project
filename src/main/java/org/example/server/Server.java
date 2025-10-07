package org.example.server;

import org.example.request.Request;
import org.example.request.RequestParser;
import org.example.response.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    private final Handler handler;
    private volatile boolean running;

    public Server(int port, Handler handler) throws IOException {
        this.executorService = Executors.newCachedThreadPool();
        this.serverSocket = new ServerSocket(port);
        this.handler = handler;

    }

    public void start() {
        new Thread(this::listen).start();
        this.running = true;
    }

    private void listen() {
        try {
            while (running) {
                Socket socket = this.serverSocket.accept();
                executorService.submit(() -> handle(socket));
            }
        } catch (IOException e) {
            if (running) throw new RuntimeException(e);
        }
    }

    private void handle(Socket conn) {
        try (conn) {
            Request request = RequestParser.requestFromReader(conn.getInputStream());

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            HandleError error = this.handler.handle(buffer, request);

            if (error != null) {
                Response response = new Response();
                response.setStatusCode(response.convertIntToStatusCode(error.getStatusCode()));
                response.setBody(error.getMessage());
                response.writeTo(conn.getOutputStream());
            } else {
                Response response = new Response();
                response.setBody(buffer.toString());
                response.writeTo(conn.getOutputStream());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        running = false;
        try { serverSocket.close(); } catch (IOException ignored) {}
        executorService.shutdown();
    }
}
