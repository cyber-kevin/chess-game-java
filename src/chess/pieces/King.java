package chess.pieces;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {

    private ChessMatch chessMatch;

    public King(Board board, Color color, ChessMatch chessMatch) {
        super(board, color);
        this.chessMatch = chessMatch;
    }

    @Override
    public String toString() {
        return "K";
    }

    public boolean canMove(Position position) {
        ChessPiece p = (ChessPiece) getBoard().piece(position);
        return p == null || p.getColor() != getColor();
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];

        Position p = new Position(0, 0);

        //above
        p.setValues(position.getRow() - 1, position.getColumn() - 1);
        for(int i=0; i<3; i++) {
            if (getBoard().positionExists(p) && canMove(p)) {
                mat[p.getRow()][p.getColumn()] = true;
            }
            p.setColumn(p.getColumn() + 1);
        }

        //same row
        p.setValues(position.getRow(), position.getColumn() - 1);
        for(int i=0; i<3; i++) {
            if (getBoard().positionExists(p) && canMove(p)) {
                mat[p.getRow()][p.getColumn()] = true;
            }
            p.setColumn(p.getColumn() + 1);
        }

        //below
        p.setValues(position.getRow() + 1, position.getColumn() - 1);
        for(int i=0; i<3; i++) {
            if (getBoard().positionExists(p) && canMove(p)) {
                mat[p.getRow()][p.getColumn()] = true;
            }
            p.setColumn(p.getColumn() + 1);
        }

        return mat;
    }

    public boolean testCastling(Position p) {
        ChessPiece piece = (ChessPiece) getBoard().piece(p);
        Piece[][] pieces = chessMatch.getPieces();

        if (p.getColumn() < position.getColumn()) {
            for (int i = position.getColumn() - 1; i > p.getColumn(); i--) {
                if (pieces[position.getRow()][i] != null) {
                    return false;
                }
            }
        }
        else {
            for (int i = position.getColumn() + 1; i < p.getColumn(); i++) {
                if (pieces[position.getRow()][i] != null) {
                    return false;
                }
            }
        }

        return getMoveCount() == 0 && piece instanceof Rook && (piece.getColor() == getColor()) && piece.getMoveCount() == 0;
    }

}
