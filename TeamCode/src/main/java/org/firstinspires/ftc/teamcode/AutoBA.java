package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "BA", group = "Auto", preselectTeleOp = "DriveTestv1")
public class AutoBA extends AutoBase {

    private static final double START_DELAY = 10.0;

    @Override
    protected String setupNote() {
        return "BA - starts DOWN (waits 10 s)";
    }

    @Override
    protected void runPath() {
        status("Start delay");
        waitSeconds(START_DELAY); // 10 sec buffer time
        shootAll();               // 4 shooting + buffer time
        forward(2);               // move forward 2
        strafeLeft(3);            // strafe 3 left
    }
}