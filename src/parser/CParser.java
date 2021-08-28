package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import errors.*;
import nodes.*;
import position.Position;
import run.CerealFile;
import tokens.*;
import tokens.Token.TokenType;

import static tokens.Token.TokenType.*;

public class CParser {
    private List<Token> tokens;
    private int tokenIndex;
    private Token currentToken;
    private CerealFile cFile;

    private enum MethodName {
        EXPR, TERM, FACT, ATOM, CALL, COMP_EXPR, ARITH_EXPR, LISTGET,
    }

    public CParser(List<Token> tokens, CerealFile cFile) {
        this.cFile = cFile;
        this.tokens = tokens;
        this.tokenIndex = -1;
        advance();
    }

    public ParseResult parse() {

        ParseResult result = new ParseResult();

        if (currentToken.type == EOF) {
            result.setNode(new Node() {
                @Override
                public String toString() {
                    return "EMPTY_NODE";
                }
            });
            return result;
        }

        result = expression();

        /*
         * if (!result.isError && currentToken.type == NEWLINE) {
         *
         * }
         */

        if (!result.isError && currentToken.type != EOF) {
            result.setError(new InvalidSyntaxError("Expected operation token", currentToken.startPosition,
                    currentToken.endPosition, cFile));
            return result;
        }
        return result;
    }

    private ParseResult expression() {

        if (currentToken.matches(KEYWORD, "var")) {
            return variable();
        }
        ParseResult result = new ParseResult();

        ParseResult exprResult = binaryOperator(MethodName.COMP_EXPR, null, AND, OR);
        result.register(exprResult);

        if (result.isError) {
            result.setError(new InvalidSyntaxError(
                    "Expected 'var', 'if', 'for', 'while', 'function', value, " + "identifier, '+', '-', or '('",
                    currentToken.startPosition, currentToken.endPosition, cFile));
            return result;
        }
        Node expr = exprResult.node;
        while (currentToken.type == LSQUARE || currentToken.type == LPAREN) {
            if (currentToken.type == LSQUARE) {
                ParseResult listGet = listGet(expr);
                result.register(listGet);
                if (result.isError)
                    return result;
                else
                    expr = listGet.node;

                TokenType[] equalTypes = { EQUALS, ADD_EQ, SUB_EQ, MUL_EQ, DIV_EQ, INTDIV_EQ, POW_EQ, MOD_EQ, AND_EQ,
                        OR_EQ };
                if (Arrays.asList(equalTypes).contains(currentToken.type)) {
                    ParseResult listSet = listSet((ListGetNode) expr);
                    result.register(listSet);
                    if (result.isError)
                        return result;
                    else
                        expr = listSet.node;
                }
            } else if (currentToken.type == LPAREN) {
                ParseResult call = call(expr);
                result.register(call);
                if (result.isError)
                    return result;
                else
                    expr = call.node;
            }
        }
        result.setNode(expr);
        return result;
    }

    private ParseResult compareExpression() {
        ParseResult result = new ParseResult();

        if (currentToken.matches(NOT)) {
            Token opToken = currentToken;
            result.registerAdvance();
            advance();

            ParseResult comapareResult = compareExpression();
            result.register(comapareResult);
            if (result.isError)
                return result;

            result.setNode(new UnaryOperationNode(opToken, comapareResult.node));
            return result;
        }

        ParseResult arithResult = binaryOperator(MethodName.ARITH_EXPR, null, EQUALS_EQ, NOT_EQ, LT, GT, LT_EQ, GT_EQ);

        result.register(arithResult);
        if (result.isError) {
            result.setError(new InvalidSyntaxError("Expected value, identifier, '+', '-', '(', or '!'",
                    currentToken.startPosition, currentToken.endPosition, cFile));
            return result;
        }

        result.setNode(arithResult.node);

        return result;

    }

    private ParseResult arithmeticExpression() {
        return binaryOperator(MethodName.TERM, null, ADD, SUB);
    }

    private ParseResult term() {
        return binaryOperator(MethodName.FACT, null, MUL, DIV, INTDIV, MOD);
    }

