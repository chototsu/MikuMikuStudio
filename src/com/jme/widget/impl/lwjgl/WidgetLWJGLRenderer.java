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

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLU;

import com.jme.renderer.ColorRGBA;
import com.jme.renderer.LWJGLRenderer;
import com.jme.scene.state.TextureState;
import com.jme.widget.Widget;
import com.jme.widget.border.WidgetBorder;
import com.jme.widget.border.WidgetBorderType;
import com.jme.widget.bounds.WidgetViewport;
import com.jme.widget.button.WidgetButtonStateType;
import com.jme.widget.renderer.WidgetRenderer;
import com.jme.widget.scroller.WidgetScrollerButton;
import com.jme.widget.text.WidgetText;

/**
 * @author Gregg Patton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WidgetLWJGLRenderer extends LWJGLRenderer implements WidgetRenderer {

    public WidgetLWJGLRenderer(int width, int height) {
        super(width, height);
    }

    /* (non-Javadoc)
     * @see com.jme.renderer.Renderer#getTextureState()
     */
    public TextureState getTextureState() {
        return new WidgetLWJGLTextureState();
    }

    public void draw(Widget w) {
        drawBox2d(w);
        drawBorder2d(w);
    }

    private void drawBox2d(Widget w) {
		initWidgetProjection(w);

        int l = w.getX() + w.getXOffset();
        int b = w.getY() + w.getYOffset();

        int r = l + w.getWidth();
        int t = b + w.getHeight();

        drawBox2d(t, l, b, r, w.getBorder(), w.getBgColor());

		resetWidgetProjection();
    }

	public void draw(WidgetText wt) {
		initWidgetProjection(wt);

		float x = (wt.getX()) + wt.getXOffset();
		//float y = (getY() + getHeight() + 1) + getYOffset();
		float y = wt.getY() + wt.getHeight() + wt.getYOffset();

		wt.getFont().renderString(wt.getText(), x, y, wt.getScale(), wt.getFgColor(), wt.getFgColor());
		
		resetWidgetProjection();
	}
	
	public void draw(WidgetScrollerButton wsb) {
		initWidgetProjection(wsb);

		int l = wsb.getX() + wsb.getXOffset();
		int b = wsb.getY() + wsb.getYOffset();

		int r = l + wsb.getWidth();
		int t = b + wsb.getHeight();

		drawBox2d(t, l, b, r, wsb.getBorder(), wsb.getBgColor());

		l += wsb.getExpander().getLeft();
		b += wsb.getExpander().getBottom();

		r -= wsb.getExpander().getRight();
		t -= wsb.getExpander().getTop();

		if (wsb.getButtonState() == WidgetButtonStateType.BUTTON_UP) {
			drawRaisedBorder2d(t, l, b, r, wsb.getBorder());
		} else if (wsb.getButtonState() == WidgetButtonStateType.BUTTON_DOWN) {
			drawLoweredBorder2d(t, l, b, r, wsb.getBorder());
		}

		resetWidgetProjection();
	}

    private void drawBorder2d(Widget w) {

        WidgetBorder border = w.getBorder();
        
        if (border.getType() == WidgetBorderType.RAISED) {
            drawRaisedBorder2d(w);
        } else if (border.getType() == WidgetBorderType.LOWERED) {
            drawLoweredBorder2d(w);
        } else if (border.getType() == WidgetBorderType.FLAT) {
            drawFlatBorder2d(w);
        }

    }

	private void drawFlatBorder2d(Widget w) {

		initWidgetProjection(w);

		int l = w.getX() + w.getXOffset();
		int b = w.getY() + w.getYOffset();

		int r = l + w.getWidth();
		int t = b + w.getHeight();

		drawFlatBorder2d(t, l, b, r, w.getBorder());

		resetWidgetProjection();
	}

	private void drawLoweredBorder2d(Widget w) {
		initWidgetProjection(w);

		int l = w.getX() + w.getXOffset();
		int b = w.getY() + w.getYOffset();

		int r = l + w.getWidth();
		int t = b + w.getHeight();

		drawLoweredBorder2d(t, l, b, r, w.getBorder());

		resetWidgetProjection();
	}

	private void drawRaisedBorder2d(Widget w) {
		initWidgetProjection(w);

		int l = w.getX() + w.getXOffset();
		int b = w.getY() + w.getYOffset();

		int r = l + w.getWidth();
		int t = b + w.getHeight();

		drawRaisedBorder2d(t, l, b, r, w.getBorder());

		resetWidgetProjection();
	}

	private void drawBox2d(int top, int left, int bottom, int right, WidgetBorder border, ColorRGBA color) {

		if (color != null) {
			GL.glColor3f(color.r, color.g, color.b);

			GL.glBegin(GL.GL_QUADS);
			GL.glVertex2f(left + border.left, bottom + border.bottom);
			GL.glVertex2f(right - border.right, bottom + border.bottom);
			GL.glVertex2f(right - border.right, top - border.top);
			GL.glVertex2f(left + border.left, top - border.top);
			GL.glEnd();
		}

	}

    private void drawBorder2d(int top, int left, int bottom, int right, WidgetBorder border, ColorRGBA topLeft, ColorRGBA bottomRight) {

        GL.glBegin(GL.GL_QUADS);

        GL.glColor3f(topLeft.r, topLeft.g, topLeft.b);

        GL.glVertex2f(left, bottom);
        GL.glVertex2f(left + border.left, bottom + border.bottom);
        GL.glVertex2f(left + border.left, top - border.top);
        GL.glVertex2f(left, top);

        GL.glVertex2f(left, top);
        GL.glVertex2f(left + border.left, top - border.top);
        GL.glVertex2f(right - border.right, top - border.top);
        GL.glVertex2f(right, top);

        GL.glColor3f(bottomRight.r, bottomRight.g, bottomRight.b);

        GL.glVertex2f(left, bottom);
        GL.glVertex2f(right, bottom);
        GL.glVertex2f(right - border.right, bottom + border.bottom);
        GL.glVertex2f(left + border.left, bottom + border.bottom);

        GL.glVertex2f(right, bottom);
        GL.glVertex2f(right - border.right, bottom + border.bottom);
        GL.glVertex2f(right - border.right, top - border.top);
        GL.glVertex2f(right, top);

        GL.glEnd();
    }

	private void drawFlatBorder2d(int top, int left, int bottom, int right, WidgetBorder border) {
        drawBorder2d(top, left, bottom, right, border, border.getFlatColor(), border.getFlatColor());
    }

    private void drawLoweredBorder2d(int top, int left, int bottom, int right, WidgetBorder border) {
        drawBorder2d(top, left, bottom, right, border, border.getDarkColor(), border.getLightColor());
    }

	private void drawRaisedBorder2d(int top, int left, int bottom, int right, WidgetBorder border) {
        drawBorder2d(top, left, bottom, right, border, border.getLightColor(), border.getDarkColor());
    }

	private void initWidgetProjection(Widget widget) {
		WidgetViewport v;
		
		Widget p = widget.getWidgetParent();
		
		if (p != null) {
			v = p.getViewport();
		} else {
			v = widget.getViewport();
		}
		
		int x = (int) v.getMinX();
		int y = (int) v.getMinY();
		int w = (int) v.getWidth();
		int h = (int) v.getHeight();

		GL.glViewport(x, y, w, h);

		GL.glEnable(GL.GL_SCISSOR_TEST);

		GL.glScissor(x, y, w, h);

		GL.glMatrixMode(GL.GL_PROJECTION);
		GL.glPushMatrix();

		GL.glLoadIdentity();

		GLU.gluOrtho2D(0, w, 0, h);

		GL.glMatrixMode(GL.GL_MODELVIEW);
		GL.glPushMatrix();

		GL.glLoadIdentity();
		
	}

	private void resetWidgetProjection() {
		GL.glDisable(GL.GL_SCISSOR_TEST);
		GL.glMatrixMode(GL.GL_PROJECTION);
		GL.glPopMatrix();
		GL.glMatrixMode(GL.GL_MODELVIEW);
		GL.glPopMatrix();
	}

}
