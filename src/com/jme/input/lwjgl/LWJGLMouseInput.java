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
package com.jme.input.lwjgl;

import java.util.logging.Level;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Window;

import com.jme.input.MouseButtonStateType;
import com.jme.input.MouseInput;
import com.jme.util.LoggingSystem;
import com.jme.widget.impl.lwjgl.WidgetLWJGLStandardCursor;

/**
 * <code>LWJGLMouseInput</code> handles mouse input via the LWJGL Input API.
 *
 * @author Mark Powell
 * @version $Id: LWJGLMouseInput.java,v 1.3 2004-04-13 23:33:49 renanse Exp $
 */
public class LWJGLMouseInput implements MouseInput {

    private MouseButtonStateType buttonType = MouseButtonStateType.MOUSE_BUTTON_NONE;
    private MouseButtonStateType previousButtonType = MouseButtonStateType.MOUSE_BUTTON_NONE;

    private int dx, dy;

    /**
     * Constructor creates a new <code>LWJGLMouseInput</code> object. A call
     * to the LWJGL creation method is made, if any problems occur during
     * this creation, it is logged.
     *
     */
    public LWJGLMouseInput() {
        try {
            Mouse.create();
            setCursorVisible(false);
        } catch (Exception e) {
            LoggingSystem.getLogger().log(Level.WARNING, "Problem during " + "creation of Mouse.");
        }
    }
    /**
     * <code>destroy</code> cleans up the native mouse reference.
     * @see com.jme.input.MouseInput#destroy()
     */
    public void destroy() {
        setCursorVisible(false);
        Mouse.destroy();

    }

    /**
     * <code>getButtonIndex</code> returns the index of a given button name.
     * @see com.jme.input.MouseInput#getButtonIndex(java.lang.String)
     */
    public int getButtonIndex(String buttonName) {
        return Mouse.getButtonIndex(buttonName);
    }

    /**
     * <code>getButtonName</code> returns the name of a given button index.
     * @see com.jme.input.MouseInput#getButtonName(int)
     */
    public String getButtonName(int buttonIndex) {
        return Mouse.getButtonName(buttonIndex);
    }

    /**
     * <code>isButtonDown</code> tests if a given button is pressed or not.
     * @see com.jme.input.MouseInput#isButtonDown(int)
     */
    public boolean isButtonDown(int buttonCode) {
        return Mouse.isButtonDown(buttonCode);
    }

    /**
     * <code>isCreated</code> returns false if the mouse is created, false
     * otherwise.
     * @see com.jme.input.MouseInput#isCreated()
     */
    public boolean isCreated() {
        return Mouse.isCreated();
    }

    /**
     * <code>poll</code> gets the current state of the mouse.
     * @see com.jme.input.MouseInput#poll()
     */
    public void poll() {
      dx = Mouse.getDX();
      dy = Mouse.getDY();
    }

    /**
     * <code>getWheelDelta</code> retrieves the change of the mouse wheel,
     * if any.
     * @see com.jme.input.MouseInput#getWheelDelta()
     */
    public int getWheelDelta() {
        return Mouse.getDWheel();
    }
    /**
     * <code>getXDelta</code> retrieves the change of the x position, if any.
     * @see com.jme.input.MouseInput#getXDelta()
     */
    public int getXDelta() {
        return dx;
    }
    /**
     * <code>getYDelta</code> retrieves the change of the y position, if any.
     * @see com.jme.input.MouseInput#getYDelta()
     */
    public int getYDelta() {
        return dy;
    }

    /**
     * <code>getXAbsolute</code> gets the absolute x axis value.
     * @see com.jme.input.MouseInput#getXAbsolute()
     */
    public int getXAbsolute() {
        return Mouse.getX();
    }

    /**
     * <code>getYAbsolute</code> gets the absolute y axis value.
     * @see com.jme.input.MouseInput#getYAbsolute()
     */
    public int getYAbsolute() {
        return Mouse.getY();
    }

    /**
     * <code>updateState</code> updates the mouse state.
     * @see com.jme.input.MouseInput#updateState()
     */
    public void updateState() {
        poll();

        setButtonStateType();
    }

    private void setButtonStateType() {
        int button = 0;

        previousButtonType = buttonType;

        for (int i = 0; i < Mouse.getButtonCount(); i++) {
            if (Mouse.isButtonDown(i)) {
                switch (i) {
                    case 0 :
                        button |= BUTTON_1;
                        break;
                    case 1 :
                        button |= BUTTON_2;
                        break;
                    case 2 :
                        button |= BUTTON_3;
                        break;
                }
            }
        }

        switch (button) {
            case 0 :
                buttonType = MouseButtonStateType.MOUSE_BUTTON_NONE;
                break;
            case BUTTON_1 :
                buttonType = MouseButtonStateType.MOUSE_BUTTON_1;
                break;
            case BUTTON_2 :
                buttonType = MouseButtonStateType.MOUSE_BUTTON_2;
                break;
            case BUTTON_3 :
                buttonType = MouseButtonStateType.MOUSE_BUTTON_3;
                break;
            case BUTTON_1_2 :
                buttonType = MouseButtonStateType.MOUSE_BUTTON_1_2;
                break;
            case BUTTON_1_3 :
                buttonType = MouseButtonStateType.MOUSE_BUTTON_1_3;
                break;
            case BUTTON_2_3 :
                buttonType = MouseButtonStateType.MOUSE_BUTTON_2_3;
                break;
            case BUTTON_1_2_3 :
                buttonType = MouseButtonStateType.MOUSE_BUTTON_1_2_3;
                break;
        }

    }


    /**
     * <code>setCursorVisible</code> sets the visiblity of the hardware cursor.
     * @see com.jme.input.MouseInput#setCursorVisible(boolean)
     */
    public void setCursorVisible(boolean v) {
      Mouse.setGrabbed(!v);
        try {

            if (v) {
                Mouse.setNativeCursor(WidgetLWJGLStandardCursor.cursor);
            } else {
                Mouse.setNativeCursor(null);
            }

        } catch (Exception e) {
            LoggingSystem.getLogger().log(Level.WARNING, "Problem showing mouse cursor.");
        }
    }

    /**
     * <code>isCursorVisible</code>
     * @see com.jme.input.MouseInput#isCursorVisible()
     */
    public boolean isCursorVisible() {
        return Mouse.getNativeCursor() != null;
    }

    /**
     * @return
     */
    public MouseButtonStateType getButtonType() {
        return buttonType;
    }

    /**
     * @return the state of the mouse buttons.
     * @see com.jme.input.MouseInput#getButtonState()
     */
    public MouseButtonStateType getButtonState() {
        return buttonType;
    }

    /**
     * @return the previous state of the mouse buttons.
     * @see com.jme.input.MouseInput#getPreviousButtonState()
     */
    public MouseButtonStateType getPreviousButtonState() {
        return previousButtonType;
    }

}
