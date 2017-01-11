package frc.team4976.library.inputs;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import frc.team4976.library.IterativeRobotModule;
import frc.team4976.library.Subsystem;
import frc.team4976.library.outputs.CANMotor;

public class CANEncoder extends Subsystem implements PIDSource {

    private CANMotor encoder;

    private PIDSourceType type = PIDSourceType.kDisplacement;

    public CANEncoder(IterativeRobotModule module, CANMotor motor) {

        super(module);
        encoder = motor;
    }

    public void setScalar(double scalar) { }

    public double getRate() { return 0; }

    public double getDistance() { return encoder.get(); }

    @Override public void setPIDSourceType(PIDSourceType pidSource) { }

    @Override public PIDSourceType getPIDSourceType() { return null; }

    @Override public double pidGet() {

        switch (type) {

            case kDisplacement: return getDistance();
            case kRate: return getRate();
        }

        return 0;
    }
}
