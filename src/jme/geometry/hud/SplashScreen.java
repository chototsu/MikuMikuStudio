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
package jme.geometry.hud;

import jme.exception.MonkeyGLException;
import jme.exception.MonkeyRuntimeException;
import jme.texture.TextureManager;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.Window;

/**
 * <code>SplashScreen</code> creates a screen encompassing splash screen to 
 * be displayed. This is typically used for title screens, and company logos. 
 * The class contains a <code>holdDisplay</code> method to allow the screen
 * to be displayed for a set amount of time. 
 * 
 * @author Mark Powell 
 * @version $Id: SplashScreen.java,v 1.2 2003-09-03 16:20:51 mojomonkey Exp $
 */
public class SplashScreen {
	private int texId;
	private float x, y;
	private float width, height;
	private boolean isBlended;
	private float red,green,blue,alpha;
	private long delay;
	
	/**
	 * Constructor instantiates a new <code>SplashScreen</code> object.
	 * @throws MonkeyGLException if this class is used before initializing
	 * 		the OpenGL context.
	 */
	public SplashScreen() {
		if(!Window.isCreated()) {
			throw new MonkeyGLException("Window must be created first.");
		}
		red = 1.0f;
		blue = 1.0f;
		green = 1.0f;
		alpha = 1.0f;
		
		width = Window.getWidth();
		height = Window.getHeight();
		
		isBlended = true;
	}
	
	/**
	 * <code>setTexture</code> sets the image to display as the splash.
	 * @param filename the image for the splash screen.
	 */
	public void setTexture(String filename) {
		texId = TextureManager.getTextureManager().loadTexture(
			filename,
			GL.GL_LINEAR,
			GL.GL_LINEAR,
			true);
	}
	
	/**
	 * <code>setColor</code> sets the color to shade the splashscreen. 
	 * 
	 * @param red the red component of the color.
	 * @param green the green component of the color.
	 * @param blue the blue component of the color.
	 * @param alpha the alpha component of the color.
	 */
	public void setColor(float red, float green, float blue, float alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
	
	/**
	 * <code>setPosition</code> sets the bottom left corner of the
	 * splash screen display. Default is (0,0).
	 * @param x the x coordinate of the bottom left corner.
	 * @param y the y coordinate of the bottom left corner.
	 */
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * <code>setSize</code> sets the size of the splash. This will
	 * "grow" from the position of the bottom left corner which can
	 * be set via <code>setPosition</code>.
	 * @param height the height of the splash.
	 * @param width the width of the splash.
	 * @throws MonkeyRuntimeException if height or width is negative.
	 */
	public void setSize(float height, float width) {
		if(height < 0 || width < 0) {
			throw new MonkeyRuntimeException("Neither height nor width " +				"may be negative");
		}
		this.width = width;
		this.height = height;
	}
	
	/**
	 * <code>setBlended</code> turns blending (using the alpha value of
	 * the color) on and off.
	 * @param value true if blending is on, false otherwise.
	 */
	public void setBlended(boolean value) {
		isBlended = value;
	}
	
	/**
	 * <code>setDelay</code> sets how long to display the splash screen 
	 * in milliseconds. 
	 * @param delay the amount of time to display the screen (in milliseconds).
	 */
	public void setDelay(long delay) {
		this.delay = delay;
	}
	
	/**
	 * <code>holdDisplay</code> will display the splash screen for the desired
	 * amount of time define by <code>setDelay</code>.
	 */
	public void holdDisplay() {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			return;
		}
	}

	/**
	 * <code>render</code> display the splash screen to the display view. 
	 *
	 */
	public void render() {
		
		//set the GL states to how we want them.
		if(isBlended) {
			GL.glEnable(GL.GL_BLEND);
		}
		GL.glDisable(GL.GL_DEPTH_TEST);
		GL.glEnable(GL.GL_TEXTURE_2D);
		GL.glMatrixMode(GL.GL_PROJECTION);
		GL.glPushMatrix();
		GL.glLoadIdentity();
		GL.glOrtho(0, Window.getWidth(), 0, Window.getHeight(), -1, 1);
		GL.glMatrixMode(GL.GL_MODELVIEW);
		GL.glPushMatrix();
		GL.glLoadIdentity();
		GL.glTranslatef(x,y,0);
		GL.glColor4f(red,green,blue,alpha);
		TextureManager.getTextureManager().bind(texId);
		
		GL.glBegin(GL.GL_QUADS);
		
		GL.glTexCoord2f(0,1);
		GL.glVertex3f(0.0f, height, 0.0f);
		
		GL.glTexCoord2f(0,0);
		GL.glVertex3f(0.0f, 0.0f, 0.0f);
		
		GL.glTexCoord2f(1,0);
		GL.glVertex3f(width, 0.0f, 0.0f);
		
		GL.glTexCoord2f(1,1);
		GL.glVertex3f(width, height, 0.0f);
		
		GL.glEnd();
		
		if(isBlended) {
			GL.glDisable(GL.GL_BLEND);
		}
		GL.glMatrixMode(GL.GL_PROJECTION);
		GL.glPopMatrix();
		GL.glMatrixMode(GL.GL_MODELVIEW);
		GL.glPopMatrix();
		GL.glEnable(GL.GL_DEPTH_TEST);
		GL.glDisable(GL.GL_TEXTURE_2D);
	}

	
}
