package ca._4976.steamworks.subsystems;

import ca._4976.library.listeners.BooleanListener;
import ca._4976.library.listeners.ButtonListener;
import ca._4976.steamworks.Robot;
import ca._4976.steamworks.io.Inputs;
import ca._4976.steamworks.io.Outputs;

/**
 * Created by jackfountain on 2017-02-11.
 */
public class ElevatorBackUp {

    public boolean elevatorUserControl = false;
    private Outputs outputs;
    private Inputs inputs;

    public ElevatorBackUp(Robot module){

        outputs = module.outputs;
        inputs = module.inputs;

            module.inputs.bottomOfHE.addListener(new BooleanListener() {
                @Override
                public void rising() {
                    if(!module.inputs.topOfSHE.get()){
                        runHE(1);
                        runSHE(1);
                    }
                }
            });

            module.inputs.topOfSHE.addListener(new BooleanListener() {
                @Override
                public void rising() {
                    stopMotors();
                }
            });

        module.operator.LB.addListener(new ButtonListener() {
            @Override
            public void rising() {
                runHE(1);
                elevatorUserControl = true;
            }
        });

        module.operator.LB.addListener(new ButtonListener() {
            @Override
            public void falling() {
                stopMotors();
                elevatorUserControl = false;
            }
        });

        module.operator.RB.addListener(new ButtonListener() {
            @Override
            public void rising() {
                runSHE(1);
                elevatorUserControl = true;
            }
        });

        module.operator.RB.addListener(new ButtonListener() {
            @Override
            public void falling() {
                stopMotors();
                elevatorUserControl = false;
            }
        });
}


    public void runHE(double speed){outputs.hopperElevator.pidWrite(speed);}

    public void runSHE(double speed){outputs.shooterElevator.pidWrite(speed);}

    public void stopMotors(){runHE(0); runSHE(0);}

    public void fire(){runHE(1); runSHE(1);}

    public void cockingSetup(){
        runHE(1);
        if(!inputs.topOfSHE.get()){
            runHE(1);
            runSHE(1);
        }
    }

}
