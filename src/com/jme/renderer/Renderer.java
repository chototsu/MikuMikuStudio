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

import com.jme.curve.Curve;
import com.jme.effects.Tint;
import com.jme.input.Mouse;
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
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.ShadeState;
import com.jme.scene.state.ShowBoundingState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.WireframeState;
import com.jme.scene.state.ZBufferState;
import com.jme.widget.WidgetRenderer;

/**
 * <code>Renderer</code> defines an interface that handles displaying 
 * of graphics data to the context. Creation of this object is typically 
 * handled via a call to a <code>DisplaySystem</code> subclass. 
 * 
 * All rendering state and tasks can be handled through this interface. 
 * 
 * Example Usage:<br>
 * NOTE: This example uses the <code>DisplaySystem</code> class to obtain the
 * <code>Renderer</code>.
 * 
 * <code>
 * DisplaySystem ds = new LWJGLDisplaySystem();<br>
 * ds.createWindow(640,480,16,60,false);<br>
 * Renderer r = ds.getRenderer();<br>
 * r.draw(point);<br>
 * </code>
 * @see com.jme.system.DisplaySystem
 * @author Mark Powell
 * @version $Id: Renderer.java,v 1.18 2004-03-12 17:37:14 mojomonkey Exp $
 */
public interface Renderer {
    /**
     * <code>setCamera</code> sets the reference to the applications camera 
     * object.
     * @param camera the camera object to use with this <code>Renderer</code>.
     */
    public void setCamera(Camera camera);
    
    /**
     * <code>getCamera</code> retrieves the camera this renderer is using.
     * @return the camera this renderer is using.
     */
    public Camera getCamera();
    
    /**
     * 
     * <code>getCamera</code> retrieves a default camera for this renderer. 
     * @param width the width of the frame.
     * @param height the height of the frame.
     * @return a default camera for this renderer.
     */
    public Camera getCamera(int width, int height);
    
    /**
     * 
     * <code>getAlphaState</code> retrieves the alpha state object for the 
     * proper renderer. 
     * @return the <code>AlphaState</code> object that can make use of the
     *      proper renderer.
     */
    public AlphaState getAlphaState();
    
    /**
     * 
     * <code>getCullState</code> retrieves the cull state object for the
     * proper renderer.
     * @return the <code>CullState</code> object that can make use of the
     *      proper renderer.
     */
    public CullState getCullState();
    /**
     * 
     * <code>getDitherState</code> retrieves the dither state object for the 
     * proper renderer. 
     * @return the <code>DitherState</code> object that can make use of the
     *      proper renderer.
     */
    public DitherState getDitherState();
    
    /**
     * 
     * <code>getFogState</code> retrieves the fog state object for the 
     * proper renderer. 
     * @return the <code>FogState</code> object that can make use of the
     *      proper renderer.
     */
    public FogState getFogState();
    /**
     * 
     * <code>getLightState</code> retrieves the light state object for the 
     * proper renderer. 
     * @return the <code>LightState</code> object that can make use of the
     *      proper renderer.
     */
    public LightState getLightState();
    
    /**
     * 
     * <code>getMaterialState</code> retrieves the material state object for the 
     * proper renderer. 
     * @return the <code>MaterialState</code> object that can make use of the
     *      proper renderer.
     */
    public MaterialState getMaterialState();
    
    /**
     * 
     * <code>getShadeState</code> retrieves the shade state object for the 
     * proper renderer. 
     * @return the <code>ShadeState</code> object that can make use of the
     *      proper renderer.
     */
    public ShadeState getShadeState();
    
    /**
     * 
     * <code>getTextureState</code> retrieves the texture state object for the 
     * proper renderer. 
     * @return the <code>TextureState</code> object that can make use of the
     *      proper renderer.
     */
    public TextureState getTextureState();
    
    /**
     * 
     * <code>getWireframeState</code> retrieves the wireframe state object for the 
     * proper renderer. 
     * @return the <code>WireframeState</code> object that can make use of the
     *      proper renderer.
     */
    public WireframeState getWireframeState();
    
    public ZBufferState getZBufferState();
    
    public ShowBoundingState getShowBoundingState();
    
    public void enableStatistics(boolean value);
    
    public void clearStatistics();
    
    public String getStatistics();
    
    /**
     * <code>setBackgroundColor</code> sets the color of window. This color
     * will be shown for any pixel that is not set via typical rendering
     * operations.
     * @param c the color to set the background to.
     */
    public void setBackgroundColor(ColorRGBA c);
    
    /**
     * <code>getBackgroundColor</code> retrieves the color used for the 
     * window background. 
     * @return the background color that is currently set to the background.
     */
    public ColorRGBA getBackgroundColor();
    
    /**
     * <code>clearZBuffer</code> clears the depth buffer of the renderer. 
     * The Z buffer allows sorting of pixels by depth or distance from the
     * view port. Clearing this buffer prepares it for the next frame.
     *
     */
    public void clearZBuffer();
    
    /**
     * <code>clearBackBuffer</code> clears the back buffer of the renderer. 
     * The backbuffer is the buffer being rendered to before it is displayed
     * to the screen. Clearing this buffer frees it for rendering the next
     * frame.
     *
     */
    public void clearBackBuffer();
    
    /**
     * <code>clearBuffers</code> clears both the depth buffer and the 
     * back buffer.
     *
     */
    public void clearBuffers();
    
    /**
     * <code>displayBackBuffer</code> swaps the back buffer with the currently
     * displayed buffer. Swapping (page flipping) allows the renderer to display
     * a prerenderer display without any flickering.
     *
     */
    public void displayBackBuffer();
    
    /**
     * 
     * <code>takeScreenShot</code> saves the current buffer to a png file. 
     * The filename is provided, .png will be appended to the end of the
     * name.
     * @param filename the name of the screenshot file.
     * @return true if the screen capture was successful, false otherwise.
     */
    public boolean takeScreenShot(String filename);
    
    /**
     * <code>draw</code> renders a scene. As it recieves a base class of
     * <code>Spatial</code> the renderer hands off management of the 
     * scene to spatial for it to determine when a <code>Geometry</code> 
     * leaf is reached.
     * @param s the scene to render.
     */
    public void draw(Spatial s);
    
    
    /**
     * <code>draw</code> renders a tint to the back buffer
     * @param t is the tint to render.
     */
    public void draw(Tint t);
    
    /**
     * <code>draw</code> renders a single point to the back buffer. 
     * @param p the point to be rendered.
     */
    public void draw(Point p);
    
    /**
     * <code>draw</code> renders a line to the back buffer.
     * @param l the line to be rendered.
     */
    public void draw(Line l);
    
    /**
     * 
     * <code>draw</code> renders a curve to the back buffer.
     * @param c the curve to be rendered.
     */
    public void draw(Curve c);
    
    /**
     * 
     * <code>draw</code> renders a mouse object.
     * @param m the mouse to be rendered.
     */
    public void draw(Mouse m);
    
    /**
     * 
     * <code>draw</code> renders text to the back buffer.
     * @param t the text object to be rendered.
     */
    public void draw(Text t);
    
    /**
     * <code>draw</code> renders a triangle mesh to the back buffer.
     * @param t the mesh to be rendered.
     */
    public void draw(TriMesh t);
    
    public void draw(CloneNode cn);
    
    public void draw(Clone c);
    
    /**
     * <code>draw</code> renders a Widget that is associated
     * with the WidgetRenderer object to the back buffer.
     * @param wp the WidgetPanel to be rendered.
     */
    public void draw(WidgetRenderer wr);

}
