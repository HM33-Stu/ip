package duke;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A simple task manager application.
 * Handles initial setup for components of the main application.
 */
public class Duke {
    //    public static final String DATABASE_PATH = "../data/duke.txt";
    public static final String DATABASE_PATH = "data/duke.txt";
    private DatabaseHandler DatabaseHandler;
    private TaskList taskList;
    private Ui Ui;
    private CommandParser commandParser;

    /**
     * Initializes a Duke instance, loads tasks and components before starting up the application.
     */
    public Duke() {
        Ui = new Ui();
        DatabaseHandler = new DatabaseHandler(DATABASE_PATH);
        try {
            taskList = new TaskList(DatabaseHandler.loadData());
            commandParser = new CommandParser(new Scanner(System.in), this.taskList);
        } catch (DukeBotException e) {
            Ui.showLoadingError();
            taskList = new TaskList();
        }
    }

    /**
     * Given a user input string, invokes the command parser and returns the output in the UI.
     */
    String getResponse(String input) {
        // Create a StringBuilder to hold the output
        StringBuilder outputBuilder = new StringBuilder();

        // Create a PrintStream that writes to the StringBuilder
        PrintStream ps = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                outputBuilder.append((char) b);
            }
        });

        // Save the original System.out
        PrintStream prev = System.out;

        // Set the System.out to the custom PrintStream
        System.setOut(ps);
        // Process the user input
        try {
            this.commandParser.processCommand(input);
            this.save();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            // Restore the original System.out
            System.setOut(prev);
        }
        // Return the captured output
        return outputBuilder.toString();
    }

    /**
     * Saves the current task list to the hard drive.
     */
    private void save() {
        List<String> lines = new ArrayList<>();
        for (Task task : this.taskList.taskList) {
            String stringTask = TaskList.taskToDbString(task);
            lines.add(stringTask);
        }
        this.DatabaseHandler.writeLinesToFile(lines);
    }

    /**
     * Starts up the application and sets up the command parser and user interface.
     */
    public void run() {
        String greeting = "____________________________________________________________\n"
                + " Hey! I'm Hari!"
                + " How may I be of service today?"
                + "____________________________________________________________";

        String goodbye = " Au revoir! Till we meet again!\n"
                + "____________________________________________________________";
        Ui.greet();

        Scanner scanner = new Scanner(System.in); // Create a Scanner object
        String line = scanner.nextLine(); // Get first input

        while (this.commandParser.processCommand(line)) {
            line = scanner.nextLine();
        }
        // Save the tasks from taskList to duke.DatabaseHandler
        this.save();
    }

    public static void main(String[] args) {
        new Duke().run();
    }

}
