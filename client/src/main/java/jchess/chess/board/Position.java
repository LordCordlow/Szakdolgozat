package jchess.chess.board;

public class Position {
    public int row;
    public int col;

    public Position(int rowPos, int colPos) {
        this.row = rowPos;
        this.col = colPos;
    }

    public Position() { }

    @Override
    public String toString() {
        return "Position{" +
                "rowPos=" + row +
                ", colPos=" + col +
                '}';
    }
}
