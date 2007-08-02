package com.jmex.font3d;

import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.SceneElement;
import com.jme.scene.TriMesh;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jmex.font3d.math.ClosedPolygon;

/**
 * This class represents a font ready to be used for 3D.
 *
 * Known bugs:
 * 
 * - When glyphs are constructed from other glyphs, the shape returned by
 *   gv.getGlyphOutline(0); has them all cluddered up. This might be a bug in the
 *   VM, and I have no time to fix it, that is why the loading of each glyph has a
 *   try-catch-all statement around it.
 * 
 * @author emanuel
 */
public class Font3D implements TextFactory {
    private static final Logger logger = Logger.getLogger(Font3D.class
            .getName());
    
    private static Hashtable<String, Font3D> loadedFonts = new Hashtable<String, Font3D>();

    // This trimesh is only used to when rendering, because orphan Batches
    // cannot be rendered as it is now.
    TriMesh render_trimesh = new TriMesh();

    // The glyphs created from the font.
    Glyph3D glyph3Ds[] = new Glyph3D[256];

    // Settings
    Font font;
    private double flatness;
    private boolean drawSides;
    private boolean drawFront;
    private boolean drawBack;

	private static AlphaState general_alphastate = null;
	private static MaterialState general_diffuse_material = null;
	boolean has_alpha_blending = false;
	boolean has_diffuse_material = false;


    // Create the
    public Font3D(Font font, double flatness, boolean drawSides,
            boolean drawFront, boolean drawBack) {
        if (font.getSize() != 1) {
            font = font.deriveFont(1.0f);
        }
        // Save for later
        this.font = font;
        this.flatness = flatness;
        this.drawSides = drawSides;
        this.drawFront = drawFront;
        this.drawBack = drawBack;

        // Clear our "parent mesh"
        render_trimesh.clearBatches();

        // Generate the glyphs
        for (int g = 0; g < 256; g++) {
            try {
                // if(g != 'H') // TEST
                // continue;
                // logger.info("Glyph: "+g+":"+(char)g);

                // GlyphVector gv = font.createGlyphVector(new
                // FontRenderContext(null, true, true), new char[] { (char)g });
                GlyphVector gv = font.layoutGlyphVector(new FontRenderContext(
                        null, true, true), new char[] { (char) g }, 0, 1, 0);
                gv.performDefaultLayout();
                ClosedPolygon closedPolygon = null;
                Glyph3D fontGlyph = new Glyph3D((char)g);

                // Get the shape
                Shape s = gv.getGlyphOutline(0);
                // GlyphMetrics metrics = gv.getGlyphMetrics(0);
                PathIterator pi = new FlatteningPathIterator(s
                        .getPathIterator(new AffineTransform()), flatness);
                // logger.info("\n\n\n\nWIND IS BLOWING:
                // "+(pi.getWindingRule() == PathIterator.WIND_EVEN_ODD ?
                // "WIND_EVEN_ODD" : "WIND_NON_ZERO"));
                float[] coords = new float[6];
                while (!pi.isDone()) {
                    int seg = pi.currentSegment(coords);
                    switch (seg) {
                        case PathIterator.SEG_MOVETO:
                            closedPolygon = new ClosedPolygon();
                            closedPolygon.addPoint(new Vector3f(coords[0],
                                    -coords[1], 0));
                            break;
                        case PathIterator.SEG_LINETO:
                            closedPolygon.addPoint(new Vector3f(coords[0],
                                    -coords[1], 0));
                            break;
                        case PathIterator.SEG_CLOSE:
                            closedPolygon.close();
                            fontGlyph.addPolygon(closedPolygon);
                            closedPolygon = null;
                            break;
                        default:
                            throw new IllegalArgumentException(
                                    "unknown segment type " + seg);
                    }
                    pi.next();
                }

                // If we added something then we have a valid glyph !
                fontGlyph.setBounds(gv.getGlyphLogicalBounds(0).getBounds2D());
                if (!fontGlyph.isEmpty()) {
                    // Time to triangulate the surface of the glyph
                    fontGlyph.triangulate();

                    // And create the actual geometry.
                    fontGlyph.generateBatch(drawSides, drawFront, drawBack);
                    if(fontGlyph.getBatch() != null)
                    {
                    	fontGlyph.setBatchId(render_trimesh.getBatchCount());
                    	render_trimesh.addBatch(fontGlyph.getBatch());
                    }
                }
                glyph3Ds[g] = fontGlyph;
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error in char: ("
                                + g
                                + ":"
                                + (char) g
                                + "), the following is most likely due to glyphs constructed " +
                                        "from other glyphs.... that does not work.", e);
            }
        }
        
        // Apply a Z-state
        ZBufferState zstate = DisplaySystem.getDisplaySystem().getRenderer().createZBufferState();
		zstate.setFunction(ZBufferState.CF_LESS);
		zstate.setWritable(true);
		zstate.setEnabled(true);
		render_trimesh.setRenderState(zstate);
		
        // Finally create display-lists for each batch
        render_trimesh.lockMeshes();
    }

