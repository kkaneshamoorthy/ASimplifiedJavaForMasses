package Memory;

import java.util.ArrayDeque;
import java.util.Deque;

/***
 * This class provides a data structure to store and manage scopes
 */
public class ScopeStack {

    /***
     * Stack of scope
     */
    private Deque<Scope> scopes;

    public ScopeStack() {
        scopes = new ArrayDeque<Scope>();
    }

    /***
     * Pushes the scope on to the stack, becomes the current scope
     * @param scope
     */
    public void push(Scope scope) {
        this.scopes.push(scope);
    }

    /***
     * Removes the scope from the stack, the current scope is the scope below
     * @return
     */
    public Scope pop() {
        return this.scopes.pop();
    }

    /***
     * Returns the current scope
     * @return
     */
    public Scope top() {
        return this.scopes.peek();
    }

    /***
     * Is stack empty
     * @return
     */
    public boolean isEmpty() {
        return this.scopes.isEmpty();
    }

}
