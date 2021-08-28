package nodes;

import java.util.List;

public class IfNode extends Node {
	public List<Node[]> cases;
	public Node elseCase;

	public IfNode(List<Node[]> cases, Node elseCase) {
		this.cases = cases;
		this.elseCase = elseCase;

		this.startPosition = cases.get(0)[0].startPosition;
		this.endPosition = elseCase == null
				? cases.get(cases.size() - 1)[cases.get(cases.size() - 1).length - 1].endPosition
				: elseCase.endPosition;
	}

	public String toString() {

		String ifStatment = "IF ";

		ifStatment += "( " + cases.get(0)[0] + " ) : { " + cases.get(0)[1] + " }";

		for(int i = 1; i < cases.size(); i++) {
			ifStatment += "\n\tELSEIF ( " + cases.get(i)[0] + " ) : { " + cases.get(i)[1] + " }";
		}

		if(elseCase != null) {
			ifStatment += "\n\tELSE { " + elseCase + " }";
		}

		return ifStatment;

	}

}
