package position;

import run.CerealFile;

public class Position {

    public int index;
    public int lineNum;
    public int columnNum;
    public CerealFile cFile;

    public Position(int index, int lineNum, int columnNum, CerealFile cFile) {
        this.index = index;
        this.lineNum = lineNum;
        this.columnNum = columnNum;
        this.cFile = cFile;
    }

    public Position advance() {
        this.index++;
        this.columnNum++;

        return this;
    }

    public Position advance(Character currentChar) {
        this.index++;
        this.columnNum++;
        if (currentChar != null) {
            if (currentChar == '\n') {
                lineNum++;
                columnNum = 0;
            }
        }

        return this;
    }

    public Position copy() {
        return new Position(index, lineNum, columnNum, cFile);
    }

}
