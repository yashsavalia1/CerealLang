package values.functions;

import java.util.List;

import nodes.CallNode;
import run.CerealFile;
import runtime.RuntimeResult;
import traceback.Context;
import values.Value;
import values.functions.builtin.Lang;

public class BuiltInFunction extends BaseFunction {

    public BuiltInFunction(String name, List<String> argumentNames) {
        super(name, argumentNames);
    }
    // add trig functions

    public RuntimeResult execute(CallNode callNode, List<Value> arguments, Context parent, CerealFile cFile) {
        RuntimeResult result = new RuntimeResult();
        Context newContext = generateNewContext(parent);

        result.register(checkAndPopulateArgs(arguments, newContext, cFile));
        if (result.isError)
            return result;

        switch (this.name) {
            case "print":
                Lang.print(arguments.get(0));
                break;

            default:
                break;
        }

        return null;
    }

    @Override
    public BuiltInFunction copy() {
        BuiltInFunction copy = new BuiltInFunction(this.name, this.argumentNames);
        copy.setContext(this.context);
        copy.setPositon(this.startPosition, this.endPosition);
        return copy;
    }

    @Override
    public String toString() {
        return "<built-in function " + name + ">";
    }
}
