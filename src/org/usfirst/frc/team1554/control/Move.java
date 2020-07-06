/*==================================================================================================
 The MIT License (MIT)

 Copyright (c) 2015 Glossawy

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 =================================================================================================*/

package org.usfirst.frc.team1554.control;

import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team1554.lib.common.control.EmulatedJoystickControl;
import org.usfirst.frc.team1554.lib.common.robot.EnhancedRobotBase;
import org.usfirst.frc.team1554.lib.util.IBuilder;

// TODO Consolidate
// TODO Move into RoboLib and combine with Action/ActionSequence framework
public abstract class Move {

    private final Move parent;
    private Move child;

    private boolean active = true;

    private Move() {
        this(null);
    }

    private Move(Move parent) {
        if (parent != null) {
            parent.child = this;
        }

        this.parent = parent;
    }

    abstract public void act();

    private void execute() {
        act();

        if (this.child != null) {
            this.child.execute();
        }
    }

    protected void callUp() {
        if (this.parent != null) {
            this.parent.callUp();
        } else if (this.active) {
            this.active = false;
            execute();
        }
    }

    public void reset() {
        this.active = true;
    }

    public static MoveSequencer startChain(EnhancedRobotBase robot) {
        return new MoveSequencer(robot);
    }

    public static class MoveSequencer implements IBuilder<Move> {

        private Move current = null;
        private EnhancedRobotBase robot;

        private MoveSequencer(EnhancedRobotBase scheme) {
            this.robot = scheme;

            this.current = new Move() {
                @Override
                public void act() {
                }
            };
        }

        public MoveSequencer setScheme(EnhancedRobotBase scheme) {
            this.robot = scheme;
            return this;
        }

        public MoveSequencer forward(final double value) {
            this.current = new Move(this.current) {

                @Override
                public void act() {
                    emulate(0, value, 0);
                }

            };

            return this;
        }

        public MoveSequencer reverse(final double value) {
            return forward(-value);
        }

        public MoveSequencer strafeRight(final double value) {
            this.current = new Move(this.current) {
                @Override
                public void act() {
                    emulate(value, 0, 0);
                }
            };

            return this;
        }

        public MoveSequencer strafeLeft(final double value) {
            return strafeRight(-value);
        }

        public MoveSequencer polar(double magnitude, double angle) {
            final double rad = Math.toRadians(angle);

            final double x = magnitude * Math.cos(rad);
            final double y = magnitude * Math.sin(rad);

            return cartesian(x, y);
        }

        public MoveSequencer cartesian(double x, double y) {
            this.current = new Move(this.current) {
                @Override
                public void act() {
                    emulate(x, y, 0);
                }
            };

            return this;
        }

        public MoveSequencer rotateCartesian(double x, double y, double rot) {
            this.current = new Move(this.current) {

                @Override
                public void act() {
                    emulate(x, y, rot);
                }

            };

            return this;
        }

        public MoveSequencer custom(Runnable run) {
            this.current = new Move(this.current) {
                @Override
                public void act() {
                    run.run();
                }
            };

            return this;
        }

        public MoveSequencer delay(double seconds) {
            this.current = new Move(this.current) {
                @Override
                public void act() {
                    Timer.delay(seconds);
                }
            };

            return this;
        }

        private void emulate(double x, double y, double twist) {
            this.robot.getMotorScheme().updateDrive(this.robot.getMotorScheme().getRobotDrive(), new EmulatedJoystickControl(x, y, twist), this.robot.getBasicSenses());
        }

        @Override
        public Move build() {
            return new Move(this.current) {
                boolean calledUp = false;

                @Override
                public void act() {
                    if (this.calledUp) {
                        return;
                    }

                    this.calledUp = true;
                    super.callUp();
                }

                @Override
                protected void callUp() {
                    act();
                }
            };
        }

        public MoveSequencer stop() {
            this.current = new Move(this.current) {

                @Override
                public void act() {
                    emulate(0, 0, 0);
                }

            };

            return this;
        }

    }

}
