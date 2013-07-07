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
public class PMDJoint implements Serializable{

    private String jointName;
    private int rigidBodyA;
    private int rigidBodyB;
    private Vector3f jointPos;
    private Vector3f jointRot;
    private Vector3f constPos1;
    private Vector3f constPos2;
    private Vector3f constRot1;
    private Vector3f constRot2;
//    private Vector3f springPos;
//    private Vector3f springRot;
    float stiffness[] = new float[6];

    static void swapConst(Vector3f v1, Vector3f v2) {
        float tmp;
//        swapConst2(v1);
//        swapConst2(v2);
//        if (v1.x > v2.x) {
//            tmp = v1.x;
//            v1.x = v2.x;
//            v2.x = tmp;
//        }
//        if (v1.y > v2.y) {
//            tmp = v1.y;
//            v1.y = v2.y;
//            v2.y = tmp;
//        }
//        if (v1.z > v2.z) {
//            tmp = v1.z;
//            v1.z = v2.z;
//            v2.z = tmp;
//        }
        tmp = v1.x;
        v1.x = v2.x;
        v2.x = tmp;
        tmp = v1.y;
        v1.y = v2.y;
        v2.y = tmp;
    }
    static void swapConst2(Vector3f v1) {
        float tmp;
//        tmp = v1.x;
//        v1.x = v1.y;
//        v1.y = tmp;
//        v1.x *= -1f;
//        v1.y *= -1f;
//        v1.z *= -1f;
    }
    
    public PMDJoint(DataInputStreamLittleEndian is) throws IOException {
        jointName = is.readString(20);
        rigidBodyA = is.readInt();
        rigidBodyB = is.readInt();
        jointPos = new Vector3f(is.readFloat(), is.readFloat(), -is.readFloat());
        jointRot = new Vector3f(-is.readFloat(), -is.readFloat(), is.readFloat());
        constPos1 = new Vector3f(is.readFloat(), is.readFloat(), -is.readFloat());
        constPos2 = new Vector3f(is.readFloat(), is.readFloat(), -is.readFloat());
        constRot1 = new Vector3f(-is.readFloat(), -is.readFloat(), is.readFloat());
        constRot2 = new Vector3f(-is.readFloat(), -is.readFloat(), is.readFloat());
        
        float tmp;
        tmp = constPos1.z;
        constPos1.z = constPos2.z;
        constPos2.z = tmp;
        swapConst(constRot1, constRot2);
        for (int i = 0; i < 6; i++) {
            stiffness[i] = is.readFloat();
        }
    }
    public void writeToStream(DataOutput os) throws IOException {
        PMDUtil.writeString(os, jointName, 20);
        os.writeInt(rigidBodyA);
        os.writeInt(rigidBodyB);
        PMDUtil.writeVector3f(os, jointPos);
        
        os.writeFloat(-jointRot.x);
        os.writeFloat(-jointRot.y);
        os.writeFloat(jointRot.z);
        
        os.writeFloat(constPos1.x);
        os.writeFloat(constPos1.y);
        os.writeFloat(-constPos2.z);

        os.writeFloat(constPos2.x);
        os.writeFloat(constPos2.y);
        os.writeFloat(-constPos1.z);
        
        os.writeFloat(-constRot2.x);
        os.writeFloat(-constRot2.y);
        os.writeFloat(constRot1.z);
        
        os.writeFloat(-constRot1.x);
        os.writeFloat(-constRot1.y);
        os.writeFloat(constRot2.z);
        for(float f : stiffness) {
            os.writeFloat(f);
        }
    }

    @Override
    public String toString() {
        return "PMDJoint{" + "jointName=" + jointName + ", rigidBodyA=" + rigidBodyA + ", rigidBodyB=" + rigidBodyB + ", jointPos=" + jointPos + ", jointRot=" + jointRot + ", constPos1=" + constPos1 + ", constPos2=" + constPos2 + ", constRot1=" + constRot1 + ", constRot2=" + constRot2 + ", stiffness=" + stiffness + '}';
    }

    public Vector3f getConstPos1() {
        return constPos1;
    }

    public void setConstPos1(Vector3f constPos1) {
        this.constPos1 = constPos1;
    }

    public Vector3f getConstPos2() {
        return constPos2;
    }

    public void setConstPos2(Vector3f constPos2) {
        this.constPos2 = constPos2;
    }

    public Vector3f getConstRot1() {
        return constRot1;
    }

    public void setConstRot1(Vector3f constRot1) {
        this.constRot1 = constRot1;
    }

    public Vector3f getConstRot2() {
        return constRot2;
    }

    public void setConstRot2(Vector3f constRot2) {
        this.constRot2 = constRot2;
    }

    public String getJointName() {
        return jointName;
    }

    public void setJointName(String jointName) {
        this.jointName = jointName;
    }

    public Vector3f getJointPos() {
        return jointPos;
    }

    public void setJointPos(Vector3f jointPos) {
        this.jointPos = jointPos;
    }

    public Vector3f getJointRot() {
        return jointRot;
    }

    public void setJointRot(Vector3f jointRot) {
        this.jointRot = jointRot;
    }

    public int getRigidBodyA() {
        return rigidBodyA;
    }

    public void setRigidBodyA(int rigidBodyA) {
        this.rigidBodyA = rigidBodyA;
    }

    public int getRigidBodyB() {
        return rigidBodyB;
    }

    public void setRigidBodyB(int rigidBodyB) {
        this.rigidBodyB = rigidBodyB;
    }

//    public Vector3f getSpringPos() {
//        return springPos;
//    }
//
//    public void setSpringPos(Vector3f springPos) {
//        this.springPos = springPos;
//    }
//
//    public Vector3f getSpringRot() {
//        return springRot;
//    }
//
//    public void setSpringRot(Vector3f springRot) {
//        this.springRot = springRot;
//    }
    public float[] getStiffness() {
        return stiffness;
    }

    public void setStiffness(float[] stiffness) {
        this.stiffness = stiffness;
    }
}
