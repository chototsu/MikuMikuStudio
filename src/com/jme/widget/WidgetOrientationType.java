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
package com.jme.widget;

import com.jme.util.JmeType;

/**
 * <code>WidgetOrientationType</code>
 * @author Gregg Patton
 * @version $Id: WidgetOrientationType.java,v 1.3 2004-04-22 22:27:12 renanse Exp $
 */
public class WidgetOrientationType extends JmeType {

    public final static WidgetOrientationType NONE =
        new WidgetOrientationType("NONE");

    public final static WidgetOrientationType HORIZONTAL =
        new WidgetOrientationType("HORIZONTAL");
    public final static WidgetOrientationType VERTICAL =
        new WidgetOrientationType("VERTICAL");

    public final static WidgetOrientationType UP =
        new WidgetOrientationType("UP");
    public final static WidgetOrientationType DOWN =
        new WidgetOrientationType("DOWN");
    public final static WidgetOrientationType LEFT =
        new WidgetOrientationType("LEFT");
    public final static WidgetOrientationType RIGHT =
        new WidgetOrientationType("RIGHT");

    private WidgetOrientationType(String name) {
        super(name);
    }

    /** <code>getType</code>
     * @param name String representation of JmeType
     * @return the widget type associated with name.
     * @see com.jme.widget.util.JmeType#getType(java.lang.String)
     */
    public JmeType getType(String name) {
        JmeType type = null;

        if (NONE.name.equals(name)) {

            type = NONE;

        } else if (HORIZONTAL.name.equals(name)) {

            type = HORIZONTAL;

        } else if (VERTICAL.name.equals(name)) {

            type = VERTICAL;

        } else if (UP.name.equals(name)) {

            type = UP;

        } else if (DOWN.name.equals(name)) {

            type = DOWN;

        } else if (LEFT.name.equals(name)) {

            type = LEFT;

        } else if (RIGHT.name.equals(name)) {

            type = RIGHT;
        }

        return type;
    }

}
