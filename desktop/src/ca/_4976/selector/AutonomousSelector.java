package ca._4976.selector;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.IRemote;
import edu.wpi.first.wpilibj.tables.IRemoteConnectionListener;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class AutonomousSelector extends JFrame {

    private String[] files = new String[] { "None" };

    private ArrayList<JCheckBox> jCheckBoxes = new ArrayList<>();

    private JPanel panel = new JPanel();

    private AutonomousSelector() {

        setTitle("disconnected");

        buildList();

        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        JScrollPane scrollPane = new JScrollPane(panel);

        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane);
        setPreferredSize(new Dimension(400, 500));
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        NetworkTable.setClientMode();
        NetworkTable.setIPAddress("roborio-4976-frc.local");

        if (!NetworkTable.getTable("Motion Control").containsKey("table"))
            NetworkTable.getTable("Motion Control").putStringArray("table", new String[0]);


        NetworkTable.getTable("Motion Control").addConnectionListener(new IRemoteConnectionListener() {

            @Override public void connected(IRemote iRemote) {

                buildList();
                setTitle("Connected");
            }

            @Override
            public void disconnected(IRemote iRemote) {

                setTitle("disconnected");

                files = new String[] { "None" };
                jCheckBoxes.clear();
                panel.removeAll();

                buildList();

            }
        }, true);

        NetworkTable.getTable("Motion Control").addTableListener((iTable, s, o, b) -> {

            if (s.equals("table")) {

                System.out.println("hello");

                files = (String[]) o;
                buildList();
            }
        });
    }

    private void buildList() {

        for (int x = 0; x < files.length; x++) {

            JCheckBox checkBox = new JCheckBox();
            checkBox.setText(files[x]);
            checkBox.setFont(new Font("Arial", Font.PLAIN, 20));
            checkBox.setVisible(true);

            checkBox.addActionListener(new AbstractAction() {
                @Override public void actionPerformed(ActionEvent e) {

                    for (Component component : panel.getComponents()) {

                        ((JCheckBox) component).setSelected(false);
                        if (NetworkTable.getTable("Motion Control").isConnected()) checkBox.setSelected(true);

                        if (checkBox.getText().equals("None")) {

                            NetworkTable.getTable("Motion Control").putString("load_table", "");

                        } else NetworkTable.getTable("Motion Control").putString("load_table", checkBox.getText());
                    }
                }
            });

            boolean contains = false;

            for (int y = 0; y < jCheckBoxes.size(); y++) {

                if (jCheckBoxes.get(y).getText().equals(files[x])) {

                    contains = true;
                    break;
                }

            }

            if (jCheckBoxes.size() == 0 || !contains) jCheckBoxes.add(checkBox);
        }

        panel.removeAll();

        jCheckBoxes.forEach(jCheckBox -> panel.add(jCheckBox));

        panel.setVisible(false);
        panel.setVisible(true);
    }

    public static void main(String[] args) { new AutonomousSelector(); }
}
