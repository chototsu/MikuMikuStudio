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
package com.jme.widget.layout;

import com.jme.util.JmeType;

/**
 * @author Gregg Patton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WidgetBorderLayoutConstraint extends JmeType {

    public static final WidgetBorderLayoutConstraint CENTER = new WidgetBorderLayoutConstraint("CENTER");
    public static final WidgetBorderLayoutConstraint EAST = new WidgetBorderLayoutConstraint("EAST");
    public static final WidgetBorderLayoutConstraint NORTH = new WidgetBorderLayoutConstraint("NORTH");
    public static final WidgetBorderLayoutConstraint SOUTH = new WidgetBorderLayoutConstraint("SOUTH");
    public static final WidgetBorderLayoutConstraint WEST = new WidgetBorderLayoutConstraint("WEST");

    public WidgetBorderLayoutConstraint(String name) {
        super(name);
    }

    public JmeType getType(String name) {
        JmeType type = null;

        if (CENTER.name.equals(name)) {
            type = CENTER;
        } else if (EAST.name.equals(name)) {
            type = EAST;
        } else if (NORTH.name.equals(name)) {
            type = NORTH;
        } else if (SOUTH.name.equals(name)) {
            type = SOUTH;
        } else if (WEST.name.equals(name)) {
            type = WEST;
        }

        return type;
    }

}
