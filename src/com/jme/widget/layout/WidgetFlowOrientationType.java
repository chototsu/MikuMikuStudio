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
package com.jme.widget.layout;

import com.jme.util.JmeType;

/**
 * <code>WidgetFlowOrientationType</code>
 * @author Gregg Patton
 * @version $Id: WidgetFlowOrientationType.java,v 1.1 2004-03-04 03:29:11 greggpatton Exp $
 */
public class WidgetFlowOrientationType extends JmeType {

    public final static WidgetFlowOrientationType TOP_LEFT_TO_RIGHT =
        new WidgetFlowOrientationType("TOP_LEFT_TO_RIGHT");
    public final static WidgetFlowOrientationType TOP_RIGHT_TO_LEFT =
        new WidgetFlowOrientationType("TOP_RIGHT_TO_LEFT");

    public final static WidgetFlowOrientationType BOTTOM_LEFT_TO_RIGHT =
        new WidgetFlowOrientationType("BOTTOM_LEFT_TO_RIGHT");
    public final static WidgetFlowOrientationType BOTTOM_RIGHT_TO_LEFT =
        new WidgetFlowOrientationType("BOTTOM_RIGHT_TO_LEFT");

    public final static WidgetFlowOrientationType LEFT_TOP_TO_BOTTOM =
        new WidgetFlowOrientationType("LEFT_TOP_TO_BOTTOM");
    public final static WidgetFlowOrientationType LEFT_BOTTOM_TO_TOP =
        new WidgetFlowOrientationType("LEFT_BOTTOM_TO_TOP");

    public final static WidgetFlowOrientationType RIGHT_TOP_TO_BOTTOM =
        new WidgetFlowOrientationType("RIGHT_TOP_TO_BOTTOM");
    public final static WidgetFlowOrientationType RIGHT_BOTTOM_TO_TOP =
        new WidgetFlowOrientationType("RIGHT_BOTTOM_TO_TOP");

    /**
     * @param name
     */
    private WidgetFlowOrientationType(String name) {
        super(name);
    }

    /** <code>getType</code> 
     * @param name
     * @return
     * @see com.jme.util.JmeType#getType(java.lang.String)
     */
    public JmeType getType(String name) {
        JmeType type = null;

        if (TOP_LEFT_TO_RIGHT.name.equals(name)) {

            type = TOP_LEFT_TO_RIGHT;

        } else if (TOP_RIGHT_TO_LEFT.name.equals(name)) {

            type = TOP_RIGHT_TO_LEFT;

        } else if (BOTTOM_LEFT_TO_RIGHT.name.equals(name)) {

            type = BOTTOM_LEFT_TO_RIGHT;

        } else if (BOTTOM_RIGHT_TO_LEFT.name.equals(name)) {

            type = BOTTOM_RIGHT_TO_LEFT;

        } else if (LEFT_TOP_TO_BOTTOM.name.equals(name)) {

            type = LEFT_TOP_TO_BOTTOM;

        } else if (LEFT_BOTTOM_TO_TOP.name.equals(name)) {

            type = LEFT_BOTTOM_TO_TOP;

        } else if (RIGHT_TOP_TO_BOTTOM.name.equals(name)) {

            type = RIGHT_TOP_TO_BOTTOM;

        } else if (RIGHT_BOTTOM_TO_TOP.name.equals(name)) {

            type = RIGHT_BOTTOM_TO_TOP;

        }

        return type;
    }

}
