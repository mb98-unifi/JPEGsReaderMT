package it.unifi;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;

public class UserInterface extends JFrame {
    private int THREADS_NUM = 4;
    private int MODE = 0;

    public UserInterface() {
        super("JPEGsReaderMT");
        setSize(800, 525);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        setLocationRelativeTo(null);

        //JPANEL1--------------------------------
        JPanel p1 = new JPanel();
        JLabel directoryLabel = new JLabel("Select Directory: ");
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(450, 24));
        JButton openButton = new JButton("Open");
        JButton KIPButton = new JButton("KIP");
        KIPButton.setPreferredSize(new Dimension(120, 30));
        KIPButton.setEnabled(false);

        p1.add(directoryLabel, FlowLayout.LEFT);
        p1.add(textField);
        p1.add(KIPButton, FlowLayout.RIGHT);
        p1.add(openButton, FlowLayout.RIGHT);

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
        p3.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 5));

        JLabel threadsLabel = new JLabel("Threads N.: ");

        Integer[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        JComboBox<Integer> threadsComboBox = new JComboBox<>(numbers);
        threadsComboBox.setSelectedIndex(3);

        JLabel modeLabel = new JLabel("Select Mode:");

        String[] modeStrings = {"No Queue", "Concurrent Queue", "Synchronized Queue"};
        JComboBox<String> modeComboBox = new JComboBox<>(modeStrings);

        JButton startButton = new JButton("Start");
        startButton.setPreferredSize(new Dimension(130, 30));

        JLabel timeLabel = new JLabel("Exec. Time:");
        JLabel printTimeLabel = new JLabel("-----");
        printTimeLabel.setFont(new Font("", Font.PLAIN, 20));

        p3.add(threadsLabel);
        p3.add(threadsComboBox);
        p3.add(modeLabel);
        p3.add(modeComboBox);
        p3.add(startButton);
        p3.add(timeLabel);
        p3.add(printTimeLabel);

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

        KIPButton.addActionListener(e -> new KIPUserInterface(list.getSelectedValue(), THREADS_NUM));

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
                    KIPButton.setEnabled(true);
                } else {
                    imageLabel.setIcon(null);
                    KIPButton.setEnabled(false);
                }

            }
        });

        threadsComboBox.addActionListener(e -> THREADS_NUM = threadsComboBox.getSelectedIndex() + 1);
        modeComboBox.addActionListener(e -> MODE = modeComboBox.getSelectedIndex());

        startButton.addActionListener(e -> {
            listModel.clear();
            startButton.setEnabled(false);

            File folder = new File(textField.getText());
            File[] files = folder.listFiles();

            if (files != null) {
                printTimeLabel.setText("Executing...");

                int elementsNumber = files.length / THREADS_NUM;

                if (files.length < 20) {
                    THREADS_NUM = 1;
                    threadsComboBox.setSelectedIndex(0);
                    elementsNumber = files.length;
                }

                Reader[] readers = new Reader[THREADS_NUM];
                StateListener stateListener = new StateListener(THREADS_NUM, printTimeLabel, startButton);

                if (MODE == 0) { //NO QUEUE
                    for (int i = 0; i < THREADS_NUM - 1; i++) {
                        readers[i] = new Reader(files, elementsNumber * i, elementsNumber * (i + 1), listModel);
                        readers[i].addPropertyChangeListener(stateListener);
                    }
                    readers[THREADS_NUM - 1] = new Reader(files, elementsNumber * (THREADS_NUM - 1), files.length, listModel);
                    readers[THREADS_NUM - 1].addPropertyChangeListener(stateListener);
                }else if(MODE == 1) {//CONCURRENT QUEUE
                    ConcurrentLinkedQueue<File> filesConcurrentQueue = new ConcurrentLinkedQueue<>(Arrays.asList(files));

                    for (int i = 0; i < THREADS_NUM; i++) {
                        readers[i] = new Reader(filesConcurrentQueue, listModel);
                        readers[i].addPropertyChangeListener(stateListener);
                    }
                }else if(MODE == 2){ //QUEUE WITH SYNCHRONIZATION
                    Queue<File> filesQueue = new LinkedList<>(Arrays.asList(files));

                    for (int i = 0; i < THREADS_NUM; i++) {
                        readers[i] = new Reader(filesQueue, listModel);
                        readers[i].addPropertyChangeListener(stateListener);
                    }
                }

                for (int i = 0; i < THREADS_NUM; i++) {
                    readers[i].execute();
                }
            }else {
                startButton.setEnabled(true);
            }
        });

    }
}
