/*
 * Copyright (c) 2004, Gregg Patton
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package com.jme.widget.input.mouse;

import com.jme.app.SimpleGame;
import com.jme.input.InputControllerAbstract;
import com.jme.input.InputSystem;
import com.jme.input.KeyInput;
import com.jme.input.RelativeMouse;
import com.jme.input.action.KeyExitAction;
import com.jme.renderer.Camera;

/**
 * <code>WidgetMouseController</code>
 * @author Gregg Patton
 * @version
 */
public class WidgetMouseTestControllerBasic extends InputControllerAbstract {

    /**
     * @param app
     * @param cam
     * @param api
     */
    public WidgetMouseTestControllerBasic(SimpleGame app, Camera cam, String api) {
        super(app, cam, api);
    }

    /* (non-Javadoc)
     * @see com.jme.input.InputControllerAbstract#setKeyBindings(java.lang.String)
     */
    protected void setKeyBindings(String api) {
        super.setKeyBindings(api);

        keyboard.set("exit", KeyInput.KEY_ESCAPE);

    }

    /* (non-Javadoc)
     * @see com.jme.input.InputControllerAbstract#setMouse(com.jme.renderer.Camera)
     */
    protected void setMouse(Camera cam) {
        RelativeMouse mouse = new RelativeMouse();
        mouse.setMouseInput(InputSystem.getMouseInput());
        
        mouse.getMouseInput().setCursorVisible(true);
        
        setMouse(mouse);
    }

    /* (non-Javadoc)
     * @see com.jme.input.InputControllerAbstract#setActions(com.jme.renderer.Camera, com.jme.app.SimpleGame)
     */
    protected void setActions(Camera cam, SimpleGame app) {
        KeyExitAction exit = new KeyExitAction(app);
        exit.setKey("exit");
        addAction(exit);
    }

}