    private ParseResult factor() {

        ParseResult result = new ParseResult();
        Token token = this.currentToken;

        // Check if factor is unary node
        if (token.type == ADD || token.type == SUB) {
            result.registerAdvance();
            advance();
            ParseResult unaryResult = this.factor();
            if (unaryResult.isError) {
                return unaryResult;
            }
            result.setNode(new UnaryOperationNode(token, unaryResult.node));
            return result;
        }

        return power();
    }

    private ParseResult power() {
        return binaryOperator(MethodName.ATOM, MethodName.FACT, POW);
    }

    private ParseResult call(Node varNode) {
        ParseResult result = new ParseResult();

        if (currentToken.type != LPAREN) {
            result.setError(new InvalidSyntaxError("Expected '('", currentToken.startPosition, currentToken.endPosition,
                    cFile));
            return result;
        }

        if (!(varNode instanceof VariableAccessNode || varNode instanceof ListGetNode || varNode instanceof CallNode || varNode instanceof ListNode)) {
            result.setError(
                    new InvalidSyntaxError("Expected variable", varNode.startPosition, varNode.endPosition, cFile));
            return result;
        }

        result.registerAdvance();
        advance();

        List<Node> argumentNodes = new ArrayList<Node>();
        if (currentToken.type != RPAREN) {
            argumentNodes.add(result.register(expression()));
            if (result.isError) {
                result.setError(new InvalidSyntaxError(
                        "Expected ')', 'var', 'if', 'for', 'while', 'function', number, identifier, '+', '-', '('"
                                + " or '!'",
                        currentToken.startPosition, currentToken.endPosition, cFile));
                return result;
            }

            while (currentToken.type == COMMA) {
                result.registerAdvance();
                advance();

                argumentNodes.add(result.register(expression()));
                if (result.isError)
                    return result;
            }

            if (currentToken.type != RPAREN) {
                result.setError(new InvalidSyntaxError("Expected ',' or ')'", currentToken.startPosition,
                        currentToken.endPosition, cFile));
                return result;
            }
        }

        result.registerAdvance();
        advance();

        result.setNode(new CallNode(varNode, argumentNodes));
        return result;
    }

    private ParseResult listSet(ListGetNode listGetNode) {
        ParseResult result = new ParseResult();

        TokenType[] equalTypes = { EQUALS, ADD_EQ, SUB_EQ, MUL_EQ, DIV_EQ, INTDIV_EQ, POW_EQ, MOD_EQ, AND_EQ, OR_EQ };

        if (!Arrays.asList(equalTypes).contains(currentToken.type)) {
            result.setError(new InvalidSyntaxError("Expected '='", currentToken.startPosition, currentToken.endPosition,
                    cFile));
            return result;
        }

        Token opToken = currentToken;

        result.registerAdvance();
        advance();

        Node toSet = result.register(expression());
        if (result.isError)
            return result;

        result.setNode(new ListReAssignNode(listGetNode.name, listGetNode.index, opToken, toSet));
        return result;
    }

    private ParseResult listGet(Node varNode) {
        ParseResult result = new ParseResult();

        if (!(varNode instanceof VariableAccessNode || varNode instanceof ListGetNode || varNode instanceof CallNode || varNode instanceof ListNode)) {
            result.setError(
                    new InvalidSyntaxError("Expected variable", varNode.startPosition, varNode.endPosition, cFile));
            return result;
        }

        if (currentToken.type != LSQUARE) {
            result.setError(new InvalidSyntaxError("Expected '['", currentToken.startPosition, currentToken.endPosition,
                    cFile));
            return result;
        }
        result.registerAdvance();
        advance();

        Node index = result.register(expression());

        if (result.isError)
            return result;

        if (currentToken.type != RSQUARE) {
            result.setError(new InvalidSyntaxError("Expected ']'", currentToken.startPosition, currentToken.endPosition,
                    cFile));
            return result;
        }

        result.registerAdvance();
        advance();

        Node listGet = new ListGetNode(varNode, index);

        result.setNode(listGet);
        return result;

    }

