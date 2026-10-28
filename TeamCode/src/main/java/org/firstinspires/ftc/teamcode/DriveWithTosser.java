package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Drive With Tosser", group = "Drive")
public class DriveWithTosser extends LinearOpMode {

    private static final double AXIAL_SCALE = 0.95;
    private static final double LATERAL_SCALE = 0.95;
    private static final double YAW_SCALE = 0.95;

    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;
    private DcMotor tosser;

    @Override
    public void runOpMode() {
        // Hardware configuration names.
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        tosser = hardwareMap.get(DcMotor.class, "tosser");

        // Drive motor directions.
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        for (DcMotor motor : new DcMotor[] {
                frontLeft, frontRight, backLeft, backRight
        }) {
            motor.setPower(0);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        // Tosser setup.
        tosser.setPower(0);
        tosser.setDirection(DcMotor.Direction.FORWARD);
        tosser.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.addData("Status", "Drive train and tosser initialized");
        telemetry.update();

        waitForStart();

        try {
            while (opModeIsActive()) {
                // Movement controls.
                double axial = AXIAL_SCALE * -gamepad1.left_stick_x;
                double lateral = -LATERAL_SCALE * gamepad1.left_stick_y;
                double yaw = YAW_SCALE * gamepad1.right_stick_x;

                double frontLeftPower = axial + lateral - yaw;
                double frontRightPower = axial - lateral - yaw;
                double backLeftPower = axial - lateral + yaw;
                double backRightPower = axial + lateral + yaw;

                // Normalize wheel powers when a magnitude exceeds 1.
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

                // Hold X for forward or B for reverse, both at full power.
                // X takes priority if both buttons are held.
                if (gamepad1.x) {
                    tosser.setPower(1.0);
                } else if (gamepad1.b) {
                    tosser.setPower(-1.0);
                } else {
                    tosser.setPower(0.0);
                }

                idle();
            }
        } finally {
            frontLeft.setPower(0);
            frontRight.setPower(0);
            backLeft.setPower(0);
            backRight.setPower(0);
            tosser.setPower(0);
        }
    }
}
