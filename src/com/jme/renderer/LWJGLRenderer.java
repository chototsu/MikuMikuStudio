/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding All rights reserved.
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

/*
 * EDIT: 02/09/2004 - Renamed original WidgetViewport to WidgetViewRectangle.
 * GOP
 */

package com.jme.renderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLU;
import org.lwjgl.opengl.Window;

import com.jme.curve.Curve;
import com.jme.effects.Tint;
import com.jme.input.Mouse;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.Clone;
import com.jme.scene.CloneNode;
import com.jme.scene.Line;
import com.jme.scene.Point;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.DitherState;
import com.jme.scene.state.FogState;
import com.jme.scene.state.LWJGLAlphaState;
import com.jme.scene.state.LWJGLCullState;
import com.jme.scene.state.LWJGLDitherState;
import com.jme.scene.state.LWJGLFogState;
import com.jme.scene.state.LWJGLLightState;
import com.jme.scene.state.LWJGLMaterialState;
import com.jme.scene.state.LWJGLShadeState;
import com.jme.scene.state.LWJGLTextureState;
import com.jme.scene.state.LWJGLWireframeState;
import com.jme.scene.state.LWJGLZBufferState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.ShadeState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.WireframeState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;
import com.jme.widget.Widget;
import com.jme.widget.border.WidgetBorder;
import com.jme.widget.border.WidgetBorderType;
import com.jme.widget.bounds.WidgetViewRectangle;
import com.jme.widget.button.WidgetButtonStateType;
import com.jme.widget.panel.WidgetPanel;
import com.jme.widget.scroller.WidgetScrollerButton;
import com.jme.widget.text.WidgetText;

/**
 * <code>LWJGLRenderer</code> provides an implementation of the <code>Renderer</code>
 * interface using the LWJGL API.
 * 
 * @see com.jme.renderer.Renderer
 * @author Mark Powell
 * @author Joshua Slack - Optimizations
 * @version $Id: LWJGLRenderer.java,v 1.28 2004-03-02 19:41:59 darkprophet Exp $
 */
public class LWJGLRenderer implements Renderer {

	//clear color
	private ColorRGBA backgroundColor;
	//width and height of renderer
	private int width;
	private int height;

	private FloatBuffer worldBuffer;
	private Vector3f vRot = new Vector3f();

	private LWJGLCamera camera;
	private LWJGLFont font;

	private float[] modelToWorld = new float[16];

	private long numberOfVerts;
	private long numberOfTris;
	private boolean statisticsOn;

	/**
	 * Constructor instantiates a new <code>LWJGLRenderer</code> object. The
	 * size of the rendering window is passed during construction.
	 * 
	 * @param width
	 *            the width of the rendering context.
	 * @param height
	 *            the height of the rendering context.
	 */
	public LWJGLRenderer(int width, int height) {
		if (width <= 0 || height <= 0) {
			LoggingSystem.getLogger().log(
				Level.WARNING,
				"Invalid width " + "and/or height values.");
			throw new JmeException("Invalid width and/or height values.");
		}
		this.width = width;
		this.height = height;

		worldBuffer =
			ByteBuffer
				.allocateDirect(64)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();

		LoggingSystem.getLogger().log(
			Level.INFO,
			"LWJGLRenderer created. W:  " + width + "H: " + height);
	}

	/**
	 * <code>setCamera</code> sets the camera this renderer is using. It
	 * asserts that the camera is of type <code>LWJGLCamera</code>.
	 * 
	 * @see com.jme.renderer.Renderer#setCamera(com.jme.renderer.Camera)
	 */
	public void setCamera(Camera camera) {
		if (camera instanceof LWJGLCamera) {
			this.camera = (LWJGLCamera) camera;
		}
	}

	/**
	 * <code>getCamera</code> returns the camera used by this renderer.
	 * 
	 * @see com.jme.renderer.Renderer#getCamera()
	 */
	public Camera getCamera() {
		return camera;
	}

	/**
	 * <code>getCamera</code> returns a default camera for use with the LWJGL
	 * renderer.
	 * 
	 * @param width
	 *            the width of the frame.
	 * @param height
	 *            the height of the frame.
	 * @return a default LWJGL camera.
	 */
	public Camera getCamera(int width, int height) {
		return new LWJGLCamera(width, height);
	}

