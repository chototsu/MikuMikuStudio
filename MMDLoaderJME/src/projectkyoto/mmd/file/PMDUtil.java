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
import java.nio.ByteBuffer;
import javax.vecmath.Vector3f;

/**
 *
 * @author Kazuhiko Kobayashi
 */
public class PMDUtil {

    public static Vector3f readVector3f(DataInputStreamLittleEndian is, Vector3f v)
            throws IOException {
        v.set(is.readFloat(), is.readFloat(), -is.readFloat());
        return v;
    }

    public static Vector3f readVector3f(DataInputStreamLittleEndian is) throws
            IOException {
        return readVector3f(is, new Vector3f());
    }
    public static Vector3f readVector3f(ByteBuffer bb, Vector3f v) {
        v.set(bb.getFloat(), bb.getFloat(), bb.getFloat());
        return v;
    }
    public static Vector3f writeVector3f(ByteBuffer bb, Vector3f v) {
        bb.putFloat(v.x);
        bb.putFloat(v.y);
        bb.putFloat(v.z);
        return v;
    }
    public static void writeString(DataOutput os, String s, int len) throws IOException {
        byte[] buf = s.getBytes("Shift_JIS");
        int l = buf.length;
        if (l > len) {
            os.write(buf, 0, len);
        } else {
            os.write(buf);
            for(;l < len;l++) {
                os.writeByte(0);
            }
        }
    }
    public static void writeVector3f(DataOutput os, Vector3f v) throws IOException{
        os.writeFloat(v.x);
        os.writeFloat(v.y);
        os.writeFloat(-v.z);
    }
}
