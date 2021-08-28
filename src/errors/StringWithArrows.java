package errors;

import java.util.Arrays;

import position.Position;

@Deprecated
public class StringWithArrows {

    @Deprecated
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
