import main.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * The main entry point of the program,
 * where command line arguments are parsed and the main method is called.
 */
public class Launcher {

    /**
    * Takes the file path of the maze as a command line argument and passes them to the main method.
     *
    * @param args The command line arguments.
    */

    public static void main(String[] args) {

        // Initialises the file path and GUI enable variables.
        String filePath = "";
        boolean enableGui = false;

        // Parses the command line arguments.
        if (args.length == 0) {
            String currentLine;
            // Asks the user for the file path if none is provided.
            System.out.println("Please enter program arguments.");
            try {
                BufferedReader commandline = new BufferedReader(new InputStreamReader(System.in));
                currentLine = commandline.readLine();
                args = currentLine.split(" ");
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }

        if (args.length == 1 && args[0].contains("/")) {
            // If the first argument contains a /, it is assumed to be a file path.
            filePath = args[0];
        } else if (args.length == 2 && args[1].contains("/") && args[0].matches("GUI")) {
            // If the second argument contains a /, it is assumed to be a file path and the
            // first argument is assumed to be "GUI".
            filePath = args[1];
            enableGui = true;
        } else {
            System.err.println("Invalid program arguments.");
            Launcher.main(new String[0]);
            return;
        }



        // Calls the main method with the file path and GUI enable variables.
        String[] mainArgs = {filePath, Boolean.toString(enableGui)};
        Main.main(mainArgs);
    }
}