    /**
     * This method is used when text wants to render, much like the shared
     * 
     * @return
     */
    public TriMesh getRenderTriMesh() {
        return render_trimesh;
    }

    /**
     * Method for creating the text from the font. TODO: react on the flags
     * parameter.
     * 
     * @param text
     * @param size
     * @param flags
     * @return
     */
    public Text3D createText(String text, float size, int flags) {

        Text3D text_obj = new Text3D(this, text, size);

        return text_obj;
    }

    /**
     * This method loads and caches a font, call this before calls to
     * {@link #createText(String, int)}.
     * 
     * @param fontname
     * @param font
     */
    public static void loadFont3D(String fontname, Font font, double flatness,
            boolean drawSides, boolean drawFront, boolean drawBack) {
        logger.info("FontSize:  " + font.getSize());
        logger.info("FontSize2D:" + font.getSize2D());
        Font3D f = new Font3D(font, flatness, drawSides, drawFront, drawBack);
        loadedFonts.put(fontname, f);
    }

    /**
     * Removes a cached Font3D.
     * 
     * @param fontname
     */
    public static void unloadFont(String fontname) {
        loadedFonts.remove(fontname);
    }

    /**
     * This method will create a peace of 3d text from this font.
     * 
     * @param fontname
     * @param text
     * @param size
     * @return
     */
    public static Text3D createText(String fontname, String text, float size,
            int flags) {
        // Find the cached font and create a text instance.
        Font3D cachedf = loadedFonts.get(fontname);

        return cachedf.createText(text, size, flags);
    }

    public Glyph3D getGlyph(char c) {
        return glyph3Ds[c];
    }

    public Font getFont() {
        return font;
    }
    
    public double getFlatness()
    {
    	return flatness;
    }
    
    public boolean drawSides()
    {
    	return drawSides;
    }
    
    public boolean drawFront()
    {
    	return drawFront;
    }
    
    public boolean drawBack()
    {
    	return drawBack;
    }

	public Glyph3D[] getGlyphs()
	{
		return glyph3Ds;
	}

	public boolean isMeshLocked()
	{
		return (render_trimesh.getLocks() & SceneElement.LOCKED_MESH_DATA) != 0;
	}

	public void unlockMesh()
	{
		render_trimesh.unlockMeshes();		
	}

	public void lockMesh()
	{
		render_trimesh.lockMeshes();
	}
	
	public void enableAlphaState()
	{
		if(has_alpha_blending)
			return;
		
		if(general_alphastate == null)
		{
	        general_alphastate = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
	        general_alphastate.setBlendEnabled(true);
	        general_alphastate.setSrcFunction(AlphaState.SB_SRC_ALPHA);
	        general_alphastate.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
	        general_alphastate.setTestEnabled(true);
	        general_alphastate.setTestFunction(AlphaState.TF_ALWAYS);
	        general_alphastate.setEnabled(true);
		}
		render_trimesh.setRenderState(general_alphastate);
		render_trimesh.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
		has_alpha_blending = true;
		render_trimesh.updateRenderState();
	}
	
	public void enableDiffuseMaterial()
	{
		if(has_diffuse_material)
			return;
		
		if(general_diffuse_material == null)
		{
			general_diffuse_material = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
			general_diffuse_material.setEnabled(true);
			general_diffuse_material.setColorMaterial(MaterialState.CM_DIFFUSE);
		}
		render_trimesh.setRenderState(general_diffuse_material);
		render_trimesh.updateRenderState();
	}
	
}
