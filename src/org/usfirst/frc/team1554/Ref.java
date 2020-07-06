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
package org.usfirst.frc.team1554;

public final class Ref {

    public static final String ROBOT_NAME = "[INSERT NAME HERE]";
    public static final short ROBOT_YEAR = 03737;

    public static final class Buttons {
        public static final int ID_TURBO_DRIVE = 1;
        public static final int ID_SWAP_JOYSTICKS = 10;
        public static final int ID_DISABLE_TWIST = 9;

        public static final int ID_FORKLIFT_UP = 3;
        public static final int ID_FORKLIFT_DOWN = 5;
        public static final int ID_ARMS_TOGGLE = 2;
    }

    public static final class Channels {
        public static final int FL_DMOTOR = 0;
        public static final int RL_DMOTOR = 1;
        public static final int FR_DMOTOR = 8;
        public static final int RR_DMOTOR = 9;

        public static final int WINCH_MOTOR = 7;

        public static final int CHANNEL_ARM_FWD = 1;
        public static final int CHANNEL_ARM_BCK = 2;
    }

    public static final class Ports {
        public static final int JOYSTICK_LEFT = 0;
        public static final int JOYSTICK_RIGHT = 1;
    }

    public static final class Values {
        public static final double DRIVE_SCALE_FACTOR = 0.8;
        public static final double TWIST_STICK_DEADBAND = 0.15;
        public static final double MAG_STICK_DEADBAND = 0.05;

        public static final double AUTO_ROTATION_VAL = -0.85;
        public static final double AUTO_ROTATION_DELAY = 1.65;

        public static final int CONCURRENCY = 2;
    }

}
