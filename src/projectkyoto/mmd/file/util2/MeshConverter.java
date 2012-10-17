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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import projectkyoto.mmd.file.PMDMaterial;
import projectkyoto.mmd.file.PMDModel;
import projectkyoto.mmd.file.PMDSkinData;
import projectkyoto.mmd.file.PMDSkinVertData;
import projectkyoto.mmd.file.PMDVertex;
import projectkyoto.mmd.file.pmn.PMNData;
import projectkyoto.mmd.file.pmn.PMNMesh;
import projectkyoto.mmd.file.pmn.PMNSkinMesh;

/**
 *
 * @author kobayasi
 */
public class MeshConverter implements Serializable{

    PMDModel model;
    public static int DEFAULT_MAX_BONE_SIZE = 20;
    int maxBoneSize = DEFAULT_MAX_BONE_SIZE;
    List<MeshData> meshDataList = new ArrayList<MeshData>();
    SkinMeshData skinMeshData;
    HashMap<Integer, Integer> meshTmpVertMap = new HashMap<Integer, Integer>();
    HashMap<Integer, Integer> skinTmpVertMap = new HashMap<Integer, Integer>();
    public ByteBuffer interleavedBuffer;
    int currentVertIndex = 0;
    PMNData pmnData;
    public MeshConverter() {
        
    }
    public MeshConverter(PMDModel model) {
        this.model = model;
        skinMeshData = new SkinMeshData(this, model);
        initSkinVertSet();
//        removeUnusedSkinVertex();
    }
    private final void initSkinVertSet() {
        for(int skinCount = 0;skinCount<model.getSkinCount();skinCount++) {
            PMDSkinData skinData = model.getSkinData()[skinCount];
            if (skinData.getSkinType() == 0) {
                for(int skinVertCount = 0;skinVertCount<skinData.getSkinVertCount();skinVertCount++) {
                    VertIndex vi = new VertIndex(skinData.getIndexBuf().get(skinVertCount));
                    skinVertSet.add(vi);
                }
                break;
            }
        }
    }

