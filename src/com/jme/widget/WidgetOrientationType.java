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
package com.jme.widget;

import com.jme.util.JmeType;

/**
 * <code>WidgetOrientationType</code>
 * @author Gregg Patton
 * @version $Id: WidgetOrientationType.java,v 1.1 2004-02-09 12:28:04 greggpatton Exp $
 */
public class WidgetOrientationType extends JmeType {

    public final static WidgetOrientationType ORIENTATION_NONE =
        new WidgetOrientationType("ORIENTATION_NONE");

    public final static WidgetOrientationType ORIENTATION_TOP_LEFT_TO_RIGHT =
        new WidgetOrientationType("ORIENTATION_TOP_LEFT_TO_RIGHT");
    public final static WidgetOrientationType ORIENTATION_TOP_RIGHT_TO_LEFT =
        new WidgetOrientationType("ORIENTATION_TOP_RIGHT_TO_LEFT");

    public final static WidgetOrientationType ORIENTATION_BOTTOM_LEFT_TO_RIGHT =
        new WidgetOrientationType("ORIENTATION_BOTTOM_LEFT_TO_RIGHT");
    public final static WidgetOrientationType ORIENTATION_BOTTOM_RIGHT_TO_LEFT =
        new WidgetOrientationType("ORIENTATION_BOTTOM_RIGHT_TO_LEFT");

    public final static WidgetOrientationType ORIENTATION_LEFT_TOP_TO_BOTTOM =
        new WidgetOrientationType("ORIENTATION_LEFT_TOP_TO_BOTTOM");
    public final static WidgetOrientationType ORIENTATION_LEFT_BOTTOM_TO_TOP =
        new WidgetOrientationType("ORIENTATION_LEFT_BOTTOM_TO_TOP");

    public final static WidgetOrientationType ORIENTATION_RIGHT_TOP_TO_BOTTOM =
        new WidgetOrientationType("ORIENTATION_RIGHT_TOP_TO_BOTTOM");
    public final static WidgetOrientationType ORIENTATION_RIGHT_BOTTOM_TO_TOP =
        new WidgetOrientationType("ORIENTATION_RIGHT_BOTTOM_TO_TOP");

    public WidgetOrientationType(String name) {
        super(name);
    }

    /** <code>getType</code> 
     * @param name String representation of JmeType
     * @return the widget type associated with name.
     * @see com.jme.widget.util.JmeType#getType(java.lang.String)
     */
    public JmeType getType(String name) {
        JmeType type = null;

        if (ORIENTATION_NONE.name.equals(name)) {
            
            type = ORIENTATION_NONE;
            
        } else if (ORIENTATION_TOP_LEFT_TO_RIGHT.name.equals(name)) {
            
            type = ORIENTATION_TOP_LEFT_TO_RIGHT;
            
        } else if (ORIENTATION_TOP_RIGHT_TO_LEFT.name.equals(name)) {
            
            type = ORIENTATION_TOP_RIGHT_TO_LEFT;
            
        } else if (ORIENTATION_BOTTOM_LEFT_TO_RIGHT.name.equals(name)) {
            
            type = ORIENTATION_BOTTOM_LEFT_TO_RIGHT;
            
        } else if (ORIENTATION_BOTTOM_RIGHT_TO_LEFT.name.equals(name)) {
            
            type = ORIENTATION_BOTTOM_RIGHT_TO_LEFT;
            
        } else if (ORIENTATION_LEFT_TOP_TO_BOTTOM.name.equals(name)) {
            
            type = ORIENTATION_LEFT_TOP_TO_BOTTOM;
            
        } else if (ORIENTATION_LEFT_BOTTOM_TO_TOP.name.equals(name)) {
            
            type = ORIENTATION_LEFT_BOTTOM_TO_TOP;
            
        } else if (ORIENTATION_RIGHT_TOP_TO_BOTTOM.name.equals(name)) {
            
            type = ORIENTATION_RIGHT_TOP_TO_BOTTOM;
            
        } else if (ORIENTATION_RIGHT_BOTTOM_TO_TOP.name.equals(name)) {
            
            type = ORIENTATION_RIGHT_BOTTOM_TO_TOP;
            
        }

        return type;
    }

}
