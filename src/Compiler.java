import exception.CompileError;
import exception.ErrorCollector;
import frontend.Lexer;
import frontend.token.Token;
import util.Debug;

import java.io.*;
import java.nio.file.*;

public class Compiler {
    public static void main(String[] args) throws IOException {
        // open source file
        Path path = Paths.get("testfile.txt");
        String content = Files.readString(path);

        Debug.log(Debug.TERM_RED + ">>>>>>>> Original content: >>>>>>>>\n" + Debug.TERM_RESET + content + "\n");
        Debug.log(Debug.TERM_RED + ">>>>>>>> Lexer Output: >>>>>>>>" + Debug.TERM_RESET);

        // generate tokens
        Lexer lexer = new Lexer(content);

        if (Debug.STAGE_LEXER) {
            StringBuilder lexerOutput = new StringBuilder();
            try {
                Token token = lexer.peek();
                while (token != null) {
                    lexerOutput.append(token).append("\n");
                    lexer.nextToken();
                    token = lexer.peek();
                }
            } catch (CompileError e) {
                ErrorCollector.getInstance().addError(e);
            }
            System.out.println(lexerOutput + "\nerrors: \n" + ErrorCollector.getInstance());
            if (!Debug.DEBUG_STATE) {
                Debug.log("\n" + Debug.TERM_RED + ">>>>>>>> Write lexer result to file >>>>>>>>" + Debug.TERM_RESET);

                Path lexerOutputFile = Path.of("lexer.txt");
                Files.writeString(lexerOutputFile, lexerOutput, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                if (ErrorCollector.getInstance().hasErrors()) {
                    Path errorFile = Path.of("error.txt");
                    Files.writeString(errorFile, ErrorCollector.getInstance().toString(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                }
            }
        }

        Debug.log("\n" + Debug.TERM_RED + ">>>>>>>> Program exit... >>>>>>>>" + Debug.TERM_RESET);
    }
}
