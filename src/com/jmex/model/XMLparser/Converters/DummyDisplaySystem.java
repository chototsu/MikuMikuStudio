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

package com.jmex.model.XMLparser.Converters;

import java.awt.Canvas;
import java.net.URL;
import java.nio.IntBuffer;

import com.jme.curve.Curve;
import com.jme.image.Texture;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.RenderQueue;
import com.jme.renderer.Renderer;
import com.jme.renderer.RendererType;
import com.jme.renderer.TextureRenderer;
import com.jme.scene.CompositeMesh;
import com.jme.scene.Line;
import com.jme.scene.Point;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.AttributeState;
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
import com.jme.system.DisplaySystem;

/**
 * Started Date: Jul 2, 2004 <br>
 * <br>
 * 
 * This class is a dummy class that is not suppost to be rendered. It is here to
 * allow the easy creation of dummy jME objects (like various RenderState and
 * Spatial) so that they can be used by conversion utilities to read/write jME.
 * It is <b>NOT </b> to be used for rendering as it won't do anything at all.
 * 
 * @author Jack Lindamood
 */
public class DummyDisplaySystem extends DisplaySystem {

    public boolean isValidDisplayMode(int width, int height, int bpp, int freq) {
        return false;
    }

    public void setVSyncEnabled(boolean enabled) {
    }

    public void setTitle(String title) {
    }

    public void createWindow(int w, int h, int bpp, int frq, boolean fs) {
    }

    public void createHeadlessWindow(int w, int h, int bpp) {
    }

    public void recreateWindow(int w, int h, int bpp, int frq, boolean fs) {
    }

    public Renderer getRenderer() {
        return new Renderer() {

            public void setCamera(Camera camera) {
            }

            public Camera createCamera(int width, int height) {
                return null;
            }

            public AlphaState createAlphaState() {
                return new AlphaState() {

                    private static final long serialVersionUID = 1L;

                    public void apply() {
                    }
                };
            }
            
            public void flush() {
            }

            public AttributeState createAttributeState() {
                return new AttributeState() {

                    private static final long serialVersionUID = 1L;

                    public void apply() {
                    }
                };
            }

            public CullState createCullState() {
                return new CullState() {

                    private static final long serialVersionUID = 1L;

                    public void apply() {
                    }
                };
            }

            public DitherState createDitherState() {
                return new DitherState() {

                    private static final long serialVersionUID = 1L;

                    public void apply() {
                    }
                };
            }

            public FogState createFogState() {
                return new FogState() {

                    private static final long serialVersionUID = 1L;

                    public void apply() {
                    }
                };
            }

            public LightState createLightState() {
                return new LightState() {

                    private static final long serialVersionUID = 1L;

                    public void apply() {
                    }
                };
            }

            public MaterialState createMaterialState() {
                return new MaterialState() {

                    private static final long serialVersionUID = 1L;

                    public void apply() {
                    }
                };
            }

            public ShadeState createShadeState() {
                return new ShadeState() {

                    private static final long serialVersionUID = 1L;

                    public void apply() {
                    }
                };
            }

            class TextureStateN extends TextureState {

                private static final long serialVersionUID = 1L;

                TextureStateN() {
                    numTexUnits = 1;
                    texture = new Texture[numTexUnits];
                }

                public void delete(int unit) {
                }

                public void deleteAll() {
                }

                public void apply() {
                }
            }

            public TextureState createTextureState() {
                return new TextureStateN();
            }

            public WireframeState createWireframeState() {
                return new WireframeState() {

                    private static final long serialVersionUID = 1L;

                    public void apply() {
                    }
                };
            }

            public ZBufferState createZBufferState() {
                return new ZBufferState() {

                    private static final long serialVersionUID = 1L;

                    public void apply() {
                    }
                };
            }

            public VertexProgramState createVertexProgramState() {
                return new VertexProgramState() {

                    private static final long serialVersionUID = 1L;

                    public boolean isSupported() {
                        return false;
                    }

                    public void load(URL file) {
                    }

                    public void apply() {
                    }
                };
            }

            public FragmentProgramState createFragmentProgramState() {
                return new FragmentProgramState() {

                    private static final long serialVersionUID = 1L;

                    public boolean isSupported() {
                        return false;
                    }

                    public void load(URL file) {
                    }

                    public void apply() {
                    }
                };
            }

            public GLSLShaderObjectsState createGLSLShaderObjectsState() {
                return new GLSLShaderObjectsState() {
                    private static final long serialVersionUID = 1L;

                    public boolean isSupported() {
                        return false;
                    }

                    public void load(URL vert, URL frag) {
                    }

                    public void apply() {
                    }

                    public void relinkProgram() {
                    }
                };
            }

            public StencilState createStencilState() {
                return new StencilState() {

                    private static final long serialVersionUID = 1L;

                    public void apply() {
                    }
                };
            }

            public void enableStatistics(boolean value) {
            }

            public void clearStatistics() {
            }

            public String getStatistics() {
                return null;
            }

            public StringBuffer getStatistics(StringBuffer a) {
                return null;
            }

            public void setBackgroundColor(ColorRGBA c) {
            }

            public ColorRGBA getBackgroundColor() {
                return null;
            }

            public void clearZBuffer() {
            }

            public void clearColorBuffer() {
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

            public boolean takeScreenShot(String filename) {
                return false;
            }

            public void grabScreenContents(IntBuffer buff, int x, int y, int w,
                    int h) {
            }

            public void draw(Spatial s) {
            }

            public void draw(Point p) {
            }

            public void draw(Line l) {
            }

            public void draw(Curve c) {
            }

            public void draw(Text t) {
            }

            public void draw(TriMesh t) {
            }

            public void draw(CompositeMesh t) {
            }

            public RenderQueue getQueue() {
                return null;
            }

            public boolean isProcessingQueue() {
                return false;
            }

            public boolean checkAndAdd(Spatial s) {
                return false;
            }

            public boolean supportsVBO() {
                return false;
            }

            public boolean isHeadless() {
                return false;
            }

            public void setHeadless(boolean headless) {
            }

            public int getWidth() {
                return -1;
            }

            public int getHeight() {
                return -1;
            }

            public void reinit(int width, int height) {
            }
        };
    }

    public RendererType getRendererType() {
        return null;
    }

    public boolean isClosing() {
        return false;
    }

    public void reset() {
    }

    public void close() {
    }

    public Vector3f getScreenCoordinates(Vector3f worldPosition) {
        return null;
    }

    public Vector3f getScreenCoordinates(Vector3f worldPosition, Vector3f store) {
        return null;
    }

    public Vector3f getWorldCoordinates(Vector2f screenPosition, float zPos) {
        return null;
    }

    public Vector3f getWorldCoordinates(Vector2f screenPosition, float zPos,
            Vector3f store) {
        return null;
    }

    public void setRenderer(Renderer r) {
    }

    public Canvas createCanvas(int w, int h) {
        return null;
    }

    public TextureRenderer createTextureRenderer(int width, int height,
            boolean useRGB, boolean useRGBA, boolean useDepth,
            boolean isRectangle, int target, int mipmaps) {
        return null;
    }

    public TextureRenderer createTextureRenderer(int width, int height,
            boolean useRGB, boolean useRGBA, boolean useDepth,
            boolean isRectangle, int target, int mipmaps, int bpp, int alpha,
            int depth, int stencil, int samples) {
        return null;
    }
}