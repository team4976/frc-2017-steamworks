package ca._4976.library.inputs;

import ca._4976.library.AsynchronousRobot;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

public class DigitalEncoder implements PIDSource {

    private AsynchronousRobot module;

    private Encoder encoder;

    private PIDSourceType type = PIDSourceType.kDisplacement;

    public DigitalEncoder(AsynchronousRobot module, int a, int b) {

        this.module = module;
        encoder = new Encoder(a, b);
    }

    public void setReversed(boolean reversed) { encoder.setReverseDirection(reversed); }

    public void setScalar(double scalar) { encoder.setDistancePerPulse(scalar); }

    public void setMinRate(double rate) { encoder.setMinRate(rate); }

    public double getRate() { return encoder.getRate(); }

    public double getDistance() { return encoder.getDistance(); }

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
