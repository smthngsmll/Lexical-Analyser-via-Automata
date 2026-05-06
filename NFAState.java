
import java.util.*;

public class NFAState {

    private Map<Character, List<NFAState>> transitions = new HashMap<>();
    private List<NFAState> epsilonTransitions = new ArrayList<>();

    private boolean isAccept = false;
    private String tokenType;

    public void addTransition(char symbol, NFAState state) {
        transitions.computeIfAbsent(symbol, k -> new ArrayList<>()).add(state);
    }

    public void addEpsilonTransition(NFAState state) {
        epsilonTransitions.add(state);
    }
public Set<Character> getTransitionSymbols() {
    return transitions.keySet();
}
    public List<NFAState> getTransitions(char symbol) {
        return transitions.getOrDefault(symbol, new ArrayList<>());
    }

    public List<NFAState> getEpsilonTransitions() {
        return epsilonTransitions;
    }

    public void setAccept(boolean accept) {
        this.isAccept = accept;
    }

    public boolean isAccept() {
        return isAccept;
    }

    public void setTokenType(String type) {
        this.tokenType = type;
    }

    public String getTokenType() {
        return tokenType;
    }
}
