package ca._4976.steamworks.subsystems.motionprofiler;

import ca._4976.steamworks.Robot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

import java.util.ArrayList;

public class MotionProfile {

    private Robot module;

    private ArrayList<TimeStamp> timeStamps = new ArrayList<>();

    private NetworkTable table = NetworkTable.getTable("motion_control");

    public MotionProfile(Robot module) { this.module = module; }

    public synchronized void record() {

        new Thread(new Record(module)).start(); }

    public synchronized void run() { new Thread(new Run(module)).start(); }

    private class Record implements Runnable {

        private Robot module;

        private Record(Robot module) { this.module = module; }

        @Override public void run() {

            System.out.println("Recording Motion Profile");

            timeStamps = new ArrayList<>();

            long tickTime = 1000000000 / 200;
            long lastTick = System.nanoTime() - tickTime;

            while (module.isEnabled()) {

                if (System.nanoTime() - lastTick > tickTime) {

                    lastTick = System.nanoTime();

                    final TimeStamp stamp = new TimeStamp();

                    stamp.leftDriveOutput = module.outputs.driveLeftFront.get();
                    stamp.rightDriveOutput = module.outputs.driveRightFront.get();

                    stamp.leftEncoderSpeed = module.inputs.driveLeft.getRate();
                    stamp.leftEncoderSpeed = module.inputs.driveRight.getRate();

                    timeStamps.add(stamp);
                }
            }

            System.out.print("Pushing to Network....              ");

            double[] leftSetPoints = new double[timeStamps.size()];
            for (int i = 0; i < leftSetPoints.length; i++) leftSetPoints[i] = timeStamps.get(i).leftEncoderSpeed;

            double[] rightSetPoints = new double[timeStamps.size()];
            for (int i = 0; i < rightSetPoints.length; i++) rightSetPoints[i] = timeStamps.get(i).rightEncoderSpeed;

            table.putNumberArray("leftSetPoints", leftSetPoints);
            table.putNumberArray("rightSetPoints", rightSetPoints);

            System.out.println("Complete!");
        }
    }

    private class Run implements Runnable {

        private Robot module;

        private Run(Robot module) { this.module = module; }

        double kP = table.getNumber("kP", 0);
        double kI = table.getNumber("kI", 0);
        double kD = table.getNumber("kD", 0);

        @Override public void run() {

            System.out.println("Run Time: " + timeStamps.size() / 200.0 + "s");

            double[] actualLeftSpeed = new double[timeStamps.size()];
            double[] actualRightSpeed = new double[timeStamps.size()];

            int i = 0;
            long tickTime = 1000000000 / 200;
            long lastTick = System.nanoTime() - tickTime;

            double lError;
            double rError;

            double lIntegral  = 0;
            double rIntegral = 0;

            double lLastError = 0;
            double rLastError = 0;

            while (i < timeStamps.size() && module.isEnabled()) {

                if (System.nanoTime() - lastTick > tickTime) {

                    lastTick = System.nanoTime();

                    final TimeStamp stamp = timeStamps.get(i);

                    actualLeftSpeed[i] = module.inputs.driveLeft.getRate();
                    actualRightSpeed[i] = module.inputs.driveRight.getRate();

                    lError = actualLeftSpeed[i] - module.inputs.driveLeft.getRate();
                    rError = actualRightSpeed[i] - module.inputs.driveLeft.getRate();

                    lIntegral += lError * tickTime;
                    rIntegral += rError * tickTime;

                    double lDerivative = (lError - lLastError) / tickTime ;
                    double rDerivative = (rError - rLastError) / tickTime;

                    outputLeft(stamp.leftDriveOutput + kP * lError + kI + lIntegral + kD * lDerivative);
                    outputRight(stamp.rightDriveOutput + kP * rError + kI + rIntegral + kD * rDerivative);

                    lLastError = lError;
                    rLastError = lError;

                    i++;
                }
            }

            outputLeft(0);
            outputRight(0);

            System.out.print("Pushing to Network....              ");

            table.putNumberArray("actualLeftSpeed", actualLeftSpeed);
            table.putNumberArray("actualRightSpeed", actualRightSpeed);

            System.out.println("Complete!");
        }

        private void outputLeft(double value) {

            module.outputs.driveLeftFront.pidWrite(value);
            module.outputs.driveLeftRear.pidWrite(value);
        }

        private void outputRight(double value) {

            module.outputs.driveRightFront.pidWrite(value);
            module.outputs.driveRightRear.pidWrite(value);
        }
    }
}
