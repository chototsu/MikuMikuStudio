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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import projectkyoto.mmd.file.util2.BufferUtil;

/**
 *
 * @author kobayasi
 */
public class VMDFile implements Serializable{
    private String vmdHeader; // char[30] Vocaloid Motion Data 0002
    private String vmdModelName; // char[20]
    private int motionCount;
//    private VMDMotion motionArray[];
    private int skinCount;
    private VMDSkin skinArray[];
    protected final ArrayList<String> boneNames = new ArrayList<String>();
//    protected final ArrayList<String> skinNames = new ArrayList<String>();
    private ByteBuffer bb1;
//    private ByteBuffer bb2;
    public VMDFile() {
//        motionArray = new VMDMotion[0];
        skinArray = new VMDSkin[0];
    }
    
    public VMDFile(URL url) throws IOException {
        read(url.openStream());
    }
    public VMDFile(InputStream is2) throws IOException {
        read(is2);
    }
    public void read(InputStream is2) throws IOException{
        DataInputStreamLittleEndian is = null;
        try {
            is = new DataInputStreamLittleEndian(new BufferedInputStream(is2));
            vmdHeader = is.readString(30);
            if (!"Vocaloid Motion Data 0002".equals(vmdHeader)) {
                throw new InvalidVMDFileException();
            }
            vmdModelName = is.readString(20);
            motionCount = is.readInt();
//            motionArray = new VMDMotion[motionCount];
            bb1 = BufferUtil.createByteBuffer(98 * motionCount);
            VMDMotion motion = new VMDMotion();
            motion.vmdFile = this;
            Integer indexes[] = new Integer[motionCount];
            for(int i=0;i<motionCount;i++) {
                motion.readFromStream(is);
                motion.writeToBuffer(bb1);
                indexes[i] = i;
            }
            Arrays.sort(indexes, new VMDComparator());
            ByteBuffer bb2 = BufferUtil.createByteBuffer(98 * motionCount);
            for(int i=0;i<motionCount;i++) {
                getMotion(motion, indexes[i]);
                bb2.position(98 * i);
                motion.writeToBuffer(bb2);
            }
            bb1 = bb2;
            skinCount = is.readInt();
//            bb2 = BufferUtil.createByteBuffer(98 * skinCount);
            skinArray = new VMDSkin[skinCount];
            for(int i=0;i<skinCount;i++) {
                skinArray[i] = new VMDSkin(is);
            }
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{vmdHeader = ").append(vmdHeader).append("\n");
        sb.append("vmdModelName = ").append(vmdModelName).append("\n");
        sb.append("motionCount = ").append(motionCount).append("\n");
        sb.append("motionArray = {\n");
        VMDMotion m = new VMDMotion();
        for(int i=0;i<motionCount;i++) {
            m.readFromBuffer(bb1);
            sb.append(m).append("\n");
        }
        sb.append("}\n");
        sb.append("skinCount = ").append(skinCount).append("\n");
        sb.append("skinArray = {\n");
        for(VMDSkin skin : skinArray) {
            sb.append(skin).append("\n");
        }
        sb.append("}\n");
        return sb.toString();
    }

//    public VMDMotion[] getMotionArray() {
//        return motionArray;
//    }

//    public void setMotionArray(VMDMotion[] motionArray) {
//        this.motionArray = motionArray;
//    }
    public synchronized VMDMotion getMotion(VMDMotion m, int i) {
        m.vmdFile = this;
        bb1.position(98 * i);
        m.readFromBuffer(bb1);
        return m;
    }
    public int getMotionCount() {
        return motionCount;
    }

    public void setMotionCount(int motionCount) {
        this.motionCount = motionCount;
    }

    public VMDSkin[] getSkinArray() {
        return skinArray;
    }

    public void setSkinArray(VMDSkin[] skinArray) {
        this.skinArray = skinArray;
    }

    public int getSkinCount() {
        return skinCount;
    }

    public void setSkinCount(int skinCount) {
        this.skinCount = skinCount;
    }

    public String getVmdHeader() {
        return vmdHeader;
    }

    public void setVmdHeader(String vmdHeader) {
        this.vmdHeader = vmdHeader;
    }

    public String getVmdModelName() {
        return vmdModelName;
    }

    public void setVmdModelName(String vmdModelName) {
        this.vmdModelName = vmdModelName;
    }
    class VMDComparator implements Comparator<Integer> {
        VMDMotion m1 = new VMDMotion();
        VMDMotion m2 = new VMDMotion();
        @Override
        public int compare(Integer t, Integer t1) {
            getMotion(m1, t);
            getMotion(m2, t1);
            if (m1.getFrameNo() < m2.getFrameNo()) {
                return -1;
            } else if (m1.getFrameNo() == m2.getFrameNo()) {
                return 0;
            }
            return 1;
        }
        
    }
}
