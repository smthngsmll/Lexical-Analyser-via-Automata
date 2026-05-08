
import java.util.Scanner;

public class main {

    public static void main(String[] args) {

        AutomataBuilder builder = new AutomataBuilder();

        DFA dfa = builder.buildLexerDFA();

        DFALexer lexer = new DFALexer(dfa);

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter source code:");

         StringBuilder input = new StringBuilder();

        while (true) {

            String line = scanner.nextLine();

            if (line.equals("END")) {
                break;
            }

            input.append(line).append("\n");
        }

        lexer.scan(input.toString());
    }
}

