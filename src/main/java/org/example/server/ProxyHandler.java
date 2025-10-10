package org.example.server;

import org.example.request.Request;
import org.example.response.ResponseWriter;
import org.example.response.StatusCode;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class ProxyHandler implements Handler{

    private String proxyPath;
    private String proxyUrl;
    private HttpClient client;

    public ProxyHandler() {
        this.client = HttpClient.newHttpClient();
    }

    public void setProxy(String path, String url) {
        this.proxyPath = path;
        this.proxyUrl = url;
    }

    public String getProxyPath() {
        return this.proxyPath;
    }

    public URI getProxyURI(Request request) {
        String[] path = request.getRequestLine().requestTarget().split(proxyPath);
        return URI.create(this.proxyUrl + path[1]);
    }

    @Override
    public void handle(ResponseWriter responseWriter, Request request) throws IOException {
        HttpRequest request1 = HttpRequest.newBuilder().uri(getProxyURI(request)).GET().build();

        try {
            HttpResponse<InputStream> response = client.send(request1, HttpResponse.BodyHandlers.ofInputStream());
            InputStream stream = response.body();
            byte[] buffer = new byte[32];
            int numberOfBytesRead;

            responseWriter.writeChunkedResponseHeaders(StatusCode.convertIntToStatusCode(response.statusCode()));
            byte[] responseBody = new byte[0];

            while ((numberOfBytesRead = stream.read(buffer)) > 0) {
                responseWriter.writeChunkedBody(Arrays.copyOfRange(buffer, 0, numberOfBytesRead));
                byte[] newArray = new byte[responseBody.length + Arrays.copyOfRange(buffer, 0, numberOfBytesRead).length];
                System.arraycopy(responseBody, 0, newArray, 0, responseBody.length);
                System.arraycopy(buffer, 0, newArray, responseBody.length, numberOfBytesRead);
                responseBody = newArray;
            }
            responseWriter.writeChunkedBodyDone();
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            responseWriter.writeResponseTrailers(md.digest(responseBody), String.valueOf(responseBody.length));


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }


    }
}
