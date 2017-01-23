/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008-2017. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package ca._4976.library;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tInstances;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tResourceType;
import edu.wpi.first.wpilibj.hal.HAL;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

import java.util.ArrayList;

/**
 * IterativeRobot implements a specific type of Robot Program framework, extending the RobotBase
 * class.
 *
 * <p>The IterativeRobot class is intended to be subclassed by a user creating a robot program.
 *
 * <p>This class is intended to implement the "old style" default code, by providing the following
 * functions which are called by the main loop, startCompetition(), at the appropriate times:
 *
 * <p>robotInit() -- provide for initialization at robot power-on
 *
 * <p>init() functions -- each of the following functions is called once when the appropriate mode
 * is entered: - DisabledInit() -- called only when first disabled - AutonomousInit() -- called each
 * and every time autonomous is entered from another mode - TeleopInit() -- called each and every
 * time teleop is entered from another mode - TestInit() -- called each and every time test mode is
 * entered from anothermode
 *
 * <p>Periodic() functions -- each of these functions is called iteratively at the appropriate
 * periodic rate (aka the "slow loop"). The period of the iterative robot is synced to the driver
 * station control packets, giving a periodic frequency of about 50Hz (50 times per second). -
 * disabledPeriodic() - autonomousPeriodic() - teleopPeriodic() - testPeriodoc()
 */
public class IterativeRobot extends RobotBase {

    private boolean m_disabledInitialized;
    private boolean m_autonomousInitialized;
    private boolean m_teleopInitialized;
    private boolean m_testInitialized;
    private boolean m_operatorControl;

    private ArrayList<Evaluable> evaluables = new ArrayList<>();
    private ArrayList<Long> evalTimes = new ArrayList<>();

    /**
     * Constructor for RobotIterativeBase.
     *
     * <p>The constructor initializes the instance variables for the robot to indicate the status of
     * initialization for disabled, autonomous, and teleop code.
     */
    public IterativeRobot() {
        // set status for initialization of disabled, autonomous, and teleop code.
        m_disabledInitialized = false;
        m_autonomousInitialized = false;
        m_teleopInitialized = false;
        m_testInitialized = false;
        m_operatorControl = false;
    }

    /**
     * Provide an alternate "main loop" via startCompetition().
     */
    public void startCompetition() {
        HAL.report(tResourceType.kResourceType_Framework,
                tInstances.kFramework_Iterative);

        robotInit();

        // Tell the DS that the robot is ready to be enabled
        HAL.observeUserProgramStarting();

        // loop forever, calling the appropriate mode-dependent function
        LiveWindow.setEnabled(false);
        while (true) {
            // Wait for new data to arrive
            m_ds.waitForData();
            // Call the appropriate function depending upon the current robot mode
            if (isDisabled()) {
                // call DisabledInit() if we are now just entering disabled mode from
                // either a different mode or from power-on
                if (!m_disabledInitialized) {
                    LiveWindow.setEnabled(false);
                    disabledInit();
                    m_disabledInitialized = true;
                    m_operatorControl = false;
                    // reset the initialization flags for the other modes
                    m_autonomousInitialized = false;
                    m_teleopInitialized = false;
                    m_testInitialized = false;
                }
                HAL.observeUserProgramDisabled();
                disabledPeriodic();
            } else if (isTest()) {
                // call TestInit() if we are now just entering test mode from either
                // a different mode or from power-on
                if (!m_testInitialized) {
                    LiveWindow.setEnabled(true);
                    testInit();
                    m_testInitialized = true;
                    m_autonomousInitialized = false;
                    m_teleopInitialized = false;
                    m_disabledInitialized = false;
                }
                HAL.observeUserProgramTest();
                peridoic();
                testPeriodic();
                if (m_operatorControl) teleopPeriodic();
            } else if (isAutonomous()) {
                // call Autonomous_Init() if this is the first time
                // we've entered autonomous_mode
                if (!m_autonomousInitialized) {
                    LiveWindow.setEnabled(false);
                    // KBS NOTE: old code reset all PWMs and relays to "safe values"
                    // whenever entering autonomous mode, before calling
                    // "Autonomous_Init()"
                    autonomousInit();
                    m_autonomousInitialized = true;
                    m_testInitialized = false;
                    m_teleopInitialized = false;
                    m_disabledInitialized = false;
                }
                HAL.observeUserProgramAutonomous();
                peridoic();
                autonomousPeriodic();
            } else {
                // call Teleop_Init() if this is the first time
                // we've entered teleop_mode
                if (!m_teleopInitialized) {
                    LiveWindow.setEnabled(false);
                    teleopInit();
                    m_teleopInitialized = true;
                    m_testInitialized = false;
                    m_autonomousInitialized = false;
                    m_disabledInitialized = false;
                }
                HAL.observeUserProgramTeleop();
                peridoic();
                teleopPeriodic();
            }
            robotPeriodic();
        }
    }

    public void enableOperatorControl() { m_operatorControl = true; }

    @Override public boolean isOperatorControl() {

        return m_operatorControl || super.isOperatorControl();
    }