    public void checkDupMaterial() {
        for(int i1 = 0;i1<model.getMaterialCount();i1++) {
            for(int i2=i1+1;i2<model.getMaterialCount();i2++) {
                if (model.getMaterial()[i1].equals(model.getMaterial()[i2])) {
//                    System.out.println("dup material");
                    break;
                }
            }
        }
    }
    void printFaceVertSize() {
        for(int skinCount = 0;skinCount<model.getSkinCount();skinCount++) {
            PMDSkinData skinData = model.getSkinData()[skinCount];
        }
    }
    public void convertMesh() {
        int faceVertNo = 0;
        for(int materialNo = 0; materialNo < model.getMaterialCount(); materialNo++) {
            meshTmpVertMap.clear();
            PMDMaterial material = model.getMaterial()[materialNo];
            // find same material
            MeshData meshData = new MeshData(model, maxBoneSize, material);
            for(int meshIndex = meshDataList.size()-1;meshIndex >=0;meshIndex--) {
                PMDMaterial material2 = meshDataList.get(meshIndex).getMaterial();
                if (material.equals(material2)) {
                    meshData = meshDataList.get(meshIndex);
                    for(int i=meshData.getVertIndexList().size()-1;i>=0;i--) {
                        Integer vertIndex = meshData.getVertIndexList().get(i);
                        meshTmpVertMap.put(vertIndex, i);
                    }
                    break;
                }
            }
            if (material.getFaceVertCount() == 0) {
                continue;
            }
            if (!meshDataList.contains(meshData)) {
                meshDataList.add(meshData);
            }
            for (int materialFaceVertNo = 0; materialFaceVertNo < material.getFaceVertCount(); materialFaceVertNo += 3) {
                int i1 = model.getFaceVertIndex()[faceVertNo++];
                int i2 = model.getFaceVertIndex()[faceVertNo++];
                int i3 = model.getFaceVertIndex()[faceVertNo++];
                if (containsSkin(i1, i2, i3)) {
                    addSkinTriangle(material, i1, i2, i3);
                } else {
                    if (!meshData.addTriangle(this, i1, i2, i3)) {
                        meshData = new MeshData(model, maxBoneSize, material);
                        meshTmpVertMap.clear();
                        meshDataList.add(meshData);
                        meshData.addTriangle(this, i1, i2, i3);
                    }
                }
            }
            if (meshData.material.getFaceVertCount() == 0) {
                meshDataList.remove(meshDataList.size()-1);
            }
        }
//        meshTmpVertMap = null;
        skinTmpVertMap = null;
//        createInterleavedBuffer();
        Iterator<MeshData> it = meshDataList.iterator();
        while(it.hasNext()) {
            MeshData md = it.next();
            if (md.getIndexList().size() == 0) {
                it.remove();
            } else {
                md.createMesh();
            }
        }
        skinMeshData.createSkinCommonVertData();
        meshTmpVertMap = null;
        skinTmpVertMap = null;
    }
    void removeUnusedSkinVertex() {
        HashSet<Integer> tmpSet = new HashSet<Integer>();
        PMDSkinData skinData0 = null;
        for(PMDSkinData skinData : model.getSkinData()) {
            if (skinData.getSkinType() == 0) {
                skinData0 = skinData;
                continue;
            }
//            for(PMDSkinVertData svd : skinData.getSkinVertData()) {
//                tmpSet.add(svd.getSkinVertIndex());
//            }
        }
        Iterator<VertIndex> it = skinVertSet.iterator();
        while(it.hasNext()) {
            VertIndex vi = it.next();
            if (!tmpSet.contains(vi.index)) {
                it.remove();
            }
        }
    }
    void printMeshData(MeshData meshData) {
//            System.out.println("vertSize = " + meshData.getVertexList().size()
//                    + " indexSize = " + meshData.getIndexList().size()
//                    + " boneSize = " + meshData.getBoneList().size());
    }
    boolean containsSkin(int i1, int i2, int i3) {
        if (containsSkin(i1)
                || containsSkin(i2)
                || containsSkin(i3)) {
            return true;
        }
        return false;
    }
    VertIndex tmpvi = new VertIndex(0);
    boolean containsSkin(int i) {
        tmpvi.index = i;
        return skinVertSet.contains(tmpvi);
    }
//    boolean _containsSkin(int i) {
//        for(int skinCount = 0;skinCount<model.getSkinCount();skinCount++) {
//            PMDSkinData skinData = model.getSkinData()[skinCount];
//            if (skinData.getSkinType() == 0) {
//                for(int skinVertCount = 0;skinVertCount<skinData.getSkinVertCount();skinVertCount++) {
//                    if (i == skinData.getSkinVertData()[skinVertCount].getSkinVertIndex()) {
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//    }
    Set<VertIndex> skinVertSet = new java.util.HashSet<VertIndex> ();
    
