package com.jmex.editors.swing.particles;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jmex.editors.swing.widget.SphericalUnitVectorPanel;
import com.jmex.editors.swing.widget.ValuePanel;
import com.jmex.effects.particles.SimpleParticleInfluenceFactory;

public class WindInfluencePanel extends InfluenceEditPanel {

    private static final long serialVersionUID = 1L;

    private ValuePanel windStrengthPanel = new ValuePanel("Strength: ", "", 0f,
            100f, 0.1f);
    private SphericalUnitVectorPanel windDirectionPanel = new SphericalUnitVectorPanel();
    private JCheckBox windRandomBox;

    public WindInfluencePanel() {
        super();
        setLayout(new GridBagLayout());
        initPanel();
    }

    private void initPanel() {
        windDirectionPanel.setBorder(createTitledBorder(" DIRECTION "));
        windDirectionPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ((SimpleParticleInfluenceFactory.BasicWind) getEdittedInfluence())
                        .setWindDirection(windDirectionPanel.getValue());
            }
        });
        windStrengthPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ((SimpleParticleInfluenceFactory.BasicWind) getEdittedInfluence())
                        .setStrength(windStrengthPanel.getFloatValue());
            }
        });
        windRandomBox = new JCheckBox(new AbstractAction("Vary Randomly") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                ((SimpleParticleInfluenceFactory.BasicWind) getEdittedInfluence())
                        .setRandom(windRandomBox.isSelected());
            }
        });

        setBorder(createTitledBorder(" WIND PARAMETERS "));
        add(windDirectionPanel, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
        add(windStrengthPanel, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
        add(windRandomBox, new GridBagConstraints(0, 2, 1, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 5, 10, 5), 0, 0));
    }

    @Override
    public void updateWidgets() {
        SimpleParticleInfluenceFactory.BasicWind wind = (SimpleParticleInfluenceFactory.BasicWind) getEdittedInfluence();
        windDirectionPanel.setValue(wind.getWindDirection());
        windStrengthPanel.setValue(wind.getStrength());
        windRandomBox.setSelected(wind.isRandom());
    }
}
