/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding
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

import com.jme.math.Vector2f;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;
import com.jme.widget.Widget;
import com.jme.widget.WidgetAlignmentType;
import com.jme.widget.WidgetAbstractContainer;
import com.jme.widget.WidgetInsets;
import com.jme.widget.bounds.WidgetViewRectangle;
import com.jme.widget.button.WidgetButtonStateType;
import com.jme.widget.layout.WidgetBorderLayout;
import com.jme.widget.layout.WidgetBorderLayoutConstraint;
import com.jme.widget.layout.WidgetLayoutManager;
import com.jme.widget.panel.WidgetScrollPanel;
import com.jme.widget.text.WidgetText;
import com.jme.widget.util.WidgetNotifier;

/**
 * <code>WidgetRolloutPanel</code>
 * @author Gregg Patton
 * @version $Id: WidgetRolloutPanel.java,v 1.5 2004-09-14 21:52:16 mojomonkey Exp $
 */
public class WidgetRolloutPanel extends WidgetAbstractContainer implements Observer {

    private static final long serialVersionUID = 1L;
	private static final String PLUS = "+";
    private static final String MINUS = "-";

    WidgetRolloutPanelButton button;

    protected WidgetScrollPanel panel;
    protected boolean expanded = false;
    protected WidgetText textPlusMinus;

    protected boolean expanding = false;
    protected boolean contracting = false;

    private float rolledUpY;

    private boolean panelEmpty = true;

    private WidgetNotifier rollUpNotifier = new WidgetNotifier();
    private WidgetNotifier rollDownNotifier = new WidgetNotifier();

    public WidgetRolloutPanel(String title) {
        super();

        setApplyOffsetX(false);

        super.setLayout(new WidgetBorderLayout());

        button = new WidgetRolloutPanelButton(title, WidgetAlignmentType.ALIGN_WEST);
        button.setInsets(new WidgetInsets(4, 4, 4, 4));
        //button.setBorder(new WidgetBorder(5,5,5,5, WidgetBorderType.RAISED));

        button.addMouseButtonUpObserver(this);

        super.add(button, WidgetBorderLayoutConstraint.NORTH);

        textPlusMinus = new WidgetText(WidgetRolloutPanel.PLUS);
        textPlusMinus.setAlignment(WidgetAlignmentType.ALIGN_EAST);
        textPlusMinus.setVisible(true);
        textPlusMinus.setCantOwnMouse(true);
        textPlusMinus.setApplyOffsetX(false);

        button.add(textPlusMinus);

        panel = new WidgetScrollPanel();
        panel.setVisible(false);

        super.add(panel, WidgetBorderLayoutConstraint.CENTER);

        setPanelEmpty();

    }

    public void doMouseButtonDown() {
        button.doMouseButtonDown();
    }

    public void doMouseButtonUp() {
        button.doMouseButtonUp();
    }

    private void setPanelEmpty() {
        panel.removeAll();
        addPanelWidget(new WidgetText("Empty", WidgetAlignmentType.ALIGN_CENTER));
        panelEmpty = true;
    }

    private void setPanelNotEmpty() {
        panel.removeAll();
        panelEmpty = false;
    }

    private void rollDown() {

        textPlusMinus.setText(WidgetRolloutPanel.MINUS);
        expanded = true;

        panel.setVisible(true);

        panel.getLayout().setMaximumWidth(button.getWidth());

        this.rollDownNotifier.notifyObservers(this);
    }

    private void rollUp() {
        textPlusMinus.setText(WidgetRolloutPanel.PLUS);

        expanded = false;

        panel.setVisible(false);

        this.rollUpNotifier.notifyObservers(this);
    }

    public void update(Observable o, Object arg) {
        if (arg == button
            && ((WidgetRolloutPanelButton) arg).getButtonState() == WidgetButtonStateType.BUTTON_UP) {
            if (expanded == true) {

                rollUp();

            } else {

                rollDown();

            }
        }
    }

    public String getTitle() {
        return button.getTitle();
    }

    public void setTitle(String title) {
        button.setTitle(title);
    }

    public void add(Widget w, Object constraints) {
        LoggingSystem.getLogger().log(Level.WARNING, "Use addPanelWidget method");
        throw new JmeException("Use addPanelWidget method");
    }

    public void add(Widget w) {
        LoggingSystem.getLogger().log(Level.WARNING, "Use addPanelWidget method");
        throw new JmeException("Use addPanelWidget method");
    }

    /** <code>remove</code>
     * @param w
     * @see com.jme.widget.WidgetAbstractContainer#remove(int)
     */
    public void remove(int w) {
        LoggingSystem.getLogger().log(Level.WARNING, "Use removePanelWidget method");
        throw new JmeException("Use removePanelWidget method");
    }

