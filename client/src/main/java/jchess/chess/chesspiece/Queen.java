package jchess.chess.chesspiece;

import jchess.chess.board.ChessBoard;
import jchess.chess.board.Position;
import jchess.chess.board.Tile;

public class Queen extends ChessPiece {
    public Queen(boolean isWhite, Position position, ChessBoard board) {
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

        if (pos.col == to.col) {
            // vertical movement
            for (int i = Math.min(pos.row, to.row) + 1; i < Math.max(pos.row, to.row); i++) {
                if (board.getPiece(i, pos.col) != null) {
                    return false;
                }
            }
            return true;
        } else if (pos.row == to.row) {
            // horizontal movement
            for (int i = Math.min(pos.col, to.col) + 1; i < Math.max(pos.col, to.col); i++) {
                if (board.getPiece(pos.row, i) != null) {
                    return false;
                }
            }
            return true;
        } else if (Math.abs(pos.row - to.row) == Math.abs(pos.col - to.col)) {
            // diagonal movement
            int rowMutator;
            int colMutator;

            // right or left
            if (pos.col < to.col) {
                colMutator = 1;
            } else {
                colMutator = -1;
            }
            // up or down
            if (pos.row < to.row) {
                rowMutator = 1;
            } else {
                rowMutator = -1;
            }

            int colIndex = pos.col + colMutator;
            int rowIndex = pos.row + rowMutator;

            while (colIndex != to.col && rowIndex != to.row) {
                if (board.getPiece(rowIndex, colIndex) != null && colIndex != to.col && rowIndex != to.row) {
                    return false;
                }
                colIndex += colMutator;
                rowIndex += rowMutator;
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return isWhite ? "WQ" : "BQ";
    }
}
