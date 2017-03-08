package ca._4976.steamworks.subsystems.profiler;

import ca._4976.library.controllers.components.Boolean;
import ca._4976.library.controllers.components.Double;

import java.io.*;
import java.util.ArrayList;

class SaveFile { //TODO: Complete Save system.

    private Boolean[] buttons = new Boolean[0];
    private Double[] axes = new Double[0];

    void save(String name, Moment[] moments) {

        try {

            BufferedWriter writer = new BufferedWriter(new FileWriter(new File("/motion/" + name)));

            for (Moment moment : moments) {

                writer.write(moment.leftDriveOutput + ",");
                writer.write(moment.rightDriveOutput + ",");
                writer.write(moment.leftEncoderPosition + ",");
                writer.write(moment.rightEncoderPosition + ",");
                writer.write(moment.leftEncoderVelocity + ",");
                writer.write(moment.rightEncoderVelocity + ",");
                writer.write(moment.rightEncoderVelocity + ",");

                for (int i = 0; i < moment.ids.length; i++) {

                    writer.write(moment.ids[i] + ".");
                    writer.write(moment.states[i] + "");

                    if (i + 1 < moment.ids.length) writer.write(",");
                }

                writer.close();
            }

        } catch (IOException e) { e.printStackTrace();}
    }

    Moment[] load(String name) {

        ArrayList<Moment> moments = new ArrayList<>();

        try {

            BufferedReader reader = new BufferedReader(new FileReader(new File("/motion/" + name)));

            for (String line = reader.readLine(); !line.equals(""); line = reader.readLine()) {

                String[] split = line.split(",");

                moments.add(new Moment(
                        java.lang.Double.parseDouble(split[0]),
                        java.lang.Double.parseDouble(split[2]),
                        java.lang.Double.parseDouble(split[3]),
                        java.lang.Double.parseDouble(split[4]),
                        java.lang.Double.parseDouble(split[5]),
                        java.lang.Double.parseDouble(split[6])
                ));

                ArrayList<Object[]> evaluables = new ArrayList<>();
                ArrayList<Integer> ids = new ArrayList<>();
                ArrayList<Object> states = new ArrayList<>();

                for (int i = 7; i < split.length; i++) {

                    String[] secondSplit = split[i].split(".");

                    int id = Integer.parseInt(secondSplit[0]);
                    String state = secondSplit[1];

                    if (id < 100) {

                        evaluables.add(buttons[id].getListeners());
                        ids.add(id);

                        if (Boolean.EVAL_STATE.FALLING.toString().equals(state))
                            states.add(Boolean.EVAL_STATE.FALLING);

                        else if (Boolean.EVAL_STATE.RISING.toString().equals(state))
                            states.add(Boolean.EVAL_STATE.RISING);

                        else if (Boolean.EVAL_STATE.PRESSED.toString().equals(state))
                            states.add(Boolean.EVAL_STATE.PRESSED);

                        else if (Boolean.EVAL_STATE.HELD.toString().equals(state))
                            states.add(Boolean.EVAL_STATE.HELD);
                    }
                }


                Object[][] evals = new Object[evaluables.size()][];
                int[] iDs = new int[ids.size()];

                for (int i = 0; i < evals.length; i++) { evals[i] = evaluables.get(i); }

                for (int i = 0; i < iDs.length; i++) { iDs[i] = ids.get(i); }

                moments.get(moments.size() - 1).addControllerInputs(evals, iDs, states.toArray(), null);
            }

        } catch (IOException e) { e.printStackTrace();}

        return null;
    }
}
