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
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.Matrix4f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 *
 * @author kobayasi
 */
public class PMDSkinMesh extends Mesh {

    int boneIndexArray[];
    Matrix4f boneMatrixArray[];
    VertexBuffer skinvb2;
//    VertexBuffer skinnb2;
    int boneMatricesParamIndex = -1;
    ShortBuffer boneIndexBuffer;
    FloatBuffer boneMatrixBuffer;
    public PMDSkinMesh() {
        super();
    }

    public int[] getBoneIndexArray() {
        return boneIndexArray;
    }

    public void setBoneIndexArray(int[] boneIndexArray) {
        this.boneIndexArray = boneIndexArray;
    }

    public Matrix4f[] getBoneMatrixArray() {
        return boneMatrixArray;
    }

    public void setBoneMatrixArray(Matrix4f[] boneMatrixArray) {
        this.boneMatrixArray = boneMatrixArray;
    }

//    public VertexBuffer getSkinnb2() {
//        return skinnb2;
//    }
//
//    public void setSkinnb2(VertexBuffer skinnb2) {
//        this.skinnb2 = skinnb2;
//    }

    public VertexBuffer getSkinvb2() {
        return skinvb2;
    }

    public void setSkinvb2(VertexBuffer skinvb2) {
        this.skinvb2 = skinvb2;
    }

    public ShortBuffer getBoneIndexBuffer() {
        return boneIndexBuffer;
    }

    public void setBoneIndexBuffer(ShortBuffer boneIndexBuffer) {
        this.boneIndexBuffer = boneIndexBuffer;
    }

    public FloatBuffer getBoneMatrixBuffer() {
        return boneMatrixBuffer;
    }

    public void setBoneMatrixBuffer(FloatBuffer boneMatrixBuffer) {
        this.boneMatrixBuffer = boneMatrixBuffer;
    }
    
    BoundingVolume bound = new BoundingBox(Vector3f.ZERO, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);

    @Override
    public BoundingVolume getBound() {
        return bound; //super.getBound();
    }
    @Override
    public synchronized PMDSkinMesh clone() {
        PMDSkinMesh newMesh = (PMDSkinMesh)super.clone();
//        newMesh.boneMatrixArray = new Matrix4f[boneMatrixArray.length];
        newMesh.skinvb2 = new VertexBuffer(VertexBuffer.Type.Position);
        FloatBuffer skinvfb2 = BufferUtils.clone((FloatBuffer)this.skinvb2.getData());
        newMesh.skinvb2.setupData(VertexBuffer.Usage.Dynamic, 3, VertexBuffer.Format.Float, skinvfb2);
        
//        newMesh.skinnb2 = new VertexBuffer(VertexBuffer.Type.Normal);
//        FloatBuffer skinnfb2 = BufferUtils.clone((FloatBuffer)this.skinnb2.getData());
//        newMesh.skinnb2.setupData(VertexBuffer.Usage.Static, 3, VertexBuffer.Format.Float, skinnfb2);
        
        VertexBuffer skinvb1 = new VertexBuffer(VertexBuffer.Type.Position);
//        FloatBuffer skinvfb1 = BufferUtils.clone((FloatBuffer)this.skinvb2.getData());
        FloatBuffer skinvfb1 = BufferUtils.clone((FloatBuffer)this.getBuffer(VertexBuffer.Type.Position).getData());
        skinvb1.setupData(VertexBuffer.Usage.Dynamic, 3, VertexBuffer.Format.Float, skinvfb1);
        newMesh.clearBuffer(VertexBuffer.Type.Position);
        newMesh.setBuffer(skinvb1);
        
//        VertexBuffer skinnb1 = new VertexBuffer(VertexBuffer.Type.Normal);
//        FloatBuffer skinnfb1 = BufferUtils.clone((FloatBuffer)this.skinnb2.getData());
//        FloatBuffer skinnfb1 = BufferUtils.clone((FloatBuffer)this.getBuffer(VertexBuffer.Type.Normal).getData());
//        skinnb1.setupData(VertexBuffer.Usage.Stream, 3, VertexBuffer.Format.Float, skinnfb1);
//        newMesh.clearBuffer(VertexBuffer.Type.Normal);
//        newMesh.setBuffer(skinnb1);
        FloatBuffer newBoneMatrixBuffer = BufferUtils.createFloatBuffer(boneMatrixBuffer.capacity());
        boneMatrixBuffer.position(0);
        newBoneMatrixBuffer.put(boneMatrixBuffer);
        newBoneMatrixBuffer.position(0);
        newMesh.setBoneMatrixBuffer(newBoneMatrixBuffer);
        return newMesh;
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
//        InputCapsule c = im.getCapsule(this);
//        boneIndexArray = c.readIntArray("boneIndexArray", null);
//        boneMatrixArray = new Matrix4f[boneIndexArray.length];
//        for(int i=0;i<boneMatrixArray.length;i++) {
//            boneMatrixArray[i] = new Matrix4f();
//            boneMatrixArray[i].loadIdentity();
//        }
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        
        super.write(ex);
    }
}
