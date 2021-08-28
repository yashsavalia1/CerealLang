package nodes;

import tokens.Token;

public class BooleanNode extends Node{

    public Token boolToken;

    public BooleanNode(Token boolToken) {
        this.boolToken = boolToken;
        this.startPosition = boolToken.startPosition;
        this.endPosition = boolToken.endPosition;
    }

    public String toString() {
        return "" + boolToken.boolValue;
    }
    
}
