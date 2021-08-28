package run;

import java.util.List;

import lexer.Lexer;
import parser.CParser;
import parser.ParseResult;
import runtime.Interpreter;
import runtime.RuntimeResult;
import symbols.SymbolTable;
import tokens.Token;
import traceback.Context;

public class Runner {

    public static SymbolTable globalSymbolTable = new SymbolTable();

    public static void main(String[] args) {
        if (args.length == 0) {
            Shell.shell();
        } else {
            FileGetter.getFile(args[0]);
        }
    }

    public static String run(CerealFile cFile) {

        // Generate Tokens
        Lexer lexer = new Lexer(cFile);
        List<Token> tokens = lexer.makeTokens();
        if (lexer.isError)
            return lexer.error.toString();

        // return tokens.toString();

        // Generate Tree
        CParser parser = new CParser(tokens, cFile);
        ParseResult parseResult = parser.parse();
        if (parseResult.isError)
            return parseResult.error.toString();

        //return parseResult.node.toString();

         // Intepret Tree
        Interpreter interpreter = new Interpreter(cFile);

        Context context = new Context("<program>");
        context.symbolTable = globalSymbolTable;

        RuntimeResult runtimeResult = interpreter.visit(parseResult.node, context);
        if (runtimeResult.isError)
            return runtimeResult.error.toString();

        return runtimeResult.value.toString(); 


    }
}