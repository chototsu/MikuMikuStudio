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
import javax.vecmath.Vector3f;

/**
 *
 * @author kobayasi
 */
public class PMDRigidBody implements Serializable{
    private String rigidBodyName;
    private int relBoneIndex;
    private int rigidBodyGroupIndex;
    private int rigidBodyGroupTarget;
    private int shapeType;
    private float shapeW;
    private float shapeH;
    private float shapeD;
    private Vector3f pos;
    private Vector3f rot; // z y x
    private float weight;
    private float posDim;
    private float rotDim;
    private float recoil;
    private float friction;
    private int rigidBodyType;
    public PMDRigidBody(DataInputStreamLittleEndian is) throws IOException {
        rigidBodyName = is.readString(20);
        relBoneIndex = is.readUnsignedShort();
        rigidBodyGroupIndex = is.readUnsignedByte();
        rigidBodyGroupTarget = is.readUnsignedShort();
        shapeType = is.readUnsignedByte();
        shapeW = is.readFloat();
        shapeH = is.readFloat();
        shapeD = is.readFloat();
        pos = new Vector3f(is.readFloat(),is.readFloat(),-is.readFloat());
        rot = new Vector3f(-is.readFloat(),-is.readFloat(),is.readFloat());
        weight = is.readFloat();
        posDim = is.readFloat();
        rotDim = is.readFloat();
        recoil = is.readFloat();
        friction = is.readFloat();
        rigidBodyType = is.readUnsignedByte();
    }
    public void writeToStream(DataOutput os) throws IOException {
        PMDUtil.writeString(os, rigidBodyName, 20);
        os.writeShort(relBoneIndex);
        os.writeByte(rigidBodyGroupIndex);
        os.writeShort(rigidBodyGroupTarget);
        os.writeByte(shapeType);
        os.writeFloat(shapeW);
        os.writeFloat(shapeH);
        os.writeFloat(shapeD);
        PMDUtil.writeVector3f(os, pos);
        os.writeFloat(-rot.x);
        os.writeFloat(-rot.y);
        os.writeFloat(rot.z);
        os.writeFloat(weight);
        os.writeFloat(posDim);
        os.writeFloat(rotDim);
        os.writeFloat(recoil);
        os.writeFloat(friction);
        os.writeByte(rigidBodyType);
    }

    @Override
    public String toString() {
        return "PMDRigidBody{" + "rigidBodyName=" + rigidBodyName + ", relBoneIndex=" + relBoneIndex + ", rigidBodyGroupIndex=" + rigidBodyGroupIndex + ", rigidBodyGroupTarget=" + rigidBodyGroupTarget + ", shapeType=" + shapeType + ", shapeW=" + shapeW + ", shapeH=" + shapeH + ", shapeD=" + shapeD + ", pos=" + pos + ", rot=" + rot + ", weight=" + weight + ", posDim=" + posDim + ", rotDim=" + rotDim + ", recoil=" + recoil + ", friction=" + friction + ", rigidBodyType=" + rigidBodyType + '}';
    }

    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public Vector3f getPos() {
        return pos;
    }

    public void setPos(Vector3f pos) {
        this.pos = pos;
    }

    public float getPosDim() {
        return posDim;
    }

    public void setPosDim(float posDim) {
        this.posDim = posDim;
    }

    public float getRecoil() {
        return recoil;
    }

    public void setRecoil(float recoil) {
        this.recoil = recoil;
    }

    public int getRelBoneIndex() {
        return relBoneIndex;
    }

    public void setRelBoneIndex(int relBoneIndex) {
        this.relBoneIndex = relBoneIndex;
    }

    public int getRigidBodyGroupIndex() {
        return rigidBodyGroupIndex;
    }

    public void setRigidBodyGroupIndex(int rigidBodyGroupIndex) {
        this.rigidBodyGroupIndex = rigidBodyGroupIndex;
    }

    public int getRigidBodyGroupTarget() {
        return rigidBodyGroupTarget;
    }

    public void setRigidBodyGroupTarget(int rigidBodyGroupTarget) {
        this.rigidBodyGroupTarget = rigidBodyGroupTarget;
    }

    public String getRigidBodyName() {
        return rigidBodyName;
    }

    public void setRigidBodyName(String rigidBodyName) {
        this.rigidBodyName = rigidBodyName;
    }

    public int getRigidBodyType() {
        return rigidBodyType;
    }

    public void setRigidBodyType(int rigidBodyType) {
        this.rigidBodyType = rigidBodyType;
    }

    public Vector3f getRot() {
        return rot;
    }

    public void setRot(Vector3f rot) {
        this.rot = rot;
    }

    public float getRotDim() {
        return rotDim;
    }

    public void setRotDim(float rotDim) {
        this.rotDim = rotDim;
    }

    public float getShapeD() {
        return shapeD;
    }

    public void setShapeD(float shapeD) {
        this.shapeD = shapeD;
    }

    public float getShapeH() {
        return shapeH;
    }

    public void setShapeH(float shapeH) {
        this.shapeH = shapeH;
    }

    public int getShapeType() {
        return shapeType;
    }

    public void setShapeType(int shapeType) {
        this.shapeType = shapeType;
    }

    public float getShapeW() {
        return shapeW;
    }

    public void setShapeW(float shapeW) {
        this.shapeW = shapeW;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
    
}
