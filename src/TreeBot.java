import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by anappp on 7/6/17.
 */
public class TreeBot extends BotStarter {
    @Override
    public int makeTurn(Field mfield, int player, Map<String, List<Integer>> banned) {
        this.banned = banned;
        alphaMap = new HashMap<>();
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
        GameNode gameNode = new GameNode(field);
        LinkedList<GameNode> queue = new LinkedList<>();
        unexploredChildren = new HashMap<>();
        Map<String, GameNode> reverseMap = new HashMap<>();
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
                    GameNode child = new GameNode(childField, nextOne, i, player);

                    /*GameNode longLostChild = reverseMap.get(child.toString());
                    if (longLostChild != null) {
                        longLostChild.passAlphaOrBetaValueUp(nextOne);
                        continue;
                    }*/
                    reverseMap.put(child.toString(), child);
                    child.checkWinner(i, childPlayer);
                    if (child.getScore() == 0) child.setScore();
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
