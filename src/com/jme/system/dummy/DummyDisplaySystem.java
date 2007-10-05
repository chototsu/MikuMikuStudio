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

package com.jme.system.dummy;

import java.awt.Canvas;
import java.net.URL;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import com.jme.curve.Curve;
import com.jme.image.Texture;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.RenderContext;
import com.jme.renderer.RenderQueue;
import com.jme.renderer.Renderer;
import com.jme.renderer.TextureRenderer;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
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
import com.jme.scene.state.ShadeState;
import com.jme.scene.state.StencilState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.VertexProgramState;
import com.jme.scene.state.WireframeState;
import com.jme.scene.state.ZBufferState;
import com.jme.scene.state.lwjgl.records.StateRecord;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jmex.awt.JMECanvas;

/**
 * Started Date: Jul 2, 2004 <br>
 * <br>
 * <p/>
 * This class is a dummy class that is not suppost to be rendered. It is here to
 * allow the easy creation of dummy jME objects (like various RenderState and
 * Spatial) so that they can be used by conversion utilities to read/write jME.
 * It is <b>NOT </b> to be used for rendering as it won't do anything at all.
 *
 * @author Jack Lindamood
 */
public class DummyDisplaySystem extends DisplaySystem {

    public DummyDisplaySystem() {
        system = new DummySystemProvider(this);
        created = true;
        
    }
    
    public boolean isValidDisplayMode( int width, int height, int bpp, int freq ) {
        return false;
    }
    
    public void setIcon(com.jme.image.Image[] iconImages) {
    }

    public void setVSyncEnabled( boolean enabled ) {
    }

    public void setTitle( String title ) {
    }

    public void createWindow( int w, int h, int bpp, int frq, boolean fs ) {
    }

    public void createHeadlessWindow( int w, int h, int bpp ) {
    }

    public void recreateWindow( int w, int h, int bpp, int frq, boolean fs ) {
    }

