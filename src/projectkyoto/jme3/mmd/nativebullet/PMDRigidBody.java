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
package projectkyoto.jme3.mmd.nativebullet;

import com.jme3.animation.Bone;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Matrix3f;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import projectkyoto.jme3.mmd.PMDNode;

/**
 *
 * @author kobayasi
 */
public class PMDRigidBody extends PhysicsRigidBody {

    final PMDNode pmdNode;
    final Bone bone;
    final int rigidBodyType;
    final Vector3f pos = new Vector3f();
    final Quaternion rot = new Quaternion();
    final Vector3f invPos = new Vector3f();
    final Quaternion invRot = new Quaternion();
    final Vector3f tmpV = new Vector3f();
    final Quaternion tmpQ = new Quaternion();
    final Bone centerBone;
    final boolean centerFlag;
    Matrix4f m = new Matrix4f();
    Matrix4f invM = new Matrix4f();

    public PMDRigidBody(PMDNode pmdNode, Bone bone, int rigidBodyType, Vector3f pos, Quaternion rot, CollisionShape cs, float f) {
        super(cs, f);
        this.pmdNode = pmdNode;
        this.bone = bone;
        this.rigidBodyType = rigidBodyType;
        this.pos.set(pos);
        this.rot.set(rot);

        invPos.set(pos);
        invPos.negateLocal();
        invRot.set(rot);
        invRot.inverseLocal();
        invM.setTransform(pos, new Vector3f(1f, 1f, 1f), rot.toRotationMatrix());
        m.set(invM);
        invM.invertLocal();
        m2.loadIdentity();
        centerBone = pmdNode.getSkeleton().getBone("センター");
        if (bone == centerBone) {
            centerFlag = true;
        } else {
            centerFlag = false;
        }
//        if (bone != null) {
//            if (!isKinematic()) {
//                bone.setUseModelSpaceVectors(true);
//            }else {
//                bone.setUseModelSpaceVectors(false);
//            }
//        }
    }

    @Override
    public void setKinematic(boolean kinematic) {
        super.setKinematic(kinematic);
//        if (bone != null) {
//            if (!isKinematic()) {
//                bone.setUseModelSpaceVectors(true);
//            }else {
//                bone.setUseModelSpaceVectors(false);
//            }
//        }
    }
    
    public void update() {
        if (isKinematic()) {
            updateFromBoneMatrix();
        } else {
            updateToBoneMatrix();
        }
    }

    public void updateFromBoneMatrix() {
        if (bone != null) {
//            tmpV.set(bone.getModelSpacePosition());
//            tmpV.addLocal(pos);
//            tmpQ.set(bone.getModelSpaceRotation());
//            tmpQ.multLocal(rot);
            m2.setTranslation(bone.getModelSpacePosition());
            m2.setRotationQuaternion(bone.getModelSpaceRotation());
            m2.multLocal(m);
            m2.toRotationMatrix(tmpMatrix3f);
            tmpQ.fromRotationMatrix(tmpMatrix3f);
            super.setPhysicsRotation(tmpQ);
            m2.toTranslationVector(tmpV);
            super.setPhysicsLocation(tmpV);
        } else {
//            tmpV.set(centerBone.getModelSpacePosition());
//            tmpV.addLocal(pos);
//            tmpQ.set(centerBone.getModelSpaceRotation());
//            tmpQ.multLocal(rot);
            m2.setTranslation(centerBone.getModelSpacePosition());
            m2.setRotationQuaternion(centerBone.getModelSpaceRotation());
            m2.multLocal(m);
            m2.toRotationMatrix(tmpMatrix3f);
            tmpQ.fromRotationMatrix(tmpMatrix3f);
            super.setPhysicsRotation(tmpQ);
            m2.toTranslationVector(tmpV);
            super.setPhysicsLocation(tmpV);
        }
//        System.out.println("objectId = "+objectId+(bone != null ? " name = "+bone.getName() : "")+" pos = "+getPhysicsLocation());
    }
    public void reset() {
        updateFromBoneMatrix();
        setLinearVelocity(Vector3f.ZERO);
        setAngularVelocity(Vector3f.ZERO);
        clearForces();
    }
    Matrix4f m2 = new Matrix4f();
    Matrix4f m3 = new Matrix4f();
    Matrix3f tmpMatrix3f = new Matrix3f();

    public void updateToBoneMatrix() {
//        System.out.println("objectId = "+objectId+" name = "+bone.getName()+" pos = "+getPhysicsLocation());
        if (bone != null) {
            if (rigidBodyType == 2) {
                if (true/*!bone.getName().contains("センター")*/) {
                    super.getPhysicsLocation(tmpV);
                    super.getPhysicsRotation(tmpQ);
                    m2.setRotationQuaternion(tmpQ);
                    m2.setTranslation(tmpV);
                    m2.multLocal(invM);
//                System.out.println("updateToBoneMatrix:tmpV = "+tmpV);
//                tmpV.addLocal(invPos);
//                tmpQ.multLocal(invRot);
//                    bone.setUseModelSpaceVectors(false);
                    bone.updateWorldVectors();
//                    bone.setUseModelSpaceVectors(true);
                    m2.toRotationMatrix(tmpMatrix3f);
                    bone.getModelSpaceRotation().fromRotationMatrix(tmpMatrix3f);
//                    m2.toRotationQuat(bone.getModelSpaceRotation());
//                bone.getModelSpacePosition().set(m2.toTranslationVector());
//                    updateFromBoneMatrix();
//                super.getPhysicsLocation();
//                super.getPhysicsRotation();
                }
            } else {
                if (rigidBodyType == 1 && !centerFlag) {
                    super.getPhysicsLocation(tmpV);
                    super.getPhysicsRotation(tmpQ);
                    m2.setRotationQuaternion(tmpQ);
                    m2.setTranslation(tmpV);
                    m2.multLocal(invM);
//                tmpV.addLocal(invPos);
//                tmpQ.multLocal(invRot);
                    m2.toRotationMatrix(tmpMatrix3f);
                    bone.getModelSpaceRotation().fromRotationMatrix(tmpMatrix3f);
//                    m2.toRotationQuat(bone.getModelSpaceRotation());
                    m2.toTranslationVector(bone.getModelSpacePosition());
                }
            }
        }
    }

    public PMDNode getPmdNode() {
        return pmdNode;
    }

    public Bone getBone() {
        return bone;
    }

    public Vector3f getInvPos() {
        return invPos;
    }

    public Quaternion getInvRot() {
        return invRot;
    }

    public Vector3f getPos() {
        return pos;
    }

    public Quaternion getRot() {
        return rot;
    }
}
