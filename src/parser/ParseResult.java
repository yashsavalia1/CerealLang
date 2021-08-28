package parser;

import errors.Error;
import nodes.Node;

public class ParseResult {

    public boolean isError;
    public Node node;
    public Error error;
    public int advanceCount;

    public ParseResult() {
        isError = false;
        advanceCount = 0;
    }

    public void registerAdvance() {
        advanceCount++;
        return;
    }

    public Node register(ParseResult result) {
        this.advanceCount += result.advanceCount;
        if (result.isError) {
            this.error = result.error;
            this.isError = true;
        }
        return result.node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public void setError(Error error) {
        if (this.error == null || advanceCount == 0) {
            isError = true;
            this.error = error;
        }

    }

    public String toString() {
        if (!isError) {
            return node.toString();
        }
        return error.toString();
    }

}
