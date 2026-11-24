
package org.firstinspires.ftc.teamcode;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "PhoneIMUTest", group = "Test")
public class PhoneIMUTest extends LinearOpMode
        implements SensorEventListener {

    private SensorManager sensorManager;

    // Phone rotation readings in degrees
    private volatile float yaw = 0;
    private volatile float pitch = 0;
    private volatile float roll = 0;
    private volatile boolean hasReading = false;

    @Override
    public void runOpMode() {

        // Access Android's built-in sensors
        sensorManager = (SensorManager)
                hardwareMap.appContext.getSystemService(
                        Context.SENSOR_SERVICE);

        if (sensorManager == null) {
            telemetry.addLine("ERROR: SensorManager unavailable");
            telemetry.update();
            return;
        }

        // Get phone's game rotation sensor
        Sensor rotationSensor = sensorManager.getDefaultSensor(
                Sensor.TYPE_GAME_ROTATION_VECTOR);

        if (rotationSensor == null) {
            telemetry.addLine("ERROR: Phone IMU unavailable!");
            telemetry.update();
            return;
        }

        // Start receiving sensor data
        boolean registered = sensorManager.registerListener(
                this,
                rotationSensor,
                SensorManager.SENSOR_DELAY_GAME);

        if (!registered) {
            telemetry.addLine("ERROR: Cannot start IMU sensor");
            telemetry.update();
            return;
        }

        try {
            telemetry.addLine("Phone IMU initialized!");
            telemetry.addLine("Press START to begin");
            telemetry.update();

            waitForStart();

            // Main loop
            while (opModeIsActive()) {

                if (hasReading) {
                    telemetry.addData("Yaw / Heading", "%.1f deg", yaw);
                    telemetry.addData("Pitch", "%.1f deg", pitch);
                    telemetry.addData("Roll", "%.1f deg", roll);
                } else {
                    telemetry.addLine("Waiting for IMU data...");
                }

                telemetry.update();
                sleep(20);
            }

        } finally {
            // Stop receiving data when OpMode ends
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float[] rotationMatrix = new float[9];
        float[] orientation = new float[3];

        // Convert rotation vector into matrix
        SensorManager.getRotationMatrixFromVector(
                rotationMatrix,
                event.values);

        // Get orientation angles
        SensorManager.getOrientation(
                rotationMatrix,
                orientation);

        // Convert radians to degrees
        yaw = (float) Math.toDegrees(orientation[0]);
        pitch = (float) Math.toDegrees(orientation[1]);
        roll = (float) Math.toDegrees(orientation[2]);

        hasReading = true;
    }

    @Override
    public void onAccuracyChanged(
            Sensor sensor, int accuracy) {
        // Nothing needed here
    }
}
