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

package projectkyoto.mmd.file.util;

public class MeshKey implements Comparable<MeshKey> {
    int materialNo;
    int bone1;
    int bone2;
    boolean morph;
    public MeshKey(int materialNo, int bone1, int bone2, boolean morph) {
        this.materialNo = materialNo;
        if (bone1 < bone2) {
            this.bone1 = bone1;
            this.bone2 = bone2;
        } else {
            this.bone1 = bone2;
            this.bone2 = bone1;
        }
        this.morph = morph;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MeshKey other = (MeshKey) obj;
        if (this.materialNo != other.materialNo) {
            return false;
        }
        if (this.bone1 != other.bone1) {
            return false;
        }
        if (this.bone2 != other.bone2) {
            return false;
        }
        if (this.morph != other.morph) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.materialNo;
        hash = 67 * hash + this.bone1;
        hash = 67 * hash + this.bone2;
        return hash;
    }

    public int compareTo(MeshKey o) {
        if (equals(o)) {
            return 0;
        }
        if (!morph) {
            return -1;
        }
        if (materialNo < o.materialNo) {
            return -1;
        }
        if (bone1 < o.bone1) {
            return -1;
        }
        if (bone2 < o.bone2) {
            return -1;
        }
        return 1;
    }

    public int getBone1() {
        return bone1;
    }

    public void setBone1(int bone1) {
        this.bone1 = bone1;
    }

    public int getBone2() {
        return bone2;
    }

    public void setBone2(int bone2) {
        this.bone2 = bone2;
    }

    public int getMaterialNo() {
        return materialNo;
    }

    public void setMaterialNo(int materialNo) {
        this.materialNo = materialNo;
    }

    public boolean isMorph() {
        return morph;
    }

    public void setMorph(boolean morph) {
        this.morph = morph;
    }
    
}
