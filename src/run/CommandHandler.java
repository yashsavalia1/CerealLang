package run;

public class CommandHandler {

    public static String processCommand(String command) {
        if (command.equals("help")) {
            return "\r\nUse the /exit command to exit the shell\r\n\r\nSyntax:\r\n------\r\n\r\n" +
            "You can use the following operators on numbers:\r\n" +
            "'+', '-', '*', '/', '^', '//', '%', '==', '!=', '>', '>=', '<', '<='\r\n\r\n" +
            "You can use the following operators on booleans:\r\n'&', '|', '!', '==', '!='\r\n\r\n" +
            "Set variables using the var keyword:\r\nEx. 'var x = 1'\r\n\r\n" +
            "If you find any bugs contact me!\r\n\r\n";
        } else {
            return format.PrinterFormat.formatError("Invalid Command: /" + command);
        }
    }
}
