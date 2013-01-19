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
public class PMDHeaderEnglish implements Serializable {

    private int englishNameCompatibility;
    private String modelName;
    private String comment;
    private String boneNameEnglish[];
    private String skinNameEnglish[];
    private String dispNameEnglish[];

    public PMDHeaderEnglish(PMDModel model, DataInputStreamLittleEndian is) throws IOException {
        englishNameCompatibility = is.readUnsignedByte();
        if (englishNameCompatibility == 1) {
            modelName = is.readString(20);
            comment = is.readString(256);
            boneNameEnglish = new String[model.getBoneList().getBoneCount()];
            for (int i = 0; i < boneNameEnglish.length; i++) {
                boneNameEnglish[i] = is.readString(20);
            }
            if (model.getSkinCount() > 0) {
                skinNameEnglish = new String[model.getSkinCount() - 1];
                for (int i = 0; i < skinNameEnglish.length; i++) {
                    skinNameEnglish[i] = is.readString(20);
                }
            }
            dispNameEnglish = new String[model.getBoneDispNameList().getBoneDispNameCount()];
            for (int i = 0; i < dispNameEnglish.length; i++) {
                dispNameEnglish[i] = is.readString(50);
            }
        }
    }

    public void writeToStream(DataOutput os) throws IOException {
        os.writeByte(englishNameCompatibility);
        if (englishNameCompatibility == 1) {
            PMDUtil.writeString(os, modelName, 20);
            PMDUtil.writeString(os, comment, 256);
            for(String boneName : boneNameEnglish) {
                PMDUtil.writeString(os, boneName, 20);
            }
            for(String skinName : skinNameEnglish) {
                PMDUtil.writeString(os, skinName, 20);
            }
            for(String dispName : dispNameEnglish) {
                PMDUtil.writeString(os, dispName, 50);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("englishNameCompatibility = ").append(englishNameCompatibility).append('\n');
        if (englishNameCompatibility != 1) {
        } else {
            sb.append("modelName = ").append(modelName).append('\n');
            sb.append("comment = ").append(comment).append('\n');
            sb.append("boneNameEnglish = ").append("{\n");
            for (int i = 0; i < boneNameEnglish.length; i++) {
                sb.append(i);
                sb.append(" ").append(boneNameEnglish[i]).append('\n');
            }
            sb.append("}\n");
            sb.append("skinNameEnglish = ").append("{\n");
            for (int i = 0; i < skinNameEnglish.length; i++) {
                sb.append(i);
                sb.append(" ").append(skinNameEnglish[i]).append('\n');
            }
            sb.append("}\n");
            sb.append("dispNameEnglish = ").append("{\n");
            for (int i = 0; i < dispNameEnglish.length; i++) {
                sb.append(i);
                sb.append(" ").append(dispNameEnglish[i]).append('\n');
            }
        }
        sb.append("}\n}}\n");
        return sb.toString();
    }

    public String[] getBoneNameEnglish() {
        return boneNameEnglish;
    }

    public void setBoneNameEnglish(String[] boneNameEnglish) {
        this.boneNameEnglish = boneNameEnglish;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String[] getDispNameEnglish() {
        return dispNameEnglish;
    }

    public void setDispNameEnglish(String[] dispNameEnglish) {
        this.dispNameEnglish = dispNameEnglish;
    }

    public int getEnglishNameCompatibility() {
        return englishNameCompatibility;
    }

    public void setEnglishNameCompatibility(int englishNameCompatibility) {
        this.englishNameCompatibility = englishNameCompatibility;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String[] getSkinNameEnglish() {
        return skinNameEnglish;
    }

    public void setSkinNameEnglish(String[] skinNameEnglish) {
        this.skinNameEnglish = skinNameEnglish;
    }
}
