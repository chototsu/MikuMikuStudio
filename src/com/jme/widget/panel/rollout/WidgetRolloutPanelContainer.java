/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this 
 * list of conditions and the following disclaimer. 
 * 
 * Redistributions in binary form must reproduce the above copyright notice, 
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution. 
 * 
 * Neither the name of the Mojo Monkey Coding, jME, jMonkey Engine, nor the 
 * names of its contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.jme.widget.panel.rollout;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;

import com.jme.util.LoggingSystem;
import com.jme.widget.Widget;
import com.jme.widget.WidgetAlignmentType;
import com.jme.widget.layout.WidgetFlowLayout;
import com.jme.widget.layout.WidgetLayoutManager;
import com.jme.widget.panel.WidgetScrollPanel;

/**
 * @author Gregg Patton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WidgetRolloutPanelContainer extends WidgetScrollPanel implements Observer {

    public WidgetRolloutPanelContainer() {
        super();

        init();
    }

    public WidgetRolloutPanelContainer(int width, int height) {
        super(width, height);

        init();
    }

    private void init() {

		super.setLayout(new WidgetFlowLayout(WidgetAlignmentType.ALIGN_WEST, 0, 5));

    }

    public void doMouseButtonDown() {}

    public void doMouseButtonUp() {}

    public void add(Widget w, Object constraints) {
		LoggingSystem.getLogger().log(Level.WARNING, "Widget cannot be added with constraints");
    }

    public void add(Widget w) {
    	if (w instanceof WidgetRolloutPanel) {
			WidgetRolloutPanel rp = (WidgetRolloutPanel)w;

			rp.setOwner(panel);

			rp.setShowVScroll(false);
			rp.setShowHScroll(false);
			
			rp.addRollUpObserver(this);
			rp.addRollDownObserver(this);
			
			super.add(w);
			
    	} else {
			LoggingSystem.getLogger().log(Level.WARNING, "Widgets must be of type RolloutPanel.");
    	}
    	
    }

    public void update(Observable o, Object arg) {

        if (arg instanceof WidgetRolloutPanel) {
            doLayout();
        } else {

			super.update(o, arg);

        }

    }

    public void remove(int i) {
        Widget w = getWidget(i);
        remove(w);
    }

    public void remove(Widget w) {

        if (w != null && w instanceof WidgetRolloutPanel) {
            ((WidgetRolloutPanel) w).deleteRollUpObserver(this);
            ((WidgetRolloutPanel) w).deleteRollDownObserver(this);
        }

        super.remove(w);
    }

    public void removeAll() {
        for (int i = 0; i < this.getWidgetCount(); i++) {
            remove(i);
        }
    }

    public void setLayout(WidgetLayoutManager layout) {
		LoggingSystem.getLogger().log(Level.WARNING, "WidgetLayoutManager cannot be modified.");
    }

}
