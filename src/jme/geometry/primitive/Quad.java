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

package jme.geometry.primitive;

import jme.exception.MonkeyRuntimeException;
import jme.math.Vector;
import jme.texture.TextureManager;

import org.lwjgl.opengl.GL;

/**
 * <code>Quad</code> handles the rendering of a single quad shape. This
 * quad shape is defined by four points and maintained in an array of
 * four <code>Vector</code> values. The ordering of the array is important
 * as each index represents a corner of the quad.
 * 
 * <br><br>
 * 0 - TopLeft<br>
 * 1 - TopRight<br>
 * 2 - BottomRight<br>
 * 3 - BottomLeft<br>
 * 
 * @author Mark Powell
 * @version $Id: Quad.java,v 1.7 2003-09-08 20:29:27 mojomonkey Exp $
 */
public class Quad extends Primitive {
    private GL gl;
	Vector[] points;
	
	/**
	 * Constructor instantiates a new <code>Quad</code> with the given set of
	 * points.
	 * @param points the points that make up the quad.
	 * @throws MonkeyRuntimeException if points are null.
	 */
	public Quad(Vector[] points) {
		if(null == points) {
			throw new MonkeyRuntimeException("Points cannot be null.");
		}
		this.points = points;
		initialize();
	}
	
	/**
	 * <code>setPoints</code> sets the points that define the quad shape.
	 * @param points the points that make up the quad.
	 * @throws MonkeyRuntimeException if points are null.
	 */
	public void setPoints(Vector[] points) {
		if(null == points) {
			throw new MonkeyRuntimeException("Points cannot be null.");
		}
		this.points = points;
		initialize();
	}
	
	/**
	 * <code>setPoint</code> sets a specific corner to the given point. 
	 * The index of the point should match:
	 * 
	 * <br><br>
	 * 0 - TopLeft<br>
	 * 1 - TopRight<br>
	 * 2 - BottomRight<br>
	 * 3 - BottomLeft<br>
	 * 
	 * @param index the corner to change.
	 * @param point the new point of the corner.
	 */
	public void setPoint(int index, Vector point) {
		points[index] = point;
		initialize();
	}

   /**
    * <code>render</code> renders a single quad with the defined points.
    */
    public void render() {
		if(getTextureId() > 0) {
			GL.glEnable(GL.GL_TEXTURE_2D);
			TextureManager.getTextureManager().bind(getTextureId());
		}
		
		GL.glBegin(GL.GL_QUADS);
		
		GL.glColor4f(red,green,blue,alpha);
		
		GL.glTexCoord2f(1,1);
		GL.glVertex3f(points[0].x,points[0].y,points[0].z);
		GL.glTexCoord2f(0,1);
		GL.glVertex3f(points[1].x,points[1].y,points[1].z);
		GL.glTexCoord2f(0,0);
		GL.glVertex3f(points[2].x,points[2].y,points[2].z);
		GL.glTexCoord2f(1,0);
		GL.glVertex3f(points[3].x,points[3].y,points[3].z);
		
		GL.glEnd();
		
		if(getTextureId() > 0) {
			GL.glDisable(GL.GL_TEXTURE_2D);
		}

    }

    /**
     * <code>initialize</code> sets up the quad to prepare it for usage.
     */
    public void initialize() {
		float size = 0.0f;
		//find the furtherest point from the center.
		float distance;
		for(int i = 0; i < 4; i++) {
			distance = (float)Math.sqrt(points[i].x * points[i].x +
					points[i].y * points[i].y +
					points[i].z * points[i].z);
			if(distance > size) {
				size = distance;
			}
		}
    }

	/**
	 * <code>preRender</code> does nothing for quad.
	 */
    public void preRender() {
		//do nothing
    }
    
    public Vector[] getPoints() {
        return points;
    }

}
