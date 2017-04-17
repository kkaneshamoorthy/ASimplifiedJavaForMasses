package Engine;

import Memory.Scope;

import java.util.ArrayDeque;
import java.util.Deque;

public class ScopeStack {
    private Deque<Scope> scopes;

    public ScopeStack() {
        scopes = new ArrayDeque<Scope>();
    }

    public void push(Scope scope) {
        this.scopes.push(scope);
    }

    public Scope pop() {
        return this.scopes.pop();
    }

    public Scope top() {
        return this.scopes.peek();
    }

    public boolean isEmpty() {
        return this.scopes.isEmpty();
    }

}
