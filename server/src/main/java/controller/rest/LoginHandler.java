package controller.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controller.crud.PlayerControllerImpl;
import model.bean.Player;

import java.io.*;
import java.util.List;
import java.util.logging.Logger;

public class LoginHandler implements HttpHandler {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            handlePostRequest(exchange);
        }
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        List<String> contentTypeHeaderList = exchange.getRequestHeaders().get("Content-type");
        String contentType = contentTypeHeaderList.get(0);

        if (!exchange.getRequestHeaders().get("Content-type").get(0).equals("application/json")) {
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            return;
        }

        InputStreamReader iStreamReader = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader bReader = new BufferedReader(iStreamReader);
        String query = bReader.readLine();

        GsonBuilder gsonBuilder = new GsonBuilder();

        Gson gson = gsonBuilder.create();
        Player playerToLogin = gson.fromJson(query, Player.class);
        Player playerFromDB = PlayerControllerImpl.getInstance().checkCredentials(playerToLogin);

        if (playerFromDB == null) {
            exchange.sendResponseHeaders(412, query.length());
            exchange.close();
            return;
        }

        exchange.sendResponseHeaders(200, query.length());
        Player responsePlayer = new Player(playerFromDB.getId(), playerFromDB.getUsername());
        logger.info("successful login by: " + playerFromDB.getUsername());
        String response = gson.toJson(responsePlayer);
        OutputStream oStream = exchange.getResponseBody();
        oStream.write(response.getBytes());
        oStream.close();
    }
}
