package chess.exceptions;

import chess.ChessPiece;

public class ChessException extends RuntimeException{

    public ChessException(String msg) {
        super(msg);
    }

}
