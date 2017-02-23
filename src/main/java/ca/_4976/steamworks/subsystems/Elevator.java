package ca._4976.steamworks.subsystems;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.Evaluable;
import ca._4976.library.listeners.BooleanListener;
import ca._4976.library.listeners.ButtonListener;
import ca._4976.steamworks.Robot;
import ca._4976.steamworks.io.Inputs;
import ca._4976.steamworks.io.Outputs;

public class Elevator extends AsynchronousRobot {

    //Declare Variables
    public int HECount = 0, SHECount = 0, maxHE = 6, maxSHE = 3;
    public boolean ballsReady = false, elevatorOpControl = false, cockingShooter = false;
    private Outputs outputs;
    private Inputs inputs;
    private Robot module;

    public Elevator(Robot module) {

        //autoToggle = false;
        outputs = module.outputs;
        inputs = module.inputs;
        this.module = module;

        if(!elevatorOpControl && !cockingShooter) {
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
                    //Increasing count in shooterMaster elevator as ball enters
                    SHECount++;
                    //Decreasing count in hopper elevator as ball leaves
                    HECount--;
                    System.out.println("HECount: " + HECount + " SHECount:" + SHECount);
                    if (SHECount >= maxSHE) {
                        //Stop motors
                        runSHE(0);
                        //There are balls in the shooterMaster elevator ready to shoot
                        ballsReady = true;
                    } else {
                        ballsReady = false;
                        runHE(1);
                        runSHE(1);
                    }

                    if(HECount >= maxHE){
                        runHE(0);
                    }

                    if (HECount < 0) {
                        runHE(0);
                    }

                }
            });
        }

        module.inputs.topOfSHE.addListener(new BooleanListener() {
            @Override
            public void rising() {
                if(!elevatorOpControl && !cockingShooter) {
                    //Decrease count in shooterMaster elevator as ball leaves
                    System.out.println("HECount: " + HECount + " SHECount:" + SHECount);
                    System.out.println("topOfShe rising");
                    SHECount--;
                } else {
                    stopMotors();
                    cockingShooter = false;
                    elevatorOpControl = false;
                }
            }
        });

        module.inputs.bottomOfHE.addListener(new BooleanListener() {
            @Override
            public void falling() {
                motorPause();
            }
        });

        module.operator.LB.addListener(new ButtonListener() {
            @Override
            public void rising() {
                runHE(0.75);
                elevatorOpControl = true;
            }
        });

        module.operator.LB.addListener(new ButtonListener() {
            @Override
            public void falling() {
                runHE(0);
                elevatorOpControl = false;
            }
        });

        module.operator.RB.addListener(new ButtonListener() {
            @Override
            public void rising() {
                runSHE(1);
                elevatorOpControl = true;
            }
        });

        module.operator.RB.addListener(new ButtonListener() {
            @Override
            public void falling() {
                runSHE(0);
                elevatorOpControl = false;
            }
        });
    }



    public void runHE(double speed) {
        outputs.hopperElevator.pidWrite(speed);
    }

    public void runSHE(double speed) {
        outputs.shooterElevator.pidWrite(speed);
    }

    public void fire() {
        runHE(1);
        runSHE(1);
        cockingShooter = false;
        elevatorOpControl = true;
    }

    public void cockingSetup() {
        System.out.println("Starting cockingSetup. autoToggle: " + elevatorOpControl + " cockingShooter: " + cockingShooter);
        elevatorOpControl = true;
        cockingShooter = true;

        if(!module.inputs.topOfSHE.get()){
            runHE(1);
            runSHE(1);
        }
    }

//    public void loop(){
//        module.runNextLoop(new Evaluable() {
//            @Override
//            public void eval() {
//                System.out.println("cocking Shooter: " + cockingShooter);
//                if(cockingShooter == true){
//                    if (inputs.topOfSHE.get() == false) {
//                        runSHE(1);
//                    }
//                    if (inputs.topOfSHE.get() == true) {
//                        runSHE(0);
//                        cockingShooter = false;
//                        System.out.println("Top of shooterMaster sensor triggered");
//                    }
//                }
//                module.runNextLoop(()->loop());
//            }
//
//        });
//
//    }

    public void runAll() { }

    public void stop() { }

    public boolean isRunning() { return false; }

    public void stopMotors(){
        runHE(0);
        runSHE(0);
        //  cockingShooter = false;
    }

    public void motorPause(){
        System.out.println("Motor pause funciton called");
        if (!module.inputs.bottomOfHE.get()) {
            System.out.println("inside first if loop");
            module.runNextLoop(() -> {
                System.out.println("Inside timer loop");
                if (!module.inputs.bottomOfHE.get()) {
                    System.out.println("Inside second if loop");
                    stopMotors();
                }
            }, 5000);
        }
    }

}