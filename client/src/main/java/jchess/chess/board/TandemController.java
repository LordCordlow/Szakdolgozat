package jchess.chess.board;

import javafx.application.Platform;
import jchess.chess.chesspiece.*;
import jchess.controller.connection.SocketController;
import jchess.model.Player;
import jchess.model.TLobby;
import jchess.utils.SystemUtils;
import jchess.utils.WindowUtils;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TandemController {
    private ChessBoard board;
    private Map<ColoredPieceType, Integer> hostTeamBank;
    private Map<ColoredPieceType, Integer> oppTeamBank;

    public TandemController(ChessBoard board) {
        this.board = board;
        this.hostTeamBank = new HashMap<>();
        this.oppTeamBank = new HashMap<>();

        this.hostTeamBank.put(ColoredPieceType.WHITE_PAWN, 0);
        this.hostTeamBank.put(ColoredPieceType.WHITE_ROOK, 0);
        this.hostTeamBank.put(ColoredPieceType.WHITE_BISHOP, 0);
        this.hostTeamBank.put(ColoredPieceType.WHITE_KNIGHT, 0);
        this.hostTeamBank.put(ColoredPieceType.WHITE_QUEEN, 0);
        this.hostTeamBank.put(ColoredPieceType.BLACK_PAWN, 0);
        this.hostTeamBank.put(ColoredPieceType.BLACK_ROOK, 0);
        this.hostTeamBank.put(ColoredPieceType.BLACK_BISHOP, 0);
        this.hostTeamBank.put(ColoredPieceType.BLACK_KNIGHT, 0);
        this.hostTeamBank.put(ColoredPieceType.BLACK_QUEEN, 0);

        this.oppTeamBank.put(ColoredPieceType.WHITE_PAWN, 0);
        this.oppTeamBank.put(ColoredPieceType.WHITE_ROOK, 0);
        this.oppTeamBank.put(ColoredPieceType.WHITE_BISHOP, 0);
        this.oppTeamBank.put(ColoredPieceType.WHITE_KNIGHT, 0);
        this.oppTeamBank.put(ColoredPieceType.WHITE_QUEEN, 0);
        this.oppTeamBank.put(ColoredPieceType.BLACK_PAWN, 0);
        this.oppTeamBank.put(ColoredPieceType.BLACK_ROOK, 0);
        this.oppTeamBank.put(ColoredPieceType.BLACK_BISHOP, 0);
        this.oppTeamBank.put(ColoredPieceType.BLACK_KNIGHT, 0);
        this.oppTeamBank.put(ColoredPieceType.BLACK_QUEEN, 0);
    }

    public ColoredPieceType addPieceToBank(String pieceStr, String teamStr) {
        ColoredPieceType pieceToBank = ColoredPieceType.valueOf(pieceStr);
        System.out.println("piece to bank: " + pieceToBank);
        if (teamStr.equals("host")) {
            System.out.println(pieceToBank + " added to hostTeamBank");
            Integer prev = hostTeamBank.get(pieceToBank);
            prev++;
            hostTeamBank.replace(pieceToBank, prev);
        } else {
            System.out.println(pieceToBank + " added to oppTeamBank");
            Integer prev = oppTeamBank.get(pieceToBank);
            prev++;
            oppTeamBank.replace(pieceToBank, prev);
        }
        return pieceToBank;
    }

    public void removePieceFromBank(ColoredPieceType piece, boolean isHostTeam) {
        if (isHostTeam) {
            Integer prev = hostTeamBank.get(piece);
            prev = Math.max(prev - 1, 0);
            hostTeamBank.replace(piece, prev);
            System.out.println(piece + " removed from hostTeamBank, remaining in bank: " + prev);
        } else {
            Integer prev = oppTeamBank.get(piece);
            prev = Math.max(prev - 1, 0);
            oppTeamBank.replace(piece, prev);
            System.out.println(piece + " removed from oppTeamBank, remaining in bank: " + prev);
        }
    }

    public void checkKill(Position from, Position to) {
        if (board.playerIsWhite != board.getPiece(from).isWhite) {
            return;
        }
        if (board.getPiece(to) == null) {
            return;
        }

        ColoredPieceType piece = null;
        if (board.getPiece(from).isWhite) {
            if (board.getPiece(to) instanceof Pawn) {
                piece = ColoredPieceType.BLACK_PAWN;
            } else if (board.getPiece(to) instanceof Knight) {
                piece = ColoredPieceType.BLACK_KNIGHT;
            } else if (board.getPiece(to) instanceof Bishop) {
                piece = ColoredPieceType.BLACK_BISHOP;
            } else if (board.getPiece(to) instanceof Queen) {
                piece = ColoredPieceType.BLACK_QUEEN;
            }
        } else {
            if (board.getPiece(to) instanceof Pawn) {
                piece = ColoredPieceType.WHITE_PAWN;
            } else if (board.getPiece(to) instanceof Knight) {
                piece = ColoredPieceType.WHITE_KNIGHT;
            } else if (board.getPiece(to) instanceof Bishop) {
                piece = ColoredPieceType.WHITE_BISHOP;
            } else if (board.getPiece(to) instanceof Queen) {
                piece = ColoredPieceType.WHITE_QUEEN;
            }
        }
        if (piece != null) {
            Player user = SystemUtils.getInstance().getUser();
            TLobby lobby = SystemUtils.getInstance().getCurrentTandem();
            if (user.getId() == lobby.getHostTeam().get(0).getId() || user.getId() == lobby.getHostTeam().get(1).getId()) {
                System.out.println(piece + " added to hostTeamBank");
                Integer prev = hostTeamBank.get(piece);
                prev++;
                hostTeamBank.replace(piece, prev);
            } else if (user.getId() == lobby.getOppTeam().get(0).getId() || user.getId() == lobby.getOppTeam().get(1).getId()) {
                System.out.println(piece + " added to oppTeamBank");
                Integer prev = oppTeamBank.get(piece);
                prev++;
                oppTeamBank.replace(piece, prev);
            }
            WindowUtils.getInstance().getFxController().updateBank();
            try {
                SocketController.getInstance().sendPieceToBank(piece);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Integer getPieceCount(ColoredPieceType piece, boolean isHostTeam) {
        return isHostTeam ? hostTeamBank.get(piece) : oppTeamBank.get(piece);
    }

    // empty -> no piece with the players color
    public boolean isBankEmpty(boolean isHostTeam, boolean isWhite) {
        List<ColoredPieceType> bank = isHostTeam ? new ArrayList<>(hostTeamBank.keySet()) : new ArrayList<>(oppTeamBank.keySet());
        for (ColoredPieceType p : bank) {
            if (p.name().split("_")[0].equals("WHITE") && isWhite) {
                return false;
            }
        }
        return true;
    }

    public ColoredPieceType[] getBank(boolean isHostTeam) {
        return isHostTeam ? (ColoredPieceType[]) hostTeamBank.keySet().toArray() : (ColoredPieceType[]) oppTeamBank.keySet().toArray();
    }

    public boolean canPlace(Position from) {
        return board.getPiece(from) == null;
    }
}
