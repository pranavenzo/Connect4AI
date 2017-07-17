/**
 * Created by anappp on 7/14/17.
 */
public class CompressedGameNode {
    Field field;
    int score;
    int preMove;

    public CompressedGameNode(Field field, int score, int preMove) {
        this.field = field;
        this.score = score;
        this.preMove = preMove;
    }

    public int getPreMove() {
        return preMove;
    }

    public Field getField() {
        return field;
    }

    public int getScore() {
        return score;
    }
}
