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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 *
 * @author Kazuhiko Kobayashi
 */
public class Coords2d implements Serializable{
    private float u;
    private float v;

    @Override
    public String toString() {
        return "{u = "+u
                +" v = "+v
                +"}";
    }

    public Coords2d(float u, float v) {
        this.u = u;
        this.v = v;
    }
    public Coords2d(DataInput is) throws IOException {
        u = is.readFloat();
        v = is.readFloat();
    }
    public Coords2d readFromStream(DataInput is) throws IOException {
        u = is.readFloat();
        v = is.readFloat();
        return this;
    }
    public void writeToStream(DataOutput os) throws IOException {
        os.writeFloat(u);
        os.writeFloat(v);
    }
    public Coords2d readFromBuffer(ByteBuffer bb) {
        u = bb.getFloat();
        v = bb.getFloat();
        return this;
    }
    public Coords2d writeToBuffer(ByteBuffer bb) {
        bb.putFloat(u);
        bb.putFloat(v);
        return this;
    }
    public Coords2d() {
    }

    public float getU() {
        return u;
    }

    public void setU(float u) {
        this.u = u;
    }

    public float getV() {
        return v;
    }

    public void setV(float v) {
        this.v = v;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Coords2d other = (Coords2d) obj;
        if (Float.floatToIntBits(this.u) != Float.floatToIntBits(other.u)) {
            return false;
        }
        if (Float.floatToIntBits(this.v) != Float.floatToIntBits(other.v)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }
    
}
