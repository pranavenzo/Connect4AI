import java.util.*;

public class GameNode {
    int preMove;

    private Field field;

    Map<GameNode, Integer> movesMap;

    GameNode parent;

    private LinkedList<GameNode> children;

    GameNode bestChild;

    int numChildren;

    double alpha;

    double beta;

    private double score;

    int player;

    int level;

    private boolean isMaximizer;

    Winner winner;

    GameNode(Field field) {
        winner = new Winner(field);
        this.field = field;
        movesMap = new HashMap<>();
        this.numChildren = 0;
        children = new LinkedList<>();
        alpha = Integer.MIN_VALUE;
        beta = Integer.MAX_VALUE;
        isMaximizer = true;
        this.level = 0;
        preMove = -1;
    }

    public GameNode(GameNode gameNode, int preMove, GameNode parent) {
        this.preMove = preMove;
        this.field = new Field(gameNode.getField());
        this.movesMap = new HashMap<>(gameNode.movesMap);
        this.parent = parent;
        this.children = new LinkedList<>(gameNode.children);
        this.alpha = gameNode.alpha;
        this.beta = gameNode.beta;
        this.score = gameNode.score;
        this.player = gameNode.player;
        this.level = parent.level + 1;
        this.isMaximizer = !parent.isMaximizer;
    }

    GameNode(Field field, GameNode parent, int preMove, int player) {
        movesMap = new HashMap<>();
        this.numChildren = 0;
        children = new LinkedList<>();
        this.parent = parent;
        winner = new Winner(field);
        this.field = field;
        alpha = Integer.MIN_VALUE;
        beta = Integer.MAX_VALUE;
        this.level = parent.level + 1;
        this.isMaximizer = !parent.isMaximizer;
        this.preMove = preMove;
        this.player = player;
    }

    public double getScore() {
        return score;
    }

    public boolean shouldIPrune() {
        if (!isMaximizer && parent.alpha >= parent.parent.beta) {
            return true;
        } else if (isMaximizer && parent.beta <= parent.parent.alpha) {
            return true;
        }
        return false;
    }

    public boolean isFull() {
        return field.isFull();
    }

    public void syncAlphaOrBeta() {
        this.alpha = parent.alpha;
        this.beta = parent.beta;
    }

    public double getKey() {
        if (isMaximizer) return alpha;
        return beta;
    }

    public void setKey(double keyValue) {
        if (isMaximizer) this.alpha = keyValue;
        else this.beta = keyValue;
    }

    public void passAlphaOrBetaValueUp(GameNode parent) {
        if (parent != null) {
            if (parent.getKey() < this.getKey() && !isMaximizer) {
                parent.setKey(this.getKey());
                parent.bestChild = this;
            } else if (parent.getKey() > this.getKey() && isMaximizer) {
                parent.setKey(this.getKey());
                parent.bestChild = this;
            }
        }
    }

    public boolean pullAlphaOrBetaDown(GameNode child) {
        if (child != null) {
            if (isMaximizer && this.alpha < child.beta) {
                this.alpha = child.beta;
                this.bestChild = child;
            } else if (!isMaximizer && this.beta > child.alpha) {
                this.beta = child.alpha;
                this.bestChild = child;
            }
            return true;
        }
        return false;
    }

    public void passAlphaOrBetaValueUp() {
        this.passAlphaOrBetaValueUp(this.parent);
    }

    public Field getField() {
        return field;
    }

    public void setLeaf() {
        this.alpha = this.score;
        this.beta = this.score;
    }

    public boolean isMaximizer() {
        return isMaximizer;
    }

    public GameNode getBestChild() {
        return bestChild;
    }

    public double getCorrectLetter() {
        if (isMaximizer) return alpha;
        return beta;
    }

    public Integer getBestMove() {
        return this.movesMap.get(getBestChild());
    }

    public void setScore() {
        this.score = newEval();
    }

    public void checkWinner(int col, int myBot) {
        if (winner.isWinner(col, myBot)) {
            if (myBot == player) this.score = BotStarter.winner;
            else this.score = BotStarter.loser;
            this.setLeaf();
        }
    }

    private double newEval() {
        Winner winner = new Winner(this.field);
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

    private double evaluationFunction() {

        Field pruneField = this.field;
        Winner oppWinner = new Winner(pruneField);
        Winner winner = new Winner(pruneField);
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
        //return 0;
    }

    private int scoreForPos(int i, int j, Field pruneField) {
        if (pruneField.getDisc(i, j) == 0) return 0;
        int sign = Math.abs(pruneField.getDisc(i, j) - player) * -1;
        return 0;
    }

    public void sortChildren() {
        if (!isMaximizer) {
            children.sort(Collections.reverseOrder(new GameNodeComparator()));
        } else children.sort(new GameNodeComparator());
    }

    @Override
    public String toString() {
        return field.toString();
    }

    public LinkedList<GameNode> getChildren() {
        return children;
    }

    public void addChild(int index, GameNode child) {
        children.add(child);
        numChildren++;
        child.preMove = index;
        movesMap.put(child, index);
    }

    public void printField() {
        winner.printScreen();
    }
}
