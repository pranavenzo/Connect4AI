import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by pranav on 12/19/17.
 */
public class MCTSGameNode {
    private double N;
    private double Q;

    public double getN() {
        return N;
    }

    public double getQ() {
        return Q;
    }

    public static double getC() {
        return c;
    }

    private String identifier;
    private int preMove;
    private int numChildren;
    private List<Integer> unexploredChildren;

    public int getPlayer() {
        return player;
    }

    @JsonIgnore
    public int getOpponent() {
        return 3 - player;
    }

    private int player;

    MCTSGameNode() {
    }

    MCTSGameNode(int preMove, int numCols, String parentIdentifier,
                 Map<String, MCTSGameNode> vertexMap, int player) {
        this.preMove = preMove;
        this.numChildren = 0;
        this.unexploredChildren = new LinkedList<>();
        for (int i = 0; i < numCols; i++) {
            //if (!vertexMap.containsKey(parentIdentifier + preMove + i)) {
                unexploredChildren.add(i);
            //}
        }
        identifier = parentIdentifier + preMove;
        this.player = player;
    }

    public int getNumChildren() {
        return numChildren;
    }

    public int getPreMove() {
        return preMove;
    }

    public List<Integer> getUnexploredChildren() {
        return unexploredChildren;
    }

    public String getIdentifier() {
        return identifier;
    }

    @JsonIgnore
    public String getParentIdentifier() {
        return identifier.substring(0, identifier.length() - 1);
    }

    public void incrementN() {
        this.N++;
    }

    public void modifyQ(int delta) {
        this.Q += delta;
    }

    @JsonIgnore
    public int getRandomAction() {
        int index = (int) (Math.random() * unexploredChildren.size());
        numChildren++;
        return unexploredChildren.remove(index);
    }

    @JsonIgnore
    private static final double c = 0.707;

    @JsonIgnore
    public double getScore(MCTSGameNode parent, int explore) {
        return this.Q / this.N + explore * c * Math.sqrt(2 * Math.log(parent.N) / this.N);
    }

    public void setN(double n) {
        N = n;
    }

    public void setQ(double q) {
        Q = q;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setPreMove(int preMove) {
        this.preMove = preMove;
    }

    public void setNumChildren(int numChildren) {
        this.numChildren = numChildren;
    }

    public void setUnexploredChildren(List<Integer> unexploredChildren) {
        this.unexploredChildren = unexploredChildren;
    }

    public void setPlayer(int player) {
        this.player = player;
    }
}