    void addSkinTriangle(PMDMaterial material, int i1,int i2,int i3) {
        skinMeshData.addTriangle(this, material, i1, i2, i3);
    }
    public PMNData createPMNData() {
        pmnData = new PMNData();
        PMNMesh meshArrah[] = new PMNMesh[meshDataList.size()];
        for(int i=0;i<meshDataList.size();i++) {
            MeshData md = meshDataList.get(i);
            PMNMesh pmnMesh = new PMNMesh();
            createInterleavedBuffer(md, pmnMesh);
            
        }
        PMNSkinMesh pmnSkinMesh = new PMNSkinMesh();
        ByteBuffer pointBuffer = ByteBuffer.allocateDirect(12 * skinMeshData.vertexList.size());
        pointBuffer.order(ByteOrder.nativeOrder());
        pmnSkinMesh.setPointBuffer(pointBuffer.asFloatBuffer());
        
        ByteBuffer normalBuffer = ByteBuffer.allocateDirect(12 * skinMeshData.vertexList.size());
        normalBuffer.order(ByteOrder.nativeOrder());
        pmnSkinMesh.setNormalBuffer(normalBuffer.asFloatBuffer());
        
        ByteBuffer texCoordBuffer = ByteBuffer.allocateDirect(8 * skinMeshData.vertexList.size());
        texCoordBuffer.order(ByteOrder.nativeOrder());
        pmnSkinMesh.setTexCoordBuffer(texCoordBuffer.asFloatBuffer());
        
        ByteBuffer boneIndexBuffer = ByteBuffer.allocateDirect(4 * skinMeshData.vertexList.size());
        boneIndexBuffer.order(ByteOrder.nativeOrder());
        pmnSkinMesh.setBoneIndexBuffer(boneIndexBuffer.asShortBuffer());

        ByteBuffer boneWeightBuffer = ByteBuffer.allocateDirect(8 * skinMeshData.vertexList.size());
        boneWeightBuffer.order(ByteOrder.nativeOrder());
        pmnSkinMesh.setBoneWeightBuffer(boneWeightBuffer.asFloatBuffer());
        for(PMDVertex v : skinMeshData.vertexList) {
            pointBuffer.putFloat(v.getPos().x)
                    .putFloat(v.getPos().y)
                    .putFloat(v.getPos().z);
            normalBuffer.putFloat(v.getNormal().x)
                    .putFloat(v.getNormal().y)
                    .putFloat(v.getNormal().z);
            texCoordBuffer.putFloat(v.getUv().getU())
                    .putFloat(v.getUv().getV());
            short boneIndex = (short)skinMeshData.boneList.indexOf(v.getBoneNum1());
            if (boneIndex < 0) {
                boneIndex = 0;
            }
            boneIndexBuffer.putShort(boneIndex);
            boneIndex = (short)skinMeshData.boneList.indexOf(v.getBoneNum2());
            if (boneIndex < 0) {
                boneIndex = 0;
            }
            boneIndexBuffer.putShort(boneIndex);
            float weight = (float)v.getBoneWeight() / 100f;
            boneWeightBuffer.putFloat(weight).putFloat(1f - weight);
        }
        pmnSkinMesh.setIndexBufferArray(new ShortBuffer[skinMeshData.indexMap.size()]);
        pmnSkinMesh.setMaterialIndexArray(new int[skinMeshData.indexMap.size()]);
        int i=0;
        for(PMDMaterial m : skinMeshData.indexMap.keySet()) {
            List<Integer>indexList = skinMeshData.indexMap.get(m);
            ByteBuffer indexBuffer = ByteBuffer.allocateDirect(2 * indexList.size());
            indexBuffer.order(ByteOrder.nativeOrder());
            for(Integer index : indexList) {
                indexBuffer.putShort(index.shortValue());
            }
            pmnSkinMesh.getIndexBufferArray()[i] = indexBuffer.asShortBuffer();
            for(int mi=0;mi<model.getMaterialCount();mi++) {
                if (model.getMaterial()[mi] == m) {
                    pmnSkinMesh.getMaterialIndexArray()[i] = mi;
                    break;
                }
            }
            i++;
        }
        
        return pmnData;
    }
    public void createInterleavedBuffer(MeshData md, PMNMesh pmnMesh) {
        byte offset = 0;
        byte[] offsetArray = new byte[5];
        pmnMesh.setOffsetArray(offsetArray);
        short[] boneIndexArray = new short[md.boneList.size()];
        pmnMesh.setBoneIndexArray(boneIndexArray);
        // point
        offsetArray[0] = offset;
        offset += 12;
        // normal
        offsetArray[1] = offset;
        offset += 12;
        if (md.material.getTextureFileName().length() >= 0) {
            // texcoord
            offsetArray[2] = offset;
            offset += 8;
        } else {
            offsetArray[2] = -1;
        }
        // boneIndex
        offsetArray[3] = offset;
        offset += 4;
        //boneWeight
        offsetArray[4] = offset;
        offset += 8;
        
        pmnMesh.setStride(offset);
        ByteBuffer interleavedBuffer = ByteBuffer.allocateDirect(offset * md.vertIndexList.size());
        interleavedBuffer.order(ByteOrder.nativeOrder());
        pmnMesh.setInterleavedBuffer(interleavedBuffer);
        PMDVertex v = new PMDVertex();
        for(Integer vertIndex : md.getVertIndexList()) {
            model.getVertex(vertIndex, v);
            interleavedBuffer.putFloat(v.getPos().x)
                    .putFloat(v.getPos().y)
                    .putFloat(v.getPos().z);
            interleavedBuffer.putFloat(v.getNormal().x)
                    .putFloat(v.getNormal().y)
                    .putFloat(v.getNormal().z);
            if (offsetArray[2] >= 0) {
                interleavedBuffer.putFloat(v.getUv().getU())
                        .putFloat(v.getUv().getV());
            }
            short boneIndex = (short)md.boneList.indexOf(v.getBoneNum1());
            if (boneIndex < 0) {
                boneIndex = 0;
            }
            interleavedBuffer.putShort(boneIndex);
            boneIndex = (short)md.boneList.indexOf(v.getBoneNum2());
            if (boneIndex < 0) {
                boneIndex = 0;
            }
            interleavedBuffer.putShort(boneIndex);
            float weight = (float)v.getBoneWeight() / 100f;
            interleavedBuffer.putFloat(weight).putFloat(1f - weight);
        }
        ByteBuffer indexBuffer = ByteBuffer.allocateDirect(2 * md.getIndexList().size());
        indexBuffer.order(ByteOrder.nativeOrder());
        for(Integer index : md.getIndexList()) {
            indexBuffer.putShort(index.shortValue());
        }
        pmnMesh.setIndexBuffer(indexBuffer.asShortBuffer());
        for(int i=0;i<model.getMaterialCount();i++) {
            if (model.getMaterial()[i] == md.getMaterial()) {
                pmnMesh.setMaterialIndex(model.getFaceVertCount());
                break;
            }
        }
        for(int i=0;i<boneIndexArray.length;i++) {
            boneIndexArray[i] = md.boneList.get(i).shortValue();
        }
    }
    public int calcPMNSize() {
        int size = 16;
        for(MeshData md : meshDataList) {
            int stride;
            if (md.material.getTextureFileName().length() >= 0) {
                stride = 44;
            } else {
                stride = 36;
            }
            size += stride * md.getVertIndexList().size();
            size += 2 * md.getIndexList().size();
        }
        return size;
    }
    public void write(OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        byte[] buf = new byte[1024 * 16];
        dos.writeInt(meshDataList.size());
        for(int i=0;i<meshDataList.size();i++) {
            MeshData md = meshDataList.get(i);
            md.write(dos, buf);
        }
        skinMeshData.write(dos, buf);
        dos.flush();
    }
    public void read(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        byte[] buf = new byte[1024 * 16];
        meshDataList = new ArrayList<MeshData>();
        int meshDataSize = dis.readInt();
        for(int i=0;i<meshDataSize;i++) {
            MeshData md = new MeshData(model, maxBoneSize, null);
            md.read(dis, buf);
            meshDataList.add(md);
        }
        skinMeshData = new SkinMeshData();
        skinMeshData.model = model;
        skinMeshData.read(dis, buf);
    }
    public int getMaxBoneSize() {
        return maxBoneSize;
    }

    public void setMaxBoneSize(int maxBoneSize) {
        this.maxBoneSize = maxBoneSize;
    }

    public List<MeshData> getMeshDataList() {
        return meshDataList;
    }

    public void setMeshDataList(List<MeshData> meshDataList) {
        this.meshDataList = meshDataList;
    }

    public PMDModel getModel() {
        return model;
    }

    public void setModel(PMDModel model) {
        this.model = model;
    }

    public SkinMeshData getSkinMeshData() {
        return skinMeshData;
    }

    public void setSkinMeshData(SkinMeshData skinMeshData) {
        this.skinMeshData = skinMeshData;
    }

    public PMNData getPmnData() {
        return pmnData;
    }
    
    
}
class VertIndex implements Serializable{
    int index;

    public VertIndex(int index) {
        this.index = index & 0xffff;
    }

    @Override
    public boolean equals(Object obj) {
//        if (obj == null) {
//            return false;
//        }
//        if (getClass() != obj.getClass()) {
//            return false;
//        }
        final VertIndex other = (VertIndex) obj;
        if (this.index != other.index) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this.index;
        return hash;
    }
}
