package com.jmex.editors.swing.particles;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.jmex.effects.particles.ParticleInfluence;

public abstract class InfluenceEditPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private ParticleInfluence influence;

    public abstract void updateWidgets();

    public void setEdittedInfluence(ParticleInfluence influence) {
        this.influence = influence;
    }

    public ParticleInfluence getEdittedInfluence() {
        return influence;
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
