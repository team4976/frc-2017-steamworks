package ca._4976.library;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.hal.FRCNetComm;
import edu.wpi.first.wpilibj.hal.HAL;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

import java.util.ArrayList;

public class AsynchronousRobot extends RobotBase {

    private ArrayList<Evaluable> constantEvaluables = new ArrayList<>();
    private ArrayList<Evaluable> evaluables = new ArrayList<>();
    private ArrayList<Long> evalTimes = new ArrayList<>();

    private boolean disabledInizalized = false;
    private boolean autonomousInizalized = false;
    private boolean teleopInizalized = false;
    private boolean testInizalized = false;
    private boolean enableOperatorControl = false;

    protected void enableOperatorControl() { enableOperatorControl = true; }

    @Override public boolean isOperatorControl() { return enableOperatorControl || super.isOperatorControl(); }

    @Override public void startCompetition() {

        robotInit();

        HAL.report(FRCNetComm.tResourceType.kResourceType_Framework, FRCNetComm.tInstances.kFramework_Iterative);
        HAL.observeUserProgramStarting();

        LiveWindow.setEnabled(false);

        while (constantEvaluables.size() > 0) {

            m_ds.waitForData();

            if (isDisabled()) {

                if (!disabledInizalized) {

                    LiveWindow.setEnabled(false);

                    disabledInizalized = true;
                    autonomousInizalized = false;
                    teleopInizalized = false;
                    testInizalized = false;
                    enableOperatorControl = false;

                    disabledInit();
                }

                HAL.observeUserProgramDisabled();
                checkEvaluables();

            } else if (isAutonomous()) {

                if (!autonomousInizalized) {

                    LiveWindow.setEnabled(false);

                    disabledInizalized = false;
                    autonomousInizalized = true;
                    teleopInizalized = false;
                    testInizalized = false;

                    autonomousInit();
                }

                HAL.observeUserProgramAutonomous();
                checkEvaluables();

            } else if (isOperatorControl()) {

                if (!teleopInizalized) {

                    LiveWindow.setEnabled(false);

                    disabledInizalized = false;
                    autonomousInizalized = false;
                    teleopInizalized = true;
                    testInizalized = false;

                    teleopInit();
                }

                HAL.observeUserProgramTeleop();
                checkEvaluables();

            } else if (isTest()) {

                if (!testInizalized) {

                    LiveWindow.setEnabled(true);

                    disabledInizalized = false;
                    autonomousInizalized = false;
                    teleopInizalized = false;
                    testInizalized = true;

                    testInit();
                }

                HAL.observeUserProgramTest();
                checkEvaluables();
            }
        }
    }

    public void runNextLoop(Evaluable evaluable, int delay) {

        if (delay == -1) constantEvaluables.add(evaluable);

        else {

            evaluables.add(evaluable);
            evalTimes.add(System.currentTimeMillis() + delay);
        }
    }

    public void runNextLoop(Evaluable evaluable) { runNextLoop(evaluable, 0); }

    private void checkEvaluables() {

        constantEvaluables.forEach(Evaluable::eval);

        if (evalTimes.size() == evaluables.size()) {

            for (int i = evaluables.size() - 1; i >= 0; i--) {

                if (evalTimes.get(i) <= System.currentTimeMillis()) {

                    evaluables.get(i).eval();
                    evaluables.remove(i);
                    evalTimes.remove(i);
                }
            }

        } else { System.out.println("what"); }
    }

    protected void robotInit() { }
    protected void disabledInit() { }
    protected void autonomousInit() { }
    protected void teleopInit() { }
    protected void testInit() { }
}
