package com.jmex.editors.swing.particles;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jmex.editors.swing.widget.ValuePanel;
import com.jmex.effects.particles.WanderInfluence;

public class WanderInfluencePanel extends InfluenceEditPanel {

    private static final long serialVersionUID = 1L;

    private ValuePanel wanderRadius = new ValuePanel("Wander Circle Radius: ", "", 0,
            Float.MAX_VALUE, 0.01f);
    private ValuePanel wanderDistance = new ValuePanel("Wander Circle Distance: ", "", -Float.MIN_VALUE,
            Float.MAX_VALUE, 0.1f);
    private ValuePanel wanderJitter = new ValuePanel("Jitter Amount: ", "", -Float.MIN_VALUE,
            Float.MAX_VALUE, 0.001f);

    public WanderInfluencePanel() {
        super();
        setLayout(new GridBagLayout());
        initPanel();
    }

    private void initPanel() {
        wanderRadius.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ((WanderInfluence) getEdittedInfluence()).setWanderRadius(wanderRadius.getFloatValue());
            }
        });
        wanderDistance.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ((WanderInfluence) getEdittedInfluence()).setWanderDistance(wanderDistance.getFloatValue());
            }
        });
        wanderJitter.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ((WanderInfluence) getEdittedInfluence()).setWanderJitter(wanderJitter.getFloatValue());
            }
        });
        
        setBorder(createTitledBorder(" WANDER PARAMETERS "));
        add(wanderRadius, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
        add(wanderDistance, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
        add(wanderJitter, new GridBagConstraints(0, 2, 1, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
    }

    @Override
    public void updateWidgets() {
        WanderInfluence wander = (WanderInfluence) getEdittedInfluence();
        wanderRadius.setValue(wander.getWanderRadius());
        wanderDistance.setValue(wander.getWanderDistance());
        wanderJitter.setValue(wander.getWanderJitter());
    }
}