	/**
	 * <code>getAlphaState</code> returns a new LWJGLAlphaState object as a
	 * regular AlphaState.
	 * 
	 * @return an AlphaState object.
	 */
	public AlphaState getAlphaState() {
		return new LWJGLAlphaState();
	}

	/**
	 * <code>getCullState</code> returns a new LWJGLCullState object as a
	 * regular CullState.
	 * 
	 * @return a CullState object.
	 * @see com.jme.renderer.Renderer#getCullState()
	 */
	public CullState getCullState() {
		return new LWJGLCullState();
	}

	/**
	 * <code>getDitherState</code> returns a new LWJGLDitherState object as a
	 * regular DitherState.
	 * 
	 * @return an DitherState object.
	 */
	public DitherState getDitherState() {
		return new LWJGLDitherState();
	}

	/**
	 * <code>getFogState</code> returns a new LWJGLFogState object as a
	 * regular FogState.
	 * 
	 * @return an FogState object.
	 */
	public FogState getFogState() {
		return new LWJGLFogState();
	}

	/**
	 * <code>getLightState</code> returns a new LWJGLLightState object as a
	 * regular LightState.
	 * 
	 * @return an LightState object.
	 */
	public LightState getLightState() {
		return new LWJGLLightState();
	}

	/**
	 * <code>getMaterialState</code> returns a new LWJGLMaterialState object
	 * as a regular MaterialState.
	 * 
	 * @return an MaterialState object.
	 */
	public MaterialState getMaterialState() {
		return new LWJGLMaterialState();
	}

	/**
	 * <code>getShadeState</code> returns a new LWJGLShadeState object as a
	 * regular ShadeState.
	 * 
	 * @return an ShadeState object.
	 */
	public ShadeState getShadeState() {
		return new LWJGLShadeState();
	}

	/**
	 * <code>getTextureState</code> returns a new LWJGLTextureState object as
	 * a regular TextureState.
	 * 
	 * @return an TextureState object.
	 */
	public TextureState getTextureState() {
		return new LWJGLTextureState();
	}

	/**
	 * <code>getWireframeState</code> returns a new LWJGLWireframeState
	 * object as a regular WireframeState.
	 * 
	 * @return an WireframeState object.
	 */
	public WireframeState getWireframeState() {
		return new LWJGLWireframeState();
	}

	public ZBufferState getZBufferState() {
		return new LWJGLZBufferState();
	}

	/**
	 * <code>setBackgroundColor</code> sets the OpenGL clear color to the
	 * color specified.
	 * 
	 * @see com.jme.renderer.Renderer#setBackgroundColor(com.jme.renderer.ColorRGBA)
	 * @param c
	 *            the color to set the background color to.
	 */
	public void setBackgroundColor(ColorRGBA c) {
		//if color is null set background to white.
		if (c == null) {
			backgroundColor.a = 1.0f;
			backgroundColor.b = 1.0f;
			backgroundColor.g = 1.0f;
			backgroundColor.r = 1.0f;
		} else {
			backgroundColor = c;
		}
		GL.glClearColor(
			backgroundColor.r,
			backgroundColor.g,
			backgroundColor.b,
			backgroundColor.a);
	}

	/**
	 * <code>getBackgroundColor</code> retrieves the clear color of the
	 * current OpenGL context.
	 * 
	 * @see com.jme.renderer.Renderer#getBackgroundColor()
	 * @return the current clear color.
	 */
	public ColorRGBA getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * <code>clearZBuffer</code> clears the OpenGL depth buffer.
	 * 
	 * @see com.jme.renderer.Renderer#clearZBuffer()
	 */
	public void clearZBuffer() {
		GL.glDisable(GL.GL_DITHER);
		GL.glEnable(GL.GL_SCISSOR_TEST);
		GL.glScissor(0, 0, width, height);
		GL.glClear(GL.GL_DEPTH_BUFFER_BIT);
		GL.glDisable(GL.GL_SCISSOR_TEST);
		GL.glEnable(GL.GL_DITHER);
	}

