package values.functions;

import java.util.List;

import errors.StackOverflowError;
import nodes.*;
import run.CerealFile;
import runtime.Interpreter;
import runtime.RuntimeResult;
import traceback.Context;
import values.*;

public class CFunction extends BaseFunction {

    public Node bodyNode;

    public CFunction(String name, Node bodyNode, List<String> argumentNames) {
        super(name, argumentNames);
        this.bodyNode = bodyNode;
    }

    public CFunction(Node bodyNode, List<String> argumentNames) {
        super(argumentNames);
        this.bodyNode = bodyNode;
    }

    public RuntimeResult execute(CallNode callNode, List<Value> arguments, Context parent, CerealFile cFile) {
        RuntimeResult result = new RuntimeResult();
        Interpreter interpreter = new Interpreter(cFile);

        Context newContext = generateNewContext(parent);

        // Check if arguments match parameters / populate args
        result.register(checkAndPopulateArgs(arguments, newContext, cFile));
        if (result.isError)
            return result;

        Value value = new CNull();
        try {
            value = result.register(interpreter.visit(this.bodyNode, newContext));
            if (result.isError)
                return result;
        } catch (java.lang.StackOverflowError e) {
            result.setError(new StackOverflowError(callNode.startPosition, callNode.endPosition, cFile, newContext));
        }

        //Not return value
        return result.setResult(value);
    }

    @Override
    public CFunction copy() {
        CFunction copy = new CFunction(this.name, this.bodyNode, this.argumentNames);
        copy.setContext(this.context);
        copy.setPositon(this.startPosition, this.endPosition);
        return copy;
    }

    @Override
    public String toString() {
        return "<function " + name + ">";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CFunction) {
            return obj == this;
        }

        return false;
    }

}
