package ca._4976.steamworks.subsystems.profiler;

import ca._4976.library.controllers.components.Boolean;
import ca._4976.library.controllers.components.Double;
import ca._4976.library.listeners.ButtonListener;
import ca._4976.steamworks.Robot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

class Playback implements Runnable {

    private final Robot robot;

    private Moment[] moments = new Moment[0];

    Playback(Robot robot) { this.robot = robot; }

    @Override public void run() {

        long lastTickTime = System.nanoTime();
        double avgTickRate = 0;
        int tickCount = 0;

        double leftIntegral = 0;
        double rightIntegral = 0;

        double lastLeftError = 0;
        double lastRightError = 0;

        Config config = Config.getInstance();

        while (robot.isEnabled() && tickCount < moments.length) {

            if (System.nanoTime() - lastTickTime >= config.tickTime) {

                lastTickTime = System.nanoTime();

                Moment moment = moments[tickCount];

                if (moment.evaluables != null) {

                    new Thread(() -> {

                        for (int i = 0; i < moment.states.length; i++) {

                            if (moment.states[i] instanceof Double.EVAL_STATE) {

                                Double.EVAL_STATE state = (Double.EVAL_STATE) moment.states[i];

                                //if (state == Double.EVAL_STATE.CHANGED)
                                //for (Object o : moment.evaluables[i]) { ((DoubleListener) o).changed();  }

                            } else {

                                Boolean.EVAL_STATE state = (Boolean.EVAL_STATE) moment.states[i];

                                if (state == Boolean.EVAL_STATE.PRESSED)
                                    for (Object o : moment.evaluables[i]) ((ButtonListener) o).pressed();
                            }
                        }

                    }).start();
                }

                double actualLeftPosition = robot.inputs.driveLeft.getDistance();
                double actualRightPosition = robot.inputs.driveRight.getDistance();

                double leftError = actualLeftPosition - moment.leftEncoderPosition;
                double rightError = actualRightPosition - moment.rightEncoderPosition;

                leftIntegral += leftError * config.tickTime;
                rightIntegral += rightError * config.tickTime;

                double leftDerivative = (leftError - lastLeftError) / config.tickTime - moment.leftEncoderVelocity;
                double rightDerivative = (rightError - lastRightError) / config.tickTime - moment.rightEncoderVelocity;

                System.out.println(moment.leftDriveOutput + " " + leftError);

                double leftDrive =
                        moment.leftDriveOutput
                                + (config.kP * leftError)
                        ;

                double rightDrive =
                        moment.rightDriveOutput
                                + (config.kP * rightError)
                          ;

                robot.outputs.driveLeftFront.set(leftDrive);
                robot.outputs.driveLeftRear.set(leftDrive);

                robot.outputs.driveRightFront.set(rightDrive);
                robot.outputs.driveRightRear.set(rightDrive);
                
                lastLeftError = leftError;
                lastRightError = rightError;

                NetworkTable.getTable("motion").putNumber("leftTarget", moment.leftEncoderPosition);
                NetworkTable.getTable("motion").putNumber("rightTarget", moment.rightEncoderPosition);
                NetworkTable.getTable("motion").putNumber("leftError", leftError);
                NetworkTable.getTable("motion").putNumber("rightError", rightError);

                tickCount++;
                avgTickRate += System.nanoTime() - lastTickTime;
            }
        }


        avgTickRate /= tickCount;
        System.out.printf("<Motion Control> Average tick time: %.3f", avgTickRate);
        System.out.printf(" %.1f%%%n", config.tickTime / avgTickRate);
    }

    void setProfile(Moment[] moments) { this.moments = moments; }
}
