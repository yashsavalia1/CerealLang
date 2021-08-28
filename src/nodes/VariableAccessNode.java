package nodes;

import tokens.Token;

public class VariableAccessNode extends Node {

    public Token variableNameToken;

    public VariableAccessNode(Token variableNameToken) {
        this.variableNameToken = variableNameToken;
        this.startPosition = variableNameToken.startPosition;
        this.endPosition = variableNameToken.endPosition;
    }

    public String toString() {

        return variableNameToken.toString();
    }
}
