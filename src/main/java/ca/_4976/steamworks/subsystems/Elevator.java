package ca._4976.steamworks.subsystems;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.listeners.BooleanListener;
import ca._4976.steamworks.Robot;
import ca._4976.steamworks.io.Outputs;


/**
 * Created by jackfountain on 2017-01-31.
 */
public class Elevator extends AsynchronousRobot {

    //Declare Variables
    public int HECount = 0, SHECount = 0;
    public boolean ballsReady = false, bottomOfHE;
    private Outputs op;

    public Elevator(Robot module) {

        op = module.outputs;
        bottomOfHE = module.inputs.optical0.get();


        //Optical0 is the bottom of the hopper elevator
        //Optical1 is the bottom of the shooter elevator/end of hopper elevator
        //Optical2 is the top of shooter elevator


        module.inputs.optical0.addListener(new BooleanListener() {
            @Override
            public void rising() {
                //Increasing count in hopper elevator as ball enters
                HECount++;
                System.out.println("HECount: " + HECount + " SHECount:" + SHECount);

                //If HECount is less than max amt than bring a ball in
                if (HECount <= 8) {
                    runHE(1);
                }
                //If HECount is equal to or more than max amt move all balls to SHE
                else if (HECount >= 9 && SHECount <= 3) {
                    runHE(1);
                    runSHE(1);
                } else if (HECount >= 9 && SHECount >= 3){
                    stopMotors();
                }
            }

        });

        module.inputs.optical1.addListener(new BooleanListener() {
            @Override
            public void rising() {
                //Increasing count in shooter elevator as ball enters
                SHECount++;
                //Decreasing count in hopper elevator as ball leaves
                HECount--;
                System.out.println("HECount: " + HECount + " SHECount:" + SHECount);
            }
        });


        module.inputs.optical2.addListener(new BooleanListener() {
            @Override
            public void rising() {
                //Decrease count in shooter elevator as ball leaves
                System.out.println("HECount: " + HECount + " SHECount:" + SHECount);
                SHECount--;
                if (SHECount >= 3) {
                    //Stop motors
                    stopMotors();
                    //There are balls in the shooter elevator ready to shoot
                    ballsReady = true;
                    } else {
                        ballsReady = false;
                        runHE(1);
                        runSHE(1);
                    }
            }
        });

        module.inputs.optical1.addListener(new BooleanListener() {
            @Override
            public void falling() {
                if (HECount <= 0) {
                    module.runNextLoop(() -> {
                        if(HECount <= 0) {
                            runHE(0);
                        }
                    }, 5000);
                }
            }
        });

        module.inputs.optical2.addListener(new BooleanListener() {
            @Override
            public void falling() {
                if (HECount <= 0 && SHECount <= 0) {
                    module.runNextLoop(() -> {
                        if(HECount <= 0 && SHECount <= 0) {
                            stopMotors();
                        }
                    }, 5000);
                }
            }
        });
    }

    public void runHE(double speed) {
        op.HopperElevator.pidWrite(speed);
    }

    public void runSHE(double speed) {
        op.ShooterElevator.pidWrite(speed);
    }

    public void fire() {
        runHE(0.75);
        runSHE(0.75);
    }

    public void cockingSetup() {
        if (ballsReady) {
            runHE(0.5);
            runSHE(0.5);
        }
    }

    public void stopMotors(){ runHE(0); runSHE(0); }

}

