package ca._4976.steamworks.subsystems;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.listeners.ButtonListener;
import ca._4976.steamworks.Robot;

/**
 * Created by Grant on 2/2/2017.
 */
public class LazySusan extends AsynchronousRobot{
    public int vision_state = 0;
    public LazySusan(Robot module){


        module.driver.BACK.addListener(new ButtonListener() {
            @Override
            public void falling() {

                if(getVision_state() == 0) {
                    setVision_state(2);
                    System.out.println("Seeing target");
                }
                else if(getVision_state() == 2) {
                    setVision_state(0);
                    System.out.println("Not seeing target");
                }
            }
        });

        //call midura's code


    }

    public int getVision_state() {
        return vision_state;
    }

    public void setVision_state(int vision_state) {
        this.vision_state = vision_state;
    }
}
