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

/**
 *
 * @author kobayasi
 */
public class PMDBoneDispList implements Serializable{
    private int boneDispCount;
    private PMDBoneDisp boneDispArray[];

    public PMDBoneDispList() {
    }
    public PMDBoneDispList(DataInputStreamLittleEndian is) throws IOException {
        boneDispCount = is.readInt();
        boneDispArray = new PMDBoneDisp[boneDispCount];
        for(int i=0;i<boneDispCount;i++) {
            boneDispArray[i] = new PMDBoneDisp(is);
        }
    }
    public void writeToStream(DataOutput os) throws IOException {
        os.writeInt(boneDispCount);
        for(int i=0;i<boneDispCount;i++) {
            boneDispArray[i].writeToStream(os);
        }
    }
    public void readFromBuffer(ByteBuffer bb) {
        boneDispCount = bb.getInt();
        boneDispArray = new PMDBoneDisp[boneDispCount];
        for(int i=0;i<boneDispCount;i++) {
            PMDBoneDisp bd = new PMDBoneDisp();
            bd.readFromBuffer(bb);
            boneDispArray[i] = bd;
        }
    }
    public void writeToBuffer(ByteBuffer bb) {
        bb.putInt(boneDispCount);
        for(PMDBoneDisp bd : boneDispArray) {
            bd.writeToBuffer(bb);
        }
    }
    @Override
    public String toString() {
        return "PMDBoneDispList{" + "boneDispCount=" + boneDispCount + ", boneDispArray=" + boneDispArray + '}';
    }

    public PMDBoneDisp[] getBoneDispArray() {
        return boneDispArray;
    }

    public void setBoneDispArray(PMDBoneDisp[] boneDispArray) {
        this.boneDispArray = boneDispArray;
    }

    public int getBoneDispCount() {
        return boneDispCount;
    }

    public void setBoneDispCount(int boneDispCount) {
        this.boneDispCount = boneDispCount;
    }
    
}
