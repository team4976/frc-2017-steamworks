package ca._4976.steamworks.subsystems;

import ca._4976.library.listeners.ButtonListener;
import ca._4976.steamworks.Robot;
import edu.wpi.first.wpilibj.PIDController;

public class shooter_cock {

    boolean speed = false;
    boolean shooter_firing = false;
    double RPM = 500, linearAc = 0;
    public int turret_result = 0, rumble = 0;

    private Robot module;

    public shooter_cock (Robot module) {
        //NetworkTable table = NetworkTable.getTable("shooter");
        PIDController ShooterPid = new PIDController((0.0002), (0), (0), module.inputs.shooter_encoder, module.outputs.shooter);// get numbers from midera

        this.module = module;

        //ShooterPid.setPID(table.getNumber("p", 0), table.getNumber("i", 0), table.getNumber("d", 0));
        module.operator.A.addListener(new ButtonListener() {
            @Override
            public void falling() {
                ShooterPid.setSetpoint(500);// get pid numbers from midura
                linearAc = 0; //get values from vision
                module.outputs.hood.set(linearAc);
                ShooterPid.enable();
                module.elevator.cockingSetup();
                turret_result = module.lazySusan.getVision_state();//get number from grants code
                System.out.println("turret result = " + turret_result);

                if (RPM < 10000 && RPM > 100){// get min rps values
                    speed = true;
                    System.out.println("speed = true");
                }
                if (speed == true && turret_result == 2){
                    System.out.println("START THE RUMBLE!!!!!!!!");
                    for (int i = 0; i < 6; i++) {

                        if (i % 2 == 0) module.runNextLoop(() -> module.operator.setRumble(1), 500 * i);

                        else module.runNextLoop(() -> module.operator.setRumble(0), 500 * i);

                        if (i % 2 == 0) module.runNextLoop(() -> module.driver.setRumble(1), 500 * i);

                        else module.runNextLoop(() -> module.driver.setRumble(0), 500 * i);
                    }
                }
            }
        });

        module.operator.B.addListener(new ButtonListener() {
            @Override
            public void rising() {
                if (RPM < 10000 && RPM  > 100){
                    module.elevator.stopMotors();
                    module.elevator.fire();
                    System.out.println("Fire");
                }
                if (RPM > 10000 || RPM < 100){
                    module.elevator.stopMotors();
                    System.out.println("no fire");
                }
            }

            @Override
            public void falling() {
                module.elevator.stopMotors();
                System.out.println("not firing");
            }
        });

        module.operator.X.addListener(new ButtonListener() {
            @Override
            public void falling() {
                module.elevator.stopMotors();
                ShooterPid.disable();
                System.out.println("mark its done");
            }
        });
    }

}
//TODO get min RPM values, Get pid numbers from midera.