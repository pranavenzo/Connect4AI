import java.util.Scanner;

/**
 * Created by anappp on 6/19/17.
 */
public class Tool {
    public static int rows = 6;
    public static int columns = 7;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNext()) {
            String line = scanner.nextLine();
            print(line);
        }
    }

    public static char computeTurn(Field field) {
        int r = 0;
        int y = 0;
        for (int i = 0; i < field.getNrRows(); i++) {
            for (int j = 0; j < field.getNrColumns(); j++) {
                if (field.getDisc(i, j) == 1) r++;
                else if (field.getDisc(i, j) == 2) y++;
            }
        }
        if (r >= y) return 'Y';
        return 'R';
    }

    public static void print(String line) {
        String board = line.split(" ")[0];
        Field field = new Field(7, 6);
        field.parseFromString(board);
        Winner winner = new Winner(field);
        winner.printScreen();

        System.out.println(line.split(" ")[1] + " " + computeTurn(field));
    }
}
