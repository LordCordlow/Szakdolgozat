package controller.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controller.crud.PlayerControllerImpl;
import model.bean.Player;
import org.mindrot.jbcrypt.BCrypt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.logging.Logger;

public class RegisterHandler implements HttpHandler {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            handlePostRequest(exchange);
        }
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        // checking the content type
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
        Player playerToRegister = gson.fromJson(query, Player.class);

        if (PlayerControllerImpl.getInstance().getPlayerByUsername(playerToRegister.getUsername()) != null) {
            exchange.sendResponseHeaders(409, query.length());
            exchange.close();
            return;
        }

        Player playerToDB = new Player(playerToRegister);
        playerToDB.setPassword(BCrypt.hashpw(playerToDB.getPassword(), BCrypt.gensalt(12)));

        if (PlayerControllerImpl.getInstance().addPlayer(playerToDB)) {
            Player responsePlayer = PlayerControllerImpl.getInstance().getPlayerByUsername(playerToDB.getUsername());
            responsePlayer.setPassword("");
            String response = gson.toJson(responsePlayer);
            logger.info("new registered player: " + responsePlayer.getUsername());
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream oStream = exchange.getResponseBody();
            oStream.write(response.getBytes());
            oStream.close();
        } else {
            exchange.sendResponseHeaders(406, query.length());
        }
    }
}
