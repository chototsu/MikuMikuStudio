/* 
* Copyright (c) 2004, jMonkeyEngine - Mojo Monkey Coding 
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
package com.jme.widget.viewport;

import com.jme.input.AbstractInputHandler;
import com.jme.renderer.Camera;

/**
 * <code>WidgetViewportCameraController</code>
 * @author Gregg Patton
 * @version $Id: WidgetViewportCameraController.java,v 1.3 2004-03-27 17:43:40 greggpatton Exp $
 */
public class WidgetViewportCameraController {
    Camera camera;
    AbstractInputHandler inputHandler;

    public WidgetViewportCameraController(Camera camera, AbstractInputHandler inputHandler) {

        this.camera = camera;
        this.inputHandler = inputHandler;

        if (this.inputHandler != null)
            this.inputHandler.setCamera(camera);

    }

    /**
     * <code>getCamera</code>
     * @return
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * <code>setCamera</code>
     * @param camera
     */
    public void setCamera(Camera camera) {
        this.camera = camera;
        if (this.camera != null && inputHandler != null)
            inputHandler.setCamera(this.camera);
    }

    /**
     * <code>getInputHandler</code>
     * @return
     */
    public AbstractInputHandler getInputHandler() {
        return inputHandler;
    }

    /**
     * <code>setInputHandler</code>
     * @param controller
     */
    public void setInputHandler(AbstractInputHandler handler) {
        inputHandler = handler;

        if (this.camera != null && inputHandler != null)
            inputHandler.setCamera(this.camera);
    }

}
