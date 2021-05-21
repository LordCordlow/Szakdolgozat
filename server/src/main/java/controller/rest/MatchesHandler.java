package controller.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controller.crud.MatchControllerImpl;
import model.bean.Match;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class MatchesHandler implements HttpHandler {
    private static final GsonBuilder gsonBuilder = new GsonBuilder();
    private static final Gson gson = gsonBuilder.create();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            handleGetRequest(exchange);
        } else if ("POST".equals(exchange.getRequestMethod())) {
            handlePostRequest(exchange);
        }
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {
        String idStr = RestServer.getIdFromParams(exchange.getRequestURI().getQuery());
        if (idStr == null) {
            exchange.sendResponseHeaders(404, 0);
            exchange.close();
            return;
        }
        int playerId = Integer.parseInt(idStr);

        String response = gson.toJson(MatchControllerImpl.getInstance().getVMatchesByPlayerId(playerId));
        exchange.sendResponseHeaders(200, response.length());
        OutputStream oStream = exchange.getResponseBody();
        oStream.write(response.getBytes());
        oStream.close();
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestHeaders().get("Content-type").get(0).equals("application/json")) {
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            return;
        }

        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        Match match = gson.fromJson(query, Match.class);

        if (match != null && MatchControllerImpl.getInstance().addMatch(match)) {
            exchange.sendResponseHeaders(200, query.length());
            exchange.close();
            return;
        }
        exchange.sendResponseHeaders(500, 0);
        exchange.close();
    }
}
