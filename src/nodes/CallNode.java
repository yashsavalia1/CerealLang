package nodes;

import java.util.List;

public class CallNode extends Node {
    public Node nodeToCall;
    public List<Node> argumentNodes;

    public CallNode(Node nodeToCall, List<Node> argumentNodes) {
        this.nodeToCall = nodeToCall;
        this.argumentNodes = argumentNodes;
        this.startPosition = nodeToCall.startPosition;

        if (argumentNodes == null || argumentNodes.size() == 0) {
            this.endPosition = nodeToCall.endPosition;
        } else {
            this.endPosition = argumentNodes.get(argumentNodes.size() - 1).endPosition;
        }


    }

    public String toString() {
        String str = "CALL (" ;

        for (Node arg : argumentNodes) {
            if (argumentNodes.get(argumentNodes.size() - 1) != arg)
                str += arg.toString() + ", ";
            else str += arg.toString();
        }

        str += ")";
        return str;
    }

}
