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

import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import projectkyoto.mmd.file.util2.BufferUtil;

/**
 *
 * @author kobayasi
 */
public class PMDBoneDispNameList implements Serializable{
    private int boneDispNameCount;
    private String[] dispNameArray;
    
    public PMDBoneDispNameList() {
    }
    public PMDBoneDispNameList(DataInputStreamLittleEndian is) throws IOException {
        boneDispNameCount = is.readUnsignedByte();
        dispNameArray = new String[boneDispNameCount];
        for(int i=0;i<boneDispNameCount;i++) {
            dispNameArray[i] = is.readString(50);
        }
    }
    public void writeToStream(DataOutput os) throws IOException {
        os.writeByte(boneDispNameCount);
        for(String dispName : dispNameArray) {
            PMDUtil.writeString(os, dispName, 50);
        }
    }
    public void readFromBuffer(ByteBuffer bb) {
        boneDispNameCount = bb.get();
        dispNameArray = new String[boneDispNameCount];
        for(int i=0;i<boneDispNameCount;i++) {
            dispNameArray[i] = BufferUtil.readString(bb, 50);
        }
    }
    public void writeToBuffer(ByteBuffer bb) {
//        boneDispNameCou
    }
    @Override
    public String toString() {
        return "PMDBoneDispNameList{" + "boneDispNameCount=" + boneDispNameCount + ", dispNameArray=" + dispNameArray + '}';
    }

    public int getBoneDispNameCount() {
        return boneDispNameCount;
    }

    public void setBoneDispNameCount(int boneDispNameCount) {
        this.boneDispNameCount = boneDispNameCount;
    }

    public String[] getDispNameArray() {
        return dispNameArray;
    }

    public void setDispNameArray(String[] dispNameArray) {
        this.dispNameArray = dispNameArray;
    }
    
}
