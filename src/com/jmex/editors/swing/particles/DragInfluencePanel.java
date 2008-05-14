package com.jmex.editors.swing.particles;

import java.awt.BorderLayout;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jmex.editors.swing.widget.ValuePanel;
import com.jmex.effects.particles.SimpleParticleInfluenceFactory;

public class DragInfluencePanel extends InfluenceEditPanel {

    private static final long serialVersionUID = 1L;
    
    private ValuePanel dragCoefficientPanel =
        new ValuePanel("Drag Coefficient: ", "", 0f, Float.MAX_VALUE, 0.1f);

    public DragInfluencePanel() {
        super();
        setLayout(new BorderLayout());
        initPanel();
    }
    
    private void initPanel() {
        dragCoefficientPanel.setBorder(createTitledBorder(" DRAG PARAMETERS "));
        dragCoefficientPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ((SimpleParticleInfluenceFactory.BasicDrag)getEdittedInfluence()).setDragCoefficient(
                    dragCoefficientPanel.getFloatValue());
            }
        });
        add(dragCoefficientPanel, BorderLayout.CENTER);
    }
    
    @Override
    public void updateWidgets() {
        dragCoefficientPanel.setValue(
            ((SimpleParticleInfluenceFactory.BasicDrag)getEdittedInfluence()).getDragCoefficient());
    }

}
