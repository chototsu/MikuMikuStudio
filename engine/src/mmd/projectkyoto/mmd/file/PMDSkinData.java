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

package projectkyoto.mmd.file;

import com.jme3.util.BufferUtils;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 *
 * @author kobayasi
 */
public class PMDSkinData implements Serializable{
    private String skinName; //20文字
    private int skinVertCount;
    private int skinType; // byte
    private ShortBuffer indexBuf;
    private FloatBuffer skinBuf;
    public PMDSkinData(DataInputStreamLittleEndian is) throws IOException {
//        PMDSkinVertData skinVertData[];
        skinName = is.readString(20);
//        System.out.println("skinName = "+skinName);
        skinVertCount = is.readInt();
        skinType = is.readByte();
//        System.out.println("skinVertCount = "+skinVertCount);
//        skinVertData = new PMDSkinVertData[skinVertCount];
//        for(int i=0;i<skinVertCount;i++) {
//            skinVertData[i] = new PMDSkinVertData(is);
//        }
        indexBuf = BufferUtils.createShortBuffer(skinVertCount);
        skinBuf = BufferUtils.createFloatBuffer(skinVertCount * 3);
        for(int i=0;i<skinVertCount;i++) {
            indexBuf.put((short)is.readInt());
            skinBuf.put(is.readFloat());
            skinBuf.put(is.readFloat());
            skinBuf.put(-is.readFloat());
        }
    }
    public void writeToStream(DataOutput os) throws IOException {
        PMDUtil.writeString(os, skinName, 20);
        os.writeInt(skinVertCount);
        os.writeByte(skinType);
        indexBuf.position(0);
        skinBuf.position(0);
        for(int i=0;i<skinVertCount;i++) {
            os.writeInt(indexBuf.get() & 0xffff);
            os.writeFloat(skinBuf.get());
            os.writeFloat(skinBuf.get());
            os.writeFloat(-skinBuf.get());
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{skinName = "+skinName
                +"\nskinVertCount = "+skinVertCount
                +"\nskinType = "+skinType
                +"\nskinVertData = ");
        for(int i=0;i<skinVertCount;i++) {
//            sb.append(skinVertData[i]);
            sb.append("\n");
        }
        sb.append("}\n");
        return sb.toString();
    }

    public String getSkinName() {
        return skinName;
    }

    public void setSkinName(String skinName) {
        this.skinName = skinName;
    }

    public int getSkinType() {
        return skinType;
    }

    public void setSkinType(int skinType) {
        this.skinType = skinType;
    }

    public int getSkinVertCount() {
        return skinVertCount;
    }

    public void setSkinVertCount(int skinVertCount) {
        this.skinVertCount = skinVertCount;
    }

    public ShortBuffer getIndexBuf() {
        return indexBuf;
    }

    public void setIndexBuf(ShortBuffer indexBuf) {
        this.indexBuf = indexBuf;
    }

    public FloatBuffer getSkinBuf() {
        return skinBuf;
    }

    public void setSkinBuf(FloatBuffer skinBuf) {
        this.skinBuf = skinBuf;
    }
    

}
