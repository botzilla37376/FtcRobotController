package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "BS", group = "Auto", preselectTeleOp = "DriveTestv1")
public class AutoBS extends AutoBase {

    @Override
    protected String setupNote() {
        return "BS - starts UP";
    }

    @Override
    protected void runPath() {
        shootAll();          // 4 shooting -> stop launcher + buffer time
        forward(2);          // move forward 2 blocks
        strafeLeft(1);       // strafe left 1
        forward(2.5);        // move forward 2.5
        strafeLeft(1.25);    // left 1.25
    }
}