package ca._4976.library.outputs;

import edu.wpi.first.wpilibj.Spark;

/**
 * Funny joke
 */
public class Tallon extends Spark {
    /**
     * Constructor.
     *
     * @param channel The PWM channel that the SPARK is attached to. 0-9 are on-board, 10-19 are on
     *                the MXP port
     */
    public Tallon(int channel) {
        super(channel);
    }
}
