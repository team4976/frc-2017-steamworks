package ca._4976.steamworks.subsystems.profiler;

import ca._4976.library.controllers.components.Boolean;
import ca._4976.library.controllers.components.Double;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

import java.io.*;
import java.util.ArrayList;

class SaveFile {

    private Boolean[] buttons = new Boolean[0];
    private Double[] axes = new Double[0];

    void save(String name, Moment[] moments) {

        try {

            name = "/home/lvuser/motion/" + name;

            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(name)));

            System.out.println("<Motion Control> Writing auto to file. (" + new File(name).getAbsolutePath() + ")");

            for (int x = 0; x < moments.length; x++) {

                Moment moment = moments[x];

                writer.write(moment.leftDriveOutput + ",");
                writer.write(moment.rightDriveOutput + ",");
                writer.write(moment.leftEncoderPosition + ",");
                writer.write(moment.rightEncoderPosition + ",");
                writer.write(moment.leftEncoderVelocity + ",");
                writer.write(moment.rightEncoderVelocity + "");

                if (moment.ids != null) {

                    for (int i = 0; i < moment.ids.length; i++) {

                        writer.write("," + moment.ids[i] + ".");
                        writer.write(moment.states[i] + "");

                    }
                }

                if (x == 0) {

                    String start_params = NetworkTable.getTable("Motion Control").getString("start_params", "");

                    if (!start_params.equals("")) {

                        writer.write("," + start_params);
                    }
                }

                writer.newLine();
            }

            writer.close();

        } catch (IOException e) { e.printStackTrace();}
    }

    Moment[] load(String name) {

        ArrayList<Moment> moments = new ArrayList<>();

        String line = "";

        try {

            BufferedReader reader = new BufferedReader(new FileReader(new File("/home/lvuser/motion/" + name)));

            for (line = reader.readLine(); line != null; line = reader.readLine()) {

                //Duct tape
                if (line.endsWith(",")) line = line.substring(0, line.length() - 1);

                String[] split = line.split(",");

                moments.add(new Moment(
                        java.lang.Double.parseDouble(split[0]),
                        java.lang.Double.parseDouble(split[1]),
                        java.lang.Double.parseDouble(split[2]),
                        java.lang.Double.parseDouble(split[3]),
                        java.lang.Double.parseDouble(split[4]),
                        java.lang.Double.parseDouble(split[5])
                ));

//                System.out.print(split[0] + "  " + moments.get(moments.size() - 1).leftDriveOutput);
//                System.out.print(" ");
//                System.out.println(split[0] + " " + moments.get(moments.size() - 1).rightDriveOutput);

                ArrayList<Object[]> evaluables = new ArrayList<>();
                ArrayList<Integer> ids = new ArrayList<>();
                ArrayList<Object> states = new ArrayList<>();

                for (int i = 6; i < split.length; i++) {

                    String[] secondSplit = split[i].split("\\.");

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

                    } else {

                        evaluables.add(axes[id - 100].getListeners());
                        ids.add(id);

                        states.add(Double.EVAL_STATE.CHANGED);
                    }
                }

                Object[][] evals = new Object[evaluables.size()][];
                int[] iDs = new int[ids.size()];

                for (int i = 0; i < evals.length; i++) {
                    evals[i] = evaluables.get(i);
                }

                for (int i = 0; i < iDs.length; i++) {
                    iDs[i] = ids.get(i);
                }

                moments.get(moments.size() - 1).addControllerInputs(evals, iDs, states.toArray(), null);
            }

        } catch (IOException e) { e.printStackTrace(); }

        System.out.println("<Motion Control> File read successfully.");

        Moment[] finalMoments = new Moment[moments.size()];

        for (int i = 0; i < finalMoments.length; i++) { finalMoments[i] = moments.get(i); }

        return finalMoments;
    }

    void changeControllerRecordPresets(Boolean[] buttons) { this.buttons = buttons; }

    void changeControllerRecordPresets(Double[] axes) { this.axes = axes; }
}
