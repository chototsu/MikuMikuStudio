package com.jmex.editors.swing.particles;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jme.math.Line;
import com.jme.math.Vector3f;
import com.jmex.effects.particles.ParticleGeometry;
import com.jmex.effects.particles.ParticleInfluence;
import com.jmex.effects.particles.SimpleParticleInfluenceFactory;
import com.jmex.effects.particles.SwarmInfluence;

public class ParticleInfluencePanel extends ParticleEditPanel {

    private static final long serialVersionUID = 1L;

    private InfluenceListModel influenceModel = new InfluenceListModel();
    private JList influenceList = new JList(influenceModel);
    private JButton deleteInfluenceButton;
    private JPanel influenceParamsPanel;
    
    public ParticleInfluencePanel() {
        super();
        setLayout(new GridBagLayout());
        initPanel();
    }
    
    private void initPanel() {
        influenceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        influenceList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int idx = influenceList.getSelectedIndex();
                deleteInfluenceButton.setEnabled(idx != -1);
                updateInfluenceParams();
            }
        });
        
        JButton newWindButton = new JButton(new AbstractAction("New Wind") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                getEdittedParticles().addInfluence(
                    SimpleParticleInfluenceFactory.createBasicWind(
                        1f, new Vector3f(Vector3f.UNIT_X), true, true));
                int idx = getEdittedParticles().getInfluences().size() - 1;
                influenceModel.fireIntervalAdded(idx, idx);
                influenceList.setSelectedIndex(idx);
            }
        });
        newWindButton.setMargin(new Insets(2, 2, 2, 2));
        
        JButton newGravityButton = new JButton(new AbstractAction("New Gravity") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                getEdittedParticles().addInfluence(
                    SimpleParticleInfluenceFactory.createBasicGravity(
                        new Vector3f(Vector3f.ZERO), true));
                int idx = getEdittedParticles().getInfluences().size() - 1;
                influenceModel.fireIntervalAdded(idx, idx);
                influenceList.setSelectedIndex(idx);
            }
        });
        newGravityButton.setMargin(new Insets(2, 2, 2, 2));

        JButton newDragButton = new JButton(new AbstractAction("New Drag") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                getEdittedParticles().addInfluence(
                    SimpleParticleInfluenceFactory.createBasicDrag(1f));
                int idx = getEdittedParticles().getInfluences().size() - 1;
                influenceModel.fireIntervalAdded(idx, idx);
                influenceList.setSelectedIndex(idx);
            }
        });
        newDragButton.setMargin(new Insets(2, 2, 2, 2));

        JButton newSwarmButton = new JButton(new AbstractAction("New Swarm") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                getEdittedParticles().addInfluence(
                    new SwarmInfluence(new Vector3f(), 3));
                int idx = getEdittedParticles().getInfluences().size() - 1;
                influenceModel.fireIntervalAdded(idx, idx);
                influenceList.setSelectedIndex(idx);
            }
        });
        newSwarmButton.setMargin(new Insets(2, 2, 2, 2));
        
        JButton newVortexButton = new JButton(new AbstractAction("New Vortex") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                getEdittedParticles().addInfluence(
                    SimpleParticleInfluenceFactory.createBasicVortex(
                        1f, 0f, new Line(new Vector3f(),
                            new Vector3f(Vector3f.UNIT_Y)), true, true));
                int idx = getEdittedParticles().getInfluences().size() - 1;
                influenceModel.fireIntervalAdded(idx, idx);
                influenceList.setSelectedIndex(idx);
            }
        });
        newVortexButton.setMargin(new Insets(2, 2, 2, 2));
        
        deleteInfluenceButton = new JButton(new AbstractAction("Delete") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                int idx = influenceList.getSelectedIndex();
                getEdittedParticles().getInfluences().remove(idx);
                influenceModel.fireIntervalRemoved(idx, idx);
                influenceList.setSelectedIndex(
                    idx >= getEdittedParticles().getInfluences().size() ? idx - 1 : idx);
            }
        });
        deleteInfluenceButton.setMargin(new Insets(2, 2, 2, 2));
        deleteInfluenceButton.setEnabled(false);
        
        JPanel influenceListPanel = new JPanel(new GridBagLayout());
        influenceListPanel.setBorder(createTitledBorder("PARTICLE INFLUENCES"));
        influenceListPanel.add(influenceList, new GridBagConstraints(0, 0, 1, 3, 0.5,
            0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(5, 10, 10, 5), 0, 0));
        influenceListPanel.add(newWindButton, new GridBagConstraints(1, 0, 1, 1,
            0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(5, 5, 5, 5), 0, 0));
        influenceListPanel.add(newGravityButton, new GridBagConstraints(2, 0, 1, 1,
            0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(5, 5, 5, 5), 0, 0));
        influenceListPanel.add(newDragButton, new GridBagConstraints(1, 1, 1, 1,
            0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(5, 5, 5, 5), 0, 0));
        influenceListPanel.add(newVortexButton, new GridBagConstraints(2, 1, 1, 1,
                0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        influenceListPanel.add(newSwarmButton, new GridBagConstraints(3, 0, 1, 1,
                0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        influenceListPanel.add(deleteInfluenceButton, new GridBagConstraints(1, 2, 2, 1,
                0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 10, 5), 0, 0));
        
        influenceParamsPanel = new JPanel(new BorderLayout());
        
        add(influenceListPanel, new GridBagConstraints(0, 0, 1, 1, 0.5,
            0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(5, 10, 10, 5), 0, 0));
        add(influenceParamsPanel, new GridBagConstraints(0, 1, 1, 1, 0.5,
            1.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
            new Insets(5, 10, 10, 5), 0, 0));
    }
    
    @Override
    public void updateWidgets() {
        influenceList.clearSelection();
        int fcount = (getEdittedParticles().getInfluences() == null) ?
            0 : getEdittedParticles().getInfluences().size();
        influenceModel.fireContentsChanged(0, fcount - 1);
    }

    /**
     * updateInfluenceParams
     */
    private void updateInfluenceParams() {
        influenceParamsPanel.removeAll();
        int idx = influenceList.getSelectedIndex();
        if (idx == -1) {
            influenceParamsPanel.validate();
            return;
        }
        ParticleInfluence influence = getEdittedParticles().getInfluences().get(idx);
        if (influence instanceof SimpleParticleInfluenceFactory.BasicWind) {
            WindInfluencePanel windParamsPanel = new WindInfluencePanel();
            windParamsPanel.setEdittedInfluence(influence);
            windParamsPanel.updateWidgets();
            influenceParamsPanel.add(windParamsPanel);
            
        } else if (influence instanceof SimpleParticleInfluenceFactory.BasicGravity) {
            GravityInfluencePanel gravityParamsPanel = new GravityInfluencePanel();
            gravityParamsPanel.setEdittedInfluence(influence);
            gravityParamsPanel.updateWidgets();
            influenceParamsPanel.add(gravityParamsPanel);
            
        } else if (influence instanceof SimpleParticleInfluenceFactory.BasicDrag) {
            DragInfluencePanel dragParamsPanel = new DragInfluencePanel();
            dragParamsPanel.setEdittedInfluence(influence);
            dragParamsPanel.updateWidgets();
            influenceParamsPanel.add(dragParamsPanel);
            
        } else if (influence instanceof SimpleParticleInfluenceFactory.BasicVortex) {
            VortexInfluencePanel vortexParamsPanel = new VortexInfluencePanel();
            vortexParamsPanel.setEdittedInfluence(influence);
            vortexParamsPanel.updateWidgets();
            influenceParamsPanel.add(vortexParamsPanel);

        } else if (influence instanceof SwarmInfluence) {
            SwarmInfluencePanel swarmInfluencePanel = new SwarmInfluencePanel();
            swarmInfluencePanel.setEdittedInfluence(influence);
            swarmInfluencePanel.updateWidgets();
            influenceParamsPanel.add(swarmInfluencePanel);

        }
        influenceParamsPanel.getParent().validate();
        influenceParamsPanel.getParent().repaint();
    }

    class InfluenceListModel extends AbstractListModel {

        private static final long serialVersionUID = 1L;

        public int getSize() {
            ParticleGeometry particles = getEdittedParticles();
            return (particles == null || particles.getInfluences() == null) ? 0
                    : particles.getInfluences().size();
        }

        public Object getElementAt(int index) {
            ParticleInfluence pf = getEdittedParticles().getInfluences().get(index);
            if (pf instanceof SimpleParticleInfluenceFactory.BasicWind) {
                return "Wind";
            } else if (pf instanceof SimpleParticleInfluenceFactory.BasicGravity) {
                return "Gravity";
            } else if (pf instanceof SimpleParticleInfluenceFactory.BasicDrag) {
                return "Drag";
            } else if (pf instanceof SimpleParticleInfluenceFactory.BasicVortex) {
                return "Vortex";
            } else if (pf instanceof SwarmInfluence) {
                return "Swarm";
            } else {
                return "???";
            }
        }

        public void fireContentsChanged(int idx0, int idx1) {
            super.fireContentsChanged(this, idx0, idx1);
        }

        public void fireIntervalAdded(int idx0, int idx1) {
            super.fireIntervalAdded(this, idx0, idx1);
        }

        public void fireIntervalRemoved(int idx0, int idx1) {
            super.fireIntervalRemoved(this, idx0, idx1);
        }
    }

}
