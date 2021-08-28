package traceback;

import position.Position;
import symbols.SymbolTable;

public class Context {

    public String displayName;
    public Context parent;
    public Position parentEntryPosition;
    public SymbolTable symbolTable;

    public Context(String displayName) {
        this.displayName = displayName;
    }

    public Context(String displayName, Context parent, Position parentEntryPosition) {
        this.displayName = displayName;
        this.parent = parent;
        this.parentEntryPosition = parentEntryPosition;
    }
}
