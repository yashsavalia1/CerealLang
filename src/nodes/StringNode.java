package nodes;

import tokens.Token;

public class StringNode extends Node {

    public Token stringToken;

    public StringNode(Token stringToken) {
        this.stringToken = stringToken;
        this.startPosition = stringToken.startPosition;
        this.endPosition = stringToken.endPosition;
    }

    @Override
    public String toString() {
        return stringToken.toString();
    }

}
