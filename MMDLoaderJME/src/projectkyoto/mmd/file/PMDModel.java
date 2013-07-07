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
package projectkyoto.mmd.file;

import java.io.BufferedInputStream;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel.MapMode;
import projectkyoto.mmd.file.util2.BufferUtil;

/**
 *
 * @author Kazuhiko Kobayashi
 */
public class PMDModel implements Serializable{
    // PMD Header

    private String id; // char[3] "Pmd"
    private float version; // float 0x00 0x00 0x80 0x3F == 1.00;
    private String modelName; // char[20] モデル名
    private String comment; // char[256] コメント
    // 頂点リスト
    private int vertCount; // 頂点数
//    private PMDVertex[] vertexList;
    private ByteBuffer vertexBuffer;
    private int faceVertCount;
    private int faceVertIndex[];
    private int materialCount;
    private PMDMaterial[] material;
    private PMDBoneList boneList;
    private PMDIKList ikList;
    private int skinCount;
    private PMDSkinData skinData[];
    private PMDSkinDispList skinDispList;
    private PMDBoneDispNameList boneDispNameList;
    private PMDBoneDispList boneDispList;
    private PMDHeaderEnglish headerEnglish;
    private PMDToonTextureList toonTextureList;
    private PMDRigidBodyList rigidBodyList;
    private PMDJointList jointList;

    public PMDModel() {
    }

    public PMDModel(URL url) throws IOException {
        readFromFile(url.openStream());
    }
    public PMDModel(InputStream is) throws IOException {
        readFromFile(is);
    }
    public void readFromFile(InputStream is) throws IOException {
        DataInputStreamLittleEndian dis = null;
        try {
//            is = new DataInputStreamLittleEndian(new BufferedInputStream(
//                    new FileInputStream(
//                    file)));
            dis = new DataInputStreamLittleEndian(new BufferedInputStream(is));
            readFromStream(dis);
            dis.close();
//            this.url = url;
        } finally {
            if (dis != null) {
                dis.close();
                dis = null;
            }
        }
//        System.out.println(toString());
    }

