package controller.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controller.system.SystemController;
import controller.websocket.ServerEndpoint;
import model.bean.Lobby;
import model.bean.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.logging.Logger;

public class LobbyHandler implements HttpHandler {
    private final GsonBuilder gsonBuilder = new GsonBuilder();
    private final Gson gson = gsonBuilder.create();
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            if (exchange.getRequestURI().getQuery() != null) {
                handleLobbyUpdateGetRequest(exchange);
            } else {
                handleBasicGetRequest(exchange);
            }
        } else if ("POST".equals(exchange.getRequestMethod())) {
            if (exchange.getRequestURI().getQuery() != null) {
                handleJoinPostRequest(exchange);
            } else {
                handleCreatePostRequest(exchange);
            }
        } else if ("DELETE".equals(exchange.getRequestMethod())) {
            handleDeleteRequest(exchange);
        }
    }

    private void handleBasicGetRequest(HttpExchange exchange) throws IOException {
        String response = gson.toJson(SystemController.getInstance().getOpenLobbies());
        exchange.sendResponseHeaders(200, response.length());
        OutputStream oStream = exchange.getResponseBody();
        oStream.write(response.getBytes());
        oStream.close();
    }

    private void handleLobbyUpdateGetRequest(HttpExchange exchange) throws IOException {
        String lobbyId = RestServer.getIdFromParams(exchange.getRequestURI().getQuery());
        Lobby lobby = SystemController.getInstance().getLobbyById(lobbyId);
        if (lobby == null) {
            exchange.sendResponseHeaders(404, 0);
            exchange.close();
        }
        String response = gson.toJson(lobby, Lobby.class);
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void handleCreatePostRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestHeaders().get("Content-type").get(0).equals("application/json")) {
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            return;
        }

        InputStreamReader iStreamReader = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader bReader = new BufferedReader(iStreamReader);
        String query = bReader.readLine();
        Player player = gson.fromJson(query, Player.class);

        String response = null;
        if (SystemController.getInstance().isOnline(player.getId())) {
            response = SystemController.getInstance().createLobby(player);
            ServerEndpoint.createSocketLobby(response, player.getId());
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        exchange.sendResponseHeaders(412, 0);
        exchange.close();
    }

    private void handleJoinPostRequest(HttpExchange exchange) throws IOException {
        String lobbyId = RestServer.getIdFromParams(exchange.getRequestURI().getQuery());
        // query not given lobbyId or bad content type
        if (lobbyId == null || !exchange.getRequestHeaders().get("Content-type").get(0).equals("application/json")) {
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            return;
        }

        InputStreamReader iStreamReader = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader bReader = new BufferedReader(iStreamReader);
        String message = bReader.readLine();
        Player playerToJoin = gson.fromJson(message, Player.class);

        // not online or already at lobby
        if (!SystemController.getInstance().isOnline(playerToJoin.getId()) ||
                SystemController.getInstance().isInLobby(playerToJoin.getId())
        ) {
            exchange.sendResponseHeaders(412, 0);
            exchange.close();
            return;
        }

        // lobby on full
        if (!SystemController.getInstance().isGuestEmpty(lobbyId)) {
            exchange.sendResponseHeaders(409, 0);
            exchange.close();
            return;
        }

        // success
        Lobby responseLobby = SystemController.getInstance().joinToLobby(playerToJoin, lobbyId);
        if (responseLobby != null) {
            String response = gson.toJson(responseLobby, Lobby.class);
            ServerEndpoint.addPlayerToSocketLobby(responseLobby.getId(), playerToJoin.getId());
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
            exchange.close();
        }
    }

    private void handleDeleteRequest(HttpExchange exchange) throws IOException {
        String lobbyId = RestServer.getIdFromParams(exchange.getRequestURI().getQuery());
        // query not given lobbyId or bad content type
        if (lobbyId == null || !exchange.getRequestHeaders().get("Content-type").get(0).equals("application/json")) {
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            return;
        }

        InputStreamReader iStreamReader = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader bReader = new BufferedReader(iStreamReader);
        String message = bReader.readLine();
        Player sender = gson.fromJson(message, Player.class);

        if (SystemController.getInstance().isOnline(sender.getId())) {
            Lobby lobbyToLeave = SystemController.getInstance().getLobbyById(lobbyId);
            if (lobbyToLeave != null) {
                // guest leaving
                Player guest = lobbyToLeave.getGuest();
                if (guest != null && lobbyToLeave.getGuest().getId() == sender.getId()) {
                    logger.info(guest.getUsername() + " left the lobby hosted by: " + lobbyToLeave.getHost().getUsername());
                    try {
                        ServerEndpoint.removePlayerFromSocketLobby(lobbyToLeave.getId(), guest.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    lobbyToLeave.setGuest(null);
                    lobbyToLeave.setGuestSocketId(null);
                    exchange.sendResponseHeaders(200, message.length());
                    exchange.close();
                } else if (lobbyToLeave.getHost().getId() == sender.getId()) {
                    // host leaving
                    if (SystemController.getInstance().deleteLobby(lobbyToLeave)) {
                        ServerEndpoint.deleteSocketLobby(lobbyToLeave.getId());
                        exchange.sendResponseHeaders(200, message.length());
                        exchange.close();
                        return;
                    }
                    exchange.sendResponseHeaders(412, 0);
                    exchange.close();
                } else {
                    exchange.sendResponseHeaders(409, 0);
                    exchange.close();
                }
            }
        } else {
            exchange.sendResponseHeaders(200, 0);
            exchange.close();
        }
    }
}
