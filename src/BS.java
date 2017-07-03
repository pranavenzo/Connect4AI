// // Copyright 2015 theaigames.com (developers@theaigames.com)

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


import java.lang.reflect.Array;
import java.util.*;

/**
 * BotStarter class
 * <p>
 * Magic happens here. You should edit this file, or more specifically
 * the makeTurn() method to make your bot do more than random moves.
 *
 * @author Jim van Eeden <jim@starapple.nl>, Joost de Meij <joost@starapple.nl>
 */

public class BS {


    Field field;
    int player;
    HashMap<Integer, List<Integer>> alphaMap;
    final static int MAX_DEPTH = 5;

    /**
     * Makes a turn. Edit this method to make your bot smarter.
     *
     * @return The column where the turn was made.
     */
    public int makeTurn(Field mfield, int player) {
        alphaMap = new HashMap<>();
        this.field = mfield;
        this.player = player;
        int inf[] = new int[2];
        inf[0] = Integer.MIN_VALUE;
        inf[1] = Integer.MAX_VALUE;
        // Winner winner = new Winner(field);
        // winner.printScreen();
        int[] score = prune(MAX_DEPTH, field, true, inf, "");
        List<Integer> moves = alphaMap.get(score[0]);
        if (moves == null) return (int) (Math.random() * (double) field.getNrColumns());
        Integer move;
        move = moves.get(0);//(int) (Math.random() * moves.size()));
        return move;
    }


    private int[] prune(int depth, Field pruneField, boolean isMaximizer, int[] alphaBeta, String path) {
        if (depth == 0) {
            int[] newAB = new int[2];
            int x = evalFunction(pruneField);
            newAB[0] = x;
            newAB[1] = x;
            return newAB;
        }
        int grandAlpha = alphaBeta[0];
        int grandBeta = alphaBeta[1];
        int alpha = alphaBeta[0];
        int beta = alphaBeta[1];
        int[] newAB = new int[2];
        int player = isMaximizer ? this.player : 3 - this.player;

        for (int i = 0; i < pruneField.getNrColumns(); i++) {
            Field newPrune = new Field(pruneField);
            if (!newPrune.addDisc(i, player)) continue;
            Winner winner = new Winner(newPrune);
            //winner.printScreen();
            if (winner.isWinner(i, this.player)) {
                newAB[0] = 1000;
                newAB[1] = 1000;
                if (isMaximizer) {
                    return newAB;
                }

            } else if (winner.isWinner(i, 3 - this.player)) {
                newAB[0] = -1000 * depth;
                newAB[1] = -1000 * depth;
                if (!isMaximizer) {
                    return newAB;
                }
            } else {
                newAB[0] = alpha;
                newAB[1] = beta;
                if (isMaximizer) {
                    int[] ret = prune(depth - 1, newPrune, false, newAB, path + i);
                    if (alpha < ret[1]) {
                        alpha = ret[1];
                        if (depth == MAX_DEPTH) {
                            List<Integer> list = alphaMap.get(alpha);
                            if (list == null) {
                                list = new LinkedList<>();
                            }
                            list.add(i);
                            alphaMap.put(alpha, list);
                        }
                    }

                } else {
                    int[] ret = prune(depth - 1, newPrune, true, newAB, path + i);
                    if (beta > ret[0]) {
                        beta = ret[0];
                    }
                }
/*
                if (isMaximizer && alpha > grandBeta) {
                    newAB[0] = grandAlpha;
                    newAB[1] = grandBeta;
                    return newAB;
                }
                if (!isMaximizer && grandAlpha > beta) {
                    newAB[0] = grandAlpha;
                    newAB[1] = grandBeta;
                    return newAB;
                }
*/
            }
            newAB[0] = alpha;
            newAB[1] = beta;
        }
        return newAB;
    }

    private int evalFunction(Field pruneField) {
        Winner winner = new Winner(pruneField);
        int myThrees = 0;
        for (int i = 0; i < pruneField.getNrRows(); i++) {
            for (int j = 0; j < pruneField.getNrColumns(); j++) {
                winner.putAt(i, j, this.player);
                if (winner.checkAlignment(i, j) == (this.player == 1 ? 'R' : 'Y')) myThrees++;
                winner.putAt(i, j, 3);
            }
        }
        return myThrees;
    }


    public static void main(String[] args) {
        BotParser parser = new BotParser(new BotStarter());
        parser.run();
    }



}
