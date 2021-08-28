package nodes;

import java.util.List;

import position.Position;

public class ListNode extends Node {

    public List<Node> elementNodes;

    public ListNode(List<Node> elementNodes, Position startPosition, Position endPosition) {
        this.elementNodes = elementNodes;
        this.startPosition = startPosition;
        this.endPosition = endPosition;

    }

    @Override
    public String toString() {
        String str = "[ ";

        for (int i = 0; i < elementNodes.size() - 1; i++) {
            str += elementNodes.get(i).toString() + ", ";
        }

        if (elementNodes.size() != 0)
            str += elementNodes.get(elementNodes.size() - 1).toString();

        str += " ]";

        return str;
    }
}
