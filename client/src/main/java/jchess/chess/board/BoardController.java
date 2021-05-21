package jchess.chess.board;

import javafx.geometry.Pos;
import jchess.chess.chesspiece.*;
import jchess.model.Player;
import jchess.model.TLobby;
import jchess.utils.AlertUtils;
import jchess.utils.SystemUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardController {
    public ChessBoard playerBoard;
    public ChessBoard oppBoard;
    public boolean whiteTurn = true;
    public List<Position> playerCastling;
    public List<Position> oppCastling;
    private Map<TileType, Position> playerTiles;
    private Map<TileType, Position> oppTiles;
    public TandemController tandemController;

    public BoardController(boolean isLocal, boolean isTandem) {
        this.playerCastling = new ArrayList<>();
        this.oppCastling = new ArrayList<>();
        if (isLocal) {
            this.playerBoard = new ChessBoard(true);
            this.oppBoard = new ChessBoard(false);
            this.playerTiles = new HashMap<>();
            this.oppTiles = new HashMap<>();
            fillTileMap(playerBoard, playerTiles);
            fillTileMap(oppBoard, oppTiles);
        } else {
            this.playerBoard = new ChessBoard(SystemUtils.getInstance().isWhite());
            this.playerTiles = new HashMap<>();
            fillTileMap(playerBoard, playerTiles);
            if (isTandem) {
                tandemController = new TandemController(playerBoard);
            }
        }
    }

    public void fillTileMap(ChessBoard board, Map<TileType, Position> map) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                map.put(board.fields[i][j].type, new Position(i, j));
            }
        }
    }

    public Position getPosByTile(TileType tileType) {
        return this.playerTiles.get(tileType);
    }

    public TileType getTileByPos(Position pos) {
        return playerBoard.fields[pos.row][pos.col].type;
    }

    public Position getOtherBoardPosition(TileType tileType) {
        return isPlayerTurn() ? oppTiles.get(tileType) : playerTiles.get(tileType);
    }

    public Position getOtherBoardPosition(Position pos) {
        return isPlayerTurn() ? oppTiles.get(playerBoard.fields[pos.row][pos.col].type) : playerTiles.get(oppBoard.fields[pos.row][pos.col].type);
    }

    public ChessBoard getOtherBoard(boolean isWhite) {
        return playerBoard.playerIsWhite && isWhite ? playerBoard : oppBoard;
    }

    public void movePiece(Position from, Position to, ChessBoard board) {
        List<Position> list = board == playerBoard ? playerCastling : oppCastling;
        if (board.getPiece(from) instanceof King && ((King) board.getPiece(from)).canCastle()) {
            // castling
            if (board.playerIsWhite) {
                if ((to.col == 2 ^ to.col == 6)) {
                        if (to.col == 2) {
                            board.setPiece(to, board.getPiece(from));
                            board.setPiece(from, null);
                            ChessPiece rook = board.getPiece(to.row, 0);
                            list.add(new Position(rook.pos.row, rook.pos.col));
                            board.setPiece(new Position(to.row, 0), null);
                            board.setPiece(new Position(to.row, to.col + 1), rook);
                            list.add(new Position(rook.pos.row, rook.pos.col));
                        } else if (to.col == 6) {
                            board.setPiece(to, board.getPiece(from));
                            board.setPiece(from, null);
                            ChessPiece rook = board.getPiece(to.row, 7);
                            list.add(new Position(rook.pos.row, rook.pos.col));
                            board.setPiece(new Position(to.row, 7), null);
                            board.setPiece(new Position(to.row, to.col - 1), rook);
                            list.add(new Position(rook.pos.row, rook.pos.col));
                        }
                } else {
                    board.getPiece(from).isFirstTime = false;
                    board.setPiece(to, board.getPiece(from));
                    board.setPiece(from, null);
                }
            } else {
                if ((to.col == 1 ^ to.col == 5)) {
                    if (board.getPiece(from).canMove(to)) {
                        if (to.col == 1) {
                            board.setPiece(to, board.getPiece(from));
                            board.setPiece(from, null);
                            ChessPiece rook = board.getPiece(to.row, 0);
                            list.add(new Position(rook.pos.row, rook.pos.col));
                            board.setPiece(new Position(to.row, 0), null);
                            board.setPiece(new Position(to.row, to.col + 1), rook);
                            list.add(new Position(rook.pos.row, rook.pos.col));
                        } else if (to.col == 5) {
                            board.setPiece(to, board.getPiece(from));
                            board.setPiece(from, null);
                            ChessPiece rook = board.getPiece(to.row, 7);
                            list.add(new Position(rook.pos.row, rook.pos.col));
                            board.setPiece(new Position(to.row, 7), null);
                            board.setPiece(new Position(to.row, to.col - 1), rook);
                            list.add(new Position(rook.pos.row, rook.pos.col));
                        }
                    }
                } else {
                    board.getPiece(from).isFirstTime = false;
                    board.setPiece(to, board.getPiece(from));
                    board.setPiece(from, null);
                }
            }
        } else {
            board.getPiece(from).isFirstTime = false;
            if (tandemController != null) {
                tandemController.checkKill(from, to);
            }
            board.setPiece(to, board.getPiece(from));
            board.setPiece(from, null);
        }
    }

    public boolean moveOnBothBoard(Position from, Position to) {
        playerCastling.clear();
        oppCastling.clear();
        if (isPlayerTurn()) {
            if (playerBoard.getPiece(from).canMove(to) && playerBoard.playerIsWhite == playerBoard.getPiece(from).isWhite) {
                movePiece(from, to, playerBoard);

                Position oppBoardFrom = oppTiles.get(playerBoard.getFieldType(from));
                Position oppBoardTo = oppTiles.get(playerBoard.getFieldType(to));
                movePiece(oppBoardFrom, oppBoardTo, oppBoard);
                printBoard(playerBoard);
                printBoard(oppBoard);

                if (playerBoard.isCheckMate()) {
                    AlertUtils.showAlert("Checkmate!");
                }
            } else {
                System.err.println("bad move");
                return false;
            }
        } else {
            if (oppBoard.getPiece(from).canMove(to) && oppBoard.playerIsWhite == oppBoard.getPiece(from).isWhite) {
                movePiece(from, to, oppBoard);
                Position playerBoardFrom = playerTiles.get(oppBoard.getFieldType(from));
                Position playerBoardTo = playerTiles.get(oppBoard.getFieldType(to));
                movePiece(playerBoardFrom, playerBoardTo, playerBoard);
                printBoard(playerBoard);
                printBoard(oppBoard);
            } else {
                System.err.println("bad move");
                return false;
            }
        }
        whiteTurn = !whiteTurn;
        return true;
    }

    public boolean moveOnBoard(Position from, Position to) {
        playerCastling.clear();
        if (playerBoard.getPiece(from) != null && playerBoard.getPiece(from).canMove(to)) {
            movePiece(from, to, playerBoard);
        } else {
            System.err.println("bad move");
            return false;
        }
        whiteTurn = !whiteTurn;
        return true;
    }

    public boolean placeFromBank(ColoredPieceType piece, Position pos) {
        if (playerBoard.getPiece(pos) != null) {
            return false;
        }

        if (!isPlayerTurn()) {
            return false;
        }

        String[] parts = piece.name().split("_");
        boolean isWhite = parts[0].equals("WHITE");
        ChessPiece chessPiece = null;
        switch (parts[1]) {
            case "PAWN": chessPiece = new Pawn(isWhite, pos, playerBoard); break;
            case "KNIGHT": chessPiece = new Knight(isWhite, pos, playerBoard); break;
            case "BISHOP": chessPiece = new Bishop(isWhite, pos, playerBoard); break;
            case "ROOK": chessPiece = new Rook(isWhite, pos, playerBoard); break;
            case "QUEEN": chessPiece = new Queen(isWhite, pos, playerBoard);
        }

        if (playerBoard.getKing(isWhite) != null && playerBoard.getKing(isWhite).isInCheck()) {
            playerBoard.setPiece(pos, chessPiece);
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (playerBoard.getPiece(i, j) != null && playerBoard.getPiece(i, j).isWhite != isWhite && playerBoard.getPiece(i, j).canMove(playerBoard.getKing(isWhite).pos)) {
                        playerBoard.setPiece(pos, null);
                        return false;
                    }
                }
            }
            tandemController.removePieceFromBank(piece, SystemUtils.getInstance().getCurrentTandem().isHostTeamMember(SystemUtils.getInstance().getUser().getId()));
            whiteTurn = !whiteTurn;
            return true;
        } else {
            playerBoard.setPiece(pos, chessPiece);
            tandemController.removePieceFromBank(piece, SystemUtils.getInstance().getCurrentTandem().isHostTeamMember(SystemUtils.getInstance().getUser().getId()));
            whiteTurn = !whiteTurn;
            return true;
        }
    }

    public void placePiece(String pieceStr, TileType to) {
        Position pos = getPosByTile(to);
        String[] parts = pieceStr.split("_");
        ChessPiece chessPiece = null;
        switch (parts[1]) {
            case "PAWN": chessPiece = new Pawn(!playerBoard.playerIsWhite, pos, playerBoard); break;
            case "KNIGHT": chessPiece = new Knight(!playerBoard.playerIsWhite, pos, playerBoard); break;
            case "BISHOP": chessPiece = new Bishop(!playerBoard.playerIsWhite, pos, playerBoard); break;
            case "ROOK": chessPiece = new Rook(!playerBoard.playerIsWhite, pos, playerBoard); break;
            case "QUEEN": chessPiece = new Queen(!playerBoard.playerIsWhite, pos, playerBoard);
        }
        playerBoard.setPiece(pos, chessPiece);
        whiteTurn = !whiteTurn;
    }

    private boolean isTandemCheckMate() {
        TLobby current = SystemUtils.getInstance().getCurrentTandem();
        Player user = SystemUtils.getInstance().getUser();
        Player opp = SystemUtils.getInstance().getOpponent();
        boolean bankEmpty = isPlayerTurn() ? current.isHostTeamMember(user.getId()) : current.isHostTeamMember(opp.getId());
        boolean saviourColor = isPlayerTurn() == SystemUtils.getInstance().isWhite();
        if (tandemController.isBankEmpty(bankEmpty, saviourColor)) {
            return true;
        }

        ChessPiece saviour = null;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Position pos = new Position(i, j);
                saviour = new Queen(saviourColor, pos, playerBoard);
                if (playerBoard.getPiece(pos) == null) {
                    playerBoard.setPiece(pos, saviour);
                    if (!playerBoard.getKing(saviourColor).isInCheck()) {
                        printBoard();
                        playerBoard.setPiece(pos, null);
                        return false;
                    }
                    playerBoard.setPiece(pos, null);
                }
            }
        }
        return true;
    }

    public void printBoard() {
        printBoard(playerBoard);
    }

    public void printBoard(ChessBoard board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Position position = new Position(i, j);
                if (board.getPiece(position) != null) {
                    System.out.print(board.getPiece(position).toString() + " ");
                } else {
                    System.out.print("00 ");
                }
            }
            System.out.println();
        }
        System.out.println("-------------------");
    }

    public void printTiles() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(playerBoard.fields[i][j].type + " ");
            }
            System.out.println();
        }
    }

    public void printBlackMoves() {
        System.out.println("BLACK MOVES");
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (playerBoard.getPiece(i, j) != null && !playerBoard.getPiece(i, j).isWhite) {
                    for (int k = 0; k < 8; k++) {
                        for (int l = 0; l < 8; l++) {
                            Position pos = new Position(k, l);
                            if (playerBoard.getPiece(i, j) != null && playerBoard.getPiece(i, j).canMove(pos)) {
                                System.out.println(playerBoard.getPiece(i, j).toString() + " at " + i + " " + j + " can move to: " + pos);
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean isCastlingLocal() {
        return playerCastling.size() > 0 && oppCastling.size() > 0;
    }

    public boolean isCastling() {
        return playerCastling.size() > 0;
    }

    public List<Position> getCastling(boolean isPlayer) {
        return isPlayer ? playerCastling : oppCastling;
    }

    public boolean isPlayerTurn() {
        if (oppBoard != null) {
            return whiteTurn && playerBoard.playerIsWhite;
        }
        return whiteTurn == playerBoard.playerIsWhite;
    }

    public boolean isPlayerWhite() {
        return playerBoard.playerIsWhite;
    }

    public boolean isCheckMate() {
       if (playerBoard.isCheckMate()) {
           if (tandemController == null) {
               return true;
           }
           return isTandemCheckMate();
       }
       return false;
    }
}
