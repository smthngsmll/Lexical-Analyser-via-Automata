
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
}