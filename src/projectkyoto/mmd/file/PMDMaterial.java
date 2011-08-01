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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 *
 * @author Kazuhiko Kobayashi
 */
public class PMDMaterial {
    private XMaterial material;
    private byte toonIndex;
    private byte edgeFlag;
    private int faceVertCount;
    private String textureFileName; // 20文字
    private byte[] textureData;

    public PMDMaterial() {
    }
    public PMDMaterial(DataInputStreamLittleEndian is) throws IOException {
        material = new XMaterial(is);
        toonIndex = is.readByte();
        edgeFlag = is.readByte();
        faceVertCount = is.readInt();
        textureFileName = is.readString(20);
//        if (textureFileName.length() != 0) {
//            texture = TextureIO.newTexture(new URL(is.url ,textureFileName), true,"bmp");
//        }
        if ( false && !textureFileName.isEmpty()) {
            InputStream textureIs = null;
            try {
                textureIs = new URL(is.url ,textureFileName).openStream();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                byte[] buf = new byte[4096];
                for(;;) {
                    int size = textureIs.read(buf);
                    if (size <= 0) {
                        break;
                    }
                    os.write(buf,0,size);
                }
                os.close();
                textureData = os.toByteArray();
            } catch(IOException ex) {
                ex.printStackTrace();
            } finally {
                if (textureIs != null) {
                    textureIs.close();
                    textureIs = null;
                }
            }
        }
    }

    public byte getEdgeFlag() {
        return edgeFlag;
    }

    public void setEdgeFlag(byte edgeFlag) {
        this.edgeFlag = edgeFlag;
    }

    public int getFaceVertCount() {
        return faceVertCount;
    }

    public void setFaceVertCount(int faceVertCount) {
        this.faceVertCount = faceVertCount;
    }

    public XMaterial getMaterial() {
        return material;
    }

    public void setMaterial(XMaterial material) {
        this.material = material;
    }

    public String getTextureFileName() {
        return textureFileName;
    }

    public void setTextureFileName(String textureFileName) {
        this.textureFileName = textureFileName;
    }

    public byte getToonIndex() {
        return toonIndex;
    }

    public void setToonIndex(byte toonIndex) {
        this.toonIndex = toonIndex;
    }

    public byte[] getTextureData() {
        return textureData;
    }

    public void setTextureData(byte[] textureData) {
        this.textureData = textureData;
    }

    @Override
    public String toString() {
        return "{material = "+material
                +" toonIndex = "+toonIndex
                +" edgeFlag = "+edgeFlag
                +" faceVertCount = "+faceVertCount
                +" textureFileName = "+textureFileName
                +"}\n";
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
        final PMDMaterial other = (PMDMaterial) obj;
        if (this.material != other.material && (this.material == null || !this.material.equals(other.material))) {
            return false;
        }
        if (this.toonIndex != other.toonIndex) {
            return false;
        }
        if (this.edgeFlag != other.edgeFlag) {
            return false;
        }
        if ((this.textureFileName == null) ? (other.textureFileName != null) : !this.textureFileName.equals(other.textureFileName)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.material != null ? this.material.hashCode() : 0);
        return hash;
    }
    
}
