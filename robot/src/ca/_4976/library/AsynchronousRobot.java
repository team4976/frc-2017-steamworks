package ca._4976.library;

import ca._4976.library.listeners.RobotStateListener;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.hal.FRCNetComm;
import edu.wpi.first.wpilibj.hal.HAL;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

import java.util.ArrayList;

public class AsynchronousRobot extends RobotBase {

    private ArrayList<RobotStateListener> listeners = new ArrayList<>();

    private final ArrayList<Evaluator> evaluators = new ArrayList<>();

    private boolean disabledInitialized = false;
    private boolean autonomousInitialized = false;
    private boolean teleopInitialized = false;
    private boolean testInitialized = false;
    private boolean enableOperatorControl = false;

    public void enableOperatorControl() { enableOperatorControl = true; }

    @Override public boolean isOperatorControl() { return enableOperatorControl || super.isOperatorControl(); }

    @Override public void startCompetition() {

        listeners.forEach(RobotStateListener::robotInit);

        Evaluable[] userInput = new Evaluable[Initialization.USER_INPUT_EVALS.size()];
        for (int i = 0; i < userInput.length; i++) userInput[i] = Initialization.USER_INPUT_EVALS.get(i);

        Evaluable[] hardwareInput = new Evaluable[Initialization.HARDWARE_INPUT_EVALS.size()];
        for (int i = 0; i < hardwareInput.length; i++) hardwareInput[i] = Initialization.HARDWARE_INPUT_EVALS.get(i);

        HAL.report(FRCNetComm.tResourceType.kResourceType_Framework, FRCNetComm.tInstances.kFramework_Iterative);
        HAL.observeUserProgramStarting();

        LiveWindow.setEnabled(false);

        while (!Thread.interrupted() && !m_ds.isFMSAttached()) { robotUpdate(userInput, hardwareInput); }

        while (!Thread.interrupted() && m_ds.isFMSAttached()) {

            try {

                robotUpdate(userInput, hardwareInput);

            } catch (Exception e) {

                listeners.forEach(RobotStateListener::disabledInit);
                DriverStation.reportError(e.getMessage(), true);
            }
        }
    }

    private void robotUpdate(Evaluable[] userInput, Evaluable[] hardwareInput) {

        m_ds.waitForData();

        if (isDisabled()) {

            if (!disabledInitialized) {

                LiveWindow.setEnabled(false);

                evaluators.clear();

                disabledInitialized = true;
                autonomousInitialized = false;
                teleopInitialized = false;
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
                teleopInitialized = false;
                testInitialized = false;

                listeners.forEach(RobotStateListener::autonomousInit);

                System.out.println("<Robot> Initialized Autonomous");
            }

            HAL.observeUserProgramAutonomous();
            for (Evaluable evaluable : hardwareInput) evaluable.eval();
            checkEvaluables();

        } else if (isOperatorControl()) {

            if (!teleopInitialized) {

                LiveWindow.setEnabled(false);

                disabledInitialized = false;
                autonomousInitialized = false;
                teleopInitialized = true;
                testInitialized = false;

                listeners.forEach(RobotStateListener::teleopInit);
            }

            HAL.observeUserProgramTeleop();
            for (Evaluable evaluable : hardwareInput) evaluable.eval();
            for (Evaluable evaluable : userInput) evaluable.eval();
            checkEvaluables();

        } else if (isTest()) {

            if (!testInitialized) {

                LiveWindow.setEnabled(true);

                disabledInitialized = false;
                autonomousInitialized = false;
                teleopInitialized = false;
                testInitialized = true;

                listeners.forEach(RobotStateListener::testInit);
            }

            HAL.observeUserProgramTest();

            if (enableOperatorControl) for (Evaluable evaluable : hardwareInput) evaluable.eval();
            for (Evaluable evaluable : hardwareInput) evaluable.eval();
            checkEvaluables();
        }
    }

    public void runNextLoop(Evaluable evaluable, int delay) {

        evaluators.add(new Evaluator(evaluable, System.currentTimeMillis() + delay));
    }

    public void runNextLoop(Evaluable evaluable) { runNextLoop(evaluable, 0); }

    public void addListener(RobotStateListener listener) { listeners.add(listener); }

    private void checkEvaluables() {

        for (int i = evaluators.size() - 1; i >= 0; i--) {

            Evaluator evaluator = evaluators.get(i);

            if (evaluator.delay <= System.currentTimeMillis()) {

                evaluators.remove(i);
                evaluator.evaluable.eval();
            }
        }
    }
}
