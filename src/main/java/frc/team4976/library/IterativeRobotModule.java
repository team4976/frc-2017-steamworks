package frc.team4976.library;

import jaci.openrio.toast.lib.module.IterativeModule;

import java.util.ArrayList;


public abstract class IterativeRobotModule extends IterativeModule {

    private  ArrayList<Evalable> evalables = new ArrayList<>();

    public void runNextLoop(Evalable evalable) { evalables.add(evalable); }

    @Override public void teleopPeriodic() {

        for (int i = evalables.size() - 1; i >= 0; i--) {

            evalables.get(i).eval();
            evalables.remove(i);
        }
    }
}
