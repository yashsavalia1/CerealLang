package errors;

import position.Position;
import run.CerealFile;

public class TypeError extends Error {

    public TypeError(String details, Position posStart, Position posEnd, CerealFile cFile) {
        super("Type Error", details, posStart, posEnd, cFile);
    }

}
