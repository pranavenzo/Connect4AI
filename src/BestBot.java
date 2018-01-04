import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * Created by anappp on 7/14/17.
 */
public class BestBot implements Bot {
    private int player;
    private int opponent;
    private Map<String, int[]> visited;
    private final int alpha = 0;
    private final int beta = 1;
    private final int bestMove = 2;
    private final int bestChild = 3;
    private int maxDepth;
    private Map<String, List<Integer>> myMoves;

    BestBot() {
        maxDepth = 5;
        myMoves = new HashMap<>();
        //myFileReader();
    }

    private void setMaxdepth(Field field) {
        double full = 0;
        for (int i = 0; i < field.getNrColumns(); i++) {
            full += 1 - (field.getNumFreeSpacesInCol(i) * 1.0) / field.getNrRows();
        }
        maxDepth = ((int) (full * 0.5)) + maxDepth;
    }

    @Override
    public int makeTurn(Field mfield, int player, Map<String, List<Integer>> banned, long time, String moves) {
        return this.makeTurn(mfield, player, banned, time, false);
    }

    public int makeTurn(Field mfield, int player, Map<String, List<Integer>> banned, long time, boolean isLearn) {
        //setMaxdepth(mfield);
        //System.out.println("\tdepth=" + maxDepth);
        visited = new HashMap<>();
        this.player = player;
        this.opponent = 3 - player;
        calls = 0;
        int[] defAb = new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE, -1, -1};
        int[] ret = prune(defAb, mfield, true, 0);
        //System.out.println("Number of Calls" + calls);
        if (!isLearn) {
            return ret[bestMove];
        }
        return ret[alpha];
    }

    static int calls;

    private int[] prune(int[] ab, Field field, boolean isMaximizer, int level) {
        calls++;
        int[] par = dup(ab);
        int myTurn = isMaximizer ? player : opponent;
        if (level == maxDepth) {
            int score = newEval(field);
            return new int[]{score, score, -1, score};
        }
        List<CompressedGameNode> kids = new LinkedList<>();
        for (int i = 0; i < field.getNrColumns(); i++) {
            if (myMoves.getOrDefault(field.toString(), new LinkedList<>()).contains(i)) {
                int score = (int) Bot.seen;
                ab = testAndSet(ab, new int[]{score, score, i, score}, isMaximizer, i);
                continue;
            }
            Field childField = new Field(field);
            if (childField.addDisc(i, myTurn)) {
                Winner winner = new Winner(childField);
                if (winner.isWinner(i, myTurn)) {
                    int score = (int) (myTurn == player ? Bot.winner : Bot.loser);
                    ab = testAndSet(ab, new int[]{score, score, i, score}, isMaximizer, i);
                    if (isMaximizer && score == Bot.winner) return ab;
                    if (!isMaximizer && score == Bot.loser) return ab;
                    continue;
                }
                if (childField.isFull()) {
                    int score = (int) Bot.draw;
                    ab = testAndSet(ab, new int[]{score, score, i, score}, isMaximizer, i);
                    continue;
                }
                kids.add(new CompressedGameNode(childField, newEval(childField), i));
            }
        }
        if (!isMaximizer) {
            kids.sort(Collections.reverseOrder(new CompressedGameNodeComparator()));
        } else kids.sort(new CompressedGameNodeComparator());
        for (CompressedGameNode kid : kids) {
            int[] abprime = prune(dup(ab), kid.getField(), !isMaximizer, level + 1);
            ab = testAndSet(ab, abprime, isMaximizer, kid.preMove);
            if (abPruning(ab, par, isMaximizer)) {
                break;
            }
        }
        return ab;
    }

    private void myFileReader() {
        try (BufferedReader br = new BufferedReader(new FileReader("/Users/pranav/Downloads/Connect4AI/src/experiences.txt"))) {
            String line = br.readLine();
            while (line != null) {
                String params[] = line.split(" ");
                List<Integer> list = toList(params[1]);
                List<Integer> list1 = myMoves.get(params[0]);
                if (list1 != null) {
                    Set<Integer> s = new HashSet<>(list1);
                    Set<Integer> s1 = new HashSet<>(list);
                    s1.addAll(s);
                    list = new LinkedList<>(s1);
                }
                myMoves.put(params[0], list);
                line = br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    private int[] dup(int[] param) {
        return new int[]{param[0], param[1], param[2], param[3]};
    }

    private boolean abPruning(int[] ab, int[] par, boolean isMaximizer) {
        return (isMaximizer && par[beta] <= ab[alpha]) || (!isMaximizer && par[alpha] >= ab[beta]);
    }

    public int[] testAndSet(int[] ab, int[] abprime, boolean isMaximizer, int move) {
        if (isMaximizer && ab[alpha] < abprime[beta]) {
            ab[alpha] = abprime[beta];
            ab[bestMove] = move;
        } else if (!isMaximizer && ab[beta] > abprime[alpha]) {
            ab[beta] = abprime[alpha];
            ab[bestMove] = move;
        }
        return ab;
    }

    private double monteCarloSearch(Field field, int player) {
        final int totalGames = 50;
        int[] results = new int[3];
        for (int i = 0; i < totalGames; i++) {
            results[simulateGame(field, player)]++;
        }
        return ((results[0] + results[player]) / totalGames);
    }

    private int simulateGame(Field field2, int player) {
        Field field = new Field(field2);
        while (true) {
            List<Integer> validCols = new LinkedList<>();
            for (int i = 0; i < field.getNrColumns(); i++) {
                if (!field.isColumnFull(i)) validCols.add(i);
            }
            //if draw i.e. board is full
            if (validCols.size() == 0) return 0;
            int col = validCols.get((int) (Math.random() * validCols.size()));
            field.addDisc(col, player);
            if (new Winner(field).isWinner(col, player)) return player;
            player = 3 - player;
        }
    }

    private int newEval(Field field) {
        Winner winner = new Winner(field);
        int total = 0;
        for (int i = 0; i < field.getNrRows(); i++) {
            for (int j = 0; j < field.getNrColumns(); j++) {
                int score = winner.checkAlignment2(i, j);
                if (field.getDisc(i, j) == player)
                    total = total + score;
                else total = total - score;
            }
        }
        return total;
    }
}
