package it.unifi;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class UserInterface extends JFrame {
    private int THREADS_NUM = 4;

    public UserInterface() {
        super("JPEGsReaderMT");
        setSize(800, 525);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        setLocationRelativeTo(null);

        //JPANEL1--------------------------------
        JPanel p1 = new JPanel();
        JLabel label = new JLabel("Select Directory: ");
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(580, 24));
        JButton openButton = new JButton("Open");

        p1.add(label);
        p1.add(textField);
        p1.add(openButton);

        getContentPane().add(p1, BorderLayout.NORTH);
        //END JPANEL1---------------------------

        //JPANEL2--------------------------------
        JPanel p2 = new JPanel();
        p2.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));

        DefaultListModel<ImageContainer> listModel = new DefaultListModel<>();

        JList<ImageContainer> list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);

        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setPreferredSize(new Dimension(250, 400));

        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(500, 400));
        imageLabel.setHorizontalAlignment(JLabel.CENTER);

        p2.add(listScroller);
        p2.add(imageLabel);

        getContentPane().add(p2, BorderLayout.LINE_START);
        //END JPANEL2---------------------------

        //JPANEL3--------------------------------
        JPanel p3 = new JPanel();
        p3.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));

        JLabel label3 = new JLabel("Threads N.: ");

        Integer[] numbers = {1, 2, 3, 4, 5, 6, 7, 8};
        JComboBox<Integer> threadsComboBox = new JComboBox<>(numbers);
        threadsComboBox.setSelectedIndex(3);

        JButton startButton = new JButton("Start");
        startButton.setPreferredSize(new Dimension(130, 30));

        JLabel label4 = new JLabel("Execution Time: ");
        JLabel label5 = new JLabel("-----");
        label5.setFont(new Font("", Font.PLAIN, 20));

        p3.add(label3);
        p3.add(threadsComboBox);
        p3.add(startButton);
        p3.add(label4);
        p3.add(label5);

        getContentPane().add(p3, BorderLayout.SOUTH);
        //END JPANEL3---------------------------

        setVisible(true);

        //LISTENERS-----------------------------
        openButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int n = fileChooser.showOpenDialog(null);
            if (n == JFileChooser.APPROVE_OPTION) {
                textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        list.addListSelectionListener(e -> {
            if (!list.getValueIsAdjusting()) {
                Image image = null;
                try {
                    image = list.getSelectedValue().getImage();
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }

                if (image != null) {
                    imageLabel.setIcon(new ImageIcon(image));
                } else {
                    imageLabel.setIcon(null);
                }

            }
        });

        threadsComboBox.addActionListener(e -> THREADS_NUM = threadsComboBox.getSelectedIndex() + 1);

        startButton.addActionListener(e -> {
            listModel.clear();
            startButton.setEnabled(false);

            File folder = new File(textField.getText());
            File[] files = folder.listFiles();

            if (files != null) {
                label5.setText("Executing...");

                int elementsNumber = files.length / THREADS_NUM;

                if (files.length < 20) {
                    THREADS_NUM = 1;
                    threadsComboBox.setSelectedIndex(0);
                    elementsNumber = files.length;
                }

                Reader[] readers = new Reader[THREADS_NUM];
                StateListener stateListener = new StateListener(THREADS_NUM, label5, startButton);

                for (int i = 0; i < THREADS_NUM - 1; i++) {
                    readers[i] = new Reader(files, elementsNumber * i, elementsNumber * (i + 1), listModel);
                    readers[i].addPropertyChangeListener(stateListener);
                }
                readers[THREADS_NUM - 1] = new Reader(files, elementsNumber * (THREADS_NUM - 1), files.length, listModel);
                readers[THREADS_NUM - 1].addPropertyChangeListener(stateListener);

                for (int i = 0; i < THREADS_NUM; i++) {
                    readers[i].execute();
                }
            }
        });

    }
}
