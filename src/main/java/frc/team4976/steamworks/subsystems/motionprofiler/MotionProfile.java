package frc.team4976.steamworks.subsystems.motionprofiler;

import frc.team4976.steamworks.RobotModule;
import jaci.openrio.toast.core.Toast;

import java.util.ArrayList;

public class MotionProfile {

    private RobotModule module;

    private ArrayList<TimeStamp> timeStamps = new ArrayList<>();

    public MotionProfile(RobotModule module) { this.module = module; }

    public synchronized void record() {

        new Thread(new Record(module)).start(); }

    public synchronized void run() { new Thread(new Run(module)).start(); }

    public class Record implements Runnable {

        private RobotModule module;
        private Toast toast = Toast.getToast();

        private Record(RobotModule module) { this.module = module; }

        @Override public void run() {

            timeStamps.clear();

            long tickTime = 1000000000 / 200;
            long lastTick = System.nanoTime() - tickTime;

            while (toast.isEnabled()) {

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

        private RobotModule module;

        public Run(RobotModule module) { this.module = module; }

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
