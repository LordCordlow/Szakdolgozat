package jchess.chess.board;

import jchess.chess.chesspiece.*;

import java.util.Arrays;

public class ChessBoard {
    public Tile[][] fields;
    public boolean playerIsWhite;

    public ChessBoard(boolean playerIsWhite) {
        this.playerIsWhite = playerIsWhite;
        fields = new Tile[8][8];
        this.pieceInit();
        this.typeInit();
    }

    // Assign the initial state of the pieces
    private void pieceInit() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Tile tile = new Tile();
                if (i == 1) {
                    tile.chessPiece = new Pawn(!playerIsWhite, new Position(i, j), this);
                } else if (i == 6) {
                    tile.chessPiece = new Pawn(playerIsWhite, new Position(i, j), this);
                } else if (i == 0) {
                    if (j == 0 || j == 7) {
                        tile.chessPiece = new Rook(!playerIsWhite, new Position(i, j), this);
                    } else if (j == 1 || j == 6) {
                        tile.chessPiece = new Knight(!playerIsWhite, new Position(i, j), this);
                    } else if (j == 2 || j == 5) {
                        tile.chessPiece = new Bishop(!playerIsWhite, new Position(i, j), this);
                    } else if (j == 3) {
                        if (playerIsWhite) {
                            tile.chessPiece = new Queen(!playerIsWhite, new Position(i, j), this);
                        } else {
                            tile.chessPiece = new King(!playerIsWhite, new Position(i, j), this);
                        }
                    } else {
                        if (playerIsWhite) {
                            tile.chessPiece = new King(!playerIsWhite, new Position(i, j), this);
                        } else {
                            tile.chessPiece = new Queen(!playerIsWhite, new Position(i, j), this);
                        }
                    }
                } else if (i == 7) {
                    if (j == 0 || j == 7) {
                        tile.chessPiece = new Rook(playerIsWhite, new Position(i, j), this);
                    } else if (j == 1 || j == 6) {
                        tile.chessPiece = new Knight(playerIsWhite, new Position(i, j), this);
                    } else if (j == 2 || j == 5) {
                        tile.chessPiece = new Bishop(playerIsWhite, new Position(i, j), this);
                    } else if (j == 3) {
                        if (playerIsWhite) {
                            tile.chessPiece = new Queen(playerIsWhite, new Position(i, j), this);
                        } else {
                            tile.chessPiece = new King(playerIsWhite, new Position(i, j), this);
                        }
                    } else {
                        if (playerIsWhite) {
                            tile.chessPiece = new King(playerIsWhite, new Position(i, j), this);
                        } else {
                            tile.chessPiece = new Queen(playerIsWhite, new Position(i, j), this);
                        }
                    }
                } else {
                    tile.chessPiece = null;
                }
                fields[i][j] = tile;
            }
        }
    }

    // Assign TileTypes for each tile
    private void typeInit() {
        TileType[] types = TileType.values();
        TileType[][] tileMatrix = new TileType[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                tileMatrix[i][j] = types[(i * 8) + j];
            }
        }

        // if player is black need to rotate the tileType matrix
        if (!this.playerIsWhite) {
            rotate180degree(tileMatrix);
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                fields[i][j].type = tileMatrix[i][j];
            }
        }
    }

    public TileType getFieldType(Position position) {
        return fields[position.row][position.col].type;
    }

    public ChessPiece getPiece(Position position) {
        return fields[position.row][position.col].chessPiece;
    }

    public ChessPiece getPiece(int i, int j) {
        return fields[i][j].chessPiece;
    }

    public King getKing(boolean isWhite) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (fields[i][j].chessPiece != null && fields[i][j].chessPiece instanceof King && fields[i][j].chessPiece.isWhite == isWhite) {
                    return (King) fields[i][j].chessPiece;
                }
            }
        }
        return null;
    }

    public void setPiece(Position position, ChessPiece chessPiece) {
        if (chessPiece != null) {
            chessPiece.pos = position;
        }
        fields[position.row][position.col].chessPiece = chessPiece;
    }

    public boolean isCheckMate() {
        boolean isCheckMate = true;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (getPiece(i, j) != null && !getPiece(i, j).isWhite) {
                    for (int k = 0; k < 8; k++) {
                        for (int l = 0; l < 8; l++) {
                            Position pos = new Position(k, l);
                            if (getPiece(i, j) != null && getPiece(i, j).canMove(pos)) {
                                isCheckMate = false;
                            }
                        }
                    }
                }
            }
        }
        return isCheckMate && (getKing(true).isInCheckMate() ^ getKing(false).isInCheckMate());
    }

    private void rotate180degree(TileType[][] matrix) {
        transpose(matrix);
        reverseColumns(matrix);
        transpose(matrix);
        reverseColumns(matrix);
    }

    private void reverseColumns(TileType[][] matrix) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0, k = 7; j < k; j++, k--) {
                TileType tmp = matrix[j][i];
                matrix[j][i] = matrix[k][i];
                matrix[k][i] = tmp;
            }
        }
    }

    private void transpose(TileType[][] matrix) {
        for (int i = 0; i < 8; i++) {
            for (int j = i; j < 8; j++) {
                TileType tmp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = tmp;
            }
        }
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "fields=" + Arrays.toString(fields) +
                ", playerIsWhite=" + playerIsWhite +
                '}';
    }
}
