package com.jme.scene.geometryinstancing;

import com.jme.math.Vector3f;
import com.jme.scene.batch.TriangleBatch;
import com.jme.scene.geometryinstancing.instance.GeometryInstance;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * <code>GeometryBatchInstance</code> uses a <code>GeometryBatchInstanceAttributes</code>
 * to define an instance of object in world space. Uses TriangleBatch as source
 * data for the instance, instead of GeomBatch which does not have an index
 * buffer.
 *
 * @author Patrik Lindegrén
 */
public class GeometryBatchInstance
        extends GeometryInstance<GeometryBatchInstanceAttributes> {
    public TriangleBatch instanceBatch;

    public GeometryBatchInstance(TriangleBatch sourceBatch,
                                 GeometryBatchInstanceAttributes attributes) {
        super(attributes);
        this.instanceBatch = sourceBatch;
    }

    /** Vector used to store and calculate world transformations */
    Vector3f worldVector = new Vector3f();

    /**
     * Uses the instanceAttributes to transform the instanceBatch into world
     * coordinates. The transformed instance batch is added to the batch.
     *
     * @param batch
     */
    public void commit(TriangleBatch batch) {
        if (batch == null || instanceBatch == null || getNumVerts() <= 0) {
            return;
        }

        int nVerts = 0;

        // Texture buffers
        for (int i = 0; i < 8; i++) {
            FloatBuffer texBufSrc = instanceBatch.getTextureBuffer(i);
            FloatBuffer texBufDst = batch.getTextureBuffer(i);
            if (texBufSrc != null && texBufDst != null) {
                texBufSrc.rewind();
                texBufDst.put(texBufSrc);
            }
        }

        // Vertex buffer
        FloatBuffer vertBufSrc = instanceBatch.getVertexBuffer();
        FloatBuffer vertBufDst = batch.getVertexBuffer();
        if (vertBufSrc != null && vertBufDst != null) {
            vertBufSrc.rewind();
            nVerts = vertBufDst.position() / 3;
            for (int i = 0; i < instanceBatch.getVertexCount(); i++) {
                worldVector.set(vertBufSrc.get(), vertBufSrc.get(),
                                vertBufSrc.get());
                attributes.getWorldMatrix().mult(worldVector, worldVector);
                vertBufDst.put(worldVector.x);
                vertBufDst.put(worldVector.y);
                vertBufDst.put(worldVector.z);
            }
        }

        // Color buffer
        FloatBuffer colorBufSrc = instanceBatch.getColorBuffer();
        FloatBuffer colorBufDst = batch.getColorBuffer();
        if (colorBufSrc != null && colorBufDst != null) {
            colorBufSrc.rewind();
            for (int i = 0; i < instanceBatch.getVertexCount(); i++) {
                colorBufDst.put(colorBufSrc.get() * attributes.getColor().r);
                colorBufDst.put(colorBufSrc.get() * attributes.getColor().g);
                colorBufDst.put(colorBufSrc.get() * attributes.getColor().b);
                colorBufDst.put(colorBufSrc.get() * attributes.getColor().a);
            }
        } else if (colorBufDst != null) {
            for (int i = 0; i < instanceBatch.getVertexCount(); i++) {
                colorBufDst.put(attributes.getColor().r);
                colorBufDst.put(attributes.getColor().g);
                colorBufDst.put(attributes.getColor().b);
                colorBufDst.put(attributes.getColor().a);
            }
        }

        // Normal buffer
        FloatBuffer normalBufSrc = instanceBatch.getNormalBuffer();
        FloatBuffer normalBufDst = batch.getNormalBuffer();
        if (normalBufSrc != null && normalBufDst != null) {
            normalBufSrc.rewind();
            for (int i = 0; i < instanceBatch.getVertexCount(); i++) {
                worldVector.set(normalBufSrc.get(), normalBufSrc.get(),
                                normalBufSrc.get());
                attributes.getNormalMatrix().mult(worldVector, worldVector);
                worldVector.normalizeLocal();
                normalBufDst.put(worldVector.x);
                normalBufDst.put(worldVector.y);
                normalBufDst.put(worldVector.z);
            }
        }

        // Index buffer
        IntBuffer indexBufSrc = instanceBatch.getIndexBuffer();
        IntBuffer indexBufDst = batch.getIndexBuffer();
        if (indexBufSrc != null && indexBufDst != null) {
            indexBufSrc.rewind();
            for (int i = 0; i < instanceBatch.getMaxIndex(); i++) {
                indexBufDst.put(nVerts + indexBufSrc.get());
            }
        }
    }

    public int getNumIndices() {
        if (instanceBatch == null) {
            return 0;
        }
        return instanceBatch.getMaxIndex();
    }

    public int getNumVerts() {
        if (instanceBatch == null) {
            return 0;
        }
        return instanceBatch.getVertexCount();
    }
}