package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import org.example.dataModel.Greet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class SimpleHttpServer {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/api/greet", new GreetHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server started on port 8000");
    }

    static class GreetHandler implements HttpHandler {
        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                handlePostRequest(exchange);
            } else if ("GET".equals(exchange.getRequestMethod())) {
                handleGetRequest(exchange);
            } else {
                exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
            }
        }

        private void handlePostRequest(HttpExchange exchange) throws IOException {
            InputStream requestBody = exchange.getRequestBody();
            Greet greetRequest = objectMapper.readValue(requestBody, Greet.class);

            // Create a response
            Greet greetResponse = new Greet("Hello, " + greetRequest.getMessage());

            String jsonResponse = objectMapper.writeValueAsString(greetResponse);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);

            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(jsonResponse.getBytes());
            responseBody.close();
        }

        private void handleGetRequest(HttpExchange exchange) throws IOException {
            // Create a response
            Greet greetResponse = new Greet("Hello, World!");

            String jsonResponse = objectMapper.writeValueAsString(greetResponse);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);

            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(jsonResponse.getBytes());
            responseBody.close();
        }
    }
}
