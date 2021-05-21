package jchess.view.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import jchess.chess.board.*;
import jchess.chess.chesspiece.ColoredPieceType;
import jchess.chess.chesspiece.PieceType;
import jchess.chess.clock.ChessClock;
import jchess.chess.movement.MovementController;
import jchess.controller.connection.RESTMatchesController;
import jchess.controller.connection.RESTTMatchesController;
import jchess.controller.connection.SocketController;
import jchess.model.Player;
import jchess.model.SocketMessage;
import jchess.model.TLobby;
import jchess.utils.AlertUtils;
import jchess.utils.SystemUtils;
import jchess.utils.WindowUtils;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class ChessController implements Initializable {
    // labels
    @FXML Label playerLabel;
    @FXML Label opponentLabel;
    @FXML Text playerTime;
    @FXML Text oppTime;
    @FXML Label playerTeamLabel;
    @FXML Label oppTeamLabel;

    // buttons
    @FXML Button drawBtn;
    @FXML Button giveUpBtn;

    // boards
    @FXML GridPane vBoard;
    @FXML GridPane vOppBoard;

    // pane matrix
    @FXML Pane[][] panes = new Pane[8][8];
    @FXML Pane[][] oppPanes = new Pane[8][8];

    // bank for tandem
    @FXML GridPane bankGrid;
    @FXML Label bankLabel;
    @FXML Label hostWPLabel, hostBPLabel, oppWPLabel, oppBPLabel;
    @FXML Label hostWBLabel, hostBBLabel, oppWBLabel, oppBBLabel;
    @FXML Label hostWKLabel, hostBKLabel, oppWKLabel, oppBKLabel;
    @FXML Label hostWRLabel, hostBRLabel, oppWRLabel, oppBRLabel;
    @FXML Label hostWQLabel, hostBQLabel, oppWQLabel, oppBQLabel;

    List<Label> hostTeamLabels;
    List<Label> oppTeamLabels;

    // board and movement class
    private ChessBoard playerBoard = null;
    private ChessBoard opponentBoard;
    private MovementController controller;

    // for movement
    private boolean inMove = false;
    private Position from;
    private Position to;
    private ColoredPieceType tandemPick;
    private boolean isHostTeamPick = false;

    private boolean isLocal = true;
    private boolean isTandem = false;

    private BoardController boardController;
    private ChessClock clock;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.isLocal = SystemUtils.getInstance().getOpponent() == null;
        this.isTandem = SystemUtils.getInstance().getCurrentTandem() != null;
        boardController = new BoardController(isLocal, isTandem);
        boardInit();
        if (isLocal) {
            opponentBoardInit();
            this.playerLabel.setText("");
            this.opponentLabel.setText("");
        } else {
            this.playerLabel.setText("You: " + SystemUtils.getInstance().getUser().getUsername());
            this.opponentLabel.setText("Opponent: " + SystemUtils.getInstance().getOpponent().getUsername());
        }

        if (isTandem) {
            bankInit();
            TLobby currentTandem = SystemUtils.getInstance().getCurrentTandem();
            Player user = SystemUtils.getInstance().getUser();
            if (currentTandem.getHostTeam().get(0).getId() == user.getId() || currentTandem.getHostTeam().get(1).getId() == user.getId()) {
                playerTeamLabel.setText("Team: Green");
                oppTeamLabel.setText("Team: Red");
            } else {
                playerTeamLabel.setText("Team: Red");
                oppTeamLabel.setText("Team: Green");
            }
        } else {
            bankGrid.setVisible(false);
            bankLabel.setVisible(false);
            playerTeamLabel.setVisible(false);
            oppTeamLabel.setVisible(false);
        }
        vOppBoard.setVisible(false);
        WindowUtils.getInstance().setFxController(this);
        clockInit();
        bankInit();
    }

    private void boardInit() {
        for (Node node : vBoard.getChildren()) {
            Integer i = GridPane.getRowIndex(node);
            Integer j = GridPane.getColumnIndex(node);
            int rowIndex = i == null ? 0 : i;
            int colIndex = j == null ? 0 : j;

            if (node instanceof Pane) {
                node.setId("pane" + rowIndex + colIndex);

                if ((rowIndex + colIndex) % 2 == 0) {
                    // 247, 197, 153 light
                    node.setStyle("-fx-background-color: rgb(247, 197, 153)");
                } else {
                    // 194, 107, 33 dark
                    node.setStyle("-fx-background-color: rgb(194, 107, 33)");
                }

                fillTile(node, rowIndex, colIndex, SystemUtils.getInstance().isWhite());
                ((Pane) node).setOnMouseClicked(this::paneClicked);
                panes[rowIndex][colIndex] = (Pane) node;
            }
        }
        controller = new MovementController(playerBoard);
    }

    private void opponentBoardInit() {
        for (Node node : vOppBoard.getChildren()) {
            Integer i = GridPane.getRowIndex(node);
            Integer j = GridPane.getColumnIndex(node);
            int rowIndex = i == null ? 0 : i;
            int colIndex = j == null ? 0 : j;

            if (node instanceof Pane) {
                node.setId("pane" + rowIndex + colIndex);

                if ((rowIndex + colIndex) % 2 == 0) {
                    // 247, 197, 153 light
                    node.setStyle("-fx-background-color: rgb(247, 197, 153)");
                } else {
                    // 194, 107, 33 dark
                    node.setStyle("-fx-background-color: rgb(194, 107, 33)");
                }

                fillTile(node, rowIndex, colIndex, !SystemUtils.getInstance().isWhite());
                ((Pane) node).setOnMouseClicked(this::paneClicked);
                oppPanes[rowIndex][colIndex] = (Pane) node;
            }
        }
    }

    private void bankInit() {
        for (Node node : bankGrid.getChildren()) {
            if (node instanceof Pane) {
                Integer colIndex = GridPane.getColumnIndex(node);
                int col = colIndex == null ? 0 : colIndex;
                if (col % 2 == 0) {
                    node.setStyle("-fx-background-color: #90ee90");
                } else {
                    node.setStyle("-fx-background-color: #ffcccb");
                }
            }
        }
        hostTeamLabels = new ArrayList<>();
        oppTeamLabels = new ArrayList<>();

        hostTeamLabels.add(hostWPLabel);
        hostTeamLabels.add(hostBPLabel);
        hostTeamLabels.add(hostWRLabel);
        hostTeamLabels.add(hostBRLabel);
        hostTeamLabels.add(hostWKLabel);
        hostTeamLabels.add(hostBKLabel);
        hostTeamLabels.add(hostWBLabel);
        hostTeamLabels.add(hostBBLabel);
        hostTeamLabels.add(hostWQLabel);
        hostTeamLabels.add(hostBQLabel);

        oppTeamLabels.add(oppWPLabel);
        oppTeamLabels.add(oppBPLabel);
        oppTeamLabels.add(oppWRLabel);
        oppTeamLabels.add(oppBRLabel);
        oppTeamLabels.add(oppWKLabel);
        oppTeamLabels.add(oppBKLabel);
        oppTeamLabels.add(oppWBLabel);
        oppTeamLabels.add(oppBBLabel);
        oppTeamLabels.add(oppWQLabel);
        oppTeamLabels.add(oppBQLabel);
    }

    private void fillTile(Node node, int rowIndex, int colIndex, boolean isWhite) {
        ColoredPieceType type = null;
        if (rowIndex == 1) {
            type = isWhite ? ColoredPieceType.BLACK_PAWN : ColoredPieceType.WHITE_PAWN;
            ((Pane) node).getChildren().add(getPieceImg(type));
        } else if (rowIndex == 6) {
            type = isWhite ? ColoredPieceType.WHITE_PAWN : ColoredPieceType.BLACK_PAWN;
            ((Pane) node).getChildren().add(getPieceImg(type));
        } else if (rowIndex == 0) {
            if (colIndex == 0 || colIndex == 7) {
                type = isWhite ? ColoredPieceType.BLACK_ROOK : ColoredPieceType.WHITE_ROOK;
                ((Pane) node).getChildren().add(getPieceImg(type));
            } else if (colIndex == 1 || colIndex == 6) {
                type = isWhite ? ColoredPieceType.BLACK_KNIGHT : ColoredPieceType.WHITE_KNIGHT;
                ((Pane) node).getChildren().add(getPieceImg(type));
            } else if (colIndex == 2 || colIndex == 5) {
                type = isWhite ? ColoredPieceType.BLACK_BISHOP : ColoredPieceType.WHITE_BISHOP;
                ((Pane) node).getChildren().add(getPieceImg(type));
            } else if (colIndex == 3) {
                type = isWhite ? ColoredPieceType.BLACK_QUEEN : ColoredPieceType.WHITE_KING;
                ((Pane) node).getChildren().add(getPieceImg(type));
            } else if (colIndex == 4) {
                type = isWhite ? ColoredPieceType.BLACK_KING : ColoredPieceType.WHITE_QUEEN;
                ((Pane) node).getChildren().add(getPieceImg(type));
            }
        } else if (rowIndex == 7) {
            if (colIndex == 0 || colIndex == 7) {
                type = isWhite ? ColoredPieceType.WHITE_ROOK : ColoredPieceType.BLACK_ROOK;
                ((Pane) node).getChildren().add(getPieceImg(type));
            } else if (colIndex == 1 || colIndex == 6) {
                type = isWhite ? ColoredPieceType.WHITE_KNIGHT : ColoredPieceType.BLACK_KNIGHT;
                ((Pane) node).getChildren().add(getPieceImg(type));
            } else if (colIndex == 2 || colIndex == 5) {
                type = isWhite ? ColoredPieceType.WHITE_BISHOP : ColoredPieceType.BLACK_BISHOP;
                ((Pane) node).getChildren().add(getPieceImg(type));
            } else if (colIndex == 3) {
                type = isWhite ? ColoredPieceType.WHITE_QUEEN : ColoredPieceType.BLACK_KING;
                ((Pane) node).getChildren().add(getPieceImg(type));
            } else if (colIndex == 4) {
                type = isWhite ? ColoredPieceType.WHITE_KING : ColoredPieceType.BLACK_QUEEN;
                ((Pane) node).getChildren().add(getPieceImg(type));
            }
        }
    }

    private ImageView getPieceImg(ColoredPieceType type) {
        String resourcePath = "jchess/view/images/chesspieces/";
        switch (type) {
            case WHITE_PAWN:
                resourcePath += "wpawn";
                break;
            case WHITE_ROOK:
                resourcePath += "wrook";
                break;
            case WHITE_KNIGHT:
                resourcePath += "wknight";
                break;
            case WHITE_BISHOP:
                resourcePath += "wbishop";
                break;
            case WHITE_QUEEN:
                resourcePath += "wqueen";
                break;
            case WHITE_KING:
                resourcePath += "wking";
                break;
            case BLACK_PAWN:
                resourcePath += "bpawn";
                break;
            case BLACK_ROOK:
                resourcePath += "brook";
                break;
            case BLACK_KNIGHT:
                resourcePath += "bknight";
                break;
            case BLACK_BISHOP:
                resourcePath += "bbishop";
                break;
            case BLACK_QUEEN:
                resourcePath += "bqueen";
                break;
            case BLACK_KING:
                resourcePath += "bking";
                break;
        }
        resourcePath += ".png";

        return new ImageView(new Image(String.valueOf(getClass().getClassLoader().getResource(resourcePath))));
    }

    private void clockInit() {
        int duration = SystemUtils.getInstance().getMatchDuration();
        clock = new ChessClock(duration, 0);
        playerTime.textProperty().bind(clock.userTimeProperty());
        oppTime.textProperty().bind(clock.oppTimeProperty());
        clock.startClock();
        SystemUtils.getInstance().setMatchStartTime(new Timestamp(new Date().getTime()));
    }

    public void paneClicked(MouseEvent mouseEvent) {
        String paneId = null;
        if (mouseEvent.getTarget() instanceof Pane) {
            paneId = ((Pane) mouseEvent.getTarget()).getId();
        } else if (mouseEvent.getTarget() instanceof ImageView) {
            paneId = ((ImageView) mouseEvent.getTarget()).getParent().getId();
        }

        if (paneId == null) {
            System.err.println("Incorrect move");
            return;
        }

        int rowIndex = Integer.parseInt(paneId.substring(4, 5));
        int colIndex = Integer.parseInt(paneId.substring(5));

        if (tandemPick != null) {
            tandemPlaceHandler(rowIndex, colIndex);
        } else {
            moveHandler(rowIndex, colIndex);
        }

    }

    public void bankPieceClicked(MouseEvent mouseEvent) {
        if (!boardController.isPlayerTurn()) {
            AlertUtils.showAlert("Not your turn!");
        }

        String team = null;
        if (mouseEvent.getTarget() instanceof ImageView) {
            team = ((ImageView) mouseEvent.getTarget()).getId().substring(0, 1);
        }
        if (team == null) {
            return;
        }

        boolean isHostTeam = false;
        Player user = SystemUtils.getInstance().getUser();
        TLobby current = SystemUtils.getInstance().getCurrentTandem();
        if (user.getId() == current.getHostTeam().get(0).getId() || user.getId() == current.getHostTeam().get(1).getId()) {
            isHostTeam = true;
        }

        if ((isHostTeam && team.equals("O")) || (!isHostTeam && team.equals("H"))) {
            AlertUtils.showAlert("That chess piece belongs to your opponent's bank!");
            return;
        }

        boolean isPieceWhite = ((ImageView) mouseEvent.getTarget()).getId().contains("WHITE");
        if (SystemUtils.getInstance().isWhite() != isPieceWhite) {
            AlertUtils.showAlert("Only your teammate can place that chess piece!");
            return;
        }

        ColoredPieceType pieceToPlace = ColoredPieceType.valueOf(((ImageView) mouseEvent.getTarget()).getId().substring(2));

        if (boardController.tandemController.getPieceCount(pieceToPlace, isHostTeam) == 0) {
            return;
        }

        tandemPick = pieceToPlace;
    }

    // offering draw
    public void onDrawOffer(ActionEvent actionEvent) {
        try {
            SocketController.getInstance().sendDrawOffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // incoming draw request
    public void showDrawOffer() {
        boolean result = AlertUtils.showDrawOfferAlert();
        try {
            SocketController.getInstance().sendDrawReply(result);
            if (result) {
                if (SystemUtils.getInstance().isWhite()) {
                    SystemUtils.getInstance().setMatchEndTime(new Timestamp(new Date().getTime()));
                    RESTMatchesController.addMatch(0);
                }
                SystemUtils.getInstance().clearLobbyData();
                clock.stopClock(false);
                WindowUtils.getInstance().switchScreen("lobby", playerLabel, "lobby", 800, 500);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showDrawOfferReply(boolean choice) {
        AlertUtils.showDrawReplyAlert(choice);
        if (choice) {
            if (SystemUtils.getInstance().isWhite()) {
                try {
                    SystemUtils.getInstance().setMatchEndTime(new Timestamp(new Date().getTime()));
                    RESTMatchesController.addMatch(0);
                    SystemUtils.getInstance().clearLobbyData();
                    clock.stopClock(false);
                    WindowUtils.getInstance().switchScreen("lobby", playerLabel, "lobby", 800, 500);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onGiveUp(ActionEvent actionEvent) {
        SystemUtils.getInstance().setMatchEndTime(new Timestamp(new Date().getTime()));
        try {
            if (!isTandem) {
                SocketController.getInstance().sendGiveUp();
                if (SystemUtils.getInstance().isWhite()) {
                    RESTMatchesController.addMatch(2);
                }
            } else {
                TLobby current = SystemUtils.getInstance().getCurrentTandem();
                Player user = SystemUtils.getInstance().getUser();
                int winner = current.isHostTeamMember(user.getId()) ? 2 : 1;
                SocketController.getInstance().sendTandemEnd(winner);
                if (user.getId() == current.getHost().getId()) {
                    RESTTMatchesController.addMatch(winner);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        clock.stopClock(false);
        SystemUtils.getInstance().clearLobbyData();
        WindowUtils.getInstance().switchScreen("lobby", playerLabel, "lobby", 800, 500);
    }

    public void showOppGiveUp() {
        AlertUtils.showGiveUpAlert();
        if (SystemUtils.getInstance().isWhite()) {
            try {
                SystemUtils.getInstance().setMatchEndTime(new Timestamp(new Date().getTime()));
                RESTMatchesController.addMatch(1);
                WindowUtils.getInstance().switchScreen("lobby", playerLabel, "lobby", 800, 500);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        SystemUtils.getInstance().clearLobbyData();
        clock.stopClock(false);
        WindowUtils.getInstance().switchScreen("lobby", playerLabel, "lobby", 800, 500);
    }

    private void moveHandler(int rowIndex, int colIndex) {
        if (!inMove) {
            inMove = true;
            from = new Position(rowIndex, colIndex);
        } else {
            to = new Position(rowIndex, colIndex);
            if (isLocal) {
                makeMovesLocal(from, to);
            } else {
                makeMove(from, to, true, true);
            }
            inMove = false;
            from = null;
            to = null;
        }
    }

    private void tandemPlaceHandler(int rowIndex, int colIndex) {
        Position placePos = new Position(rowIndex, colIndex);
        if (boardController.placeFromBank(tandemPick, placePos)) {
            panes[rowIndex][colIndex].getChildren().add(getPieceImg(tandemPick));
            updateBank();
            try {
                SocketController.getInstance().sendRemoveFromBank(tandemPick);
                SocketController.getInstance().sendPiecePlacement(tandemPick, boardController.getTileByPos(placePos));
            } catch (IOException e) {
                e.printStackTrace();
            }
            clock.switchTurn();
        } else {
            AlertUtils.showAlert("Invalid placement!");
        }
        tandemPick = null;
    }

    // called from socket - opponent placing a piece
    public void tandemPlaceHandler(String pieceStr, TileType to) {
        boardController.placePiece(pieceStr, to);
        ColoredPieceType pieceType = ColoredPieceType.valueOf(pieceStr);
        Position placePos = boardController.getPosByTile(to);
        panes[placePos.row][placePos.col].getChildren().add(getPieceImg(pieceType));
        if (boardController.isCheckMate()) {
            saveMatch();
        }
        clock.switchTurn();
    }

    public void printTiles() {
        this.boardController.printTiles();
    }

    // function called in socket
    public void makeMove(TileType from, TileType to, boolean send) {
        makeMove(boardController.getPosByTile(from), boardController.getPosByTile(to), false, false);
    }

    // function called from socket
    public void addPieceToBank(String pieceStr, String teamStr) {
        boardController.tandemController.addPieceToBank(pieceStr, teamStr);
        updateBank();
    }

    // function called from socket
    public void removePieceFromBank(String pieceStr, String teamStr) {
        boardController.tandemController.removePieceFromBank(ColoredPieceType.valueOf(pieceStr), teamStr.equals("host"));
        updateBank();
    }

    public void updateBank() {
        setBankLabelText(hostWPLabel, ColoredPieceType.WHITE_PAWN, true);
        setBankLabelText(hostWRLabel, ColoredPieceType.WHITE_ROOK, true);
        setBankLabelText(hostWBLabel, ColoredPieceType.WHITE_BISHOP, true);
        setBankLabelText(hostWKLabel, ColoredPieceType.WHITE_KNIGHT, true);
        setBankLabelText(hostWQLabel, ColoredPieceType.WHITE_QUEEN, true);
        setBankLabelText(hostBPLabel, ColoredPieceType.BLACK_PAWN, true);
        setBankLabelText(hostBRLabel, ColoredPieceType.BLACK_ROOK, true);
        setBankLabelText(hostBBLabel, ColoredPieceType.BLACK_BISHOP, true);
        setBankLabelText(hostBKLabel, ColoredPieceType.BLACK_KNIGHT, true);
        setBankLabelText(hostBQLabel, ColoredPieceType.BLACK_QUEEN, true);

        setBankLabelText(oppWPLabel, ColoredPieceType.WHITE_PAWN, false);
        setBankLabelText(oppWRLabel, ColoredPieceType.WHITE_ROOK, false);
        setBankLabelText(oppWBLabel, ColoredPieceType.WHITE_BISHOP, false);
        setBankLabelText(oppWKLabel, ColoredPieceType.WHITE_KNIGHT, false);
        setBankLabelText(oppWQLabel, ColoredPieceType.WHITE_QUEEN, false);
        setBankLabelText(oppBPLabel, ColoredPieceType.BLACK_PAWN, false);
        setBankLabelText(oppBRLabel, ColoredPieceType.BLACK_ROOK, false);
        setBankLabelText(oppBBLabel, ColoredPieceType.BLACK_BISHOP, false);
        setBankLabelText(oppBKLabel, ColoredPieceType.BLACK_KNIGHT, false);
        setBankLabelText(oppBQLabel, ColoredPieceType.BLACK_QUEEN, false);
    }

    public void setBankLabelText(Label label, ColoredPieceType piece, boolean isHostTeam) {
        Integer count = boardController.tandemController.getPieceCount(piece, isHostTeam);
        if (count != 0) {
            label.setText(count.toString());
        } else {
            label.setText("");
        }
    }

    private void makeMove(Position from, Position to, boolean turnCheck, boolean send) {
        if (turnCheck) {
            if (!boardController.isPlayerTurn()) {
                AlertUtils.showAlert("Not your turn!");
                return;
            }
        }
        if (!boardController.moveOnBoard(from, to)) {
            AlertUtils.showAlert("Incorrect move");
            return;
        }

        movePieceImg(panes, from, to);
        if (send) {
            TileType msgFrom = boardController.getTileByPos(from);
            TileType msgTo = boardController.getTileByPos(to);
            try {
                SocketController.getInstance().sendMove(msgFrom, msgTo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        List<Position> playerCastling =  boardController.getCastling(true);
        if (boardController.isCastling()) {
            movePieceImg(panes, playerCastling.get(0), playerCastling.get(1));
        }

        if (boardController.isCheckMate()) {
            AlertUtils.showAlert("Checkmate!");
            saveMatch();
        }
        clock.switchTurn();
    }

    private void makeMovesLocal(Position from, Position to) {
        if (!boardController.moveOnBothBoard(from, to)) {
            AlertUtils.showAlert("Incorrect move");
            return;
        }
        Position tmpFrom = null;
        Position tmpTo = null;
        if (!boardController.isPlayerTurn()) {
            movePieceImg(panes, from, to);
            tmpFrom = boardController.getOtherBoardPosition(from);
            tmpTo = boardController.getOtherBoardPosition(to);
            movePieceImg(oppPanes, tmpFrom, tmpTo);
            handleCastling();
            switchBoard();
            clock.switchTurn();
        } else {
            movePieceImg(oppPanes, from, to);
            tmpFrom = boardController.getOtherBoardPosition(from);
            tmpTo = boardController.getOtherBoardPosition(to);
            movePieceImg(panes, tmpFrom, tmpTo);
            handleCastling();
            switchBoard();
            clock.switchTurn();
        }
    }

    private void handleCastling() {
        List<Position> playerCastling =  boardController.getCastling(true);
        List<Position> oppCastling = boardController.getCastling(false);
        if (boardController.isCastlingLocal()) {
            movePieceImg(panes, playerCastling.get(0), playerCastling.get(1));
            movePieceImg(oppPanes, oppCastling.get(0), oppCastling.get(1));
        }
    }

    // called from socket
    public void saveMatch(int winner) {
        SystemUtils.getInstance().setMatchEndTime(new Timestamp(new Date().getTime()));
        if (winner == 1) {
            if (SystemUtils.getInstance().getCurrentTandem().isHostTeamMember(SystemUtils.getInstance().getUser().getId())) {
                AlertUtils.showAlert("Your team won the game!");
            } else {
                AlertUtils.showAlert("Your team lost the game!");
            }
        } else if (winner == 2) {
            if (SystemUtils.getInstance().getCurrentTandem().isHostTeamMember(SystemUtils.getInstance().getUser().getId())) {
                AlertUtils.showAlert("Your team lost the game!");
            } else {
                AlertUtils.showAlert("Your team won the game!");
            }
        }

        TLobby current = SystemUtils.getInstance().getCurrentTandem();
        Player user = SystemUtils.getInstance().getUser();
        if (current.getHost().getId() == user.getId()) {
            try {
                RESTTMatchesController.addMatch(winner);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        clock.stopClock(false);
        SystemUtils.getInstance().clearLobbyData();
        WindowUtils.getInstance().switchScreen("lobby", playerLabel, "lobby", 800, 500);
    }

    public void saveMatch() {
        SystemUtils.getInstance().setMatchEndTime(new Timestamp(new Date().getTime()));
        if (isLocal) {
            return;
        }

        if (!isTandem) {
            if (SystemUtils.getInstance().isWhite()) {
                int winner = boardController.isPlayerTurn() ? 2 : 1;
                try {
                    RESTMatchesController.addMatch(winner);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            TLobby current = SystemUtils.getInstance().getCurrentTandem();
            Player user = SystemUtils.getInstance().getUser();
            int winner = 0;
            if (boardController.isPlayerTurn()) {
                winner = current.isHostTeamMember(user.getId()) ? 2 : 1;
            } else {
                winner = current.isHostTeamMember(user.getId()) ? 1 : 2;
            }
            try {
                if (SystemUtils.getInstance().isWhite()) {
                    SocketController.getInstance().sendTandemEnd(winner);
                }
                if (current.getHost().getId() == user.getId()) {
                    RESTTMatchesController.addMatch(winner);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        clock.stopClock(false);
        SystemUtils.getInstance().clearLobbyData();
        WindowUtils.getInstance().switchScreen("lobby", playerLabel, "lobby", 800, 500);
    }

    private void movePieceImg(Pane[][] paneMatrix, Position from, Position to) {
        ImageView pieceImg = null;
        for (Node node : paneMatrix[from.row][from.col].getChildren()) {
            if (node instanceof ImageView) {
                pieceImg = (ImageView) node;
                break;
            }
        }

        if (pieceImg == null) {
            System.err.println("Failed to get image from FROM-POS");
            return;
        }

        paneMatrix[from.row][from.col].getChildren().removeAll();

        ImageView oldPieceImg = null;
        for (Node node : paneMatrix[to.row][to.col].getChildren()) {
            if (node instanceof ImageView) {
                oldPieceImg = (ImageView) node;
                break;
            }
        }

        paneMatrix[to.row][to.col].getChildren().removeAll(oldPieceImg);
        paneMatrix[to.row][to.col].getChildren().add(pieceImg);
    }

    private void switchBoard() {
        vBoard.setVisible(!vBoard.isVisible());
        vOppBoard.setVisible(!vOppBoard.isVisible());
    }
}
