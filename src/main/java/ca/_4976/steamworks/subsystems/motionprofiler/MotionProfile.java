package ca._4976.steamworks.subsystems.motionprofiler;

import ca._4976.steamworks.Robot;

import java.util.ArrayList;

public class MotionProfile {

    private Robot module;

    private ArrayList<TimeStamp> timeStamps = new ArrayList<>();

    public MotionProfile(Robot module) { this.module = module; }

    public synchronized void record() {

        new Thread(new Record(module)).start(); }

    public synchronized void run() { new Thread(new Run(module)).start(); }

    public class Record implements Runnable {

        private Robot module;

        private Record(Robot module) { this.module = module; }

        @Override public void run() {

            timeStamps.clear();

            long tickTime = 1000000000 / 200;
            long lastTick = System.nanoTime() - tickTime;

            while (module.isEnabled()) {

                if (System.nanoTime() - lastTick > tickTime) {

                    lastTick = System.nanoTime();

                    final TimeStamp stamp = new TimeStamp();

                    stamp.leftDriveOutput = module.outputs.driveLeftFront.get();
                    stamp.rightDriveOutput = module.outputs.driveRightFront.get();

                    timeStamps.add(stamp);
                }
            }
        }
    }

    public class Run implements Runnable {

        private Robot module;

        public Run(Robot module) { this.module = module; }

        @Override public void run() {

            System.out.println("Run Time: " + timeStamps.size() / 200.0 + "s");

            int i = 0;
            long tickTime = 1000000000 / 200;
            long lastTick = System.nanoTime() - tickTime;

            while (i < timeStamps.size()) {

                if (System.nanoTime() - lastTick > tickTime) {

                    lastTick = System.nanoTime();

                    final TimeStamp stamp = timeStamps.get(i);

                    module.outputs.driveLeftFront.pidWrite(stamp.leftDriveOutput);
                    module.outputs.driveLeftRear.pidWrite(stamp.leftDriveOutput);

                    module.outputs.driveRightFront.pidWrite(stamp.rightDriveOutput);
                    module.outputs.driveRightRear.pidWrite(stamp.rightDriveOutput);

                    i++;
                }
            }
        }
    }
}
