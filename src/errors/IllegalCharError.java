package errors;

import position.Position;
import run.CerealFile;

public class IllegalCharError extends Error {

    public IllegalCharError(String details, Position posStart, Position posEnd, CerealFile cFile) {
        super("Illegal Character", details, posStart, posEnd, cFile);
    }
}