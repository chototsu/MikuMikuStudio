/*
 * Copyright (c) 2011 Kazuhiko Kobayashi
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'MMDLoaderJME' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package projectkyoto.mmd.file.util2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import projectkyoto.mmd.file.PMDMaterial;
import projectkyoto.mmd.file.PMDModel;
import projectkyoto.mmd.file.PMDSkinData;
import projectkyoto.mmd.file.PMDVertex;

/**
 *
 * @author kobayasi
 */
public class SkinMeshData implements Serializable{

    PMDModel model;
    List<Integer> boneList = new ArrayList<Integer>();
    List<PMDVertex> vertexList = new ArrayList<PMDVertex>();
    Map<PMDMaterial, List<Integer>> indexMap = new HashMap<PMDMaterial, List<Integer>>();

    public SkinMeshData(MeshConverter mc, PMDModel model) {
        this.model = model;
        for(PMDSkinData sd : model.getSkinData()) {
            if (sd.getSkinType() == 0) {
                for(int i=0;i<sd.getSkinVertCount();i++) {
                    int skinVertIndex = sd.getIndexBuf().get(i);
                    PMDVertex v = model.getVertex(skinVertIndex);
                    vertexList.add(v);
                    mc.skinTmpVertMap.put(skinVertIndex, i);
                }
                break;
            }
        }
    }

    public void addTriangle(MeshConverter mc, PMDMaterial material, int i1, int i2, int i3) {
        addBoneList(i1);
        addBoneList(i2);
        addBoneList(i3);
        List<Integer> indexList = indexMap.get(material);
        if (indexList == null) {
            indexList = new ArrayList<Integer>();
            indexMap.put(material, indexList);
        }
        addVertex(mc, indexList,i1);
        addVertex(mc, indexList,i2);
        addVertex(mc, indexList,i3);
    }

    private void addBoneList(int vertIndex) {
        PMDVertex v = model.getVertex(vertIndex);
        if (!boneList.contains(v.getBoneNum1())) {
            boneList.add(v.getBoneNum1());
        }
        if (!boneList.contains(v.getBoneNum2())) {
            boneList.add(v.getBoneNum2());
        }
    }

    private void addVertex(MeshConverter mc, List<Integer>indexList, int vertIndex) {
        PMDVertex v = model.getVertex(vertIndex);
        Integer index = mc.skinTmpVertMap.get(vertIndex);
        int newVertIndex;
        if (index != null /*vertexList.contains(v)*/) {
            newVertIndex = index.intValue(); //vertexList.indexOf(v);
        } else {
            newVertIndex = vertexList.size();
            vertexList.add(v);
            mc.skinTmpVertMap.put(vertIndex, newVertIndex);
            index = newVertIndex;
        }
        indexList.add(index/*newVertIndex*/);
    }

    public List<Integer> getBoneList() {
        return boneList;
    }

    public void setBoneList(List<Integer> boneList) {
        this.boneList = boneList;
    }


    public PMDModel getModel() {
        return model;
    }

    public void setModel(PMDModel model) {
        this.model = model;
    }

    public List<PMDVertex> getVertexList() {
        return vertexList;
    }

    public void setVertexList(List<PMDVertex> vertexList) {
        this.vertexList = vertexList;
    }

    public Map<PMDMaterial, List<Integer>> getIndexMap() {
        return indexMap;
    }

    public void setIndexMap(Map<PMDMaterial, List<Integer>> indexMap) {
        this.indexMap = indexMap;
    }
}
