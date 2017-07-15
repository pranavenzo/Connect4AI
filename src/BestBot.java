import java.util.*;

/**
 * Created by anappp on 7/14/17.
 */
public class BestBot implements Bot {
    private static int outputMove;

    private int player;

    private int opponent;

    private Map<String, int[]> visited;

    private final int alpha = 0;

    private final int beta = 1;

    private int maxDepth;

    BestBot() {
        visited = new HashMap<>();
        maxDepth = 5;
    }

    private void setMaxdepth(Field field) {
        int full = 0;
        for (int i = 0; i < field.getNrColumns(); i++) {
            if (field.isColumnFull(i)) full++;
        }
        maxDepth = 8 + full;
    }

    @Override
    public int makeTurn(Field mfield, int player, Map<String, List<Integer>> banned, long time) {
        //setMaxdepth(mfield);
        this.player = player;
        this.opponent = 3 - player;
        int[] defAb = new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE};
        prune(defAb, mfield, true, 0);
        return outputMove;
    }

    private int[] prune(int[] ab, Field field, boolean isMaximizer, int level) {
        if (level == maxDepth) {
            int score = newEval(field);
            return new int[]{score, score};
        }
        List<CompressedGameNode> kids = new LinkedList<>();
        for (int i = 0; i < field.getNrColumns(); i++) {
            Field childField = new Field(field);
            if (childField.addDisc(i, isMaximizer ? player : opponent)) {
                int[] existing = visited.get(childField.toString());
                if (existing != null) {
                    ab = testAndSet(dup(ab), existing, isMaximizer, i);
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
            if (abPruning(dup(ab), abprime, isMaximizer)) break;
            ab = testAndSet(dup(ab), abprime, isMaximizer, kid.preMove);
        }
        // visited.put(field.toString(), ab);
        return ab;
    }

    int[] dup(int[] param) {
        return new int[]{param[0], param[1]};
    }

    private boolean abPruning(int[] ab, int[] abprime, boolean isMaximizer) {
        return isMaximizer && abprime[beta] > ab[beta] || !isMaximizer && abprime[alpha] < ab[alpha];
    }

    public int[] testAndSet(int[] ab, int[] abprime, boolean isMaximizer, int move) {
        if (isMaximizer && ab[alpha] < abprime[beta]) {
            ab[alpha] = abprime[beta];
            outputMove = move;
        } else if (!isMaximizer && ab[beta] > abprime[alpha]) {
            ab[beta] = abprime[alpha];
            outputMove = move;
        }
        return dup(ab);
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
