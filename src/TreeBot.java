import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class TreeBot implements Bot {
    private long start;

    private long time;

    private Map<String, Integer> unexploredChildren;

    private Map<String, GameNode> reverseMap;

    private Field field;

    private int player;

    int max_depth = 8;

    private Map<String, List<Integer>> banned;

    static int moveMade;

    static double maxAlpha;

    TreeBot() {
        banned = new HashMap<>();
        fillMap();
    }

    private void setMax_depth() {
        /*int full = 0;
        for (int i = 0; i < field.getNrColumns(); i++) {
            if (field.isColumnFull(i)) full++;
        }
        max_depth = 8 + full;*/
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

    private int getMax_depth() {
        return max_depth;
    }

    @Override
    public int makeTurn(Field mfield, int player, Map<String, List<Integer>> banned, long time) {
        this.time = time;
        this.start = System.currentTimeMillis();
        this.field = mfield;
        this.player = player;
        setMax_depth();
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

    public int treePrune(Field field) {
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
            if (nextOne.getScore() == winner || nextOne.getScore() == loser || nextOne.getScore() == seen) {
                nextOne.setLeaf();
                nextOne.passAlphaOrBetaValueUp();
                cascadeAlphaBeta(nextOne);
                continue;
            }
            if (nextOne.level == getMax_depth() || nextOne.isFull()) {
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
                List<Integer> x = banned.get(nextOne.toString());
                if (childField.addDisc(i, childPlayer)) {
                    GameNode c = reverseMap.get(childField.toString());
                    if (unexploredChildren.get(childField.toString()) != null
                            && unexploredChildren.get(childField.toString()) == 0
                            && nextOne.pullAlphaOrBetaDown(c)) {
                        continue;
                    }
                    GameNode child = new GameNode(childField, nextOne, i, player);
                    child.checkWinner(i, childPlayer);
                    if (child.getScore() == 0) child.setScore();

                    if (x != null && x.contains(i)) {
                        child.setScore(seen);
                    }
                    reverseMap.put(child.toString(), child);
                    nextOne.addChild(i, child);
                    unexploredChildren.put(nextOne.toString(), unexploredChildren.get(nextOne.toString()) + 1);
                }
            }
            nextOne.sortChildren();
            queue.addAll(0, nextOne.getChildren());
        }
        //System.out.println(unexploredChildren.size());
        if(gameNode.getBestChild() == null) {
            for(GameNode c: gameNode.getChildren()) {
                c.passAlphaOrBetaValueUp();
            }
        }
        return gameNode.getBestChild().preMove;
    }
}
