package ca._4976.library;

import ca._4976.library.listeners.RobotStateListener;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.hal.FRCNetComm;
import edu.wpi.first.wpilibj.hal.HAL;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

import java.util.ArrayList;

public class AsynchronousRobot extends RobotBase {

    private ArrayList<RobotStateListener> listeners = new ArrayList<>();
    private ArrayList<Evaluable> constantEvaluables = new ArrayList<>();
    private ArrayList<Evaluable> evaluables = new ArrayList<>();
    private ArrayList<Long> evalTimes = new ArrayList<>();

    private boolean disabledInitialized = false;
    private boolean autonomousInitialized = false;
    private boolean telexInitialized = false;
    private boolean testInitialized = false;
    private boolean enableOperatorControl = false;

    public void enableOperatorControl() { enableOperatorControl = true; }

    @Override public boolean isOperatorControl() { return enableOperatorControl || super.isOperatorControl(); }

    @Override public void startCompetition() {

        listeners.forEach(RobotStateListener::robotInit);

        HAL.report(FRCNetComm.tResourceType.kResourceType_Framework, FRCNetComm.tInstances.kFramework_Iterative);
        HAL.observeUserProgramStarting();

        LiveWindow.setEnabled(false);

        while (constantEvaluables.size() > 0) {

            m_ds.waitForData();

            if (isDisabled()) {

                if (!disabledInitialized) {

                    LiveWindow.setEnabled(false);

                    disabledInitialized = true;
                    autonomousInitialized = false;
                    telexInitialized = false;
                    testInitialized = false;
                    enableOperatorControl = false;

                    listeners.forEach(RobotStateListener::disabledInit);
                }

                HAL.observeUserProgramDisabled();
                checkEvaluables();

            } else if (isAutonomous()) {

                if (!autonomousInitialized) {

                    LiveWindow.setEnabled(false);

                    disabledInitialized = false;
                    autonomousInitialized = true;
                    telexInitialized = false;
                    testInitialized = false;

                    listeners.forEach(RobotStateListener::autonomousInit);
                }

                HAL.observeUserProgramAutonomous();
                checkEvaluables();

            } else if (isOperatorControl()) {

                if (!telexInitialized) {

                    LiveWindow.setEnabled(false);

                    disabledInitialized = false;
                    autonomousInitialized = false;
                    telexInitialized = true;
                    testInitialized = false;

                    listeners.forEach(RobotStateListener::teleopInit);
                }

                HAL.observeUserProgramTeleop();
                checkEvaluables();

            } else if (isTest()) {

                if (!testInitialized) {

                    LiveWindow.setEnabled(true);

                    disabledInitialized = false;
                    autonomousInitialized = false;
                    telexInitialized = false;
                    testInitialized = true;

                    listeners.forEach(RobotStateListener::testInit);
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

    public void addListener(RobotStateListener listener) { listeners.add(listener); }

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

        } else { throw new RuntimeException("Evaluables out of sync."); }
    }
}
