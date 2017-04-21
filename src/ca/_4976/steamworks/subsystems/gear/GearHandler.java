package ca._4976.steamworks.subsystems.gear;

import ca._4976.library.Evaluable;
import ca._4976.library.listeners.ButtonListener;
import ca._4976.library.listeners.RobotStateListener;
import ca._4976.steamworks.Robot;
import ca._4976.steamworks.subsystems.profiler.Playback;
import ca._4976.steamworks.subsystems.profiler.Record;

public class GearHandler {

    private Config config = new Config();
    private int state = 0;
    private Record record;
    private Playback playback;
    private Robot robot;

    public GearHandler(Robot robot){

        this.robot = robot;

        record = new Record(robot);
        playback = new Playback(robot);

        robot.addListener(new RobotStateListener() {

            @Override public void disabledInit() {

                state = 0;
                robot.outputs.roller.set(0);
            }
        });

        Evaluable currentControl = new Evaluable() {

            @Override public void eval() {

                if (robot.outputs.roller.getOutputCurrent() > config.currentLimit) {

                    state = 2;

                    robot.runNextLoop(() -> {

                        robot.outputs.roller.set(-config.gripSpeed);
                        robot.outputs.gear.output(true);
                        System.out.println("<Gear Handler> Gear roller over current perhaps we have a gear.");

                        robot.driver.setRumble(1);
                        robot.runNextLoop(() -> robot.driver.setRumble(0), 200);

                    }, config.gripDelay);
                }

                if (state == 1) robot.runNextLoop(this);
            }
        };

        robot.driver.A.addListener(new ButtonListener() {

            @Override public void pressed() {

                if (state != 1) {

                    state = 1;

                    robot.outputs.roller.set(-config.intakeSpeed);
                    robot.outputs.gear.output(false);
                    robot.runNextLoop(currentControl, 5);

                    System.out.println("<Gear Handler> Attempting to intake a gear.");
                }
            }
        });

        robot.driver.B.addListener(new ButtonListener() {

            @Override public void pressed() {

                state = 3;

                robot.outputs.roller.set(config.releaseSpeed);
                robot.outputs.gear.output(false);

                robot.runNextLoop(() -> robot.outputs.roller.set(0), config.releaseTime);

                robot.runNextLoop(() -> { {

                    robot.outputs.gear.output(true);
                    state = 0;

                }}, config.raiseDelay);

                System.out.println("<Gear Handler> Releasing gear.");
            }
        });

        robot.driver.X.addListener(new ButtonListener() {

            @Override public void pressed() {

                state = 0;

                robot.outputs.roller.set(0);
                robot.outputs.gear.output(true);

                System.out.println("<Gear Handler> Resetting.");
            }
        });

        robot.operator.LB.addListener(new ButtonListener() {

            @Override public void pressed() {

                if (robot.isTest() || robot.isAutonomous() || robot.isEnabled()) {

                    state = 1;

                    robot.outputs.roller.set(-config.intakeSpeed);
                    robot.outputs.gear.output(false);
                    robot.runNextLoop(currentControl, 5);

                    System.out.println("<Gear Handler> Attempting to intake a gear.");

                   // robot.profiler.record.setPaused(true);
                   // record.start();
                    robot.vision.gear.start();
                }
            }
        });
    }

    private void play() {

        record.stop();
        try { Thread.sleep(2); } catch (InterruptedException e) { e.printStackTrace(); }
        playback.setProfile(record.getProfile().getReversed());
        playback.setListener(robot.profiler.getLog());
        playback.start();

        robot.runNextLoop(new Evaluable() {

            @Override public void eval() {

                if (playback.isRunning()) robot.runNextLoop(this);

                else robot.profiler.playback.setPaused(false);
            }
        });
    }
}