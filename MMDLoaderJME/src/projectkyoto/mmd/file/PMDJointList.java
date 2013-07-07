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
 * @author kobayasi
 */
public class PMDJointList implements Serializable{
    private int jointCount;
    private PMDJoint jointArray[];

    public PMDJointList() {
        jointCount = 0;
        jointArray = new PMDJoint[0];
    }

    public PMDJointList(DataInputStreamLittleEndian is) throws IOException {
        jointCount = is.readInt();
        jointArray = new PMDJoint[jointCount];
        for(int i=0;i<jointArray.length;i++) {
            jointArray[i] = new PMDJoint(is);
        }
    }
    public void writeToStream(DataOutput os) throws IOException {
        os.writeInt(jointCount);
        for(PMDJoint joint : jointArray) {
            joint.writeToStream(os);
        }
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("jointCount = ").append(jointCount).append('\n');
        sb.append("jointArray = {\n");
        for(int i=0;i<jointArray.length;i++) {
            sb.append(jointArray[i].toString()).append('\n');
        }
        sb.append("}\n");
        return sb.toString();
    }

    public PMDJoint[] getJointArray() {
        return jointArray;
    }

    public void setJointArray(PMDJoint[] jointArray) {
        this.jointArray = jointArray;
    }

    public int getJointCount() {
        return jointCount;
    }

    public void setJointCount(int jointCount) {
        this.jointCount = jointCount;
    }
    
}
