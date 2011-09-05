/*
 * Copyright (c) 2011 Kazuhiko Kobayashi All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * <p/>
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * <p/>
 * * Neither the name of 'MMDLoaderJME' nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * <p/>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package projectkyoto.jme3.mmd;

import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.math.Matrix4f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;

/**
 *
 * @author kobayasi
 */
public class PMDMesh extends Mesh {

    short boneIndexArray[];
    Matrix4f boneMatrixArray[];
    VertexBuffer vbBackup;
    VertexBuffer nbBackup;

    public PMDMesh() {
        super();
    }

    public short[] getBoneIndexArray() {
        return boneIndexArray;
    }

    public void setBoneIndexArray(short[] boneIndexArray) {
        this.boneIndexArray = boneIndexArray;
    }

    public Matrix4f[] getBoneMatrixArray() {
        return boneMatrixArray;
    }

    public void setBoneMatrixArray(Matrix4f[] boneMatrixArray) {
        this.boneMatrixArray = boneMatrixArray;
    }
    BoundingVolume bound = new BoundingBox(Vector3f.ZERO, 20, 20, 20);

    @Override
    public BoundingVolume getBound() {
        return bound;
    }

    @Override
    public PMDMesh clone() {
        PMDMesh newMesh = (PMDMesh) super.clone();
        newMesh.boneMatrixArray = new Matrix4f[boneMatrixArray.length];
        for (int i = 0; i < newMesh.boneMatrixArray.length; i++) {
            newMesh.boneMatrixArray[i] = new Matrix4f();
        }
        return newMesh;
    }

    public VertexBuffer getNbBackup() {
        return nbBackup;
    }

    public void setNbBackup(VertexBuffer nbBackup) {
        this.nbBackup = nbBackup;
    }

    public VertexBuffer getVbBackup() {
        return vbBackup;
    }

    public void setVbBackup(VertexBuffer vbBackup) {
        this.vbBackup = vbBackup;
    }

    public void createSoftwareSkinningBuffers() {
        boolean retryFlag = false;
        for (;;) {
            try {
                VertexBuffer vb;
                vb = new VertexBuffer(VertexBuffer.Type.Position);
                FloatBuffer vfb = BufferUtils.clone((FloatBuffer) vbBackup.getData());
                vb.setupData(VertexBuffer.Usage.Dynamic, 3, VertexBuffer.Format.Float, vfb);
                clearBuffer(VertexBuffer.Type.Position);
                setBuffer(vb);
                break;
            } catch (OutOfMemoryError ex) {
                if (!retryFlag) {
                    System.gc();
                    retryFlag = true;
                } else {
                    throw new RuntimeException(ex);
                }
            }
        }
        retryFlag = false;
        for (;;) {
            try {
                VertexBuffer nb;
                nb = new VertexBuffer(VertexBuffer.Type.Normal);
                FloatBuffer nfb = BufferUtils.clone((FloatBuffer) nbBackup.getData());
                nb.setupData(VertexBuffer.Usage.Dynamic, 3, VertexBuffer.Format.Float, nfb);
                clearBuffer(VertexBuffer.Type.Normal);
                setBuffer(nb);
                break;
            } catch (OutOfMemoryError ex) {
                if (!retryFlag) {
                    System.gc();
                    retryFlag = true;
                } else {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    public void releaseSoftwareSkinningBufferes() {
        clearBuffer(VertexBuffer.Type.Position);
        setBuffer(vbBackup);
        clearBuffer(VertexBuffer.Type.Normal);
        setBuffer(nbBackup);
    }
}
