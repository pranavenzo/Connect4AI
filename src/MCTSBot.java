import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by pranav on 12/19/17.
 */
public class MCTSBot implements Bot {
    private final int numCols = 7;
    private final int numRows = 6;
    private Map<String, MCTSGameNode> vertexMap;

    MCTSBot() {
        vertexMap = new HashMap<>();
    }

//    private void writeHere() throws IOException {
//        PrintWriter writer = new PrintWriter(new FileOutputStream(new File("tree.txt"), false));
//        for (String keys : vertexMap.keySet()) {
//            writer.println(keys + " " + (Arrays.toString(banned.get(keys).toArray()).replaceAll(" ", "")));
//        }
//        writer.close();
//    }
//
//    public void readFromFile() {
//    }

    @Override
    public int makeTurn(Field mfield, int player, Map<String,
            List<Integer>> banned, long time, String moves) {
        int numIterations = 10000;
        MCTSGameNode v0 = vertexMap.getOrDefault(moves, new MCTSGameNode(9, numCols,
                "", vertexMap, player));
        vertexMap.put(v0.getIdentifier(), v0);
        for (int i = 0; i < numIterations; i++) {
            MCTSGameNode vl = treePolicy(v0);
            int delta = defaultPolicy(vl, player);
            backup(vl, delta);
        }
        return bestChild(v0, 0).getPreMove();
    }

    public MCTSGameNode treePolicy(MCTSGameNode v) {
        while (true) {
            if (v.getNumChildren() < numCols) {
                return expand(v);
            }
            v = bestChild(v, 1);
        }
    }

    public void backup(MCTSGameNode v, int qValue) {
        while (v != null) {
            v.incrementN();
            v.modifyQ(qValue);
            v = vertexMap.get(v.getParentIdentifier());
        }
    }

    public int defaultPolicy(MCTSGameNode v, int player) {
        Field field = generateState(v, player);
        int result = simulateGame(field, player);
        if (result == player) return 1;
        if (result == 3 - player) return -1;
        return 0;
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
            int col = getRandomValue(validCols);
            field.addDisc(col, player);
            if (new Winner(field).isWinner(col, player)) return player;
            player = 3 - player;
        }
    }

    private MCTSGameNode expand(MCTSGameNode v) {
        int move = v.getRandomAction();
        MCTSGameNode nextNode = new MCTSGameNode(move, numCols,
                v.getIdentifier(), vertexMap, v.getOpponent());
        vertexMap.put(nextNode.getIdentifier(), nextNode);
        return nextNode;
    }

    private MCTSGameNode bestChild(MCTSGameNode v, int explore) {
        String parentId = v.getIdentifier();
        List<Integer> equalIndices = new LinkedList<>();
        MCTSGameNode bestChild = vertexMap.get(parentId + 0);
        double max = bestChild.getScore(v, explore);
        equalIndices.add(0);
        for (int i = 1; i < numCols; i++) {
            MCTSGameNode temp = vertexMap.get(parentId + i);
            double x = temp.getScore(v, explore);
            if (max < x) {
                max = x;
                equalIndices = new LinkedList<>();
                equalIndices.add(i);
            }
            if (max == x) {
                equalIndices.add(i);
            }
        }
        return vertexMap.get(parentId + getRandomValue(equalIndices));
    }

    private int getRandomValue(List<Integer> l) {
        int index = (int) (Math.random() * l.size());
        return l.get(index);
    }

    public Field generateState(MCTSGameNode v, int player) {
        String chars[] = v.getIdentifier().split("");
        Field field = new Field(numCols, numRows);
        for (int i = 1; i < chars.length; i++) {
            field.addDisc(Integer.parseInt(chars[i]), player);
        }
        return field;
    }
}
