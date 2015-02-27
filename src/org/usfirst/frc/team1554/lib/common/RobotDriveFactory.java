package org.usfirst.frc.team1554.lib.common;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SpeedController;

public interface RobotDriveFactory<T extends RobotDrive> {

    public static final RobotDriveFactory<RobotDrive> DEFAULT = new RobotDriveFactory<RobotDrive>() {

        @Override
        public RobotDrive createForTwoChannels(SpeedController left, SpeedController right) {
            return new RobotDrive(left, right);
        }

        @Override
        public RobotDrive createForFourChannels(SpeedController frontLeft, SpeedController rearLeft, SpeedController frontRight, SpeedController rearRight) {
            return new RobotDrive(frontLeft, rearLeft, frontRight, rearRight);
        }

    };

    T createForTwoChannels(SpeedController left, SpeedController right);

    T createForFourChannels(SpeedController frontLeft, SpeedController rearLeft, SpeedController frontRight, SpeedController rearRight);

}
