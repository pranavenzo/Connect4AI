/**
 * Created by anappp on 5/26/17.
 */
public class Winner {
    // Check rows, if there are 4 or more disks of the same color - return winner color
    Field mField;
    char[][] field;
    int rows;
    int cols;

    Winner(Field mfield) {
        this.mField = mfield;
        this.rows = mfield.getNrRows();
        this.cols = mfield.getNrColumns();
        field = new char[rows][cols];
        parse();
    }

    private void parse() {
        for (int i = 0; i < mField.getNrRows(); i++) {
            for (int j = 0; j < mField.getNrColumns(); j++) {
                field[i][j] = mField.getDisc(i, j) == 0 ? ' ' : mField.getDisc(i, j) == 1 ? 'R' : 'Y';
            }
        }
    }

    public void putAt(int row, int col, int player) {
        if (player == 1) field[row][col] = 'R';
        else if (player == 2) field[row][col] = 'Y';
        else field[row][col] = ' ';
    }

    public boolean isWinner(int col, int player) {
        int row = rows - 1;
        while (row >= 0 && field[row][col] != ' ') row--;
        if (row < rows - 1)
            row += 1;
        return checkAlignment(row, col) == (player == 1 ? 'R' : 'Y');
    }

    public char getDisc(int row, int col) {
        return field[row][col];
    }

    public char checkAlignment(int row, int column) {
        int countFour = 0;
        char checker = field[row][column];

        for (int i = 0; i < 3; i++) { // horizontal checking ( left )
            if ((column - 1 - i) >= 0 && field[row][column - i - 1] == checker) {
                countFour++;
            } else {
                break;
            }
        }

        for (int i = 0; i < 3; i++) { // horizontal checking ( right )
            if ((column + 1 + i) < cols && field[row][column + i + 1] == checker) {
                countFour++;
            } else break;

        }
        if (countFour >= 3) return checker;

        countFour = 0;

        // vertical checking ( down )
        for (int i = 0; i < 3; i++) {
            if ((row + i + 1) < rows && field[row + i + 1][column] == checker) {
                countFour++;
            } else break;
        }
        //vertical up
        for (int i = 0; i < 3; i++) {
            if ((row - i - 1) >= 0 && field[row - i - 1][column] == checker) {
                countFour++;
            } else break;

        }
        if (countFour >= 3) {
            return checker;
        }

        countFour = 0;

        for (int i = 0; i < 3; i++) {
            if (((row - i - 1) >= 0 && (column + 1 + i) < cols) &&
                    (field[row - i - 1][column + 1 + i] == checker)) { // diagonal checking ( up right )
                countFour++;
            } else {
                break;
            }
        }
        for (int i = 0; i < 3; i++) {
            if ((((row + i + 1) < rows) && (column - 1 - i) >= 0) && (field[row + i + 1][column - 1 - i] == checker)) {
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
                    (field[row - i - 1][column - 1 - i] == checker)) { // diagonal checking ( up left )
                countFour++;

            } else {
                break;
            }
        }
        for (int i = 0; i < 3; i++) {
            if ((((row + i + 1) < rows) && (column + 1 + i) < cols) &&
                    (field[row + i + 1][column + 1 + i] == checker)) {
                // diagonal checking ( down right )
                countFour++;

            } else {
                break;
            }
        }
        if (countFour >= 3) {
            return checker;
        }
        return ' ';
    }

    public void printScreen() {
        // Make the header of the board
        System.out.printf("\n ");
        for (int i = 0; i < field[0].length; ++i)
            System.out.printf("   %d", i);
        System.out.println();

        System.out.printf("  ");
        for (int i = 0; i < field[0].length; ++i)
            System.out.printf("----");
        System.out.println("-");

        // Print the board contents
        for (int i = 0; i < field.length; ++i) {
            System.out.printf("%c ", 'A' + i);
            for (int k = 0; k < field[0].length; ++k)
                System.out.printf("| %c ", field[i][k]);
            System.out.println("|");

            // print the line between each row
            System.out.printf("  ");
            for (int k = 0; k < field[0].length; ++k)
                System.out.printf("----");
            System.out.println("-");
        }
    }


}
