package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.exceptions.ChessException;
import chess.pieces.*;

import java.util.ArrayList;
import java.util.List;

public class ChessMatch {

    private Board board;
    private int turn = 1;
    private Color currentPLayer = Color.WHITE;
    private List<Piece> piecesOnTheBoard;
    private List<Piece> capturedPieces;
    private boolean check;
    private boolean checkMate;
    private ChessPiece enPassantVulnerable;

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

    public boolean getCheckMate() {
        return checkMate;
    }

    public ChessPiece getEnPassantVulnerable() {
        return enPassantVulnerable;
    }

    public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {

        Position source = sourcePosition.toPosition();
        Position target = targetPosition.toPosition();
        validateSourcePosition(source);

        // #special move castling
        boolean castling = false;
        if (board.piece(source) instanceof King && board.piece(target) instanceof Rook) {
            King king = (King) board.piece(source);

            if (king.testCastling(target)) {
                castling = true;
            }
        }

        if (!castling) {
            validateTargetPosition(source, target);
        }

        ChessPiece capturedPiece = (ChessPiece) makeMove(source, target, castling);

        if (testCheck(currentPLayer)) {
            undoMove(source, target, capturedPiece, castling);
            throw new ChessException("You can't put yourself in check");
        }

        ChessPiece movedPiece = (ChessPiece) board.piece(target);

        check = testCheck(opponent(currentPLayer));
        checkMate = testCheckMate(opponent(currentPLayer));

        if (!checkMate) {
            nextTurn();
        }

        // #special move En Passant
        if (movedPiece instanceof Pawn && (target.getRow() == source.getRow() - 2 || target.getRow() == source.getRow() + 2)) {
            enPassantVulnerable = movedPiece;
            System.out.println("VULNERABLE m: " + movedPiece);
        }
        else {
            enPassantVulnerable = null;
        }

        return capturedPiece;
    }

    private Piece makeMove(Position source, Position target, boolean castling) {
        Piece capturedPiece = null;
        if (castling) {
            boolean kingSideCastling = target.getColumn() > source.getColumn();

            ChessPiece switchedKing = (ChessPiece) board.removePiece(source);
            ChessPiece switchedRook = (ChessPiece) board.removePiece(target);

            if (kingSideCastling) {
                board.placePiece(switchedKing, new Position(source.getRow(), source.getColumn() + 2));
                board.placePiece(switchedRook, new Position(target.getRow(), target.getColumn() - 2));
            } else {
                board.placePiece(switchedKing, new Position(source.getRow(), source.getColumn() - 2));
                board.placePiece(switchedRook, new Position(target.getRow(), target.getColumn() + 3));
            }

            switchedKing.increaseMoveCount();
            switchedRook.increaseMoveCount();

            return null;
        } else {

            ChessPiece piece = (ChessPiece) board.removePiece(source);
            piece.increaseMoveCount();
            capturedPiece = board.removePiece(target);
            board.placePiece(piece, target);

            if (capturedPiece != null) {
                piecesOnTheBoard.remove(capturedPiece);
                capturedPieces.add(capturedPiece);
            }

            // #special move En Passant
            if (piece instanceof Pawn) {
                if (source.getColumn() != target.getColumn() && capturedPiece == null) {
                    Position pawnPosition;

                    if (piece.getColor() == Color.WHITE) {
                        pawnPosition = new Position(target.getRow() + 1, target.getColumn());
                    }
                    else {
                        pawnPosition = new Position(target.getRow() - 1, target.getColumn());
                    }

                    capturedPiece = board.removePiece(pawnPosition);
                    capturedPieces.add(capturedPiece);
                    piecesOnTheBoard.remove(capturedPiece);
                }
            }

            return capturedPiece;
        }
    }

    private void undoMove(Position source, Position target, Piece capturedPiece, boolean castling) {
        if (castling) {
            King king = (King) getKing(currentPLayer);
            boolean kingSideCastling = king.getChessPosition().toPosition().getColumn() > source.getColumn();
            board.removePiece(king.getChessPosition().toPosition());
            board.placePiece(king, source);
            king.decreaseMoveCount();

            Piece rook;
            if (kingSideCastling) {
                rook = board.removePiece(new Position(source.getRow(), source.getColumn() + 1));
            }
            else {
                rook =  board.removePiece(new Position(source.getRow(), source.getColumn() - 1));
            }
            board.placePiece(rook, target);

            ((ChessPiece) rook).decreaseMoveCount();
        }
        else {
            ChessPiece piece = (ChessPiece) board.removePiece(target);
            piece.decreaseMoveCount();
            board.placePiece(piece, source);

            if (capturedPiece != null) {
                board.placePiece(capturedPiece, target);
                capturedPieces.remove(capturedPiece);
                piecesOnTheBoard.add(capturedPiece);
            }
            // #special move En Passant
            if (piece instanceof Pawn) {
                if (source.getColumn() != target.getColumn() && capturedPiece == enPassantVulnerable) {
                    ChessPiece pawn = (ChessPiece) board.removePiece(target);
                    Position pawnPosition;

                    if (piece.getColor() == Color.WHITE) {
                        pawnPosition = new Position(3, target.getColumn());
                    }
                    else {
                        pawnPosition = new Position(4, target.getColumn());
                    }
                    board.placePiece(pawn, pawnPosition);
                }
            }
        }
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

    private boolean testCheckMate(Color color) {
        if (!testCheck(color)) {
            return false;
        }

        List<Piece> team = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color).toList();

        for (Piece piece : team) {

            boolean[][] possibleMoves = piece.possibleMoves();
            for (int i=0; i<board.getRows(); i++) {
                for (int j=0; j<board.getColumns(); j++) {
                    if (possibleMoves[i][j]) {
                        Position source = ((ChessPiece) piece).getChessPosition().toPosition();
                        Position target = new Position(i, j);
                        Piece capturedPiece = makeMove(source, target, false);
                        boolean testCheck = testCheck(color);
                        undoMove(source, target, capturedPiece, false);
                        if (!testCheck) {
                            return false;
                        }

                    }

                }

            }

        }

        return true;
    }

    private Color opponent(Color currentPLayer) {
        return currentPLayer == Color.WHITE ? Color.BLACK : Color.WHITE;
    }

    private void initialSetup() {

        placeNewPiece('a', 1, new Rook(board, Color.WHITE));
        placeNewPiece('b', 1, new Knight(board, Color.WHITE));
        placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('d', 1, new Queen(board, Color.WHITE));
        placeNewPiece('e', 1, new King(board, Color.WHITE, this));
        placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('g', 1, new Knight(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));
        placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));

        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('b', 8, new Knight(board, Color.BLACK));
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('d', 8, new Queen(board, Color.BLACK));
        placeNewPiece('e', 8, new King(board, Color.BLACK, this));
        placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('g', 8, new Knight(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
        placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));

//        placeNewPiece('a', 1, new Rook(board, Color.WHITE));
//        placeNewPiece('e', 1, new King(board, Color.WHITE, this));
//        placeNewPiece('h', 1, new Rook(board, Color.WHITE));
//        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
//        placeNewPiece('d', 8, new Queen(board, Color.BLACK));
//        placeNewPiece('b', 8, new King(board, Color.BLACK, this));
//        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
    }

}
