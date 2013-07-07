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

import com.jme3.math.FastMath;
import com.jme3.util.BufferUtils;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
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
public class SkinMeshData implements Serializable {

    PMDModel model;
    List<Integer> boneList = new ArrayList<Integer>();
    List<PMDVertex> vertexList = new ArrayList<PMDVertex>();
    Map<PMDMaterial, List<Integer>> indexMap = new HashMap<PMDMaterial, List<Integer>>();
    public SkinMeshData() {
        
    }
    public SkinMeshData(MeshConverter mc, PMDModel model) {
        this.model = model;
        if (model.getVertexBuffer() != null)
        for (PMDSkinData sd : model.getSkinData()) {
            if (sd.getSkinType() == 0) {
                for (int i = 0; i < sd.getSkinVertCount(); i++) {
                    int skinVertIndex = sd.getIndexBuf().get(i) & 0xffff;
                    try {
                        PMDVertex v = model.getVertex(skinVertIndex);
                        vertexList.add(v);
                        mc.skinTmpVertMap.put(skinVertIndex, i);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
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
        addVertex(mc, indexList, i1);
        addVertex(mc, indexList, i2);
        addVertex(mc, indexList, i3);
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

    private void addVertex(MeshConverter mc, List<Integer> indexList, int vertIndex) {
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
    public ByteBuffer skinvfbb;
    public ByteBuffer skinnfbb;
    public ByteBuffer skintfbb;
    public ByteBuffer skinbisbb;
    public ByteBuffer wfbb;
    public FloatBuffer skinvfb;
    public FloatBuffer skinvfb2;
    public FloatBuffer skinnfb;
    public FloatBuffer skintfb;
    public ShortBuffer skinbisb;
    public FloatBuffer wfb;
    public int[] skinIndexArray;
    public Map<PMDMaterial, ShortBuffer> indexShortBufferMap = new HashMap<PMDMaterial, ShortBuffer>();

    public void write(DataOutputStream os, byte[] buf) throws IOException {
        BufferUtil.write(skinvfbb, os, buf);
        BufferUtil.write(skinnfbb, os, buf);
        if (skintfbb != null) {
            os.writeBoolean(true);
            BufferUtil.write(skintfbb, os, buf);
        } else {
            os.writeBoolean(false);
        }
        BufferUtil.write(wfbb, os, buf);
        BufferUtil.write(skinbisbb, os, buf);
        os.writeInt(skinIndexArray.length);
        for (int i : skinIndexArray) {
            os.writeInt(i);
        }
        os.writeInt(indexShortBufferMap.size());
        for (PMDMaterial mat : indexShortBufferMap.keySet()) {
            os.writeInt(mat.getMaterialNo());
            ShortBuffer sb = indexShortBufferMap.get(mat);
            os.writeInt(sb.capacity() * 2);
            sb.position(0);
            if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
                for (int i = 0; i < sb.capacity(); i++) {
                    short s = sb.get();
                    os.writeByte(s);
                    os.writeByte(s >> 8);
                }
            } else {
                for (int i = 0; i < sb.capacity(); i++) {
                    short s = sb.get();
                    os.writeByte(s >> 8);
                    os.writeByte(s);
                }
            }
        }
    }

    public void read(DataInputStream is, byte[] buf) throws IOException {
        skinvfbb = BufferUtil.read(is, buf);
        skinvfb = skinvfbb.asFloatBuffer();
        skinnfbb = BufferUtil.read(is, buf);
        skinnfb = skinnfbb.asFloatBuffer();
        if (is.readBoolean()) {
            skintfbb = BufferUtil.read(is, buf);
            skintfb = skintfbb.asFloatBuffer();
        }
        wfbb = BufferUtil.read(is, buf);
        wfb = wfbb.asFloatBuffer();
        skinbisbb = BufferUtil.read(is, buf);
        skinbisb = skinbisbb.asShortBuffer();
        int length = is.readInt();
        skinIndexArray = new int[length];
        for (int i = 0; i < length; i++) {
            skinIndexArray[i] = is.readInt();
        }
        int size = is.readInt();
        indexShortBufferMap = new HashMap<PMDMaterial, ShortBuffer>();
        for(int i=0;i<size;i++) {
            PMDMaterial mat = model.getMaterial()[is.readInt()];
            ShortBuffer sb = BufferUtil.read(is, buf).asShortBuffer();
            indexShortBufferMap.put(mat, sb);
        }
        skinvfb2 = BufferUtils.createFloatBuffer(skinvfb.capacity());
        skinvfb.position(0);
        skinvfb2.put(skinvfb);
    }

    void createSkinCommonVertData() {
        skinvfbb = BufferUtils.createByteBuffer(getVertexList().size() * 3 * 4);
        skinvfb = skinvfbb.asFloatBuffer();

        skinvfb2 = BufferUtils.createFloatBuffer(getVertexList().size() * 3);

        skinnfbb = BufferUtils.createByteBuffer(getVertexList().size() * 3 * 4);
        skinnfb = skinnfbb.asFloatBuffer();

        skintfbb = BufferUtils.createByteBuffer(getVertexList().size() * 2 * 4);
        skintfb = skintfbb.asFloatBuffer();

        skinbisbb = BufferUtils.createByteBuffer(getVertexList().size() * 2 * 2);
        skinbisb = skinbisbb.asShortBuffer();

        wfbb = BufferUtils.createByteBuffer(getVertexList().size() * 2 * 4);
        wfb = wfbb.asFloatBuffer();

        for (PMDVertex v : getVertexList()) {
            skinvfb.put(v.getPos().x).put(v.getPos().y).put(v.getPos().z);
            v.getNormal().normalize();
            skinnfb.put(v.getNormal().x).put(v.getNormal().y).put(v.getNormal().z);
//            float f1 = v.getUv().getU();
//            float f2 = v.getUv().getV();
//                tfb.put(v.getUv().getU()).put(1f - v.getUv().getV());
//            f1 = f1 - FastMath.floor(f1);
//            f2 = f2 - FastMath.floor(f2);
//            f2 = 1 - f2;
//            skintfb.put(f1).put(f2);
            skintfb.put(v.getUv().getU()).put(1f - v.getUv().getV());
//            skinbisb.put((short) meshConverter.getSkinMeshData()
//                    .getBoneList().indexOf(v.getBoneNum1()))
//                    .put((short) meshConverter.getSkinMeshData()
//                    .getBoneList().indexOf(v.getBoneNum2()));
            short b1 = (short) getBoneList().indexOf(v.getBoneNum1());
            short b2 = (short) getBoneList().indexOf(v.getBoneNum2());
            if (b1 < 0) {
                b1 = 0;
            }
            if (b2 < 0) {
                b2 = 0;
            }
            skinbisb.put(b1).put(b2);
            float weight = (float) v.getBoneWeight() / 100.0f;
            wfb.put(weight).put(1f - weight);
        }
        skinvfb.position(0);
        skinvfb2.position(0);
        skinvfb2.put(skinvfb);
        skinnfb.position(0);
//        skinnfb2.position(0);
//        skinnfb2.put(skinnfb);
        skinIndexArray = new int[getBoneList().size()];
        for (int i = 0; i < skinIndexArray.length; i++) {
            if (i < getBoneList().size()) {
                skinIndexArray[i] = getBoneList().get(i).shortValue();
            } else {
                skinIndexArray[i] = 0;
            }
        }
        for (PMDMaterial key : indexMap.keySet()) {
            List<Integer> indexList = indexMap.get(key);
            ShortBuffer isb = BufferUtils.createShortBuffer(indexList.size());
            for (Integer index : indexList) {
                isb.put(index.shortValue());
            }
            indexShortBufferMap.put(key, isb);
        }
        indexMap = null;
        boneList = null;
        vertexList = null;
    }

    List<Integer> getBoneList() {
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

    Map<PMDMaterial, List<Integer>> getIndexMap() {
        return indexMap;
    }

    public void setIndexMap(Map<PMDMaterial, List<Integer>> indexMap) {
        this.indexMap = indexMap;
    }
}
