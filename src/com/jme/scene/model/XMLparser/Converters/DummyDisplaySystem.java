package com.jme.scene.model.XMLparser.Converters;

import com.jme.system.DisplaySystem;
import com.jme.renderer.*;
import com.jme.scene.state.*;
import com.jme.scene.*;
import com.jme.bounding.BoundingVolume;
import com.jme.effects.Tint;
import com.jme.curve.Curve;
import com.jme.input.Mouse;
import com.jme.widget.WidgetRenderer;
import com.jme.widget.font.WidgetFont;
import com.jme.math.Vector3f;
import com.jme.math.Vector2f;

import java.net.URL;

/**
 * Started Date: Jul 2, 2004<br><br>
 *
 * This class is a dummy class that is not suppost to be rendered.  It is here to allow the easy
 * creation of dummy jME objects (like various RenderState and Spatial) so that they can be
 * used by conversion utilities to read/write jME.  It is <b>NOT</b> to be used for rendering as it
 * won't do anything at all.
 * @author Jack Lindamood
 */
class DummyDisplaySystem extends DisplaySystem{
    public boolean isValidDisplayMode(int width, int height, int bpp, int freq) {return false;}
    public void setVSyncEnabled(boolean enabled) {}
    public void setTitle(String title) {}
    public void createWindow(int w, int h, int bpp, int frq, boolean fs) {}
    public Renderer getRenderer() {
        return new Renderer(){
            public void setCamera(Camera camera) {}
            public Camera getCamera() {return null;}
            public Camera getCamera(int width, int height) {return null;}
            public AlphaState getAlphaState() {
                return new AlphaState(){
                    public void apply() {}
                };
            }
            public AttributeState getAttributeState() {
                return new AttributeState(){
                    public void apply() {}
                };
            }
            public CullState getCullState() {
                return new CullState(){
                    public void apply() {}
                };
            }
            public DitherState getDitherState() {
                return new DitherState(){
                    public void apply() {
                    }
                };}
            public FogState getFogState() {
                return new FogState(){
                    public void apply() {}
                };
            }
            public LightState getLightState() {
                return new LightState(){
                    public void apply() {}
                };
            }
            public MaterialState getMaterialState() {
                return new MaterialState(){
                    public void apply() {}
                };
            }
            public ShadeState getShadeState() {
                return new ShadeState(){
                    public void apply(){}
                };
            }
            class TextureStateN extends TextureState{
                TextureStateN(){
                    numTexUnits=1;
                }
                public void delete(int unit) {}
                public void deleteAll() {}
                public void apply() {}
            }
            public TextureState getTextureState() {
                return new TextureStateN();
            }
            public WireframeState getWireframeState() {
                return new WireframeState(){
                    public void apply(){}
                };
            }
            public ZBufferState getZBufferState() {
                return new ZBufferState(){
                    public void apply(){}
                };
            }
            public VertexProgramState getVertexProgramState() {
                return new VertexProgramState(){
                    public boolean isSupported() {return false;}
                    public void load(URL file) {}
                    public void apply() {}
                };
            }
            public StencilState getStencilState() {
                return new StencilState(){
                    public void apply() {}
                };
            }
            public void enableStatistics(boolean value){}
            public void clearStatistics() {}
            public String getStatistics() {return null;}
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
            public void draw(Spatial s) {}
            public void drawBounds(Clone c) {}
            public void drawBounds(Spatial s) {}
            public void drawBounds(BoundingVolume bv) {}
            public void drawBounds(Geometry g) {}
            public void draw(Tint t) {}
            public void draw(Point p) {}
            public void draw(Line l) {}
            public void draw(Curve c) {}
            public void draw(Mouse m) {}
            public void draw(Text t) {}
            public void draw(TriMesh t) {}
            public void draw(CloneNode cn) {}
            public void draw(Clone c) {}
            public void draw(WidgetRenderer wr) {}
            public RenderQueue getQueue() {return null;}
            public boolean isProcessingQueue() {return false;}
            public boolean checkAndAdd(Spatial s) {return false;}
            public boolean supportsVBO() {return false;}
        };
    }
    public RendererType getRendererType() {return null;}
    public boolean isCreated() {return false;}
    public boolean isClosing() {return false;}
    public void reset() {}
    public void close() {}
    public WidgetFont getFont(String fontName) {return null;}
    public TextureRenderer createTextureRenderer(int width, int height, boolean useRGB, boolean useRGBA, boolean useDepth, boolean isRectangle, int target, int mipmaps) {return null;}
    public Vector3f getScreenCoordinates(Vector3f worldPosition) {return null;}
    public Vector3f getWorldCoordinates(Vector2f screenPosition, float zPos) {return null;}
}