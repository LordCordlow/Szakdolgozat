package controller.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controller.system.SystemController;
import controller.websocket.ServerEndpoint;
import model.bean.Lobby;
import model.bean.Player;
import model.bean.TLobby;
import org.glassfish.tyrus.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.logging.Logger;

public class TLobbyHandler implements HttpHandler {
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
        String response = gson.toJson(SystemController.getInstance().getOpenTandems());
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void handleLobbyUpdateGetRequest(HttpExchange exchange) throws IOException {
        String lobbyId = RestServer.getIdFromParams(exchange.getRequestURI().getQuery());
        TLobby tandem = SystemController.getInstance().getTandemById(lobbyId);
        if (tandem == null) {
            exchange.sendResponseHeaders(404, 0);
            exchange.close();
        }
        String response = gson.toJson(tandem, TLobby.class);
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

        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        Player player = gson.fromJson(query, Player.class);

        String response = null;

        if (SystemController.getInstance().isOnline(player.getId())) {
            response = SystemController.getInstance().createTandem(player);
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
        if (lobbyId == null || !exchange.getRequestHeaders().get("Content-type").get(0).equals("application/json")) {
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            return;
        }

        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody());
        BufferedReader br = new BufferedReader(isr);
        String message = br.readLine();
        Player playerToJoin = gson.fromJson(message, Player.class);

        if (!SystemController.getInstance().isOnline(playerToJoin.getId()) ||
            SystemController.getInstance().isInLobby(playerToJoin.getId()) ||
            SystemController.getInstance().isInTandem(playerToJoin.getId())
        ) {
            exchange.sendResponseHeaders(412, 0);
            exchange.close();
            return;
        }

        if (!SystemController.getInstance().isTandemOpen(lobbyId)) {
            exchange.sendResponseHeaders(409, 0);
            exchange.close();
            return;
        }

        TLobby responseTLobby = SystemController.getInstance().joinToTandem(playerToJoin, lobbyId);
        if (responseTLobby != null) {
            String response = gson.toJson(responseTLobby, TLobby.class);
            ServerEndpoint.addPlayerToSocketLobby(responseTLobby.getId(), playerToJoin.getId());
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
            exchange.close();
        }
    }

    private void handleDeleteRequest(HttpExchange exchange) throws IOException {
        String id = RestServer.getIdFromParams(exchange.getRequestURI().getQuery());
        if (id == null || !exchange.getRequestHeaders().get("Content-type").get(0).equals("application/json")) {
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            return;
        }

        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String message = br.readLine();
        Player sender = gson.fromJson(message, Player.class);

        if (SystemController.getInstance().isOnline(sender.getId())) {
            TLobby tandemToLeave = SystemController.getInstance().getTandemById(id);
            System.out.println("tandemToLeave: " + tandemToLeave);
            if (tandemToLeave != null) {
                // one of the guests is leaving
                if (tandemToLeave.isInLobby(sender.getId()) && tandemToLeave.getHost().getId() != sender.getId()) {
                    System.out.println("guest leaving");
                    if (tandemToLeave.removeFromTeam(sender)) {
                        logger.info(sender.getUsername() + " left the tandem lobby hosted by: " + tandemToLeave.getHost());
                        ServerEndpoint.removePlayerFromSocketLobby(tandemToLeave.getId(), sender.getId());
                        exchange.sendResponseHeaders(200, message.length());
                        exchange.close();
                    }
                } else if (tandemToLeave.getHost().getId() == sender.getId()) {
                    // host is leaving
                    System.out.println("host leaving");
                    if (SystemController.getInstance().deleteTandem(tandemToLeave)) {
                        logger.info(sender.getUsername() + " deleted the tandem lobby");
                        ServerEndpoint.deleteSocketLobby(tandemToLeave.getId());
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
