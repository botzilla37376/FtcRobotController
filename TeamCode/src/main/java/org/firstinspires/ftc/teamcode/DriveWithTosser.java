package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "DriveTestv1", group = "Test")
public class DriveTestv1 extends LinearOpMode {

    private static final double AXIAL_SCALE = 0.95;
    private static final double LATERAL_SCALE = 0.95;
    private static final double YAW_SCALE = 0.95;
    private static final double INTAKE_TEST_POWER = 1.0;
    private static final double SERVO_TEST_POWER = 1.0;

    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;
    private DcMotor tosser;
    private DcMotor frontIntake;
    private CRServo rightIntake;
    private CRServo windmill;

    @Override
    public void runOpMode() {
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        tosser = hardwareMap.get(DcMotor.class, "tosser");
        frontIntake = hardwareMap.get(DcMotor.class, "frontIntake");
        rightIntake = hardwareMap.get(CRServo.class, "rightIntake");
        windmill = hardwareMap.get(CRServo.class, "windmill");

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

        tosser.setPower(0);
        tosser.setDirection(DcMotor.Direction.FORWARD);
        tosser.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        frontIntake.setPower(0);
        frontIntake.setDirection(DcMotor.Direction.FORWARD);
        frontIntake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontIntake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        rightIntake.setDirection(CRServo.Direction.FORWARD);
        windmill.setDirection(CRServo.Direction.FORWARD);
        rightIntake.setPower(0);
        windmill.setPower(0);

        telemetry.addData("Status", "DriveTestv1 ready - hold controls to test");
        telemetry.addLine("X/B tosser (+/- full power)");
        telemetry.addData("A/Y front intake", "+/- %.0f%%", 100 * INTAKE_TEST_POWER);
        telemetry.addLine("RB/RT right servo (+/-)");
        telemetry.addLine("D-pad up/down windmill (+/-)");
        telemetry.addData("Servo power limit", "%.0f%%", 100 * SERVO_TEST_POWER);
        telemetry.update();

        waitForStart();

        try {
            while (opModeIsActive()) {
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

                // X takes priority if both tosser buttons are held.
                if (gamepad1.x) {
                    tosser.setPower(0.45);
                } else if (gamepad1.b) {
                    tosser.setPower(-1.0);
                } else {
                    tosser.setPower(0.0);
                }

                double frontIntakePower = INTAKE_TEST_POWER
                        * ((gamepad1.a ? 1.0 : 0.0) - (gamepad1.y ? 1.0 : 0.0));
                double rightIntakePower = SERVO_TEST_POWER
                        * ((gamepad1.right_bumper ? 1.0 : 0.0) - gamepad1.right_trigger);
                double windmillPower = SERVO_TEST_POWER
                        * ((gamepad1.dpad_up ? 1.0 : 0.0) - (gamepad1.dpad_down ? 1.0 : 0.0));

                frontIntake.setPower(frontIntakePower);
                rightIntake.setPower(rightIntakePower);
                windmill.setPower(windmillPower);

                telemetry.addData("Tosser X/B", "%.2f", tosser.getPower());
                telemetry.addData("Front intake A/Y", "%.2f", frontIntakePower);
                telemetry.addData("Right servo RB/RT", "%.2f", rightIntakePower);
                telemetry.addData("Windmill up/down", "%.2f", windmillPower);
                telemetry.update();
                idle();
            }
        } finally {
            frontLeft.setPower(0);
            frontRight.setPower(0);
            backLeft.setPower(0);
            backRight.setPower(0);
            tosser.setPower(0);
            frontIntake.setPower(0);
            rightIntake.setPower(0);
            windmill.setPower(0);
        }
    }
}}