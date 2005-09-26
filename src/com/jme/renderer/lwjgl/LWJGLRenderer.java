/*
 * Copyright (c) 2003-2005 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme.renderer.lwjgl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.glu.GLU;

import com.jme.curve.Curve;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.RenderQueue;
import com.jme.renderer.Renderer;
import com.jme.scene.CompositeMesh;
import com.jme.scene.Geometry;
import com.jme.scene.Line;
import com.jme.scene.Point;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.VBOInfo;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.AttributeState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.DitherState;
import com.jme.scene.state.FogState;
import com.jme.scene.state.FragmentProgramState;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.ShadeState;
import com.jme.scene.state.StencilState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.VertexProgramState;
import com.jme.scene.state.WireframeState;
import com.jme.scene.state.ZBufferState;
import com.jme.scene.state.lwjgl.LWJGLAlphaState;
import com.jme.scene.state.lwjgl.LWJGLAttributeState;
import com.jme.scene.state.lwjgl.LWJGLCullState;
import com.jme.scene.state.lwjgl.LWJGLDitherState;
import com.jme.scene.state.lwjgl.LWJGLFogState;
import com.jme.scene.state.lwjgl.LWJGLFragmentProgramState;
import com.jme.scene.state.lwjgl.LWJGLLightState;
import com.jme.scene.state.lwjgl.LWJGLMaterialState;
import com.jme.scene.state.lwjgl.LWJGLShadeState;
import com.jme.scene.state.lwjgl.LWJGLShaderObjectsState;
import com.jme.scene.state.lwjgl.LWJGLStencilState;
import com.jme.scene.state.lwjgl.LWJGLTextureState;
import com.jme.scene.state.lwjgl.LWJGLVertexProgramState;
import com.jme.scene.state.lwjgl.LWJGLWireframeState;
import com.jme.scene.state.lwjgl.LWJGLZBufferState;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;

/**
 * <code>LWJGLRenderer</code> provides an implementation of the
 * <code>Renderer</code> interface using the LWJGL API.
 * 
 * @see com.jme.renderer.Renderer
 * @author Mark Powell
 * @author Joshua Slack - Optimizations and Headless rendering
 * @author Tijl Houtbeckers - Small optimizations
 * @version $Id: LWJGLRenderer.java,v 1.83 2005-09-26 21:42:56 renanse Exp $
 */
public class LWJGLRenderer extends Renderer {

    private Vector3f vRot = new Vector3f();

    private LWJGLFont font;

    private boolean usingVBO = false;

    private boolean ignoreVBO = false;

    private boolean inOrthoMode;

    private Vector3f tempVa = new Vector3f();

    private DisplayMode mode = Display.getDisplayMode();

    private FloatBuffer prevVerts;

    private FloatBuffer prevNorms;

    private FloatBuffer prevColor;

    private FloatBuffer[] prevTex;
    
    private ContextCapabilities capabilities;

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

        LoggingSystem.getLogger().log(Level.INFO,
                "LWJGLRenderer created. W:  " + width + "H: " + height);
        
        capabilities = GLContext.getCapabilities();
        
