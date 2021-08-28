package nodes;

import tokens.Token;

public class VariableAssignNode extends Node {

    public Token variableNameToken;
    public Node valueNode;

    public VariableAssignNode(Token variableName, Node valueNode) {
        this.variableNameToken = variableName;
        this.valueNode = valueNode;
        this.startPosition = variableName.startPosition;
        this.endPosition = valueNode.endPosition;
    } 

    public String toString() {
        return "( var " + this.variableNameToken.keyString + " = [" + valueNode.toString() + "] )";
    }

}