    private ParseResult atom() {
        ParseResult result = new ParseResult();
        Token token = this.currentToken;

        // Check if factor is single number
        if (token.type == INT || token.type == FLOAT) {
            result.registerAdvance();
            advance();
            result.setNode(new NumberNode(token));
            return result;
        } else if (token.type == STRING) {
            result.registerAdvance();
            advance();
            result.setNode(new StringNode(token));
            return result;
        } else if (token.matches(KEYWORD, "true") || token.matches(KEYWORD, "false")) {
            result.registerAdvance();
            advance();
            result.setNode(new BooleanNode(token));
            return result;

        } else if (token.isNull) {
            result.registerAdvance();
            advance();
            result.setNode(new NullNode(token));
            return result;

        } else if (token.type == IDENTIFIER) {
            result.registerAdvance();
            advance();

            TokenType[] equalTypes = { EQUALS, ADD_EQ, SUB_EQ, MUL_EQ, DIV_EQ, INTDIV_EQ, POW_EQ, MOD_EQ, AND_EQ,
                    OR_EQ };

            if (Arrays.asList(equalTypes).contains(currentToken.type)) {
                ParseResult varResult = reAssignVariable(currentToken, token);
                result.register(varResult);

                if (result.isError) {
                    return result;
                }

                result.setNode(varResult.node);
                return result;
            }

            result.setNode(new VariableAccessNode(token));
            return result;

            // Checks if token is Parentheses
        } else if (token.type == LPAREN) {
            result.registerAdvance();
            advance();

            // Runs an expression for the length of the expression
            result = expression();
            if (currentToken.type == RPAREN) {
                result.registerAdvance();
                advance();
                return result;
            } else {
                result.setError(new InvalidSyntaxError("Expected ')'", currentToken.startPosition,
                        currentToken.endPosition, cFile));
                return result;
            }
        } else if (token.type == LSQUARE) {
            ParseResult listResult = listExpression();
            result.register(listResult);

            if (result.isError)
                return result;

            result.setNode(listResult.node);
            return result;

        } else if (token.matches(KEYWORD, "if")) {
            ParseResult ifResult = ifExpression();
            result.register(ifResult);

            if (result.isError)
                return result;

            result.setNode(ifResult.node);
            return result;
        } else if (token.matches(KEYWORD, "for")) {
            ParseResult forResult = forExpression();
            result.register(forResult);

            if (result.isError)
                return result;

            result.setNode(forResult.node);
            return result;

        } else if (token.matches(KEYWORD, "while")) {
            ParseResult whileResult = whileExpression();
            result.register(whileResult);

            if (result.isError)
                return result;

            result.setNode(whileResult.node);
            return result;
        } else if (token.matches(KEYWORD, "function")) {
            ParseResult funcResult = funcExpression();
            result.register(funcResult);

            if (result.isError)
                return result;

            result.setNode(funcResult.node);
            return result;
        } else
            result.setError(new InvalidSyntaxError(
                    "Expected 'if', 'for', 'while', 'function', value, identifier, '+', " + "'-', or '('",
                    token.startPosition, token.endPosition, cFile));

        return result;

    }

    private ParseResult reAssignVariable(Token operationToken, Token varIdentifierToken) {
        ParseResult result = new ParseResult();

        result.registerAdvance();
        advance();

        ParseResult exprResult = expression();

        result.register(exprResult);
        if (result.isError) {
            return result;
        }

        Node expression = exprResult.node;

        result.setNode(new VariableReAssignNode(varIdentifierToken, operationToken, expression));
        return result;

    }

    private ParseResult variable() {
        ParseResult result = new ParseResult();
        result.registerAdvance();
        advance();
        if (currentToken.type != IDENTIFIER) {
            result.setError(new InvalidSyntaxError("Expected Identifier", currentToken.startPosition,
                    currentToken.endPosition, cFile));
            return result;
        }

        Token varIdentifierToken = currentToken;
        result.registerAdvance();
        advance();

        if (currentToken.type != EQUALS) {
            result.setError(new InvalidSyntaxError("Expected '='", currentToken.startPosition, currentToken.endPosition,
                    cFile));
            return result;
        }

        result.registerAdvance();
        advance();

        ParseResult exprResult = expression();

        result.register(exprResult);
        if (result.isError) {
            return result;
        }

        Node expression = exprResult.node;

        result.setNode(new VariableAssignNode(varIdentifierToken, expression));
        return result;
    }

