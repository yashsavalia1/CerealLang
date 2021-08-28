package nodes;

public class WhileNode extends Node{
    public Node conditionNode;
    public Node bodyNode;

    public WhileNode(Node condition, Node body) {
        conditionNode = condition;
        bodyNode = body;
        this.startPosition = condition.startPosition;
        this.endPosition = condition.endPosition;
    }

    public String toString() {
        return "{ WHILE " + conditionNode.toString() + " }" + " { " + bodyNode.toString() + " }";
    }
}
