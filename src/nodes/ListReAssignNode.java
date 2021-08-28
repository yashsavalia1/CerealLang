package nodes;

import tokens.Token;

public class ListReAssignNode extends Node {

    public Node listName;
    public Node index;
    public Node newValue;
    public Token operationToken;

    public ListReAssignNode(Node listName, Node index,Token operationToken, Node newValue) {
        this.listName = listName;
        this.index = index;
        this.newValue = newValue;
        this.operationToken = operationToken;
    }

    @Override
    public String toString() {
        return null;
    }
}
