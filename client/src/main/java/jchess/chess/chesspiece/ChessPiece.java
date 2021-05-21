package jchess.chess.chesspiece;

import javafx.geometry.Pos;
import jchess.chess.board.ChessBoard;
import jchess.chess.board.Position;

import java.util.ArrayList;
import java.util.List;

public abstract class ChessPiece {
    public boolean isWhite;
    public boolean isFirstTime;
    public boolean isSaviour;
    public Position pos;
    public ChessBoard board;
    public boolean checkKingInCheck = true;
    protected List<Position> possibleMoves;

    public ChessPiece(boolean isWhite, Position pos, ChessBoard board) {
        this.isFirstTime = true;
        this.isSaviour = true;
        this.isWhite = isWhite;
        this.pos = pos;
        this.board = board;
        this.possibleMoves = new ArrayList<>();
    }

    public ChessPiece() { }

    public abstract boolean canMove(Position to);

    public boolean canMove(Position to, boolean checkKingInCheck) {
        this.checkKingInCheck = checkKingInCheck;
        return canMove(to);
    }

    public void move(Position to) {
        this.pos.row = to.row;
        this.pos.col = to.col;
    }
}