    // FUNCTION EXPRESSION
    private ParseResult funcExpression() {
        ParseResult result = new ParseResult();
        if (!currentToken.matches(KEYWORD, "function")) {
            result.setError(new InvalidSyntaxError("Expected 'function'", currentToken.startPosition,
                    currentToken.endPosition, cFile));
            return result;
        }

        result.registerAdvance();
        advance();

        Token variableNameToken = null;

        // if function is named
        if (currentToken.type == IDENTIFIER) {
            variableNameToken = currentToken;

            result.registerAdvance();
            advance();

            if (currentToken.type != LPAREN) {
                result.setError(new InvalidSyntaxError("Expected '('", currentToken.startPosition,
                        currentToken.endPosition, cFile));
                return result;
            }

            // if function is anonymous
        } else {

            if (currentToken.type != LPAREN) {
                result.setError(new InvalidSyntaxError("Expected identifier or '('", currentToken.startPosition,
                        currentToken.endPosition, cFile));
                return result;
            }

        }

        result.registerAdvance();
        advance();

        List<Token> argumentTokens = new ArrayList<Token>();

        // Checks first ID
        if (currentToken.type == IDENTIFIER) {
            argumentTokens.add(currentToken);

            result.registerAdvance();
            advance();

            // Checks next IDs
            while (currentToken.type == COMMA) {
                result.registerAdvance();
                advance();

                if (currentToken.type != IDENTIFIER) {
                    result.setError(new InvalidSyntaxError("Expected identifier", currentToken.startPosition,
                            currentToken.endPosition, cFile));
                    return result;
                }

                argumentTokens.add(currentToken);
                result.registerAdvance();
                advance();

            }

        }

        if (currentToken.type != RPAREN) {
            result.setError(new InvalidSyntaxError("Expected ',' or ')'", currentToken.startPosition,
                    currentToken.endPosition, cFile));
            return result;
        }

        result.registerAdvance();
        advance();

        if (currentToken.type != ARROW) {
            result.setError(new InvalidSyntaxError("Expected '->'", currentToken.startPosition,
                    currentToken.endPosition, cFile));
            return result;
        }

        result.registerAdvance();
        advance();

        Node nodeToReturn = result.register(expression());
        if (result.isError)
            return result;

        // Set result node
        if (variableNameToken == null) {
            result.setNode(new FunctionDefinitionNode(argumentTokens, nodeToReturn));
        } else {
            result.setNode(new FunctionDefinitionNode(variableNameToken, argumentTokens, nodeToReturn));
        }

        return result;
    }

    // LIST EXPRESSION
    private ParseResult listExpression() {
        ParseResult result = new ParseResult();

        List<Node> elementNodes = new ArrayList<Node>();
        Position startPosition = this.currentToken.startPosition.copy();

        if (currentToken.type != LSQUARE) {
            result.setError(new InvalidSyntaxError("Expected '['", currentToken.startPosition, currentToken.endPosition,
                    cFile));
            return result;
        }

        result.registerAdvance();
        advance();

        if (this.currentToken.type == RSQUARE) {
            result.registerAdvance();
            advance();
        } else {
            // First element
            elementNodes.add(result.register(expression()));

            if (result.isError) {
                result.setError(new InvalidSyntaxError(
                        "Expected ']', 'var', 'if', 'for', 'while', 'function', number, identifier, '+', '-', '(', '[' or '!'",
                        this.currentToken.startPosition, this.currentToken.endPosition, cFile));
                return result;
            }

            // Next Elements
            while (currentToken.type == COMMA) {
                result.registerAdvance();
                advance();

                elementNodes.add(result.register(expression()));

                if (result.isError)
                    return result;
            }

            if (currentToken.type != RSQUARE) {
                result.setError(new InvalidSyntaxError("Expected ',' or ']'", currentToken.startPosition,
                        currentToken.endPosition, cFile));
                return result;
            }

            result.registerAdvance();
            advance();
        }

        result.setNode(new ListNode(elementNodes, startPosition, this.currentToken.endPosition.copy()));
        return result;
    }

