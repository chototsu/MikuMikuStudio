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
package com.jme.widget.impl.lwjgl;

import java.util.logging.Level;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Window;

import com.jme.util.LoggingSystem;
import com.jme.widget.input.mouse.WidgetMouseButtonType;
import com.jme.widget.input.mouse.WidgetMouseStateAbstract;

/**
 * @author Gregg Patton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WidgetLWJGLMouseState extends WidgetMouseStateAbstract {

    public WidgetLWJGLMouseState() {
        this.x = Window.getWidth() / 2;
        this.y = Window.getHeight() / 2;
    }

	public void init() {

		try {

            Mouse.create();
			Mouse.setNativeCursor(WidgetLWJGLStandardCursor.cursor);

        } catch (Exception e) {
			LoggingSystem.getLogger().log(Level.WARNING, e.getMessage());
        }
	}
	
	public void destroy() {
		try {
			
            Mouse.setNativeCursor(null);
            
        } catch (Exception e) {
			LoggingSystem.getLogger().log(Level.WARNING, e.getMessage());
        }
        
		Mouse.destroy();
	}
	
	public void poll() {
		Mouse.poll();
	}

    public void setState() {
    	
    	poll();
    	
        dx = Mouse.dx;
        dy = Mouse.dy;
        dwheel = Mouse.dwheel;

        x += dx;
        y += dy;

        setButtonType();
        
    }

    private void setButtonType() {
        int button = 0;

        for (int i = 0; i < Mouse.buttonCount; i++) {
            if (Mouse.isButtonDown(i)) {
                switch (i) {
                    case 0 :
                        button |= BUTTON_1;
                        break;
                    case 1 :
                        button |= BUTTON_2;
                        break;
                    case 2 :
                        button |= BUTTON_3;
                        break;
                }
            }
        }

        switch (button) {
            case 0 :
                buttonType = null;
                break;
            case BUTTON_1 :
                buttonType = WidgetMouseButtonType.MOUSE_BUTTON_1;
                break;
            case BUTTON_2 :
                buttonType = WidgetMouseButtonType.MOUSE_BUTTON_2;
                break;
            case BUTTON_3 :
                buttonType = WidgetMouseButtonType.MOUSE_BUTTON_3;
                break;
            case BUTTON_1_2 :
                buttonType = WidgetMouseButtonType.MOUSE_BUTTON_1_2;
                break;
            case BUTTON_1_3 :
                buttonType = WidgetMouseButtonType.MOUSE_BUTTON_1_3;
                break;
            case BUTTON_2_3 :
                buttonType = WidgetMouseButtonType.MOUSE_BUTTON_2_3;
                break;
            case BUTTON_1_2_3 :
                buttonType = WidgetMouseButtonType.MOUSE_BUTTON_1_2_3;
                break;
        }
        
    }

    public String toString() {
        return "[x=" + x + ", y=" + y + ", dx=" + dx + ", dy=" + dy + "]";

    }

}
