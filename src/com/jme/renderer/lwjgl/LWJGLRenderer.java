/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights
 * reserved.
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

package com.jme.renderer.lwjgl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.*;
import org.lwjgl.opengl.glu.*;

import com.jme.bounding.BoundingVolume;
import com.jme.curve.Curve;
import com.jme.effects.Tint;
import com.jme.input.Mouse;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Clone;
import com.jme.scene.CloneNode;
import com.jme.scene.Geometry;
import com.jme.scene.Line;
import com.jme.scene.Point;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.state.*;
import com.jme.scene.state.lwjgl.*;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;
import com.jme.widget.WidgetRenderer;
import org.lwjgl.BufferUtils;

/**
 * <code>LWJGLRenderer</code> provides an implementation of the
 * <code>Renderer</code> interface using the LWJGL API.
 *
 * @see com.jme.renderer.Renderer
 * @author Mark Powell
 * @author Joshua Slack - Optimizations
 * @version $Id: LWJGLRenderer.java,v 1.19 2004-05-21 15:41:27 mojomonkey Exp $
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

    private LWJGLWireframeState boundsWireState = new LWJGLWireframeState();

    private LWJGLTextureState boundsTextState = new LWJGLTextureState();

    private LWJGLZBufferState boundsZState = new LWJGLZBufferState();

    private boolean inOrthoMode;

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
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Invalid width " + "and/or height values.");
            throw new JmeException("Invalid width and/or height values.");
        }
        this.width = width;
        this.height = height;

        worldBuffer = ByteBuffer.allocateDirect(64).order(
                ByteOrder.nativeOrder()).asFloatBuffer();

        LoggingSystem.getLogger().log(Level.INFO,
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
        return new LWJGLCamera(width, height, this);
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
     * <code>getAttributeState</code> returns a new LWJGLAttributeState object
     * as a regular AttributeState.
     *
     * @return an AttributeState object.
     */
    public AttributeState getAttributeState() {
        return new LWJGLAttributeState();
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
     * <code>getWireframeState</code> returns a new LWJGLWireframeState object
     * as a regular WireframeState.
     *
     * @return an WireframeState object.
     */
    public WireframeState getWireframeState() {
        return new LWJGLWireframeState();
    }

    /**
     * <code>getZBufferState</code> returns a new LWJGLZBufferState object as
     * a regular ZBufferState.
     *
     * @return a ZBufferState object.
     */
    public ZBufferState getZBufferState() {
        return new LWJGLZBufferState();
    }

    /**
     * <code>getVertexProgramState</code> returns a new
     * LWJGLVertexProgramState object as a regular VertexProgramState.
     *
     * @return a VertexProgramState object.
     */
    public VertexProgramState getVertexProgramState() {
        return new LWJGLVertexProgramState();
    }

    /**
     * <code>getStencilState</code> returns a new LWJGLStencilState object as
     * a regular StencilState.
     *
     * @return a StencilState object.
     */
    public StencilState getStencilState() {
        return new LWJGLStencilState();
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
        GL11.glClearColor(backgroundColor.r, backgroundColor.g,
                backgroundColor.b, backgroundColor.a);
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
        GL11.glDisable(GL11.GL_DITHER);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(0, 0, width, height);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glEnable(GL11.GL_DITHER);
    }

    /**
     * <code>clearBackBuffer</code> clears the OpenGL color buffer.
     *
     * @see com.jme.renderer.Renderer#clearBackBuffer()
     */
    public void clearBackBuffer() {
        GL11.glDisable(GL11.GL_DITHER);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(0, 0, width, height);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glEnable(GL11.GL_DITHER);
    }

    /**
     * <code>clearBuffers</code> clears both the color and the depth buffer.
     *
     * @see com.jme.renderer.Renderer#clearBuffers()
     */
    public void clearBuffers() {
        GL11.glDisable(GL11.GL_DITHER);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(0, 0, width, height);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glEnable(GL11.GL_DITHER);
    }

    /**
     * <code>displayBackBuffer</code> flips the rendered buffer (back) with
     * the currently displayed buffer.
     *
     * @see com.jme.renderer.Renderer#displayBackBuffer()
     */
    public void displayBackBuffer() {
        GL11.glFlush();
        Window.update();
    }

    public void setOrtho() {
        if (inOrthoMode) { throw new JmeException(
                "Already in Orthographic mode."); }
        //set up ortho mode
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GLU.gluOrtho2D(0, Window.getWidth(), 0, Window.getHeight());
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        inOrthoMode = true;
    }

    public void setOrthoCenter() {
        if (inOrthoMode) { throw new JmeException(
                "Already in Orthographic mode."); }
        //set up ortho mode
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GLU.gluOrtho2D(-Window.getWidth() / 2, Window.getWidth() / 2, -Window
                .getHeight() / 2, Window.getHeight() / 2);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        inOrthoMode = true;
    }

    public void unsetOrtho() {
        if (!inOrthoMode) { throw new JmeException("Not in Orthographic mode."); }
        //remove ortho mode, and go back to original
        // state
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
        inOrthoMode = false;
    }

    /**
     * <code>takeScreenShot</code> saves the current buffer to a file. The
     * file name is provided, and .png will be appended. True is returned if the
     * capture was successful, false otherwise.
     *
     * @param filename
     *            the name of the file to save.
     * @return true if successful, false otherwise.
     */
    public boolean takeScreenShot(String filename) {
        if (null == filename) { throw new JmeException(
                "Screenshot filename cannot be null"); }
        LoggingSystem.getLogger().log(Level.INFO,
                "Taking screenshot: " + filename + ".png");

        //Create a pointer to the image info and create a buffered image to
        //hold it.
        IntBuffer buff = ByteBuffer.allocateDirect(width * height * 4).order(
                ByteOrder.nativeOrder()).asIntBuffer();
        GL11.glReadPixels(0, 0, width, height, GL12.GL_BGRA,
                GL11.GL_UNSIGNED_BYTE, buff);
        BufferedImage img = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);

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
            LoggingSystem.getLogger().log(Level.WARNING,
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
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GLU.gluOrtho2D(0, Window.getWidth(), 0, Window.getHeight());
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();

        // set the color of the tint
        GL11.glColor4f(t.getTintColor().r, t.getTintColor().g,
                t.getTintColor().b, t.getTintColor().a);

        // drawQuad
        GL11.glBegin(GL11.GL_QUADS);
        {
            GL11.glVertex2f(0, 0);
            GL11.glVertex2f(0, Window.getHeight());
            GL11.glVertex2f(Window.getWidth(), Window.getHeight());
            GL11.glVertex2f(Window.getWidth(), 0);
        }
        GL11.glEnd();

        // remove ortho mode, and go back to original
        // state
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();

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
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();

        GL11.glTranslatef(translation.x, translation.y, translation.z);
        GL11.glRotatef(rot, vRot.x, vRot.y, vRot.z);
        GL11.glScalef(scale, scale, scale);

        // render the object
        GL11.glBegin(GL11.GL_POINTS);

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
                        GL11.glNormal3f(normal[i].x, normal[i].y, normal[i].z);
                        GL11.glColor4f(color[i].r, color[i].g, color[i].b,
                                color[i].a);
                        GL11.glTexCoord2f(texture[i].x, texture[i].y);
                        GL11.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
                    }
                } else {
                    // N,C
                    for (int i = 0; i < vertex.length; i++) {
                        GL11.glNormal3f(normal[i].x, normal[i].y, normal[i].z);
                        GL11.glColor4f(color[i].r, color[i].g, color[i].b,
                                color[i].a);
                        GL11.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
                    }
                }
            } else {
                if (texture != null) {
                    // N,T
                    for (int i = 0; i < vertex.length; i++) {
                        GL11.glNormal3f(normal[i].x, normal[i].y, normal[i].z);
                        GL11.glTexCoord2f(texture[i].x, texture[i].y);
                        GL11.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
                    }
                } else {
                    // N
                    for (int i = 0; i < vertex.length; i++) {
                        GL11.glNormal3f(normal[i].x, normal[i].y, normal[i].z);
                        GL11.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
                    }
                }
            }
        } else {
            if (color != null) {
                if (texture != null) {
                    // C,T
                    for (int i = 0; i < vertex.length; i++) {
                        GL11.glColor4f(color[i].r, color[i].g, color[i].b,
                                color[i].a);
                        GL11.glTexCoord2f(texture[i].x, texture[i].y);
                        GL11.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
                    }
                } else {
                    // C
                    for (int i = 0; i < vertex.length; i++) {
                        GL11.glColor4f(color[i].r, color[i].g, color[i].b,
                                color[i].a);
                        GL11.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
                    }
                }
            } else {
                if (texture != null) {
                    // T
                    for (int i = 0; i < vertex.length; i++) {
                        GL11.glTexCoord2f(texture[i].x, texture[i].y);
                        GL11.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
                    }
                } else {
                    // none
                    for (int i = 0; i < vertex.length; i++) {
                        GL11.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
                    }
                }
            }
        }

        GL11.glEnd();

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();

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
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();

        GL11.glTranslatef(translation.x, translation.y, translation.z);
        GL11.glRotatef(rot, vRot.x, vRot.y, vRot.z);
        GL11.glScalef(scale, scale, scale);

        // render the object
        GL11.glBegin(GL11.GL_LINES);

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
                        GL11.glNormal3f(normal[i].x, normal[i].y, normal[i].z);
                        GL11.glColor4f(color[i].r, color[i].g, color[i].b,
                                color[i].a);
                        GL11.glTexCoord2f(texture[i].x, texture[i].y);
                        GL11.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
                        i++;
                        GL11.glNormal3f(normal[i].x, normal[i].y, normal[i].z);
                        GL11.glColor4f(color[i].r, color[i].g, color[i].b,
                                color[i].a);
                        GL11.glTexCoord2f(texture[i].x, texture[i].y);
                        GL11.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
                    }

                } else {
                    // N,C
                    for (int i = 0; i < vertex.length - 1; i++) {
                        GL11.glNormal3f(normal[i].x, normal[i].y, normal[i].z);
                        GL11.glColor4f(color[i].r, color[i].g, color[i].b,
                                color[i].a);
                        GL11.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
                        i++;
                        GL11.glNormal3f(normal[i].x, normal[i].y, normal[i].z);
                        GL11.glColor4f(color[i].r, color[i].g, color[i].b,
                                color[i].a);
                        GL11.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
                    }
                }
            } else {
                if (texture != null) {
                    // N,T
                    for (int i = 0; i < vertex.length - 1; i++) {
                        GL11.glNormal3f(normal[i].x, normal[i].y, normal[i].z);
                        GL11.glTexCoord2f(texture[i].x, texture[i].y);
                        GL11.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
                        i++;
                        GL11.glNormal3f(normal[i].x, normal[i].y, normal[i].z);
                        GL11.glTexCoord2f(texture[i].x, texture[i].y);
                        GL11.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
                    }

                } else {
                    // N
                    for (int i = 0; i < vertex.length - 1; i++) {
                        GL11.glNormal3f(normal[i].x, normal[i].y, normal[i].z);
                        GL11.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
                        i++;
                        GL11.glNormal3f(normal[i].x, normal[i].y, normal[i].z);
                        GL11.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
                    }

                }
            }
        } else {
            if (color != null) {
                if (texture != null) {
                    // C,T
                    for (int i = 0; i < vertex.length - 1; i++) {
                        GL11.glColor4f(color[i].r, color[i].g, color[i].b,
                                color[i].a);
                        GL11.glTexCoord2f(texture[i].x, texture[i].y);
                        GL11.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
                        i++;
                        GL11.glColor4f(color[i].r, color[i].g, color[i].b,
                                color[i].a);
                        GL11.glTexCoord2f(texture[i].x, texture[i].y);
                        GL11.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
                    }

                } else {
                    // C
                    for (int i = 0; i < vertex.length - 1; i++) {
                        GL11.glColor4f(color[i].r, color[i].g, color[i].b,
                                color[i].a);
                        GL11.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
                        i++;
                        GL11.glColor4f(color[i].r, color[i].g, color[i].b,
                                color[i].a);
                        GL11.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
                    }
                }
            } else {
                if (texture != null) {
                    // T
                    for (int i = 0; i < vertex.length - 1; i++) {
                        GL11.glTexCoord2f(texture[i].x, texture[i].y);
                        GL11.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
                        i++;
                        GL11.glTexCoord2f(texture[i].x, texture[i].y);
                        GL11.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
                    }

                } else {
                    // none
                    for (int i = 0; i < vertex.length - 1; i++) {
                        GL11.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
                        i++;
                        GL11.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
                    }
                }
            }
        }

        GL11.glEnd();

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();

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
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();

        GL11.glTranslatef(translation.x, translation.y, translation.z);
        GL11.glRotatef(rot, vRot.x, vRot.y, vRot.z);
        GL11.glScalef(scale, scale, scale);

        // render the object
        GL11.glBegin(GL11.GL_LINE_STRIP);

        ColorRGBA[] color = c.getColors();
        float colorInterval = 0;
        float colorModifier = 0;
        int colorCounter = 0;
        if (null != color) {
            GL11.glColor4f(color[0].r, color[0].g, color[0].b, color[0].a);

            colorInterval = 1f / c.getColors().length;
            colorModifier = colorInterval;
            colorCounter = 0;
        }

        Vector3f point;
        float limit = (1 + (1.0f / c.getSteps()));
        for (float t = 0; t <= limit; t += 1.0f / c.getSteps()) {

            if (t >= colorInterval && color != null) {

                colorInterval += colorModifier;
                GL11.glColor4f(c.getColors()[colorCounter].r,
                        c.getColors()[colorCounter].g,
                        c.getColors()[colorCounter].b,
                        c.getColors()[colorCounter].a);
                colorCounter++;
            }

            point = c.getPoint(t);
            GL11.glVertex3f(point.x, point.y, point.z);
        }

        if (statisticsOn) {
            numberOfVerts += limit;
        }

        GL11.glEnd();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
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
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();

        GL11.glTranslatef(translation.x, translation.y, translation.z);
        GL11.glRotatef(rot, vRot.x, vRot.y, vRot.z);
        GL11.glScalef(scale, scale, scale);

        prepVBO(t);

        // render the object

        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        if (t.isVBOVertexEnabled() && GLContext.GL_ARB_vertex_buffer_object) {
          ARBBufferObject.glBindBufferARB( GL15.GL_ARRAY_BUFFER, t.getVBOVertexID());
          GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);
          ARBBufferObject.glBindBufferARB( GL15.GL_ARRAY_BUFFER, 0);
        } else {
          GL11.glVertexPointer(3, 0, t.getVerticeAsFloatBuffer());
        }

        FloatBuffer normals = t.getNormalAsFloatBuffer();
        if (normals != null || t.getVBONormalID() > 0) {
          GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
          if (t.isVBONormalEnabled() && GLContext.GL_ARB_vertex_buffer_object) {
            ARBBufferObject.glBindBufferARB( GL15.GL_ARRAY_BUFFER, t.getVBONormalID());
            GL11.glNormalPointer(GL11.GL_FLOAT, 0, 0);
            ARBBufferObject.glBindBufferARB( GL15.GL_ARRAY_BUFFER, 0);
          } else {
            GL11.glNormalPointer(0, normals);
          }
        } else {
          GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
        }

        FloatBuffer colors = t.getColorAsFloatBuffer();
        if (colors != null || t.getVBOColorID() > 0) {
          GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
          if (t.isVBOColorEnabled() && GLContext.GL_ARB_vertex_buffer_object) {
            ARBBufferObject.glBindBufferARB( GL15.GL_ARRAY_BUFFER, t.getVBOColorID());
            GL11.glColorPointer(4, GL11.GL_FLOAT, 0, 0);
            ARBBufferObject.glBindBufferARB( GL15.GL_ARRAY_BUFFER, 0);
          } else {
            GL11.glColorPointer(4, 0, colors);
          }
        } else {
          GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
        }

        for (int i = 0; i < t.getNumberOfUnits(); i++) {
          FloatBuffer textures = t.getTextureAsFloatBuffer(i);
          if (textures != null) {
            if (GLContext.GL_ARB_multitexture && GLContext.OpenGL13) {
              GL13.glClientActiveTexture(GL13.GL_TEXTURE0 + i);
            }
            if (textures != null || t.getVBOTextureID(i) > 0) {

              GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
              if (t.isVBOTextureEnabled() && GLContext.GL_ARB_vertex_buffer_object) {
                ARBBufferObject.glBindBufferARB( GL15.GL_ARRAY_BUFFER, t.getVBOTextureID(i));
                GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);
                ARBBufferObject.glBindBufferARB( GL15.GL_ARRAY_BUFFER, 0);
              } else {
                GL11.glTexCoordPointer(2, 0, textures);
              }
            } else {
              GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            }
          }
        }

        IntBuffer indices = t.getIndexAsBuffer();
        if (statisticsOn) {
          int adder = t.getIndexAsBuffer().capacity();
          int vertAdder = t.getIndexAsBuffer().capacity();
          numberOfTris += adder / 3;
          numberOfVerts += vertAdder;
        }
        GL11.glDrawElements(GL11.GL_TRIANGLES, indices);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
    }

    IntBuffer buf = BufferUtils.createIntBuffer(16);
    public void prepVBO(Geometry g) {
      if (!GLContext.GL_ARB_vertex_buffer_object) return;
      if (g.isVBOVertexEnabled() && g.getVBOVertexID() <= 0) {
        ARBBufferObject.glGenBuffersARB(buf);
        g.setVBOVertexID(buf.get(0));
        ARBBufferObject.glBindBufferARB( GL15.GL_ARRAY_BUFFER, g.getVBOVertexID() );
        ARBBufferObject.glBufferDataARB( GL15.GL_ARRAY_BUFFER, g.getVertQuantity()*3*4, g.getVerticeAsFloatBuffer(), ARBBufferObject.GL_STATIC_DRAW_ARB);
        buf.clear();
      }
      if (g.isVBONormalEnabled() && g.getVBONormalID() <= 0) {
        ARBBufferObject.glGenBuffersARB(buf);
        g.setVBONormalID(buf.get(0));
        ARBBufferObject.glBindBufferARB( GL15.GL_ARRAY_BUFFER, g.getVBONormalID() );
        ARBBufferObject.glBufferDataARB( GL15.GL_ARRAY_BUFFER, g.getTextures().length*3*4, g.getNormalAsFloatBuffer(), ARBBufferObject.GL_STATIC_DRAW_ARB);
        buf.clear();
      }
      if (g.isVBOColorEnabled() && g.getVBOColorID() <= 0) {
        ARBBufferObject.glGenBuffersARB(buf);
        g.setVBOColorID(buf.get(0));
        ARBBufferObject.glBindBufferARB( GL15.GL_ARRAY_BUFFER, g.getVBOColorID() );
        ARBBufferObject.glBufferDataARB( GL15.GL_ARRAY_BUFFER, g.getTextures().length*4*4, g.getColorAsFloatBuffer(), ARBBufferObject.GL_STATIC_DRAW_ARB);
        buf.clear();
      }
      if (g.isVBOTextureEnabled()) {
        for (int i = 0; i < g.getNumberOfUnits(); i++) {

          if (g.getVBOTextureID(i) <= 0) {
            ARBBufferObject.glGenBuffersARB(buf);
            g.setVBOTextureID(i, buf.get(0));
            ARBBufferObject.glBindBufferARB(GL15.GL_ARRAY_BUFFER,
                                            g.getVBOTextureID(i));
            ARBBufferObject.glBufferDataARB(GL15.GL_ARRAY_BUFFER,
                                            g.getTextures().length * 2 * 4,
                                            g.getTextureAsFloatBuffer(i),
                                            ARBBufferObject.GL_STATIC_DRAW_ARB);
            buf.clear();
          }
        }
      }
      buf.clear();
      ARBBufferObject.glBindBufferARB( GL15.GL_ARRAY_BUFFER, 0 );
    }

    /**
     * <code>draw</code> renders a <code>TriMesh</code> object including
     * it's normals, colors, textures and vertices.
     *
     * @see com.jme.renderer.Renderer#draw(com.jme.scene.TriMesh)
     * @param t
     *            the mesh to render.
     */
    public void drawBounds(Geometry g) {
        // get the bounds
        if (!(g.getWorldBound() instanceof TriMesh)) return;
        drawBounds(g.getWorldBound());
    }

    /**
     * <code>draw</code> renders a <code>TriMesh</code> object including
     * it's normals, colors, textures and vertices.
     *
     * @see com.jme.renderer.Renderer#draw(com.jme.scene.TriMesh)
     * @param t
     *            the mesh to render.
     */
    public void drawBounds(BoundingVolume bv) {
        // get the bounds
        if (!(bv instanceof TriMesh)) return;
        bv.recomputeMesh();
        setBoundsStates(true);
        draw((TriMesh) bv);
        setBoundsStates(false);
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

        GL11.glVertexPointer(3, 0, t.getVerticeAsFloatBuffer());
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);

        FloatBuffer normals = t.getNormalAsFloatBuffer();
        if (normals != null) {
            GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
            GL11.glNormalPointer(0, normals);
        } else {
            GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
        }

        FloatBuffer colors = t.getColorAsFloatBuffer();
        if (colors != null) {
            GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
            GL11.glColorPointer(4, 0, colors);
        } else {
            GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
        }

        FloatBuffer textures = t.getTextureAsFloatBuffer();
        if (textures != null) {
            GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            GL11.glTexCoordPointer(2, 0, textures);
        } else {
            GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
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
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();

        GL11.glTranslatef(translation.x, translation.y, translation.z);
        GL11.glRotatef(rot, vRot.x, vRot.y, vRot.z);
        GL11.glScalef(scale, scale, scale);

        IntBuffer indices = c.getIndexBuffer();
        if (statisticsOn) {
            int adder = indices.capacity();
            numberOfTris += adder / 3;
        }

        GL11.glDrawElements(GL11.GL_TRIANGLES, indices);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
    }

    /**
     * <code>draw</code> renders a scene by calling the nodes
     * <code>onDraw</code> method.
     *
     * @see com.jme.renderer.Renderer#draw(com.jme.scene.Spatial)
     */
    public void draw(Spatial s) {
        if (s != null) {
            s.onDraw(this);
        }

    }

    /**
     * <code>drawBounds</code> renders a scene by calling the nodes
     * <code>onDraw</code> method.
     *
     * @see com.jme.renderer.Renderer#draw(com.jme.scene.Spatial)
     */
    public void drawBounds(Spatial s) {
        if (s != null) {
            s.onDrawBounds(this);
        }

    }

    /**
     * <code>drawBounds</code> renders the bounds of a scene by calling the
     * nodes <code>onDrawBounds</code> method.
     *
     * @see com.jme.renderer.Renderer#drawBounds(com.jme.scene.Spatial)
     */
    public void drawBounds(Clone c) {
        drawBounds(c.getWorldBound());
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

        font.print((int) t.getLocalTranslation().x, (int) t
                .getLocalTranslation().y, t.getLocalScale(), t.getText(), 0);
    }

    /**
     * <code>draw</code> renders a mouse object using a defined mouse texture.
     *
     * @see com.jme.renderer.Renderer#draw(com.jme.input.Mouse)
     */
    public void draw(Mouse m) {
        if (!m.hasCursor()) { return; }

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0, Window.getWidth(), 0, Window.getHeight(), -1, 1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glTranslatef(m.getLocalTranslation().x, m.getLocalTranslation().y,
                0);

        //render the cursor
        int width = m.getImageWidth();
        int height = m.getImageHeight();

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f(0, 0);

        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2f(width, 0);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2f(width, height);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2f(0, height);
        GL11.glEnd();

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
    }

    /**
     * <code>draw</code> renders a WidgetRenderer object to the back buffer.
     *
     * @see com.jme.renderer.Renderer#draw(WidgetRenderer)
     */
    public void draw(WidgetRenderer wr) {
        wr.render();
    }

    /**
     * <code>enableStatistics</code> will turn on statistics gathering.
     *
     * @param value
     *            true to use statistics, false otherwise.
     */
    public void enableStatistics(boolean value) {
        statisticsOn = value;
    }

    /**
     * <code>clearStatistics</code> resets the vertices and triangles counter
     * for the statistics information.
     */
    public void clearStatistics() {
        numberOfVerts = 0;
        numberOfTris = 0;
    }

    /**
     * <code>getStatistics</code> returns a string value of the rendering
     * statistics information (number of triangles and number of vertices).
     *
     * @return the string representation of the current statistics.
     */
    public String getStatistics() {
        return "Number of Triangles: " + numberOfTris
                + " : Number of Vertices: " + numberOfVerts;
    }

    /**
     *
     * <code>setBoundsStates</code> sets the rendering states for bounding
     * volumes, this includes wireframe and zbuffer.
     *
     * @param enabled
     *            true if these states are to be enabled, false otherwise.
     */
    private void setBoundsStates(boolean enabled) {
        boundsTextState.apply(); // no enabled -- no texture

        boundsWireState.setEnabled(enabled);
        boundsWireState.apply();

        boundsZState.setEnabled(enabled);
        boundsZState.apply();
    }

}
