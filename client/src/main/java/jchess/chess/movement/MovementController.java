package jchess.chess.movement;

import jchess.chess.board.ChessBoard;
import jchess.chess.board.Position;

public class MovementController {
    public ChessBoard board;

    public MovementController(ChessBoard board) {
        this.board = board;
    }

    public boolean canMove() {
        return true;
    }

    public int move(Position from, Position to) {
//        if (board.fields[from.x][from.y].chessPiece.isWhite == board.playerIsWhite) {
//            if (board.fields[to.x][to.y].chessPiece == null ||
//                board.fields[to.x][to.y].chessPiece.isWhite != board.playerIsWhite
//            ) {
//                board.fields[to.x][to.y].chessPiece = board.fields[from.x][from.y].chessPiece;
//                board.fields[from.x][from.y].chessPiece = null;
//                return 0;
//            }
//            return 2;
//        }
//        return 1;
        return 0;
    }
}
