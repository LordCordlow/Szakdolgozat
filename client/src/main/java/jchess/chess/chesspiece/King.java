package jchess.chess.chesspiece;

import jchess.chess.board.BoardController;
import jchess.chess.board.ChessBoard;
import jchess.chess.board.Position;
import jchess.chess.board.Tile;

public class King extends ChessPiece {
    public boolean inCheck = false;
    public King(boolean isWhite, Position position, ChessBoard board) {
        super(isWhite, position, board);
    }

    @Override
    public boolean canMove(Position to) {
        if (canCastle()) {
            if (board.playerIsWhite) {
                if (to.col == 2) {
                    for (int i = pos.col - 1; i > 0; i--) {
                        if (board.getPiece(pos.row, i) != null) {
                            return false;
                        }
                    }

                    for (int i = pos.col - 1; i > 0; i--) {
                        Position tmpPos = new Position(this.pos.row, this.pos.col);
                        ChessPiece tmpPiece = board.getPiece(to.row, i);

                        Position mvPos = new Position(to.row, i);
                        board.setPiece(mvPos, this);
                        board.setPiece(tmpPos, null);

                        if (this.isInCheck()) {
                            board.setPiece(mvPos, tmpPiece);
                            board.setPiece(tmpPos, this);
                            return false;
                        }

                        board.setPiece(mvPos, tmpPiece);
                        board.setPiece(tmpPos, this);
                    }
                    return true;
                } else if (to.col == 6) {
                    for (int i = pos.col + 1; i < 7; i++) {
                        if (board.getPiece(to.row, i) != null) {
                            return false;
                        }
                    }

                    for (int i = pos.col + 1; i < 7; i++) {
                        Position tmpPos = new Position(pos.row, pos.col);
                        ChessPiece tmpPiece = board.getPiece(pos.row, i);

                        Position mvPos = new Position(pos.row, i);
                        board.setPiece(mvPos, this);
                        board.setPiece(tmpPos, null);

                        if (this.isInCheck()) {
                            board.setPiece(mvPos, tmpPiece);
                            board.setPiece(tmpPos, this);
                            return false;
                        }

                        board.setPiece(mvPos, tmpPiece);
                        board.setPiece(tmpPos, this);
                    }
                    return true;
                }
            } else {
                if (to.col == 1) {
                    for (int i = pos.col - 1; i > 0; i--) {
                        if (board.getPiece(pos.row, i) != null) {
                            return false;
                        }
                    }

                    for (int i = pos.col - 1; i > 0; i--) {
                        Position tmpPos = new Position(this.pos.row, this.pos.col);
                        ChessPiece tmpPiece = board.getPiece(to.row, i);

                        Position mvPos = new Position(to.row, i);
                        board.setPiece(mvPos, this);
                        board.setPiece(tmpPos, null);

                        if (this.isInCheck()) {
                            board.setPiece(mvPos, tmpPiece);
                            board.setPiece(tmpPos, this);
                            return false;
                        }

                        board.setPiece(mvPos, tmpPiece);
                        board.setPiece(tmpPos, this);
                    }
                    return true;
                } else if (to.col == 5) {
                    for (int i = pos.col + 1; i < 5; i++) {
                        if (board.getPiece(to.row, i) != null) {
                            return false;
                        }
                    }

                    for (int i = pos.col + 1; i < 5; i++) {
                        Position tmpPos = new Position(pos.row, pos.col);
                        ChessPiece tmpPiece = board.getPiece(pos.row, i);

                        Position mvPos = new Position(pos.row, i);
                        board.setPiece(mvPos, this);
                        board.setPiece(tmpPos, null);

                        if (this.isInCheck()) {
                            board.setPiece(mvPos, tmpPiece);
                            board.setPiece(tmpPos, this);
                            return false;
                        }

                        board.setPiece(mvPos, tmpPiece);
                        board.setPiece(tmpPos, this);
                    }
                    return true;
                }
            }

        }

        if (board.getPiece(to) != null && board.getPiece(to).isWhite == this.isWhite) {
            return false;
        }

        ChessPiece toTmp = board.getPiece(to);
        Position tmpPos = new Position(this.pos.row, this.pos.col);
        board.setPiece(to, this);
        board.setPiece(tmpPos, null);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.getPiece(i, j) != null && !(board.getPiece(i, j) instanceof King) && board.getPiece(i, j).isWhite != this.isWhite && board.getPiece(i, j).canMove(to)) {
                    board.setPiece(to, toTmp);
                    board.setPiece(tmpPos, this);
                    return false;
                }
            }
        }
        board.setPiece(to, toTmp);
        board.setPiece(tmpPos, this);

        if (pos.col == to.col) {
            return Math.abs(pos.row - to.row) <= 1;
        } else if (pos.row == to.row) {
            return Math.abs(pos.col - to.col) <= 1;
        } else if (Math.abs(pos.col - to.col) + Math.abs(pos.row - to.row) > 2) {
            return false;
        }

        return true;
    }

    public boolean canCastleKingSide() {
        if (canCastle()) {
            if (isWhite == board.playerIsWhite && board.getPiece(0, 6) != null) {
                return board.getPiece(0, 6).isFirstTime;
            } else {
                if (board.getPiece(7, 6) != null) {
                    return board.getPiece(7, 6).isFirstTime;
                }
            }
        }
        return false;
    }

    public boolean canCastleQueenSide() {
        if (canCastle()) {
            if (isWhite == board.playerIsWhite && board.getPiece(0, 2) != null) {
                return board.getPiece(7, 0).isFirstTime;
            } else {
                return board.getPiece(7, 7).isFirstTime;
            }
        }
        return false;
    }

    public boolean canCastle() {
        if (isFirstTime && !isInCheck()) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (board.getPiece(i, j) instanceof Rook && board.getPiece(i, j).isWhite == this.isWhite && board.getPiece(i, j).isFirstTime) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isInCheck() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.getPiece(i, j) != null && !(board.getPiece(i, j) instanceof King) && board.getPiece(i, j) != this && board.getPiece(i, j).isWhite != this.isWhite && board.getPiece(i, j).canMove(this.pos, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isInCheckMate() {
        if (this.isInCheck()) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    Position pos = new Position(i, j);
                    if (this.canMove(pos)) {
                        ChessPiece tmpPiece = board.getPiece(pos);
                        Position tmpPos = new Position(this.pos.row, this.pos.col);

                        board.setPiece(pos, this);
                        board.setPiece(tmpPos, null);
                        if (!this.isInCheck()) {
                            board.setPiece(pos, tmpPiece);
                            board.setPiece(tmpPos, this);
                            return false;
                        }
                        board.setPiece(pos, tmpPiece);
                        board.setPiece(tmpPos, this);
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return isWhite ? "WK" : "BK";
    }
}
