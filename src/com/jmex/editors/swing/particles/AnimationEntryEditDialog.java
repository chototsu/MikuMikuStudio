package com.jmex.editors.swing.particles;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.jmex.editors.swing.widget.ValuePanel;
import com.jmex.effects.particles.AnimationEntry;

public class AnimationEntryEditDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    public AnimationEntryEditDialog(final AnimationEntry entry) {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        setLayout(new GridBagLayout());
        
        final ValuePanel offsetPanel = new ValuePanel("Offset: ", "%", 0, 100, 1);
        offsetPanel.setValue((int)(entry.getOffset()*100));
        offsetPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                entry.setOffset(offsetPanel.getIntValue()/100f);
            }
        });

        JPanel off = new JPanel(new GridBagLayout());
        off.setBorder(createTitledBorder("OFFSET"));
        off.add(offsetPanel, new GridBagConstraints(0, 0, 1, 1, 1.0,
                0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        add(off, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 5, 5, 5), 0, 0));

        
        final ValuePanel ratePanel = new ValuePanel("Transition Time: ", "secs", 0f, Float.MAX_VALUE, .01f);
        ratePanel.setValue(0f);
        ratePanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                entry.setRate(ratePanel.getFloatValue());
            }
        });

        ratePanel.setValue(entry.getRate());

        JPanel rate = new JPanel(new GridBagLayout());
        rate.setBorder(createTitledBorder("PARTICLE SIZE"));
        rate.add(ratePanel, new GridBagConstraints(0, 0, 1, 1, 1.0,
                0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        add(rate, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 5, 5, 5), 0, 0));

        final JLabel framesLabel = createBoldLabel("Frames: ");
        
        final JTextField framesField = new JTextField();
        framesField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                entry.setFrames(makeFrames(framesField.getText()));
            }
            public void insertUpdate(DocumentEvent e) {
                entry.setFrames(makeFrames(framesField.getText()));
            }
            public void removeUpdate(DocumentEvent e) {
                entry.setFrames(makeFrames(framesField.getText()));
            }
        });

        framesField.setText(makeText(entry.getFrames()));

        JPanel frames = new JPanel(new GridBagLayout());
        frames.setBorder(createTitledBorder("ANIMATION FRAMES"));
        frames.add(framesLabel, new GridBagConstraints(0, 0, 1, 1, 0.0,
                0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 0), 0, 0));
        frames.add(framesField, new GridBagConstraints(1, 0, 1, 1, 1.0,
                0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        add(frames, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 5, 5, 5), 0, 0));
        
        pack();
    }

    private String makeText(int[] frames) {
        if (frames == null || frames.length == 0) return "";
        
        StringBuilder sb = new StringBuilder();
        for (int frame : frames) {
            sb.append(frame);
            sb.append(",");
        }
        return sb.substring(0, sb.length()-1);
    }
    
    private int[] makeFrames(String text) {
        StringTokenizer tok = new StringTokenizer(text, ",", false);
        ArrayList<Integer> vals = new ArrayList<Integer>();
        while (tok.hasMoreTokens()) {
            String token = tok.nextToken();
            if (token != null) {
                token.trim();
                try {
                    vals.add(Integer.parseInt(token));
                } catch (NumberFormatException nfe) {
                    ; // ignore.
                }
            }
        }
        int[] rVal = new int[vals.size()];
        for (int x = 0; x < rVal.length; x++) {
            rVal[x] = vals.get(x);
        }
        return rVal;
    }

    protected TitledBorder createTitledBorder(String title) {
        TitledBorder border = new TitledBorder(" " + title + " ");
        border.setTitleFont(new Font("Arial", Font.PLAIN, 10));
        return border;
    }

    protected JLabel createBoldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        return label;
    }

}
