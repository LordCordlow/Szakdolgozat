package controller.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controller.system.SystemController;

import java.io.IOException;
import java.io.OutputStream;

public class PlayersHandler implements HttpHandler {
    private final GsonBuilder gsonBuilder = new GsonBuilder();
    private final Gson gson = gsonBuilder.create();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            handleGetRequest(exchange);
        }
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {
        String response = gson.toJson(SystemController.getInstance().getOnlinePlayers());
        exchange.sendResponseHeaders(200, response.length());
        OutputStream oStream = exchange.getResponseBody();
        oStream.write(response.getBytes());
        oStream.close();
    }
}
