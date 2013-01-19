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

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.material.Material;
import com.jme3.math.Matrix4f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;
import java.io.IOException;
import java.nio.FloatBuffer;
import projectkyoto.mmd.file.PMDMaterial;

/**
 *
 * @author kobayasi
 */
public class PMDGeometry extends Geometry {

    PMDMaterial pmdMaterial;
    Material glslSkinningMaterial;
    Material noSkinningMaterial;

    public PMDGeometry(String name, Mesh mesh) {
        super(name, mesh);
    }

    public PMDGeometry(String name) {
        super(name);
    }

    public PMDGeometry() {
    }

    public PMDMaterial getPmdMaterial() {
        return pmdMaterial;
    }

    public void setPmdMaterial(PMDMaterial pmdMaterial) {
        this.pmdMaterial = pmdMaterial;
    }

    public Material getGlslSkinningMaterial() {
        return glslSkinningMaterial;
    }

    public void setGlslSkinningMaterial(Material glslSkinningMaterial) {
        this.glslSkinningMaterial = glslSkinningMaterial;
    }

    public Material getNoSkinningMaterial() {
        return noSkinningMaterial;
    }

    public void setNoSkinningMaterial(Material noSkinningMaterial) {
        this.noSkinningMaterial = noSkinningMaterial;
    }

    @Override
    public PMDGeometry clone() {
        Mesh meshBackup = mesh;
        mesh = new Mesh();
        PMDGeometry newPMDGeometry = (PMDGeometry)super.clone(true);
        mesh = meshBackup;
//        newPMDGeometry.setMesh(getMesh().clone());
        if (mesh instanceof PMDSkinMesh) {
            PMDSkinMesh oldMesh = (PMDSkinMesh)mesh;
            PMDSkinMesh newMesh = new PMDSkinMesh();
            newMesh.setBuffer(oldMesh.getBuffer(Type.Position));
            newMesh.setBuffer(oldMesh.getBuffer(Type.Normal));
            newMesh.setBuffer(oldMesh.getBuffer(Type.BoneIndex));
            newMesh.setBuffer(oldMesh.getBuffer(Type.BoneWeight));
            newMesh.boneIndexArray = oldMesh.boneIndexArray;
            newMesh.boneMatrixArray = new Matrix4f[oldMesh.boneMatrixArray.length];
            if (oldMesh.getBuffer(Type.TexCoord) != null) {
                newMesh.setBuffer(oldMesh.getBuffer(Type.TexCoord));
            }
//            for(int i=0;i<mesh.boneMatrixArray.length;i++) {
//                newMesh.boneMatrixArray[i] = new Matrix4f();
//                newMesh.boneMatrixArray[i].loadIdentity();
//            }
            newMesh.bound = oldMesh.bound.clone();
            newMesh.setBuffer(oldMesh.getBuffer(Type.Index));
            newPMDGeometry.setMesh(newMesh);

            newMesh.setBoneIndexBuffer(oldMesh.getBoneIndexBuffer());
            FloatBuffer fb = BufferUtils.createFloatBuffer(oldMesh.getBoneMatrixBuffer().capacity());
            newMesh.setBoneMatrixBuffer(fb);
        } else {
            PMDMesh oldMesh = (PMDMesh)mesh;
            PMDMesh newMesh = new PMDMesh();
            newMesh.boneIndexArray = oldMesh.boneIndexArray;
            newMesh.boneMatrixArray = new Matrix4f[oldMesh.boneMatrixArray.length];
            for (int i = 0; i < newMesh.boneMatrixArray.length; i++) {
                newMesh.boneMatrixArray[i] = new Matrix4f();
                newMesh.boneMatrixArray[i].set(oldMesh.boneMatrixArray[i]);
            }
            newMesh.setMode(Mesh.Mode.Triangles);
            newMesh.setVbBackup(oldMesh.getVbBackup());
            newMesh.setNbBackup(oldMesh.getNbBackup());
            newMesh.setBuffer(oldMesh.getVbBackup());
            newMesh.setBuffer(oldMesh.getNbBackup());
            newMesh.setBuffer(oldMesh.getBuffer(Type.Index));
            if (oldMesh.getBuffer(Type.TexCoord) != null) {
                newMesh.setBuffer(oldMesh.getBuffer(Type.TexCoord));
            }
            newMesh.setBuffer(oldMesh.getBuffer(Type.BoneIndex));
            newMesh.setBuffer(oldMesh.getBuffer(Type.BoneWeight));
            if (oldMesh.getBuffer(Type.InterleavedData) != null)
            newMesh.setBuffer(oldMesh.getBuffer(Type.InterleavedData));
            newPMDGeometry.setMesh(newMesh);
            
            newMesh.setBoneIndexBuffer(oldMesh.getBoneIndexBuffer());
            FloatBuffer fb = BufferUtils.createFloatBuffer(oldMesh.getBoneMatrixBuffer().capacity());
            newMesh.setBoneMatrixBuffer(fb);
//            newMesh.setInterleaved();
        }
        newPMDGeometry.glslSkinningMaterial = glslSkinningMaterial.clone();
        newPMDGeometry.noSkinningMaterial = noSkinningMaterial.clone();
        return newPMDGeometry;
    }

    @Override
    public PMDGeometry clone(boolean cloneMaterial) {
        return clone();
    }

//    @Override
//    public void setMesh(Mesh mesh) {
//        super.setMesh(mesh);
//        if (mesh instanceof PMDMesh) {
//            pmdMesh = (PMDMesh)mesh;
//        }
//    }

    @Override
    public void setMaterial(Material material) {
        super.setMaterial(material);
        if (mesh instanceof PMDMesh) {
            PMDMesh pmdMesh = (PMDMesh)mesh;
            pmdMesh.boneMatricesParamIndex = -1;
        }
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule c = im.getCapsule(this);
        glslSkinningMaterial = (Material)c.readSavable("glslSkinningMaterial", null);
        noSkinningMaterial = (Material)c.readSavable("noSkinningMaterial", null);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule c = ex.getCapsule(this);
        c.write(glslSkinningMaterial, "glslSkinningMaterial", null);
        c.write(noSkinningMaterial, "noSkinningMaterial", null);
    }
    
}
