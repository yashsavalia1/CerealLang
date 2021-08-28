package parser;

import java.util.List;

import errors.InvalidSyntaxError;
import errors.Error;
import nodes.*;
import run.*;
import tokens.*;
import tokens.Token.TokenType;

import static tokens.Token.TokenType.*;

@Deprecated
public class DParser {
    private List<Token> tokens;
    private int tokenIndex;
    private Token currentToken;
    private CerealFile cFile;

    public DParser(List<Token> tokens, CerealFile cFile) {
        this.cFile = cFile;
        this.tokens = tokens;
        this.tokenIndex = -1;
        this.advance();
    }

    public Token advance() {
        this.tokenIndex += 1;
        if (this.tokenIndex < this.tokens.size()) {
            this.currentToken = this.tokens.get(this.tokenIndex);
        }
        return currentToken;
    }

    public Node parse() {
        Node expression = expression();
        if (currentToken.type !=EOF) {
            
			Error error = new InvalidSyntaxError("Expected '+', '-', '*' or '/'", 
            currentToken.startPosition, currentToken.endPosition, cFile);
            System.out.println(error);
        }
        return expression;
    }

    public Node factor() {
        Token token = this.currentToken;

        if (token.type == ADD || token.type == SUB) {
            advance();
            return new UnaryOperationNode(token, this.factor());
        } else if (token.type == INT || token.type == FLOAT) {
            advance();
            return new NumberNode(token);
        } else if (token.type == LPAREN) {
            advance();
            Node express = expression();
            if (currentToken.type == RPAREN) {
                advance();
                return express;
            } else {

            }
        }

        return null;
    }

    public Node term() {
        Node leftNode = factor();

        while (currentToken.type == MUL || currentToken.type == DIV) {
            Token operationToken = this.currentToken;
            advance();
            Node rightNumberNode = factor();

            leftNode = new BinaryOperationNode(leftNode, operationToken, rightNumberNode);
        }

        return leftNode;
    }

    public Node expression() {
        Node leftNode = term();

        while (currentToken.type == ADD || currentToken.type == SUB) {
            Token operationToken = this.currentToken;
            advance();
            Node rightNumberNode = term();

            leftNode = new BinaryOperationNode(leftNode, operationToken, rightNumberNode);
        }

        return leftNode;
    }

    public Node binaryOperator(boolean isTerm, TokenType operation1, TokenType operation2) {
        Node leftNode = isTerm ? factor() : term();

        while (currentToken.type == operation1 || currentToken.type == operation2) {
            Token operationToken = this.currentToken;
            advance();
            Node rightNumberNode = isTerm ? factor() : term();

            leftNode = new BinaryOperationNode(leftNode, operationToken, rightNumberNode);
        }

        return leftNode;
    }

}
