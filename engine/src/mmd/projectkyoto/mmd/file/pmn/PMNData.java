/*
 * Copyright (c) 2010-2014, Kazuhiko Kobayashi
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package projectkyoto.mmd.file.pmn;

import projectkyoto.mmd.file.PMDModel;

/**
 *
 * @author kobayasi
 */
public class PMNData {

    PMDModel pmdModel;
    PMNMesh meshArray[];
    PMNSkinMesh skinMesh;

    public PMNData() {
    }

    public PMNMesh[] getMeshArray() {
        return meshArray;
    }

    public void setMeshArray(PMNMesh[] meshArray) {
        this.meshArray = meshArray;
    }

    public PMDModel getPmdModel() {
        return pmdModel;
    }

    public void setPmdModel(PMDModel pmdModel) {
        this.pmdModel = pmdModel;
    }

    public PMNSkinMesh getSkinMesh() {
        return skinMesh;
    }

    public void setSkinMesh(PMNSkinMesh skinMesh) {
        this.skinMesh = skinMesh;
    }
}
