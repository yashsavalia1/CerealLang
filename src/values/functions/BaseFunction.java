package values.functions;

import java.util.List;

import errors.RunTimeError;
import run.CerealFile;
import runtime.RuntimeResult;
import symbols.SymbolTable;
import traceback.Context;
import values.Value;

public abstract class BaseFunction extends Value {

    public String name;
    public List<String> argumentNames;

    public BaseFunction(String name, List<String> argumentNames) {
        if (name != null)
            this.name = name;
        else
            this.name = "<anonymous>";
        this.argumentNames = argumentNames;
    }

    public BaseFunction(List<String> argumentNames) {
        this.name = "<anonymous>";
        this.argumentNames = argumentNames;
    }

    public Context generateNewContext(Context parent) {
        Context newContext = new Context(this.name, parent, this.startPosition);
        newContext.symbolTable = new SymbolTable(newContext.parent.symbolTable);
        return newContext;
    }

    public RuntimeResult checkArgs(List<Value> arguments, CerealFile cFile) {
        RuntimeResult result = new RuntimeResult();

        if (arguments.size() > argumentNames.size()) {
            result.setError(new RunTimeError(
                    arguments.size() - argumentNames.size() + " too many arguments passed into '" + this.name + "'",
                    this.startPosition, this.endPosition, cFile, this.context));
            return result;
        }

        if (arguments.size() < argumentNames.size()) {
            result.setError(new RunTimeError(
                    argumentNames.size() - arguments.size() + " too few arguments passed into '" + this.name + "'",
                    this.startPosition, this.endPosition, cFile, this.context));
            return result;
        }

        return result;
    }

    public void populateArgs(List<Value> arguments, Context newContext) {
        for (int i = 0; i < arguments.size(); i++) {
            String argName = argumentNames.get(i);
            Value argument = arguments.get(i);
            argument.setContext(newContext);
            newContext.symbolTable.set(argName, argument);
        }
    }

    public RuntimeResult checkAndPopulateArgs(List<Value> arguments, Context newContext, CerealFile cFile) {
        RuntimeResult result = new RuntimeResult();

        result.register(checkArgs(arguments, cFile));

        if(result.isError) return result;

        populateArgs(arguments, newContext);
        return result;
    }

    @Override
    public Value copy() {
        return null;
    }

    public String toString() {
        return null;
    }

    public boolean equals(Object obj) {
        return false;
    }

}
