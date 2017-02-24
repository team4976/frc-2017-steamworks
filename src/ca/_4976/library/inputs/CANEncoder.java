package ca._4976.library.inputs;

import ca._4976.library.AsynchronousRobot;
import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

public class CANEncoder implements PIDSource {

    private AsynchronousRobot module;

    private CANTalon encoder;

    private PIDSourceType type = PIDSourceType.kDisplacement;

    public CANEncoder(AsynchronousRobot module, CANTalon motor) {

        this.module = module;
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
