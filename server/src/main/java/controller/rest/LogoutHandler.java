package controller.rest;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controller.system.SystemController;
import controller.websocket.ServerEndpoint;
import model.bean.Lobby;
import model.bean.Player;
import model.bean.TLobby;
import org.glassfish.tyrus.server.Server;

import java.io.IOException;

public class LogoutHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("DELETE".equals(exchange.getRequestMethod())) {
            handleDeleteRequest(exchange);
        }
    }

    private void handleDeleteRequest(HttpExchange exchange) throws IOException {
        String idFromParams = RestServer.getIdFromParams(exchange.getRequestURI().getQuery());
        boolean logout = false;
        boolean removeFromLobby = false;
        if (idFromParams != null) {
            int playerId = Integer.parseInt(idFromParams);
            // remove from online players
            if (SystemController.getInstance().isOnline(playerId)) {
                logout = SystemController.getInstance().logoutPlayer(playerId);
            }

            // remove from lobby or delete the lobby
            if (SystemController.getInstance().isInLobby(playerId)) {
                for (Lobby l : SystemController.getInstance().getLobbies()) {
                    // check player guest or host
                    if (l.getGuest() != null && l.getGuest().getId() == playerId) {
                        l.setGuest(null);
                        l.setGuestSocketId(null);
                        ServerEndpoint.removePlayerFromSocketLobby(l.getId(), playerId);
                        break;
                    } else if (l.getHost() != null && l.getHost().getId() == playerId) {
                        ServerEndpoint.deleteSocketLobby(l.getId());
                        removeFromLobby = SystemController.getInstance().deleteLobby(l);
                        break;
                    }
                }
            }

            if (SystemController.getInstance().isInTandem(playerId)) {
                for (TLobby t : SystemController.getInstance().getTandems()) {
                    if (t.isInLobby(playerId) && t.getHost().getId() != playerId) {
                        Player playerToRemove = new Player(playerId, "");
                        t.removeFromTeam(playerToRemove);
                        ServerEndpoint.removePlayerFromSocketLobby(t.getId(), playerId);
                        break;
                    } else if (t.isInLobby(playerId) && t.getHost().getId() == playerId) {
                        SystemController.getInstance().deleteTandem(t);
                        ServerEndpoint.deleteSocketLobby(t.getId());
                        break;
                    }
                }
            }
            int statusCode = logout ? 200 : 400;
            statusCode += (removeFromLobby ? 2 : 0);

            exchange.sendResponseHeaders(statusCode, 0);
            exchange.close();
        }
    }
}
