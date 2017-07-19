import java.io.*;
import java.util.*;

public class Connect4 {
    private static final char NONE = ' ';

    private static final char RED = 'R';

    private static final char YELLOW = 'Y';

    private char[][] board;

    private int turns;

    private int rows;

    private int columns;

    private Map<String, List<Integer>> banned;

    private Map<String, Integer> boardToMoveYellow;

    private Map<String, Integer> boardToMoveRed;

    /**
     * Initializes the instance variables.
     */
    private Connect4() {
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
        boardToMoveYellow = new HashMap<>();
        boardToMoveRed = new HashMap<>();
    }

    /**
     * Returns a copy of the current board
     *
     * @return a char matrix
     */
    public char[][] getBoard() {
        char[][] copy = new char[rows][columns];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, columns);
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
    private int putPiece(int column, char color) {
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
    private void printScreen() {
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
    private char checkAlignment(int row, int column) {
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
    private void play(int bot1, int bot2, boolean print, boolean human1, boolean human2) throws IOException {
        List<Integer> moves = new LinkedList<>();
        char currentPlayer = RED;
        // Begin playing the game
        Scanner in = new Scanner(System.in);
        int col = -1;
        int row = -1;
        Bot botnum1 = bot1 == recBot ? new BotStarter() : bot1 == treeBot ? new TreeBot(false) : new BestBot();
        Bot botnum2 = bot2 == recBot ? new BotStarter() : bot2 == treeBot ? new TreeBot(false) : new BestBot();
        do {
            currentPlayer = currentPlayer == RED ? YELLOW : RED;
            if (print) {
                this.printScreen();
                System.out.printf("Current player: '%c'\n", currentPlayer);
            }
            // read and validate the input
            if (currentPlayer == RED & !human2) {
                long startTime = System.currentTimeMillis();
                col = bot(botnum2, 1, bot2);
                boardToMoveRed.put(serializeBoard(this.board), col);
                System.out.print(col);
                row = this.putPiece(col, currentPlayer);
                moves.add(col);
                if (print) {
                    long endTime = System.currentTimeMillis();
                    System.out.println("Time taken: " + (endTime - startTime) + "ms");
                }
            } else if (currentPlayer == YELLOW & !human1) {
                long startTime = System.currentTimeMillis();
                col = bot(botnum1, 2, bot1);
                boardToMoveYellow.put(serializeBoard(this.board), col);
                System.out.print(col);
                try {
                    row = this.putPiece(col, currentPlayer);
                } catch (Exception e) {
                    System.out.println(serializeBoard(this.board));
                    throw e;
                }
                moves.add(col);
                if (print) {
                    long endTime = System.currentTimeMillis();
                    System.out.println("Time taken: " + (endTime - startTime) + "ms");
                }
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
        if (print) {
            this.printScreen();
            System.out.printf("\n!!! Winner is Player '%c' !!!\n", currentPlayer);
        }
        myFileReader();
        if (turns < 42) {
            if (currentPlayer == YELLOW) {
                learn(moves, board, RESULTS.LOSS, 1);
                Connect4.count++;
            }
            if (currentPlayer == RED) {
                learn(moves, board, RESULTS.LOSS, 2);
            }
        } else {
            if (currentPlayer == YELLOW) {
                learn(moves, board, RESULTS.LOSS, 1);
            }
            if (currentPlayer == RED) {
                learn(moves, board, RESULTS.LOSS, 2);
            }
            draws++;
        }
        writeHere();
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
        PrintWriter writer = new PrintWriter(new FileOutputStream(new File("/homes/panappin/Connect4AI/Connect4AI/experiences.txt"), false));
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

    private void learn(List<Integer> moves, char[][] board, RESULTS result, int player) throws IOException {
        if (result == RESULTS.LOSS) {
            do {
                if (moves.size() < 2) return;
                int lastMove = moves.remove(moves.size() - 1);
                board = popPiece(board, lastMove);
                lastMove = moves.remove(moves.size() - 1);
                board = popPiece(board, lastMove);
                Field field = new Field(columns, rows);
                field.parseFromString(serializeBoard(board));
                int res = new BestBot().makeTurn(field, player, null, 0, true);
/*                Winner winner = new Winner(field);
                winner.printScreen();*/
                if ((res > Bot.loser && res != Bot.draw) && !banned.containsKey(field.toString())) {
                    String s = serializeBoard(board);
                    List<Integer> current = banned.getOrDefault(s, new LinkedList<>());
                    current.add(lastMove);
                    Set<Integer> ss = new HashSet<>(current);
                    current = new LinkedList<>(ss);
                    banned.put(s, current);
                    break;
                }
            } while (true);
        }
    }

    private void myFileReader() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("/homes/panappin/Connect4AI/Connect4AI/experiences.txt"))) {
            String line = br.readLine();
            while (line != null) {
                String params[] = line.split(" ");
                List<Integer> list = toList(params[1]);
                List<Integer> list1 = banned.get(params[0]);
                if (list1 != null) {
                    Set<Integer> s = new HashSet<>(list1);
                    Set<Integer> s1 = new HashSet<>(list);
                    s1.addAll(s);
                    list = new LinkedList<>(s1);
                }
                banned.put(params[0], list);
                line = br.readLine();
            }
            br.close();
        }
    }

    private List<Integer> toList(String s) {
        List<Integer> out = new LinkedList<>();
        s = s.substring(1, s.length() - 1);
        String[] chars = s.split(",");
        for (String aChar : chars) {
            out.add(Integer.parseInt(aChar));
        }
        return out;
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

    private int bot(Bot bot, int player, int botNum) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                ret.append(board[i][j] == ' ' ? "0" : board[i][j] == 'R' ? "1" : "2").append(",");
            }
        }
        ret = new StringBuilder(ret.substring(0, ret.length() - 1));
        Field field = new Field(columns, rows);
        field.parseFromString(ret.toString());
        return bot.makeTurn(field, player, banned, Long.MAX_VALUE);
    }

    private static int count;

    private static int draws;

    private static final int treeBot = 1;

    private static final int bestBot = 2;

    private static final int recBot = 0;

    public static void main(String[] args) throws IOException {
        count = 0;
        draws = 0;
        int failed = 0;
        boolean isDebug = true;
        long startTime = System.currentTimeMillis();
        final int totalGames = 1;
        for (int i = 0; i < totalGames; i++) {
            long startTimeGame = System.currentTimeMillis();
            Connect4 connect4 = new Connect4();
            try {
                connect4.play(bestBot, bestBot, true, false, false);
            } catch (Exception e) {
                System.out.println("Game: " + (i + 1) + "\nFailed");
                failed++;
                if (isDebug) throw e;
            }
            System.out.println("Finished game: " + (i + 1) + "\nYellow wins: " + count);
            long endTimeGame = System.currentTimeMillis();
            System.out.println("Time taken: " + (endTimeGame - startTimeGame) + "ms");
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + "ms");
        System.out.println("Yellow wins:" + count);
        System.out.println("Draws:" + draws);
        System.out.println("Failed:" + failed);
    }
}
