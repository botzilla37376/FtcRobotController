package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Power Overdrive - Drive Train Only", group = "Drive")
public class DriveTrainOnly extends LinearOpMode {

    // Original input multipliers, applied before wheel-power normalization.
    private static final double AXIAL_SCALE = 1;
    private static final double LATERAL_SCALE = 1;
    private static final double YAW_SCALE = 1;

    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    @Override
    public void runOpMode() {
        // These names must match the robot's hardware configuration exactly.
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        // Motor directions preserved from the supplied Java and Blocks files.
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        for (DcMotor motor : new DcMotor[] {frontLeft, frontRight, backLeft, backRight}) {
            motor.setPower(0);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        telemetry.addData("Status", "Drive train initialized");
        telemetry.update();

        waitForStart();

        try {
            while (opModeIsActive()) {
                // Preserve the original mapping: left X -> axial, left Y -> lateral.
                double axial = AXIAL_SCALE * gamepad1.left_stick_x;
                double lateral = LATERAL_SCALE * gamepad1.left_stick_y;
                double yaw = YAW_SCALE * gamepad1.right_stick_x;

                // Original wheel-power calculations.
                double frontLeftPower = axial + lateral - yaw;
                double frontRightPower = axial - lateral - yaw;
                double backLeftPower = axial - lateral + yaw;
                double backRightPower = axial + lateral + yaw;

                // Scale all four powers together only when a magnitude exceeds 1.
                double max = Math.max(
                        Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower)),
                        Math.max(Math.abs(backLeftPower), Math.abs(backRightPower)));

                if (max > 1.0) {
                    frontLeftPower /= max;
                    frontRightPower /= max;
                    backLeftPower /= max;
                    backRightPower /= max;
                }

                frontLeft.setPower(frontLeftPower);
                frontRight.setPower(frontRightPower);
                backLeft.setPower(backLeftPower);
                backRight.setPower(backRightPower);

                idle();
            }
        } finally {
            frontLeft.setPower(0);
            frontRight.setPower(0);
            backLeft.setPower(0);
            backRight.setPower(0);
        }
    }
}
