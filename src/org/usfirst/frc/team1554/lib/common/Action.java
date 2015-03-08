package org.usfirst.frc.team1554.lib.common;

import org.usfirst.frc.team1554.lib.util.Callback;

/**
 * Needs Documentation
 *
 * @author Matthew
 *         Created 2/28/2015 at 9:31 PM
 */
public abstract class Action {

    public final String actionName;
    private Action next;

    public static Action as(String name, Runnable action) {
        return new Action(name) {
            @Override
            protected void act() {
                action.run();
            }
        };
    }

    public static Action newConditionalAction(Callback<Boolean> checkFunction, Action trueAction, Action falseAction) {
        return new Action(trueAction.actionName + " OR " + falseAction.actionName) {
            @Override
            protected void act() {
                if (checkFunction.call())
                    trueAction.act();
                else
                    falseAction.act();
            }

            @Override
            protected Action next() {
                throw new UnsupportedOperationException("Ambiguous Case! Should TrueAction or FalseAction be Returned?! Conditional Actions do not have a next() value...");
            }

            @Override
            protected Action append(Action node) {
                throw new UnsupportedOperationException("Ambiguous Case! Should TrueAction or FalseAction be Returned?! Conditional Actions cannot be appended to...");
            }
        };
    }

    public Action(String name) {
        this.actionName = name;
    }

    Action(Action parent) {
        this.actionName = parent.actionName;
    }

    public String name() {
        return actionName;
    }

    @Override
    public String toString() {
        return name();
    }

    abstract protected void act();

    protected Action next() {
        return next;
    }

    void setNext(Action next) {
        this.next = next;
    }

    Action append(Action node) {
        if (next == null)
            next = node;
        else
            next.append(node);

        return next;
    }

}
