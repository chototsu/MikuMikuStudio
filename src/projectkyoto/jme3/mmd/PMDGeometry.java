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

import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import projectkyoto.mmd.file.PMDMaterial;

/**
 *
 * @author kobayasi
 */
public class PMDGeometry extends Geometry {

    PMDMaterial pmdMaterial;
    Material glslSkinningMaterial;
    Material noSkinningMaterial;
    PMDMesh pmdMesh;

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
        PMDGeometry newPMDGeometry = (PMDGeometry)super.clone(false);
        newPMDGeometry.setMesh(getMesh().clone());
        newPMDGeometry.glslSkinningMaterial = glslSkinningMaterial.clone();
        newPMDGeometry.noSkinningMaterial = noSkinningMaterial.clone();
        return newPMDGeometry;
    }

    @Override
    public PMDGeometry clone(boolean cloneMaterial) {
        return clone();
    }

    @Override
    public void setMesh(Mesh mesh) {
        super.setMesh(mesh);
        if (mesh instanceof PMDMesh) {
            pmdMesh = (PMDMesh)mesh;
        }
    }
    
}