    public void readFromStream(DataInputStreamLittleEndian is) throws
            IOException {
        readFromStream(is, false);
    }
    public void readFromStream(DataInputStreamLittleEndian is, boolean skipVertFlag) throws
            IOException {
        id = is.readString(3);
        if (!"Pmd".equals(id)) {
            throw new InvalidPMDFileException("Invalid ID:" + id);
        }
        version = is.readFloat();
        modelName = is.readString(20);
        comment = is.readString(256);
        vertCount = is.readInt();
//        vertexList = new PMDVertex[vertCount];
//        vertexBuffer = ByteBuffer.allocateDirect(PMDVertex.size() * vertCount);
        if (skipVertFlag) {
//            vertexBuffer = BufferUtil.createByteBuffer(PMDVertex.size() * vertCount);
//            vertexBuffer.order(ByteOrder.nativeOrder());
//            PMDVertex tmpVertex = new PMDVertex();
//            for (int i = 0; i < vertCount; i++) {
//                tmpVertex.readFromStream(is);
////                tmpVertex.writeToBuffer(vertexBuffer);
//
//            }
            is.skip(38 * vertCount);
        } else {
            vertexBuffer = BufferUtil.createByteBuffer(PMDVertex.size() * vertCount);
            vertexBuffer.order(ByteOrder.nativeOrder());
            PMDVertex tmpVertex = new PMDVertex();
            for (int i = 0; i < vertCount; i++) {
                tmpVertex.readFromStream(is);
                tmpVertex.writeToBuffer(vertexBuffer);
            }
        }
        faceVertCount = is.readInt();
        if (skipVertFlag) {
//            for (int i = 0; i < faceVertCount; i++) {
//                is.readUnsignedShort();
//            }
            long skip = is.skip(faceVertCount * 2);
            if (skip != faceVertCount * 2) {
                throw new IllegalArgumentException("skip = "+skip+" "+faceVertCount * 2);
            }
        } else {
            faceVertIndex = new int[faceVertCount];
            for (int i = 0; i < faceVertCount; i++) {
                faceVertIndex[i] = is.readUnsignedShort();
            }
            // 逆にする。
            for (int i = 0; i < faceVertCount; i += 3) {
                int tmp = faceVertIndex[i];
                faceVertIndex[i] = faceVertIndex[i + 1];
                faceVertIndex[i + 1] = tmp;
            }
        }
        materialCount = is.readInt();
        material = new PMDMaterial[materialCount];
        for (int i = 0; i < materialCount; i++) {
            material[i] = new PMDMaterial(is);
            material[i].setMaterialNo(i);
        }
        boneList = new PMDBoneList(is);
        ikList = new PMDIKList(is);
        skinCount = is.readShort();
        skinData = new PMDSkinData[skinCount];
        for (int i = 0; i < skinCount; i++) {
            skinData[i] = new PMDSkinData(is);
        }
        skinDispList = new PMDSkinDispList(is);
        boneDispNameList = new PMDBoneDispNameList(is);
        boneDispList = new PMDBoneDispList(is);
        headerEnglish = new PMDHeaderEnglish(this, is);
        toonTextureList = new PMDToonTextureList(is);
        try {
            rigidBodyList = new PMDRigidBodyList(is);
            jointList = new PMDJointList(is);
        } catch(EOFException ex) {
            rigidBodyList = new PMDRigidBodyList();
            jointList = new PMDJointList();
        }
//        toonTextureList = new PMDToonTextureList();
//        rigidBodyList = new PMDRigidBodyList();
//        jointList = new PMDJointList();
    }
    public void writeToStream(DataOutput os) throws IOException {
        PMDUtil.writeString(os, "Pmd", 3);
        os.writeFloat(version);
        PMDUtil.writeString(os, modelName, 20);
        PMDUtil.writeString(os, comment, 256);
        os.writeInt(vertCount);
        System.out.print("vertCount out = "+vertCount);
        for (int i = 0; i < vertCount; i++) {
            PMDVertex tmpVertex = new PMDVertex();
            getVertex(i, tmpVertex);
            tmpVertex.writeToStream(os);
        }
        os.writeInt(faceVertCount);
        for (int i = 0; i < faceVertCount; i += 3) {
            os.writeShort(faceVertIndex[i+1]);
            os.writeShort(faceVertIndex[i]);
            os.writeShort(faceVertIndex[i+2]);
        }
        os.writeInt(materialCount);
        for(PMDMaterial mat : material) {
            mat.writeToStream(os);
        }
        boneList.writeToStream(os);
        ikList.writeToStream(os);
        os.writeShort(skinCount);
        for(PMDSkinData skin : skinData) {
            skin.writeToStream(os);
        }
        skinDispList.writeToStream(os);
        boneDispNameList.writeToStream(os);
        boneDispList.writeToStream(os);
        headerEnglish.writeToStream(os);
        toonTextureList.writeToStream(os);
        rigidBodyList.writeToStream(os);
        jointList.writeToStream(os);
        
    }    
    public PMDVertex getVertex(int i) {
        return getVertex(i, new PMDVertex());
    }
    public PMDVertex getVertex(int i, PMDVertex in) {
        vertexBuffer.position(PMDVertex.size() * i);
        in.readFromBuffer(vertexBuffer);
        return in;
    }
    public void setVertex(int i, PMDVertex in) {
        vertexBuffer.position(PMDVertex.size() * i);
        in.writeToBuffer(vertexBuffer);
    }
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{id = " + id
                + " version = " + version
                + " modelName = " + modelName
                + " comment = " + comment);
        sb.append(" vertexCount = " + vertCount);
        PMDVertex tmpVertex = new PMDVertex();
        vertexBuffer.position(0);
        for (int i=0;i<vertCount;i++) {
            tmpVertex.readFromBuffer(vertexBuffer);
            sb.append(tmpVertex.toString());
        }
        sb.append(" faceVertCount = " + faceVertCount);
        sb.append(" faceVertIndex = {");
        for (int i : faceVertIndex) {
            sb.append(i).append(" ");
        }
        sb.append("}");
        sb.append(" materialCount = ").append(materialCount);
        sb.append(" PMDMaterial = {");
        for (PMDMaterial m : material) {
            sb.append(m).append(" ");
        }
        sb.append("}\n");
        sb.append("boneList = ");
        sb.append(boneList.toString());
        sb.append("\nikList = " + ikList);
        sb.append("\nskinData = {");

