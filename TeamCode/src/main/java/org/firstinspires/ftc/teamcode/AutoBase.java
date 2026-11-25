package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Shared hardware + helpers for all autonomous routines.
 * Hardware names, directions and drive mixing match DriveTestv1 exactly.
 *
 * Movement is TIME-BASED (drive motors run without encoders in DriveTestv1).
 * Tune SECONDS_PER_TILE_* on the field: drive 1 tile, adjust until it lands right.
 * 1 "block" = 1 field tile (24 in).
 */
public abstract class AutoBase extends LinearOpMode {

    // ---------------- DRIVE TUNING ----------------
    protected static final double DRIVE_POWER = 0.5;
    protected static final double STRAFE_POWER = 0.5;
    protected static final double SECONDS_PER_TILE_FORWARD = 1.0;  // TUNE ME
    protected static final double SECONDS_PER_TILE_STRAFE = 1.3;   // TUNE ME (strafing is slower)
    protected static final double SETTLE_TIME = 0.25;              // pause after each move

    // ---------------- SHOOTER TUNING ----------------
    protected static final int SHOTS = 4;
    protected static final double TOSSER_SHOOT_POWER = 0.45;   // same as TeleOp X button
    protected static final double TOSSER_SPINUP_TIME = 1.5;    // let flywheel get to speed
    protected static final double FEED_TIME_PER_SHOT = 0.6;    // how long feeders run per shot
    protected static final double TIME_BETWEEN_SHOTS = 0.8;    // recovery between shots
    protected static final double POST_SHOOT_BUFFER = 1.0;     // "buffer time" after shooting

    // Feed directions: + matches TeleOp (windmill = D-pad up, rightIntake = RB, frontIntake = A).
    // Flip a sign if it runs backwards, or set to 0 to not use that mechanism for feeding.
    protected static final double WINDMILL_FEED_POWER = 1.0;
    protected static final double RIGHT_INTAKE_FEED_POWER = 1.0;
    protected static final double FRONT_INTAKE_FEED_POWER = 1.0;

    protected DcMotor frontLeft, frontRight, backLeft, backRight;
    protected DcMotor tosser, frontIntake;
    protected CRServo rightIntake, windmill;

    private final ElapsedTime matchTimer = new ElapsedTime();

    /** The routine itself. */
    protected abstract void runPath();

    /** Shown on the Driver Station during init, e.g. where to place the robot. */
    protected abstract String setupNote();

    @Override
    public final void runOpMode() {
        initHardware();

        telemetry.addData("Auto", getClass().getSimpleName());
        telemetry.addData("Setup", setupNote());
        telemetry.addData("Status", "Ready - press START");
        telemetry.update();

        waitForStart();
        matchTimer.reset();

        try {
            if (opModeIsActive()) {
                runPath();
            }
            status("Done");
        } finally {
            stopAll();
        }
    }

    // ---------------- HARDWARE ----------------

    private void initHardware() {
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

        for (DcMotor m : new DcMotor[] {frontLeft, frontRight, backLeft, backRight}) {
            m.setPower(0);
            m.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            m.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
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
    }

    // ---------------- DRIVE ----------------

    /**
     * Robot-relative drive. Uses the SAME mixing as DriveTestv1, so
     * forward / strafe / turn behave exactly like the sticks do in TeleOp.
     *
     * @param forward     + = forward      (TeleOp: left stick up)
     * @param strafeRight + = strafe right (TeleOp: left stick right)
     * @param turn        + = turn right   (TeleOp: right stick right)
     */
    protected void setDrive(double forward, double strafeRight, double turn) {
        double axial = -strafeRight;   // TeleOp: axial = -left_stick_x
        double lateral = forward;      // TeleOp: lateral = -left_stick_y
        double yaw = turn;             // TeleOp: yaw = right_stick_x

        double fl = axial + lateral - yaw;
        double fr = axial - lateral - yaw;
        double bl = axial - lateral + yaw;
        double br = axial + lateral + yaw;

        double max = Math.max(Math.max(Math.abs(fl), Math.abs(fr)),
                Math.max(Math.abs(bl), Math.abs(br)));
        if (max > 1.0) {
            fl /= max; fr /= max; bl /= max; br /= max;
        }

        frontLeft.setPower(fl);
        frontRight.setPower(fr);
        backLeft.setPower(bl);
        backRight.setPower(br);
    }

    protected void stopDrive() {
        setDrive(0, 0, 0);
    }

    /** Drive forward (negative = backward) a number of tiles. */
    protected void forward(double tiles) {
        status(String.format("Forward %.2f tiles", tiles));
        setDrive(Math.signum(tiles) * DRIVE_POWER, 0, 0);
        waitSeconds(Math.abs(tiles) * SECONDS_PER_TILE_FORWARD);
        stopDrive();
        waitSeconds(SETTLE_TIME);
    }

    protected void strafeLeft(double tiles) {
        strafeRight(-tiles);
    }

    /** Strafe right (negative = left) a number of tiles. */
    protected void strafeRight(double tiles) {
        status(String.format("Strafe %s %.2f tiles", tiles >= 0 ? "right" : "left", Math.abs(tiles)));
        setDrive(0, Math.signum(tiles) * STRAFE_POWER, 0);
        waitSeconds(Math.abs(tiles) * SECONDS_PER_TILE_STRAFE);
        stopDrive();
        waitSeconds(SETTLE_TIME);
    }

    // ---------------- SHOOTER ----------------

    /** Spin up the tosser and feed SHOTS balls, then stop the launcher and wait the buffer. */
    protected void shootAll() {
        status("Spinning up tosser");
        tosser.setPower(TOSSER_SHOOT_POWER);
        waitSeconds(TOSSER_SPINUP_TIME);

        for (int i = 1; i <= SHOTS && opModeIsActive(); i++) {
            status("Shot " + i + " of " + SHOTS);
            setFeed(true);
            waitSeconds(FEED_TIME_PER_SHOT);
            setFeed(false);
            if (i < SHOTS) {
                waitSeconds(TIME_BETWEEN_SHOTS);
            }
        }

        status("Stopping launcher + buffer");
        tosser.setPower(0);
        waitSeconds(POST_SHOOT_BUFFER);
    }

    private void setFeed(boolean on) {
        windmill.setPower(on ? WINDMILL_FEED_POWER : 0);
        rightIntake.setPower(on ? RIGHT_INTAKE_FEED_POWER : 0);
        frontIntake.setPower(on ? FRONT_INTAKE_FEED_POWER : 0);
    }



    /** Wait without blocking STOP. */
    protected void waitSeconds(double seconds) {
        ElapsedTime t = new ElapsedTime();
        while (opModeIsActive() && t.seconds() < seconds) {
            idle();
        }
    }

    protected void status(String step) {
        telemetry.addData("Auto", getClass().getSimpleName());
        telemetry.addData("Step", step);
        telemetry.addData("Match time", "%.1f s", matchTimer.seconds());
        telemetry.update();
    }

    protected void stopAll() {
        stopDrive();
        tosser.setPower(0);
        frontIntake.setPower(0);
        rightIntake.setPower(0);
        windmill.setPower(0);
    }
}