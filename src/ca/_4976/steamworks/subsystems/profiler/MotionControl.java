package ca._4976.steamworks.subsystems.profiler;

import ca._4976.library.controllers.components.Boolean;
import ca._4976.library.controllers.components.Double;
import ca._4976.library.listeners.RobotStateListener;
import ca._4976.steamworks.Robot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class MotionControl {

    public MotionControl(Robot robot) {

        Playback playback = new Playback(robot);

        Record record = new Record(robot);

        SaveFile saveFile = new SaveFile();

        NetworkTable table = NetworkTable.getTable("Motion Control");

        record.changeControllerRecordPresets(new Boolean[] {
                robot.driver.A,
                robot.driver.B,
                robot.driver.X,
                robot.driver.Y,
                robot.driver.RB,
                robot.driver.LB,
                robot.driver.BACK,
                robot.driver.START,
                robot.driver.LS,
                robot.driver.RS,
                robot.operator.A,
                robot.operator.B,
                robot.operator.X,
                robot.operator.Y,
                robot.operator.RB,
                robot.operator.LB,
                robot.operator.BACK,
                robot.operator.START,
                robot.operator.LS,
                robot.operator.RS
        });

        record.changeControllerRecordPresets(new Double[] {
                robot.driver.LV,
                robot.driver.RH,
                robot.driver.RV,
                robot.operator.LH,
                robot.operator.LV,
                robot.operator.RH,
                robot.operator.RV,
                robot.operator.LT,
                robot.operator.RT,
        });

        robot.addListener(new RobotStateListener() {

            @Override public void autonomousInit() {

                if (!table.getString("load_table", "").equals(""))
                    playback.setProfile(saveFile.load(table.getString("load_table", "")));

                else playback.setProfile(record.getProfile());

                robot.inputs.driveLeft.reset();
                robot.inputs.driveRight.reset();

                synchronized (this) { new Thread(playback).run(); }
            }

            @Override public void testInit() {

                robot.inputs.driveLeft.reset();
                robot.inputs.driveRight.reset();

                robot.enableOperatorControl();

                synchronized (this) { new Thread(record).start(); }
            }
        });
    }
}
