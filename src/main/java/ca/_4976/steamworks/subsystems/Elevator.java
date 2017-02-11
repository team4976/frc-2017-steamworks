package ca._4976.steamworks.subsystems;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.listeners.BooleanListener;
import ca._4976.library.listeners.ButtonListener;
import ca._4976.steamworks.Robot;
import ca._4976.steamworks.io.Outputs;

/**
 * Created by jackfountain on 2017-01-31.
 */
public class Elevator extends AsynchronousRobot {

    //Declare Variables
    public int HECount = 0, SHECount = 0, maxHE = 6, maxSHE = 3;
    public boolean ballsReady = false, autoToggle = false;
    private Outputs op;

    public Elevator(Robot module) {

        autoToggle = false;
        op = module.outputs;

        //Optical0 is the bottom of the hopper elevator
        //Optical1 is the bottom of the shooter elevator/end of hopper elevator
        //Optical2 is the top of shooter elevator
        if(autoToggle == false) {
            module.inputs.bottomOfHE.addListener(new BooleanListener() {
                @Override
                public void rising() {
                    //Increase count in hopper elevator as ball enters
                    HECount++;
                    System.out.println("HECount: " + HECount + " SHECount:" + SHECount);

                    //If HECount is less than max amt than bring a ball in
                    if (HECount < maxHE && HECount > 0) {
                        runHE(1);
                    }
                    //If HECount is equal to or more than max amt move all balls to SHE
                    else if (HECount >= maxSHE && SHECount < maxSHE) {
                        runHE(1);
                        runSHE(1);
                    }

                    if (HECount <= 0 && SHECount <= 0) {
                        module.runNextLoop(() -> {
                            if (HECount <= 0 && SHECount <= 0) {
                                stopMotors();
                            }
                        }, 5000);
                    }

                    if(SHECount >= maxSHE){
                        runSHE(0);
                    }

                    if(HECount >= maxHE){
                        runHE(0);
                    }

                    if (HECount < 0) {
                        runHE(0);
                    }
                }
            });

            module.inputs.bottomOfSHE.addListener(new BooleanListener() {
                @Override
                public void rising() {
                    //Increasing count in shooter elevator as ball enters
                    SHECount++;
                    //Decreasing count in hopper elevator as ball leaves
                    HECount--;
                    System.out.println("HECount: " + HECount + " SHECount:" + SHECount);
                    if (SHECount >= maxSHE) {
                        //Stop motors
                        runSHE(0);
                        //There are balls in the shooter elevator ready to shoot
                        ballsReady = true;
                    } else {
                        ballsReady = false;
                        runHE(1);
                        runSHE(1);
                    }

                    if(HECount >= maxHE){
                        runHE(0);
                    }

                    if (HECount <= 0) {
                        module.runNextLoop(() -> {
                            if (HECount <= 0) {
                                runHE(0);
                            }
                        }, 5000);
                    }

                    if (HECount < 0) {
                        runHE(0);
                    }

                }
            });


            module.inputs.topOfSHE.addListener(new BooleanListener() {
                @Override
                public void rising() {
                    //Decrease count in shooter elevator as ball leaves
                    System.out.println("HECount: " + HECount + " SHECount:" + SHECount);
                    SHECount--;
                }
            });

            module.inputs.bottomOfSHE.addListener(new BooleanListener() {
                @Override
                public void falling() {

                    if (HECount <= 0) {
                        module.runNextLoop(() -> {
                            if (HECount <= 0) {
                                runHE(0);
                            }
                        }, 5000);
                    }
                }
            });

            module.inputs.topOfSHE.addListener(new BooleanListener() {
                @Override
                public void falling() {
                    if (HECount <= 0 && SHECount <= 0) {
                        module.runNextLoop(() -> {
                            if (HECount <= 0 && SHECount <= 0) {
                                stopMotors();
                            }
                        }, 5000);
                    }
                }
            });

        }

        module.operator.LB.addListener(new ButtonListener() {
            @Override
            public void rising() {
                runHE(1);
                 autoToggle = true;
            }
        });

        module.operator.LB.addListener(new ButtonListener() {
            @Override
            public void falling() {
                runHE(0);
                autoToggle = false;
            }
        });

        module.operator.RB.addListener(new ButtonListener() {
            @Override
            public void rising() {
                runSHE(1);
                autoToggle = true;
            }
        });

        module.operator.RB.addListener(new ButtonListener() {
            @Override
            public void falling() {
                runSHE(0);
                autoToggle = false;
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
        autoToggle = true;
    }

    public void cockingSetup() {
      //  if (ballsReady) {
            runHE(0.5);
            runSHE(0.5);
        }
    //}

    public void stopMotors(){ runHE(0); runSHE(0); }

}