    // WHILE EXPRESSION
    private ParseResult whileExpression() {
        ParseResult result = new ParseResult();
        if (!currentToken.matches(KEYWORD, "while")) {
            result.setError(new InvalidSyntaxError("Expected 'while'", currentToken.startPosition,
                    currentToken.endPosition, cFile));
            return result;
        }

        result.registerAdvance();
        advance();

        Node condition = result.register(expression());
        if (result.isError)
            return result;

        if (!currentToken.matches(COLON)) {
            result.setError(new InvalidSyntaxError("Expected ':'", currentToken.startPosition, currentToken.endPosition,
                    cFile));
            return result;
        }

        result.registerAdvance();
        advance();

        Node expression = result.register(expression());
        if (result.isError)
            return result;

        result.setNode(new WhileNode(condition, expression));
        return result;
    }

    // FOR EXPRESSION
    private ParseResult forExpression() {
        ParseResult result = new ParseResult();
        if (!currentToken.matches(KEYWORD, "for")) {
            result.setError(new InvalidSyntaxError("Expected 'for'", currentToken.startPosition,
                    currentToken.endPosition, cFile));
            return result;
        }

        result.registerAdvance();
        advance();

        boolean startingParen = false;
        if (currentToken.type == LPAREN) {
            startingParen = true;
            result.registerAdvance();
            advance();
        }

        // Simplified for loop
        if (currentToken.matches(KEYWORD, "times")) {
            result.registerAdvance();
            advance();

            Node reps = result.register(expression());
            if (result.isError)
                return result;

            if (currentToken.type == RPAREN) {
                if (!startingParen) {
                    result.setError(new InvalidSyntaxError("Expected starting '('", currentToken.startPosition,
                            currentToken.endPosition, cFile));
                    return result;
                }
                result.registerAdvance();
                advance();
            } else if (startingParen) {
                result.setError(new InvalidSyntaxError("Expected ')'", currentToken.startPosition,
                        currentToken.endPosition, cFile));
                return result;
            }

            if (!currentToken.matches(COLON)) {
                result.setError(new InvalidSyntaxError("Expected ':'", currentToken.startPosition,
                        currentToken.endPosition, cFile));
                return result;
            }

            result.registerAdvance();
            advance();

            Node expression = result.register(expression());
            if (result.isError)
                return result;

            result.setNode(new ForNode(reps, expression));
            return result;
        }

        Node initial = result.register(expression());
        if (result.isError)
            return result;

        if (currentToken.type != SEMICOLON) {
            result.setError(new InvalidSyntaxError("Expected ';'", currentToken.startPosition, currentToken.endPosition,
                    cFile));
            return result;
        }

        result.registerAdvance();
        advance();

        Node condition = result.register(expression());
        if (result.isError)
            return result;

        if (currentToken.type != SEMICOLON) {
            result.setError(new InvalidSyntaxError("Expected ';'", currentToken.startPosition, currentToken.endPosition,
                    cFile));
            return result;
        }

        result.registerAdvance();
        advance();

        Node increment = result.register(expression());
        if (result.isError)
            return result;

        if (currentToken.type == RPAREN) {
            if (!startingParen) {
                result.setError(new InvalidSyntaxError("Expected starting '('", currentToken.startPosition,
                        currentToken.endPosition, cFile));
                return result;
            }

            result.registerAdvance();
            advance();
        } else if (startingParen) {
            result.setError(new InvalidSyntaxError("Expected ')'", currentToken.startPosition, currentToken.endPosition,
                    cFile));
            return result;
        }

        if (!currentToken.matches(COLON)) {
            result.setError(new InvalidSyntaxError("Expected ':'", currentToken.startPosition, currentToken.endPosition,
                    cFile));
            return result;
        }

        result.registerAdvance();
        advance();

        Node expression = result.register(expression());
        if (result.isError)
            return result;

        result.setNode(new ForNode(initial, condition, increment, expression));
        return result;

    }

