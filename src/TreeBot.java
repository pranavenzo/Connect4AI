import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TreeBot implements Bot {
    private long start;

    private long time;

    private Map<String, Integer> unexploredChildren;

    private Map<String, GameNode> reverseMap;

    private Field field;

    private int player;

    int max_depth = 8;

    private Map<String, List<Integer>> banned;

    TreeBot() {
    }

    @Override
    public int makeTurn(Field mfield, int player, Map<String, List<Integer>> banned, long time) {
        this.time = time;
        this.start = System.currentTimeMillis();
        this.banned = banned;
        this.field = mfield;
        this.player = player;
        return treePrune(field);
    }

    public void cascadeAlphaBeta(GameNode nextOne) {
        if (nextOne.parent == null) return;
        unexploredChildren.put(nextOne.parent.toString(),
                unexploredChildren.get(nextOne.parent.toString()) - 1);
        while (nextOne.parent != null && unexploredChildren.get(nextOne.parent.toString()) == 0) {
            nextOne = nextOne.parent;
            if (nextOne.parent == null) break;
            nextOne.passAlphaOrBetaValueUp();
            unexploredChildren.put(nextOne.parent.toString(),
                    unexploredChildren.get(nextOne.parent.toString()) - 1);
        }
    }

    private int treePrune(Field field) {
        reverseMap = new HashMap<>();
        unexploredChildren = new HashMap<>();
        GameNode gameNode = new GameNode(field, this.player);
        LinkedList<GameNode> queue = new LinkedList<>();
        reverseMap.put(gameNode.toString(), gameNode);
        queue.add(gameNode);
        while (!queue.isEmpty()) {
            GameNode nextOne = queue.removeFirst();
            unexploredChildren.put(nextOne.toString(), 0);
            if (nextOne.parent != null) {
                nextOne.syncAlphaOrBeta();
            }
            if (nextOne.getScore() == winner || nextOne.getScore() == loser) {
                nextOne.setLeaf();
                nextOne.passAlphaOrBetaValueUp();
                cascadeAlphaBeta(nextOne);
                continue;
            }
            if (nextOne.level == max_depth || nextOne.isFull()) {
                nextOne.setScore();
                nextOne.setLeaf();
                nextOne.passAlphaOrBetaValueUp();
                cascadeAlphaBeta(nextOne);
                continue;
            }
            if (nextOne.parent != null
                    && nextOne.parent.parent != null
                    && nextOne.shouldIPrune()) {
                cascadeAlphaBeta(nextOne);
                continue;
            }
            for (int i = 0; i < field.getNrColumns(); i++) {
                int childPlayer;
                if (nextOne.level % 2 != 0) {
                    childPlayer = 3 - this.player;
                } else childPlayer = this.player;
                Field childField = new Field(nextOne.getField());
                if (childField.addDisc(i, childPlayer)) {
                    /*GameNode c = reverseMap.get(childField.toString());
                    if (unexploredChildren.get(childField.toString()) != null
                            && unexploredChildren.get(childField.toString()) == 0
                            && nextOne.pullAlphaOrBetaDown(c)) {
                        continue;
                    }*/
                    GameNode child = new GameNode(childField, nextOne, i, player);
                    child.checkWinner(i, childPlayer);
                    if (child.getScore() == 0) child.setScore();
                    reverseMap.put(child.toString(), child);
                    nextOne.addChild(i, child);
                    unexploredChildren.put(nextOne.toString(), unexploredChildren.get(nextOne.toString()) + 1);
                }
            }
            nextOne.sortChildren();
            queue.addAll(0, nextOne.getChildren());
        }
        //System.out.println(unexploredChildren.size());
        return gameNode.getBestChild().preMove;
    }
}
