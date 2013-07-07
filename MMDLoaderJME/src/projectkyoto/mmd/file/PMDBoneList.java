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

/**
 *
 * @author Kazuhiko Kobayashi
 */
public class PMDBoneList implements Serializable{
    private int boneCount;
    private PMDBone[] bones;
    public PMDBoneList(DataInputStreamLittleEndian is) throws IOException {
        boneCount = is.readUnsignedShort();
        bones = new PMDBone[boneCount];
        for(int i=0;i<boneCount;i++) {
            bones[i] = new PMDBone(is);
        }
    }
    public void writeToStream(DataOutput os) throws IOException {
        os.writeShort(boneCount);
        for(PMDBone bone : bones) {
            bone.writeToStream(os);
        }
    }
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{bouneCount = "+boneCount);
        sb.append(" bones = {\n");
        if (bones == null) {
            sb.append("null");
        } else {
            int i=0;
            for(PMDBone bone : bones) {
                sb.append("boneIndex = "+(i++)+" ");
                sb.append(bone);
            }
        }
        sb.append("}\n");
        return sb.toString();
    }

    public int getBoneCount() {
        return boneCount;
    }

    public void setBoneCount(int boneCount) {
        this.boneCount = boneCount;
    }

    public PMDBone[] getBones() {
        return bones;
    }

    public void setBones(PMDBone[] bones) {
        this.bones = bones;
    }

}