        queue = new RenderQueue(this);
        if (TextureState.getNumberOfUnits() == -1) createTextureState(); // force units population
        prevTex = new FloatBuffer[TextureState.getNumberOfUnits()];
    }

    /**
     * Reinitialize the renderer with the given width/height. Also calls resize
     * on the attached camera if present.
     * 
     * @param width
     *            int
     * @param height
     *            int
     */
    public void reinit(int width, int height) {
        if (width <= 0 || height <= 0) {
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Invalid width " + "and/or height values.");
            throw new JmeException("Invalid width and/or height values.");
        }
        this.width = width;
        this.height = height;
        if (camera != null)
            camera.resize(width, height);
        mode = Display.getDisplayMode();
        capabilities = GLContext.getCapabilities();
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
     * <code>createCamera</code> returns a default camera for use with the
     * LWJGL renderer.
     * 
     * @param width
     *            the width of the frame.
     * @param height
     *            the height of the frame.
     * @return a default LWJGL camera.
     */
    public Camera createCamera(int width, int height) {
        return new LWJGLCamera(width, height, this);
    }

    /**
     * <code>createAlphaState</code> returns a new LWJGLAlphaState object as a
     * regular AlphaState.
     * 
     * @return an AlphaState object.
     */
    public AlphaState createAlphaState() {
        return new LWJGLAlphaState();
    }

    /**
     * <code>createAttributeState</code> returns a new LWJGLAttributeState
     * object as a regular AttributeState.
     * 
     * @return an AttributeState object.
     */
    public AttributeState createAttributeState() {
        return new LWJGLAttributeState();
    }

    /**
     * <code>createCullState</code> returns a new LWJGLCullState object as a
     * regular CullState.
     * 
     * @return a CullState object.
     * @see com.jme.renderer.Renderer#createCullState()
     */
    public CullState createCullState() {
        return new LWJGLCullState();
    }

    /**
     * <code>createDitherState</code> returns a new LWJGLDitherState object as
     * a regular DitherState.
     * 
     * @return an DitherState object.
     */
    public DitherState createDitherState() {
        return new LWJGLDitherState();
    }

    /**
     * <code>createFogState</code> returns a new LWJGLFogState object as a
     * regular FogState.
     * 
     * @return an FogState object.
     */
    public FogState createFogState() {
        return new LWJGLFogState();
    }

    /**
     * <code>createLightState</code> returns a new LWJGLLightState object as a
     * regular LightState.
     * 
     * @return an LightState object.
     */
    public LightState createLightState() {
        return new LWJGLLightState();
    }

    /**
     * <code>createMaterialState</code> returns a new LWJGLMaterialState
     * object as a regular MaterialState.
     * 
     * @return an MaterialState object.
     */
    public MaterialState createMaterialState() {
        return new LWJGLMaterialState();
    }

    /**
     * <code>createShadeState</code> returns a new LWJGLShadeState object as a
     * regular ShadeState.
     * 
     * @return an ShadeState object.
     */
    public ShadeState createShadeState() {
        return new LWJGLShadeState();
    }

    /**
     * <code>createTextureState</code> returns a new LWJGLTextureState object
     * as a regular TextureState.
     * 
     * @return an TextureState object.
     */
    public TextureState createTextureState() {
        return new LWJGLTextureState();
    }

    /**
     * <code>createWireframeState</code> returns a new LWJGLWireframeState
     * object as a regular WireframeState.
     * 
     * @return an WireframeState object.
     */
    public WireframeState createWireframeState() {
        return new LWJGLWireframeState();
    }

    /**
     * <code>createZBufferState</code> returns a new LWJGLZBufferState object
     * as a regular ZBufferState.
     * 
     * @return a ZBufferState object.
     */
    public ZBufferState createZBufferState() {
        return new LWJGLZBufferState();
    }

    /**
     * <code>createVertexProgramState</code> returns a new
     * LWJGLVertexProgramState object as a regular VertexProgramState.
     * 
     * @return a LWJGLVertexProgramState object.
     */
    public VertexProgramState createVertexProgramState() {
        return new LWJGLVertexProgramState();
    }

    /**
     * <code>createFragmentProgramState</code> returns a new
     * LWJGLFragmentProgramState object as a regular FragmentProgramState.
     * 
     * @return a LWJGLFragmentProgramState object.
     */
    public FragmentProgramState createFragmentProgramState() {
        return new LWJGLFragmentProgramState();
    }

    /**
     * <code>createShaderObjectsState</code> returns a new
     * LWJGLShaderObjectsState object as a regular ShaderObjectsState.
     * 
     * @return an ShaderObjectsState object.
     */
    public GLSLShaderObjectsState createGLSLShaderObjectsState() {
        return new LWJGLShaderObjectsState();
    }

    /**
     * <code>createStencilState</code> returns a new LWJGLStencilState object
     * as a regular StencilState.
     * 
     * @return a StencilState object.
     */
    public StencilState createStencilState() {
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
        // if color is null set background to white.
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
     * <code>clearZBuffer</code> clears the OpenGL depth buffer.
     * 
     * @see com.jme.renderer.Renderer#clearZBuffer()
     */
    public void clearZBuffer() {
        Spatial.clearCurrentState( RenderState.RS_ZBUFFER );
        if (Spatial.defaultStateList[RenderState.RS_ZBUFFER] != null)
            Spatial.defaultStateList[RenderState.RS_ZBUFFER].apply();
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
    }

    /**
     * <code>clearBackBuffer</code> clears the OpenGL color buffer.
     * 
     * @see com.jme.renderer.Renderer#clearColorBuffer()
     */
    public void clearColorBuffer() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
    }

    /**
     * <code>clearBuffers</code> clears both the color and the depth buffer.
     * 
     * @see com.jme.renderer.Renderer#clearBuffers()
     */
    public void clearBuffers() {
        clearColorBuffer();
        clearZBuffer();
    }

    /**
     * <code>clearBuffers</code> clears both the color and the depth buffer
     * for only the part of the buffer defined by the renderer width/height.
     * 
     * @see com.jme.renderer.Renderer#clearBuffers()
     */
    public void clearStrictBuffers() {
        GL11.glDisable(GL11.GL_DITHER);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(0, 0, width, height);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glEnable(GL11.GL_DITHER);
    }

    /**
     * <code>displayBackBuffer</code> renders any queued items then flips the
     * rendered buffer (back) with the currently displayed buffer.
     * 
     * @see com.jme.renderer.Renderer#displayBackBuffer()
     */
    public void displayBackBuffer() {
        renderQueue();

        if (Spatial.getCurrentState(RenderState.RS_ZBUFFER) != null
                && !((ZBufferState) Spatial
                .getCurrentState(RenderState.RS_ZBUFFER)).isWritable()) {
            if (Spatial.defaultStateList[RenderState.RS_ZBUFFER] != null)
                Spatial.defaultStateList[RenderState.RS_ZBUFFER].apply();
            Spatial.clearCurrentState(RenderState.RS_ZBUFFER);
        }

        prevColor = prevNorms = prevVerts = null;
        Arrays.fill(prevTex, null);

        GL11.glFlush();
        if (!isHeadless())
            Display.update();
    }

    
    /**
     * 
     * <code>setOrtho</code> sets the display system to be in orthographic
     * mode. If the system has already been set to orthographic mode a
     * <code>JmeException</code> is thrown. The origin (0,0) is the bottom
     * left of the screen.
     *  
     */
    public void setOrtho() {
        if (inOrthoMode) {
            throw new JmeException("Already in Orthographic mode.");
        }
        // set up ortho mode
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GLU.gluOrtho2D(0, mode.getWidth(), 0, mode.getHeight());
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        inOrthoMode = true;
    }

    public void setOrthoCenter() {
        if (inOrthoMode) {
            throw new JmeException("Already in Orthographic mode.");
        }
        // set up ortho mode
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GLU.gluOrtho2D(-mode.getWidth() / 2, mode.getWidth() / 2, -mode
                .getHeight() / 2, mode.getHeight() / 2);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        inOrthoMode = true;
    }
    
    /**
     * 
     * <code>setOrthoCenter</code> sets the display system to be in
     * orthographic mode. If the system has already been set to orthographic
     * mode a <code>JmeException</code> is thrown. The origin (0,0) is the
     * center of the screen.
     * 
     */
    public void unsetOrtho() {
        if (!inOrthoMode) {
            throw new JmeException("Not in Orthographic mode.");
        }
        // remove ortho mode, and go back to original
        // state
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        postdrawGeometry(null);
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
        if (null == filename) {
            throw new JmeException("Screenshot filename cannot be null");
        }
        LoggingSystem.getLogger().log(Level.INFO,
                "Taking screenshot: " + filename + ".png");

        // Create a pointer to the image info and create a buffered image to
        // hold it.
        IntBuffer buff = ByteBuffer.allocateDirect(width * height * 4).order(
                ByteOrder.LITTLE_ENDIAN).asIntBuffer(); 
        grabScreenContents(buff, 0, 0, width, height);
        BufferedImage img = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);

        // Grab each pixel information and set it to the BufferedImage info.
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                img.setRGB(x, y, buff.get((height - y - 1) * width + x));
            }
        }

        // write out the screenshot image to a file.
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
     * <code>grabScreenContents</code> reads a block of pixels from the
     * current framebuffer.
     * 
     * @param buff
     *            a buffer to store contents in.
     * @param x -
     *            x starting point of block
     * @param y -
     *            y starting point of block
     * @param w -
     *            width of block
     * @param h -
     *            height of block
     */
    public void grabScreenContents(IntBuffer buff, int x, int y, int w, int h) {
        GL11.glReadPixels(x, y, w, h, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE,
                        buff);
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
        predrawGeometry(p);

        IntBuffer indices = p.getIndexBuffer();
        indices.rewind();

        int verts = p.getVertQuantity();
        if (statisticsOn) {
            numberOfVerts += verts;
        }

        GL11.glPointSize(p.getPointSize());
        if (p.isAntialiased()) {
            GL11.glEnable(GL11.GL_POINT_SMOOTH);
            GL11.glHint(GL11.GL_POINT_SMOOTH_HINT, GL11.GL_NICEST);
        }

        GL11.glDrawElements(GL11.GL_POINTS, indices);

        if (p.isAntialiased()) {
            GL11.glDisable(GL11.GL_POINT_SMOOTH);
        }

        postdrawGeometry(p);
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
        predrawGeometry(l);

        IntBuffer indices = l.getIndexBuffer();
        indices.rewind();
        int verts = l.getVertQuantity();
        if (statisticsOn) {
            numberOfVerts += verts;
        }
        
        GL11.glLineWidth(l.getLineWidth());
        if (l.getStippleFactor() != (short)0xFFFF) {
            GL11.glEnable(GL11.GL_LINE_STIPPLE);
            GL11.glLineStipple(l.getStippleFactor(), l.getStipplePattern());
        }
        if (l.isAntialiased()) {
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        }
        
        switch (l.getMode()) {
        	case Line.SEGMENTS:
                GL11.glDrawElements(GL11.GL_LINES, indices);
                break;
            case Line.CONNECTED:
                GL11.glDrawElements(GL11.GL_LINE_STRIP, indices);
                break;
            case Line.LOOP:
                GL11.glDrawElements(GL11.GL_LINE_LOOP, indices);
                break;
        }

        if (l.getStippleFactor() != (short)0xFFFF) {
            GL11.glDisable(GL11.GL_LINE_STIPPLE);
        }
        if (l.isAntialiased()) {
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
        }

        postdrawGeometry(l);
    }

    /**
     * <code>draw</code> renders a curve object.
     * 
     * @param c
     *            the curve object to render.
     */
    public void draw(Curve c) {
        // set world matrix
        Quaternion rotation = c.getWorldRotation();
        Vector3f translation = c.getWorldTranslation();
        Vector3f scale = c.getWorldScale();
        float rot = rotation.toAngleAxis(vRot);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();

        GL11.glTranslatef(translation.x, translation.y, translation.z);
        GL11.glRotatef(rot, vRot.x, vRot.y, vRot.z);
        GL11.glScalef(scale.x, scale.y, scale.z);

        // render the object
        GL11.glBegin(GL11.GL_LINE_STRIP);

        FloatBuffer color = c.getColorBuffer();
        if (color != null) color.rewind();
        float colorInterval = 0;
        float colorModifier = 0;
        int colorCounter = 0;
        if (null != color) {
            GL11.glColor4f(color.get(), color.get(), color.get(), color.get());

            colorInterval = 4f / color.capacity() ;
            colorModifier = colorInterval;
            colorCounter = 0;
            color.rewind();
        }

        Vector3f point;
        float limit = (1 + (1.0f / c.getSteps()));
        for (float t = 0; t <= limit; t += 1.0f / c.getSteps()) {

            if (t >= colorInterval && color != null) {

                colorInterval += colorModifier;
                GL11.glColor4f(color.get(), color.get(), color.get(), color.get());
                colorCounter++;
            }

            point = c.getPoint(t, tempVa);
            GL11.glVertex3f(point.x, point.y, point.z);
        }

        if (statisticsOn) {
            numberOfVerts += limit;
        }

        GL11.glEnd();
        postdrawGeometry(c);
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
        predrawGeometry(t);

        IntBuffer indices = t.getIndexBuffer();
        indices.rewind();
        int verts = t.getVertQuantity();
        if (statisticsOn) {
            numberOfTris += t.getTriangleQuantity();
            numberOfVerts += verts;
        }

    	indices.limit(t.getTriangleQuantity()*3); // make sure only the necessary indices are sent through on old cards.
        GL11.glDrawElements(GL11.GL_TRIANGLES, indices);
        indices.clear();
            

        postdrawGeometry(t);
    }

    /**
     * <code>draw</code> renders a <code>CompositeMesh</code> object
     * including it's normals, colors, textures and vertices.
     * 
     * @see com.jme.renderer.Renderer#draw(com.jme.scene.CompositeMesh)
     * @param t
     *            the mesh to render.
     */
    public void draw(CompositeMesh t) {
        predrawGeometry(t);

        IntBuffer indices = t.getIndexBuffer().duplicate(); // returns secondary pointer to same data
        CompositeMesh.IndexRange[] ranges = t.getIndexRanges();
        int verts = t.getVertQuantity();
        if (statisticsOn) {
            numberOfVerts += verts;
            numberOfTris += t.getTriangleQuantity();
        }

        indices.position(0);
        for (int i = 0; i < ranges.length; i++) {
            int mode;
            switch (ranges[i].getKind()) {
            case CompositeMesh.IndexRange.TRIANGLES:
                mode = GL11.GL_TRIANGLES;
                break;
            case CompositeMesh.IndexRange.TRIANGLE_STRIP:
                mode = GL11.GL_TRIANGLE_STRIP;
                break;
            case CompositeMesh.IndexRange.TRIANGLE_FAN:
                mode = GL11.GL_TRIANGLE_FAN;
                break;
            case CompositeMesh.IndexRange.QUADS:
                mode = GL11.GL_QUADS;
                break;
            case CompositeMesh.IndexRange.QUAD_STRIP:
                mode = GL11.GL_QUAD_STRIP;
                break;
            default:
                throw new JmeException("Unknown index range type "
                        + ranges[i].getKind());
            }
            indices.limit(indices.position() + ranges[i].getCount());
            GL11.glDrawElements(mode, indices);
            indices.position(indices.limit());
        }

        postdrawGeometry(t);
    }

    
    protected IntBuffer buf = org.lwjgl.BufferUtils.createIntBuffer(16);
    /**
     * <code>prepVBO</code> binds the geometry data to a vbo buffer and
     * sends it to the GPU if necessary. The vbo id is stored in the
     * geometry's VBOInfo class.
     * @param g the geometry to initialize VBO for.
     */
    public void prepVBO(Geometry g) {
        if (!capabilities.GL_ARB_vertex_buffer_object)
            return;
        
        VBOInfo vbo = g.getVBOInfo();
        
        if (vbo.isVBOVertexEnabled() && vbo.getVBOVertexID() <= 0) {
            if (g.getVertexBuffer() != null) {
                g.getVertexBuffer().rewind();
                buf.rewind();
                ARBVertexBufferObject.glGenBuffersARB(buf);
                vbo.setVBOVertexID(buf.get(0));
                ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, vbo.getVBOVertexID());
                ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, g.getVertexBuffer(), 
                        ARBVertexBufferObject.GL_STATIC_DRAW_ARB);
            }
        }
        if (vbo.isVBONormalEnabled() && vbo.getVBONormalID() <= 0) {
            if (g.getNormalBuffer() != null) {
                g.getNormalBuffer().rewind();
                buf.rewind();
	            ARBVertexBufferObject.glGenBuffersARB(buf);
	            vbo.setVBONormalID(buf.get(0));
	            ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, vbo.getVBONormalID());
	            ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, g.getNormalBuffer(),
	            		ARBVertexBufferObject.GL_STATIC_DRAW_ARB);
            }
        }
        if (vbo.isVBOColorEnabled() && vbo.getVBOColorID() <= 0) {
            if (g.getColorBuffer() != null) {
                g.getColorBuffer().rewind();
                buf.rewind();
            	ARBVertexBufferObject.glGenBuffersARB(buf);
            	vbo.setVBOColorID(buf.get(0));
                ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, vbo.getVBOColorID());
                ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, g.getColorBuffer(), 
                        ARBVertexBufferObject.GL_STATIC_DRAW_ARB);
            }
        }
        if (vbo.isVBOTextureEnabled()) {
            for (int i = 0; i < g.getNumberOfUnits(); i++) {

                if (vbo.getVBOTextureID(i) <= 0
                        && g.getTextureBuffer(i) != null) {
                    if (g.getTextureBuffer(i) != null) {
                        g.getTextureBuffer(i).rewind();
                        buf.rewind();
                        ARBVertexBufferObject.glGenBuffersARB(buf);
                        vbo.setVBOTextureID(i, buf.get(0));
                        ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, vbo.getVBOTextureID(i));
                        ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, g.getTextureBuffer(i), 
                                ARBVertexBufferObject.GL_STATIC_DRAW_ARB);
                    }
                }
            }
        }
        buf.clear();
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
     * <code>draw</code> renders a text object using a predefined font.
     * 
     * @see com.jme.renderer.Renderer#draw(com.jme.scene.Text)
     */
    public void draw(Text t) {
        if (font == null) {
            font = new LWJGLFont();
        }
        font.setColor(t.getTextColor());
        font.print((int) t.getWorldTranslation().x, (int) t
                .getWorldTranslation().y, t.getWorldScale(), t.getText(), 0);
    }

    /**
     * checkAndAdd is used to process the spatial for the render queue. It's
     * queue mode is checked, and it is added to the proper queue. If the
     * queue mode is QUEUE_SKIP, false is returned.
     * 
     * @return true if the spatial was added to a queue, false otherwise.
     */
    public boolean checkAndAdd(Spatial s) {
        int rqMode = s.getRenderQueueMode();
        if (rqMode != Renderer.QUEUE_SKIP) {
            getQueue().addToQueue(s, rqMode);
            return true;
        }
        return false;
    }

    /**
     * Return true if the system running this supports VBO
     * 
     * @return boolean true if VBO supported
     */
    public boolean supportsVBO() {
        return capabilities.OpenGL15;
    }

    /**
     * re-initializes the GL context for rendering of another piece of geometry.
     */
    private void postdrawGeometry(Geometry t) {
        VBOInfo vbo = t != null ? t.getVBOInfo() : null;
        if (vbo != null && capabilities.GL_ARB_vertex_buffer_object) {
            ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, 0);
        }

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
    }
    
    /**
     * <code>flush</code> tells opengl to finish all currently waiting
     * commands in the buffer.
     */
    public void flush() {
        GL11.glFlush();
    }

    /**
     * prepares the GL Context for rendering this geometry. This involves
     * setting the rotation, translation, and scale, initializing VBO, and
     * obtaining the buffer data.
     * @param t the geometry to process.
     */
    private void predrawGeometry(Geometry t) {
        // set world matrix
        Quaternion rotation = t.getWorldRotation();
        Vector3f translation = t.getWorldTranslation();
        Vector3f scale = t.getWorldScale();
        float rot = rotation.toAngleAxis(vRot);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();

        GL11.glTranslatef(translation.x, translation.y, translation.z);
        GL11.glRotatef(rot, vRot.x, vRot.y, vRot.z);
        GL11.glScalef(scale.x, scale.y, scale.z);
        if (!(scale.x == 1 && scale.y == 1 && scale.z == 1))
            GL11.glEnable(GL11.GL_NORMALIZE); // since we are using
        // glScalef, we should enable
        // this to keep normals
        // working.

        VBOInfo vbo = t.getVBOInfo();
        if (vbo != null && capabilities.GL_ARB_vertex_buffer_object) {
            prepVBO(t);
            ignoreVBO = false;
        } else
            ignoreVBO = true;

        // render the object

        FloatBuffer verticies = t.getVertexBuffer();
        if ((!ignoreVBO && vbo.getVBOVertexID() > 0)) { // use VBO
            usingVBO = true;
            GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, vbo.getVBOVertexID());
            GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);
        } else if (verticies == null) {
            GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        } else if (prevVerts != verticies) {  
            // textures have changed
            GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            if (usingVBO)
                ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, 0);
            verticies.rewind();
            GL11.glVertexPointer(3, 0, verticies);
        }
        prevVerts = verticies;

        FloatBuffer normals = t.getNormalBuffer();
        if ((!ignoreVBO && vbo.getVBONormalID() > 0)) { // use VBO
            usingVBO = true;
            GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
            ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, vbo.getVBONormalID());
            GL11.glNormalPointer(GL11.GL_FLOAT, 0, 0);
        } else if (normals == null) {
            GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
        } else if (prevNorms != normals) {  
            // textures have changed
            GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
            if (usingVBO)
                ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, 0);
            normals.rewind();
            GL11.glNormalPointer(0, normals);
        }
        prevNorms = normals;

        FloatBuffer colors = t.getColorBuffer();
        if ((!ignoreVBO && vbo.getVBOColorID() > 0)) { // use VBO
            usingVBO = true;
            GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
            ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, vbo.getVBOColorID());
            GL11.glColorPointer(4, GL11.GL_FLOAT, 0, 0);
        } else if (colors == null) {
            GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
            ColorRGBA defCol = t.getDefaultColor();
            if (defCol != null)
                GL11.glColor4f(defCol.r, defCol.g, defCol.b, defCol.a);
        } else if (prevColor != colors) {  
            // textures have changed
            GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
            if (usingVBO)
                ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, 0);
            colors.rewind();
            GL11.glColorPointer(4, 0, colors);
        }
        prevColor = colors;

        for (int i = 0; i < TextureState.getNumberOfUnits(); i++) {
            FloatBuffer textures = t.getTextureBuffer(i);
            if (capabilities.GL_ARB_multitexture && capabilities.OpenGL13) {
                GL13.glClientActiveTexture(GL13.GL_TEXTURE0 + i);
            }
            if ((!ignoreVBO && vbo.getVBOTextureID(i) > 0)) { // use VBO
                usingVBO = true;
                GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, vbo.getVBOTextureID(i));
                GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);
            } else if (textures == null) {
                GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            } else if (prevTex[i] != textures) {  
                // textures have changed
                GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                if (usingVBO)
                	ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, 0);
                textures.rewind();
                GL11.glTexCoordPointer(2, 0, textures);
            }
            prevTex[i] = textures;
        }
    }   
}