package com.jmex.font3d;

import java.nio.IntBuffer;

import com.jme.math.Vector3f;
import com.jme.scene.batch.TriangleBatch;
import com.jme.util.geom.BufferUtils;
import com.jmex.font3d.math.PlanarEdge;
import com.jmex.font3d.math.PlanarVertex;
import com.jmex.font3d.math.TriangulationVertex;

/**
 * This class holds one glyph as a trimesh. The data is stored in this order:
 * sides,front,back. TODO: We should have a threshold on the angle of the
 * outline, that way we could make smooth only where it should actually be
 * smooth.
 * 
 * @author emanuel
 */
public class Glyph3DBatch extends TriangleBatch {
    private static final long serialVersionUID = -2744055578491222293L;

    public Glyph3DBatch(Glyph3D glyph3D, boolean drawSides, boolean drawFront,
            boolean drawBack) {
        // Calculate how many vertices we need
        int vertex_count = 0;
        int triangle_count = 0;
        if (drawSides) {
            vertex_count += glyph3D.getOutline().size() * 2;
        }
        if (drawBack) {
            vertex_count += glyph3D.getVertices().size();
        }
        if (drawFront) {
            vertex_count += glyph3D.getVertices().size();
        }

        // Calculate how many triangles we need
        if (drawSides) {
            triangle_count += glyph3D.getOutline().size() * 2;
        }
        if (drawBack) {
            triangle_count += glyph3D.getSurface().capacity() / 3;
        }
        if (drawFront) {
            triangle_count += glyph3D.getSurface().capacity() / 3;
        }

        // Allocate what we need
        Vector3f verts[] = new Vector3f[vertex_count];
        Vector3f norms[] = new Vector3f[vertex_count];
        IntBuffer triangles = BufferUtils.createIntBuffer(triangle_count * 3);
        // triangles.rewind();
        int vcount = 0; // Used to pad indexes.
        // Add all the vertices (either one or two layers)
        int numverts = glyph3D.getVertices().size();
        if (drawSides) {
            for (TriangulationVertex v : glyph3D.getVertices()) {
                norms[v.getIndex()] = glyph3D.getOutlineNormals()[v.getIndex()];
                verts[v.getIndex()] = new Vector3f(v.getPoint());
                verts[v.getIndex()].z += 0.5f;
                norms[v.getIndex() + numverts] = glyph3D.getOutlineNormals()[v
                        .getIndex()];
                verts[v.getIndex() + numverts] = new Vector3f(v.getPoint());
                verts[v.getIndex() + numverts].z -= 0.5f;
            }
            vcount += numverts * 2;

            // Add indices
            for (PlanarEdge e : glyph3D.getOutline()) {
                if (!e.isRealEdge())
                    continue;
                PlanarVertex src = e.getOrigin();
                PlanarVertex dst = e.getDestination();
                int v1 = src.getIndex();
                int v2 = dst.getIndex();
                int v3 = dst.getIndex() + numverts;
                int v4 = src.getIndex() + numverts;
                triangles.put(new int[] { v1, v3, v2, v3, v1, v4 });
            }
        }
        if (drawFront) {
            Vector3f frontnormal = new Vector3f(0, 0, -1);
            for (TriangulationVertex v : glyph3D.getVertices()) {
                norms[vcount + v.getIndex()] = frontnormal;
                verts[vcount + v.getIndex()] = new Vector3f(v.getPoint());
                verts[vcount + v.getIndex()].z -= 0.5f;
            }
            // We need to add the offset (vertcount)
            glyph3D.getSurface().rewind();
            while (glyph3D.getSurface().remaining() > 0) {
                triangles.put(glyph3D.getSurface().get() + vcount);
            }
            vcount += numverts;
        }
        if (drawBack) {
            Vector3f backnormal = new Vector3f(0, 0, 1);
            for (TriangulationVertex v : glyph3D.getVertices()) {
                norms[vcount + v.getIndex()] = backnormal;
                verts[vcount + v.getIndex()] = new Vector3f(v.getPoint());
                verts[vcount + v.getIndex()].z += 0.5f;
            }
            glyph3D.getSurface().rewind();
            while (glyph3D.getSurface().remaining() > 0) {
                int tri[] = { glyph3D.getSurface().get() + vcount,
                        glyph3D.getSurface().get() + vcount,
                        glyph3D.getSurface().get() + vcount };
                triangles.put(tri[2]);
                triangles.put(tri[1]);
                triangles.put(tri[0]);
            }
            vcount += numverts;
        }

        // Now lets give it a spin....
        setVertexBuffer(BufferUtils.createFloatBuffer(verts));
        setNormalBuffer(BufferUtils.createFloatBuffer(norms));
        setIndexBuffer(triangles);
        // System.out.println("triangles:"+triangles);
    }
}
