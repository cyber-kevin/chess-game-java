package application;

import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.Color;

import java.io.IOException;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

public class UI {

    public static void printMatch(ChessMatch chessMatch) {
        printBoard(chessMatch.getPieces());
        System.out.println();
        System.out.println("Turn: " + chessMatch.getTurn());
        System.out.println("Waiting player: " + chessMatch.getCurrentPLayer());
    }

    public static void printBoard(ChessPiece[][] pieces) {

        for (int i=0; i < pieces.length; i++) {
            System.out.print((pieces.length - i) + " ");
            for(int j=0; j < pieces.length; j++) {
                printPiece(pieces[i][j], false);
            }
            System.out.println();
        }

        System.out.println("  a b c d e f g h");

    }

    public static void printBoard(ChessPiece[][] pieces, boolean[][] possibleMoves) {

        for (int i=0; i < pieces.length; i++) {
            System.out.print((pieces.length - i) + " ");
            for(int j=0; j < pieces.length; j++) {
                printPiece(pieces[i][j], possibleMoves[i][j]);
            }
            System.out.println();
        }

        System.out.println("  a b c d e f g h");
    }

    public static void clearScreen() {
        System.out.println("\033[H\033[2J");
        System.out.flush();
    }

    public static ChessPosition readChessPosition(Scanner input) {

        try {
            String pos = input.nextLine();
            char column = Character.toLowerCase(pos.charAt(0));
            int row = Integer.parseInt(String.valueOf(pos.charAt(1)));

            return new ChessPosition(column, row);
        }
        catch (RuntimeException e) {
            throw new InputMismatchException("Error reading ChessPosition. Valid values are from a1 to h8");
        }
    }

    private static void printPiece(ChessPiece piece, boolean possibleMove) {

        Color background = Color.EMPTY;

        if (possibleMove) {
            background = Color.CYAN_BACKGROUND;
        }

        if (piece == null) {
           System.out.print(background.paint() + "." + background.reset());
        }
        else {
            Color color;
            if (piece.getColor() == Color.WHITE) {
                color = Color.WHITE;
            }
            else {
                color = piece.getColor();
            }
            System.out.print(background.paint() + color.paint() + piece + color.reset() + background.reset());
        }
        System.out.print(" ");
    }

}
