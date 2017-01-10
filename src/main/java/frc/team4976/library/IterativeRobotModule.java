package frc.team4976.library;

import jaci.openrio.toast.lib.module.IterativeModule;

import java.util.ArrayList;


public abstract class IterativeRobotModule extends IterativeModule {

    private  ArrayList<Object[]> evalables = new ArrayList<>();

    public void runNextLoop(Evalable evalable) { evalables.add(new Object[] {evalable, 0L}); }

    public void runNextLoop(Evalable evalable, int delay) {
        evalables.add(new Object[] {evalable, System.currentTimeMillis() + delay}); }

    @Override public void teleopPeriodic() {

        for (int i = evalables.size() - 1; i >= 0; i--) {

            long time = ((long) evalables.get(i)[1]);

            if (time == 0 || time <= System.currentTimeMillis()) {

                ((Evalable) evalables.get(i)[0]).eval();
                evalables.remove(i);
            }
        }
    }
}
