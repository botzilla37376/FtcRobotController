package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "RA", group = "Auto", preselectTeleOp = "DriveTestv1")
public class AutoRA extends AutoBase {

    @Override
    protected String setupNote() {
        return "RA - starts UP";
    }

    @Override
    protected void runPath() {
        shootAll();          // 4 shooting -> stop launcher + buffer time
        forward(2);          // move forward 2 blocks
        strafeLeft(1);       // strafe left 1 block
        forward(2.5);        // move forward 2.5
        strafeLeft(1.25);    // 1.25 (assumed strafe left, same as BS)
    }
}