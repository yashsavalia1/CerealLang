package errors;

import position.Position;
import run.CerealFile;

public class InvalidNumberError extends Error {

    public InvalidNumberError(String details, Position posStart, Position posEnd, CerealFile cFile) {
        super("Invalid Number", details, posStart, posEnd, cFile);
        
    }

}
