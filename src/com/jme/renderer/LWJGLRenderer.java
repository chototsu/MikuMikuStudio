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
package com.jme.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.logging.Level;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.Window;

import com.jme.input.Mouse;
import com.jme.math.Matrix3f;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.Line;
import com.jme.scene.Point;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.DitherState;
import com.jme.scene.state.FogState;
import com.jme.scene.state.LWJGLAlphaState;
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

/**
 * <code>LWJGLRenderer</code> provides an implementation of the 
 * <code>Renderer</code> interface using the LWJGL API.
 * @see com.jme.renderer.Renderer
 * @author Mark Powell
 * @version $Id: LWJGLRenderer.java,v 1.7 2003-12-02 20:08:09 mojomonkey Exp $
 */
public class LWJGLRenderer implements Renderer {
    //clear color
    private ColorRGBA backgroundColor;
    //width and height of renderer
    private int width;
    private int height;

    private FloatBuffer worldBuffer;

    private LWJGLCamera camera;
    private LWJGLFont font;

    /**
     * Constructor instantiates a new <code>LWJGLRenderer</code> object. The
     * size of the rendering window is passed during construction.
     * @param width the width of the rendering context.
     * @param height the height of the rendering context.
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
     * <code>setCullingMode</code> defines which side of a triangle (if any) 
     * will be culled. Front means the side the normal faces will be culled,
     * back means the side opposite the normal will be culled.
     * @param mode the side to cull.
     */
    public void setCullingMode(int mode) {
        switch(mode) {
            case CULL_FRONT:
                GL.glCullFace(GL.GL_FRONT);
                GL.glEnable(GL.GL_CULL_FACE);
                break;
            case CULL_BACK:
                GL.glCullFace(GL.GL_BACK);
                GL.glEnable(GL.GL_CULL_FACE);
                break;
            case CULL_NONE:
                GL.glCullFace(GL.GL_NONE);
                GL.glEnable(GL.GL_CULL_FACE);
                break;
            default:
                GL.glDisable(GL.GL_CULL_FACE);
                break;
        }
    }

    /**
     * <code>setCamera</code> sets the camera this renderer is using. It
     * asserts that the camera is of type <code>LWJGLCamera</code>.
     * @see com.jme.renderer.Renderer#setCamera(com.jme.renderer.Camera)
     */
    public void setCamera(Camera camera) {
        if (camera instanceof LWJGLCamera) {
            this.camera = (LWJGLCamera) camera;
        }
    }

    /**
     * <code>getCamera</code> returns the camera used by this renderer.
     * @see com.jme.renderer.Renderer#getCamera()
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * <code>getCamera</code> returns a default camera for use with the 
     * LWJGL renderer. 
     * 
     * @param width the width of the frame.
     * @param height the height of the frame.
     * @return a default LWJGL camera.
     */
    public Camera getCamera(int width, int height) {
        return new LWJGLCamera(width, height);
    }

    /**
     * <code>getAlphaState</code> returns a new LWJGLAlphaState object as
     * a regular AlphaState. 
     * @return an AlphaState object.
     */
    public AlphaState getAlphaState() {
        return new LWJGLAlphaState();
    }

    /**
     * <code>getDitherState</code> returns a new LWJGLDitherState object as
     * a regular DitherState. 
     * @return an DitherState object.
     */
    public DitherState getDitherState() {
        return new LWJGLDitherState();
    }

    /**
     * <code>getFogState</code> returns a new LWJGLFogState object as
     * a regular FogState. 
     * @return an FogState object.
     */
    public FogState getFogState() {
        return new LWJGLFogState();
    }

    /**
     * <code>getLightState</code> returns a new LWJGLLightState object as
     * a regular LightState. 
     * @return an LightState object.
     */
    public LightState getLightState() {
        return new LWJGLLightState();
    }

    /**
     * <code>getMaterialState</code> returns a new LWJGLMaterialState object as
     * a regular MaterialState. 
     * @return an MaterialState object.
     */
    public MaterialState getMaterialState() {
        return new LWJGLMaterialState();
    }

    /**
     * <code>getShadeState</code> returns a new LWJGLShadeState object as
     * a regular ShadeState. 
     * @return an ShadeState object.
     */
    public ShadeState getShadeState() {
        return new LWJGLShadeState();
    }

    /**
     * <code>getTextureState</code> returns a new LWJGLTextureState object as
     * a regular TextureState. 
     * @return an TextureState object.
     */
    public TextureState getTextureState() {
        return new LWJGLTextureState();
    }

    /**
     * <code>getWireframeState</code> returns a new LWJGLWireframeState object as
     * a regular WireframeState. 
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
     * @param c the color to set the background color to.
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
     * @see com.jme.renderer.Renderer#getBackgroundColor()
     * @return the current clear color.
     */
    public ColorRGBA getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * <code>clearZBuffer</code> clears the OpenGL depth buffer.
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
     * @see com.jme.renderer.Renderer#displayBackBuffer()
     */
    public void displayBackBuffer() {
        GL.glFlush();
        Window.paint();
        Window.update();
    }

