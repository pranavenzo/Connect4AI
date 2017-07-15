import java.util.Comparator;

/**
 * Created by anappp on 7/14/17.
 */
public class CompressedGameNodeComparator implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        CompressedGameNode g1 = (CompressedGameNode) o1;
        CompressedGameNode g2 = (CompressedGameNode) o2;
        if (g1.getScore() < g2.getScore()) return 1;
        if (g1.getScore() == g2.getScore()) return 0;
        return -1;
    }
}
