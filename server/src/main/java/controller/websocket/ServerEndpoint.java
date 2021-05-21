package controller.websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import controller.system.SystemController;
import model.bean.Player;
import model.bean.SocketLobby;
import model.bean.SocketMessage;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@javax.websocket.server.ServerEndpoint(value = "/game/{user}")
public class ServerEndpoint {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final GsonBuilder gsonBuilder = new GsonBuilder();
    private final Gson gson = gsonBuilder.create();
    private static List<SocketLobby> socketLobbies = new ArrayList<>();

    public static void createSocketLobby(String lobbyId, int hostId) {
        socketLobbies.add(new SocketLobby(lobbyId, hostId));
        printSocketLobbies();
    }

    public static void deleteSocketLobby(String lobbyId) {
        SocketLobby res = null;
        for (SocketLobby l : socketLobbies) {
            if (l.getLobbyId().equals(lobbyId)) {
                res = l;
            }
        }
        socketLobbies.remove(res);
        printSocketLobbies();
    }

    public static void addPlayerToSocketLobby(String lobbyId, int playerId) {
        for (SocketLobby l : socketLobbies) {
            if (l.getLobbyId().equals(lobbyId)) {
                l.addPlayer(playerId);
            }
        }
        printSocketLobbies();
    }

    public static void removePlayerFromSocketLobby(String lobbyId, int playerId) {
        for (SocketLobby l : socketLobbies) {
            if (l.getLobbyId().equals(lobbyId)) {
                l.removePlayer(playerId);
            }
        }
        printSocketLobbies();
    }

    public static void assignReady(String lobbyId, int playerId) {
        for (SocketLobby l : socketLobbies) {
            if (l.getLobbyId().equals(lobbyId)) {
                l.assignReady(playerId);
            }
        }
    }

    public static void printSocketLobbies() {
        System.out.println("Socket lobbies incoming...");
        for (SocketLobby l : socketLobbies) {
            System.out.println(l);
        }
    }

    private void setMatchDuration(String lobbyId, int matchDuration) {
        for (SocketLobby sl : socketLobbies) {
            if (sl.getLobbyId().equals(lobbyId)) {
                sl.setMatchDuration(matchDuration);
            }
        }
    }

    private String getLobbyIdPlayerId(int playerId) {
        for (SocketLobby l : socketLobbies) {
            if (l.isPlayerInLobby(playerId)) {
                return l.getLobbyId();
            }
        }
        return null;
    }

    private SocketLobby getLobbyByPlayerId(int playerId) {
        for (SocketLobby sl : socketLobbies) {
            if (sl.isPlayerInLobby(playerId)) {
                return sl;
            }
        }
        return null;
    }

    private void broadcast(Session session, String message) throws IOException {
        for (Session s : session.getOpenSessions()) {
            s.getBasicRemote().sendText(message);
        }
    }

    private void checkTandemStart(Session session, int playerId) throws IOException {
        String lobbyId = getLobbyIdPlayerId(playerId);
        for (SocketLobby sl : socketLobbies) {
            if (sl.getLobbyId().equals(lobbyId) && sl.isAllReady()) {
                String message = "tStart/" + sl.getLobbyId() + "/" + sl.getMatchDuration();
                broadcast(session, message);
                break;
            }
        }
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("user") String user) {
        logger.info("Connected on socket: " + session.getId());
    }

    @OnMessage
    public String onMessage(String message, Session session) throws IOException {
        SocketMessage incoming = gson.fromJson(message, SocketMessage.class);
        if (incoming.getMessageType().equals("start")) {
            SystemController.getInstance().addOnlinePlayer(incoming.getSocketId(), incoming.getPlayer());
            SocketMessage outgoing = new SocketMessage(incoming.getSocketId(), incoming.getPlayer(), "ok", null);
            return gson.toJson(outgoing);
        }

        if (incoming.getMessageType().startsWith("ready")) {
            String[] parts = incoming.getMessageType().split("-");
            int playerId = Integer.parseInt(parts[1]);
            int duration = 0;
            if (parts.length > 2) {
                duration = Integer.parseInt(parts[2]);
            }
            String lobbyId = this.getLobbyIdPlayerId(playerId);
            if (lobbyId != null) {
                assignReady(lobbyId, playerId);
                String response = "gotReady-" + playerId + "-" + duration;
                broadcast(session, response);
            }
        }

        if (incoming.getMessageType().startsWith("tReady")) {
            String[] parts = incoming.getMessageType().split("-");
            int playerId = Integer.parseInt(parts[1]);
            String lobbyId = getLobbyIdPlayerId(playerId);
            int duration = 0;
            if (parts.length > 2) {
                duration = Integer.parseInt(parts[2]);
            }

            if (lobbyId != null) {
                assignReady(lobbyId, playerId);
                if (duration > 0) {
                    setMatchDuration(lobbyId, duration);
                }
            }
            checkTandemStart(session, playerId);
        }

        if (incoming.getMessageType().equals("move")) {
            broadcast(session, message);
        }

        if (incoming.getMessageType().startsWith("place")) {
            broadcast(session, message);
        }

        if (incoming.getMessageType().startsWith("bankAdd")) {
            broadcast(session, message);
        }

        if (incoming.getMessageType().startsWith("bankRemove")) {
            broadcast(session, message);
        }

        if (incoming.getMessageType().equals("draw")) {
            broadcast(session, message);
        }

        if (incoming.getMessageType().startsWith("drawReply")) {
            broadcast(session, message);
        }

        if (incoming.getMessageType().equals("giveUp")) {
            broadcast(session, message);
        }

        if (incoming.getMessageType().startsWith("tandemEnd")) {
            broadcast(session, message);
        }

        switch (message) {
            case "start":
                logger.info("helo");
                break;
            case "quit":
                try {
                    session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Game ended"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
        }
        return message;
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.info(String.format("Session %s closed because of %s", session.getId(), closeReason));
    }
}
