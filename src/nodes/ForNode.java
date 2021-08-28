package nodes;

import run.CerealFile;
//import runtime.Interpreter;
import runtime.RuntimeResult;
import tokens.Token;
import tokens.Token.TokenType;

public class ForNode extends Node {

    public boolean isSimplified;
    public Node repetitions;
    public Node initialNode;
    public Node endCondition;
    public Node incrementNode;
    public Node bodyNode;

    public ForNode(Node reps, Node bodyNode) {
        this.isSimplified = true;
        this.repetitions = reps;
        this.incrementNode = new NumberNode(new Token(TokenType.INT, 1, null, null));

        this.bodyNode = bodyNode;
        this.startPosition = reps.startPosition;
        this.endPosition = bodyNode.endPosition;

    }

    public ForNode(Node initial, Node endCond, Node increment, Node bodyValue) {
        initialNode = initial;
        endCondition = endCond;
        incrementNode = increment;
        bodyNode = bodyValue;
        this.startPosition = initial.startPosition;
        this.endPosition = bodyValue.endPosition;

    }

    //TODO Implement executor for each round in loop (if possible)
    public RuntimeResult execute(CerealFile cFile) {
       // Interpreter interpreter = new Interpreter(cFile);
        return null;
    }

    public String toString() {
        return null;
    }

}
