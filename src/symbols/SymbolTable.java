package symbols;

import java.util.HashMap;
import java.util.Map;

import values.Value;

public class SymbolTable {

	public Map<String, Value> symbols;
	public SymbolTable parent;
	
	public SymbolTable() {
		symbols = new HashMap<>();
	}

	public SymbolTable(SymbolTable parent) {
		this.parent = parent;
		symbols = new HashMap<>();
	}

	public Value get(String name) {
		Value value = symbols.get(name);
		if(value == null && parent != null) {
			return parent.get(name);
		}

		return value;
	}

	public void set(String name, Value value) {
		symbols.put(name, value);
	}

	public boolean remove(String name) {
		Value removedKey = symbols.remove(name);

		return removedKey != null;
	}


}
