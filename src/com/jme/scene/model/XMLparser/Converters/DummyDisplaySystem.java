package com.jme.scene.model.XMLparser.Converters;

import com.jme.system.DisplaySystem;
import com.jme.renderer.*;
import com.jme.scene.state.*;
import com.jme.scene.*;
import com.jme.bounding.BoundingVolume;
import com.jme.curve.Curve;
import com.jme.widget.WidgetRenderer;
import com.jme.widget.font.WidgetFont;
import com.jme.math.Vector3f;
import com.jme.math.Vector2f;
import com.jme.image.Texture;

import java.net.URL;
import java.nio.IntBuffer;

/**
 * Started Date: Jul 2, 2004<br><br>
 *
 * This class is a dummy class that is not suppost to be rendered.  It is here to allow the easy
 * creation of dummy jME objects (like various RenderState and Spatial) so that they can be
 * used by conversion utilities to read/write jME.  It is <b>NOT</b> to be used for rendering as it
 * won't do anything at all.
 * @author Jack Lindamood
 */
public class DummyDisplaySystem extends DisplaySystem{
    public boolean isValidDisplayMode(int width, int height, int bpp, int freq) {return false;}
    public void setVSyncEnabled(boolean enabled) {}
    public void setTitle(String title) {}
    public void createWindow(int w, int h, int bpp, int frq, boolean fs) {}
    public void recreateWindow(int w, int h, int bpp, int frq, boolean fs) {}
    public Renderer getRenderer() {
        return new Renderer(){
            public void setCamera(Camera camera) {}
            public Camera getCamera() {return null;}
            public Camera createCamera(int width, int height) {return null;}
            public Camera getCamera(int width, int height) {return createCamera(width,height);}
            public AlphaState createAlphaState() {
                return new AlphaState(){
                    private static final long serialVersionUID = 1L;

					public void apply() {}
                };
            }
            public AlphaState getAlphaState() {return createAlphaState();}

            public AttributeState createAttributeState() {
                return new AttributeState(){
                    private static final long serialVersionUID = 1L;

					public void apply() {}
                };
            }
            public AttributeState getAttributeState() {return createAttributeState();}

            public CullState createCullState() {
                return new CullState(){
                    private static final long serialVersionUID = 1L;

					public void apply() {}
                };
            }
            public CullState getCullState() {return createCullState();}

            public DitherState createDitherState() {
                return new DitherState(){
                    private static final long serialVersionUID = 1L;

					public void apply() {
                    }
                };}
            public DitherState getDitherState() {return createDitherState();}

            public FogState createFogState() {
                return new FogState(){
                    private static final long serialVersionUID = 1L;

					public void apply() {}
                };
            }
            public FogState getFogState() {return createFogState();}

            public LightState createLightState() {
                return new LightState(){
                    private static final long serialVersionUID = 1L;

					public void apply() {}
                };
            }
            public LightState getLightState() {return createLightState();}

            public MaterialState createMaterialState() {
                return new MaterialState(){
                    private static final long serialVersionUID = 1L;

					public void apply() {}
                };
            }
            public MaterialState getMaterialState() {return createMaterialState();}

            public ShadeState createShadeState() {
                return new ShadeState(){
                    private static final long serialVersionUID = 1L;

					public void apply(){}
                };
            }
            public ShadeState getShadeState() {return createShadeState();}

            class TextureStateN extends TextureState{
                private static final long serialVersionUID = 1L;
				TextureStateN(){
                    numTexUnits=1;
                    texture = new Texture[numTexUnits];
                }
                public void delete(int unit) {}
                public void deleteAll() {}
                public void apply() {}
                public void assignTextureID(Texture t) {}
            }
            public TextureState createTextureState() {
                return new TextureStateN();
            }

            public TextureState getTextureState() {return createTextureState();}

            public WireframeState createWireframeState() {
                return new WireframeState(){
                    private static final long serialVersionUID = 1L;

					public void apply(){}
                };
            }
            public WireframeState getWireframeState() {return createWireframeState();}

            public ZBufferState createZBufferState() {
                return new ZBufferState(){
                    private static final long serialVersionUID = 1L;

					public void apply(){}
                };
            }
            public ZBufferState getZBufferState() {return createZBufferState();}

            public VertexProgramState createVertexProgramState() {
                return new VertexProgramState(){
                    private static final long serialVersionUID = 1L;
					public boolean isSupported() {return false;}
                    public void load(URL file) {}
                    public void apply() {}
                };
            }
            public VertexProgramState getVertexProgramState() {return createVertexProgramState();}

            public FragmentProgramState createFragmentProgramState() {
                return new FragmentProgramState(){
                    private static final long serialVersionUID = 1L;
					public boolean isSupported() {return false;}
                    public void load(URL file) {}
                    public void apply() {}
                };
            }
            public FragmentProgramState getFragmentProgramState() {return createFragmentProgramState();}

            public StencilState createStencilState() {
                return new StencilState(){
                    private static final long serialVersionUID = 1L;

					public void apply() {}
                };
            }
            public StencilState getStencilState() {return createStencilState();}

            public void enableStatistics(boolean value){}
            public void clearStatistics() {}
            public String getStatistics() {return null;}

            public StringBuffer getStatistics(StringBuffer a) {return null;}

            public void setBackgroundColor(ColorRGBA c) {}
            public ColorRGBA getBackgroundColor() {return null;}
            public void clearZBuffer() {}
            public void clearBackBuffer() {}
            public void clearBuffers() {}
            public void displayBackBuffer() {}
            public void setOrtho() {}
            public void setOrthoCenter() {}
            public void unsetOrtho() {}
            public boolean takeScreenShot(String filename) {return false;}
            public void grabScreenContents(IntBuffer buff, int x, int y, int w, int h) {}
            public void draw(Spatial s) {}
            public void drawBounds(Spatial s) {}
            public void drawBounds(BoundingVolume bv) {}
            public void drawBounds(Geometry g) {}
            public void draw(Point p) {}
            public void draw(Line l) {}
            public void draw(Curve c) {}
            public void draw(Text t) {}
            public void draw(TriMesh t) {}
            public void draw(WidgetRenderer wr) {}
            public RenderQueue getQueue() {return null;}
            public boolean isProcessingQueue() {return false;}
            public boolean checkAndAdd(Spatial s) {return false;}
            public boolean supportsVBO() {return false;}
    };
    }
    public RendererType getRendererType() {return null;}
    public boolean isClosing() {return false;}
    public void reset() {}
    public void close() {}
    public WidgetFont getFont(String fontName) {return null;}
    public TextureRenderer createTextureRenderer(int width, int height, boolean useRGB, boolean useRGBA, boolean useDepth, boolean isRectangle, int target, int mipmaps) {return null;}
    public Vector3f getScreenCoordinates(Vector3f worldPosition) {return null;}
    public Vector3f getScreenCoordinates(Vector3f worldPosition, Vector3f store) {return null;}
    public Vector3f getWorldCoordinates(Vector2f screenPosition, float zPos) {return null;}
    public Vector3f getWorldCoordinates(Vector2f screenPosition, float zPos, Vector3f store) {return null;}
}
