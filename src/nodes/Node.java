package nodes;

import position.Position;

public abstract class Node {
    public Position startPosition;
    public Position endPosition;

    public abstract String toString();

    public String type() {
        return this.getClass().getName();
    }

}
