package values;

import java.util.ArrayList;
import java.util.List;

public class CList extends Value {

    public List<Value> elements;

    public CList(List<Value> elements) {
        this.elements = elements;
    }

    // Adds element to existing list
    public static CList add(CList list, Value value) {
        list.elements.add(value);
        return list;
    }

    // Creates new list that is the merge of the lists passed
    public static CList merge(CList list, CList list2) {
        List<Value> mergedList = new ArrayList<Value>(list.elements);
        mergedList.addAll(list2.elements);

        CList copy = new CList(mergedList);
        copy.setContext(list.context);

        return copy;
    }

    public static boolean remove(CList list, Value value) {
        return list.elements.remove(value);
    }

    public static Value removeAtIndex(CList list, CNumber num) {
        if (num.isInteger)
            return list.elements.remove(num.value.intValue());
        else
            return null;
    }

    public static void set(CList list, int index, Value value) {
        if (index >= 0 && index < list.elements.size())
            list.elements.set(index, value);
        else
            System.out.println("Fatal List Error");
    }

    @Override
    public CList copy() {
        CList copy = new CList(new ArrayList<Value>(this.elements));
        copy.setPositon(this.startPosition, this.endPosition);
        copy.setContext(this.context);

        return copy;
    }

    @Override
    public String toString() {
        String str = "[";

        for (int i = 0; i < elements.size() - 1; i++) {
            str += elements.get(i).toString() + ", ";
        }

        if (elements.size() != 0) {
            str += elements.get(elements.size() - 1).toString();
        }

        return str + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CList) {
            if (elements.equals(((CList) obj).elements)) {
                return true;
            }
        }

        return false;
    }

}
