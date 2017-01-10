package frc.team4976.library.inputs;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import frc.team4976.library.IterativeRobotModule;
import frc.team4976.library.Subsystem;

public class DigitalEncoder extends Subsystem implements PIDSource {

    private Encoder encoder;

    private PIDSourceType type = PIDSourceType.kDisplacement;

    public DigitalEncoder(IterativeRobotModule module, int a, int b) {

        super(module);
        encoder = new Encoder(a, b);
    }

    public void setScalar(double scalar) { encoder.setDistancePerPulse(scalar); }

    public double getRate() { return encoder.getRate(); }

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
