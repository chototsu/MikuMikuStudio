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
package com.jme.widget;

import com.jme.widget.util.WidgetType;

/**
 * @author Gregg Patton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public final class WidgetAlignmentType extends WidgetType {

    public final static WidgetAlignmentType ALIGN_NONE = new WidgetAlignmentType("ALIGN_NONE");
	public final static WidgetAlignmentType ALIGN_CENTER = new WidgetAlignmentType("ALIGN_CENTER");

	public final static WidgetAlignmentType ALIGN_NORTH = new WidgetAlignmentType("ALIGN_NORTH");
	public final static WidgetAlignmentType ALIGN_SOUTH = new WidgetAlignmentType("ALIGN_SOUTH");
	public final static WidgetAlignmentType ALIGN_EAST = new WidgetAlignmentType("ALIGN_EAST");
    public final static WidgetAlignmentType ALIGN_WEST = new WidgetAlignmentType("ALIGN_WEST");

	public final static WidgetAlignmentType ALIGN_NW = new WidgetAlignmentType("ALIGN_NW");
	public final static WidgetAlignmentType ALIGN_SW = new WidgetAlignmentType("ALIGN_SW");
	public final static WidgetAlignmentType ALIGN_NE = new WidgetAlignmentType("ALIGN_NE");
	public final static WidgetAlignmentType ALIGN_SE = new WidgetAlignmentType("ALIGN_SE");

    private WidgetAlignmentType(String name) {
        super(name);
    }

    public WidgetType getType(String name) {
        WidgetType type = null;

        if (ALIGN_NONE.name.equals(name)) {
            type = ALIGN_NONE;
		} else if (ALIGN_CENTER.name.equals(name)) {
			type = ALIGN_CENTER;

		} else if (ALIGN_NORTH.name.equals(name)) {
			type = ALIGN_NORTH;
		} else if (ALIGN_SOUTH.name.equals(name)) {
			type = ALIGN_SOUTH;
		} else if (ALIGN_EAST.name.equals(name)) {
			type = ALIGN_EAST;
        } else if (ALIGN_WEST.name.equals(name)) {
            type = ALIGN_WEST;

		} else if (ALIGN_NW.name.equals(name)) {
			type = ALIGN_NW;
		} else if (ALIGN_SW.name.equals(name)) {
			type = ALIGN_SW;
		} else if (ALIGN_NE.name.equals(name)) {
			type = ALIGN_NE;
		} else if (ALIGN_SE.name.equals(name)) {
			type = ALIGN_SE;
        }

        return type;
    }

}
