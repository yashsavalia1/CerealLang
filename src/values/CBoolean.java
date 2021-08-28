package values;

public class CBoolean extends Value {

    public Boolean bool;

    public CBoolean(Boolean bool) {
        this.bool = bool;
    }

    public static CBoolean and(CBoolean bool1, CBoolean bool2) {
        return new CBoolean(bool1.bool && bool2.bool);
    }

    public static CBoolean or(CBoolean bool1, CBoolean bool2) {
        return new CBoolean(bool1.bool || bool2.bool);
    }

    public static CBoolean not(CBoolean bool1) {
        return new CBoolean(!bool1.bool);
    }

    public static CBoolean isEqual(CBoolean bool1, CBoolean bool2) {
        return new CBoolean(bool1.bool == bool2.bool);
    }

    public static CBoolean notEqual(CBoolean bool1, CBoolean bool2) {
        return new CBoolean(bool1.bool != bool2.bool);
    }

    @Override
    public CBoolean copy() {
        CBoolean copy = new CBoolean(this.bool);
        copy.setPositon(this.startPosition, this.endPosition);
        copy.context = this.context;
        return copy;
    }

    @Override
    public String toString() {
        return bool.toString();
    }

    @Override
    public boolean equals(Object secondBoolean) {
        if (secondBoolean instanceof CBoolean)
            return this.bool == ((CBoolean) secondBoolean).bool;
        return false;
    }
}
