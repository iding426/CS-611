import java.util.Scanner;

public abstract class Input {
    protected Scanner scanner;

    public Input() {
        scanner = new Scanner(System.in);
    }

    public abstract String getUsername();
}
