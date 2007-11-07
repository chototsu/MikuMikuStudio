/*
 * Copyright (c) 2003-2007 jMonkeyEngine
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
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.ARBBufferObject;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.EXTCompiledVertexArray;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.opengl.glu.GLU;

import com.jme.curve.Curve;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.RenderContext;
import com.jme.renderer.RenderQueue;
import com.jme.renderer.Renderer;
import com.jme.scene.Line;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.VBOInfo;
import com.jme.scene.batch.GeomBatch;
import com.jme.scene.batch.LineBatch;
import com.jme.scene.batch.PointBatch;
import com.jme.scene.batch.QuadBatch;
import com.jme.scene.batch.TriangleBatch;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.AttributeState;
import com.jme.scene.state.ClipState;
import com.jme.scene.state.ColorMaskState;
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
import com.jme.scene.state.lwjgl.LWJGLClipState;
import com.jme.scene.state.lwjgl.LWJGLColorMaskState;
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
import com.jme.scene.state.lwjgl.records.LineRecord;
import com.jme.scene.state.lwjgl.records.RendererRecord;
import com.jme.scene.state.lwjgl.records.StateRecord;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.WeakIdentityCache;

/**
 * <code>LWJGLRenderer</code> provides an implementation of the
 * <code>Renderer</code> interface using the LWJGL API.
 * 
 * @see com.jme.renderer.Renderer
 * @author Mark Powell - initial implementation, and more.
 * @author Joshua Slack - Further work, Optimizations, Headless rendering
 * @author Tijl Houtbeckers - Small optimizations and improved VBO
 * @version $Id: LWJGLRenderer.java,v 1.146 2007-11-07 15:33:46 nca Exp $
 */
public class LWJGLRenderer extends Renderer {
    private static final Logger logger = Logger.getLogger(LWJGLRenderer.class.getName());

    private Vector3f vRot = new Vector3f();

    private LWJGLFont font;

    private boolean supportsVBO = false;
    
    private boolean indicesVBO = false;

    private boolean inOrthoMode;

    private Vector3f tempVa = new Vector3f();

    private FloatBuffer prevVerts;

    private FloatBuffer prevNorms;

    private FloatBuffer prevColor;

    private FloatBuffer[] prevTex;
    
    private int prevNormMode = GL11.GL_ZERO;
    
    protected ContextCapabilities capabilities;
    
    private int prevTextureNumber = 0;

    private boolean generatingDisplayList = false;
    
