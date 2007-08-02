package com.jme.util.geom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.SharedBatch;
import com.jme.scene.batch.TriangleBatch;

public class GeometryTool {
    private static final Logger logger = Logger.getLogger(GeometryTool.class.getName());
    
    public static final int MV_SAME_NORMALS = 1;
    public static final int MV_SAME_TEXS = 2;
    public static final int MV_SAME_COLORS = 4;

    public static VertMap[] minimizeVerts(TriMesh mesh, int options) {
        VertMap[] result = new VertMap[mesh.getBatchCount()];
        
        for (int x = mesh.getBatchCount(); --x >= 0; )
            result[x] = minimizeVerts(mesh.getBatch(x), options);

        return result;
    }

    @SuppressWarnings("unchecked")
    public static VertMap minimizeVerts(TriangleBatch batch, int options) {
        if (batch instanceof SharedBatch)
            batch = ((SharedBatch)batch).getTarget();
        
        int vertCount = -1;
        int oldCount = batch.getVertexCount();
        int newCount = 0;
        
        VertMap result = new VertMap(batch);
                
        while (vertCount != newCount) {
            vertCount = batch.getVertexCount();
            // go through each vert...
            Vector3f[] verts = BufferUtils.getVector3Array(batch.getVertexBuffer());
            Vector3f[] norms = null;
            if (batch.getNormalBuffer() != null)
                norms = BufferUtils.getVector3Array(batch.getNormalBuffer());
            
            ColorRGBA[] colors = null;
            if (batch.getColorBuffer() != null)
                colors = BufferUtils.getColorArray(batch.getColorBuffer());
            
            Vector2f[][] tex = new Vector2f[batch.getNumberOfUnits()][];
            for (int x = 0; x < tex.length; x++) {
                if (batch.getTextureBuffer(x) != null) {
                    tex[x] = BufferUtils.getVector2Array(batch.getTextureBuffer(x));
                }
            }
    
            int[] inds = BufferUtils.getIntArray(batch.getIndexBuffer());
            
            HashMap<VertKey, Integer> store = new HashMap<VertKey, Integer>();
            int good = 0;
            for (int x = 0, max = verts.length; x < max; x++) {
                VertKey vkey = new VertKey(verts[x], norms != null ? norms[x] : null, colors != null ? colors[x] : null, getTexs(tex,x), options);
                // if we've already seen it, mark it for deletion and repoint the corresponding index
                if (store.containsKey(vkey)) {
                    int newInd = store.get(vkey);
                    result.replaceIndex(x, newInd);
                    findReplace(x, newInd, inds);
                    verts[x] = null;
                    if (norms != null)
                        norms[newInd].addLocal(norms[x].normalizeLocal());
                    if (colors != null)
                        colors[x] = null;
                } else {
                    store.put(vkey, x);
                    good++;
                }
            }
                
            ArrayList<Vector3f> newVects = new ArrayList<Vector3f>(good);
            ArrayList<Vector3f> newNorms = new ArrayList<Vector3f>(good);
            ArrayList<ColorRGBA> newColors = new ArrayList<ColorRGBA>(good);
            ArrayList[] newTexs = new ArrayList[batch.getNumberOfUnits()];
            for (int x = 0; x < newTexs.length; x++) {
                if (batch.getTextureBuffer(x) != null) {
                    newTexs[x] = new ArrayList<Vector2f>(good);
                }
            }

            // go through each vert
            // add non-duped verts, texs, normals to new buffers
            // and set into batch.
            int off = 0;
            for (int x = 0, max = verts.length; x < max; x++) {
                if (verts[x] == null) {
                    // shift indices above this down a notch.
                    decrementIndices(x-off, inds);
                    result.decrementIndices(x-off);
                    off++;
                } else {
                    newVects.add(verts[x]);
                    if (norms != null)
                        newNorms.add(norms[x].normalizeLocal());
                    if (colors != null)
                        newColors.add(colors[x]);
                    for (int y = 0; y < newTexs.length; y++) {
                        if (batch.getTextureBuffer(y) != null)
                            newTexs[y].add(tex[y][x]);
                    }
                }
            }

            batch.setVertexBuffer(BufferUtils.createFloatBuffer(newVects.toArray(new Vector3f[0])));
            if (norms != null)
                batch.setNormalBuffer(BufferUtils.createFloatBuffer(newNorms.toArray(new Vector3f[0])));
            if (colors != null)
                batch.setColorBuffer(BufferUtils.createFloatBuffer(newColors.toArray(new ColorRGBA[0])));
            
            for (int x = 0; x < newTexs.length; x++) {
                if (batch.getTextureBuffer(x) != null) {
                    batch.setTextureBuffer(BufferUtils.createFloatBuffer((Vector2f[])newTexs[x].toArray(new Vector2f[0])), x);
                }
            }
    
            batch.getIndexBuffer().clear();
            batch.getIndexBuffer().put(inds);
            newCount = batch.getVertexCount();
        }
        logger.info("batch: " + batch + " old: " + oldCount + " new: "
                + newCount);

        return result;
    }

    private static Vector2f[] getTexs(Vector2f[][] tex, int i) {
        Vector2f[] res = new Vector2f[tex.length];
        for (int x = 0; x < tex.length; x++) {
            if (tex[x] != null) {
                res[x] = tex[x][i];
            }
        }
        return res;
    }

    private static void findReplace(int oldI, int newI, int[] indices) {
        for (int x = indices.length; --x >= 0; )
            if (indices[x] == oldI) indices[x] = newI;
    }

    private static void decrementIndices(int above, int[] inds) {
        for (int x = inds.length; --x >= 0; )
            if (inds[x] >= above) inds[x]--;
    }

}
