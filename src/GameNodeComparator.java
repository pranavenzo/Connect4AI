import java.util.Comparator;

/**
 * Created by anappp on 6/23/17.
 */
public class GameNodeComparator implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        GameNode g1 = (GameNode) o1;
        GameNode g2 = (GameNode) o2;
        if (g1.getScore() < g2.getScore()) return 1;
        if (g1.getScore() == g2.getScore()) return 0;
        return -1;
    }
}
