import java.util.List;
import java.util.Map;

/**
 * Created by anappp on 7/11/17.
 */
public interface Bot {
    double winner = 1000;
    double loser = -1000;
    int makeTurn(Field mfield, int player, Map<String, List<Integer>> banned, long time);
}