    /**
     * <code>draw</code> draws a point object where a point contains a 
     * collection of vertices, normals, colors and texture coordinates.
     * @see com.jme.renderer.Renderer#draw(com.jme.scene.Point)
     * @param p the point object to render.
     */
    public void draw(Point p) {
        // set world matrix
        Matrix3f rotation = p.getWorldRotation();
        Vector3f translation = p.getWorldTranslation();
        float scale = p.getWorldScale();
        float[] modelToWorld =
            {
                scale * rotation.get(0, 0),
                scale * rotation.get(1, 0),
                scale * rotation.get(2, 0),
                0.0f,
                scale * rotation.get(0, 1),
                scale * rotation.get(1, 1),
                scale * rotation.get(2, 1),
                0.0f,
                scale * rotation.get(0, 2),
                scale * rotation.get(1, 2),
                scale * rotation.get(2, 2),
                0.0f,
                translation.x,
                translation.y,
                translation.z,
                1.0f };

        GL.glMatrixMode(GL.GL_MODELVIEW);
        GL.glPushMatrix();
        worldBuffer.clear();
        worldBuffer.put(modelToWorld);
        worldBuffer.flip();
        GL.glMultMatrixf(worldBuffer);

        // render the object
        GL.glBegin(GL.GL_POINTS);

        // draw points
        Vector3f[] vertex = p.getVertices();
        Vector3f[] normal = p.getNormals();
        ColorRGBA[] color = p.getColors();
        Vector2f[] texture = p.getTextures();

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
     * @see com.jme.renderer.Renderer#draw(com.jme.scene.Line)
     * @param l the line object to render.
     */
    public void draw(Line l) {
        // set world matrix
        Matrix3f rotation = l.getWorldRotation();
        Vector3f translation = l.getWorldTranslation();
        float scale = l.getWorldScale();
        float[] modelToWorld =
            {
                scale * rotation.get(0, 0),
                scale * rotation.get(1, 0),
                scale * rotation.get(2, 0),
                0.0f,
                scale * rotation.get(0, 1),
                scale * rotation.get(1, 1),
                scale * rotation.get(2, 1),
                0.0f,
                scale * rotation.get(0, 2),
                scale * rotation.get(1, 2),
                scale * rotation.get(2, 2),
                0.0f,
                translation.x,
                translation.y,
                translation.z,
                1.0f };

        GL.glMatrixMode(GL.GL_MODELVIEW);
        GL.glPushMatrix();
        worldBuffer.clear();
        worldBuffer.put(modelToWorld);
        worldBuffer.flip();
        GL.glMultMatrixf(worldBuffer);

        // render the object
        GL.glBegin(GL.GL_LINES);

        // draw line
        Vector3f[] vertex = l.getVertices();
        Vector3f[] normal = l.getNormals();
        ColorRGBA[] color = l.getColors();
        Vector2f[] texture = l.getTextures();

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
                        GL.glNormal3f(
                            normal[i + 1].x,
                            normal[i + 1].y,
                            normal[i + 1].z);
                        GL.glColor4f(
                            color[i + 1].r,
                            color[i + 1].g,
                            color[i + 1].b,
                            color[i + 1].a);
                        GL.glTexCoord2f(texture[i].x, texture[i].y);
                        GL.glVertex3f(
                            vertex[i + 1].x,
                            vertex[i + 1].y,
                            vertex[i + 1].z);
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
                        GL.glNormal3f(
                            normal[i + 1].x,
                            normal[i + 1].y,
                            normal[i + 1].z);
                        GL.glColor4f(
                            color[i + 1].r,
                            color[i + 1].g,
                            color[i + 1].b,
                            color[i + 1].a);
                        GL.glVertex3f(
                            vertex[i + 1].x,
                            vertex[i + 1].y,
                            vertex[i + 1].z);
                    }
                }
            } else {
                if (texture != null) {
                    // N,T
                    for (int i = 0; i < vertex.length - 1; i++) {
                        GL.glNormal3f(normal[i].x, normal[i].y, normal[i].z);
                        GL.glTexCoord2f(texture[i].x, texture[i].y);
                        GL.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
                        GL.glNormal3f(
                            normal[i + 1].x,
                            normal[i + 1].y,
                            normal[i + 1].z);
                        GL.glTexCoord2f(texture[i].x, texture[i].y);
                        GL.glVertex3f(
                            vertex[i + 1].x,
                            vertex[i + 1].y,
                            vertex[i + 1].z);
                    }

                } else {
                    // N
                    for (int i = 0; i < vertex.length - 1; i++) {
                        GL.glNormal3f(normal[i].x, normal[i].y, normal[i].z);
                        GL.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
                        GL.glNormal3f(
                            normal[i + 1].x,
                            normal[i + 1].y,
                            normal[i + 1].z);
                        GL.glVertex3f(
                            vertex[i + 1].x,
                            vertex[i + 1].y,
                            vertex[i + 1].z);
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
                        GL.glColor4f(
                            color[i + 1].r,
                            color[i + 1].g,
                            color[i + 1].b,
                            color[i + 1].a);
                        GL.glTexCoord2f(texture[i].x, texture[i].y);
                        GL.glVertex3f(
                            vertex[i + 1].x,
                            vertex[i + 1].y,
                            vertex[i + 1].z);
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
                        GL.glColor4f(
                            color[i + 1].r,
                            color[i + 1].g,
                            color[i + 1].b,
                            color[i + 1].a);
                        GL.glVertex3f(
                            vertex[i + 1].x,
                            vertex[i + 1].y,
                            vertex[i + 1].z);
                    }
                }
            } else {
                if (texture != null) {
                    // T
                    for (int i = 0; i < vertex.length - 1; i++) {
                        GL.glTexCoord2f(texture[i].x, texture[i].y);
                        GL.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
                        GL.glTexCoord2f(texture[i].x, texture[i].y);
                        GL.glVertex3f(
                            vertex[i + 1].x,
                            vertex[i + 1].y,
                            vertex[i + 1].z);
                    }

                } else {
                    // none
                    for (int i = 0; i < vertex.length - 1; i++) {
                        GL.glVertex3f(vertex[i].x, vertex[i].y, vertex[i].z);
                        GL.glVertex3f(
                            vertex[i + 1].x,
                            vertex[i + 1].y,
                            vertex[i + 1].z);
                    }
                }
            }
        }

        GL.glEnd();

        GL.glMatrixMode(GL.GL_MODELVIEW);
        GL.glPopMatrix();

    }

    /**
     * <code>draw</code> renders a <code>TriMesh</code> object including it's
     * normals, colors, textures and vertices.
     * @see com.jme.renderer.Renderer#draw(com.jme.scene.TriMesh)
     * @param t the mesh to render.
     */
    public void draw(TriMesh t) {
        // set world matrix
        Matrix3f rotation = t.getWorldRotation();
        Vector3f translation = t.getWorldTranslation();
        float scale = t.getWorldScale();
        
        float[] modelToWorld =
            {
                scale * rotation.get(0, 0),
                scale * rotation.get(1, 0),
                scale * rotation.get(2, 0),
                0.0f,
                scale * rotation.get(0, 1),
                scale * rotation.get(1, 1),
                scale * rotation.get(2, 1),
                0.0f,
                scale * rotation.get(0, 2),
                scale * rotation.get(1, 2),
                scale * rotation.get(2, 2),
                0.0f,
                translation.x,
                translation.y,
                translation.z,
                1.0f };

        GL.glMatrixMode(GL.GL_MODELVIEW);
        GL.glPushMatrix();
        worldBuffer.clear();
        worldBuffer.put(modelToWorld);
        worldBuffer.flip();
        GL.glMultMatrixf(worldBuffer);

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

        GL.glDrawElements(GL.GL_TRIANGLES, t.getIndexAsBuffer());

        GL.glMatrixMode(GL.GL_MODELVIEW);
        GL.glPopMatrix();
    }

    /**
     * <code>draw</code> renders a scene by calling the nodes <code>onDraw</code>
     * method.
     * @see com.jme.renderer.Renderer#draw(com.jme.scene.Spatial)
     */
    public void draw(Spatial s) {
        if (s != null) {
            s.onDraw(this);
        }
    }

    /**
     * <code>draw</code> renders a text object using a predefined font.
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
     * <code>draw</code> renders a mouse object using a defined mouse texture.
     * @see com.jme.renderer.Renderer#draw(com.jme.input.Mouse)
     */
    public void draw(Mouse m) {
        if(!m.hasCursor()) {
            return;
        }
        
        GL.glMatrixMode(GL.GL_PROJECTION);
        GL.glPushMatrix();
        GL.glLoadIdentity();
        GL.glOrtho(0, Window.getWidth(), 0, Window.getHeight(), -1, 1);
        GL.glMatrixMode(GL.GL_MODELVIEW);
        GL.glPushMatrix();
        GL.glLoadIdentity();
        GL.glTranslatef(m.getLocalTranslation().x, m.getLocalTranslation().y, 0);
        
        //render the cursor
        int width = m.getImageWidth();
        int height = m.getImageHeight();
        
        GL.glBegin(GL.GL_QUADS);
        GL.glTexCoord2f(0, 0);
        GL.glVertex2f(0,0);
        
        GL.glTexCoord2f(1, 0);
        GL.glVertex2f(width,0);
        GL.glTexCoord2f(1, 1);
        GL.glVertex2f(width,height);
        GL.glTexCoord2f(0, 1);
        GL.glVertex2f(0,height);
        GL.glEnd();
        
        GL.glMatrixMode(GL.GL_PROJECTION);
        GL.glPopMatrix();
        GL.glMatrixMode(GL.GL_MODELVIEW);
        GL.glPopMatrix();
    }

}
