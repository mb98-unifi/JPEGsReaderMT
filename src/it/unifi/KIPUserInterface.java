package it.unifi;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class KIPUserInterface extends JDialog {

    public KIPUserInterface(ImageContainer imageContainer, int THREADS_NUM) {
        setSize(450, 100);
        setLayout(new BorderLayout());
        setResizable(false);
        setLocationRelativeTo(null);

        //JPANEL1--------------------------------
        JPanel p1 = new JPanel();
        JLabel directoryLabel = new JLabel("Select Directory: ");
        JTextField textField = new JTextField(System.getProperty("user.dir"));
        textField.setPreferredSize(new Dimension(235, 24));
        JButton openButton = new JButton("Open");

        p1.add(directoryLabel, FlowLayout.LEFT);
        p1.add(textField);
        p1.add(openButton, FlowLayout.RIGHT);

        getContentPane().add(p1, BorderLayout.NORTH);
        //END JPANEL1---------------------------

        //JPANEL2--------------------------------
        JPanel p2 = new JPanel();
        JLabel kernelLabel = new JLabel("Select Kernel: ");

        String[] modeStrings = {"Box Blur", "Edge Detection", "Sharpen", "Emboss"};
        JComboBox<String> modeComboBox = new JComboBox<>(modeStrings);

        JButton saveButton = new JButton("Save");
        saveButton.setPreferredSize(new Dimension(150, 30));

        p2.add(modeComboBox, FlowLayout.LEFT);
        p2.add(kernelLabel, FlowLayout.LEFT);
        p2.add(saveButton, FlowLayout.RIGHT);

        getContentPane().add(p2, BorderLayout.SOUTH);
        //END JPANEL2---------------------------

        openButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int n = fileChooser.showOpenDialog(this);
            if (n == JFileChooser.APPROVE_OPTION) {
                textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        saveButton.addActionListener(e -> {
            if (Files.isDirectory(Path.of(textField.getText()))) {
                Thread kipMainThread = new KIPMainThread(imageContainer, THREADS_NUM, textField, modeComboBox);
                kipMainThread.start();

                dispose();
            }
        });

        setModalityType(ModalityType.APPLICATION_MODAL);
        setVisible(true);
    }
}
