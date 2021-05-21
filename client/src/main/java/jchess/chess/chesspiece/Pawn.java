package jchess.chess.chesspiece;

import javafx.geometry.Pos;
import jchess.chess.board.ChessBoard;
import jchess.chess.board.Position;
import jchess.chess.board.Tile;

public class Pawn extends ChessPiece {
    public Pawn(boolean isWhite, Position position, ChessBoard board) {
        super(isWhite, position, board);
    }

    @Override
    public boolean canMove(Position to) {
        // our piece in target position
        if (board.getPiece(to) != null && board.getPiece(to).isWhite == this.isWhite) {
            return false;
        }

        if (checkKingInCheck) {
            Position tmpPos = new Position(this.pos.row, this.pos.col);
            ChessPiece tmpPiece = board.getPiece(to);

            board.setPiece(to, this);
            board.setPiece(tmpPos, null);

            if (board.getKing(isWhite) != null && board.getKing(isWhite).isInCheck()) {
                board.setPiece(to, tmpPiece);
                board.setPiece(tmpPos, this);
                return false;
            }

            board.setPiece(to, tmpPiece);
            board.setPiece(tmpPos, this);
        }

        checkKingInCheck = true;

        // can't kill a piece moving forward
        if (board.getPiece(to) != null && pos.col == to.col) {
            return false;
        }

        if (isFirstTime && pos.col == to.col && Math.abs(to.row - pos.row) <= 2) {
            if (to.row < pos.row && isWhite == board.playerIsWhite) {
                return true;
            } else {
                return to.row > pos.row && isWhite != board.playerIsWhite;
            }
        } else if (pos.col == to.col && Math.abs(pos.row - to.row) < 2) {
            if (to.row < pos.row && isWhite == board.playerIsWhite) {
                return true;
            } else {
                return to.row > pos.row && isWhite != board.playerIsWhite;
            }
        } else if (to.col == pos.col - 1 || to.col == pos.col + 1) {
            if (board.getPiece(to) != null && board.getPiece(to).isWhite != this.isWhite) {
                return this.isWhite == board.playerIsWhite ? to.row == pos.row - 1 : to.row == pos.row + 1;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return isWhite ? "WP" : "BP";
    }
}