    // IF EXPRESSION
    private ParseResult ifExpression() {
        ParseResult result = new ParseResult();
        List<Node[]> cases = new ArrayList<Node[]>();
        Node elseCase = null;

        // Search for if
        if (!currentToken.matches(KEYWORD, "if")) {
            result.setError(new InvalidSyntaxError("Expected 'if'", currentToken.startPosition,
                    currentToken.endPosition, cFile));
            return result;
        }

        result.registerAdvance();
        advance();

        Node condition = result.register(expression());

        if (result.isError)
            return result;

        if (!currentToken.matches(COLON)) {
            result.setError(new InvalidSyntaxError("Expected ':'", currentToken.startPosition, currentToken.endPosition,
                    cFile));
            return result;
        }

        result.registerAdvance();
        advance();

        Node expression = result.register(expression());
        if (result.isError)
            return result;

        Node[] firstIf = { condition, expression };
        cases.add(firstIf);

        // Search for elseif
        while (currentToken.matches(KEYWORD, "elseif")) {
            result.registerAdvance();
            advance();

            Node newCondition = result.register(expression());

            if (result.isError)
                return result;

            if (!currentToken.matches(COLON)) {
                result.setError(new InvalidSyntaxError("Expected ':'", currentToken.startPosition,
                        currentToken.endPosition, cFile));
                return result;
            }

            result.registerAdvance();
            advance();

            Node newExpression = result.register(expression());

            if (result.isError)
                return result;

            Node[] elseIfs = { newCondition, newExpression };
            cases.add(elseIfs);
        }

        if (currentToken.matches(KEYWORD, "else")) {
            result.registerAdvance();
            advance();
            if (currentToken.matches(COLON)) {
                result.registerAdvance();
                advance();
            }
            elseCase = result.register(expression());

            if (result.isError)
                return result;
        }

        result.setNode(new IfNode(cases, elseCase));

        return result;
    }

    private ParseResult binaryOperator(MethodName func1, MethodName func2, TokenType... operation) {
        ParseResult result = new ParseResult();
        Node leftNode = null;
        ParseResult nodeResult;

        switch (func1) {
            case ATOM:
                nodeResult = atom();
                break;
            case TERM:
                nodeResult = term();
                break;
            case ARITH_EXPR:
                nodeResult = arithmeticExpression();
                break;
            case COMP_EXPR:
                nodeResult = compareExpression();
                break;
            case EXPR:
                nodeResult = expression();
                break;
            case FACT:
                nodeResult = factor();
                break;
            default:
                nodeResult = null;
                break;
        }

        result.register(nodeResult);
        leftNode = nodeResult.node;

        if (result.isError)
            return result;

        List<TokenType> list = Arrays.asList(operation);

        while (list.contains(currentToken.type)) {
            Token operationToken = this.currentToken;
            result.registerAdvance();
            advance();
            Node rightNode = null;
            if (func2 == null)
                func2 = func1;

            switch (func2) {
                case ATOM:
                    nodeResult = atom();
                    break;
                case TERM:
                    nodeResult = term();
                    break;
                case ARITH_EXPR:
                    nodeResult = arithmeticExpression();
                    break;
                case COMP_EXPR:
                    nodeResult = compareExpression();
                    break;
                case EXPR:
                    nodeResult = expression();
                    break;
                case FACT:
                    nodeResult = factor();
                    break;
                default:
                    nodeResult = null;
                    break;
            }

            result.register(nodeResult);
            rightNode = nodeResult.node;

            if (result.isError)
                return result;

            leftNode = new BinaryOperationNode(leftNode, operationToken, rightNode);
        }

        result.setNode(leftNode);
        return result;
    }

    public Token advance() {
        this.tokenIndex += 1;
        if (this.tokenIndex < this.tokens.size()) {
            this.currentToken = this.tokens.get(this.tokenIndex);
        }
        return currentToken;
    }
}