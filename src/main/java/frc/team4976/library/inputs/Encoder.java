package frc.team4976.library.inputs;

import frc.team4976.library.IterativeRobotModule;
import frc.team4976.library.Subsystem;

public class Encoder extends Subsystem {

    edu.wpi.first.wpilibj.Encoder encoder;

    public Encoder(IterativeRobotModule module, int a, int b) {

        super(module);
        encoder = new edu.wpi.first.wpilibj.Encoder(a, b);
    }


}