	/**
	 * <code>clearBackBuffer</code> clears the OpenGL color buffer.
	 * 
	 * @see com.jme.renderer.Renderer#clearBackBuffer()
	 */
	public void clearBackBuffer() {
		GL.glDisable(GL.GL_DITHER);
		GL.glEnable(GL.GL_SCISSOR_TEST);
		GL.glScissor(0, 0, width, height);
		GL.glClear(GL.GL_COLOR_BUFFER_BIT);
		GL.glDisable(GL.GL_SCISSOR_TEST);
		GL.glEnable(GL.GL_DITHER);
	}

	/**
	 * <code>clearBuffers</code> clears both the color and the depth buffer.
	 * 
	 * @see com.jme.renderer.Renderer#clearBuffers()
	 */
	public void clearBuffers() {
		GL.glDisable(GL.GL_DITHER);
		GL.glEnable(GL.GL_SCISSOR_TEST);
		GL.glScissor(0, 0, width, height);
		GL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		GL.glDisable(GL.GL_SCISSOR_TEST);
		GL.glEnable(GL.GL_DITHER);
	}

	/**
	 * <code>displayBackBuffer</code> flips the rendered buffer (back) with
	 * the currently displayed buffer.
	 * 
	 * @see com.jme.renderer.Renderer#displayBackBuffer()
	 */
	public void displayBackBuffer() {
		GL.glFlush();
		Window.paint();
		Window.update();
	}

