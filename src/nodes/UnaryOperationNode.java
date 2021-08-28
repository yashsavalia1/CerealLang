package nodes;

import tokens.Token;

public class UnaryOperationNode extends Node {

	public Node operand;
	public Token operatorToken;

	public UnaryOperationNode(Token opToken, Node operand) {
		this.operatorToken = opToken;
		this.operand = operand;

		this.startPosition = opToken.startPosition;
		this.endPosition = operand.endPosition;
	}

	public Node getOperand() {
		return this.operand;
	}

	public Token getOperatorToken() {
		return operatorToken;
	}

	public String toString() {
		return "(" + operatorToken + ", " + operand + ")";
	}

}