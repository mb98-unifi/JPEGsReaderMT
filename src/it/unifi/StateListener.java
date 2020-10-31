package it.unifi;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class StateListener implements PropertyChangeListener {
    final private int THREADS_NUM;
    private int i = 0;
    final private long start;
    final private JLabel label;
    final private JButton startButton;

    public StateListener(int THREADS_NUM, JLabel label, JButton startButton) {
        this.THREADS_NUM = THREADS_NUM;
        start = System.currentTimeMillis();
        this.label = label;
        this.startButton = startButton;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String value = evt.getNewValue().toString();
        if (value.equals("DONE")) {
            if (++i == THREADS_NUM) {
                long end = System.currentTimeMillis();
                label.setText((end - start) + "ms");
                startButton.setEnabled(true);
            }
        }
    }
}
