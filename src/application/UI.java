package application;

import chess.ChessPiece;
import chess.ChessPosition;
import chess.Color;

import java.util.InputMismatchException;
import java.util.Scanner;

public class UI {

    public static void printBoard(ChessPiece[][] pieces) {

        for (int i=0; i < pieces.length; i++) {
            System.out.print((pieces.length - i) + " ");
            for(int j=0; j < pieces.length; j++) {
                printPiece(pieces[i][j]);
            }
            System.out.println();
        }

        System.out.println("  a b c d e f g h");

    }

    public static ChessPosition readChessPosition(Scanner input) {

        try {
            String pos = input.nextLine();
            char column = pos.charAt(0);
            int row = Integer.parseInt(pos.substring(1));

            return new ChessPosition(column, row);
        }
        catch (RuntimeException e) {
            throw new InputMismatchException("Error reading ChessPosition. Valid values are from a1 to h8");
        }
    }

    private static void printPiece(ChessPiece piece) {
        if (piece == null) {
            System.out.print(".");
        }
        else {
            Color color;
            if (piece.getColor() == Color.WHITE) {
                color = Color.WHITE;
            }
            else {
                color = piece.getColor();
            }
            System.out.print(color.paint() + piece + color.reset());
        }
        System.out.print(" ");
    }

}
