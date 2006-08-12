/*
 * Copyright (c) 2003-2006 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme.input.controls;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

import javax.swing.*;

import com.jme.input.*;
import com.jme.input.controls.binding.*;
import com.jme.input.joystick.*;

/**
 * @author Matthew D. Hicks
 */
public class GameControl {
    private String name;
    private List<Binding> bindings;

    public GameControl(String name) {
        this(name, null);
    }

    public GameControl(String name, Binding binding) {
        this.name = name;
        bindings = new LinkedList<Binding>();
        bindings.add(binding);
    }

    public List<Binding> getBindings() {
        return bindings;
    }
    
    public void clearBindings() {
    	bindings.clear();
    }
    
    public void addBinding(Binding binding) {
    	bindings.add(binding);
    }
    
    public void removeBinding(Binding binding) {
    	bindings.remove(binding);
    }
    
    public void replace(Binding oldBinding, Binding newBinding) {
    	if (oldBinding != null) {
    		removeBinding(oldBinding);
    	}
    	addBinding(newBinding);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static final JPanel createConfigurationPanel(
            List<GameControl> controls) {
        JPanel panel = new JPanel();
        SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);
        MouseListener assigner = new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                if (e.getComponent() instanceof BindingField) {
                    BindingField field = (BindingField)e.getComponent();
                    new GameControlAssignment(field);
                    //System.out.println("Binding: " + field.getBinding());
                }
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }
        };
        for (GameControl control : controls) {
            JLabel label = new JLabel(control.getName());
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            BindingField field = new BindingField(control, null);
            if (control.getBindings().size() > 0) {
            	field.setBinding(control.getBindings().get(0));
            }
            field.addMouseListener(assigner);
            field.setEditable(false);
            panel.add(label);
            panel.add(field);
        }
        makeCompactGrid(panel, controls.size(), 2, 5, 5, 5, 5);
        return panel;
    }

    private static void makeCompactGrid(Container parent, int rows, int cols,
            int initialX, int initialY, int xPad, int yPad) {
        SpringLayout layout;
        try {
            layout = (SpringLayout) parent.getLayout();
        } catch (ClassCastException exc) {
            System.err
                    .println("The first argument to makeCompactGrid must use SpringLayout.");
            return;
        }

        // Align all cells in each column and make them the same width.
        Spring x = Spring.constant(initialX);
        for (int c = 0; c < cols; c++) {
            Spring width = Spring.constant(0);
            for (int r = 0; r < rows; r++) {
                width = Spring.max(width, getConstraintsForCell(r, c, parent,
                        cols).getWidth());
            }
            for (int r = 0; r < rows; r++) {
                SpringLayout.Constraints constraints = getConstraintsForCell(r,
                        c, parent, cols);
                constraints.setX(x);
                constraints.setWidth(width);
            }
            x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
        }

        // Align all cells in each row and make them the same height.
        Spring y = Spring.constant(initialY);
        for (int r = 0; r < rows; r++) {
            Spring height = Spring.constant(0);
            for (int c = 0; c < cols; c++) {
                height = Spring.max(height, getConstraintsForCell(r, c, parent,
                        cols).getHeight());
            }
            for (int c = 0; c < cols; c++) {
                SpringLayout.Constraints constraints = getConstraintsForCell(r,
                        c, parent, cols);
                constraints.setY(y);
                constraints.setHeight(height);
            }
            y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
        }

        // Set the parent's size.
        SpringLayout.Constraints pCons = layout.getConstraints(parent);
        pCons.setConstraint(SpringLayout.SOUTH, y);
        pCons.setConstraint(SpringLayout.EAST, x);
    }

    private static SpringLayout.Constraints getConstraintsForCell(int row,
            int col, Container parent, int cols) {
        SpringLayout layout = (SpringLayout) parent.getLayout();
        Component c = parent.getComponent(row * cols + col);
        return layout.getConstraints(c);
    }
}

class GameControlAssignment implements KeyInputListener, MouseInputListener, JoystickInputListener {
	private BindingField field;
	private boolean hasBeenSet;
	
	public GameControlAssignment(BindingField field) {
		field.setText("Press a key");
		
		MouseInput.get().setCursorVisible(false);
		
		this.field = field;
		KeyInput.get().addListener(this);
		MouseInput.get().addListener(this);
		JoystickInput.get().addListener(this);
	}

	public void onKey(char character, int keyCode, boolean pressed) {
		if (pressed) {
			setBinding(new KeyboardBinding(keyCode));
		}
	}

	public void onButton(int button, boolean pressed, int x, int y) {
		if (pressed) {
			setBinding(new MouseButtonBinding(button));
		}
	}

	public void onMove(int xDelta, int yDelta, int newX, int newY) {
		if ((xDelta == 0) && (yDelta == 0)) return;
		if (Math.abs(xDelta) > Math.abs(yDelta)) {
			// X change is greater
			if (xDelta > 0) {
				setBinding(new MouseAxisBinding(MouseAxisBinding.AXIS_X, false));
			} else {
				setBinding(new MouseAxisBinding(MouseAxisBinding.AXIS_X, true));
			}
		} else {
			// Y change is greater
			if (yDelta > 0) {
				setBinding(new MouseAxisBinding(MouseAxisBinding.AXIS_Y, false));
			} else {
				setBinding(new MouseAxisBinding(MouseAxisBinding.AXIS_Y, true));
			}
		}
	}

	public void onWheel(int wheelDelta, int x, int y) {
		if (wheelDelta > 0) {
			setBinding(new MouseAxisBinding(MouseAxisBinding.AXIS_W, false));
		} else if (wheelDelta < 0) {
			setBinding(new MouseAxisBinding(MouseAxisBinding.AXIS_W, true));
		}
	}

	public void onAxis(Joystick controller, int axis, float axisValue) {
		if (axisValue != 0.0f) {
			boolean reverse = (axisValue < 0.0f);
			setBinding(new JoystickAxisBinding(controller, axis, reverse));
		}
	}

	public void onButton(Joystick controller, int button, boolean pressed) {
		if (pressed) {
			setBinding(new JoystickButtonBinding(controller, button));
		}
	}
	
	public synchronized void setBinding(Binding binding) {
		if (hasBeenSet) return;
		hasBeenSet = true;
		
		field.getControl().replace(field.getBinding(), binding);
		field.setBinding(binding);
		
		MouseInput.get().setCursorVisible(true);
		KeyInput.get().removeListener(this);
		MouseInput.get().removeListener(this);
		JoystickInput.get().removeListener(this);
	}
}
