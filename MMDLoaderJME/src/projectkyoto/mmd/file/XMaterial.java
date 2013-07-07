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
public class XMaterial implements Serializable{
    private XColorRGBA faceColor;
    private float power;
    private XColorRGB specularColor;
    private XColorRGB ambientColor;

    public XMaterial() {
    }
    public XMaterial(DataInputStreamLittleEndian is) throws IOException {
        faceColor = new XColorRGBA(is);
        power = is.readFloat();
        specularColor = new XColorRGB(is);
        ambientColor = new XColorRGB(is);
    }
    public void writeToStream(DataOutput os) throws IOException {
        faceColor.writeToStream(os);
        os.writeFloat(power);
        specularColor.writeToStream(os);
        ambientColor.writeToStream(os);
    }

    @Override
    public String toString() {
        return "{faceColor = "+faceColor
                +" power = "+power
                +" specularColor = "+specularColor
                +" emissiveColor = "+ambientColor
                +"}";
    }

    public XColorRGB getAmbientColor() {
        return ambientColor;
    }

    public void setAmbientColor(XColorRGB ambientColor) {
        this.ambientColor = ambientColor;
    }

    public XColorRGBA getFaceColor() {
        return faceColor;
    }

    public void setFaceColor(XColorRGBA faceColor) {
        this.faceColor = faceColor;
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }

    public XColorRGB getSpecularColor() {
        return specularColor;
    }

    public void setSpecularColor(XColorRGB specularColor) {
        this.specularColor = specularColor;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final XMaterial other = (XMaterial) obj;
        if (this.faceColor != other.faceColor && (this.faceColor == null || !this.faceColor.equals(other.faceColor))) {
            return false;
        }
        if (Float.floatToIntBits(this.power) != Float.floatToIntBits(other.power)) {
            return false;
        }
        if (this.specularColor != other.specularColor && (this.specularColor == null || !this.specularColor.equals(other.specularColor))) {
            return false;
        }
        if (this.ambientColor != other.ambientColor && (this.ambientColor == null || !this.ambientColor.equals(other.ambientColor))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + (this.faceColor != null ? this.faceColor.hashCode() : 0);
        return hash;
    }
    
}
