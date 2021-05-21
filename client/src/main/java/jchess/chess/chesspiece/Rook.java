package jchess.chess.chesspiece;

import jchess.chess.board.ChessBoard;
import jchess.chess.board.Position;
import jchess.chess.board.Tile;

public class Rook extends ChessPiece {
    private Tile[][] fields;

    public Rook(boolean isWhite, Position position, ChessBoard board) {
        super(isWhite, position, board);
        this.fields = board.fields;
    }

    @Override
    public boolean canMove(Position to) {
        if (board.getPiece(to) != null) {
            if (board.getPiece(to).isWhite == this.isWhite) {
                return false;
            }
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
            if (pos.row > to.row) {
                for (int i = pos.row - 1; i > to.row; i--) {
                    if (board.getPiece(i, pos.col) != null) {
                        return false;
                    }
                }
            } else {
                for (int i = pos.row + 1; i < to.row; i++) {
                    if (board.getPiece(i, pos.col) != null) {
                        return false;
                    }
                }
            }
        } else if (pos.row == to.row) {
            if (pos.col > to.col) {
                for (int i = pos.col - 1; i > to.col; i--) {
                    if (board.getPiece(pos.row, i) != null) {
                        return false;
                    }
                }
            } else {
                for (int i = pos.col + 1; i < to.col; i++) {
                    if (board.getPiece(pos.row, i) != null) {
                        return false;
                    }
                }
            }
        } else if (pos.row != to.row || pos.col != to.col) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return isWhite ? "WR" : "BR";
    }
}
