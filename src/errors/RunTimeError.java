package errors;

import traceback.Context;
import position.Position;
import run.CerealFile;

import static format.PrinterFormat.*;

public class RunTimeError extends Error {

    public Context context;

    public RunTimeError(String details, Position posStart, Position posEnd, CerealFile cFile, Context context) {
        super("Runtime Error", details, posStart, posEnd, cFile);
        this.context = context;
    }

    public RunTimeError(String errorName, String details, Position posStart, Position posEnd, CerealFile cFile,
            Context context) {
        super(errorName, details, posStart, posEnd, cFile);
        this.context = context;
    }

    public String toString() {

        String result = errorName + ": " + details;
        result += "\n" + this.generateTraceback();
        result += "\n" + stringWithArrows(this.posStart.cFile.fileText, posStart, posEnd);

        return formatError(result);
    }

    private String generateTraceback() {
        String result = "";
        Position position = posStart.copy();
        Context ctx = new Context(context.displayName, context.parent, context.parentEntryPosition);
        int traceback = 0;
        while (ctx != null) {
            traceback++;

            if (traceback <= 20)
                result = "\tFile " + cFile.fileName + ", line " + position.lineNum + ", in " + ctx.displayName + "\n"
                        + result;

            position = ctx.parentEntryPosition;
            ctx = ctx.parent;

        }

        if (traceback > 20) {
            result += "\n\t(and " + (traceback - 20) + " more lines...)\n";
        }

        return "Traceback of call:\n" + result;
    }
}
