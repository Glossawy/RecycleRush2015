package org.usfirst.frc.team1554.lib.common;

import org.usfirst.frc.team1554.lib.collect.Array;
import org.usfirst.frc.team1554.lib.readonly.ReadOnlyString;
import org.usfirst.frc.team1554.lib.readonly.StringConstant;
import org.usfirst.frc.team1554.lib.util.Callback;
import org.usfirst.frc.team1554.lib.util.IBuilder;

// TODO Consider a Binary Tree Type Structure to better accomodate Conditional Button Actions

/**
 * Basically acts as a Named Runnable. A Name MUST be provided, this
 * allows it to be used in Sendable/NamedSendable Data.
 *
 * @author Matthew Crocco
 */
public abstract class ButtonAction {

    private final ReadOnlyString name;

    public static ButtonAction as(String name, Runnable action) {
        return new ButtonAction(name) {
            @Override
            public void act() {
                action.run();
            }
        };
    }

    public ButtonAction(String name) {
        this.name = new StringConstant(name);
    }

    ButtonAction() {
        this("");
    }

    public String name() {
        return this.name.get();
    }

    abstract public void act();

    // -- Multi Buttons -- //
    static class MultiButton extends ButtonAction {
        private final Array<ButtonAction> actions;
        private boolean stopped = false;

        MultiButton(String name, ButtonAction first) {
            super(name);

            actions = Array.of(true, 4, ButtonAction.class);
            actions.add(first);
        }

        @Override
        public final void act() {
            for (ButtonAction ba : actions)
                if (!stopped)
                    ba.act();
                else
                    break;
        }

        void add(ButtonAction action) {
            actions.add(action);
        }

        void insert(int index, ButtonAction action) {
            actions.insert(index, action);
        }

        void stop() {
            stopped = true;
        }
    }

    public static class MultiButtonBuilder implements IBuilder<ButtonAction> {

        private final MultiButton multiButton;

        private MultiButtonBuilder(String name, ButtonAction firstAction) {
            multiButton = new MultiButton(name, firstAction);
        }

        public static MultiButtonBuilder create(String name, ButtonAction root) {
            return new MultiButtonBuilder(name, root);
        }

        public void nextAction(Runnable action) {
            multiButton.add(new ButtonAction() {
                @Override
                public void act() {
                    action.run();
                }
            });
        }

        public void insertAction(int index, Runnable action) {
            multiButton.insert(index, new ButtonAction() {
                @Override
                public void act() {
                    action.run();
                }
            });
        }

        public void nextConditional(Callback<Boolean> determinant, Runnable trueAction, Runnable falseAction) {
            multiButton.add(new ConditionalButtonAction(determinant) {
                @Override
                public void whenTrue() {
                    if (trueAction != null)
                        trueAction.run();
                }

                @Override
                public void whenFalse() {
                    if (falseAction != null)
                        falseAction.run();
                }
            });
        }

        public void insertConditional(int index, Callback<Boolean> determinant, Runnable trueAction, Runnable falseAction) {
            multiButton.insert(index, new ConditionalButtonAction(determinant) {
                @Override
                public void whenTrue() {
                    if (trueAction != null)
                        trueAction.run();
                }

                @Override
                public void whenFalse() {
                    if (falseAction != null)
                        falseAction.run();
                }
            });
        }

        public void nextConditionalTerminate(Callback<Boolean> determinant, Runnable continueAction) {
            multiButton.add(new ConditionalButtonAction(determinant) {
                @Override
                public void whenTrue() {
                    multiButton.stop();
                }

                @Override
                public void whenFalse() {
                    if (continueAction != null)
                        continueAction.run();
                }
            });
        }

        @Override
        public ButtonAction build() {
            return multiButton;
        }
    }

    // -- Conditional Button -- //
    public static abstract class ConditionalButtonAction extends ButtonAction {

        private final Callback<Boolean> callback;

        public ConditionalButtonAction(String name, Callback<Boolean> callback) {
            super(name);
            this.callback = callback;
        }

        ConditionalButtonAction(Callback<Boolean> callback) {
            this("", callback);
        }

        @Override
        public final void act() {
            if (callback.call())
                whenTrue();
            else
                whenFalse();
        }

        abstract public void whenTrue();

        abstract public void whenFalse();
    }

}
