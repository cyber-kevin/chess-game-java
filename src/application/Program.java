package application;

import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.exceptions.ChessException;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Program {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        ChessMatch chessMatch = new ChessMatch();
        List<ChessPiece> capturedPieces = new ArrayList<>();

        while (!chessMatch.getCheckMate()) {
            try {
                UI.clearScreen();
                UI.printMatch(chessMatch, capturedPieces);
                System.out.println();
                System.out.print("Source: ");
                ChessPosition source = UI.readChessPosition(input);

                boolean[][] possibleMoves = chessMatch.possibleMoves(source);
                UI.clearScreen();
                UI.printBoard(chessMatch.getPieces(), possibleMoves);

                System.out.print("Target: ");
                ChessPosition target = UI.readChessPosition(input);
                ChessPiece capturedPiece = chessMatch.performChessMove(source, target);
                if (capturedPiece != null)
                    capturedPieces.add(capturedPiece);

                if (chessMatch.getPromoted() != null) {
                    System.out.println();
                    System.out.println("** PROMOTED " + chessMatch.getPromoted().getColor() + " PAWN **");

                    boolean invalidPromotion = true;

                    while (invalidPromotion) {
                        try {
                            System.out.print("Choose between Rook (R), Knight (N), Bishop (B) or Queen (Q) to promote your pawn: ");
                            char promoteTo = input.nextLine().charAt(0);
                            chessMatch.replacePromotedPiece(chessMatch.getPromoted(), promoteTo);
                            invalidPromotion = false;
                        } catch (InvalidParameterException e) {
                            System.out.println();
                            System.out.println(e.getMessage());
                            input.nextLine();
                        }

                    }
                }

            }
            catch (ChessException | InputMismatchException e) {
                System.out.println();
                System.out.println(e.getMessage());
                input.nextLine();
            }
        }

        UI.clearScreen();
        UI.printMatch(chessMatch, capturedPieces);




    }

}
