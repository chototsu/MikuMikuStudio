/*
 * Copyright (c) 2003-2006 jMonkeyEngine All rights reserved. Redistribution and
 * use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met: * Redistributions of source
 * code must retain the above copyright notice, this list of conditions and the
 * following disclaimer. * Redistributions in binary form must reproduce the
 * above copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the distribution. *
 * Neither the name of 'jMonkeyEngine' nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme.scene.shape;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jme.math.FastMath;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.TriangleBatch;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;

/**
 * <code>Icosahedron</code>
 * 
 * @author Joshua Slack
 * @version $Revision: 1.4 $
 */
public class Icosahedron extends TriMesh {
    private static final long serialVersionUID = 1L;

    private static final int NUM_POINTS = 12;
    private static final int NUM_TRIS = 20;

    private float sideLength;

    public Icosahedron() {}
    
    /**
     * Creates an Icosahedron (think of 20-sided dice) with center at the origin.
     * The length of the sides will be as specified in sideLength.
     * 
     * @param name
     *            The name of the Icosahedron.
     * @param sideLength
     *            The length of each side of the Icosahedron.
     */
    public Icosahedron(String name, float sideLength) {
        super(name);
        this.sideLength = sideLength;

        TriangleBatch tBatch = getBatch(0);

        // allocate vertices
        tBatch.setVertexCount(NUM_POINTS);
        tBatch.setVertexBuffer(BufferUtils.createVector3Buffer(NUM_POINTS));
        tBatch.setNormalBuffer(BufferUtils.createVector3Buffer(NUM_POINTS));
        tBatch.setTextureBuffer(BufferUtils.createVector2Buffer(NUM_POINTS), 0);

        tBatch.setTriangleQuantity(NUM_TRIS);
        tBatch.setIndexBuffer(BufferUtils.createIntBuffer(3 * tBatch
                .getTriangleCount()));

        setVertexData();
        setNormalData();
        setTextureData();
        setIndexData();
    }

    private void setIndexData() {
        TriangleBatch tBatch = getBatch(0);
        IntBuffer indices = tBatch.getIndexBuffer();
        indices.rewind();
        indices.put(0).put(8).put(4);
        indices.put(0).put(5).put(10);
        indices.put(2).put(4).put(9);
        indices.put(2).put(11).put(5);
        indices.put(1).put(6).put(8);
        indices.put(1).put(10).put(7);
        indices.put(3).put(9).put(6);
        indices.put(3).put(7).put(11);
        indices.put(0).put(10).put(8);
        indices.put(1).put(8).put(10);
        indices.put(2).put(9).put(11);
        indices.put(3).put(11).put(9);
        indices.put(4).put(2).put(0);
        indices.put(5).put(0).put(2);
        indices.put(6).put(1).put(3);
        indices.put(7).put(3).put(1);
        indices.put(8).put(6).put(4);
        indices.put(9).put(4).put(6);
        indices.put(10).put(5).put(7);
        indices.put(11).put(7).put(5);

        if (!true) { // outside view
            for (int i = 0; i < tBatch.getTriangleCount(); i++) {
                int iSave = tBatch.getIndexBuffer().get(3 * i + 1);
                tBatch.getIndexBuffer().put(3 * i + 1,
                        tBatch.getIndexBuffer().get(3 * i + 2));
                tBatch.getIndexBuffer().put(3 * i + 2, iSave);
            }
        }

    }

    private void setTextureData() {
        TriangleBatch batch = getBatch(0);
        Vector2f tex = new Vector2f();
        Vector3f vert = new Vector3f();
        for (int i = 0; i < NUM_POINTS; i++) {
            BufferUtils.populateFromBuffer(vert, batch.getVertexBuffer(), i);
            if (FastMath.abs(vert.z) < sideLength) {
                tex.x = 0.5f * (1.0f + FastMath.atan2(vert.y, vert.x)
                        * FastMath.INV_PI);
            } else {
                tex.x = 0.5f;
            }
            tex.y = FastMath.acos(vert.z) * FastMath.INV_PI;
            batch.getTextureBuffers().get(0).put(tex.x).put(tex.y);
        }
    }

    private void setNormalData() {
        TriangleBatch batch = getBatch(0);
        Vector3f norm = new Vector3f();
        for (int i = 0; i < NUM_POINTS; i++) {
            BufferUtils.populateFromBuffer(norm, batch.getVertexBuffer(), i);
            norm.normalizeLocal();
            BufferUtils.setInBuffer(norm, batch.getNormalBuffer(), i);
        }
    }

    private void setVertexData() {
        TriangleBatch batch = getBatch(0);
        float fGoldenRatio = 0.5f * (1.0f + FastMath.sqrt(5.0f));
        float fInvRoot = 1.0f / FastMath.sqrt(1.0f + fGoldenRatio
                * fGoldenRatio);
        float fU = fGoldenRatio * fInvRoot * sideLength;
        float fV = fInvRoot * sideLength;

        FloatBuffer vbuf = batch.getVertexBuffer();
        vbuf.rewind();
        vbuf.put(fU).put(fV).put(0.0f);
        vbuf.put(-fU).put(fV).put(0.0f);
        vbuf.put(fU).put(-fV).put(0.0f);
        vbuf.put(-fU).put(-fV).put(0.0f);
        vbuf.put(fV).put(0.0f).put(fU);
        vbuf.put(fV).put(0.0f).put(-fU);
        vbuf.put(-fV).put(0.0f).put(fU);
        vbuf.put(-fV).put(0.0f).put(-fU);
        vbuf.put(0.0f).put(fU).put(fV);
        vbuf.put(0.0f).put(-fU).put(fV);
        vbuf.put(0.0f).put(fU).put(-fV);
        vbuf.put(0.0f).put(-fU).put(-fV);
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(sideLength, "sideLength", 0);
        
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        sideLength = capsule.readFloat("sideLength", 0);
        
    }
}
