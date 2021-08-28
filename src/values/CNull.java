package values;

public class CNull extends Value {

    @Override
    public CNull copy() {
        CNull copy = new CNull();
        copy.setPositon(this.startPosition, this.endPosition);
        copy.context = this.context;
        return copy;
    }

    public String toString() {
        return "null";
    }

    public boolean equals(Object obj) {
        if (obj instanceof CNull)
            return true;
        return false;
    }

}
