package com.jmex.editors.swing.particles;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jme.math.FastMath;
import com.jmex.editors.swing.widget.ValuePanel;
import com.jmex.editors.swing.widget.VectorPanel;
import com.jmex.effects.particles.SwarmInfluence;

public class SwarmInfluencePanel extends InfluenceEditPanel {

    private static final long serialVersionUID = 1L;

    private ValuePanel swarmRange = new ValuePanel("Range: ", "", -Float.MIN_VALUE,
            Float.MAX_VALUE, 0.1f);
    private ValuePanel swarmTurnSpeed = new ValuePanel("Turn Speed: ", "", -Float.MIN_VALUE,
            Float.MAX_VALUE, 0.1f);
    private ValuePanel swarmMaxSpeed = new ValuePanel("Max Speed: ", "", -Float.MIN_VALUE,
            Float.MAX_VALUE, 0.1f);
    private ValuePanel swarmAcceleration = new ValuePanel("Acceleration: ", "", -Float.MIN_VALUE,
            Float.MAX_VALUE, 0.1f);
    private ValuePanel swarmDeviance = new ValuePanel("Deviance: ", "", 0,
            180, 1f);
    private VectorPanel swarmLocationPanel = new VectorPanel(-Float.MIN_VALUE,
            Float.MAX_VALUE, 0.1f);

    public SwarmInfluencePanel() {
        super();
        setLayout(new GridBagLayout());
        initPanel();
    }

    private void initPanel() {
        swarmRange.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ((SwarmInfluence) getEdittedInfluence()).setSwarmRange(swarmRange.getFloatValue());
            }
        });
        swarmDeviance.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ((SwarmInfluence) getEdittedInfluence()).setDeviance(FastMath.DEG_TO_RAD * swarmDeviance.getFloatValue());
            }
        });
        swarmMaxSpeed.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ((SwarmInfluence) getEdittedInfluence()).setMaxSpeed(swarmMaxSpeed.getFloatValue());
            }
        });
        swarmAcceleration.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ((SwarmInfluence) getEdittedInfluence()).setSpeedBump(swarmAcceleration.getFloatValue());
            }
        });
        swarmTurnSpeed.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ((SwarmInfluence) getEdittedInfluence()).setTurnSpeed(swarmTurnSpeed.getFloatValue());
            }
        });

        swarmLocationPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ((SwarmInfluence) getEdittedInfluence()).setSwarmOffset(swarmLocationPanel.getValue());
            }
        });
        
        swarmLocationPanel.setBorder(createTitledBorder(" SWARM OFFSET "));
        
        setBorder(createTitledBorder(" SWARM PARAMETERS "));
        add(swarmLocationPanel, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
        add(swarmRange, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
        add(swarmMaxSpeed, new GridBagConstraints(0, 2, 1, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
        add(swarmAcceleration, new GridBagConstraints(0, 3, 1, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
        add(swarmTurnSpeed, new GridBagConstraints(0, 4, 1, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
        add(swarmDeviance, new GridBagConstraints(0, 5, 1, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
    }

    @Override
    public void updateWidgets() {
        SwarmInfluence swarm = (SwarmInfluence) getEdittedInfluence();
        swarmLocationPanel.setValue(swarm.getSwarmOffset());
        swarmRange.setValue(swarm.getSwarmRange());
        swarmMaxSpeed.setValue(swarm.getMaxSpeed());
        swarmAcceleration.setValue(swarm.getSpeedBump());
        swarmTurnSpeed.setValue(swarm.getTurnSpeed());
        swarmDeviance.setValue(swarm.getDeviance() * FastMath.RAD_TO_DEG);
    }
}
