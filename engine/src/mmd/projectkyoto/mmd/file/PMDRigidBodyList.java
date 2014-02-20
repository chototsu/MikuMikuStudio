/*
 * Copyright (c) 2010-2014, Kazuhiko Kobayashi
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package projectkyoto.mmd.file;

import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

/**
 *
 * @author kobayasi
 */
public class PMDRigidBodyList implements Serializable{

    private int rigidBodyCount;
    private PMDRigidBody[] rigidBodyArray;

    public PMDRigidBodyList() {
        rigidBodyCount = 0;
        rigidBodyArray = new PMDRigidBody[0];
    }

    public PMDRigidBodyList(DataInputStreamLittleEndian is) throws IOException {
        rigidBodyCount = is.readInt();
        rigidBodyArray = new PMDRigidBody[rigidBodyCount];
        for (int i = 0; i < rigidBodyCount; i++) {
            rigidBodyArray[i] = new PMDRigidBody(is);
        }
    }
    public void writeToStream(DataOutput os) throws IOException {
        os.writeInt(rigidBodyCount);
        for(PMDRigidBody rigidBody : rigidBodyArray) {
            rigidBody.writeToStream(os);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("rigidBodyCount = ").append(rigidBodyCount).append('\n');
        sb.append("rigidBodyArray = {\n");
        for (int i = 0; i < rigidBodyArray.length; i++) {
            sb.append(rigidBodyArray[i]).append('\n');
        }
        return sb.toString();
    }

    public PMDRigidBody[] getRigidBodyArray() {
        return rigidBodyArray;
    }

    public void setRigidBodyArray(PMDRigidBody[] rigidBodyArray) {
        this.rigidBodyArray = rigidBodyArray;
    }

    public int getRigidBodyCount() {
        return rigidBodyCount;
    }

    public void setRigidBodyCount(int rigidBodyCount) {
        this.rigidBodyCount = rigidBodyCount;
    }
}
