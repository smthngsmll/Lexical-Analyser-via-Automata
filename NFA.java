
import java.util.*;

public class NFA {

    private NFAState start;
    private Set<NFAState> acceptStates;

    public NFA(NFAState start, Set<NFAState> acceptStates) {
        this.start = start;
        this.acceptStates = acceptStates;
    }

    public NFAState getStartState() {
        return start;
    }

    public Set<NFAState> getAcceptStates() {
        return acceptStates;
    }

    public void addEpsilonTransition(NFAState from, NFAState to) {
        from.addEpsilonTransition(to);
    }
    public Set<Character> getAlphabet() {
    Set<Character> alphabet = new HashSet<>();
    collectAlphabet(start, alphabet, new HashSet<NFAState>());
    return alphabet;
}

private void collectAlphabet(NFAState state, Set<Character> alphabet, Set<NFAState> visited) {
    if (state == null || visited.contains(state)) return;

    visited.add(state);

    for (Character c : state.getTransitionSymbols()) {
        alphabet.add(c);
        for (NFAState next : state.getTransitions(c)) {
            collectAlphabet(next, alphabet, visited);
        }
    }

    for (NFAState next : state.getEpsilonTransitions()) {
        collectAlphabet(next, alphabet, visited);
    }
}
}