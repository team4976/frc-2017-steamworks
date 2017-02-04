package ca._4976.steamworks.subsystems.motionprofiler;

import ca._4976.steamworks.Robot;
import com.sun.org.apache.xpath.internal.SourceTree;
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

            module.inputs.driveLeft.reset();
            module.inputs.driveRight.reset();

            while (module.isEnabled()) {

                if (System.nanoTime() - lastTick > tickTime) {

                    lastTick = System.nanoTime();

                    final TimeStamp stamp = new TimeStamp();

                    stamp.leftDriveOutput = module.outputs.driveLeftFront.get();
                    stamp.rightDriveOutput = module.outputs.driveRightFront.get();

                    stamp.leftEncoderPos = module.inputs.driveLeft.getDistance();
                    stamp.rightEncoderPos = module.inputs.driveRight.getDistance();

                    stamp.leftEncoderVelocity = module.inputs.driveLeft.getRate();
                    stamp.rightEncoderVelocity = module.inputs.driveRight.getRate();

                    timeStamps.add(stamp);
                }
            }

            System.out.print("Pushing to Network....              ");

            double[] leftSetPoints = new double[timeStamps.size()];
            for (int i = 0; i < leftSetPoints.length; i++) leftSetPoints[i] = timeStamps.get(i).leftEncoderPos;

            double[] rightSetPoints = new double[timeStamps.size()];
            for (int i = 0; i < rightSetPoints.length; i++) rightSetPoints[i] = timeStamps.get(i).rightEncoderPos;

            table.putNumberArray("leftSetPoints", leftSetPoints);
            table.putNumberArray("rightSetPoints", rightSetPoints);

            System.out.println("Complete!");
        }
    }

    private class Run implements Runnable {

        private Robot module;

        private Run(Robot module) { this.module = module; }

        double kP = table.getNumber("kP", 0);
        double kD = table.getNumber("kD", 0);

        @Override public void run() {

            System.out.println("Run Time: " + timeStamps.size() / 200.0 + "s");

            double actualLeftPos;
            double actualRightPos;

            int i = 0;
            long tickTime = 1000000000 / 200;
            long lastTick = System.nanoTime() - tickTime;

            double lError;
            double rError;

            double lLastError = 0;
            double rLastError = 0;

            module.inputs.driveLeft.reset();
            module.inputs.driveRight.reset();

            int ticks = 0;
            long tickLength = 0;

            while (i < timeStamps.size() && module.isEnabled()) {

                if (System.nanoTime() - lastTick > tickTime) {

                    lastTick = System.nanoTime();

                    long time = System.nanoTime();

                    final TimeStamp stamp = timeStamps.get(i);

                    if (ticks > 199) {

                        System.out.println("Tick Runtime:" + tickLength / (double) ticks);
                        ticks = 0;
                        tickLength = 0;
                    }

                    actualLeftPos = module.inputs.driveLeft.getDistance();
                    actualRightPos = module.inputs.driveRight.getDistance();

                    lError = -(actualLeftPos - stamp.leftEncoderPos);
                    rError = -(actualRightPos - stamp.rightEncoderPos);

                    table.putNumber("leftA", actualLeftPos);
                    table.putNumber("leftS", lError);

                    table.putNumber("rightA", actualRightPos);
                    table.putNumber("rightS", rError);


                    double lDerivative = ((lError - lLastError) / tickTime) - stamp.leftEncoderVelocity;
                    double rDerivative = ((rError - rLastError) / tickTime) - stamp.rightEncoderVelocity;

                    System.out.println(actualLeftPos + " " + actualRightPos);

                    outputLeft(stamp.leftDriveOutput + (kP * lError) + (kD * lDerivative));
                    outputRight(stamp.rightDriveOutput + (kP * rError) + (kD * rDerivative));

                    lLastError = lError;
                    rLastError = rError;

                    i++;
                    ticks++;
                    tickLength += System.nanoTime() - time;
                }
            }

            outputLeft(0);
            outputRight(0);

            System.out.print("Pushing to Network....              ");

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
