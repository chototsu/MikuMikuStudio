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
 
/*
 * EDIT:  02/08/2004 - Added update(boolean updateState) to allow for a 
 *                      WidgetViewport to update an AbstractInputController 
 *                      without polling the mouse.  GOP
 */
 
package com.jme.input;

/**
 * <code>AbsoluteMouse</code> defines a mouse object that maintains a position
 * within the window. Each call to update adjusts the current position by the
 * change in position since the previous update. The mouse is forced to be
 * contained within the values provided during construction (typically these
 * correspond to the width and height of the window).
 * @author Mark Powell
 * @author Gregg Patton
 * @version $Id: AbsoluteMouse.java,v 1.2 2004-02-09 11:51:28 greggpatton Exp $
 */
public class AbsoluteMouse extends Mouse {
    //position
    private int width, height;
    
    /**
     * Constructor instantiates a new <code>AbsoluteMouse</code> object. The
     * limits of the mouse movements are provided.
     * @param width the width of the mouse's limit.
     * @param height the height of the mouse's limit.
     */
    public AbsoluteMouse(int width, int height) {
        super();
        this.width = width;
        this.height = height;
    }
    
    /**
     * <code>update</code> sets the mouse's current position within the window.
     */
    public void update() {
        update(true);
    }

    /** <code>update</code> 
     * @param updateState
     * @see com.jme.input.Mouse#update(boolean)
     */
    public void update(boolean updateState) {
        if (updateState)
            mouse.poll();
            
        localTranslation.x += mouse.getXDelta();
        localTranslation.y += mouse.getYDelta();

        if (localTranslation.x < 0) {
            localTranslation.x = 0;
        } else if (localTranslation.x > width) {
            localTranslation.x = width;
        }

        if (localTranslation.y
            < 0 - imageHeight) {
            localTranslation.y = 0 - imageHeight;
        } else if (
            localTranslation.y
                > height - imageHeight) {
            localTranslation.y =
                height - imageHeight;
        }
    }
}
