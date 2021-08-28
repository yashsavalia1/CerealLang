package errors;

import position.Position;
import run.CerealFile;
import traceback.Context;

public class StackOverflowError extends RunTimeError {

    public StackOverflowError(Position posStart, Position posEnd, CerealFile cFile, Context context) {
        super("Stack Overflow Error",posStart, posEnd, cFile, context);
    }



}
