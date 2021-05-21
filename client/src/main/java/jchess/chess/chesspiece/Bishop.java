package jchess.chess.chesspiece;

import jchess.chess.board.ChessBoard;
import jchess.chess.board.Position;
import jchess.chess.board.Tile;
import jchess.chess.board.TileType;

public class Bishop extends ChessPiece {
    public Bishop(boolean isWhite, Position position, ChessBoard board) {
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

        // check for equal movement on both axis
        if (Math.abs(pos.row - to.row) == Math.abs(pos.col - to.col)) {
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
        return isWhite ? "WB" : "BB";
    }
}
