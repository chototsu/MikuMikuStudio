package com.jmex.font3d;

import com.jme.bounding.OrientedBoundingBox;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.SharedBatch;
import com.jme.scene.batch.TriangleBatch;

/**
 * This class represents a peace of text compiled using the
 * {@link Font3D#createText(String, String, int, boolean, boolean, boolean)}.
 * 
 * @author emanuel
 */
public class Text3D extends Node implements JmeText {
    private static final long serialVersionUID = 7715674618025080804L;
    private Font3D factory;
    private float height, width;
    private int size;
    private StringBuffer text = new StringBuffer();
    private ColorRGBA fontcolor = new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f);
    private float localscale;
    TriMesh render_mesh;
    OrientedBoundingBox render_mesh_bounds;

    public Text3D(Font3D factory, String text, int size) {
        // Save for later
        this.factory = factory;

        // Setup a render-container
        render_mesh = new TriMesh("RenderMesh");
        render_mesh_bounds = new OrientedBoundingBox();
        attachChild(render_mesh);

        // And now scale to the correct "size" (all font are size 1)
        setSize(size);

        // Ready the glyphs
        setText(text);
    }

    @Override
    public void updateWorldBound() {
        worldBound = render_mesh_bounds.transform(getWorldRotation(),
                getWorldTranslation(), getWorldScale(), worldBound);
    }

    public TextFactory getFactory() {
        return factory;
    }

    public int getFlags() {
        return 0; // TODO: this should be working
    }

    public int getSize() {
        return size;
    }

    public StringBuffer getText() {
        return text;
    }

    public void setSize(int size) {
        this.size = size;
        localscale = (float) size / (float) factory.getFont().getSize();
        render_mesh.setLocalScale(localscale);
    }

    public void setText(String text) {
        // Clean out the old batches
        render_mesh.clearBatches();

        // Set width and text to zip
        this.width = 0;
        this.height = 0;
        this.text.setLength(0);

        // Now append the text
        appendText(text);
    }

    public void appendText(String moretext) {
        // Create the damn things
        for (char c : moretext.toCharArray()) {
            Glyph3D glyph = factory.getGlyph(c);
            Glyph3DBatch b = glyph.getBatch();
            if (b != null && b.getVertexCount() > 0) {
                SharedBatch myb = new SharedBatch(b);
                // myb.translatePoints(width, 0, 0);
                render_mesh.addBatch(myb);
            }
            width += glyph.getBounds().getWidth() * localscale;
            height = (float) Math.max(height, glyph.getBounds().getHeight()
                    * localscale);
        }
        this.text.append(moretext);
        updateModelBound();
    }

    public void updateModelBound() {
        // TODO Auto-generated method stub
        render_mesh_bounds.extent.x = width / 2;
        render_mesh_bounds.extent.y = height / 2;
        render_mesh_bounds.extent.z = localscale / 2;
        // render_mesh_bounds.xAxis.set(Vector3f.UNIT_X);
        // render_mesh_bounds.yAxis.set(Vector3f.UNIT_Y);
        // render_mesh_bounds.zAxis.set(Vector3f.UNIT_Z);
        render_mesh_bounds.getCenter().x = width / 2;
        render_mesh_bounds.getCenter().y = height / 2;
        // render_mesh_bounds.getCenter().z = localscale/2;
        // render_mesh_bounds.computeCorners();
        // System.out.println("Bounds:"+render_mesh_bounds.extent);
    }

    @Override
    public void draw(Renderer r) {
        // super.draw(r);

        TriangleBatch batch;
        float oldx = render_mesh.getLocalTranslation().x;
        int batchindx = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            // Get glyph and set translation
            Glyph3D glyph = factory.getGlyph(c);

            // Get batch (if any) and draw
            Glyph3DBatch b = glyph.getBatch();
            if (b != null && b.getVertexCount() > 0) {
                batch = render_mesh.getBatch(batchindx++);
                if (batch != null && batch.isEnabled())
                    batch.onDraw(r);
            }
            render_mesh.getLocalTranslation().x += glyph.getBounds().getWidth()
                    * localscale;
            render_mesh.updateWorldVectors();
        }
        render_mesh.getLocalTranslation().x = oldx;
        render_mesh.updateWorldVectors();

        /*
         * for(int i = 0 ; i < getBatchCount(); i++) { TriangleBatch batch =
         * getBatch(i); //factory.getRenderTriMesh().getLocalTranslation().x +=
         * glyph.getBounds().getWidth() * localscale; if(batch != null &&
         * batch.getVertexCount() > 0) { r.draw(batch); } }
         */
    }

    public ColorRGBA getFontColor() {
        return fontcolor;
    }

    public void setFontColor(ColorRGBA colorRGBA) {
        fontcolor.set(colorRGBA);
        render_mesh.setDefaultColor(fontcolor);
    }

    public float getWidth() {
        return width;
    }

}
