package format;

import java.util.Arrays;

import position.Position;

public enum PrinterFormat {
    
    
    ANSI_RESET("\u001b[0m"), 
    ANSI_BLACK("\u001b[30m"), 
    ANSI_RED("\u001b[31m"), 
    ANSI_GREEN("\u001b[32m"),
    ANSI_YELLOW("\u001b[33m"), 
    ANSI_BLUE("\u001b[34m"), 
    ANSI_PURPLE("\u001b[35m"), 
    ANSI_CYAN("\u001b[36m"),
    ANSI_WHITE("\u001b[37m");

    private final String value;

    private PrinterFormat(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public static String formatError(String error) {
        return /* ANSI_RED + */ error /* + ANSI_RESET */;
    }

    public static String formatWarn(String warn) {
        return /* ANSI_YELLOW + */ "[WARN] " + warn /* + ANSI_RESET */;
    }

    public static String stringWithArrows(String text, Position start, Position end) {
        String result = "";

        // Calculate Indices
        int startIndex = Math.max(text.substring(0, start.index).indexOf("\n"), 0);
        int endIndex = text.indexOf("\n", startIndex + 1);
        if (endIndex < 0)
            endIndex = text.length();

        // Generate each line
        int lineCount = end.lineNum - start.lineNum + 1;
        for (int i = 0; i < lineCount; i++) {
            // Calculate line columns
            String line = text.substring(startIndex, endIndex);
            int columnStart = i == 0 ? start.columnNum : 0;
            int columnEnd = i == lineCount - 1 ? end.columnNum : line.length() - 1;

            // Append to result
            result += line + "\n";
            char[] spaces = new char[columnStart];
            char[] arrows = new char[columnEnd - columnStart];
            Arrays.fill(spaces, ' ');
            Arrays.fill(arrows, '^');
            result += (new String(spaces) + new String(arrows));

            // recalculate indices
            startIndex = endIndex;
            endIndex = text.indexOf('\n', startIndex + 1);
            if (endIndex < 0)
                endIndex = text.length();
        }

        return result.replaceAll("\t", "");
    }

}
