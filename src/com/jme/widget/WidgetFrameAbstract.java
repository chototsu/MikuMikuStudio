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
package com.jme.widget;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;

import com.jme.input.InputControllerAbstract;
import com.jme.input.MouseInput;
import com.jme.renderer.Renderer;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;
import com.jme.util.Timer;
import com.jme.widget.bounds.WidgetViewport;
import com.jme.widget.font.WidgetFontManager;
import com.jme.widget.input.mouse.WidgetMouseButtonType;
import com.jme.widget.util.WidgetFrameRate;

/**
 * @author Gregg Patton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class WidgetFrameAbstract extends WidgetContainerAbstract implements Observer {

    private static Timer timer;
    private static WidgetFrameRate frameRate;
    private static DisplaySystem displaySystem;

    public WidgetFrameAbstract(DisplaySystem ds, InputControllerAbstract ic, Timer timer) {
        super();

        displaySystem = ds;

        WidgetFrameAbstract.timer = timer;

        frameRate = new WidgetFrameRate(timer);

        setInputController(ic);
        
        init();
    }

    public static DisplaySystem getDisplaySystem() {
        if (displaySystem == null) {
            noInitMsg();
        }

        return displaySystem;
    }

    public static WidgetFrameRate getFrameRate() {

        if (frameRate == null) {
            noInitMsg();
        }

        return frameRate;
    }

    public static Timer getTimer() {

        if (timer == null) {
            noInitMsg();
        }

        return timer;
    }

    protected static void noInitMsg() {
        String msg = "WidgetFrameAbstract is not initialized.";
        LoggingSystem.getLogger().log(Level.WARNING, msg);
        throw new JmeException(msg);
    }

    public void init() {

        WidgetFontManager.init(displaySystem);

        setViewport(new WidgetViewport(0, 0, displaySystem.getWidth(), displaySystem.getHeight()));

        setSize(displaySystem.getWidth(), displaySystem.getHeight());

        doLayout();

    }

    public static void destroy() {
    }

    public void update(Observable o, Object arg) {
    }


    public void handleInput() {
        handleInput(true, 0);
    }
    
    public void handleInput(boolean updateController) {
        handleInput(updateController, 0);
    }
            
    public void handleInput(float time) {
        handleInput(true, time);
    }
    
    public void handleInput(boolean updateController, float time) {
        
        if (updateController) {
            getInputController().update(time);
        }
        
        handleMouse();
        handleKeyboard();
    }

    protected void handleKeyboard() {
        //super.handleKeyboard();
    }

    protected void handleMouse() {
        MouseInput mi = getMouseInput();

        WidgetMouseButtonType buttonType = mi.getButtonState();
        WidgetMouseButtonType lastButtonType = mi.getPreviousButtonState();

        if (lastButtonType != buttonType) {

            if (buttonType != WidgetMouseButtonType.MOUSE_BUTTON_NONE) {

                handleMouseButtonDown();

            } else {

                handleMouseButtonUp();

            }
        }

        if (buttonType == null && (mi.getXDelta() != 0 || mi.getYDelta() != 0)) {

            handleMouseMove();

        } else if (buttonType != null && (mi.getXDelta() != 0 || mi.getYDelta() != 0)) {

            handleMouseDrag();
        }

        lastButtonType = buttonType;
    }

    public void doMouseButtonDown() {
        Widget mouseOwner = getMouseOwner();

        if (mouseOwner != null && mouseOwner != this) {
            mouseOwner.doMouseButtonDown();
        }
    }

    public void handleMouseButtonDown() {
        super.handleMouseButtonDown();
        doMouseButtonDown();
    }

    public void doMouseButtonUp() {
        Widget mouseOwner = getMouseOwner();

        if (mouseOwner != null && mouseOwner != this) {
            mouseOwner.doMouseButtonUp();
        }
    }

    public void handleMouseButtonUp() {
        super.handleMouseButtonUp();
        doMouseButtonUp();
    }

    public void doMouseDrag() {
        Widget mouseOwner = getMouseOwner();

        if (mouseOwner != null && mouseOwner != this) {
            mouseOwner.doMouseDrag();
        }
    }

    public void handleMouseDrag() {
        super.handleMouseDrag();
        doMouseDrag();
    }

    public void handleMouseEnter() {
        super.handleMouseEnter();
        doMouseEnter();
    }

    public void handleMouseExit() {
        super.handleMouseExit();
        doMouseExit();
    }

    public void handleMouseMove() {
        super.handleMouseMove();
        doMouseMove();
    }

    public void onDraw(Renderer r) {
        super.onDraw(r);

        timer.update();
    }

    public void draw(Renderer r) {
        if (isVisible() == false)
            return;

        super.draw(r);

        r.getCamera().update();
    }

}
