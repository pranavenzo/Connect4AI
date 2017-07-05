import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Connect4 {
    public static final char NONE = ' ';

    public static final char RED = 'R';

    public static final char YELLOW = 'Y';

    char[][] board;

    int turns;

    int rows;

    int columns;

    Map<String, List<Integer>> banned;

    /**
     * Initializes the instance variables.
     */
    public Connect4() {
        rows = 6;
        columns = 7;
        board = new char[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                board[i][j] = NONE;
            }
        }
        turns = 0;
        banned = new HashMap<>();
    }

    /**
     * Returns a copy of the current board
     *
     * @return a char matrix
     */
    public char[][] getBoard() {
        char[][] copy = new char[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                copy[i][j] = board[i][j];
            }
        }
        return copy;
    }

    /**
     * Put a piece of the given color in the given column
     * The function returns the row where the piece have been
     * put. If the column is full it return -1.
     *
     * @param column a column of the board
     * @param color  RED or YELLOW
     */
    public int putPiece(int column, char color) {
        if (board[0][column] != NONE) return -1;
        int i;
        for (i = 0; i < rows; i++) {
            if (board[i][column] != NONE && i != 0) {
                board[i - 1][column] = color;
                break;
            }
            if (i == rows - 1) {
                board[i][column] = color;
                return i;
            }
        }
        i--;
        return (i);
    }

    /**
     * Print the screen in the standard output
     */
    public void printScreen() {
        // Make the header of the board
        System.out.printf("\n ");
        for (int i = 0; i < board[0].length; ++i)
            System.out.printf("   %d", i);
        System.out.println();
        System.out.printf("  ");
        for (int i = 0; i < board[0].length; ++i)
            System.out.printf("----");
        System.out.println("-");
        // Print the board contents
        for (int i = 0; i < board.length; ++i) {
            System.out.printf("%c ", 'A' + i);
            for (int k = 0; k < board[0].length; ++k)
                System.out.printf("| %c ", board[i][k]);
            System.out.println("|");
            // print the line between each row
            System.out.printf("  ");
            for (int k = 0; k < board[0].length; ++k)
                System.out.printf("----");
            System.out.println("-");
        }
    }

    /**
     * Check if an alignment has been made using the given tile
     *
     * @param row
     * @param column
     * @return the color if there is an alignment, NONE otherwise.
     */
    public char checkAlignment(int row, int column) {
        int countFour = 0;
        char checker = board[row][column];
        for (int i = 0; i < 3; i++) { // horizontal checking ( left )
            if ((column - 1 - i) >= 0 && board[row][column - i - 1] == checker) {
                countFour++;
            } else {
                break;
            }
        }
        for (int i = 0; i < 3; i++) { // horizontal checking ( right )
            if ((column + 1 + i) < columns && board[row][column + i + 1] == checker) {
                countFour++;
            } else break;
        }
        if (countFour >= 3) return checker;
        countFour = 0;
        // vertical checking ( down )
        for (int i = 0; i < 3; i++) {
            if ((row + i + 1) < rows && board[row + i + 1][column] == checker) {
                countFour++;
            } else break;
        }
        //vertical up
        for (int i = 0; i < 3; i++) {
            if ((row - i - 1) >= 0 && board[row - i - 1][column] == checker) {
                countFour++;
            } else break;
        }
        if (countFour >= 3) {
            return checker;
        }
        countFour = 0;
        for (int i = 0; i < 3; i++) {
            if (((row - i - 1) >= 0 && (column + 1 + i) < columns) &&
                    (board[row - i - 1][column + 1 + i] == checker)) { // diagonal checking ( up right )
                countFour++;
            } else {
                break;
            }
        }
        for (int i = 0; i < 3; i++) {
            if ((((row + i + 1) < rows) && (column - 1 - i) >= 0) && (board[row + i + 1][column - 1 - i] == checker)) {
                // diagonal checking ( down left )
                countFour++;
            } else {
                break;
            }
        }
        if (countFour >= 3) {
            return checker;
        }
        countFour = 0;
        for (int i = 0; i < 3; i++) {
            if (((row - i - 1) >= 0 && (column - 1 - i) >= 0) &&
                    (board[row - i - 1][column - 1 - i] == checker)) { // diagonal checking ( up left )
                countFour++;
            } else {
                break;
            }
        }
        for (int i = 0; i < 3; i++) {
            if ((((row + i + 1) < rows) && (column + 1 + i) < columns) &&
                    (board[row + i + 1][column + 1 + i] == checker)) {
                // diagonal checking ( down right )
                countFour++;
            } else {
                break;
            }
        }
        if (countFour >= 3) {
            return checker;
        }
        return NONE;
    }

    /**
     * Launch the game for one game.
     */
    public void play(int bot1, int bot2) throws IOException {
        List<Integer> moves = new LinkedList<>();
        char currentPlayer = RED;
        // Begin playing the game
        Scanner in = new Scanner(System.in);
        int col = -1;
        int row = -1;
        do {
            currentPlayer = currentPlayer == RED ? YELLOW : RED;
            /*this.printScreen();
            System.out.printf("Current player: '%c'\n", currentPlayer);
            */// read and validate the input
            if (currentPlayer == RED) {
                // long startTime = System.currentTimeMillis();
                col = bot(1, bot2);
                row = this.putPiece(col, currentPlayer);
                moves.add(col);
                // long endTime = System.currentTimeMillis();
                // System.out.println("Time taken: " + (endTime - startTime) + "ms");
            } else if (currentPlayer == YELLOW) {
                col = bot(2, bot1);
                row = this.putPiece(col, currentPlayer);
                moves.add(col);
            } else {
                col = -1;
                row = -1;
                do {
                    System.out.printf("Choose a column: ");
                    String line = in.nextLine();
                    if (line == null || line.length() != 1) {
                        System.out.println("Invalid Input. Please re-enter a valid value.");
                        continue;
                    }
                    col = line.charAt(0) - '0';
                    if (col < 0 || col > columns) {
                        continue;
                    }
                    moves.add(col);
                    row = this.putPiece(col, currentPlayer);
                } while (row < 0);
            }
            turns++;
        } while ((row < 0 || this.checkAlignment(row, col) == NONE) && turns < 42);
        /*this.printScreen();
        System.out.printf("\n!!! Winner is Player '%c' !!!\n", currentPlayer);
        */if (turns < 42) {
            if (currentPlayer == YELLOW) {
                //learn(moves, board, RESULTS.LOSS, false, 1);
                Connect4.count++;
            }
            if (currentPlayer == RED) {
                //learn(moves, board, RESULTS.LOSS, false, 2);
            }
        } else draws++;
        //   writeHere();
    }

    private String serializeBoard(char[][] board) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                ret.append(board[i][j] == ' ' ? "0" : board[i][j] == 'R' ? "1" : "2").append(",");
            }
        }
        return ret.toString().substring(0, ret.toString().length() - 1);
    }

    private void writeHere() throws IOException {
        PrintWriter writer = new PrintWriter(new FileOutputStream(new File("experiences.txt"), false));
        for (String keys : banned.keySet()) {
            writer.println(keys + " " + (Arrays.toString(banned.get(keys).toArray()).replaceAll(" ", "")));
        }
        writer.close();
    }

    private enum RESULTS {
        WIN,
        DRAW,
        LOSS
    }

    private void learn(List<Integer> moves, char[][] board, RESULTS result, boolean myMoveLast, int player) {
        if (result == RESULTS.LOSS) {
            do {
                if (moves.size() < 2) return;
                int lastMove = moves.remove(moves.size() - 1);
                board = popPiece(board, lastMove);
                lastMove = moves.remove(moves.size() - 1);
                board = popPiece(board, lastMove);
                Field field = new Field(columns, rows);
                field.parseFromString(serializeBoard(board));
                int res = new BotStarter().makeTurn2(field, player);
                Winner winner = new Winner(field);
                winner.printScreen();
                if (res > 0) {
                    String s = serializeBoard(board);
                    List<Integer> current = banned.get(s);
                    if (current == null) current = new LinkedList<>();
                    current.add(lastMove);
                    Set<Integer> ss = new HashSet<>(current);
                    current = new LinkedList<>(ss);
                    banned.put(s, current);
                    break;
                }
            } while (true);
        }
    }

    private char[][] popPiece(char board[][], int col) {
        for (int i = 0; i < rows; i++) {
            if (board[i][col] != ' ') {
                board[i][col] = ' ';
                break;
            }
        }
        return board;
    }

    private int bot(int player, int botNum) {
        String ret = "";
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                ret = ret + (board[i][j] == ' ' ? "0" : board[i][j] == 'R' ? "1" : "2") + ",";
            }
        }
        ret = ret.substring(0, ret.length() - 1);
        Field field = new Field(columns, rows);
        field.parseFromString(ret);
        BotStarter botStarter = new BotStarter();
        return botStarter.makeTurn(field, player, banned, botNum);
    }

    public static int count;

    public static int draws;

    private static int treeBot = 1;

    private static int recBot = 0;

    public static void main(String[] args) throws IOException {
        count = 0;
        draws = 0;
        double total = 0;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            long startTimeGame = System.currentTimeMillis();
            Connect4 connect4 = new Connect4();
            try {
                connect4.play(treeBot, recBot);
            } catch (Exception e) {
                System.out.println("Game: " + (i + 1) + "\nFailed");
            }
            System.out.println("Finished game: " + (i + 1) + "\nYellow wins: " + count);
            long endTimeGame = System.currentTimeMillis();
            System.out.println("Time taken: " + (endTimeGame - startTimeGame) + "ms");
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + "ms");
        System.out.println("Yellow wins:" + count);
        System.out.println("Draws:" + draws);
    }
}