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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * BotStarter class
 * <p>
 * Magic happens here. You should edit this file, or more specifically
 * the makeTurn() method to make your bot do more than random moves.
 *
 * @author Jim van Eeden <jim@starapple.nl>, Joost de Meij <joost@starapple.nl>
 */
public class BotStarter implements Bot {
    Field field;
    int player;
    HashMap<Integer, List<Integer>> alphaMap;
    int MAX_DEPTH = 2;
    Map<String, List<Integer>> banned;

    /**
     * Makes a turn. Edit this method to make your bot smarter.
     *
     * @return The column where the turn was made.
     */
    public int makeTurn(Field mfield, int player, Map<String, List<Integer>> banned, long time, String moves) {
        calls = 0;
        this.banned = banned;
        alphaMap = new HashMap<>();
        this.field = mfield;
        this.player = player;
        int movee = prune(MAX_DEPTH, field, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        List<Integer> mo = alphaMap.get(movee);
        if (mo == null) return (int) (Math.random() * (double) field.getNrColumns());
        Integer move;
        move = mo.get((int) (Math.random() * mo.size()));
        //System.out.println(calls);
        return move;
    }

    private void fillMap() {
        try (BufferedReader br = new BufferedReader(new FileReader("experiences.txt"))) {
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
        } catch (Exception ignored) {
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

    public int makeTurn2(Field mfield, int player) {
        alphaMap = new HashMap<>();
        this.field = mfield;
        this.player = player;
        int movee = prune(MAX_DEPTH, field, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        return movee;
    }

    private int depthTarget() {
        int num = 0;
        for (int i = 0; i < field.getNrColumns(); i++) {
            for (int j = 0; j < field.getNrRows(); j++) {
                if (field.getDisc(j, i) != 0) {
                    num++;
                }
            }
        }
        return (int) Math.round((9.0 + 6 * Math.pow(((double) num) / (field.getNrRows() * field.getNrColumns()), 1)));
    }

    private int isBanned(Field field, int col, int ret, int sign) {
        /*if (banned == null || field == null) return ret;
        List<Integer> bannedList = banned.get(field.toString());
        if (field.toString().equals("0,0,1,2,0,0,0,0,0,2,1,0,0,0,2,0,1,1,0,2,0,1,0,2,2,0,1,0,2,0,1,2,0,2,0,1,0,2,1,2,1,0"))
            System.out.println(bannedList + " " + col);
        if (bannedList == null) return ret;
        for (int i = 0; i < bannedList.size(); i++) {
            if (col == bannedList.get(i)) return sign * 10000;
        }*/
        return ret;
    }

    static int calls;

    public int prune(int depth, Field pruneField, int alpha, int beta, boolean isMaximizer) {
        calls++;
        boolean isChanged = false;
        if (depth == 0) {
            int x = evalFunction(pruneField);
            return x;
        }
        int key = isMaximizer ? alpha : beta;
        for (int i = 0; i < field.getNrColumns(); i++) {
            Field newPrunefield = new Field(pruneField);
            if (newPrunefield.addDisc(i, isMaximizer ? player : 3 - player)) {
                Winner winner = new Winner(newPrunefield);
                if (isMaximizer) {
                    int ret = isBanned(pruneField, i, 0, -1);
                    if (winner.isWinner(i, this.player)) {
                        ret = 1000;
                    } else if (ret == 0) {
                        ret = winner.isWinner(i, 3 - this.player) ? -1000 * depth : prune(depth - 1,
                                newPrunefield, key, beta, false);
                    }
                    if (ret >= key) {
                        key = ret;
                        isChanged = true;
                        if (depth == MAX_DEPTH) {
                            List<Integer> list = alphaMap.get(key);
                            if (list == null) {
                                list = new LinkedList<>();
                            }
                            list.add(i);
                            alphaMap.put(key, list);
                        }
                    }
                } else {
                    int ret = isBanned(pruneField, i, 0, 1);
                    if (winner.isWinner(i, 3 - this.player)) {
                        ret = -1000 * depth;
                    } else if (ret == 0) {
                        ret = winner.isWinner(i, this.player) ? 1000 : prune(depth - 1, newPrunefield, alpha, key, true);
                    }
                    if (ret < key) {
                        key = ret;
                        isChanged = true;
                    }
                }
            }
            if (key == 1000 && isMaximizer) return key;
            if (key == -1000 * depth && !isMaximizer) return key;
            if (isMaximizer && key > beta) return key;
            if (!isMaximizer && key < alpha) return key;
        }
        if (!isChanged && !isMaximizer) {
            return key + 1;
        } else if (!isChanged) {
            return key - 1;
        }
        return key;
    }

    private int evalFunction(Field pruneField) {
        Winner winner = new Winner(pruneField);
        Winner oppWinner = new Winner(pruneField);
        //winner.printScreen();
        int oppCount = 0;
        int oppColCount[] = new int[pruneField.getNrColumns()];
        int count = 0;
        int colCount[] = new int[pruneField.getNrColumns()];
        Arrays.fill(colCount, 0);
        Arrays.fill(oppColCount, 0);
        int score = 0;
        int oppScore = 0;
        for (int i = 0; i < pruneField.getNrRows(); i++) {
            for (int j = 0; j < pruneField.getNrColumns(); j++) {
                //Compute player score
                if (winner.getDisc(i, j) == ' ') {
                    winner.putAt(i, j, this.player);
                }
                if (winner.getDisc(i, j) == (this.player == 1 ? 'R' : 'Y')) {
                    count++;
                    colCount[j]++;
                } else {
                    if (colCount[j] >= 4) score += colCount[j];
                    if (count >= 4) score += count;
                    count = 0;
                    colCount[j] = 0;
                }
                //Compute opposition score
                if (oppWinner.getDisc(i, j) == ' ') {
                    oppWinner.putAt(i, j, 3 - this.player);
                }
                if (oppWinner.getDisc(i, j) == (this.player == 1 ? 'Y' : 'R')) {
                    oppCount++;
                    oppColCount[j]++;
                } else {
                    if (oppColCount[j] >= 4) oppScore += oppColCount[j];
                    if (oppCount >= 4) oppScore += oppCount;
                    oppCount = 0;
                    oppColCount[j] = 0;
                }
            }
            if (count >= 4) score += count;
            count = 0;
            if (oppCount >= 4) oppScore += oppCount;
            oppCount = 0;
        }
        for (int c : colCount) {
            if (c >= 4) score += c;
        }
        return score - oppScore;
    }

    private int evalFunction2(Field pruneField) {
        int[] score = new int[3];
        for (int i = 0; i < pruneField.getNrColumns(); i++) {
            Field newField = new Field(pruneField);
            int nextPlayer = 3 - player;
            while (newField.addDisc(i, nextPlayer)) {
                Winner winner = new Winner(newField);
                if (winner.isWinner(i, nextPlayer)) {
                    score[nextPlayer] += 100;
                    break;
                }
                nextPlayer = 3 - nextPlayer;
            }
        }
        for (int i = 0; i < pruneField.getNrColumns(); i++) {
            Field newField = new Field(pruneField);
            int nextPlayer = player;
            while (newField.addDisc(i, nextPlayer)) {
                Winner winner = new Winner(newField);
                if (winner.isWinner(i, nextPlayer)) {
                    score[nextPlayer] += 100;
                    break;
                }
                nextPlayer = 3 - nextPlayer;
            }
        }
        return score[player] = score[3 - player];
    }

    public static void main(String[] args) {
        BotParser parser = new BotParser(new TreeBot(true));
        parser.run();
    }
}