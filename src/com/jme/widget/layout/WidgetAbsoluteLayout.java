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
package com.jme.widget.layout;


import com.jme.math.Vector2f;
import com.jme.widget.Widget;
import com.jme.widget.WidgetContainerAbstract;

/**
 * @author Gregg Patton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WidgetAbsoluteLayout extends WidgetLayoutManager {

    public void addLayoutWidget(Widget w) {}

    public void removeLayoutWidget(Widget w) {}

    public Vector2f preferredLayoutSize(WidgetContainerAbstract parent) {
        if (parent.isVisible() == false)
            return new Vector2f();

        int tot = parent.getWidgetCount();

        float width = 0;
        float height = 0;

        for (int i = 0; i < tot; i++) {
            Widget w = parent.getWidget(i);

            Vector2f l = w.getLocation();
            Vector2f size = w.getPreferredSize();

            if (width < l.x + size.x)
                width = l.x + size.x;
            if (height < l.y + size.y)
                height = l.y + size.y;
        }

        Vector2f ret = new Vector2f();

        ret.x = width;
        ret.y = height;

        return ret;
    }

    public Vector2f minimumLayoutSize(WidgetContainerAbstract parent) {
        return null;
    }

    public void layoutContainer(WidgetContainerAbstract parent) {

        int tot = parent.getWidgetCount();

        for (int i = 0; i < tot; i++) {
            Widget w = parent.getWidget(i);

            Vector2f s = w.getPreferredSize();

            w.setSize(s);
        }

    }

}
