package org.usfirst.frc.team1554.lib.common;

import org.usfirst.frc.team1554.lib.util.Callback;

// TODO Resolve limitations inherent in allowing forking logic.

/**
 * Used to more easily create a structured sequence of Button Actions. This class does
 * extend Action and as such can be used in it's place. This was done to allow someone
 * to append ActionSequences on to other ActionSequences, this allows for more flexibility
 * and the inclusion of forking logic without the need for unnecessarily complex collections
 * and checks. <br />
 * <br />
 * Since forking logic creates ambiguity as to which Sequence should be returned by {@link
 * #appendIf(org.usfirst.frc.team1554.lib.util.Callback, ActionSequence, ActionSequence)}
 * appendIf},
 * it is considered a terminal action for the sequence it is called upon. As such, said sequence
 * should
 * no longer be appended to. To protect against such a pitfall, an UnsupportedOperationException
 * will
 * be thrown. Otherwise we have a nasty runtime error. <br />
 * <br />
 * Despite these limitations, holding on to each individual fork allows for the following flow:
 * <pre>
 *                     --> D1 -> E1 -> @...
 *                   /
 * A -> B -> C -> @-
 *                   \
 *                     --> D2 -> E2 -> @...
 * </pre>
 * Where '@' indicates a new branching point created by appendIf that leads to two different
 * ActionSequences. Conditional Logic is now possible in a rudimentary form using Actions, Even
 * allowing simple loops using Callbacks.
 *
 * @author Matthew
 *         Created 2/28/2015 at 9:01 PM
 */
public final class ActionSequence extends Action {

    private Action root, last;

    public ActionSequence(String name, Action root) {
        super(name);

        this.root = root;
        this.last = root;

        setNext(root);
    }

    public ActionSequence(Action root) {
        this(root.actionName + "-Sequence", root);
    }

    @Override
    public ActionSequence append(Action next) {
        this.last.append(next);

        this.last = next;
        return this;
    }

    public ActionSequence prepend(Action newRoot) {
        newRoot.append(root);
        this.root = newRoot;

        return this;
    }

    public Action appendIf(Callback<Boolean> checkFunction, ActionSequence trueAction, ActionSequence falseAction) {
        append(Action.newConditionalAction(checkFunction, trueAction, falseAction));
        return this;
    }

    @Override
    public void act() {
        root.act();
    }

}
