import java.util.Scanner;

public class Input {
    public static final Scanner reader = new Scanner(System.in);

    public static int readInt() {
        String line = reader.nextLine();
        return Integer.parseInt(line);
    }
}