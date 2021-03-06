// Copyright 2015 theaigames.com (developers@theaigames.com)

//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at

//        http://www.apache.org/licenses/LICENSE-2.0

//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
//    For the full copyright and license information, please view the LICENSE
//    file that was distributed with this source code.

/**
 * Field class
 * <p>
 * Field class that contains the field status data and various helper functions.
 *
 * @author Jim van Eeden <jim@starapple.nl>, Joost de Meij <joost@starapple.nl>
 */
public class Field {
    private int[][] mBoard;
    private int mCols = 0, mRows = 0;
    private String mLastError = "";
    public int mLastColumn = 0;

    public Field(int columns, int rows) {
        mBoard = new int[rows][columns];
        mCols = columns;
        mRows = rows;
        clearBoard();
    }

    Field(Field field) {
        this.mCols = field.getNrColumns();
        this.mRows = field.getNrRows();
        mBoard = new int[mRows][mCols];
        for (int i = 0; i < mRows; i++) {
            for (int j = 0; j < mCols; j++) {
                mBoard[i][j] = field.getDisc(i, j);
            }
        }
    }

    Field(String field, int rows, int cols) {
        mBoard = new int[rows][cols];
        mCols = cols;
        mRows = rows;
        this.parseFromString(field);
    }

    /**
     * Sets the number of columns (this clears the board)
     *
     * @param args : int cols
     */
    public void setColumns(int cols) {
        mCols = cols;
        mBoard = new int[mRows][mCols];
    }

    /**
     * Sets the number of rows (this clears the board)
     *
     * @param args : int rows
     */
    public void setRows(int rows) {
        mRows = rows;
        mBoard = new int[mRows][mCols];
    }

    /**
     * Clear the board
     */
    public void clearBoard() {
        for (int x = 0; x < mCols; x++) {
            for (int y = 0; y < mRows; y++) {
                mBoard[y][x] = 0;
            }
        }
    }

    /**
     * Adds a disc to the board
     *
     * @param args : command line arguments passed on running of application
     * @return : true if disc fits, otherwise false
     */
    public Boolean addDisc(int column, int disc) {
        mLastError = "";
        if (column < mCols) {
            for (int y = mRows - 1; y >= 0; y--) { // From bottom column up
                if (mBoard[y][column] == 0) {
                    mBoard[y][column] = disc;
                    mLastColumn = column;
                    return true;
                }
            }
            mLastError = "Column is full.";
        } else {
            mLastError = "Move out of bounds.";
        }
        return false;
    }

    /**
     * Initialise field from comma separated String
     *
     * @param String :
     */
    public void parseFromString(String s) {
        s = s.replace(';', ',');
        String[] r = s.split(",");
        int counter = 0;
        for (int y = 0; y < mRows; y++) {
            for (int x = 0; x < mCols; x++) {
                mBoard[y][x] = Integer.parseInt(r[counter]);
                counter++;
            }
        }
    }

    /**
     * Returns the current piece on a given column and row
     *
     * @param args : int column, int row
     * @return : int
     */
    public int getDisc(int row, int column) {
        return mBoard[row][column];
    }

    /**
     * Returns whether a slot is open at given column
     *
     * @param args : int column
     * @return : Boolean
     */
    public Boolean isValidMove(int column) {
        return (mBoard[0][column] == 0);
    }

    /**
     * Returns reason why addDisc returns false
     *
     * @param args :
     * @return : reason why addDisc returns false
     */
    public String getLastError() {
        return mLastError;
    }

    @Override
    /**
     * Creates comma separated String with every cell.
     * @param args :
     * @return : String
     */
    public String toString() {
        String r = "";
        int counter = 0;
        for (int y = 0; y < mRows; y++) {
            for (int x = 0; x < mCols; x++) {
                if (counter > 0) {
                    r += ",";
                }
                r += mBoard[y][x];
                counter++;
            }
        }
        return r;
    }

    /**
     * Checks whether the field is full
     *
     * @return : Returns true when field is full, otherwise returns false.
     */
    public boolean isFull() {
        for (int x = 0; x < mRows; x++)
            for (int y = 0; y < mCols; y++)
                if (mBoard[x][y] == 0)
                    return false; // At least one cell is not filled
        // All cells are filled
        return true;
    }

    /**
     * Checks whether the given column is full
     *
     * @return : Returns true when given column is full, otherwise returns false.
     */
    public boolean isColumnFull(int column) {
        return (mBoard[0][column] != 0);
    }

    public int getNumFreeSpacesInCol(int column) {
        int i = this.getNrRows() - 1;
        while (i >= 0 && this.getDisc(i, column) != 0) i--;
        return i + 1;
    }

    /**
     * @return : Returns the number of columns in the field.
     */
    public int getNrColumns() {
        return mCols;
    }

    /**
     * @return : Returns the number of rows in the field.
     */
    public int getNrRows() {
        return mRows;
    }
}
