package ca._4976.library.inputs;

import ca._4976.library.IterativeRobot;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

public class DigitalEncoder implements PIDSource {

    private IterativeRobot module;

    private Encoder encoder;

    private PIDSourceType type = PIDSourceType.kDisplacement;

    public DigitalEncoder(IterativeRobot module, int a, int b) {

        this.module = module;
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
