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

package jme.controller;

import jme.entity.Entity;
import jme.system.KeyBindingManager;

import org.lwjgl.Display;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.vector.Vector3f;

/**
 * <code>AbstractGameController</code> defines common keyboard, mouse and
 * camera behavior. It provides some convienience methods for handling 
 * mouse input, keyboard input and camera control as defined by 
 * <code>KeyboardController</code>, <code>MouseController</code> and
 * <code>EntityController</code>.
 * 
 * @author Mark Powell
 * @version 0.1.0
 */
public abstract class AbstractGameController
    implements KeyboardController, MouseController, EntityController {

    private int x, y;
    private int absX, absY;

    protected Entity entity;

    /**
     * <code>pollMouse</code> updates the mouse position and the absolute 
     * mouse position.
     * 
     * @see jme.controller.MouseController#pollMouse()
     */
    public void pollMouse() {
        Mouse.poll();

        //If the mouse has not moved, don't bother updating it.
        if (Mouse.dx != 0 || Mouse.dy != 0) {

            x += Mouse.dx;
            y += Mouse.dy;

            //Keep values within the limits of the window.
            if (x < 0) {
                absX = x;
                x = 0;
            } else if (x >= Display.getWidth()) {
                absX = x - Display.getWidth();
                x = Display.getWidth() - 1;
            }

            if (y < 0) {
                absY = y;
                y = 0;
            } else if (y >= Display.getHeight()) {
                absY = y - Display.getWidth();
                y = Display.getHeight() - 1;
            }
        }
    }

    /**
     * @see jme.controller.KeyboardController#pollKeyboard()
     */
    public void pollKeyboard() {
        Keyboard.poll();
    }

    /**
     * @see jme.controller.KeyboardController#isKeyDown(Object)
     */
    public boolean isKeyDown(Object keyCode) {
        if (!(keyCode instanceof String)) {
            return false;
        }
        return KeyBindingManager.getKeyBindingManager().isValidCommand(
            (String)keyCode);
    }

    /**
     * @see jme.controller.KeyboardController#setBuffered()
     */
    public void setBuffered() {
        Keyboard.enableBuffer();
    }

    /**
     * @see jme.controller.KeyboardController#next()
     */
    public int next() {
        Keyboard.next();
        return (Keyboard.key);
    }

    /**
     * @see jme.controller.MouseController#getMousePositionX()
     */
    public int getMousePositionX() {
        return x;
    }

    /**
     * @see jme.controller.MouseController#getMousePositionY()
     */
    public int getMousePositionY() {
        return y;
    }

    /**
     * @see jme.controller.MouseController#getAbsoluteMousePositionX()
     */
    public int getAbsoluteMousePositionX() {
        return absX;
    }

    /**
     * @see jme.controller.MouseController#getAbsoluteMousePositionY()
     */
    public int getAbsoluteMousePositionY() {
        return absY;
    }

    /**
     * @see jme.controller.MouseController#getMouseDeltaX()
     */
    public int getMouseDeltaX() {
        return Mouse.dx;
    }

    /**
     * @see jme.controller.MouseController#getMouseDeltaY()
     */
    public int getMouseDeltaY() {
        return Mouse.dy;
    }

    /**
     * @see jme.controller.MouseController#setMousePosition(int, int)
     */
    public void setMousePosition(int x, int y) {
        this.x = Math.min(Math.max(0, x), Display.getWidth() - 1);
        this.y = Math.min(Math.max(0, y), Display.getHeight() - 1);
    }

    public int getNumberOfButtons() {
        return Mouse.buttonCount;
    }

    /**
     * @see jme.controller.MouseController#isButtonDown(int)
     */
    public boolean isButtonDown(int mouseButton) {
        return Mouse.isButtonDown(mouseButton);
    }

    /**
     * @see jme.controller.EntityController#setEntityPosition(Vector3f)
     */
    public void setEntityPosition(Vector3f position) {
        entity.setPosition(position);
    }
    
    /**
     * <code>setEntityYaw</code> sets the yaw of the controlled entity.
     * @param angle the yaw angle of the controlled entity.
     */
    public void setEntityYaw(float angle) {
        entity.setYaw(angle);
    }
	/**
	 * <code>setEntityRoll</code> sets the roll of the controlled entity.
	 * @param angle the roll angle of the controlled entity.
	 */
    public void setEntityRoll(float angle) {
        entity.setRoll(angle);
    }
    
    /**
     * <code>setEntityPitch</code> sets the pitch of the controlled entity.
     * @param angle the pitch angle of the controlled entity.
     */
    public void setEntityPitch(float angle) {
        entity.setPitch(angle);
    }

    /**
     * @see jme.controller.EntityController#getEntityPosition()
     */
    public Vector3f getEntityPosition() {
        return entity.getPosition();
    }
    
    /**
     * @see jme.controller.EntityController#render()
     */
    public abstract void render();
    /**
     * <code>update</code> defines a method that should be called each round
     * or frame. What occurs during this update is the responsiblity of
     * the subclass. A typical implementation would be to poll all devices,
     * process input, then update the state of the entity.
     * 
     * @param frameRate denotes the current speed of the rendering, to allow
     * 		for time based movements.
     */
    public abstract boolean update(float frameRate);
}
