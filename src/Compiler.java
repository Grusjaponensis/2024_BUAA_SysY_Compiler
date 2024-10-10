import exception.CompileError;
import exception.ErrorCollector;
import frontend.Lexer;
import frontend.Parser;
import frontend.ast.CompUnit;
import frontend.token.Token;
import frontend.token.TokenList;
import util.Debug;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;

public class Compiler {
    public static void main(String[] args) throws IOException {
        // open source file
        Path path = Paths.get("testfile.txt");
        String content = Files.readString(path);

        Debug.log(Debug.TERM_RED + ">>>>>>>> Original content: >>>>>>>>\n" + Debug.TERM_RESET + content + "\n");
        Debug.log(Debug.TERM_RED + ">>>>>>>> Lexer Output: >>>>>>>>" + Debug.TERM_RESET);

        // generate tokens
        Lexer lexer = new Lexer(content);

        ArrayList<Token> tokens = new ArrayList<>();
        StringBuilder lexerOutput = new StringBuilder();
        try {
            Token token = lexer.peek();
            while (token != null) {
                lexerOutput.append(token).append("\n");
                tokens.add(token);
                lexer.nextToken();
                token = lexer.peek();
            }
        } catch (CompileError e) {
            ErrorCollector.getInstance().addError(e);
        }

        System.out.println(lexerOutput);

        // generate AST
        Parser parser = new Parser(new TokenList(tokens));
        CompUnit compUnit = parser.parse();

        Debug.log(Debug.TERM_RED + ">>>>>>>> Parser Output: >>>>>>>>" + Debug.TERM_RESET);

        System.out.println(compUnit);

        Path parserOutputFile = Path.of("parser.txt");
        Files.writeString(parserOutputFile, compUnit.toString(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        if (ErrorCollector.getInstance().hasErrors()) {
            Path errorFile = Path.of("error.txt");
            Files.writeString(
                    errorFile,
                    ErrorCollector.getInstance().toString(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE
            );
        }

        System.out.println(Debug.TERM_RED + "errors: \n" + Debug.TERM_RESET + ErrorCollector.getInstance());

        Debug.log("\n" + Debug.TERM_RED + ">>>>>>>> Program exit... >>>>>>>>" + Debug.TERM_RESET);
    }
}
