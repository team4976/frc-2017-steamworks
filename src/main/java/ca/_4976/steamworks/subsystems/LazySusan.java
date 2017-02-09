package ca._4976.steamworks.subsystems;

import ca._4976.library.AsynchronousRobot;
import ca._4976.library.listeners.ButtonListener;
import ca._4976.steamworks.Robot;
import edu.wpi.first.wpilibj.*;

/**
 * Created by Grant on 2/2/2017.
 */
public class LazySusan extends AsynchronousRobot{
              //midura is a placeholder for midura's ready, searching or failed variable
              int vision_state = 0;
    public LazySusan(Robot module){


        module.driver.BACK.addListener(new ButtonListener() {
            @Override
            public void falling() {

                if(vision_state == 0) {
                    vision_state = 2;
                    System.out.println("Seeing target");
                }
                else if(vision_state == 2) {
                    vision_state = 0;
                    System.out.println("Not seeing target");
                }
            }
        });

        //call midura's code

        module.driver.A.addListener(new ButtonListener() {
            @Override public void rising() {
                if (vision_state == 0) {

                    module.runNextLoop(() -> module.driver.setRumble(1), 0);

                    module.runNextLoop(() -> module.driver.setRumble(0), 3000);
                }

            }

        });
    }
}
//give up