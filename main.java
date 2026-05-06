
import java.util.Scanner;

public class main {

    public static void main(String[] args) {

        AutomataBuilder builder = new AutomataBuilder();

        DFA dfa = builder.buildLexerDFA();

        DFALexer lexer = new DFALexer(dfa);

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter source code:");

        String input = scanner.nextLine();

        lexer.scan(input);
    }
}

