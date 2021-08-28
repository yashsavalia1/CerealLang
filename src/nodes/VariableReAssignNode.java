package nodes;

import tokens.Token;

public class VariableReAssignNode extends Node {

    public Token variableNameToken;
    public Token operationToken;
    public Node valueNode;


    public VariableReAssignNode(Token variableName, Token operationToken, Node valueNode) {
        this.variableNameToken = variableName;
        this.operationToken = operationToken;
        this.valueNode = valueNode;
        this.startPosition = variableName.startPosition;
        this.endPosition = valueNode.endPosition;
    }

    public String toString() {
        return "(" + this.variableNameToken.keyString + " " + operationToken.type + " [" + valueNode.toString() + "] )";
    }

}
