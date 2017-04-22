package ca._4976.processing;


import java.io.*;

public class AutonomousFileProcessor {

    private AutonomousFileProcessor() {

        File dir = new File("input");

        File[] files = dir.listFiles() != null ? dir.listFiles() : new File[0];

        for (int f = 0; f < files.length; f++) {

            try {

                BufferedReader reader = new BufferedReader(new FileReader("input/" + files[f].getName()));

                String file = files[f].getName();

                BufferedWriter writer = new BufferedWriter(new FileWriter("output/" + (file.endsWith(".csv") ? file : file + ".csv")));

                for (String line = reader.readLine(); line != null; line = reader.readLine()) {

                    System.out.println(line);

                    if (line.endsWith(",")) line = line.substring(0, line.length() - 1);

                    if (line.contains("config")) {

                        writer.write(line);

                    } else {

                        String[] split = line.split(",");

                        for (int i = 0; i < 6; i++) {

                            split[i] = -Double.parseDouble(split[i]) +"";
                        }

                        writer.write(split[1] + "," + split[0] + "," +
                                split[3] + "," + split[2] + "," + split[5] + "," + split[4]);

                        for (int i = 6; i < split.length; i++) writer.write("," + split[i]);
                    }

                    writer.newLine();
                }

                writer.close();

            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    public static void main(String[] args) { new AutonomousFileProcessor(); }
}