    public void runNextLoop(Evaluable evaluable) {

        evaluables.add(evaluable);
        evalTimes.add(0L);
    }

    public void runNextLoop(Evaluable evaluable, int delay) {

        evaluables.add(evaluable);
        evalTimes.add(System.currentTimeMillis() + delay);
    }

    private void peridoic() {

        if (evalTimes.size() == evaluables.size()) {

            for (int i = evaluables.size() - 1; i >= 0; i--) {

                if (System.currentTimeMillis() >= evalTimes.get(i)) {

                    evaluables.get(i).eval();
                    evaluables.remove(i);
                    evalTimes.remove(i);
                }
            }

        } else throw new RuntimeException("Something went wrong.");
    }

  /* ----------- Overridable initialization code ----------------- */

    /**
     * Robot-wide initialization code should go here.
     *
     * <p>Users should override this method for default Robot-wide initialization which will be called
     * when the robot is first powered on. It will be called exactly one time.
     *
     * <p>Warning: the Driver Station "Robot Code" light and FMS "Robot Ready" indicators will be off
     * until RobotInit() exits. Code in RobotInit() that waits for enable will cause the robot to
     * never indicate that the code is ready, causing the robot to be bypassed in a match.
     */
    protected void robotInit() { }

    /**
     * Initialization code for disabled mode should go here.
     *
     * <p>Users should override this method for initialization code which will be called each time the
     * robot enters disabled mode.
     */
    protected void disabledInit() { }

    /**
     * Initialization code for autonomous mode should go here.
     *
     * <p>Users should override this method for initialization code which will be called each time the
     * robot enters autonomous mode.
     */
    protected void autonomousInit() { }

    /**
     * Initialization code for teleop mode should go here.
     *
     * <p>Users should override this method for initialization code which will be called each time the
     * robot enters teleop mode.
     */
    protected void teleopInit() { }

    /**
     * Initialization code for test mode should go here.
     *
     * <p>Users should override this method for initialization code which will be called each time the
     * robot enters test mode.
     */
    @SuppressWarnings("PMD.JUnit4TestShouldUseTestAnnotation")
    protected void testInit() { }

  /* ----------- Overridable periodic code ----------------- */

    /**
     * Periodic code for all robot modes should go here.
     *
     * <p>This function is called each time a new packet is received from the driver station.
     *
     * <p>Packets are received approximately every 20ms.  Fixed loop timing is not guaranteed due to
     * network timing variability and the function may not be called at all if the Driver Station is
     * disconnected.  For most use cases the variable timing will not be an issue.  If your code does
     * require guaranteed fixed periodic timing, consider using Notifier or PIDController instead.
     */
    protected void robotPeriodic() { }


    /**
     * Periodic code for disabled mode should go here.
     *
     * <p>Users should override this method for code which will be called each time a new packet is
     * received from the driver station and the robot is in disabled mode.
     *
     * <p>Packets are received approximately every 20ms.  Fixed loop timing is not guaranteed due to
     * network timing variability and the function may not be called at all if the Driver Station is
     * disconnected.  For most use cases the variable timing will not be an issue.  If your code does
     * require guaranteed fixed periodic timing, consider using Notifier or PIDController instead.
     */
    protected void disabledPeriodic() { }


    /**
     * Periodic code for autonomous mode should go here.
     *
     * <p>Users should override this method for code which will be called each time a new packet is
     * received from the driver station and the robot is in autonomous mode.
     *
     * <p>Packets are received approximately every 20ms.  Fixed loop timing is not guaranteed due to
     * network timing variability and the function may not be called at all if the Driver Station is
     * disconnected.  For most use cases the variable timing will not be an issue.  If your code does
     * require guaranteed fixed periodic timing, consider using Notifier or PIDController instead.
     */
    protected void autonomousPeriodic() { }


    /**
     * Periodic code for teleop mode should go here.
     *
     * <p>Users should override this method for code which will be called each time a new packet is
     * received from the driver station and the robot is in teleop mode.
     *
     * <p>Packets are received approximately every 20ms.  Fixed loop timing is not guaranteed due to
     * network timing variability and the function may not be called at all if the Driver Station is
     * disconnected.  For most use cases the variable timing will not be an issue.  If your code does
     * require guaranteed fixed periodic timing, consider using Notifier or PIDController instead.
     */
    protected void teleopPeriodic() { }

    /**
     * Periodic code for test mode should go here.
     *
     * <p>Users should override this method for code which will be called each time a new packet is
     * received from the driver station and the robot is in test mode.
     *
     * <p>Packets are received approximately every 20ms.  Fixed loop timing is not guaranteed due to
     * network timing variability and the function may not be called at all if the Driver Station is
     * disconnected.  For most use cases the variable timing will not be an issue.  If your code does
     * require guaranteed fixed periodic timing, consider using Notifier or PIDController instead.
     */
    @SuppressWarnings("PMD.JUnit4TestShouldUseTestAnnotation")
    protected void testPeriodic() { }
}
