package ca._4976.steamworks.subsystems;

import ca._4976.library.Evaluable;
import ca._4976.library.listeners.ButtonListener;
import ca._4976.library.listeners.DoubleListener;
import ca._4976.library.listeners.RobotStateListener;
import ca._4976.steamworks.Robot;
import com.sun.org.apache.xpath.internal.SourceTree;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Shooter {

    private NetworkTable table = NetworkTable.getTable("Shooter");

    private double targetRPM = table.getNumber("Setpoint (RPM)", 3100);
    private double targetError = table.getNumber("Target Error (RPM)", 100);

    boolean speed = false;
    boolean shooter_firing = false;
    double linearAc = 0;
    public int turret_result = 0, rumble = 0;
    double RPM = 500;
    double setSpeed = 3400;
    boolean pressed = false;

    public Shooter(Robot robot) {

        robot.addListener(new RobotStateListener() {

            @Override public void robotInit() {

                robot.outputs.shooterMaster.disableControl();

                robot.runNextLoop(() -> {

                    NetworkTable.getTable("Status").putNumber("Shooter Speed (RPM)", robot.outputs.shooterMaster.getSpeed());
                    NetworkTable.getTable("Status").putNumber("Shooter Error (RPM)", robot.outputs.shooterMaster.getError());

                }, -1);
            }

            @Override public void disabledInit() {
                robot.outputs.shooterMaster.set(0);
            }
        });

        robot.driver.LB.addListener(new ButtonListener() {

            @Override public void pressed() {

                if (!robot.outputs.shooterMaster.isControlEnabled()) {

                    robot.outputs.shooterMaster.enableControl();
                    robot.outputs.shooterMaster.set(targetRPM);

                } else {

                    robot.outputs.shooterMaster.set(0);
                    robot.outputs.shooterMaster.disableControl();
                }
            }
        });


        robot.driver.RB.addListener(new ButtonListener() { //TODO: Add targeting

            @Override public void pressed() {

                if (robot.outputs.shooterMaster.getError() < targetError) {

                    System.out.println("<Shooter> Beginning to shoot.");

                    robot.elevator.runAll();

                    robot.runNextLoop(() -> { if (!robot.driver.RB.get()) robot.elevator.stop(); }, 500);
                }
            }

            @Override public void held() {

                if (robot.outputs.shooterMaster.getError() < targetError)
                    System.out.println("<Shooter> Beginning to shoot.");

                Evaluable evaluable = new Evaluable() {

                    @Override public void eval() {

                        if (robot.outputs.shooterMaster.getError() > targetError) {

                            if (robot.elevator.isRunning())
                                System.err.println("<Shooter> WARN: (RPM) too low to fire.");

                            robot.elevator.stop();

                        } else robot.elevator.runAll();

                        if (robot.driver.RB.get()) robot.runNextLoop(this);
                    }
                };

                evaluable.eval();
            }

            @Override public void falling() { robot.elevator.stop(); }
        });


        robot.operator.UP.addListener(new ButtonListener() {

            @Override public void pressed() {

                targetRPM += 100;
                System.out.println("<Shooter> Target (RPM) was set to " + (int) targetRPM + ".");

                if (robot.outputs.shooterMaster.get() != 0) robot.outputs.shooterMaster.set(targetRPM);
            }
        });

        robot.operator.DOWN.addListener(new ButtonListener() {

            @Override public void pressed() {

                targetRPM -= 100;
                System.out.println("<Shooter> Target (RPM) was set to " + (int) targetRPM + ".");

                if (robot.outputs.shooterMaster.get() != 0) robot.outputs.shooterMaster.set(targetRPM);
            }
        });

        robot.operator.A.addListener(new ButtonListener() {
            @Override
            public void falling() {

                //module.visionClass.lazySusan();
                robot.outputs.shooterMaster.set(setSpeed);
                robot.elevator.cockingSetup();
                turret_result = robot.lazySusan.getVision_state();//visionClass.getTargetState();
                System.out.println("turret result = " + turret_result);

                //RPM = module.outputs.shooterMaster.getEncVelocity();
                if (RPM < 10000 && RPM > 100) {// get min rps values
                    speed = true;
                    System.out.println("speed = true");
                }
                if (speed == true && turret_result == 2) {

                    System.out.println("START THE RUMBLE!!!!!!!!");
                    linearAc = 0; //linearAc = visionClass.getLinearAc();
                    robot.outputs.shooterHood.set(linearAc);


                } else if (turret_result == 0) {

                    robot.runNextLoop(() -> robot.driver.setRumble(1), 0);

                    robot.runNextLoop(() -> robot.operator.setRumble(1), 0);

                    robot.runNextLoop(() -> robot.driver.setRumble(0), 3000);

                    robot.runNextLoop(() -> robot.operator.setRumble(0), 3000);
                }
            }
        });

        robot.operator.B.addListener(new ButtonListener() {
            @Override
            public void rising() {
                //RPM = module.outputs.shooterMaster.getEncVelocity();
                if (RPM < 10000 && RPM > 100) {
                    robot.elevator.stopMotors();
                    robot.elevator.fire();
                    System.out.println("Fire");
                }
                if (RPM > 10000 || RPM < 100) {
                    robot.elevator.stopMotors();
                    System.out.println("no fire");
                }
            }

            @Override
            public void falling() {
                robot.elevator.stopMotors();
                System.out.println("not firing");
            }
        });

        robot.operator.X.addListener(new ButtonListener() {
            @Override
            public void falling() {
                robot.elevator.stopMotors();
                robot.outputs.shooterMaster.set(0);
                System.out.println("mark its done");
            }
        });

        robot.operator.LH.addListener((value -> robot.outputs.turret.set(Math.abs(value) > 0.1 ? value * -0.2 : 0)));
    }
}