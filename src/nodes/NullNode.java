package nodes;

import tokens.Token;

public class NullNode extends Node {

    public Token nullToken;

    public NullNode(Token nullToken) {
        this.nullToken = nullToken;
        this.startPosition = nullToken.startPosition.copy();
        this.endPosition = nullToken.endPosition.copy();
    }

    public String toString() {
        return "null";
    }

}
