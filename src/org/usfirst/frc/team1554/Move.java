package org.usfirst.frc.team1554;

import org.usfirst.frc.team1554.lib.EnhancedRobotBase;
import org.usfirst.frc.team1554.lib.util.IBuilder;

import edu.wpi.first.wpilibj.Timer;

public abstract class Move {

	private Move parent;
	private Move child;
	
	private Move() {
		this(null);
	}
	
	private Move(Move parent) {
		if(parent != null) {
			parent.child = this;
		}
		
		this.parent = parent;
	}
	
	abstract public void act();
	
	private final void execute() {
		act();
		
		if(this.child != null)
			child.execute();
	}
	
	protected void callUp() {
		if(parent != null)
			parent.callUp();
		else
			execute();
	}
	
	public static Builder startChain(EnhancedRobotBase robot) {
		return new Builder(robot);
	}
	
	public static class Builder implements IBuilder<Move> {

		private Move current = null;
		private EnhancedRobotBase robot;
		
		private Builder(EnhancedRobotBase scheme) {
			this.robot = scheme;
			
			current = new Move() {
				@Override public void act() {}
			};
		}
		
		public Builder setScheme(EnhancedRobotBase scheme) {
			this.robot = scheme;
			return this;
		}
		
		public Builder forward(final double value) {
			current = new Move(current) {

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
			current = new Move(current) {
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
			double rad = Math.toRadians(angle);
			
			double x = magnitude * Math.cos(rad);
			double y = magnitude * Math.sin(rad);
			
			return cartesian(x, y);
		}
		
		public Builder cartesian(double x, double y) {
			current = new Move(current) {
				@Override
				public void act() {
					emulate(x, y, 0);
				}
			};
			
			return this;
		}
		
		public Builder custom(Runnable run) {
			current = new Move(current) {
				@Override
				public void act() {
					run.run();
				}
			};
			
			return this;
		}
		
		public Builder delay(double seconds) {
			current = new Move(current) {
				@Override
				public void act() {
					Timer.delay(seconds);
				}
			};
			
			return this;
		}
	
		private void emulate(double x, double y, double twist) {
			robot.getMotorScheme().updateDrive(robot.getMotorScheme().getRobotDrive(), new EmulatedJoystickControl(x, y, twist), robot.getBasicSenses()); 
		}
		
		@Override
		public Move build() {
			return new Move(current) {
				boolean calledUp = false;
				
				@Override
				public void act() {
					if(calledUp) {
						robot.getMotorScheme().getRobotDrive().stopMotor();
						return;
					}
					
					calledUp = true;
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