        for (int i = 0; i < skinCount; i++) {
            sb.append(skinData[i]);
            sb.append("\n");
        }
        sb.append("}\n}\n");

        sb.append(rigidBodyList.toString());
        return sb.toString();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getFaceVertCount() {
        return faceVertCount;
    }

    public void setFaceVertCount(int faceVertCount) {
        this.faceVertCount = faceVertCount;
    }

    public int[] getFaceVertIndex() {
        return faceVertIndex;
    }

    public void setFaceVertIndex(int[] faceVertIndex) {
        this.faceVertIndex = faceVertIndex;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PMDMaterial[] getMaterial() {
        return material;
    }

    public void setMaterial(PMDMaterial[] material) {
        this.material = material;
    }

    public int getMaterialCount() {
        return materialCount;
    }

    public void setMaterialCount(int materialCount) {
        this.materialCount = materialCount;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public float getVersion() {
        return version;
    }

    public void setVersion(float version) {
        this.version = version;
    }

    public int getVertCount() {
        return vertCount;
    }

    public void setVertCount(int vertCount) {
        this.vertCount = vertCount;
    }

    public ByteBuffer getVertexBuffer() {
        return vertexBuffer;
    }

    public void setVertexBuffer(ByteBuffer vertexBuffer) {
        this.vertexBuffer = vertexBuffer;
    }


    public PMDBoneList getBoneList() {
        return boneList;
    }

    public void setBoneList(PMDBoneList boneList) {
        this.boneList = boneList;
    }

    public int getSkinCount() {
        return skinCount;
    }

    public void setSkinCount(int skinCount) {
        this.skinCount = skinCount;
    }

    public PMDSkinData[] getSkinData() {
        return skinData;
    }

    public void setSkinData(PMDSkinData[] skinData) {
        this.skinData = skinData;
    }

    public PMDIKList getIkList() {
        return ikList;
    }

    public void setIkList(PMDIKList ikList) {
        this.ikList = ikList;
    }

    public PMDBoneDispList getBoneDispList() {
        return boneDispList;
    }

    public void setBoneDispList(PMDBoneDispList boneDispList) {
        this.boneDispList = boneDispList;
    }

    public PMDBoneDispNameList getBoneDispNameList() {
        return boneDispNameList;
    }

    public void setBoneDispNameList(PMDBoneDispNameList boneDispNameList) {
        this.boneDispNameList = boneDispNameList;
    }

    public PMDHeaderEnglish getHeaderEnglish() {
        return headerEnglish;
    }

    public void setHeaderEnglish(PMDHeaderEnglish headerEnglish) {
        this.headerEnglish = headerEnglish;
    }

    public PMDSkinDispList getSkinDispList() {
        return skinDispList;
    }

    public void setSkinDispList(PMDSkinDispList skinDispList) {
        this.skinDispList = skinDispList;
    }

    public PMDToonTextureList getToonTextureList() {
        return toonTextureList;
    }

    public void setToonTextureList(PMDToonTextureList toonTextureList) {
        this.toonTextureList = toonTextureList;
    }

    public PMDJointList getJointList() {
        return jointList;
    }

    public void setJointList(PMDJointList jointList) {
        this.jointList = jointList;
    }

    public PMDRigidBodyList getRigidBodyList() {
        return rigidBodyList;
    }

    public void setRigidBodyList(PMDRigidBodyList rigidBodyList) {
        this.rigidBodyList = rigidBodyList;
    }
}
