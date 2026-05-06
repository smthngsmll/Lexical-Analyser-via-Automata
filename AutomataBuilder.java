
import java.util.*;

public class AutomataBuilder {

    public DFA buildLexerDFA() {

        List<NFA> nfas = new ArrayList<>();

        // Keywords
        nfas.add(buildKeywordNFA("if"));
        nfas.add(buildKeywordNFA("then"));
        nfas.add(buildKeywordNFA("else"));
        nfas.add(buildKeywordNFA("while"));
        nfas.add(buildKeywordNFA("return"));
        nfas.add(buildKeywordNFA("for"));
        nfas.add(buildKeywordNFA("break"));
        nfas.add(buildKeywordNFA("continue"));
        nfas.add(buildKeywordNFA("int"));
        nfas.add(buildKeywordNFA("float"));

        // Identifier + Number
        nfas.add(buildIdentifierNFA());
        nfas.add(buildNumberNFA());

        // Operators
        nfas.add(buildOperatorNFA("+"));
        nfas.add(buildOperatorNFA("-"));
        nfas.add(buildOperatorNFA("*"));
        nfas.add(buildOperatorNFA("/"));
        nfas.add(buildOperatorNFA("="));
        nfas.add(buildOperatorNFA("=="));
        nfas.add(buildOperatorNFA("!="));
        nfas.add(buildOperatorNFA("<="));
        nfas.add(buildOperatorNFA(">="));
        nfas.add(buildOperatorNFA("<"));
        nfas.add(buildOperatorNFA(">"));

        // Delimiters
        nfas.add(buildDelimiterNFA('('));
        nfas.add(buildDelimiterNFA(')'));
        nfas.add(buildDelimiterNFA('{'));
        nfas.add(buildDelimiterNFA('}'));
        nfas.add(buildDelimiterNFA(';'));
        nfas.add(buildDelimiterNFA(','));

        NFA combined = combineNFAs(nfas);

        return convertToDFA(combined);
    }

    private NFA combineNFAs(List<NFA> nfas) {

        // Set of accept states for the new combined NFA
        Set<NFAState> newAcceptStates = new HashSet<>();

        // New start state that will point to each NFA
        NFAState newStartState = new NFAState();

        // Create the resulting NFA with the new start and accept states
        NFA newNFA = new NFA(newStartState, newAcceptStates);

        // For each input NFA:
        for (NFA nfa : nfas) {

            // Add all its accept states to the new NFA
            newAcceptStates.addAll(nfa.getAcceptStates());

            // Add epsilon transition from new start state to this NFA's start state
            newStartState.addEpsilonTransition(nfa.getStartState());
        }

        return newNFA;
    }

    private DFA convertToDFA(NFA nfa) {

        // Alphabet of the automaton (input symbols)
        Set<Character> alphabet = nfa.getAlphabet();

        // Compute ε-closure of the NFA start state → DFA start state
        Set<NFAState> startSubset =
                epsilonClosure(Set.of(nfa.getStartState()));

        DFAState startState = new DFAState(startSubset);

        // Initialize DFA with the start state
        DFA dfa = new DFA(startState);

        // Maps each subset of NFA states → corresponding DFA state
        Map<Set<NFAState>, DFAState> stateMap =
                new HashMap<>();

        // Queue for BFS traversal over subsets (DFA states)
        Queue<Set<NFAState>> queue =
                new LinkedList<>();

        // Initialize processing with the start subset
        stateMap.put(startSubset, startState);

        queue.add(startSubset);

        // Process each discovered subset (a new DFA state)
        while (!queue.isEmpty()) {

            // Get next subset to process
            Set<NFAState> currentSet = queue.poll();

            DFAState currentState =
                    stateMap.get(currentSet);

            // For each input symbol, compute the next subset
            for (char symbol : alphabet) {

                // move(currentSet, symbol): all states reachable from currentSet via 'symbol'
                // epsilonClosure(...): include all states reachable via epsilon transitions afterward
                Set<NFAState> nextSet =
                        epsilonClosure(move(currentSet, symbol));

                if (nextSet.isEmpty()) {
                    continue;
                }

                // Check if this subset has already been created as a DFA state
                DFAState nextState =
                        stateMap.get(nextSet);

                if (nextState == null) {

                    // Create a new DFA state for this subset
                    nextState = new DFAState(nextSet);

                    // A DFA state is accepting if ANY NFA state in the subset is an accepting state.
                    boolean accept = false;

                    String tokenType = null;

                    for (NFAState state : nextSet) {

                        if (state.isAccept()) {

                            accept = true;

                            tokenType =
                                    state.getTokenType();

                            break;
                        }
                    }

                    nextState.setAccept(accept);

                    if (accept) {
                        nextState.setTokenType(tokenType);
                    }

                    // Add new state to DFA and tracking structures
                    dfa.addState(nextState);

                    stateMap.put(nextSet, nextState);

                    queue.add(nextSet);
                }

                // Add transition: currentState --symbol--> nextState
                currentState.addTransition(symbol, nextState);
            }
        }

        return dfa;
    }

