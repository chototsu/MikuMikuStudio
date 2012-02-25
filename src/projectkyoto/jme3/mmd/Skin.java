/*
 * Copyright (c) 2011 Kazuhiko Kobayashi All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * <p/>
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * <p/>
 * * Neither the name of 'MMDLoaderJME' nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * <p/>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package projectkyoto.jme3.mmd;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.util.BufferUtils;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import projectkyoto.mmd.file.PMDSkinData;
import projectkyoto.mmd.file.PMDSkinVertData;

/**
 *
 * @author kobayasi
 */
public class Skin implements Cloneable, Savable{

    String skinName;
    float weight = 0f;
    PMDNode pmdNode;
    boolean updateNeeded = false;
    ShortBuffer indexBuf;
    FloatBuffer skinBuf;

    public Skin(PMDNode pmdNode, String skinName) {
        this.pmdNode = pmdNode;
        this.skinName = skinName;
    }

    public Skin() {
    }

    public String getSkinName() {
        return skinName;
    }

    public void setSkinName(String skinName) {
        this.skinName = skinName;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        if (this.weight != weight) {
            this.weight = weight;
            pmdNode.setUpdateNeeded(true);
            setUpdateNeeded(true);
        }
    }

    public boolean isUpdateNeeded() {
        return updateNeeded;
    }

    public void setUpdateNeeded(boolean updateNeeded) {
        this.updateNeeded = updateNeeded;
    }

    @Override
    protected Skin clone() throws CloneNotSupportedException {
        return (Skin)super.clone();
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule c = ex.getCapsule(this);
        c.write(skinName, "skinName", "");
        c.write(weight, "weight", 0f);
        
    }

    public ShortBuffer getIndexBuf() {
        return indexBuf;
    }

    public void setIndexBuf(ShortBuffer indexBuf) {
        this.indexBuf = indexBuf;
    }

    public PMDNode getPmdNode() {
        return pmdNode;
    }

    public void setPmdNode(PMDNode pmdNode) {
        this.pmdNode = pmdNode;
    }

    public FloatBuffer getSkinBuf() {
        return skinBuf;
    }

    public void setSkinBuf(FloatBuffer skinBuf) {
        this.skinBuf = skinBuf;
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule c = im.getCapsule(this);
        skinName = c.readString("skinName", "");
        weight = c.readFloat("weight", 0f);
    }
}
