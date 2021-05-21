package jchess.chess.chesspiece;

import jchess.chess.board.ChessBoard;
import jchess.chess.board.Position;
import jchess.chess.board.Tile;

public class Knight extends ChessPiece {
    private final int[] rowMoves = new int[] {-1, -2, -2, -1, +1, +2, +2, +1};
    private final int[] colMoves = new int[] {+2, +1, -1, -2, -2, -1, +1, +2};

    public Knight(boolean isWhite, Position position, ChessBoard board) {
        super(isWhite, position, board);
    }

    @Override
    public boolean canMove(Position to) {
        // our piece in target position
        if (board.getPiece(to) != null && board.getPiece(to).isWhite == isWhite) {
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

        // search for possible row and col movement combinations
        for (int i = 0; i < 8; i++) {
            if (pos.col + colMoves[i] == to.col && pos.row + rowMoves[i] == to.row) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return isWhite ? "Wk" : "Bk";
    }
}
