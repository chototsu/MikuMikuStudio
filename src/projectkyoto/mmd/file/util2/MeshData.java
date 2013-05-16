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
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
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
//    List<PMDVertex> vertexList = new ArrayList<PMDVertex>();
    List<Integer> indexList = new ArrayList<Integer>();
//    public ByteBuffer indexBuffer;
    List<Integer> vertIndexList = new ArrayList<Integer>();
    private PMDVertex tmpVert = new PMDVertex();
    public MeshData(PMDModel model, int maxBoneSize, PMDMaterial material) {
        this.model = model;
        this.maxBoneSize = maxBoneSize;
        this.material = material;
//        indexBuffer = ByteBuffer.allocateDirect(material.getFaceVertCount() * 2);
//        indexBuffer.order(ByteOrder.nativeOrder());
    }
    public boolean addTriangle(MeshConverter mc, int i1,int i2,int i3) {
        int boneListSizeBefore = boneList.size();
        addBoneList(i1);
        addBoneList(i2);
        addBoneList(i3);
        if (boneList.size() <= maxBoneSize) {
            addVertex(mc, i1);
            addVertex(mc, i2);
            addVertex(mc, i3);
            return true;
        }
        for(int i=boneList.size();i>boneListSizeBefore;i--) {
            boneList.remove(i-1);
        }
//        System.out.println("bone remove:size = "+boneListSizeBefore+" "+boneList.size());
        return false;
    }
    private void addBoneList(int vertIndex) {
        PMDVertex v = model.getVertex(vertIndex, tmpVert);
        if (v.getBoneWeight() != 0 && !boneList.contains(v.getBoneNum1()))
            boneList.add(v.getBoneNum1());
        if (v.getBoneWeight() != 100 && !boneList.contains(v.getBoneNum2()))
            boneList.add(v.getBoneNum2());
    }
    private void addVertex(MeshConverter mc, int vertIndex) {
        int newVertIndex;
        Integer index = mc.meshTmpVertMap.get(vertIndex);
        if (index != null /*vertexList.contains(v)*/) {
            newVertIndex = index.intValue();//vertexList.indexOf(v);
        } else {
            newVertIndex = vertIndexList.size();
            vertIndexList.add(vertIndex);
            mc.meshTmpVertMap.put(vertIndex, newVertIndex);
            
//            PMDVertex v = model.getVertex(vertIndex, tmpVert);
//            mc.currentVertIndex++;
//            mc.interleavedBuffer.putFloat(v.getPos().x);
//            mc.interleavedBuffer.putFloat(v.getPos().y);
//            mc.interleavedBuffer.putFloat(v.getPos().z);
//            mc.interleavedBuffer.putFloat(v.getNormal().x);
//            mc.interleavedBuffer.putFloat(v.getNormal().y);
//            mc.interleavedBuffer.putFloat(v.getNormal().z);
//            mc.interleavedBuffer.putFloat(v.getUv().getU()).putFloat(1f - v.getUv().getV());
//            float weight = (float) v.getBoneWeight() / 100.0f;
//            mc.interleavedBuffer.putFloat(weight).putFloat(1f - weight)
//                    .putFloat(0).putFloat(0);
//            mc.interleavedBuffer.putShort((short)v.getBoneNum1()).putShort((short)v.getBoneNum2());
        }
//        indexBuffer.putShort((short)newVertIndex);
        indexList.add(newVertIndex);
    }
    ByteBuffer vfbb;
    ByteBuffer nfbb;
    ByteBuffer tfbb;
    ByteBuffer wfbb;
    ByteBuffer isbb;
    ByteBuffer bisbb;
    ByteBuffer indexBufferb;

    public FloatBuffer vfb;
    public FloatBuffer nfb;
    public FloatBuffer tfb;
    public FloatBuffer wfb;
    public ShortBuffer isb;
    public ShortBuffer bisb;
    public ShortBuffer indexBuffer;
    public int[] indexArray;
    public void write(DataOutputStream os, byte[] buf) throws IOException {
        os.writeInt(material.getMaterialNo());
        BufferUtil.write(vfbb, os, buf);
        BufferUtil.write(nfbb, os, buf);
        if (tfbb != null) {
            os.writeBoolean(true);
            BufferUtil.write(tfbb, os, buf);
        } else {
            os.writeBoolean(false);
        }
        BufferUtil.write(wfbb, os, buf);
        BufferUtil.write(isbb, os, buf);
        BufferUtil.write(bisbb, os, buf);
        BufferUtil.write(indexBufferb, os, buf);
        os.writeInt(indexArray.length);
        for(int i : indexArray) {
            os.writeInt(i);
        }
    }
    public void read(DataInputStream is, byte[] buf) throws IOException {
        material = model.getMaterial()[is.readInt()];
        vfbb = BufferUtil.read(is, buf);
        vfb = vfbb.asFloatBuffer();
        nfbb = BufferUtil.read(is, buf);
        nfb = nfbb.asFloatBuffer();
        if (is.readBoolean()) {
            tfbb = BufferUtil.read(is, buf);
            tfb = tfbb.asFloatBuffer();
        }
        wfbb = BufferUtil.read(is, buf);
        wfb = wfbb.asFloatBuffer();
        isbb = BufferUtil.read(is, buf);
        isb = isbb.asShortBuffer();
        bisbb = BufferUtil.read(is, buf);
        bisb = bisbb.asShortBuffer();
        indexBufferb = BufferUtil.read(is, buf);
        indexBuffer = indexBufferb.asShortBuffer();
        int length = is.readInt();
        indexArray = new int[length];
        for(int i=0;i<length;i++) {
            indexArray[i] = is.readInt();
        }
    }
    void createMesh() {
        boolean textureFlag = true;
        if (getMaterial().getTextureFileName().length() == 0) {
            textureFlag = false;
        }
        vfbb = BufferUtils.createByteBuffer(4 * getVertIndexList().size() * 3);
        vfb = vfbb.asFloatBuffer();
//        vfb = BufferUtils.createFloatBuffer(getVertIndexList().size() * 3);
        nfbb = BufferUtils.createByteBuffer(4 * getVertIndexList().size() * 3);
        nfb = nfbb.asFloatBuffer();
//        nfb = BufferUtils.createFloatBuffer(getVertIndexList().size() * 3);

        tfb = null;
        if (textureFlag ) {
            tfbb = BufferUtils.createByteBuffer(4 * getVertIndexList().size() * 2);
            tfb = tfbb.asFloatBuffer();
//            tfb = BufferUtils.createFloatBuffer(getVertIndexList().size() * 2);
        }
        wfbb = BufferUtils.createByteBuffer(4 * getVertIndexList().size() * 2);
        wfb = wfbb.asFloatBuffer();
//        wfb = BufferUtils.createFloatBuffer(getVertIndexList().size() * 2);
        isbb = BufferUtils.createByteBuffer(2 * getIndexList().size());
        isb = isbb.asShortBuffer();
//        isb = BufferUtils.createShortBuffer(getIndexList().size()/*md.getMaterial().getFaceVertCount()*/);
        bisbb = BufferUtils.createByteBuffer(2 * getVertIndexList().size() * 2);
        bisb = bisbb.asShortBuffer();
//        bisb = BufferUtils.createShortBuffer(getVertIndexList().size() * 2);
        PMDVertex v = new PMDVertex();
        for (Integer vertIndex : getVertIndexList()) {
            model.getVertex(vertIndex, v);
            vfb.put(v.getPos().x).put(v.getPos().y).put(v.getPos().z);
            v.getNormal().normalize();
            nfb.put(v.getNormal().x).put(v.getNormal().y).put(v.getNormal().z);

//            bvfb.put(v.getPos().x).put(v.getPos().y).put(v.getPos().z);
//            bnfb.put(v.getNormal().x).put(v.getNormal().y).put(v.getNormal().z);
            if (textureFlag) {
                float f1 = v.getUv().getU();
                float f2 = 1f-v.getUv().getV();
//                tfb.put(v.getUv().getU()).put(1f - v.getUv().getV());
//                f1 = f1 - FastMath.floor(f1);
//                f2 = f2 - FastMath.floor(f2);
//                f2 = 1 - f2;
                tfb.put(f1).put(f2);
            }
            float weight = (float) v.getBoneWeight() / 100.0f;
            wfb.put(weight).put(1f - weight);
            short b1 = (short)getBoneList().indexOf(v.getBoneNum1());
            short b2 = (short)getBoneList().indexOf(v.getBoneNum2());
            if (b1 < 0) b1 = 0;
            if (b2 < 0) b2 = 0;
            bisb.put(b1).put(b2);
//            bisb.put((short) md.getBoneList().indexOf(v.getBoneNum1())).put((short) md.getBoneList().indexOf(v.getBoneNum2()));
//            if (( weight != 0 && md.getBoneList().indexOf(v.getBoneNum1()) < 0)
//                    || (weight != 1 && md.getBoneList().indexOf(v.getBoneNum2())<0)){
//                System.out.println("ERROR!! "+v.getBoneNum1()+" "+v.getBoneNum2());
//                System.out.println(""+md.getBoneList().indexOf(v.getBoneNum1())+" "+md.getBoneList().indexOf(v.getBoneNum2()));
//                System.out.println("weight = "+weight);
//            }
        }
        for (Integer index : getIndexList()) {
            isb.put(index.shortValue());
//            System.out.println("index = "+index);
        }
//        System.out.println("isb.capacity() = " + isb.capacity());
//        System.out.println("isb.capacity() = " + md.getIndexList().size());

//        bvb.setupData(VertexBuffer.Usage.CpuOnly, 3, VertexBuffer.Format.Float, bvfb);
//        bnb.setupData(VertexBuffer.Usage.CpuOnly, 3, VertexBuffer.Format.Float, bnfb);
        indexArray = new int[getBoneList().size()];
        indexBufferb = BufferUtils.createByteBuffer(2 * getBoneList().size());
        indexBuffer = indexBufferb.asShortBuffer();
//        indexBuffer = BufferUtils.createShortBuffer(getBoneList().size());
        for (int i = 0; i < indexArray.length; i++) {
            if (i < getBoneList().size()) {
                indexArray[i] = getBoneList().get(i).shortValue();
            } else {
                indexArray[i] = 0;
            }
            indexBuffer.put((short)indexArray[i]);
        }
        boneList = null;
        vertIndexList = null;
        vertIndexList = null;
    }
    List<Integer> getBoneList() {
        return boneList;
    }

    public void setBoneList(List<Integer> boneList) {
        this.boneList = boneList;
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

//    public ByteBuffer getIndexBuffer() {
//        return indexBuffer;
//    }

    List<Integer> getVertIndexList() {
        return vertIndexList;
    }

    List<Integer> getIndexList() {
        return indexList;
    }

//    public void printTrinangles() {
//        for(int i=0;i<indexList.size();i++) {
//            PMDVertex v = vertexList.get(indexList.get(i));
//            System.out.println(v);
//        }
//    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MeshData other = (MeshData) obj;
        if (this != obj) {
            return false;
        }
        return true;
    }
}
