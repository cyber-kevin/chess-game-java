package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.exceptions.ChessException;
import chess.pieces.King;
import chess.pieces.Rook;

import java.util.ArrayList;
import java.util.List;

public class ChessMatch {

    private Board board;
    private int turn = 1;
    private Color currentPLayer = Color.WHITE;
    private List<Piece> piecesOnTheBoard;
    private List<Piece> capturedPieces;

    private boolean check;

    public ChessMatch() {
        board = new Board(8, 8);
        piecesOnTheBoard = new ArrayList<>();
        capturedPieces = new ArrayList<>();
        initialSetup();
    }

    public ChessPiece[][] getPieces() {
        ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];

        for(int i=0; i < board.getRows(); i++) {
            for(int j=0; j < board.getColumns(); j++) {
                mat[i][j] = (ChessPiece) board.piece(i, j);
            }
        }

        return mat;
    }

    public int getTurn() {
        return turn;
    }

    public Color getCurrentPLayer() {
        return currentPLayer;
    }

    public boolean getCheck() {
        return check;
    }

    public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
        Position source = sourcePosition.toPosition();
        Position target = targetPosition.toPosition();
        validateSourcePosition(source);
        validateTargetPosition(source, target);

        Piece capturedPiece = makeMove(source, target);

        if (testCheck(currentPLayer)) {
            undoMove(source, target, capturedPiece);
            throw new ChessException("You can't put yourself in check");
        }

        check = testCheck(opponent(currentPLayer));

        nextTurn();

        return (ChessPiece) capturedPiece;
    }

    private Piece makeMove(Position source, Position target) {
        Piece piece = board.removePiece(source);
        Piece capturedPiece = board.removePiece(target);
        board.placePiece(piece, target);

        if (capturedPiece != null) {
            piecesOnTheBoard.remove(capturedPiece);
            capturedPieces.add(capturedPiece);
        }

        return capturedPiece;
    }

    private void undoMove(Position source, Position target, Piece capturedPiece) {
        Piece piece = board.removePiece(target);
        board.placePiece(piece, source);

        if (capturedPiece != null)
            board.placePiece(capturedPiece, target);
    }

    private void nextTurn() {
        boolean wasBlack = currentPLayer == Color.BLACK;

        if (wasBlack) {
            currentPLayer = Color.WHITE;
            turn++;
        }
        else {
            currentPLayer = Color.BLACK;
        }
    }

    private void validateSourcePosition(Position position) {
        if (!board.thereIsAPiece(position))
            throw new ChessException("There is no piece on source position");
        if (currentPLayer != ((ChessPiece) board.piece(position)).getColor())
            throw new ChessException("The chosen piece is not yours");
        if (!board.piece(position).isThereAnyPossibleMove())
            throw new ChessException("There is no possible moves for the chosen piece");
    }

    private void validateTargetPosition(Position source, Position target) {
        if (!board.piece(source).possibleMove(target)) {
            throw new ChessException("The chosen piece can't move to target position");
        }
    }

    public boolean[][] possibleMoves(ChessPosition sourcePosition) {
        Position position = sourcePosition.toPosition();
        validateSourcePosition(position);

        return board.piece(position).possibleMoves();
    }

    private void placeNewPiece(char column, int row, ChessPiece piece) {
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
        piecesOnTheBoard.add(piece);
    }

    private ChessPiece getKing(Color color) {
        List<Piece> pieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color).toList();

        for (Piece piece : pieces) {
            if (piece instanceof King)
                return (ChessPiece) piece;
        }

        throw new IllegalStateException("There is no " + color.toString() + " king on the board");
    }

    private boolean testCheck(Color color) {
        Position kingPosition = getKing(color).getChessPosition().toPosition();

        for (Piece piece: piecesOnTheBoard) {
            if (((ChessPiece) piece).getColor() != color) {
                if (piece.possibleMoves()[kingPosition.getRow()][kingPosition.getColumn()])
                   return true;
            }
        }

        return false;
    }

    private Color opponent(Color currentPLayer) {
        return currentPLayer == Color.WHITE ? Color.BLACK : Color.WHITE;
    }

    private void initialSetup() {
        placeNewPiece('a', 1, new Rook(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));
        placeNewPiece('e', 1, new King(board, Color.WHITE));
        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
        placeNewPiece('e', 8, new King(board, Color.BLACK));
    }

}
