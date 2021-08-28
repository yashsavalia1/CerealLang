package parser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import nodes.BinaryOperationNode;
import nodes.Node;
import nodes.NumberNode;
import tokens.Token;
import tokens.Token.TokenType;

@Deprecated
public class JParser {

    private List<Token> tokens;
    private int currentTokenIndex;
    private Token currentToken;

    public JParser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentTokenIndex = -1;
        this.advance();
    }

    private Token advance() {
        this.currentTokenIndex += 1;
        if (this.currentTokenIndex < this.tokens.size()) {
            this.currentToken = this.tokens.get(this.currentTokenIndex);
        }
        return currentToken;
    }

    public NumberNode factor() {
        Token token = this.currentToken;
        if (token.type == TokenType.INT || token.type == TokenType.FLOAT) {
            this.advance();
            return new NumberNode(token);
        }
        System.out.println("Factor as null");
        return null;
    }

    public Node term() {
        try {
            Method method = this.getClass().getMethod("factor");
            return binOp(method, TokenType.MUL, TokenType.DIV);
        } catch (Exception e) {
            return null;
        }
    }

    public Node expr() {
        try {
            Method method = this.getClass().getMethod("term");
            return binOp(method, TokenType.ADD, TokenType.SUB);
        } catch (Exception e) {
            return null;
        }
    }

    public Node binOp(Method method, TokenType opr1, TokenType opr2) {
        try {
            Node left = (Node) method.invoke(this);
            if (currentToken.type == opr1 || currentToken.type == opr2) {
                Token token = this.currentToken;
                this.advance();
                Node right = (Node) method.invoke(this);
                left = new BinaryOperationNode(left, token, right);
            }
            return left;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Node parse(){
        return this.expr();
    }

}
