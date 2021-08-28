package nodes;

import tokens.Token;

public class NumberNode extends Node {

    public Token numberToken;

    public NumberNode(Token numberToken) {
        this.numberToken = numberToken;
        this.startPosition = numberToken.startPosition;
        this.endPosition = numberToken.endPosition;
    }

    public String toString() {
        return numberToken.toString();
    }

}
