package tokens;

import java.util.Arrays;
import java.util.List;

import position.Position;

public class Token {

    public enum TokenType {
        INT, FLOAT, STRING, ADD('+', true), ADD_EQ, SUB('-', true), SUB_EQ, MUL('*', true), MUL_EQ, DIV('/', true),
        DIV_EQ, POW('^', true), POW_EQ, INTDIV(true), INTDIV_EQ, MOD('%', true), MOD_EQ, LPAREN('('), RPAREN(')'),
        LSQUARE('['), RSQUARE(']'), KEYWORD, IDENTIFIER, EQUALS('=', true), EQUALS_EQ, NOT('!', true), NOT_EQ,
        GT('>', true), LT('<', true), GT_EQ, LT_EQ, AND('&', true), AND_EQ, OR('|', true), OR_EQ, COLON(':'),
        SEMICOLON(';'), ARROW, COMMA(','), NEWLINE('\n'), EOF;

        public final Character character;
        public final boolean canMakeEQOrTok;

        TokenType(Character c) {
            character = c;
            canMakeEQOrTok = false;
        }

        TokenType(Character c, boolean canMakeEQ) {
            character = c;
            this.canMakeEQOrTok = canMakeEQ;
        }

        TokenType(boolean canMakeEQ) {
            character = null;
            this.canMakeEQOrTok = canMakeEQ;
        }

        TokenType() {
            character = null;
            canMakeEQOrTok = false;
        }

        public static TokenType get(Character c) {
            for (TokenType t : TokenType.values()) {
                if (t.character == c) {
                    return t;
                }
            }

            return null;
        }
    }

    public static final String DIGITS = "0123456789";
    public static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static final String LETTERS_DIGITS = LETTERS + DIGITS;

    private static final String[] KEYS_AS_ARRAY = { "var", "true", "false", "null", "if", "elseif", "else", "for",
            "while", "times", "function", "clear" };
    public static final List<String> KEYWORDS = Arrays.asList(KEYS_AS_ARRAY);

    public TokenType type;
    public Number value;
    public String keyString;
    public Boolean boolValue;
    public Position startPosition;
    public Position endPosition;
    public boolean isNull;

    public Token(TokenType type, Position startPos, Position endPos) {
        this.type = type;

        if (startPos != null) {
            startPosition = startPos.copy();
            endPosition = startPos.copy();
            endPosition.advance();
        }

        if (endPos != null) {
            endPosition = endPos.copy();
        }

    }

    public Token(TokenType type, Number value, Position startPosition, Position endPosition) {
        this.type = type;
        this.value = value;

        if (startPosition != null) {
            this.startPosition = startPosition.copy();
            this.endPosition = startPosition.copy();
            this.endPosition.advance();
        }

        if (endPosition != null) {
            this.endPosition = endPosition.copy();
        }

    }

    public Token(TokenType type, String keyString, Position startPosition, Position endPosition) {
        this.type = type;
        this.keyString = keyString;

        switch (keyString) {
            case "true":
                boolValue = true;
                break;
            case "false":
                boolValue = false;
                break;
            case "null":
                isNull = true;
                break;
        }

        if (startPosition != null) {
            this.startPosition = startPosition.copy();
            this.endPosition = startPosition.copy();
            this.endPosition.advance();
        }

        if (endPosition != null) {
            this.endPosition = endPosition.copy();
        }

    }

    public Token(TokenType type, boolean boolValue, Position startPosition, Position endPosition) {
        this.type = type;
        this.boolValue = boolValue;

        if (startPosition != null) {
            this.startPosition = startPosition.copy();
            this.endPosition = startPosition.copy();
            this.endPosition.advance();
        }

        if (endPosition != null) {
            this.endPosition = endPosition.copy();
        }

    }

    public boolean matches(TokenType type) {
        return this.type == type;
    }

    @Deprecated
    public boolean matches(TokenType type, Boolean boolValue) {
        if (this.boolValue != null && boolValue != null) {
            return this.type == type && this.boolValue == boolValue;
        }
        return false;
    }

    public boolean matches(TokenType type, Number value) {
        if (this.value != null && value != null) {
            return this.type == type && this.value == value;
        }
        return false;
    }

    public boolean matches(TokenType type, String keyString) {
        if (this.keyString != null && keyString != null) {
            return this.type == type && this.keyString.equalsIgnoreCase(keyString);
        }
        return false;
    }

    public String toString() {
        if (this.value != null) {
            return this.type + ":" + this.value.toString();
        }

        if (this.keyString != null) {
            if (this.keyString.equals("true") || this.keyString.equals("false")) {
                return "BOOL:" + this.keyString;
            }

            // For other types
            return type.toString() + ":" + this.keyString;
        }

        return this.type.toString();
    }
}
