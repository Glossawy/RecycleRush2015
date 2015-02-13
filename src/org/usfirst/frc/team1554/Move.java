package org.usfirst.frc.team1554;

import org.usfirst.frc.team1554.lib.EnhancedRobotBase;
import org.usfirst.frc.team1554.lib.util.IBuilder;

import edu.wpi.first.wpilibj.Timer;

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

	private final void execute() {
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

	public static Builder startChain(EnhancedRobotBase robot) {
		return new Builder(robot);
	}

	public static class Builder implements IBuilder<Move> {

		private Move current = null;
		private EnhancedRobotBase robot;

		private Builder(EnhancedRobotBase scheme) {
			this.robot = scheme;

			this.current = new Move() {
				@Override
				public void act() {
				}
			};
		}

		public Builder setScheme(EnhancedRobotBase scheme) {
			this.robot = scheme;
			return this;
		}

		public Builder forward(final double value) {
			this.current = new Move(this.current) {

				@Override
				public void act() {
					emulate(0, value, 0);
				}

			};

			return this;
		}

		public Builder reverse(final double value) {
			return forward(-value);
		}

		public Builder strafeRight(final double value) {
			this.current = new Move(this.current) {
				@Override
				public void act() {
					emulate(value, 0, 0);
				}
			};

			return this;
		}

		public Builder strafeLeft(final double value) {
			return strafeRight(-value);
		}

		public Builder polar(double magnitude, double angle) {
			final double rad = Math.toRadians(angle);

			final double x = magnitude * Math.cos(rad);
			final double y = magnitude * Math.sin(rad);

			return cartesian(x, y);
		}

		public Builder cartesian(double x, double y) {
			this.current = new Move(this.current) {
				@Override
				public void act() {
					emulate(x, y, 0);
				}
			};

			return this;
		}

		public Builder custom(Runnable run) {
			this.current = new Move(this.current) {
				@Override
				public void act() {
					run.run();
				}
			};

			return this;
		}

		public Builder delay(double seconds) {
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
						Builder.this.robot.getMotorScheme().getRobotDrive().stopMotor();
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

	}

}
