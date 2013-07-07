/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
