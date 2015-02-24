package org.usfirst.frc.team1554.lib.util;

import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary;
import edu.wpi.first.wpilibj.communication.HALControlWord;
import edu.wpi.first.wpilibj.hal.HALUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Matthew on 2/22/2015.
 */
class HALRoboUtils {

    static boolean userButton() {
        final HALControlWord control = FRCNetworkCommunicationsLibrary.HALGetControlWord();

        if (control.getDSAttached()) {
            final ByteBuffer status = ByteBuffer.allocateDirect(4);
            status.order(ByteOrder.LITTLE_ENDIAN);

            final boolean val = HALUtil.getFPGAButton(status.asIntBuffer());
            HALUtil.checkStatus(status.asIntBuffer());
            return val;
        }

        return false;
    }

    static void toDS(String message) {
        final HALControlWord controlWord = FRCNetworkCommunicationsLibrary.HALGetControlWord();
        if (controlWord.getDSAttached()) {
            FRCNetworkCommunicationsLibrary.HALSetErrorData(message);
        }
    }

}
