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

import java.util.logging.Level;

import jme.entity.Entity;
import jme.system.KeyBindingManager;
import jme.utility.LoggingSystem;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * <code>BasicController</code> defines a very basic controller that
 * only purpose is to provide an exit method. By pressing the escape
 * key the use is able to exit the system. It also maintains the 
 * Entity for rendering, that is, if the entity is a Camera, setting 
 * the viewport.
 * 
 * @author Mark Powell
 */
public class BasicController extends AbstractGameController {
    protected KeyBindingManager key;

    public BasicController(Entity entity) {
        this.entity = entity;
        try {
            Mouse.create();
            Keyboard.create();
        } catch (Exception e) {
            e.printStackTrace();
            LoggingSystem.getLoggingSystem().getLogger().log(
                Level.WARNING,
                "Error creating Mouse and/or Keyboard");
        }

        setDefaultKeyBindings();
        LoggingSystem.getLoggingSystem().getLogger().log(
            Level.INFO,
            "Created game controller");

    }

    /**
     * <code>update</code> checks if the exit key has been pressed,
     * if not continues.
     * @return true if exit has NOT been pressed, false otherwise.
     */
    public boolean update(float frameRate) {
        Keyboard.poll();
        if (isKeyDown("exit")) {
            return false;
        }

        return checkAdditionalKeys();
 
    }
    
    /**
     * <code>render</code> renders the entity the controller is controlling.
     */
    public void render() {
        entity.render();
    }
    
    /**
     * <code>checkAdditionalKeys</code> always returns true.
     * @return true
     */
    protected boolean checkAdditionalKeys() {
        return true;
    }
    
    /**
     * <code>setDefaultKeyBindings</code> sets the exit key to escape.
     */
    private void setDefaultKeyBindings() {
        key = KeyBindingManager.getKeyBindingManager();
        key.set("exit", Keyboard.KEY_ESCAPE);
    }

}
