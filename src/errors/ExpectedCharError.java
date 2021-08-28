package errors;

import position.Position;
import run.CerealFile;

public class ExpectedCharError extends Error {

    public ExpectedCharError(String details, Position posStart, Position posEnd, CerealFile cFile) {
        super("Expected Character", details, posStart, posEnd, cFile);

    }
}
