package ca._4976.steamworks.subsystems.profiler;

import ca._4976.library.Evaluable;
import ca._4976.library.controllers.components.Boolean;
import ca._4976.library.controllers.components.Double;
import ca._4976.library.listeners.ButtonListener;
import ca._4976.library.listeners.DoubleListener;
import java.io.*;
import java.util.ArrayList;

class SaveFile {

    private Boolean[] buttons = new Boolean[0];
    private Double[] axes = new Double[0];

    Profile load(String name) {

        long start = System.nanoTime();

        ArrayList<Moment> moments = new ArrayList<>();
        ArrayList<Evaluable> evaluables = new ArrayList<>();
        ArrayList<Integer> times = new ArrayList<>();

        String line = "";

        double speed = 3200;
        double angle = 0.48;
        double position = 0;

        boolean runShooter = false;
        boolean extendWinch = false;

        try {

            BufferedReader reader = new BufferedReader(new FileReader(new File("/home/lvuser/motion/" + name)));

            int time = 0;
            for (line = reader.readLine(); line != null; line = reader.readLine()) {

                if (line.endsWith(",")) line = line.substring(0, line.length() - 1);

                if (line.toLowerCase().contains("config")) {

                    String[] split = line.substring(line.indexOf(":") + 1).split(",");

                    speed = java.lang.Double.parseDouble(split[0]);
                    angle = java.lang.Double.parseDouble(split[1]);
                    position = java.lang.Double.parseDouble(split[2]);
                    runShooter = java.lang.Boolean.parseBoolean(split[4]);
                    extendWinch = java.lang.Boolean.parseBoolean(split[5]);

                    continue;
                }

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

                for (int i = 6; i < split.length; i++) {

                    String[] secondSplit = split[i].split("\\.");

                    int id = Integer.parseInt(secondSplit[0]);
                    String state = secondSplit[1];

                    if (id > 100) id -= 100;

                    if (split.length == 2) for (ButtonListener listener : buttons[id].getListeners()) {

                        switch (state) {

                            case "FALLING": evaluables.add(listener::held); break;
                            case "RISING": evaluables.add(listener::rising);break;
                            case "PRESSED": evaluables.add(listener::pressed); break;
                            case "HELD": evaluables.add(listener::held); break;
                        }

                        times.add(time * 1000 / 200);

                    } else if (split.length == 3) for (DoubleListener listener : axes[id].getListeners()) {

                        evaluables.add(() -> listener.changed(java.lang.Double.parseDouble(split[2])));
                        times.add(time * 1000 / 200);
                    }
                }

                time++;
            }

            System.out.println("<Motion Control> File read successfully in " + (int) ((System.nanoTime() - start) / 1e+6) + "ms");

        } catch (IOException e) {

            System.out.println("<Motion Control> Failed to read file.");
            e.printStackTrace();
            System.out.println(line);
        }

        Moment[] finalMoments = new Moment[moments.size()];
        Evaluable[] finalEvaluables = new Evaluable[evaluables.size()];
        int[] finalTimes = new int[times.size()];

        for (int i = 0; i < finalMoments.length; i++) { finalMoments[i] = moments.get(i); }
        for (int i = 0; i < finalEvaluables.length; i++) { finalEvaluables[i] = evaluables.get(i); }
        for (int i = 0; i < finalTimes.length; i++) { finalTimes[i] = times.get(i); }

        return new Profile(
                speed,
                angle,
                position,
                finalMoments,
                finalEvaluables,
                finalTimes,
                runShooter,
                extendWinch
        );
    }

    String[] getFileNames() {

        File dir = new File("/home/lvuser/motion");

        return dir.list() != null ? dir.list() : new String[0];
    }

    void changeControllerRecordPresets(Boolean[] buttons) { this.buttons = buttons; }

    void changeControllerRecordPresets(Double[] axes) { this.axes = axes; }
}
