package org.example.server;

import org.example.request.Request;
import org.example.request.RequestParser;
import org.example.response.ResponseWriter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    private final Handler handler;
    private ProxyHandler proxyHandler;
    private volatile boolean running;
    private final int socketTimeout;

    public Server(int port, int socketTimeout) throws IOException {
        this.executorService = Executors.newCachedThreadPool();
        this.serverSocket = new ServerSocket(port);
        this.handler = new DefaultHandler();
        this.socketTimeout = socketTimeout;
        this.proxyHandler = new ProxyHandler();
    }

    public void start() {
        this.running = true;
        new Thread(this::listen).start();
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
            conn.setSoTimeout(socketTimeout);
            System.out.println("Connection accepted from: " + conn.getRemoteSocketAddress());
            Request request = RequestParser.requestFromReader(conn.getInputStream());
            System.out.println("Request parsed: " + request.getRequestLine().method() + " " + request.getRequestLine().requestTarget());
            ResponseWriter responseWriter = new ResponseWriter(conn.getOutputStream());

            if (request.getRequestLine().requestTarget().startsWith(proxyHandler.getProxyPath())) {
                this.proxyHandler.handle(responseWriter, request);
            }
            else if (Objects.equals(request.getRequestLine().requestTarget(), "/video")) {
                VideoHandler handler1 = new VideoHandler();
                handler1.handle(responseWriter, request);
            }
            else {
                this.handler.handle(responseWriter, request);
            }

            System.out.println("Response sent successfully");

        } catch (java.net.SocketTimeoutException e) {
            System.err.println("Connection timed out: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error handling connection: " + e.getMessage());
        }
    }

    public void close() {
        running = false;
        try { serverSocket.close(); } catch (IOException ignored) {}
        executorService.shutdown();
    }

    public void setProxy(String path, String url) {
        this.proxyHandler.setProxy(path, url);
    }
}
