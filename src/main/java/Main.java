import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class Main {

    static String hits = "", misses = "";

    public static void main(String[] args) throws IOException {

        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        TextGraphics tGraphics = screen.newTextGraphics();
        String solution;
        int misses = 0;

        screen.startScreen();
        screen.setCursorPosition(null);

        solution = readSolution(screen, tGraphics);

        initHits(solution);

        printHangman(screen, tGraphics, misses);
        printHitsAndMisses(screen, tGraphics);
        while (misses < 9) {
            misses = nextGuess(screen, solution, misses);

            printHangman(screen, tGraphics, misses);

            printHitsAndMisses(screen, tGraphics);

            printFinalResult(screen, tGraphics, solution, misses);
        }
    }

    private static String readSolution(Screen screen, TextGraphics tGraphics) throws IOException {
        KeyStroke solution;
        String solutionString = "";

        tGraphics.putString(2, 1, "Please enter the secret word and press \"Enter\" afterwards.");
        screen.refresh();

        solution = screen.readInput();
        while (solution.getKeyType() != KeyType.Enter) {
            solutionString += solution.getCharacter();
            solution = screen.readInput();
        }

        return solutionString.toLowerCase();
    }

    private static void initHits(String secretWord) {
        for (int i = 0; i < secretWord.length(); i++) {
            hits += "_ ";
        }
    }

    private static int nextGuess(Screen screen, String solution, int faultNum) throws IOException {
        String guess = "";

        guess += screen.readInput().getCharacter();
        guess = guess.toLowerCase();

        if (solution.toLowerCase().contains(guess)) {
            for (int i = 0; i < solution.length(); i++) {
                if (solution.toLowerCase().charAt(i) == guess.charAt(0)) {
                    hits = hits.substring(0, i * 2) + guess + hits.substring(i * 2 + 1, hits.length());
                }
            }
        } else {
            faultNum++;

            if (faultNum > 1) {
                misses += ", " + guess;
            } else {
                misses += guess;
            }
        }

        return faultNum;
    }

    private static void printHangman(Screen screen, TextGraphics tGraphics, int faultNum) throws IOException {
        screen.clear();
        screen.refresh();

        tGraphics.drawLine(10, 18, 35, 18, Symbols.BLOCK_SOLID);
        screen.refresh();

        for (int i = 1; i <= faultNum; i++) {
            switch (i) {
                case 1:
                    tGraphics.drawLine(30, 17, 30, 1, Symbols.BLOCK_SOLID);
                    tGraphics.drawLine(31, 17, 31, 1, Symbols.BLOCK_SOLID);
                    break;
                case 2:
                    tGraphics.drawLine(29, 1, 15, 1, Symbols.BLOCK_SOLID);
                    break;
                case 3:
                    tGraphics.putString(15, 2, String.valueOf(Symbols.BLOCK_SOLID));
                    break;
                case 4:
                    tGraphics.drawRectangle(new TerminalPosition(13, 3), new TerminalSize(5, 3), '*');
                    break;
                case 5:
                    tGraphics.drawLine(15, 6, 15, 11, Symbols.SINGLE_LINE_VERTICAL);
                    break;
                case 6:
                    tGraphics.drawLine(12, 8, 14, 8, Symbols.SINGLE_LINE_HORIZONTAL);
                    break;
                case 7:
                    tGraphics.drawLine(16, 8, 18, 8, Symbols.SINGLE_LINE_HORIZONTAL);
                    break;
                case 8:
                    tGraphics.drawLine(14, 12, 11, 15, '/');
                    break;
                case 9:
                    tGraphics.drawLine(16, 12, 19, 15, '\\');
                    break;
            }
        }
        screen.refresh();
    }

    private static void printHitsAndMisses(Screen screen, TextGraphics tGraphics) throws IOException {
        tGraphics.putString(10, 20, "Word: " + hits);
        tGraphics.putString(10, 22, "Misses: " + misses);
        screen.refresh();
    }

    private static void printFinalResult(Screen screen, TextGraphics textGraphics, String solution, int faultNum) throws IOException {
        String compare = String.valueOf(hits.charAt(0));

        for (int i = 3; i <= hits.length(); i += 2) {
            compare += (hits.substring(i - 1, i));
        }

        if (faultNum == 9) {
            textGraphics.putString(40, 10, "Game over!");
            textGraphics.putString(40, 11, "The word was \"" + solution + "\".");

            stopScreen(screen);
        } else if (compare.equals(solution.toLowerCase())) {
            textGraphics.putString(40, 10, "Congratulations, you won!");

            stopScreen(screen);
        }
    }

    private static void stopScreen(Screen screen) throws IOException {
        screen.refresh();

        KeyStroke esc;

        do {
            esc = screen.readInput();
        } while (esc.getKeyType() != KeyType.Escape);

        screen.stopScreen();

        System.exit(0);
    }
}
