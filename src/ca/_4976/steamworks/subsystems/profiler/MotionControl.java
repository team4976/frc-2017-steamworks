package ca._4976.steamworks.subsystems.profiler;

import ca._4976.library.controllers.components.Boolean;
import ca._4976.library.controllers.components.Double;
import ca._4976.library.listeners.ButtonListener;
import ca._4976.library.listeners.RobotStateListener;
import ca._4976.steamworks.Robot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class MotionControl {

    public Playback playback;
    private SaveFile saveFile = new SaveFile();

    private NetworkTable table = NetworkTable.getTable("Motion Control");

    public MotionControl(Robot robot) {

        playback = new Playback(robot);

        Recorder record = new Recorder(robot);

        Boolean disable = new Boolean(0) { @Override public boolean get() { return false; }};

        disable.addListener(new ButtonListener() {

            @Override public void pressed() { playback.disable(); }
        });

        Boolean[] buttons = new Boolean[] {
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
                robot.operator.RS,
                disable,
                robot.operator.UP,
                robot.operator.UP_LEFT,
                robot.operator.UP_RIGHT,
                robot.operator.LEFT,
                robot.operator.RIGHT,
                robot.operator.DOWN,
                robot.operator.DOWN_LEFT,
                robot.operator.DOWN_RIGHT
        };

        record.changeControllerRecordPresets(buttons);
        saveFile.changeControllerRecordPresets(buttons);

        Double[] axes = new Double[] {
                robot.driver.LV,
                robot.driver.RH,
                robot.driver.RV,
                robot.operator.LH,
                robot.operator.LV,
                robot.operator.RH,
                robot.operator.RV,
                robot.operator.LT,
                robot.operator.RT,
        };

        record.changeControllerRecordPresets(axes);
        saveFile.changeControllerRecordPresets(axes);

        table.putString("load_table", "");

        robot.driver.BACK.addListener(new ButtonListener() {

            @Override public void pressed() {

                robot.inputs.driveLeft.reset();
                robot.inputs.driveRight.reset();

                synchronized (this) { new Thread(record).start(); }
            }
        });

        robot.addListener(new RobotStateListener() {

            @Override public void robotInit() { table.putStringArray("table", new SaveFile().getFileNames()); }

            @Override public void disabledInit() { table.putStringArray("table", new SaveFile().getFileNames()); }

            @Override public void autonomousInit() {

                if (table.getString("load_table", "").equals("")) playback.setProfile(record.getProfile());

                robot.inputs.driveLeft.reset();
                robot.inputs.driveRight.reset();

                synchronized (this) { new Thread(playback).start(); }
            }

            @Override public void testInit() {

                robot.inputs.driveLeft.reset();
                robot.inputs.driveRight.reset();

                robot.enableOperatorControl();

                synchronized (this) { new Thread(record).start(); }
            }
        });
    }

    public void loadTable() {

        String load = table.getString("load_table", "");

        if (!load.equals("")) {

            System.out.println("<Motion Control> Getting autonomous: " + load);
            playback.setProfile(saveFile.load(load));
        }

        else System.out.println("<Motion Control> Successfully set autonomous to last record.");
    }
}