    public Renderer getRenderer() {
        return new Renderer() {

            public void setCamera( Camera camera ) {
            }

            public Camera createCamera( int width, int height ) {
                return null;
            }

            public AlphaState createAlphaState() {
                return new AlphaState() {

                    private static final long serialVersionUID = 1L;

                    public void apply() {
                    }

                    public StateRecord createStateRecord() { return null; }
                    
                    
                };
            }

            public void flush() {
            }

            public void finish() {
            }

            public AttributeState createAttributeState() {
                return new AttributeState() {

                    private static final long serialVersionUID = 1L;

                    public void apply() {
                    }
                    public StateRecord createStateRecord() { return null; }
                };
            }

            public CullState createCullState() {
                return new CullState() {

                    private static final long serialVersionUID = 1L;

                    public void apply() {
                    }
                    public StateRecord createStateRecord() { return null; }
                };
            }

            public DitherState createDitherState() {
                return new DitherState() {

                    private static final long serialVersionUID = 1L;

                    public void apply() {
                    }
                    public StateRecord createStateRecord() { return null; }
                };
            }

            public FogState createFogState() {
                return new FogState() {

                    private static final long serialVersionUID = 1L;

                    public void apply() {
                    }
                    public StateRecord createStateRecord() { return null; }
                };
            }

            public LightState createLightState() {
                return new LightState() {

                    private static final long serialVersionUID = 1L;

                    public void apply() {
                    }
                    public StateRecord createStateRecord() { return null; }
                };
            }

            public MaterialState createMaterialState() {
                return new MaterialState() {

                    private static final long serialVersionUID = 1L;

                    public void apply() {
                    }
                    public StateRecord createStateRecord() { return null; }
                };
            }

            public ShadeState createShadeState() {
                return new ShadeState() {

                    private static final long serialVersionUID = 1L;

                    public void apply() {
                    }
                    public StateRecord createStateRecord() { return null; }
                };
            }

            class TextureStateN extends TextureState {

                private static final long serialVersionUID = 1L;

                TextureStateN() {
                    numTotalTexUnits = 1;
                    texture = new ArrayList<Texture>(1);
                }

                public void load( int unit ) {
                }

                public void delete( int unit ) {
                }

                public void deleteAll() {
                }

                public void deleteAll(boolean removeFromCache) {
                }

                public void apply() {
                }
                public StateRecord createStateRecord() { return null; }
            }

            public TextureState createTextureState() {
                return new TextureStateN();
            }

            public WireframeState createWireframeState() {
                return new WireframeState() {

                    private static final long serialVersionUID = 1L;

                    public void apply() {
                    }
                    public StateRecord createStateRecord() { return null; }
                };
            }

            public ZBufferState createZBufferState() {
                return new ZBufferState() {

                    private static final long serialVersionUID = 1L;

                    public void apply() {
                    }
                    public StateRecord createStateRecord() { return null; }
                };
            }

            public VertexProgramState createVertexProgramState() {
                return new VertexProgramState() {

                    private static final long serialVersionUID = 1L;

                    public boolean isSupported() {
                        return false;
                    }

                    public void load( URL file ) {
                    }

                    public void load( String contents ) {
                    }

                    public void apply() {
                    }

                    public String getProgram() {
                        return null;
                    }
                    public StateRecord createStateRecord() { return null; }
                };
            }

            public FragmentProgramState createFragmentProgramState() {
                return new FragmentProgramState() {

                    private static final long serialVersionUID = 1L;

                    public boolean isSupported() {
                        return false;
                    }

                    public void load( URL file ) {
                    }

                    public void load( String contents ) {
                    }

                    public void apply() {
                    }

                    public String getProgram() {
                        return null;
                    }
                    public StateRecord createStateRecord() { return null; }
                };
            }

            public GLSLShaderObjectsState createGLSLShaderObjectsState() {
                return new GLSLShaderObjectsState() {
                    private static final long serialVersionUID = 1L;

                    public boolean isSupported() {
                        return false;
                    }

                    public void load( URL vert, URL frag ) {
                    }
                    
                    public void load(String vert, String frag) {
                        
                    }

                    public void apply() {
                    }

                    public StateRecord createStateRecord() { return null; }
                };
            }

            public StencilState createStencilState() {
                return new StencilState() {

                    private static final long serialVersionUID = 1L;

                    public void apply() {
                    }
                    public StateRecord createStateRecord() { return null; }
                };
            }

            public ClipState createClipState() {
                return new ClipState() {

                    private static final long serialVersionUID = 1L;

                    public void apply() {
                    }
                    public StateRecord createStateRecord() { return null; }
                };
            }

            public ColorMaskState createColorMaskState() {
                return new ColorMaskState() {

                    private static final long serialVersionUID = 1L;

                    public void apply() {
                    }
                    public StateRecord createStateRecord() { return null; }
                };
            }

            public void enableStatistics( boolean value ) {
            }

            public void clearStatistics() {
            }

            public void setBackgroundColor( ColorRGBA c ) {
            }

            public ColorRGBA getBackgroundColor() {
                return null;
            }

            public void clearZBuffer() {
            }

            public void clearColorBuffer() {
            }

            public void clearStencilBuffer() {
            }

            public void clearBuffers() {
            }

            public void clearStrictBuffers() {
            }

            public void displayBackBuffer() {
            }

            public void setOrtho() {
            }

            public void setOrthoCenter() {
            }

            public void unsetOrtho() {
            }

            public boolean takeScreenShot( String filename ) {
                return false;
            }

            public void grabScreenContents( IntBuffer buff, int x, int y, int w,
                                            int h ) {
            }

            public void draw( Spatial s ) {
            }

            public void draw( PointBatch batch ) {
            }

            public void draw( LineBatch batch ) {
            }

            public void draw( Curve c ) {
            }

            public void draw( Text t ) {
            }

            public RenderQueue getQueue() {
                return null;
            }

            public boolean isProcessingQueue() {
                return false;
            }

            public boolean checkAndAdd( SceneElement s ) {
                return false;
            }

            public boolean supportsVBO() {
                return false;
            }

            public boolean isHeadless() {
                return false;
            }

            public void setHeadless( boolean headless ) {
            }

            public int getWidth() {
                return -1;
            }

            public int getHeight() {
                return -1;
            }

            public void reinit( int width, int height ) {
            }

            public int createDisplayList( GeomBatch g ) {
                return -1;
            }

            public void releaseDisplayList( int listId ) {
            }

            public void setPolygonOffset( float factor, float offset ) {
            }

            public void clearPolygonOffset() {
            }

            public void deleteVBO( Buffer buffer ) {
                
            }

            public void deleteVBO( int vboid ) {
                
            }

            public void clearVBOCache() {
                
            }

            public Integer removeFromVBOCache( Buffer buffer ) {
                return null;
            }

            public void draw(TriangleBatch batch) {
            }

			@Override
			public void draw(QuadBatch batch) {
			}

            @Override
            public StateRecord createLineRecord() {
                return null;
            }

            @Override
            public StateRecord createRendererRecord() {
                return null;
            }

            @Override
            public void checkCardError() throws JmeException { }

            @Override
            public void cleanup() { }
        };
    }

    public boolean isClosing() {
        return false;
    }

    @Override
    public boolean isActive()
    {
    	return true;
    }
    
    public void reset() {
    }

    public void close() {
    }

    public Vector3f getScreenCoordinates( Vector3f worldPosition, Vector3f store ) {
        return null;
    }

    public Vector3f getWorldCoordinates( Vector2f screenPosition, float zPos,
                                         Vector3f store ) {
        return null;
    }

    public void setRenderer( Renderer r ) {
    }

    public Canvas createCanvas( int w, int h ) {
        return null;
    }

    public TextureRenderer createTextureRenderer( int width, int height, int target) {
        return null;
    }

    protected void updateDisplayBGC() { }

    @Override
    public String getAdapter() {
        return null;
    }
    
    /**
	 * <code>getDisplayVendor</code> returns the vendor of the graphics
	 * adapter
	 * 
	 * @return The adapter vendor
	 */
	public String getDisplayVendor() {
		return null;
	}

	/**
	 * <code>getDisplayRenderer</code> returns details of the adapter
	 * 
	 * @return The adapter details
	 */
	public String getDisplayRenderer() {
		return null;
	}

	/**
	 * <code>getDisplayAPIVersion</code> returns the API version supported
	 * 
	 * @return The api version supported
	 */
	public String getDisplayAPIVersion() {
		return null;
	}

    @Override
    public String getDriverVersion() {
        return null;
    }

    @Override
    public void setCurrentCanvas(JMECanvas canvas) { }

    @Override
    public RenderContext getCurrentContext() {
        return null;
    }

    @Override
    public void initForCanvas(int width, int height) { }

    @Override
    public RenderContext removeContext(Object contextKey) {
        return null;
    }
}
