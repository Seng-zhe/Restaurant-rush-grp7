package restaurantrush.boundary;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Reads the player's input safely, re-prompting until it is valid.
 *
 * <p>Works the same when typing and when a demo file is fed in with
 * {@code java ... < demo/file.txt}. In a replay, each answer read from the file
 * is echoed so the transcript reads like a live game. Lines starting with
 * {@code #} are comments and are skipped, so demo files can be annotated.</p>
 */
public class InputReader {

    private final Scanner scanner;
    private final PrintStream out;
    private final boolean echo;

    /**
     * Creates a reader.
     *
     * @param in  where input comes from, usually {@code System.in}
     * @param out where prompts go, usually {@code System.out}
     */
    public InputReader(InputStream in, PrintStream out) {
        this.scanner = new Scanner(in);
        this.out = out;
        this.echo = System.console() == null;
    }

    /**
     * Asks for a whole number within a range, repeating until one is given.
     *
     * @param prompt text shown before the input
     * @param min    smallest accepted value
     * @param max    largest accepted value
     * @return a number between {@code min} and {@code max}
     * @throws InputClosedException if input ends, e.g. a demo file runs out
     */
    public int readInt(String prompt, int min, int max) {
        while (true) {
            String line = readLine(prompt);
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
                out.println("  Please enter a number from " + min + " to " + max + ".");
            } catch (NumberFormatException e) {
                out.println("  \"" + line + "\" is not a number. Please try again.");
            }
        }
    }

    /**
     * Asks for a customer number such as {@code 3} or {@code C3}.
     *
     * @param prompt text shown before the input
     * @return the customer number
     * @throws InputClosedException if input ends
     */
    public int readCustomerId(String prompt) {
        while (true) {
            String line = readLine(prompt);
            String digits = line.toUpperCase().startsWith("C") ? line.substring(1) : line;
            try {
                return Integer.parseInt(digits.trim());
            } catch (NumberFormatException e) {
                out.println("  Enter a customer number, e.g. 2 or C2.");
            }
        }
    }

    private String readLine(String prompt) {
        while (true) {
            out.print(prompt);
            String line;
            try {
                line = scanner.nextLine().trim();
            } catch (NoSuchElementException e) {
                out.println();
                throw new InputClosedException();
            }
            if (line.startsWith("#")) {
                out.println(echo ? line : "");
                continue;
            }
            if (echo) {
                out.println(line);
            }
            if (!line.isEmpty()) {
                return line;
            }
        }
    }

    /** Thrown when there is no more input to read. */
    public static class InputClosedException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        /** Creates the exception with a standard message. */
        public InputClosedException() {
            super("Input ended before the game finished.");
        }
    }
}
