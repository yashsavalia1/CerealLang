package nodes;

import tokens.Token;

public class BinaryOperationNode extends Node {

    public Node leftNode;
    public Node rightNode;
    public Token operatorToken;

    public BinaryOperationNode(Node leftNode, Token opToken, Node rightNode) {
        this.operatorToken = opToken;
        this.leftNode = leftNode;
        this.rightNode = rightNode;
        this.startPosition = leftNode.startPosition;
        this.endPosition = rightNode.endPosition;
    }

    public String toString() {
        return "(" + this.leftNode.toString() + ", " + operatorToken.toString() + ", " + this.rightNode.toString()
                + ")";
    }
}
