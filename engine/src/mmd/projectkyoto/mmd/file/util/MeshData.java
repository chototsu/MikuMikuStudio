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

package projectkyoto.mmd.file.util;

import projectkyoto.mmd.file.*;
import java.util.*;

/**
 *
 * @author kobayasi
 */
public class MeshData {

    MeshKey meshKey;
    PMDModel model;
    List<Integer> indexList = new ArrayList<Integer>();
    List<PMDVertex> vertexList = new ArrayList<PMDVertex>();

//    void addIndex(int origIndex) {
//        PMDVertex v = model.getVertexList()[origIndex];
//        for (int newIndex = 0; newIndex < vertexList.size(); newIndex++) {
//            if (vertexList.get(newIndex) == v) {
//                indexList.add(newIndex);
//                return;
//            }
//        }
//        indexList.add(vertexList.size());
//        vertexList.add(v);
//    }

    public MeshData(MeshKey meshKey, PMDModel model) {
        this.meshKey = meshKey;
        this.model = model;
    }

    public List<Integer> getIndexList() {
        return indexList;
    }

    public void setIndexList(List<Integer> indexList) {
        this.indexList = indexList;
    }

    public MeshKey getMeshKey() {
        return meshKey;
    }

    public void setMeshKey(MeshKey meshKey) {
        this.meshKey = meshKey;
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
    
}