	/**
	 * <code>takeScreenShot</code> saves the current buffer to a file. The
	 * file name is provided, and .png will be appended. True is returned if
	 * the capture was successful, false otherwise.
	 * 
	 * @param filename
	 *            the name of the file to save.
	 * @return true if successful, false otherwise.
	 */
	public boolean takeScreenShot(String filename) {
		if (null == filename) {
			throw new JmeException("Screenshot filename cannot be null");
		}
		LoggingSystem.getLogger().log(
			Level.INFO,
			"Taking screenshot: " + filename + ".png");

		//Create a pointer to the image info and create a buffered image to
		//hold it.
		IntBuffer buff =
			ByteBuffer
				.allocateDirect(width * height * 4)
				.order(ByteOrder.nativeOrder())
				.asIntBuffer();
		GL.glReadPixels(
			0,
			0,
			width,
			height,
			GL.GL_BGRA,
			GL.GL_UNSIGNED_BYTE,
			buff);
		BufferedImage img =
			new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		//Grab each pixel information and set it to the BufferedImage info.
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				img.setRGB(x, y, buff.get((height - y - 1) * width + x));
			}
		}

		//write out the screenshot image to a file.
		try {
			File out = new File(filename + ".png");
			return ImageIO.write(img, "png", out);
		} catch (IOException e) {
			LoggingSystem.getLogger().log(
				Level.WARNING,
				"Could not create file: " + filename + ".png");
			return false;
		}
	}

	/**
	 * <code>draw</code> renders a tint to the back buffer
	 * 
	 * @param t
	 *            is the tint to render.
	 */
	public void draw(Tint t) {
		// set up ortho mode
		GL.glMatrixMode(GL.GL_PROJECTION);
		GL.glPushMatrix();
		GL.glLoadIdentity();
		GLU.gluOrtho2D(0, Window.getWidth(), 0, Window.getHeight());
		GL.glMatrixMode(GL.GL_MODELVIEW);
		GL.glPushMatrix();
		GL.glLoadIdentity();

		// draw the texture
		if (t.useTexture()) {
			GL.glTranslatef(
				t.getTextureLocation().x,
				t.getTextureLocation().y,
				0);
			GL.glColor4f(
				t.getTextureColor().r,
				t.getTextureColor().g,
				t.getTextureColor().b,
				t.getTextureColor().a);

			GL.glBegin(GL.GL_QUADS);
			{
				// bottom left
				GL.glTexCoord2f(0, 0);
				GL.glVertex2f(0, 0);

				// top left
				GL.glTexCoord2f(0, 1);
				GL.glVertex2f(0, t.getTextureSize().y);

				// top right
				GL.glTexCoord2f(1, 1);
				GL.glVertex2f(t.getTextureSize().x, t.getTextureSize().y);

				// bottom right
				GL.glTexCoord2f(1, 0);
				GL.glVertex2f(t.getTextureSize().x, 0);
			}
			GL.glEnd();
		}

		// draw the tint
		// set the color
		GL.glDisable(GL.GL_TEXTURE_2D);
		GL.glLoadIdentity();
		GL.glColor4f(
			t.getTintColor().r,
			t.getTintColor().g,
			t.getTintColor().b,
			t.getTintColor().a);
		// drawQuad
		GL.glBegin(GL.GL_QUADS);
		{
			GL.glVertex2f(0, 0);
			GL.glVertex2f(0, Window.getHeight());
			GL.glVertex2f(Window.getWidth(), Window.getHeight());
			GL.glVertex2f(Window.getWidth(), 0);
		}
		GL.glEnd();

		// remove ortho mode, and go back to original
		// state
		GL.glEnable(GL.GL_TEXTURE_2D);
		GL.glMatrixMode(GL.GL_PROJECTION);
		GL.glPopMatrix();
		GL.glMatrixMode(GL.GL_MODELVIEW);
		GL.glPopMatrix();

	}

	/**
	 * <code>draw</code> draws a point object where a point contains a
	 * collection of vertices, normals, colors and texture coordinates.
	 * 
	 * @see com.jme.renderer.Renderer#draw(com.jme.scene.Point)
	 * @param p
	 *            the point object to render.
	 */
	public void draw(Point p) {
		// set world matrix
		Quaternion rotation = p.getWorldRotation();
		Vector3f translation = p.getWorldTranslation();
		float scale = p.getWorldScale();
		float rot = rotation.toAngleAxis(vRot);
		GL.glMatrixMode(GL.GL_MODELVIEW);
		GL.glPushMatrix();

		GL.glTranslatef(translation.x, translation.y, translation.z);
		GL.glRotatef(rot, vRot.x, vRot.y, vRot.z);
		GL.glScalef(scale, scale, scale);

		// render the object
		GL.glBegin(GL.GL_POINTS);

		// draw points
		Vector3f[] vertex = p.getVertices();
		Vector3f[] normal = p.getNormals();
		ColorRGBA[] color = p.getColors();
		Vector2f[] texture = p.getTextures();

		if (statisticsOn) {
			numberOfVerts += vertex.length;
		}

		if (normal != null) {
			if (color != null) {
				if (texture != null) {
					// N,C,T
					for (int i = 0; i < vertex.length; i++) {
						GL.glNormal3f(normal[i].x, normal[i].y, normal[i].z);
						GL.glColor4f(
							color[i].r,
							color[i].g,
							color[i].b,
							color[i].a);
						GL.glTexCoord2f(texture[i].x, texture[i].y);
						GL.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
					}
				} else {
					// N,C
					for (int i = 0; i < vertex.length; i++) {
						GL.glNormal3f(normal[i].x, normal[i].y, normal[i].z);
						GL.glColor4f(
							color[i].r,
							color[i].g,
							color[i].b,
							color[i].a);
						GL.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
					}
				}
			} else {
				if (texture != null) {
					// N,T
					for (int i = 0; i < vertex.length; i++) {
						GL.glNormal3f(normal[i].x, normal[i].y, normal[i].z);
						GL.glTexCoord2f(texture[i].x, texture[i].y);
						GL.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
					}
				} else {
					// N
					for (int i = 0; i < vertex.length; i++) {
						GL.glNormal3f(normal[i].x, normal[i].y, normal[i].z);
						GL.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
					}
				}
			}
		} else {
			if (color != null) {
				if (texture != null) {
					// C,T
					for (int i = 0; i < vertex.length; i++) {
						GL.glColor4f(
							color[i].r,
							color[i].g,
							color[i].b,
							color[i].a);
						GL.glTexCoord2f(texture[i].x, texture[i].y);
						GL.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
					}
				} else {
					// C
					for (int i = 0; i < vertex.length; i++) {
						GL.glColor4f(
							color[i].r,
							color[i].g,
							color[i].b,
							color[i].a);
						GL.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
					}
				}
			} else {
				if (texture != null) {
					// T
					for (int i = 0; i < vertex.length; i++) {
						GL.glTexCoord2f(texture[i].x, texture[i].y);
						GL.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
					}
				} else {
					// none
					for (int i = 0; i < vertex.length; i++) {
						GL.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
					}
				}
			}
		}

		GL.glEnd();

		GL.glMatrixMode(GL.GL_MODELVIEW);
		GL.glPopMatrix();

	}

	/**
	 * <code>draw</code> draws a line object where a line contains a
	 * collection of vertices, normals, colors and texture coordinates.
	 * 
	 * @see com.jme.renderer.Renderer#draw(com.jme.scene.Line)
	 * @param l
	 *            the line object to render.
	 */
	public void draw(Line l) {
		// set world matrix
		Quaternion rotation = l.getWorldRotation();
		Vector3f translation = l.getWorldTranslation();
		float scale = l.getWorldScale();
		float rot = rotation.toAngleAxis(vRot);
		GL.glMatrixMode(GL.GL_MODELVIEW);
		GL.glPushMatrix();

		GL.glTranslatef(translation.x, translation.y, translation.z);
		GL.glRotatef(rot, vRot.x, vRot.y, vRot.z);
		GL.glScalef(scale, scale, scale);

		// render the object
		GL.glBegin(GL.GL_LINES);

		// draw line
		Vector3f[] vertex = l.getVertices();
		Vector3f[] normal = l.getNormals();
		ColorRGBA[] color = l.getColors();
		Vector2f[] texture = l.getTextures();

		if (statisticsOn) {
			numberOfVerts += vertex.length;
		}

		if (normal != null) {
			if (color != null) {
				if (texture != null) {
					// N,C,T
					for (int i = 0; i < vertex.length - 1; i++) {
						GL.glNormal3f(normal[i].x, normal[i].y, normal[i].z);
						GL.glColor4f(
							color[i].r,
							color[i].g,
							color[i].b,
							color[i].a);
						GL.glTexCoord2f(texture[i].x, texture[i].y);
						GL.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
						i++;
						GL.glNormal3f(normal[i].x, normal[i].y, normal[i].z);
						GL.glColor4f(
							color[i].r,
							color[i].g,
							color[i].b,
							color[i].a);
						GL.glTexCoord2f(texture[i].x, texture[i].y);
						GL.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
					}

				} else {
					// N,C
					for (int i = 0; i < vertex.length - 1; i++) {
						GL.glNormal3f(normal[i].x, normal[i].y, normal[i].z);
						GL.glColor4f(
							color[i].r,
							color[i].g,
							color[i].b,
							color[i].a);
						GL.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
						i++;
						GL.glNormal3f(normal[i].x, normal[i].y, normal[i].z);
						GL.glColor4f(
							color[i].r,
							color[i].g,
							color[i].b,
							color[i].a);
						GL.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
					}
				}
			} else {
				if (texture != null) {
					// N,T
					for (int i = 0; i < vertex.length - 1; i++) {
						GL.glNormal3f(normal[i].x, normal[i].y, normal[i].z);
						GL.glTexCoord2f(texture[i].x, texture[i].y);
						GL.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
						i++;
						GL.glNormal3f(normal[i].x, normal[i].y, normal[i].z);
						GL.glTexCoord2f(texture[i].x, texture[i].y);
						GL.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
					}

				} else {
					// N
					for (int i = 0; i < vertex.length - 1; i++) {
						GL.glNormal3f(normal[i].x, normal[i].y, normal[i].z);
						GL.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
						i++;
						GL.glNormal3f(normal[i].x, normal[i].y, normal[i].z);
						GL.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
					}

				}
			}
		} else {
			if (color != null) {
				if (texture != null) {
					// C,T
					for (int i = 0; i < vertex.length - 1; i++) {
						GL.glColor4f(
							color[i].r,
							color[i].g,
							color[i].b,
							color[i].a);
						GL.glTexCoord2f(texture[i].x, texture[i].y);
						GL.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
						i++;
						GL.glColor4f(
							color[i].r,
							color[i].g,
							color[i].b,
							color[i].a);
						GL.glTexCoord2f(texture[i].x, texture[i].y);
						GL.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
					}

				} else {
					// C
					for (int i = 0; i < vertex.length - 1; i++) {
						GL.glColor4f(
							color[i].r,
							color[i].g,
							color[i].b,
							color[i].a);
						GL.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
						i++;
						GL.glColor4f(
							color[i].r,
							color[i].g,
							color[i].b,
							color[i].a);
						GL.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
					}
				}
			} else {
				if (texture != null) {
					// T
					for (int i = 0; i < vertex.length - 1; i++) {
						GL.glTexCoord2f(texture[i].x, texture[i].y);
						GL.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
						i++;
						GL.glTexCoord2f(texture[i].x, texture[i].y);
						GL.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
					}

				} else {
					// none
					for (int i = 0; i < vertex.length - 1; i++) {
						GL.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
						i++;
						GL.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
					}
				}
			}
		}

		GL.glEnd();

		GL.glMatrixMode(GL.GL_MODELVIEW);
		GL.glPopMatrix();

	}

	/**
	 * <code>draw</code> renders a curve object.
	 * 
	 * @param c
	 *            the curve object to render.
	 */
	public void draw(Curve c) {
		//      set world matrix
		Quaternion rotation = c.getWorldRotation();
		Vector3f translation = c.getWorldTranslation();
		float scale = c.getWorldScale();
		float rot = rotation.toAngleAxis(vRot);
		GL.glMatrixMode(GL.GL_MODELVIEW);
		GL.glPushMatrix();

		GL.glTranslatef(translation.x, translation.y, translation.z);
		GL.glRotatef(rot, vRot.x, vRot.y, vRot.z);
		GL.glScalef(scale, scale, scale);

		// render the object
		GL.glBegin(GL.GL_LINE_STRIP);

		ColorRGBA[] color = c.getColors();
		float colorInterval = 0;
		float colorModifier = 0;
		int colorCounter = 0;
		if (null != color) {
			GL.glColor4f(color[0].r, color[0].g, color[0].b, color[0].a);

			colorInterval = 1f / c.getColors().length;
			colorModifier = colorInterval;
			colorCounter = 0;
		}

		Vector3f point;
		float limit = (1 + (1.0f / c.getSteps()));
		for (float t = 0; t <= limit; t += 1.0f / c.getSteps()) {

			if (t >= colorInterval && color != null) {

				colorInterval += colorModifier;
				GL.glColor4f(
					c.getColors()[colorCounter].r,
					c.getColors()[colorCounter].g,
					c.getColors()[colorCounter].b,
					c.getColors()[colorCounter].a);
				colorCounter++;
			}

			point = c.getPoint(t);
			GL.glVertex3f(point.x, point.y, point.z);
		}

		if (statisticsOn) {
			numberOfVerts += limit;
		}

		GL.glEnd();
		GL.glMatrixMode(GL.GL_MODELVIEW);
		GL.glPopMatrix();
	}

	/**
	 * <code>draw</code> renders a <code>TriMesh</code> object including
	 * it's normals, colors, textures and vertices.
	 * 
	 * @see com.jme.renderer.Renderer#draw(com.jme.scene.TriMesh)
	 * @param t
	 *            the mesh to render.
	 */
	public void draw(TriMesh t) {
		// set world matrix
		Quaternion rotation = t.getWorldRotation();
		Vector3f translation = t.getWorldTranslation();
		float scale = t.getWorldScale();
		float rot = rotation.toAngleAxis(vRot);
		GL.glMatrixMode(GL.GL_MODELVIEW);
		GL.glPushMatrix();

		GL.glTranslatef(translation.x, translation.y, translation.z);
		GL.glRotatef(rot, vRot.x, vRot.y, vRot.z);
		GL.glScalef(scale, scale, scale);

		// render the object

		GL.glVertexPointer(3, 0, t.getVerticeAsFloatBuffer());
		GL.glEnableClientState(GL.GL_VERTEX_ARRAY);

		FloatBuffer normals = t.getNormalAsFloatBuffer();
		if (normals != null) {
			GL.glEnableClientState(GL.GL_NORMAL_ARRAY);
			GL.glNormalPointer(0, normals);
		} else {
			GL.glDisableClientState(GL.GL_NORMAL_ARRAY);
		}

		FloatBuffer colors = t.getColorAsFloatBuffer();
		if (colors != null) {
			GL.glEnableClientState(GL.GL_COLOR_ARRAY);
			GL.glColorPointer(4, 0, colors);
		} else {
			GL.glDisableClientState(GL.GL_COLOR_ARRAY);
		}

		for (int i = 0; i < t.getNumberOfUnits(); i++) {
			FloatBuffer textures = t.getTextureAsFloatBuffer(i);
			if (textures != null) {
				GL.glClientActiveTextureARB(GL.GL_TEXTURE0_ARB + i);
				if (textures != null) {
					GL.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
					GL.glTexCoordPointer(2, 0, textures);
				} else {
					GL.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
				}
			}
		}

		IntBuffer indices = t.getIndexAsBuffer();
		if (statisticsOn) {
			int adder = indices.capacity();
			numberOfTris += adder;
			numberOfVerts += adder * 3;
		}
		GL.glDrawElements(GL.GL_TRIANGLES, indices);

		GL.glMatrixMode(GL.GL_MODELVIEW);
		GL.glPopMatrix();
	}

	/**
	 * <code>draw</code> draws a clone node object. The data for the geometry
	 * defined in the clone node is set but not rendered. The rendering occurs
	 * by the clone node's children (Clones).
	 * 
	 * @param cn
	 *            the clone node to render.
	 * @see com.jme.renderer.Renderer#draw(com.jme.scene.CloneNode)
	 */
	public void draw(CloneNode cn) {
		TriMesh t = cn.getGeometry();

		// render the object

		GL.glVertexPointer(3, 0, t.getVerticeAsFloatBuffer());
		GL.glEnableClientState(GL.GL_VERTEX_ARRAY);

		FloatBuffer normals = t.getNormalAsFloatBuffer();
		if (normals != null) {
			GL.glEnableClientState(GL.GL_NORMAL_ARRAY);
			GL.glNormalPointer(0, normals);
		} else {
			GL.glDisableClientState(GL.GL_NORMAL_ARRAY);
		}

		FloatBuffer colors = t.getColorAsFloatBuffer();
		if (colors != null) {
			GL.glEnableClientState(GL.GL_COLOR_ARRAY);
			GL.glColorPointer(4, 0, colors);
		} else {
			GL.glDisableClientState(GL.GL_COLOR_ARRAY);
		}

		FloatBuffer textures = t.getTextureAsFloatBuffer();
		if (textures != null) {
			GL.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
			GL.glTexCoordPointer(2, 0, textures);
		} else {
			GL.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
		}
	}

	/**
	 * <code>draw</code> renders a clone object. The depends on the data for
	 * geometry previously being set by a clone node. The world transformations
	 * are then made and the geometry is rendered.
	 * 
	 * @param c
	 *            the clone object.
	 * @see com.jme.renderer.Renderer#draw(com.jme.scene.Clone)
	 */
	public void draw(Clone c) {
		//set world matrix
		Quaternion rotation = c.getWorldRotation();
		Vector3f translation = c.getWorldTranslation();
		float scale = c.getWorldScale();
		float rot = rotation.toAngleAxis(vRot);
		GL.glMatrixMode(GL.GL_MODELVIEW);
		GL.glPushMatrix();

		GL.glTranslatef(translation.x, translation.y, translation.z);
		GL.glRotatef(rot, vRot.x, vRot.y, vRot.z);
		GL.glScalef(scale, scale, scale);

		IntBuffer indices = c.getIndexBuffer();
		if (statisticsOn) {
			int adder = indices.capacity();
			numberOfTris += adder;
			numberOfVerts += adder * 3;
		}

		GL.glDrawElements(GL.GL_TRIANGLES, indices);

		GL.glMatrixMode(GL.GL_MODELVIEW);
		GL.glPopMatrix();
	}

	/**
	 * <code>draw</code> renders a scene by calling the nodes <code>onDraw</code>
	 * method.
	 * 
	 * @see com.jme.renderer.Renderer#draw(com.jme.scene.Spatial)
	 */
	public void draw(Spatial s) {
		if (s != null) {
			s.onDraw(this);
		}

	}

	/**
	 * <code>draw</code> renders a text object using a predefined font.
	 * 
	 * @see com.jme.renderer.Renderer#draw(com.jme.scene.Text)
	 */
	public void draw(Text t) {
		if (font == null) {
			font = new LWJGLFont();
		}

		font.print(
			(int) t.getLocalTranslation().x,
			(int) t.getLocalTranslation().y,
			t.getText(),
			0);
	}

	/**
	 * <code>draw</code> renders a mouse object using a defined mouse
	 * texture.
	 * 
	 * @see com.jme.renderer.Renderer#draw(com.jme.input.Mouse)
	 */
	public void draw(Mouse m) {
		if (!m.hasCursor()) {
			return;
		}

		GL.glMatrixMode(GL.GL_PROJECTION);
		GL.glPushMatrix();
		GL.glLoadIdentity();
		GL.glOrtho(0, Window.getWidth(), 0, Window.getHeight(), -1, 1);
		GL.glMatrixMode(GL.GL_MODELVIEW);
		GL.glPushMatrix();
		GL.glLoadIdentity();
		GL.glTranslatef(
			m.getLocalTranslation().x,
			m.getLocalTranslation().y,
			0);

		//render the cursor
		int width = m.getImageWidth();
		int height = m.getImageHeight();

		GL.glBegin(GL.GL_QUADS);
		GL.glTexCoord2f(0, 0);
		GL.glVertex2f(0, 0);

		GL.glTexCoord2f(1, 0);
		GL.glVertex2f(width, 0);
		GL.glTexCoord2f(1, 1);
		GL.glVertex2f(width, height);
		GL.glTexCoord2f(0, 1);
		GL.glVertex2f(0, height);
		GL.glEnd();

		GL.glMatrixMode(GL.GL_PROJECTION);
		GL.glPopMatrix();
		GL.glMatrixMode(GL.GL_MODELVIEW);
		GL.glPopMatrix();
	}

	/**
	 * <code>draw</code> renders a WidgetPanel object to the back buffer.
	 * 
	 * @see com.jme.renderer.Renderer#draw(WidgetPanel)
	 */
	public void draw(WidgetPanel wp) {
		drawBox2d(wp);
		drawBorder2d(wp);
	}

	/**
	 * <code>draw</code> renders a WidgetText object to the back buffer.
	 * 
	 * @see com.jme.renderer.Renderer#draw(com.jme.widget.text.WidgetText)
	 */
	public void draw(WidgetText wt) {

		if (wt.getFgColor() == null)
			return;

		initWidgetProjection(wt);

		float x = (wt.getX()) + wt.getXOffset();
		float y = wt.getY() + wt.getHeight() + wt.getYOffset();

		wt.getFont().renderString(
			wt.getText(),
			x,
			y,
			wt.getScale(),
			wt.getFgColor(),
			wt.getFgColor());

		resetWidgetProjection();
	}

	/**
	 * <code>draw</code> renders a WidgetScrollerButton object to the back
	 * buffer.
	 * 
	 * @see com.jme.renderer.Renderer#draw(com.jme.widget.scroller.WidgetScrollerButton)
	 */
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

	public void enableStatistics(boolean value) {
		System.out.println("Stats are " + value);
		statisticsOn = value;
	}

	public void clearStatistics() {
		numberOfVerts = 0;
		numberOfTris = 0;
	}

	public String getStatistics() {
		return "Number of Triangles: "
			+ numberOfTris
			+ " : Number of Vertices: "
			+ numberOfVerts;
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

	private void drawBox2d(
		int top,
		int left,
		int bottom,
		int right,
		WidgetBorder border,
		ColorRGBA color) {

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

	private void drawBorder2d(
		int top,
		int left,
		int bottom,
		int right,
		WidgetBorder border,
		ColorRGBA topLeft,
		ColorRGBA bottomRight) {

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

	private void drawFlatBorder2d(
		int top,
		int left,
		int bottom,
		int right,
		WidgetBorder border) {
		drawBorder2d(
			top,
			left,
			bottom,
			right,
			border,
			border.getFlatColor(),
			border.getFlatColor());
	}

	private void drawLoweredBorder2d(
		int top,
		int left,
		int bottom,
		int right,
		WidgetBorder border) {
		drawBorder2d(
			top,
			left,
			bottom,
			right,
			border,
			border.getDarkColor(),
			border.getLightColor());
	}

	private void drawRaisedBorder2d(
		int top,
		int left,
		int bottom,
		int right,
		WidgetBorder border) {
		drawBorder2d(
			top,
			left,
			bottom,
			right,
			border,
			border.getLightColor(),
			border.getDarkColor());
	}

	private void initWidgetProjection(Widget widget) {
		WidgetViewRectangle v;

		Widget p = widget.getWidgetParent();

		if (p != null) {
			v = p.getViewRectangle();
		} else {
			v = widget.getViewRectangle();
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

		if (this.camera != null) {
			camera.update();
		}
	}
}
