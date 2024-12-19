import backend.MIPSBuilder;
import exception.CompileError;
import exception.ErrorCollector;
import frontend.Lexer;
import frontend.Parser;
import frontend.ast.CompUnit;
import frontend.token.Token;
import frontend.token.TokenList;
import ir.IRBuilder;
import util.Debug;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Compiler {
    public static void main(String[] args) throws IOException {
        // open source file
        Path path = Paths.get("testfile.txt");
        String content = Files.readString(path);

        Debug.log(Debug.TERM_RED + ">>>>>>>> Original content: >>>>>>>>\n" + Debug.TERM_RESET + content + "\n");

        // generate tokens
        Lexer lexer = new Lexer(content);

        ArrayList<Token> tokens = new ArrayList<>();
        try {
            Token token = lexer.peek();
            while (token != null) {
                tokens.add(token);
                lexer.nextToken();
                token = lexer.peek();
            }
        } catch (CompileError e) {
            ErrorCollector.getInstance().addError(e);
        }
        Debug.log(Debug.TERM_RED + ">>>>>>>> Lexer Output: >>>>>>>>" + Debug.TERM_RESET);
        tokens.forEach(System.out::println);

        // generate AST
        Parser parser = new Parser(new TokenList(tokens));
        CompUnit compUnit = parser.parse();

        Debug.log(Debug.TERM_RED + ">>>>>>>> Parser Output: >>>>>>>>" + Debug.TERM_RESET);

        System.out.println(compUnit);

        // semantic analysis
        Debug.log(Debug.TERM_RED + ">>>>>>>> Semantic Analysis Output: >>>>>>>>" + Debug.TERM_RESET);

        compUnit.analyzeSemantic();

        String output = compUnit.output();
        System.out.println(output);

        if (ErrorCollector.getInstance().hasErrors()) {
            Path errorFile = Paths.get("error.txt");
            Files.writeString(errorFile, ErrorCollector.getInstance().toString());
            System.out.println(Debug.TERM_RED + "errors: \n" + Debug.TERM_RESET + ErrorCollector.getInstance());
            // return;
        }

        // generate IR
        compUnit.generateIR();

        Debug.log("\n\n" + Debug.TERM_RED + ">>>>>>>> LLVM IR: >>>>>>>>" + Debug.TERM_RESET + "\n");

        System.out.println(IRBuilder.getInstance().generateIR(true));

        // IR without optimization
        Path irFile = Paths.get("llvm_ir.txt");
        Files.writeString(irFile, IRBuilder.getInstance().generateIR(false));

        if (Debug.STAGE_OPTIMIZATION) {
            // IR with optimization
            IRBuilder.getInstance().optimize();
            Path optimizedIRFile = Paths.get("llvm_ir_after_optimization.txt");
            Files.writeString(optimizedIRFile, IRBuilder.getInstance().generateIR(false));
        }

        // generate Object code
        IRBuilder.getInstance().generateObjectCode();
        Debug.log(Debug.TERM_RED + ">>>>>>>> Object Code: >>>>>>>>" + Debug.TERM_RESET);
        System.out.println(MIPSBuilder.getInstance().generateObjectCode(false));

        Path mipsFile = Paths.get("mips.txt");
        Files.writeString(mipsFile, MIPSBuilder.getInstance().generateObjectCode(Debug.STAGE_OPTIMIZATION));

        Debug.log(Debug.TERM_RED + ">>>>>>>> Program exit... >>>>>>>>" + Debug.TERM_RESET);
    }
}
