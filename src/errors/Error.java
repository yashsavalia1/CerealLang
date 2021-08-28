package errors;

import position.*;
import run.CerealFile;

import static format.PrinterFormat.*;

public abstract class Error {

    protected String errorName;
    public String details;
    public Position posStart;
    public Position posEnd;
    protected CerealFile cFile;

    public Error(String errorName, String details, Position posStart, Position posEnd, CerealFile cFile) {
        this.errorName = errorName;
        this.details = details;
        this.posStart = posStart;
        this.posEnd = posEnd;
        this.cFile = cFile;
    }

    public String toString() {
        String result = errorName + ": " + details + 
        "\nFile: " + cFile.fileName + ", line " + this.posStart.lineNum;
        result += "\n\n" + stringWithArrows(cFile.fileText, posStart, posEnd);
        return formatError(result);
    }

    
}
