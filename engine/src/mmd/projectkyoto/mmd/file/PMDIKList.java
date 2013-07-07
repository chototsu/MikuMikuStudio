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
public class PMDIKList implements Serializable{
    private int ikDataCount;
    private PMDIKData pmdIKData[];
    public PMDIKList(DataInputStreamLittleEndian is) throws IOException {
        ikDataCount = is.readUnsignedShort();
//        System.out.println("ikDataCount = "+ikDataCount);
        pmdIKData = new PMDIKData[ikDataCount];
        for(int i=0;i<ikDataCount;i++) {
            pmdIKData[i] = new PMDIKData(is);
        }
    }
    public void writeToStream(DataOutput os) throws IOException {
        os.writeShort(ikDataCount);
        for(PMDIKData ikData : pmdIKData) {
            ikData.writeToStream(os);
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{ikDataCount = "+ikDataCount+"\n");
        sb.append("ikData = {\n");
        for(int i=0;i<ikDataCount;i++) {
            sb.append("ikDataCount = "+i);
            sb.append(" ikData = ");
            sb.append(pmdIKData[i]);
            sb.append("\n");
        }
        sb.append("}\n");
        sb.append("}\n");
        return sb.toString();
    }

    public int getIkDataCount() {
        return ikDataCount;
    }

    public void setIkDataCount(int ikDataCount) {
        this.ikDataCount = ikDataCount;
    }

    public PMDIKData[] getPmdIKData() {
        return pmdIKData;
    }

    public void setPmdIKData(PMDIKData[] pmdIKData) {
        this.pmdIKData = pmdIKData;
    }

}
