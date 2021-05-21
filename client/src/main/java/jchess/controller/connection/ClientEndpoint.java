package jchess.controller.connection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Platform;
import jchess.model.Player;
import jchess.model.SocketMessage;
import jchess.model.TLobby;
import jchess.utils.AlertUtils;
import jchess.utils.SystemUtils;
import jchess.utils.WindowUtils;

import javax.websocket.*;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

@javax.websocket.ClientEndpoint
public class ClientEndpoint {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private CountDownLatch latch;
    private GsonBuilder gsonBuilder = new GsonBuilder();
    private Gson gson = gsonBuilder.create();
    public Session session = null;

    public ClientEndpoint() {
    }

    @OnOpen
    public void onOpen(Session session) {
        logger.info("Connected: " + session.getId());
        this.session = session;
        SystemUtils.getInstance().session = session;
        try {
            SocketMessage message = new SocketMessage(
                    session.getId(),
                    SystemUtils.getInstance().getUser(),
                    "start",
                    null
            );
            String messageString = gson.toJson(message);
            SystemUtils.getInstance().setSocketId(session.getId());
            session.getBasicRemote().sendText(messageString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) throws IOException {
        SystemUtils.getInstance().session.getBasicRemote().sendText(message);
    }

    @OnMessage
    public String onMessage(String message, Session session) throws IOException {
        if (message.startsWith("gotReady")) {
            String[] parts = message.split("-");
            int id = Integer.parseInt(parts[1]);
            int matchDuration = 0;
            if (parts.length > 2) {
                matchDuration = Integer.parseInt(parts[2]);
                if (matchDuration > 0) {
                    SystemUtils.getInstance().setMatchDuration(matchDuration);
                }
            }
            if (id == SystemUtils.getInstance().getOpponent().getId()) {
                SystemUtils.getInstance().setOpponentReady(true);
            }
            try {
                if (SystemUtils.getInstance().isPlayerReady() && SystemUtils.getInstance().isOpponentReady() && SystemUtils.getInstance().getMatchDuration() > 0) {
                    Platform.runLater(() -> WindowUtils.getInstance().switchScreen("chess", null, 600, 700));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "";
        }

        if (message.startsWith("tStart")) {
            String[] parts = message.split("/");
            if (parts[1].equals(SystemUtils.getInstance().getCurrentTandem().getId())) {
                SystemUtils.getInstance().setMatchDuration(Integer.parseInt(parts[2]));
            }
            System.out.println("tandem match can be started");
            Platform.runLater(() -> WindowUtils.getInstance().switchScreen("chess", null, 800, 700));
        }

        SocketMessage incoming = gson.fromJson(message, SocketMessage.class);
        System.out.println(incoming);
        if (incoming.getMessageType().equals("ok")) {
            RESTPlayersController.getOnlinePlayers();
            RESTLobbyController.getLobbies();
            RESTTandemController.getTandems();
        }

        if (incoming.getMessageType().equals("move")) {
            if (incoming.getPlayer().getId() == SystemUtils.getInstance().getOpponent().getId()) {
                Platform.runLater(() -> WindowUtils.getInstance().getFxController().printTiles());
                Platform.runLater(() -> WindowUtils.getInstance().getFxController().makeMove(incoming.getMove().getFrom(), incoming.getMove().getTo(), false));
            }
        }

        if (incoming.getMessageType().startsWith("place")) {
            if (incoming.getPlayer().getId() == SystemUtils.getInstance().getOpponent().getId()) {
                String[] parts = incoming.getMessageType().split("-");
                Platform.runLater(() -> WindowUtils.getInstance().getFxController().tandemPlaceHandler(parts[1], incoming.getMove().getTo()));
            }
        }

        if (incoming.getMessageType().startsWith("bankAdd")) {
            if (SystemUtils.getInstance().getCurrentTandem().isInLobby(incoming.getPlayer().getId()) &&
                SystemUtils.getInstance().getUser().getId() != incoming.getPlayer().getId()
            ) {
                String[] parts = incoming.getMessageType().split("-");
                Platform.runLater(() -> WindowUtils.getInstance().getFxController().addPieceToBank(parts[1], parts[2]));
            }
        }

        if (incoming.getMessageType().startsWith("bankRemove")) {
            if (SystemUtils.getInstance().getCurrentTandem().isInLobby(incoming.getPlayer().getId()) &&
                    SystemUtils.getInstance().getUser().getId() != incoming.getPlayer().getId()
            ) {
                String[] parts = incoming.getMessageType().split("-");
                Platform.runLater(() -> WindowUtils.getInstance().getFxController().removePieceFromBank(parts[1], parts[2]));
            }
        }

        if (incoming.getMessageType().equals("draw")) {
            if (incoming.getPlayer().getId() == SystemUtils.getInstance().getOpponent().getId()) {
                Platform.runLater(() -> WindowUtils.getInstance().getFxController().showDrawOffer());
            }
        }

        if (incoming.getMessageType().startsWith("drawReply")) {
            if (incoming.getPlayer().getId() == SystemUtils.getInstance().getOpponent().getId()) {
                String[] parts = incoming.getMessageType().split("-");
                if (parts.length == 2) {
                    boolean choice = parts[1].equals("ok");
                    Platform.runLater(() -> WindowUtils.getInstance().getFxController().showDrawOfferReply(choice));
                }
            }
        }

        if (incoming.getMessageType().equals("giveUp")) {
            if (incoming.getPlayer().getId() == SystemUtils.getInstance().getOpponent().getId()) {
                Platform.runLater(() -> WindowUtils.getInstance().getFxController().showOppGiveUp());
            }
        }

        if (incoming.getMessageType().startsWith("tandemEnd")) {
            Player sender = incoming.getPlayer();
            Player user = SystemUtils.getInstance().getUser();
            TLobby current = SystemUtils.getInstance().getCurrentTandem();
            if (!current.isInLobby(sender.getId()) || sender.getId() == user.getId()) {
                return "";
            }

            int winner = Integer.parseInt(incoming.getMessageType().split("-")[1]);
            Platform.runLater(() -> WindowUtils.getInstance().getFxController().saveMatch(winner));
        }
        return "asd";
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.info(String.format("Session %s close because if %s", session.getId(), closeReason));
    }
}
