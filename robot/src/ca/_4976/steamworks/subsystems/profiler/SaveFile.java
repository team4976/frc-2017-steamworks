package ca._4976.steamworks.subsystems.profiler;

import ca._4976.data.Moment;
import ca._4976.data.Profile;
import ca._4976.library.Evaluator;
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
        ArrayList<Evaluator> evaluators = new ArrayList<>();

        String line = "";

        double speed = 3200;
        double angle = 0.48;
        double position = 0;
        double endTime = 0;

        boolean runShooter = false;
        boolean extendWinch = false;

	    int time = 0;

	    try {

            BufferedReader reader = new BufferedReader(new FileReader(new File("/home/lvuser/motion/" + name)));

            for (line = reader.readLine(); line != null; line = reader.readLine()) {

                if (line.endsWith(",")) line = line.substring(0, line.length() - 1);

                if (line.toLowerCase().contains("config")) {

                    String[] split = line.split(":")[1].split(",");

                    speed = java.lang.Double.parseDouble(split[0]);
                    angle = java.lang.Double.parseDouble(split[1]);
                    position = java.lang.Double.parseDouble(split[2]);
                    runShooter = java.lang.Boolean.parseBoolean(split[3]);
                    extendWinch = java.lang.Boolean.parseBoolean(split[4]);

                    if (split.length > 5) endTime = java.lang.Double.parseDouble(split[5]);

                    continue;
                }

                while (line.contains(",,")) line = line.replace(",,", ",");
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

	                System.out.println("\t" + split[i] + " " + moments.size());

	                String[] secondSplit = split[i].split("\\.");

                    int id = Integer.parseInt(secondSplit[0]);
                    String state = secondSplit[1];

                    if (id > 100) id -= 100;

                    if (secondSplit.length == 2) for (ButtonListener listener : buttons[id].getListeners()) {

                        switch (state) {

                            case "FALLING": evaluators.add(new Evaluator(listener::held, time)); break;
                            case "RISING": evaluators.add(new Evaluator(listener::rising, time)); break;
                            case "PRESSED": evaluators.add(new Evaluator(listener::pressed, time)); break;
                            case "HELD": evaluators.add(new Evaluator(listener::held, time)); break;
                        }

                    } else if (split.length == 3) for (DoubleListener listener : axes[id].getListeners()) {

                        double value = java.lang.Double.parseDouble(split[2] + "." + split[3]);

                        evaluators.add(new Evaluator(() -> listener.changed(value), time));
                    }
                }

                time++;
            }

            System.out.println("<Motion Control> File read successfully in " + (int) ((System.nanoTime() - start) / 1e+6) + "ms");

        } catch (IOException e) {

            System.out.println("<Motion Control> Failed to read file.");
            e.printStackTrace();

        } catch (NumberFormatException e) {

		    System.out.println("<Motion Control> Failed to read file.");
		    //e.printStackTrace();
		    System.out.println(time + ": " +  line);
	    }

        Moment[] finalMoments = new Moment[moments.size()];
        Evaluator[] finalEvaluators = new Evaluator[evaluators.size()];

        for (int i = 0; i < finalMoments.length; i++) { finalMoments[i] = moments.get(i); }
        for (int i = 0; i < finalEvaluators.length; i++) { finalEvaluators[i] = evaluators.get(i); }

        return new Profile(
                speed,
                angle,
                position,
                runShooter,
                extendWinch,
                endTime,
                finalMoments,
                finalEvaluators
        );
    }

    String[] getFileNames() {

        File dir = new File("/home/lvuser/motion");

        int inadmissible = 0;

	    File[] results = dir.listFiles();
	    assert results != null;
	    for (File result : results) { if (result.isDirectory()) inadmissible++; }

	    String[] paths = new String[results.length - inadmissible];

	    int b = 0;
	    for (int i = b; i < paths.length; i++) {

		    if (results[i].isDirectory()) paths[i] = results[++b].getName();

		    else paths[i] = results[b].getName();

		    b++;
	    }

        return paths;
    }

    void changeControllerRecordPresets(Boolean[] buttons) { this.buttons = buttons; }

    void changeControllerRecordPresets(Double[] axes) { this.axes = axes; }
}
