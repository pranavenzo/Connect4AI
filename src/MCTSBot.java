import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by pranav on 12/19/17.
 */
public class MCTSBot implements Bot {
    private final int numCols = 7;
    private final int numRows = 6;
    private Map<String, MCTSGameNode> vertexMap;
    private int myPlayer;

    MCTSBot() {
        vertexMap = new HashMap<>();
    }

    public int getMapSize() {
        return vertexMap.size();
    }

    public void writeHere() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        PrintWriter writer = new PrintWriter(new FileOutputStream(new File("tree.txt"), false));
        writer.write("[");
        String toW = "";
        for (String keys : vertexMap.keySet()) {
            MCTSGameNode toWrite = vertexMap.get(keys);
            String s = objectMapper.writeValueAsString(toWrite);
            toW += (s + ",");
        }
        writer.write(toW.substring(0, toW.length() - 1));
        writer.write("]");
        writer.close();
    }

    public void readFromFile() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<MCTSGameNode> myObjects;
        try {
            myObjects = mapper.readValue(jsonFile(), new TypeReference<List<MCTSGameNode>>() {
            });
        } catch (com.fasterxml.jackson.databind.JsonMappingException e) {
            e.printStackTrace();
            return;
        }
        for (MCTSGameNode x : myObjects) {
            vertexMap.put(x.getIdentifier(), x);
        }
    }

    private File jsonFile() {
        return new File("tree.txt");
    }

    @Override
    public int makeTurn(Field mfield, int player, Map<String,
            List<Integer>> banned, long time, String moves) {
        myPlayer = player;
        int numIterations = 3000;
        MCTSGameNode v0 = vertexMap.getOrDefault(moves, new MCTSGameNode(9, numCols,
                "", vertexMap, player));
        vertexMap.put(v0.getIdentifier(), v0);
        for (int i = 0; i < numIterations; i++) {
            Object[] ret = treePolicy(v0, player);
            MCTSGameNode vl = (MCTSGameNode) ret[0];
            int currPlayer = (Integer) ret[1];
            int delta = -1;
            try {
                delta = defaultPolicy(vl, currPlayer);
            } catch (Exception e) {
                e.printStackTrace();
            }
            backup(vl, delta, currPlayer);
        }
        return bestChild(v0, 0).getPreMove();
    }

    public Object[] treePolicy(MCTSGameNode v, int currPlayer) {
        while (true) {
            if (v.getNumChildren() < numCols) {
                try {
                    return new Object[]{expand(v), currPlayer};
                } catch (Exception e) {
                    throw e;
                }
            }
            currPlayer = -currPlayer;
            v = bestChild(v, 1);
        }
    }

    public void backup(MCTSGameNode v, int qValue, int currPlayer) {
        while (v != null) {
            v.incrementN();
            v.modifyQ(qValue * (-currPlayer));
            v = vertexMap.get(v.getParentIdentifier());
            currPlayer = -currPlayer;
        }
    }

    public int defaultPolicy(MCTSGameNode v, int player) {
        Field field = generateState(v);
        int result = 0;
        int numRuns = 1;
        for (int i = 0; i < numRuns; i++) {
            result += simulateGame(field, player);
        }
        return (int) Math.round(result / (1.0 * numRuns));
    }

    public int convertResult(int result, int player) {
        if (player == myPlayer) {
            if (result == player) return 1;
            if (result == 3 - player) return -1;
        } else {
            if (result == player) return -1;
            if (result == 3 - player) return 1;
        }
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
            player = -player;
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

    public Field generateState(MCTSGameNode v) {
        int player = 1;
        String chars[] = v.getIdentifier().split("");
        Field field = new Field(numCols, numRows);
        for (int i = 1; i < chars.length; i++) {
            field.addDisc(Integer.parseInt(chars[i]), player);
            player = -player;
        }
        return field;
    }
}
