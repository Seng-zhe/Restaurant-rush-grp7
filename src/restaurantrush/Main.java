package restaurantrush;

import restaurantrush.boundary.ConsoleUI;
import restaurantrush.boundary.InputReader;
import restaurantrush.control.GameController;

/**
 * Entry point for Restaurant Rush.
 */
public final class Main {

    private Main() {
    }

    /**
     * Starts a game in the base setting.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        InputReader input = new InputReader(System.in, System.out);
        new ConsoleUI(new GameController(), input, System.out).run();
    }
}
