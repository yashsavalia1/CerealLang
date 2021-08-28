package errors;

import position.Position;
import run.CerealFile;

public class InvalidSyntaxError extends Error {

    public InvalidSyntaxError(String details, Position posStart, Position posEnd, CerealFile cFile) {
        super("Invalid Syntax", details, posStart, posEnd, cFile);

    }


}
