package jchess.chess.board;

import jchess.chess.chesspiece.ChessPiece;

public class Tile {
    public ChessPiece chessPiece;
    public TileType type;

    public Tile(ChessPiece chessPiece, TileType type) {
        this.chessPiece = chessPiece;
        this.type = type;
    }

    public Tile() { }

    @Override
    public String toString() {
        return "Tile{" +
                "chessPiece=" + chessPiece +
                ", type=" + type +
                '}';
    }
}
