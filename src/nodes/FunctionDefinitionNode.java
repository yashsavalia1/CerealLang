package nodes;

import java.util.List;

import tokens.Token;

public class FunctionDefinitionNode extends Node {
    public Token variableName;
    public List<Token> arguments;
    public Node bodyNode;

    public FunctionDefinitionNode(Token variableTok, List<Token> arguments, Node body) {
        this.variableName = variableTok;
        this.arguments = arguments;
        this.bodyNode = body;

        this.startPosition = variableTok.startPosition;
        this.endPosition = body.endPosition;
    }

    public FunctionDefinitionNode(List<Token> arguments, Node body) {
        this.arguments = arguments;
        this.bodyNode = body;

        if (arguments != null && arguments.size() > 0) {
            this.startPosition = arguments.get(0).startPosition;
        } else {
            this.startPosition = body.startPosition;
        }

        this.endPosition = body.endPosition;
    }

    public String toString() {
        String val;
        if (variableName != null) {
            val = variableName.toString() + " (";
        } else {
            val = "<anonymous> (";
        }

        for (int i = 0; i < arguments.size() - 1; i++) {
            val += arguments.get(i).toString() + ", ";
        }

        if (arguments.size() != 0) {
            val += arguments.get(arguments.size() - 1);
        }

        val += ") -> ";

        val += "{ " + bodyNode.toString() + " }";

        return val;
    }

}
