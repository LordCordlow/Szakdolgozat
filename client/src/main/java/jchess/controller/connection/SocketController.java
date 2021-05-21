package jchess.controller.connection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jchess.chess.board.TileType;
import jchess.chess.chesspiece.ChessPiece;
import jchess.chess.chesspiece.ColoredPieceType;
import jchess.chess.chesspiece.PieceType;
import jchess.model.ChessMove;
import jchess.model.Player;
import jchess.model.SocketMessage;
import jchess.model.TLobby;
import jchess.utils.SystemUtils;
import org.glassfish.tyrus.client.ClientManager;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

public class SocketController {
    private ClientEndpoint clientEndpoint;
    private static SocketController single_instance;
    private final GsonBuilder gsonBuilder = new GsonBuilder();
    private final Gson gson = gsonBuilder.create();

    private SocketController() {
        this.clientEndpoint = new ClientEndpoint();
    }

    public static SocketController getInstance() {
        if (single_instance == null) {
            single_instance = new SocketController();
        }
        return single_instance;
    }

    public void openSocketConnection() {
        ClientManager clientManager = ClientManager.createClient();

        try {
            String url = SystemUtils.getInstance().appProps.getProperty("websocket_url") + "/" + SystemUtils.getInstance().getUser().getId();
            clientManager.connectToServer(ClientEndpoint.class, new URI(url));
        } catch (DeploymentException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void sendReady(int matchDuration) throws IOException {
        String ready = "ready-" + SystemUtils.getInstance().getUser().getId() + "-" + matchDuration;
        SocketMessage message = new SocketMessage(SystemUtils.getInstance().getSocketId(), SystemUtils.getInstance().getUser(), ready, null);
        SystemUtils.getInstance().setPlayerReady(true);
        String json = gson.toJson(message, SocketMessage.class);
        clientEndpoint.sendMessage(json);
    }

    public void sendTandemReady(int matchDuration) throws IOException {
        String ready = "tReady-" + SystemUtils.getInstance().getUser().getId() + (matchDuration > 0 ? "-" + matchDuration : "");
        SocketMessage message = new SocketMessage(SystemUtils.getInstance().getSocketId(), SystemUtils.getInstance().getUser(), ready, null);
        String json = gson.toJson(message, SocketMessage.class);
        clientEndpoint.sendMessage(json);
    }

    public void sendMove(TileType from, TileType to) throws IOException {
        ChessMove move = new ChessMove(from, to, SystemUtils.getInstance().getUser().getId());
        SocketMessage message = new SocketMessage(
                SystemUtils.getInstance().getSocketId(),
                SystemUtils.getInstance().getUser(),
                "move",
                move
        );
        String json = gson.toJson(message, SocketMessage.class);
        clientEndpoint.sendMessage(json);
    }

    public void sendPiecePlacement(ColoredPieceType piece, TileType to) throws IOException {
        ChessMove move = new ChessMove(null, to, SystemUtils.getInstance().getUser().getId());
        SocketMessage message = new SocketMessage(
                SystemUtils.getInstance().getSocketId(),
                SystemUtils.getInstance().getUser(),
                "place-" + piece.name(),
                move
        );
        String json = gson.toJson(message, SocketMessage.class);
        clientEndpoint.sendMessage(json);
    }

    public void sendPieceToBank(ColoredPieceType piece) throws IOException {
        String pieceToBank = "bankAdd-" + piece.name();
        TLobby current = SystemUtils.getInstance().getCurrentTandem();
        Player user = SystemUtils.getInstance().getUser();
        if (current.getHostTeam().get(0).getId() == user.getId() || current.getHostTeam().get(1).getId() == user.getId()) {
            pieceToBank += "-" + "host";
        } else if (current.getOppTeam().get(0).getId() == user.getId() || current.getOppTeam().get(1).getId() == user.getId()) {
            pieceToBank += "-" + "opp";
        }
        SocketMessage message = new SocketMessage(
                SystemUtils.getInstance().getSocketId(),
                SystemUtils.getInstance().getUser(),
                pieceToBank,
                null
        );
        String json = gson.toJson(message, SocketMessage.class);
        clientEndpoint.sendMessage(json);
    }

    public void sendRemoveFromBank(ColoredPieceType pieceType) throws IOException {
        String pieceFromBank = "bankRemove-" + pieceType.name();
        TLobby current = SystemUtils.getInstance().getCurrentTandem();
        Player user = SystemUtils.getInstance().getUser();
        if (current.isHostTeamMember(user.getId())) {
            pieceFromBank += "-host";
        } else {
            pieceFromBank += "-opp";
        }
        SocketMessage message = new SocketMessage(
                SystemUtils.getInstance().getSocketId(),
                user,
                pieceFromBank,
                null
        );
        String json = gson.toJson(message, SocketMessage.class);
        clientEndpoint.sendMessage(json);
    }

    public void sendDrawOffer() throws IOException {
        SocketMessage message = new SocketMessage(
                SystemUtils.getInstance().getSocketId(),
                SystemUtils.getInstance().getUser(),
                "draw",
                null
        );
        String json = gson.toJson(message, SocketMessage.class);
        clientEndpoint.sendMessage(json);
    }

    public void sendDrawReply(boolean choice) throws IOException {
        String result = choice ? "drawReply-ok" : "drawReply-cancel";
        SocketMessage message = new SocketMessage(
                SystemUtils.getInstance().getSocketId(),
                SystemUtils.getInstance().getUser(),
                result,
                null
        );
        String json = gson.toJson(message, SocketMessage.class);
        clientEndpoint.sendMessage(json);
    }

    public void sendGiveUp() throws IOException {
        SocketMessage message = new SocketMessage(
                SystemUtils.getInstance().getSocketId(),
                SystemUtils.getInstance().getUser(),
                "giveUp",
                null
        );
        String json = gson.toJson(message, SocketMessage.class);
        clientEndpoint.sendMessage(json);
    }

    public void sendTandemEnd(int winner) throws IOException {
        SocketMessage message = new SocketMessage(
                SystemUtils.getInstance().getSocketId(),
                SystemUtils.getInstance().getUser(),
                "tandemEnd-" + winner,
                null
        );
        String json = gson.toJson(message, SocketMessage.class);
        clientEndpoint.sendMessage(json);
    }

    public boolean isSessionActive() {
        return clientEndpoint.session != null && clientEndpoint.session.isOpen();
    }
}
