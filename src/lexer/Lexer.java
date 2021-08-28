package lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import errors.*;
import position.Position;
import run.CerealFile;
import tokens.Token;
import tokens.Token.TokenType;

import static tokens.Token.TokenType.*;

public class Lexer {

    private Position position;
    private Character currentChar;
    public boolean isError;
    public errors.Error error;
    private CerealFile cFile;

    public Lexer(CerealFile cFile) {
        this.cFile = cFile;
        isError = false;
        this.position = new Position(-1, 0, -1, cFile);
        this.advance();
    }

    public List<Token> makeTokens() {
        List<Token> tokens = new ArrayList<Token>();

        while (this.currentChar != null) {
            if (" \t".contains(currentChar.toString())) {
                this.advance();
            } else if (currentChar == '#') {
                this.skipComment();

                //MAKE NUMBERS
            } else if ((Token.DIGITS + ".").contains(this.currentChar.toString())) {
                tokens.add(makeNumber());

                //MAKE IDENTIFIERS
            } else if (Token.LETTERS.contains(this.currentChar.toString())) {
                tokens.add(makeIdentifier());

                //MAKE STRINGS
            } else if (this.currentChar == '"') {
                tokens.add(makeString());

            } else if (get(this.currentChar) != null) {
                TokenType type = get(this.currentChar);

                assert type != null;
                if (type.canMakeEQOrTok) {
                    tokens.add(makeEqualsOrToken(type));
                } else {
                    tokens.add(new Token(type, position, null));
                    advance();
                }

            } else {
                char errorChar = this.currentChar;
                Position start = this.position.copy();
                this.advance();
                this.isError = true;
                this.error = new IllegalCharError("'" + errorChar + "'", start, this.position, this.cFile);
                return null;
            }
        }

        tokens.add(new Token(EOF, position, null));
        return tokens;
    }

    private Token makeEqualsOrToken(TokenType type) {
        Position startPosition = this.position.copy();
        advance();
        if (currentChar != null && currentChar == '=') {
            advance();
            return new Token(TokenType.valueOf(type + "_EQ"), startPosition, this.position);
        }
        if (currentChar != null && currentChar == '/' && type == DIV) {
            return makeEqualsOrToken(INTDIV);
        }

        if (currentChar != null && this.currentChar == '>' && type == SUB) {
            advance();
            return new Token(ARROW, startPosition, this.position);
        }

        return new Token(type, startPosition, this.position);

    }

    private Token makeIdentifier() {
        String identString = "";
        Position startPosition = this.position.copy();

        while (this.currentChar != null && (Token.LETTERS_DIGITS + '_').contains(this.currentChar.toString())) {
            identString += this.currentChar;
            this.advance();
        }

        TokenType type = Token.KEYWORDS.contains(identString) ? KEYWORD : IDENTIFIER;

        return new Token(type, identString, startPosition, this.position);
    }

    private Token makeNumber() {
        String numString = "";
        int dotCount = 0;
        int eCount = 0;
        Position startPosition = this.position.copy();

        while (this.currentChar != null && (Token.DIGITS + "." + "e").contains(this.currentChar.toString())) {
            if (this.currentChar == '.') {
                dotCount++;
                numString += '.';
            } else if (this.currentChar == 'e') {
                eCount++;
                numString += 'e';
                advance();

                if (this.currentChar != null && this.currentChar == '-')
                    numString += '-';
                else if (this.currentChar != null && this.currentChar == '+')
                    numString += '+';
                else
                    continue;
            } else {
                numString += this.currentChar;
            }

            advance();
        }

        Number value = 0;

        if (eCount > 1 || dotCount > 1) {
            this.isError = true;
            this.error = new InvalidNumberError("'" + numString + "' is not a valid decimal value", startPosition,
                    this.position.copy(), this.cFile);
            return new Token(INT, value, startPosition, this.position);
        }

        if (dotCount == 0 && eCount == 0) {
            try {
                value = Integer.parseInt(numString);
            } catch (NumberFormatException e) {
                this.isError = true;
                this.error = new InvalidNumberError("'" + numString + "' is out of integer bounds", startPosition,
                        this.position.copy(), this.cFile);
            }

            return new Token(INT, value, startPosition, this.position);
        }

        try {
            value = Double.parseDouble(numString);
        } catch (NumberFormatException e) {
            this.isError = true;
            this.error = new InvalidNumberError("'" + numString + "' is not a valid decimal value", startPosition,
                    this.position.copy(), this.cFile);
        }
        return new Token(FLOAT, value, startPosition, this.position);
    }

    private Token makeString() {
        String str = "";
        Position start = this.position.copy();
        boolean isEscape = false;

        advance();

        HashMap<Character, Character> escapeChars = new HashMap<>();
        escapeChars.put('n', '\n');
        escapeChars.put('t', '\t');
        escapeChars.put('b', '\b');

        while (this.currentChar != null && (this.currentChar != '"' || isEscape)) {
            if (isEscape) {
                if (escapeChars.get(this.currentChar) != null) {
                    str += escapeChars.get(this.currentChar);
                } else {
                    str += this.currentChar;
                }
                isEscape = false;
            } else {
                if (this.currentChar == '\\') {
                    isEscape = true;
                } else {
                    str += this.currentChar;
                    isEscape = false;
                }
            }

            advance();
        }

        if(this.currentChar == null || currentChar != '\"') {
            Position startPos = this.position.copy();
            advance();
            Position endPos = this.position.copy();
            this.isError = true;
            this.error = new InvalidSyntaxError("Expected '\"'", startPos, endPos, cFile);
        }

        advance();
        return new Token(STRING, str, start, this.position);

    }

    private void skipComment() {
        advance();

        while (currentChar != null && currentChar != '\n') {
            advance();
        }
    }

    private void advance() {

        position.advance(currentChar);

        currentChar = position.index < cFile.fileText.length() ? cFile.fileText.charAt(position.index) : null;
    }

}