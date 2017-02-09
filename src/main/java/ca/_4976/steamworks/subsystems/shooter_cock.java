package ca._4976.steamworks.subsystems;

import ca._4976.library.Evaluable;
import ca._4976.library.listeners.ButtonListener;
import ca._4976.steamworks.Robot;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class shooter_cock {

    boolean speed = false;
    boolean shooter_firing = false;
    double RPM = 0, linearAc = 0;
    public int turret_result = 0, vision_state = 0, rumble = 0;

    private Robot module;

    public shooter_cock (Robot module) {
        NetworkTable table = NetworkTable.getTable("shooter");
        PIDController ShooterPid = new PIDController((0.0002), (0), (0), module.inputs.shooter_encoder, module.outputs.shooter);// get numbers from midera

        this.module = module;

        //ShooterPid.setPID(table.getNumber("p", 0), table.getNumber("i", 0), table.getNumber("d", 0));
        module.operator.A.addListener(new ButtonListener() {
            @Override
            public void falling() {
                ShooterPid.setSetpoint(0);// get pid nummbers from midera
                linearAc = 0; //get values from vision
                module.outputs.hood.set(linearAc);
                ShooterPid.enable();
                //cockingSetup();
                //LazySusan();
                turret_result = vision_state;//get number from grants code

                if (RPM < 10000 && RPM > 100){// get min rps values
                    speed = true;
                }
                if (speed == true && turret_result == 2){
                    while(rumble < 4) {

                        module.runNextLoop(new Evaluable() {
                            @Override
                            public void eval() {
                                module.driver.setRumble(1);
                                module.operator.setRumble(1);
                            }
                        });
                        module.runNextLoop(new Evaluable() {
                            @Override
                            public void eval() {
                                module.driver.setRumble(0);
                                module.operator.setRumble(0);
                                rumble ++;
                            }
                        },250);
                    }
                }
//                module.runNextLoop(new Evaluable() {
//                    @Override
//                    public void eval() {
//                        if (speed == true && turret_result == 2) {
//                            module.driver.setRumble(1);
//                            module.operator.setRumble(1);
//                            module.runNextLoop(new Evaluable() {
//                                @Override
//                                public void eval() {
//                                    module.driver.setRumble(0);
//                                    module.operator.setRumble(0);
//                                }
//                            },2000);
//                        }
//                    }
//                }, 6000);
            }

        });

        module.operator.X.addListener(new ButtonListener() {
            @Override
            public void rising() {
                if (RPM < 10000 && RPM  > 100){
                    shooter_firing = true;;
                }
                if (RPM > 10000 || RPM < 100){
                    shooter_firing = false;
                }
            }

            @Override
            public void falling() {
                shooter_firing = false;
            }
        });

        module.operator.B.addListener(new ButtonListener() {
            @Override
            public void falling() {
                ShooterPid.disable();
                //disable.cockingSetup();
                shooter_firing = false;
            }
        });
    }

}
//TODO get min RPM values, Get pid numbers from midera.