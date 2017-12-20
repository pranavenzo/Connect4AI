import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by pranav on 12/19/17.
 */
public class MCTSGameNode {
    private double N;
    private double Q;
    private String identifier;
    private int preMove;
    private int numChildren;
    private List<Integer> unexploredChildren;

    public int getPlayer() {
        return player;
    }

    public int getOpponent() {
        return 3 - player;
    }

    private int player;

    MCTSGameNode(int preMove, int numCols, String parentIdentifier,
                 Map<String, MCTSGameNode> vertexMap, int player) {
        this.preMove = preMove;
        this.numChildren = 0;
        this.unexploredChildren = new LinkedList<>();
        for (int i = 0; i < numCols; i++) {
            if (!vertexMap.containsKey(parentIdentifier + preMove + i)) {
                unexploredChildren.add(i);
            }
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

    public String getParentIdentifier() {
        return identifier.substring(0, identifier.length() - 1);
    }

    public void incrementN() {
        this.N++;
    }

    public void modifyQ(int delta) {
        this.Q += delta;
    }

    public int getRandomAction() {
        int index = (int) (Math.random() * unexploredChildren.size());
        numChildren++;
        return unexploredChildren.remove(index);
    }

    private static final double c = 0.5;

    public double getScore(MCTSGameNode parent, int explore) {
        return this.Q / this.N + explore * c * Math.sqrt(Math.log(parent.N) / this.N);
    }
}