    protected WeakIdentityCache<Buffer, Integer> vboMap = new WeakIdentityCache<Buffer, Integer>();
    
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
            logger.warning("Invalid width and/or height values.");
            throw new JmeException("Invalid width and/or height values.");
        }
        this.width = width;
        this.height = height;

        logger.info("LWJGLRenderer created. W:  " + width + "H: " + height);
        
        capabilities = GLContext.getCapabilities();
        
        queue = new RenderQueue(this);
        if (TextureState.getNumberOfTotalUnits() == -1)
            createTextureState(); // force units population
        prevTex = new FloatBuffer[TextureState.getNumberOfTotalUnits()];
        
        supportsVBO = capabilities.GL_ARB_vertex_buffer_object;
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
            logger.warning("Invalid width and/or height values.");
            throw new JmeException("Invalid width and/or height values.");
        }
        this.width = width;
        this.height = height;
        if (camera != null)
        {
            camera.resize(width, height);
            camera.apply();
        }
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
     * <code>createClipState</code> returns a new LWJGLClipState object as a
     * regular ClipState.
     * 
     * @return a ClipState object.
     * @see com.jme.renderer.Renderer#createClipState()
     */
    public ClipState createClipState() {
        return new LWJGLClipState();
    }

    /**
     * <code>createColorMaskState</code> returns a new LWJGLColorMaskState
     * object as a regular ColorMaskState.
     * 
     * @return a ColorMaskState object.
     */
    public ColorMaskState createColorMaskState() {
        return new LWJGLColorMaskState();
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
        if (Renderer.defaultStateList[RenderState.RS_ZBUFFER] != null)
            Renderer.defaultStateList[RenderState.RS_ZBUFFER].apply();
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
     * <code>clearStencilBuffer</code>
     * 
     * @see com.jme.renderer.Renderer#clearStencilBuffer()
     */
    public void clearStencilBuffer() {
        // Clear the stencil buffer
        GL11.glClearStencil(0);
        GL11.glStencilMask(~0);
        GL11.glDisable(GL11.GL_DITHER);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(0, 0, getWidth(), getHeight());
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    /**
     * <code>clearBuffers</code> clears both the color and the depth buffer.
     * 
     * @see com.jme.renderer.Renderer#clearBuffers()
     */
    public void clearBuffers() {
        // make sure no funny business is going on in the z before clearing.
        if (Renderer.defaultStateList[RenderState.RS_ZBUFFER] != null)
            Renderer.defaultStateList[RenderState.RS_ZBUFFER].apply();
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
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

        Renderer.defaultStateList[RenderState.RS_COLORMASK_STATE].apply();

        reset();
        

        GL11.glFlush();
        if (!isHeadless())
            Display.update();
        
        vboMap.expunge();
    }

    // XXX: look more at this
    public void reset() {
        prevColor = prevNorms = prevVerts = null;
        Arrays.fill(prevTex, null);
    }

    public boolean isInOrthoMode() {
        return inOrthoMode;
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
        RendererRecord matRecord = (RendererRecord) DisplaySystem.getDisplaySystem().getCurrentContext().getRendererRecord();
        matRecord.switchMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GLU.gluOrtho2D(0, width, 0, height);
        matRecord.switchMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        inOrthoMode = true;
    }

    public void setOrthoCenter() {
        if (inOrthoMode) {
            throw new JmeException("Already in Orthographic mode.");
        }
        // set up ortho mode
        RendererRecord matRecord = (RendererRecord) DisplaySystem.getDisplaySystem().getCurrentContext().getRendererRecord();
        matRecord.switchMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GLU.gluOrtho2D(-width / 2, width / 2, -height / 2, height / 2);
        matRecord.switchMode(GL11.GL_MODELVIEW);
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
        RendererRecord matRecord = (RendererRecord) DisplaySystem.getDisplaySystem().getCurrentContext().getRendererRecord();
        matRecord.switchMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();           
        matRecord.switchMode(GL11.GL_MODELVIEW);
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
        if (null == filename) {
            throw new JmeException("Screenshot filename cannot be null");
        }
        logger.info("Taking screenshot: " + filename + ".png");

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
            logger.warning("Could not create file: " + filename + ".png");
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
        GL11
                .glReadPixels(x, y, w, h, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE,
                        buff);
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
        float rot = rotation.toAngleAxis(vRot) * FastMath.RAD_TO_DEG;
        RendererRecord matRecord = (RendererRecord) DisplaySystem.getDisplaySystem().getCurrentContext().getRendererRecord();
        matRecord.switchMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();

        GL11.glTranslatef(translation.x, translation.y, translation.z);
        GL11.glRotatef(rot, vRot.x, vRot.y, vRot.z);
        GL11.glScalef(scale.x, scale.y, scale.z);

        applyStates(c.states, null);
        
        // render the object
        GL11.glBegin(GL11.GL_LINE_STRIP);

        FloatBuffer color = c.getColorBuffer(0);
        if (color != null)
            color.rewind();
        float colorInterval = 0;
        float colorModifier = 0;
        int colorCounter = 0;
        if (null != color) {
            matRecord.setCurrentColor(color.get(), color.get(), color.get(), color.get());

            colorInterval = 4f / color.limit() ;
            colorModifier = colorInterval;
            colorCounter = 0;
            color.rewind();
        }

        Vector3f point;
        float limit = (1 + (1.0f / c.getSteps()));
        for (float t = 0; t <= limit; t += 1.0f / c.getSteps()) {

            if (t >= colorInterval && color != null) {

                colorInterval += colorModifier;
                matRecord.setCurrentColor(color.get(), color.get(), color.get(), color.get());
                colorCounter++;
            }

            point = c.getPoint(t, tempVa);
            GL11.glVertex3f(point.x, point.y, point.z);
        }

        if (statisticsOn) {
            stats.numberOfVerts += limit;
        }

        GL11.glEnd();
        undoTransforms(c);
    }

    /**
     * <code>draw</code> renders a <code>LineBatch</code> object including
     * it's normals, colors, textures and vertices.
     * 
     * @see Renderer#draw(LineBatch)
     * @param batch
     *            the lines to render.
     */
    public void draw(LineBatch batch) {
        if (!batch.predraw(this)) return;

        if (statisticsOn) {
            stats.numberOfLines += batch.getVertexCount() >> 1;
            stats.numberOfVerts += batch.getVertexCount();
            stats.numberOfMesh++;
        }

        if (batch.getDisplayListID() != -1) {
            renderDisplayList(batch);
            // invalidate line record as we do not know the line state anymore
            ((LineRecord) DisplaySystem.getDisplaySystem().getCurrentContext().getLineRecord()).invalidate();
            return;
        }

        if (!generatingDisplayList) applyStates(batch.states, batch);
        doTransforms(batch.getParentGeom());
        if(batch.isEnabled()) {
            int mode = GL11.GL_LINES;
            switch (batch.getMode()) {
                case Line.SEGMENTS:
                    mode = GL11.GL_LINES;
                    break;
                case Line.CONNECTED:
                    mode = GL11.GL_LINE_STRIP;
                    break;
                case Line.LOOP:
                    mode = GL11.GL_LINE_LOOP;
                    break;
            }
            
            LineRecord lineRecord = (LineRecord) DisplaySystem.getDisplaySystem().getCurrentContext().getLineRecord();
            lineRecord.applyLineWidth(batch.getLineWidth());
            lineRecord.applyLineStipple(batch.getStippleFactor(), batch.getStipplePattern());
            lineRecord.applyLineSmooth(batch.isAntialiased());
            if (!lineRecord.isValid())
                lineRecord.validate();

            if (!predrawGeometry(batch)) {
                // make sure only the necessary indices are sent through on old cards.
                IntBuffer indices = batch.getIndexBuffer();
                indices.rewind();
                indices.limit(batch.getVertexCount());
                
                if (capabilities.GL_EXT_compiled_vertex_array)
                    EXTCompiledVertexArray.glLockArraysEXT(0, batch.getVertexCount());

                GL11.glDrawElements(mode, indices);
                
                if (capabilities.GL_EXT_compiled_vertex_array)
                    EXTCompiledVertexArray.glUnlockArraysEXT();
                indices.clear();
            } else {
                if (capabilities.GL_EXT_compiled_vertex_array)
                    EXTCompiledVertexArray.glLockArraysEXT(0, batch.getVertexCount());
    
                GL11.glDrawElements(mode, batch.getIndexBuffer().limit(), GL11.GL_UNSIGNED_INT, 0);
    
                if (capabilities.GL_EXT_compiled_vertex_array)
                    EXTCompiledVertexArray.glUnlockArraysEXT();
            }
            
            postdrawGeometry(batch);
        }
        undoTransforms(batch.getParentGeom());
        
        batch.postdraw(this);
    }

    /**
     * <code>draw</code> renders a <code>PointBatch</code> object including
     * it's normals, colors, textures and vertices.
     * 
     * @see Renderer#draw(PointBatch)
     * @param batch
     *            the points to render.
     */
    public void draw(PointBatch batch) {
        if (!batch.predraw(this)) return;

        if (statisticsOn) {
            stats.numberOfPoints += batch.getVertexCount();
            stats.numberOfVerts += batch.getVertexCount();
            stats.numberOfMesh++;
        }

        if (batch.getDisplayListID() != -1) {
            renderDisplayList(batch);
            return;
        }

        if (!generatingDisplayList) applyStates(batch.states, batch);
        doTransforms(batch.getParentGeom());
        if(batch.isEnabled()) {
            
            GL11.glPointSize(batch.getPointSize());
            if (batch.isAntialiased()) {
                GL11.glEnable(GL11.GL_POINT_SMOOTH);
                GL11.glHint(GL11.GL_POINT_SMOOTH_HINT, GL11.GL_NICEST);
            }
            
            if (!predrawGeometry(batch)) {
                // make sure only the necessary indices are sent through on old cards.
                IntBuffer indices = batch.getIndexBuffer();
                indices.rewind();
                indices.limit(batch.getVertexCount());
                
                if (capabilities.GL_EXT_compiled_vertex_array)
                    EXTCompiledVertexArray.glLockArraysEXT(0, batch.getVertexCount());

                GL11.glDrawElements(GL11.GL_POINTS, indices);
                
                if (capabilities.GL_EXT_compiled_vertex_array)
                    EXTCompiledVertexArray.glUnlockArraysEXT();
                indices.clear();
            } else {
                if (capabilities.GL_EXT_compiled_vertex_array)
                    EXTCompiledVertexArray.glLockArraysEXT(0, batch.getVertexCount());
    
                GL11.glDrawElements(GL11.GL_POINTS, batch.getIndexBuffer().limit(), GL11.GL_UNSIGNED_INT, 0);
    
                if (capabilities.GL_EXT_compiled_vertex_array)
                    EXTCompiledVertexArray.glUnlockArraysEXT();
            }
            
            if (batch.isAntialiased()) {
                GL11.glDisable(GL11.GL_POINT_SMOOTH);
            }
            
            postdrawGeometry(batch);
        }
        undoTransforms(batch.getParentGeom());
        
        batch.postdraw(this);
    }
    
    /**
     * <code>draw</code> renders a <code>QuadBatch</code> object including
     * it's normals, colors, textures and vertices.
     * 
     * @see Renderer#draw(QuadBatch)
     * @param batch
     *            the mesh to render.
     */
    public void draw(QuadBatch batch) {
        if (!batch.predraw(this)) return;

        if (statisticsOn) {
            stats.numberOfQuads += batch.getQuadCount();
            stats.numberOfVerts += batch.getVertexCount();
            stats.numberOfMesh++;
        }

        if (batch.getDisplayListID() != -1) {
            renderDisplayList(batch);
            return;
        }

        if (!generatingDisplayList) applyStates(batch.states, batch);
        doTransforms(batch.getParentGeom());
        if(batch.isEnabled()) {
            int mode = batch.getMode();
            int glMode;

            switch (mode) {
                case QuadBatch.QUADS:
                    glMode = GL11.GL_QUADS;
                    break;
                case QuadBatch.QUAD_STRIP:
                    glMode = GL11.GL_QUAD_STRIP;
                    break;
                default:
                    throw new JmeException("Unknown triangle mode "
                            + mode);
            }

            if (!predrawGeometry(batch)) {
                // make sure only the necessary indices are sent through on old cards.
                IntBuffer indices = batch.getIndexBuffer();
                indices.rewind();
                indices.limit(batch.getMaxIndex());
                
                if (capabilities.GL_EXT_compiled_vertex_array)
                    EXTCompiledVertexArray.glLockArraysEXT(0, batch.getVertexCount());
    
                GL11.glDrawElements(glMode, indices);
                if (capabilities.GL_EXT_compiled_vertex_array)
                    EXTCompiledVertexArray.glUnlockArraysEXT();
                indices.clear();
            } else {
                if (capabilities.GL_EXT_compiled_vertex_array)
                    EXTCompiledVertexArray.glLockArraysEXT(0, batch.getVertexCount());
    
                GL11.glDrawElements(glMode, batch.getIndexBuffer().limit(), GL11.GL_UNSIGNED_INT, 0);
    
                if (capabilities.GL_EXT_compiled_vertex_array)
                    EXTCompiledVertexArray.glUnlockArraysEXT();
            }

            postdrawGeometry(batch);
        }
        undoTransforms(batch.getParentGeom());

        batch.postdraw(this);
    }

    /**
     * <code>draw</code> renders a <code>TriMesh</code> object including
     * it's normals, colors, textures and vertices.
     * 
     * @see Renderer#draw(TriangleBatch)
     * @param batch
     *            the mesh to render.
     */
    public void draw(TriangleBatch batch) {
        if (!batch.predraw(this)) return;
        if (statisticsOn) {
            stats.numberOfTris += batch.getTriangleCount();
            stats.numberOfVerts += batch.getVertexCount();
            stats.numberOfMesh++;
        }

        if (batch.getDisplayListID() != -1) {
            renderDisplayList(batch);
            return;
        }

        if (!generatingDisplayList) applyStates(batch.states, batch);
        doTransforms(batch.getParentGeom());
        if(batch.isEnabled()) {
            int mode = batch.getMode();
            int glMode;

            switch (mode) {
                case TriangleBatch.TRIANGLES:
                    glMode = GL11.GL_TRIANGLES;
                    break;
                case TriangleBatch.TRIANGLE_STRIP:
                    glMode = GL11.GL_TRIANGLE_STRIP;
                    break;
                case TriangleBatch.TRIANGLE_FAN:
                    glMode = GL11.GL_TRIANGLE_FAN;
                    break;
                default:
                    throw new JmeException("Unknown triangle mode "
                            + mode);
            }

            if (!predrawGeometry(batch)) {
                // make sure only the necessary indices are sent through on old cards.
                IntBuffer indices = batch.getIndexBuffer();
                if (indices == null) {
                    logger.severe("missing indices on geometry object: "+batch.toString());
                } else {
                    indices.rewind();
                    indices.limit(batch.getMaxIndex());
                    
                    if (capabilities.GL_EXT_compiled_vertex_array) {
                        EXTCompiledVertexArray.glLockArraysEXT(0, batch.getVertexCount());
                    }
        
                    GL11.glDrawElements(glMode, indices);
                    if (capabilities.GL_EXT_compiled_vertex_array) {
                        EXTCompiledVertexArray.glUnlockArraysEXT();
                    }
                    
                    indices.clear();
                }
            } else {
                if (capabilities.GL_EXT_compiled_vertex_array) {
                    EXTCompiledVertexArray.glLockArraysEXT(0, batch.getVertexCount());
                }
    
                GL11.glDrawElements(glMode, batch.getIndexBuffer().limit(), GL11.GL_UNSIGNED_INT, 0);
    
                if (capabilities.GL_EXT_compiled_vertex_array) {
                    EXTCompiledVertexArray.glUnlockArraysEXT();
                }
            }

            postdrawGeometry(batch);
        }
        undoTransforms(batch.getParentGeom());

        batch.postdraw(this);
    }

    private synchronized void renderDisplayList(GeomBatch batch) {
        applyStates(batch.states, batch);
        if ((batch.getLocks() & SceneElement.LOCKED_TRANSFORMS) == 0) {
            doTransforms(batch.getParentGeom());
            GL11.glCallList(batch.getDisplayListID());
            undoTransforms(batch.getParentGeom());
        } else {
            GL11.glCallList(batch.getDisplayListID());
        }
        // invalidate line record as we do not know the line state anymore
        ((LineRecord) DisplaySystem.getDisplaySystem().getCurrentContext().getLineRecord()).invalidate();
        // invalidate "current arrays"
        reset();
    }

    /**
     * <code>prepVBO</code> binds the geometry data to a vbo buffer and sends
     * it to the GPU if necessary. The vbo id is stored in the geometry's
     * VBOInfo class. If a new vbo id is created, the VBO is also stored in a cache.
     * Before creating a new VBO this cache will be checked to see if a VBO is 
     * already created for that Buffer.
     * 
     * @param g
     *            the geometry to initialize VBO for.
     */
    public void prepVBO(GeomBatch g) {
        if (!supportsVBO())
            return;
        RendererRecord rendRecord = (RendererRecord) DisplaySystem.getDisplaySystem().getCurrentContext().getRendererRecord();

        VBOInfo vbo = g.getVBOInfo();
        
        if (vbo.isVBOVertexEnabled() && vbo.getVBOVertexID() <= 0) {
            if (g.getVertexBuffer() != null) {
            	
            	Object vboid;
				if ((vboid = vboMap.get(g.getVertexBuffer())) != null) {
					vbo.setVBOVertexID(((Integer) vboid).intValue());
				} else {            	
	                g.getVertexBuffer().rewind();
                    int vboID = rendRecord.makeVBOId();
	                vbo.setVBOVertexID(vboID);
	                vboMap.put(g.getVertexBuffer(), vboID);
	                
                    // ensure no VBO is bound
                    rendRecord.invalidateVBO(); // make sure we set it...
                    rendRecord.setBoundVBO(vbo.getVBOVertexID());
                    ARBBufferObject.glBindBufferARB(
	                        ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, vbo
	                                .getVBOVertexID());
	                ARBBufferObject.glBufferDataARB(
	                        ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, g
	                                .getVertexBuffer(),
	                        ARBBufferObject.GL_STATIC_DRAW_ARB);
				}
            }
        }
        
        if ((g.getType() & SceneElement.TRIANGLEBATCH) != 0) {
		
			if (vbo.isVBOIndexEnabled() && vbo.getVBOIndexID() <= 0) {
				TriangleBatch tb = (TriangleBatch) g;
				if (tb.getIndexBuffer() != null) {
					Object vboid;
					if ((vboid = vboMap.get(tb.getIndexBuffer())) != null) {
						vbo.setVBOIndexID(((Integer) vboid).intValue());
					} else {
						tb.getIndexBuffer().rewind();
                        int vboID = rendRecord.makeVBOId();
                        vbo.setVBOIndexID(vboID);
						vboMap.put(tb.getIndexBuffer(), vboID);

                        rendRecord.invalidateVBO(); // make sure we set it...
                        rendRecord.setBoundElementVBO(vbo.getVBOIndexID());
						ARBBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB, tb
								.getIndexBuffer(), ARBBufferObject.GL_STATIC_DRAW_ARB);

					}
				}
			}
        }
        
        if (vbo.isVBONormalEnabled() && vbo.getVBONormalID() <= 0) {
            if (g.getNormalBuffer() != null) {
                
            	Object vboid;
				if ((vboid = vboMap.get(g.getNormalBuffer())) != null) {
					vbo.setVBONormalID(((Integer) vboid).intValue());
				} else {
	            	g.getNormalBuffer().rewind();
                    int vboID = rendRecord.makeVBOId();
                    vbo.setVBONormalID(vboID);
		            vboMap.put(g.getNormalBuffer(), vboID);
		            
                    rendRecord.invalidateVBO(); // make sure we set it...
                    rendRecord.setBoundVBO(vbo.getVBONormalID());
	                ARBBufferObject.glBufferDataARB(
	                        ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, g
	                                .getNormalBuffer(),
		            		ARBBufferObject.GL_STATIC_DRAW_ARB);
				}
            }
        }
        if (vbo.isVBOColorEnabled() && vbo.getVBOColorID() <= 0) {
            if (g.getColorBuffer() != null) {
            	Object vboid;
				if ((vboid = vboMap.get(g.getColorBuffer())) != null) {
					vbo.setVBOColorID(((Integer) vboid).intValue());
				} else {
	                g.getColorBuffer().rewind();
                    int vboID = rendRecord.makeVBOId();
                    vbo.setVBOColorID(vboID);
	            	vboMap.put(g.getColorBuffer(), vboID);
	            	
                    rendRecord.invalidateVBO(); // make sure we set it...
                    rendRecord.setBoundVBO(vbo.getVBOColorID());
	                ARBBufferObject.glBufferDataARB(
	                        ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, g
	                                .getColorBuffer(),
	                        ARBBufferObject.GL_STATIC_DRAW_ARB);
				}
            }
        }
        if (vbo.isVBOTextureEnabled()) {
            for (int i = 0; i < g.getNumberOfUnits(); i++) {

                if (vbo.getVBOTextureID(i) <= 0
                        && g.getTextureBuffer(i) != null) {
                	Object vboid;
                	if ((vboid = vboMap.get(g.getTextureBuffer(i))) != null) {
    					vbo.setVBOTextureID(i, ((Integer) vboid).intValue());
    				} else {                        
    					g.getTextureBuffer(i).rewind();
                        int vboID = rendRecord.makeVBOId();
                        vbo.setVBOTextureID(i, vboID);
                        vboMap.put(g.getTextureBuffer(i), vboID);
                        
                        rendRecord.invalidateVBO(); // make sure we set it...
                        rendRecord.setBoundVBO(vbo.getVBOTextureID(i));
                        ARBBufferObject.glBufferDataARB(
                                ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, g
                                        .getTextureBuffer(i),
                                        ARBBufferObject.GL_STATIC_DRAW_ARB);
                    }
                }
            }
        }
    }

    /**
     * <code>draw</code> renders a scene by calling the nodes
     * <code>onDraw</code> method.
     * 
     * @see com.jme.renderer.Renderer#draw(com.jme.scene.Spatial)
     */
    public void draw(Spatial s) {
        getCamera().apply();
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
        applyStates(t.states, null);
        font.print(this, (int) t.getWorldTranslation().x, (int) t
                .getWorldTranslation().y, t.getWorldScale(), t.getText(), 0);
    }

    /**
     * checkAndAdd is used to process the SceneElement for the render queue. It's
     * queue mode is checked, and it is added to the proper queue. If the queue
     * mode is QUEUE_SKIP, false is returned.
     * 
     * @return true if the SceneElement was added to a queue, false otherwise.
     */
    public boolean checkAndAdd(SceneElement s) {
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
        return supportsVBO;
    }

    /**
     * re-initializes the GL context for rendering of another piece of geometry.
     */
    protected void postdrawGeometry(GeomBatch t) {
        // currently nothing to do here.
    }
    
    /**
     * <code>flush</code> tells opengl to send through all currently waiting
     * commands in the buffer.
     */
    public void flush() {
        GL11.glFlush();
    }
    
    /**
     * <code>finish</code> is similar to flush, however it blocks until all
     * waiting OpenGL commands have been finished.
     */
    public void finish() {
        GL11.glFinish();
    }

    /**
	 * Prepares the GL Context for rendering this geometry. This involves
	 * initializing the VBO and obtaining the buffer data.
	 * 
	 * @param t
	 *            the geometry to process.
	 * @return true if VBO is used for indicis, false if not
	 */
	protected boolean predrawGeometry(GeomBatch t) {
        RenderContext context = DisplaySystem.getDisplaySystem().getCurrentContext();
        RendererRecord rendRecord = (RendererRecord) context.getRendererRecord();

        VBOInfo vbo = t.getVBOInfo();
        if (vbo != null && supportsVBO()) {
            prepVBO(t);
        }

        indicesVBO = false;
        
        // set up data to be sent to card
        // first to go is vertices
        int oldLimit = -1;
        FloatBuffer verticies = t.getVertexBuffer();
        if (verticies != null) {
            oldLimit = verticies.limit();
            // make sure only the necessary verts are sent through on old cards.
            verticies.limit(t.getVertexCount() * 3); 
        }
        if ((supportsVBO && vbo != null && vbo.getVBOVertexID() > 0)) { // use VBO
            GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            rendRecord.setBoundVBO(vbo.getVBOVertexID());
            GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);
        } else if (verticies == null) {
            GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        } else if (prevVerts != verticies) {  
            // textures have changed
            GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            // ensure no VBO is bound
            if (supportsVBO)
                rendRecord.setBoundVBO(0);
            verticies.rewind();
            GL11.glVertexPointer(3, 0, verticies);
        }
        if (oldLimit != -1)
            verticies.limit(oldLimit);
        prevVerts = verticies;
        
        // We do not need to set a limit() since this is done in draw(TriMesh)
        if ((t.getType() & SceneElement.TRIANGLEBATCH) != 0) {
	        if ((supportsVBO && vbo != null && vbo.getVBOIndexID() > 0)) { // use VBO
	            indicesVBO = true;
                rendRecord.setBoundElementVBO(vbo.getVBOIndexID());
	        } else if (supportsVBO) {
	            rendRecord.setBoundElementVBO(0);
            }
        }

        int normMode = t.getNormalsMode();
        if (normMode != SceneElement.NM_OFF) {
            applyNormalMode(normMode, t);
            FloatBuffer normals = t.getNormalBuffer();
            oldLimit = -1;
            if (normals != null) {
            	// make sure only the necessary normals are sent through on old cards.
                oldLimit = normals.limit();
                normals.limit(t.getVertexCount() * 3); 
            }
            if ((supportsVBO && vbo != null && vbo.getVBONormalID() > 0)) { // use VBO
                GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
                rendRecord.setBoundVBO(vbo.getVBONormalID());
                GL11.glNormalPointer(GL11.GL_FLOAT, 0, 0);            
            } else if (normals == null) {
                GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
            } else if (prevNorms != normals) {  
                // textures have changed
                GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
                // ensure no VBO is bound
                if (supportsVBO)
                    rendRecord.setBoundVBO(0);
                normals.rewind();
                GL11.glNormalPointer(0, normals);
            }
            if (oldLimit != -1)
                normals.limit(oldLimit);
            prevNorms = normals;
        } else {
            if (prevNormMode == GL12.GL_RESCALE_NORMAL) {
                GL11.glDisable(GL12.GL_RESCALE_NORMAL);
                prevNormMode = GL11.GL_ZERO;
            } else if (prevNormMode == GL11.GL_NORMALIZE) {
                GL11.glDisable(GL11.GL_NORMALIZE);
                prevNormMode = GL11.GL_ZERO;
            }
            oldLimit = -1;
            GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
            prevNorms = null;
        }

        FloatBuffer colors = t.getColorBuffer();
        oldLimit = -1;
        if (colors != null) {
            // make sure only the necessary colors are sent through on old cards.
        	oldLimit = colors.limit();
            colors.limit(t.getVertexCount() * 4); 
        }
        if ((supportsVBO && vbo != null && vbo.getVBOColorID() > 0)) { // use VBO
            GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
            rendRecord.setBoundVBO(vbo.getVBOColorID());
            GL11.glColorPointer(4, GL11.GL_FLOAT, 0, 0);
        } else if (colors == null) {
            GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);

            // Disabling a color array causes the current color to be undefined.
            // So enforce a current color here.
            ColorRGBA defCol = t.getDefaultColor();
            if (defCol != null) {
                rendRecord.setCurrentColor(defCol);
            } else {
                // no default color, so set to white.
                rendRecord.setCurrentColor(1, 1, 1, 1);
            }
        } else if (prevColor != colors) {  
            // colors have changed
            GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
            // ensure no VBO is bound
            if (supportsVBO)
                rendRecord.setBoundVBO(0);
            colors.rewind();
            GL11.glColorPointer(4, 0, colors);
        }
        if (oldLimit != -1)
            colors.limit(oldLimit);
        prevColor = colors;

        TextureState ts = (TextureState) context.currentStates[RenderState.RS_TEXTURE];
        int offset = 0;
        if(ts != null) {
            offset = ts.getTextureCoordinateOffset();
            
            for(int i = 0; i < ts.getNumberOfSetTextures() && i < TextureState.getNumberOfFragmentTexCoordUnits(); i++) {
                FloatBuffer textures = t.getTextureBuffer(i + offset);
                oldLimit = -1;
                if (textures != null) {
            		// make sure only the necessary texture coords are sent through on old cards.
                	oldLimit = textures.limit();
                    textures.limit(t.getVertexCount() * 2); 
                }
                if (capabilities.GL_ARB_multitexture) {
                    ARBMultitexture.glClientActiveTextureARB(ARBMultitexture.GL_TEXTURE0_ARB + i);
                }
                if ((supportsVBO && vbo != null && vbo.getVBOTextureID(i) > 0)) { // use VBO
                    GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                    rendRecord.setBoundVBO(vbo.getVBOTextureID(i));
                    GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);
                } else if (textures == null) {
                    GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                } else if (prevTex[i] != textures) {  
                    // textures have changed
                    GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                    // ensure no VBO is bound
                    if (supportsVBO)
                        rendRecord.setBoundVBO(0);
                    // set data
                    textures.rewind();
                    GL11.glTexCoordPointer(2, 0, textures);
                } else {
                    GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                }
                prevTex[i] = textures;
                if (oldLimit != -1)
                    textures.limit(oldLimit);
            }
            
            if (ts.getNumberOfSetTextures() < prevTextureNumber) {
				for (int i = ts.getNumberOfSetTextures(); i < prevTextureNumber; i++) {
                    if (capabilities.GL_ARB_multitexture) {
                        ARBMultitexture.glClientActiveTextureARB(ARBMultitexture.GL_TEXTURE0_ARB + i);
                    }
					GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
				}
			}
            

            prevTextureNumber = ts.getNumberOfSetTextures() < TextureState.getNumberOfFixedUnits() ? ts.getNumberOfSetTextures()
					: TextureState.getNumberOfFixedUnits();
		}
        return indicesVBO;
    }   
    
    private void applyNormalMode(int normMode, GeomBatch t) {
        switch (normMode) {
            case SceneElement.NM_GL_NORMALIZE_IF_SCALED:
                Vector3f scale = t.getParentGeom().getWorldScale();
                if (!scale.equals(Vector3f.UNIT_XYZ)) {
                    if (scale.x == scale.y && scale.y == scale.z && capabilities.OpenGL12 && prevNormMode != GL12.GL_RESCALE_NORMAL) {
                        if (prevNormMode == GL11.GL_NORMALIZE)
                            GL11.glDisable(GL11.GL_NORMALIZE);
                        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                        prevNormMode = GL12.GL_RESCALE_NORMAL;
                    } else if (prevNormMode != GL11.GL_NORMALIZE) {
                        if (prevNormMode == GL12.GL_RESCALE_NORMAL)
                            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
                        GL11.glEnable(GL11.GL_NORMALIZE);
                        prevNormMode = GL11.GL_NORMALIZE;
                    }
                } else {
                    if (prevNormMode == GL12.GL_RESCALE_NORMAL) {
                        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
                        prevNormMode = GL11.GL_ZERO;
                    } else if (prevNormMode == GL11.GL_NORMALIZE) {
                        GL11.glDisable(GL11.GL_NORMALIZE);
                        prevNormMode = GL11.GL_ZERO;
                    }                        
                }
                break;
            case SceneElement.NM_GL_NORMALIZE_PROVIDED:
                if (prevNormMode != GL11.GL_NORMALIZE) {
                    if (prevNormMode == GL12.GL_RESCALE_NORMAL)
                        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
                    GL11.glEnable(GL11.GL_NORMALIZE);
                    prevNormMode = GL11.GL_NORMALIZE;
                }
                break;
            case SceneElement.NM_USE_PROVIDED:
            default:
                if (prevNormMode == GL12.GL_RESCALE_NORMAL) {
                    GL11.glDisable(GL12.GL_RESCALE_NORMAL);
                    prevNormMode = GL11.GL_ZERO;
                } else if (prevNormMode == GL11.GL_NORMALIZE) {
                    GL11.glDisable(GL11.GL_NORMALIZE);
                    prevNormMode = GL11.GL_ZERO;
                }
                break;
        }
    }

    protected void doTransforms(Spatial t) {
        // set world matrix
        if (!generatingDisplayList || (t.getLocks() & SceneElement.LOCKED_TRANSFORMS) != 0) {
            RendererRecord matRecord = (RendererRecord) DisplaySystem.getDisplaySystem().getCurrentContext().getRendererRecord();
            matRecord.switchMode(GL11.GL_MODELVIEW);
	        GL11.glPushMatrix();
	
	        Vector3f translation = t.getWorldTranslation();
	        if (!translation.equals(Vector3f.ZERO))
	            GL11.glTranslatef(translation.x, translation.y, translation.z);
	
	        Quaternion rotation = t.getWorldRotation();
	        if (!rotation.isIdentity()) {
	            float rot = rotation.toAngleAxis(vRot) * FastMath.RAD_TO_DEG;
	            GL11.glRotatef(rot, vRot.x, vRot.y, vRot.z);
	        }
	        
	        Vector3f scale = t.getWorldScale();
	        if (!scale.equals(Vector3f.UNIT_XYZ)) {
	            GL11.glScalef(scale.x, scale.y, scale.z);
            }
        }
    }
    
    protected void undoTransforms(Spatial t) {
    	if (!generatingDisplayList || (t.getLocks() & SceneElement.LOCKED_TRANSFORMS) != 0) {
            RendererRecord matRecord = (RendererRecord) DisplaySystem.getDisplaySystem().getCurrentContext().getRendererRecord();
            matRecord.switchMode(GL11.GL_MODELVIEW);
            GL11.glPopMatrix();
        }
    }

    // inherited documentation
    public int createDisplayList(GeomBatch g) {
        int listID = GL11.glGenLists(1);

        generatingDisplayList = true;
        RenderContext context = DisplaySystem.getDisplaySystem().getCurrentContext();
        // invalidate states -- this makes sure things like line stipple get called in list.
        context.invalidateStates();
        RenderState oldTS = context.currentStates[RenderState.RS_TEXTURE];
        context.currentStates[RenderState.RS_TEXTURE] = g.states[RenderState.RS_TEXTURE];
        GL11.glNewList(listID, GL11.GL_COMPILE);
        if (g instanceof TriangleBatch)
            draw((TriangleBatch)g);
        else if (g instanceof QuadBatch)
            draw((QuadBatch)g);
        else if (g instanceof LineBatch)
            draw((LineBatch)g);
        else if (g instanceof PointBatch)
            draw((PointBatch)g);
        GL11.glEndList();
        context.currentStates[RenderState.RS_TEXTURE] = oldTS;
        generatingDisplayList = false;
        
        return listID;
    }

    // inherited documentation
    public void releaseDisplayList(int listId) {
        GL11.glDeleteLists(listId, 1);
    }

    // inherited documentation
    public void setPolygonOffset(float factor, float offset) {
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glPolygonOffset(factor, offset);
    }

    // inherited documentation
    public void clearPolygonOffset() {
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
    }
    
    /**
     * @see Renderer#deleteVBO(Buffer)
     */
	public void deleteVBO(Buffer buffer) {
		Integer i = removeFromVBOCache(buffer);
		if (i!=null)
			deleteVBO(i.intValue());
	}
	
	/**
	 * @see Renderer#deleteVBO(int)
	 */
	public void deleteVBO(int vboid) {
		if (vboid < 1 || !supportsVBO())
			return;
        RendererRecord rendRecord = (RendererRecord) DisplaySystem.getDisplaySystem().getCurrentContext().getRendererRecord();
        rendRecord.deleteVBOId(vboid);
	}
    
	/**
	 * @see Renderer#clearVBOCache()
	 */
	public void clearVBOCache() {
		vboMap.clear();		
	}
	
	/**
	 * @see Renderer#removeFromVBOCache(Buffer)
	 */
	public Integer removeFromVBOCache(Buffer buffer) {
		return vboMap.remove(buffer);
		
	}
    
    /**
     * <code>setStates</code> applies the given states if and only if they are
     * different from the currently set states.
     */
    public void applyStates(RenderState[] states, GeomBatch batch) {
        RenderContext context = DisplaySystem.getDisplaySystem().getCurrentContext();

        //TODO: To be used for the attribute shader solution
        if (batch != null) {
            GLSLShaderObjectsState shaderState = (GLSLShaderObjectsState)(context.enforcedStateList[RenderState.RS_GLSL_SHADER_OBJECTS] != null ? context.enforcedStateList[RenderState.RS_GLSL_SHADER_OBJECTS] : states[RenderState.RS_GLSL_SHADER_OBJECTS]);
            if (shaderState != null && shaderState != defaultStateList[RenderState.RS_GLSL_SHADER_OBJECTS]) {
                shaderState.setBatch(batch);  
                shaderState.setNeedsRefresh(true);
            }
        }

        RenderState tempState = null;        
        for (int i = 0; i < states.length; i++) {
            tempState = context.enforcedStateList[i] != null ? context.enforcedStateList[i]
                    : states[i];

            if (tempState != null) {
                if (!RenderState.QUICK_COMPARE[i] || tempState.needsRefresh()
                        || tempState != context.currentStates[i]) {
                    tempState.apply();
                    tempState.setNeedsRefresh(false);
                }
            }
        }
    }

    @Override
    public StateRecord createLineRecord() {
        return new LineRecord();
    }

    @Override
    public StateRecord createRendererRecord() {
        return new RendererRecord();
    }

    @Override
    public void checkCardError() throws JmeException {
        try {
            org.lwjgl.opengl.Util.checkGLError();
        } catch (OpenGLException exception) {
            throw new JmeException("Error in opengl: "+exception.getMessage(), exception);
        }
    }

    @Override
    public void cleanup() {
        // clear vbos
        RendererRecord rendRecord = (RendererRecord) DisplaySystem.getDisplaySystem().getCurrentContext().getRendererRecord();
        rendRecord.cleanupVBOs();
    }
}
