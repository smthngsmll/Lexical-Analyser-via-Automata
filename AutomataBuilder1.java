import java.util.*;

public class AutomataBuilder {

    private NFA buildIdentifierNFA() {
        NFAState start = new NFAState();
        NFAState id = new NFAState();

        for (char c = 'a'; c <= 'z'; c++) {
            start.addTransition(c, id);
            id.addTransition(c, id);
        }

        for (char c = 'A'; c <= 'Z'; c++) {
            start.addTransition(c, id);
            id.addTransition(c, id);
        }

        for (char c = '0'; c <= '9'; c++) {
            id.addTransition(c, id);
        }

        id.setAccept(true);
        id.setTokenType("IDENTIFIER");

        Set<NFAState> accept = new HashSet<>();
        accept.add(id);

        return new NFA(start, accept);
    }

    private NFA buildNumberNFA() {
        NFAState start = new NFAState();
        NFAState num = new NFAState();

        for (char c = '0'; c <= '9'; c++) {
            start.addTransition(c, num);
            num.addTransition(c, num);
        }

        num.setAccept(true);
        num.setTokenType("NUMBER");

        Set<NFAState> accept = new HashSet<>();
        accept.add(num);

        return new NFA(start, accept);
    }

    private NFA buildKeywordNFA(String keyword) {
        NFAState start = new NFAState();
        NFAState current = start;

        for (char c : keyword.toCharArray()) {
            NFAState next = new NFAState();
            current.addTransition(c, next);
            current = next;
        }

        current.setAccept(true);
        current.setTokenType("KEYWORD");

        Set<NFAState> accept = new HashSet<>();
        accept.add(current);

        return new NFA(start, accept);
    }

    private NFA buildOperatorNFA(String operator) {
        NFAState start = new NFAState();
        NFAState current = start;

        for (char c : operator.toCharArray()) {
            NFAState next = new NFAState();
            current.addTransition(c, next);
            current = next;
        }

        current.setAccept(true);
        current.setTokenType("OPERATOR");

        Set<NFAState> accept = new HashSet<>();
        accept.add(current);

        return new NFA(start, accept);
    }

    private NFA buildDelimiterNFA(char delimiter) {
        NFAState start = new NFAState();
        NFAState end = new NFAState();

        start.addTransition(delimiter, end);

        end.setAccept(true);
        end.setTokenType("DELIMITER");

        Set<NFAState> accept = new HashSet<>();
        accept.add(end);

        return new NFA(start, accept);
    }
}