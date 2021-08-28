package values;

public class CString extends Value {

    public String valString;

    public CString(String value) {
        this.valString = value;
    }

    public static CString add(CString string1, CString string2) {
        CString sum;
        sum = new CString(string1.valString + string2.valString);

        if (string1.context != null) sum.setContext(string1.context);
        else sum.setContext(string2.context);

        return sum;
    }

    @Override
    public CString copy() {
        CString copy = new CString(this.valString);
        copy.setPositon(this.startPosition, this.endPosition);
        copy.setContext(this.context);
        return copy;
    }

    public String toString() {
        return "\"" + valString + "\"";
    }

    public boolean equals(Object otherString) {
        if (otherString instanceof CString) {
            return this.valString.equals(((CString) otherString).valString);
        }

        return false;
    }
}
 