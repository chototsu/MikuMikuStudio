package com.jmex.editors.swing.particles;

import java.awt.BorderLayout;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jmex.editors.swing.widget.VectorPanel;
import com.jmex.effects.particles.SimpleParticleInfluenceFactory;

public class GravityInfluencePanel extends InfluenceEditPanel {

    private static final long serialVersionUID = 1L;

    private VectorPanel inflVector = new VectorPanel(
            -Float.MAX_VALUE, Float.MAX_VALUE, 0.1f);

    public GravityInfluencePanel() {
        super();
        setLayout(new BorderLayout());
        initPanel();
    }

    private void initPanel() {
        inflVector
                .setBorder(createTitledBorder(" GRAVITY INFLUENCE "));
        inflVector.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ((SimpleParticleInfluenceFactory.BasicGravity) getEdittedInfluence())
                        .setGravityForce(inflVector.getValue());
            }
        });
        add(inflVector, BorderLayout.CENTER);
    }

    @Override
    public void updateWidgets() {
        inflVector
                .setValue(((SimpleParticleInfluenceFactory.BasicGravity) getEdittedInfluence())
                        .getGravityForce());
    }
}
