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

/*
 * EDIT:  02/08/2004 - Added update(boolean updateState) to allow for a
 *                      WidgetViewport to update an AbstractInputHandler
 *                      without polling the mouse.  GOP
 */


package com.jme.input;

/**
 * <code>RelativeMouse</code> defines a mouse controller that only maintains
 * the relative change from one poll to the next. This does not maintain the
 * position of a mouse in a rendering window. This type of controller is
 * typically useful for a first person mouse look or similar.
 * @author Mark Powell
 * @version $Id: RelativeMouse.java,v 1.11 2004-07-30 21:20:23 cep21 Exp $
 */
public class RelativeMouse extends Mouse {
    /**
     * Constructor creates a new <code>RelativeMouse</code> object.
     * @param name the name of the scene element. This is required for identification and
     * 		comparision purposes.
     */
    public RelativeMouse(String name) {
        super(name);
    }

    /**
     * <code>update</code> sets the translation vector to the change from the
     * previous polling.
     */
    public void update() {
        update(true);
    }

    /** <code>update</code> updates the mouse's position by simply adding to the current location
     * the mouse's X and Y movement delta.  Unlike AbsoluteMouse, no checks are made for moving
     * outside a paticular bounds because this class is used only for frame to frame
     * relative movements.
     * @param updateState
     * @see com.jme.input.Mouse#update(boolean)
     */
    public void update(boolean updateState) {
        if (updateState)
            mouse.updateState();

        localTranslation.x = mouse.getXDelta() * _speed;
        localTranslation.y = mouse.getYDelta() * _speed;
        worldTranslation.set(localTranslation);
        _hotSpotLocation.set(localTranslation).addLocal(_hotSpotOffset);
    }
}
