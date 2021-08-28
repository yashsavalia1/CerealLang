package nodes;

import position.Position;

public class ListGetNode extends Node {

    public Node name;
    public Node index;

    public ListGetNode(Node name, Node index) {
        this.name = name;
        this.index = index;

        this.startPosition = name.startPosition;
        Position end = index.endPosition.copy();
        end.advance();
        this.endPosition = end;
    }

    @Override
    public String toString() {
        return name.toString() + "[ " + index.toString() + " ]";
    }

}
