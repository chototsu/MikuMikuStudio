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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.vecmath.Point3f;
import projectkyoto.mmd.file.PMDMaterial;
import projectkyoto.mmd.file.PMDModel;
import projectkyoto.mmd.file.PMDVertex;

/**
 *
 * @author kobayasi
 */
public class MeshData {
    PMDModel model;
    int maxBoneSize;
    PMDMaterial material;
    List<Integer>boneList = new ArrayList<Integer>();
    List<PMDVertex> vertexList = new ArrayList<PMDVertex>();
    List<Integer> indexList = new ArrayList<Integer>();
    public MeshData(PMDModel model, int maxBoneSize, PMDMaterial material) {
        this.model = model;
        this.maxBoneSize = maxBoneSize;
        this.material = material;
    }
    public boolean addTriangle(int i1,int i2,int i3) {
        int boneListSizeBefore = boneList.size();
        addBoneList(i1);
        addBoneList(i2);
        addBoneList(i3);
        if (boneList.size() <= maxBoneSize) {
            addVertex(i1);
            addVertex(i2);
            addVertex(i3);
            return true;
        }
        for(int i=boneList.size();i>boneListSizeBefore;i--) {
            boneList.remove(i-1);
        }
//        System.out.println("bone remove:size = "+boneListSizeBefore+" "+boneList.size());
        return false;
    }
    private void addBoneList(int vertIndex) {
        PMDVertex v = model.getVertexList()[vertIndex];
        if (!boneList.contains(v.getBoneNum1()))
            boneList.add(v.getBoneNum1());
        if (!boneList.contains(v.getBoneNum2()))
            boneList.add(v.getBoneNum2());
    }
    private void addVertex(int vertIndex) {
        PMDVertex v = model.getVertexList()[vertIndex];
        int newVertIndex;
        if (vertexList.contains(v)) {
            newVertIndex = vertexList.indexOf(v);
        } else {
            newVertIndex = vertexList.size();
            vertexList.add(v);
        }
        indexList.add(newVertIndex);
    }
    public List<Integer> getBoneList() {
        return boneList;
    }

    public void setBoneList(List<Integer> boneList) {
        this.boneList = boneList;
    }

    public List<Integer> getIndexList() {
        return indexList;
    }

    public void setIndexList(List<Integer> indexList) {
        this.indexList = indexList;
    }

    public PMDMaterial getMaterial() {
        return material;
    }

    public void setMaterial(PMDMaterial material) {
        this.material = material;
    }

    public int getMaxBoneSize() {
        return maxBoneSize;
    }

    public void setMaxBoneSize(int maxBoneSize) {
        this.maxBoneSize = maxBoneSize;
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
    public void printTrinangles() {
        for(int i=0;i<indexList.size();i++) {
            PMDVertex v = vertexList.get(indexList.get(i));
            System.out.println(v);
        }
    }
}