    private Set<NFAState> epsilonClosure(Set<NFAState> states) {

        // Stores all states in the epsilon closure
        Set<NFAState> closure = new HashSet<>();

        // Queue for BFS traversal of epsilon transitions
        Queue<NFAState> queue =
                new LinkedList<>();

        // Initialize closure and queue with the starting states
        for (NFAState state : states) {

            queue.add(state);

            closure.add(state);
        }

        // Explore epsilon transitions until no new states are found
        while (!queue.isEmpty()) {

            // Get the next state to process
            NFAState current = queue.poll();

            // Get all states reachable via epsilon transitions from current
            List<NFAState> nextSet =
                    current.getEpsilonTransitions();

            // If there are epsilon transitions, process them
            if (nextSet != null) {

                for (NFAState next : nextSet) {

                    // Add unseen states to closure and continue exploration
                    if (!closure.contains(next)) {

                        closure.add(next);

                        queue.add(next);
                    }
                }
            }
        }

        return closure;
    }

    private Set<NFAState> move(Set<NFAState> states,
                               char symbol) {

        // Stores all states reachable via 'symbol'
        Set<NFAState> reachableStates =
                new HashSet<>();

        // For each state, follow transitions labeled with the symbol
        for (NFAState state : states) {

            // Get all transitions from this state on the given symbol
            reachableStates.addAll(
                    state.getTransitions(symbol));
        }

        return reachableStates;
    }

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

        id.addTransition('_', id);

        id.setAccept(true);

        id.setTokenType("ID");

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

        num.setTokenType("NUM");

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

        current.setTokenType(
                "KW_" + keyword.toUpperCase()
        );

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

    switch (operator) {
        case "+":
            current.setTokenType("OP_PLUS");
            break;

        case "-":
            current.setTokenType("OP_MINUS");
            break;

        case "*":
            current.setTokenType("OP_MUL");
            break;

        case "/":
            current.setTokenType("OP_DIV");
            break;

        case "=":
            current.setTokenType("ASSIGN");
            break;

        case "==":
            current.setTokenType("EQ");
            break;

        case "!=":
            current.setTokenType("NEQ");
            break;

        case "<=":
            current.setTokenType("LE");
            break;

        case ">=":
            current.setTokenType("GE");
            break;

        case "<":
            current.setTokenType("LT");
            break;

        case ">":
            current.setTokenType("GT");
            break;
    }

    Set<NFAState> accept = new HashSet<>();

    accept.add(current);

    return new NFA(start, accept);
} 

       

    private NFA buildDelimiterNFA(char delimiter) {

        NFAState start = new NFAState();

        NFAState end = new NFAState();

        start.addTransition(delimiter, end);

        end.setAccept(true);

       switch (delimiter) {

    case '(':
        end.setTokenType("LPAREN");
        break;

    case ')':
        end.setTokenType("RPAREN");
        break;

    case '{':
        end.setTokenType("LBRACE");
        break;

    case '}':
        end.setTokenType("RBRACE");
        break;

    case ';':
        end.setTokenType("SEMI");
        break;

    case ',':
        end.setTokenType("COMMA");
        break;
}

        Set<NFAState> accept = new HashSet<>();

        accept.add(end);

        return new NFA(start, accept);
    }
}