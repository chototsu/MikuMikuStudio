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
package com.jme.widget.font;

import java.util.Hashtable;
import java.util.logging.Level;

import com.jme.renderer.Renderer;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;
import com.jme.widget.impl.lwjgl.WidgetLWJGLFont;
import com.jme.widget.impl.lwjgl.WidgetLWJGLRenderer;

/**
 * @author Gregg Patton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WidgetFontManager {

    private static WidgetFontManager fm;
    private static Hashtable fontList;
	private static Renderer renderer;
	
    protected WidgetFontManager(Renderer r) {
        fontList = new Hashtable();

		renderer = r;
		
		if (renderer instanceof WidgetLWJGLRenderer) {
			WidgetFont f = new WidgetLWJGLFont("Default");
			fontList.put(f.getName(), f);

			//        f = new WidgetFont("Default");
			//        fontList.put(f.getName(), f);
		}

    }

    public static WidgetFont getFont(String fontName) {

        if (fm == null || fontList.size() == 0) {
        	String msg = "FontManager is not initialized.";
            LoggingSystem.getLogger().log(Level.WARNING, msg);
            throw new JmeException(msg);
        }

        return (WidgetFont) fontList.get(fontName);

    }

    public static void init(Renderer r) {
        fm = new WidgetFontManager(r);
    }
}