    /** <code>remove</code>
     * @param w
     * @see com.jme.widget.WidgetAbstractContainer#remove(com.jme.widget.Widget)
     */
    public void remove(Widget w) {
        LoggingSystem.getLogger().log(Level.WARNING, "Use removePanelWidget method");
        throw new JmeException("Use removePanelWidget method");
    }

    /** <code>removeAll</code>
     *
     * @see com.jme.widget.WidgetAbstractContainer#removeAll()
     */
    public void removeAll() {
        LoggingSystem.getLogger().log(Level.WARNING, "Use removeAllPanelWidgets method");
        throw new JmeException("Use removeAllPanelWidgets method");
    }

    public void addPanelWidget(Widget w, Object constraints) {
        if (panelEmpty == true)
            setPanelNotEmpty();

        panel.add(w, constraints);
    }

    public void addPanelWidget(Widget w) {
        if (panelEmpty == true)
            setPanelNotEmpty();

        panel.add(w);
    }

    public WidgetInsets getPanelInsets() {
        return panel.getInsets();
    }

    public void setPanelInsets(WidgetInsets insets) {
        panel.setInsets(insets);
    }

    public Widget getPanelWidget(int n) {
        return panel.getWidget(n);
    }

    public int getPanelWidgetCount() {
        if (panel != null)
            return panel.getWidgetCount();
        return 0;

    }

    public void removePanelWidget(int w) {
        panel.remove(w);

        if (panel.getWidgetCount() == 0)
            setPanelEmpty();
    }

    public void removePanelWidget(Widget w) {
        panel.remove(w);

        if (panel.getWidgetCount() == 0)
            setPanelEmpty();
    }
    public void removeAllPanelWidgets() {
        setPanelEmpty();
    }

    public WidgetLayoutManager getLayout() {
        LoggingSystem.getLogger().log(Level.WARNING, "Use getPanelLayout method");
        throw new JmeException("Use getPanelLayout method");
    }

    public void setLayout(WidgetLayoutManager layout) {
        LoggingSystem.getLogger().log(Level.WARNING, "Use setPanelLayout method");
        throw new JmeException("Use setPanelLayout method");
    }

    public WidgetLayoutManager getPanelLayout() {
        return panel.getLayout();
    }

    public void setPanelLayout(WidgetLayoutManager layout) {
        panel.setLayout(layout);
    }

    public boolean isShowVScroll() {
        return panel.isShowVScroll();
    }

    public void setShowVScroll(boolean b) {
        panel.setShowVScroll(b);
    }

    public boolean isShowHScroll() {
        return panel.isShowHScroll();
    }

    public void setShowHScroll(boolean b) {
        panel.setShowHScroll(b);
    }

    public void addRollUpObserver(Observer o) {
        this.rollUpNotifier.addObserver(o);
    }

    public void deleteRollUpObserver(Observer o) {
        this.rollUpNotifier.deleteObserver(o);
    }

    public void addRollDownObserver(Observer o) {
        this.rollDownNotifier.addObserver(o);
    }

    public void deleteRollDownObserver(Observer o) {
        this.rollDownNotifier.deleteObserver(o);
    }

    public Vector2f getPreferredSize() {

        Vector2f ret = super.getPreferredSize();

        if (getOwner() != null) {

            float ownerW = getOwner().getWidth();

            float panelW = panel.getPreferredSize().x;

            ret.x = panelW > ownerW ? panelW : ownerW;

        }

        if (ret.x < button.getText().getWidth() + textPlusMinus.getWidth())
            ret.x = (button.getText().getWidth() + (textPlusMinus.getWidth() * 2));

        return ret;
    }

    public void setSize(int width, int height) {
        super.setSize(width, height);

        if (getOwner() != null) {

            float ownerW = getOwner().getWidth();

            if (ownerW != 0) {

                button.setRolloutPanelWidth((int) ownerW);

            } else
                button.setRolloutPanelWidth(width);

        } else {

            button.setRolloutPanelWidth(width);
        }

    }

    public void setSize(Vector2f size) {
        setSize((int) size.x, (int) size.y);

    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#setOwner(jme.widget.Widget)
     */
    public void setOwner(Widget owner) {
        button.setOwner(owner);
        panel.setOwner(owner);
        super.setOwner(owner);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#setViewport(jme.widget.bounds.WidgetViewRectangle)
     */
    public void setViewRectangle(WidgetViewRectangle viewport) {
        WidgetViewRectangle v = new WidgetViewRectangle(viewport);

        if (getOwner() != null)
            v.setWidth(getOwner().getViewRectangle().getWidth());

        super.setViewRectangle(v);

    }
    public String toString() {
        return "[" + getTitle() + super.toString() + "]";
    }

    /** <code>initWidgetRenderer</code>
     *
     * @see com.jme.widget.Widget#initWidgetRenderer()
     */
    public void initWidgetRenderer() {}

}
