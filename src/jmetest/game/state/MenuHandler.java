/*
 * Copyright (c) 2003-2005 jMonkeyEngine
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

package jmetest.game.state;

import com.jme.input.AbsoluteMouse;
import com.jme.input.InputHandler;
import com.jme.input.InputSystem;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.system.DisplaySystem;

/**
 * The input handler we use to navigate the menu. E.g. has an absolute mouse.
 * If the escape key is pressed the application will be ended using the static
 * exit method of TestGameStateSystem.
 * 
 * @author Per Thulin
 */
public class MenuHandler extends InputHandler {	
	
	public MenuHandler() {
        setKeyBindings();
        setUpMouse();
    }

    private void setKeyBindings() {
        keyboard = KeyBindingManager.getKeyBindingManager();
        keyboard.setKeyInput(KeyInput.get());
        keyboard.set("exit", KeyInput.KEY_ESCAPE);
    }

    private void setUpMouse() {
		DisplaySystem display = DisplaySystem.getDisplaySystem();
        mouse = new AbsoluteMouse("Mouse Input", display.getWidth(),
        		display.getHeight());
        mouse.setMouseInput( MouseInput.get());
        setMouse(mouse);
    }
    
    public void update(float tpf) {
    	super.update(tpf);
		if (keyboard.isValidCommand("exit", false)) {
			TestGameStateSystem.exit();
		}
    }
    
}