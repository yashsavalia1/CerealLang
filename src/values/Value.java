package values;

import position.Position;
import traceback.Context;

public abstract class Value {

    public Position startPosition;
    public Position endPosition;
    public Context context;


    public void setPositon(Position startPosition, Position endPosition) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public abstract Value copy();

    public abstract String toString();

    public abstract boolean equals(Object obj);
}
