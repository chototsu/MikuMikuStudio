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
import com.jme3.animation.Skeleton;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.joints.SixDofJoint;
import com.jme3.bullet.joints.SixDofSpringJoint;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;
import java.util.HashMap;
import java.util.Map;
import javax.vecmath.Quat4f;
import projectkyoto.jme3.mmd.PMDNode;
import projectkyoto.mmd.file.PMDException;
import projectkyoto.mmd.file.PMDJoint;
import projectkyoto.mmd.file.PMDModel;

/**
 *
 * @author kobayasi
 */
public class PMDPhysicsWorld {
    // bullet physics

    PhysicsSpace physicsSpace;
    Map<PMDNode, PMDRigidBody[]> rigidBodyMap = new HashMap<PMDNode, PMDRigidBody[]>();
    Map<PMDNode, SixDofJoint[]> constraintMap = new HashMap<PMDNode, SixDofJoint[]>();
    float accuracy = 1f / 240;

    public PMDPhysicsWorld() {
        float dist = 400f;
        physicsSpace = new PhysicsSpace(
                new Vector3f(-dist, -dist, -dist),
                new Vector3f(dist, dist, dist),
                PhysicsSpace.BroadphaseType.AXIS_SWEEP_3);
//        physicsSpace.setGravity(new Vector3f(0f, -9.8f * 2*2*2, 20f));
//        physicsSpace.setGravity(new Vector3f(0f, -9.8f * 2, 0f));
        physicsSpace.setGravity(new Vector3f(0f, -9.8f * 1f, 0f));
//        physicsSpace.create();
//        physicsSpace.update(dist, 1);
        physicsSpace.setAccuracy(accuracy);
    }

//    public PMDPhysicsWorld(PhysicsSpace physicsSpace) {
//        this.physicsSpace = physicsSpace;
//    }
//    void init() {
//        float dist = 400f;
//        collisionConfig = new DefaultCollisionConfiguration();
//        dispatcher = new CollisionDispatcher(collisionConfig);
//        overlappingPairCache = new AxisSweep3(
//                new javax.vecmath.Vector3f(-dist, -dist, -dist),
//                new javax.vecmath.Vector3f(dist, dist, dist),1024);
//        solver = new SequentialImpulseConstraintSolver();
//        btWorld = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache,
//                solver, collisionConfig);
//        btWorld.setGravity(new javax.vecmath.Vector3f(0f, -9.8f * 2, 0f));
//    }
    public void addPMDNode(PMDNode pmdNode) {
        Skeleton skeleton = pmdNode.getSkeleton();
        skeleton.updateWorldVectors();
        PMDModel pmdModel = pmdNode.getPmdModel();
        PMDRigidBody[] rigidBodyArray = new PMDRigidBody[pmdModel.getRigidBodyList().getRigidBodyArray().length];
        rigidBodyMap.put(pmdNode, rigidBodyArray);
        for (int i = 0; i < pmdModel.getRigidBodyList().getRigidBodyArray().length; i++) {
            projectkyoto.mmd.file.PMDRigidBody fileRigidBody =
                    pmdModel.getRigidBodyList().getRigidBodyArray()[i];
            Bone bone = null;
            if (fileRigidBody.getRelBoneIndex() != 0xffff) {
                bone = skeleton.getBone(fileRigidBody.getRelBoneIndex());
            }
            PMDRigidBody rb = createRigidBody(pmdNode, fileRigidBody, bone);
            rigidBodyArray[i] = rb;

//            btWorld.addRigidBody(rb, (short) (1 << fileRigidBody.getRigidBodyGroupIndex()),
//                    (short) fileRigidBody.getRigidBodyGroupTarget());
            rb.setCollisionGroup(1 << (fileRigidBody.getRigidBodyGroupIndex()));
            rb.setCollideWithGroups(fileRigidBody.getRigidBodyGroupTarget());
//                  rb.setCollideWithGroups(0 );
            physicsSpace.addCollisionObject(rb);
        }
        SixDofJoint constArray[] = new SixDofJoint[pmdModel.getJointList().getJointCount()];
        constraintMap.put(pmdNode, constArray);
        for (int i = 0; i < constArray.length; i++) {
            SixDofJoint constraint = createConstraint(pmdNode,
                    rigidBodyArray, pmdModel.getJointList().getJointArray()[i]);
            constArray[i] = constraint;
            physicsSpace.add(constraint);
        }
//        physicsSpace.update(1 / 60f, 1);
    }

    public void removePMDNode(PMDNode pmdNode) {
        PMDRigidBody[] rigidBodyArray = rigidBodyMap.remove(pmdNode);
        if (rigidBodyArray != null) {
            for (PMDRigidBody rb : rigidBodyArray) {
                physicsSpace.remove(rb);
            }
        }
        SixDofJoint[] constArray = constraintMap.remove(pmdNode);
        if (constArray != null) {
            for (SixDofJoint joint : constArray) {
                physicsSpace.remove(joint);
            }
        }
    }
    float[] buf = new float[3];

    PMDRigidBody createRigidBody(PMDNode pmdNode,
            projectkyoto.mmd.file.PMDRigidBody fileRigidBody, Bone bone) {
        javax.vecmath.Vector3f localInertia = new javax.vecmath.Vector3f();
//        CollisionShape cs;
        boolean kinematic = false;
//        RigidBodyConstructionInfo rbInfo;
        float mass;
        Matrix4f trans = new Matrix4f();
        Matrix4f trans2 = new Matrix4f();
        Vector3f v = new Vector3f(
                fileRigidBody.getPos().x,
                fileRigidBody.getPos().y,
                fileRigidBody.getPos().z);

        trans.loadIdentity();
//        buf[0] = fileRigidBody.getRot().x;
//        buf[1] = fileRigidBody.getRot().y;
//        buf[2] = fileRigidBody.getRot().z;
//
        Quaternion q = new Quaternion(buf);
        Matrix3f rotMatrix = new Matrix3f();
        convPMDEuler(rotMatrix, fileRigidBody.getRot().x, fileRigidBody.getRot().y, fileRigidBody.getRot().z);
        Matrix4f m = new Matrix4f();
        m.loadIdentity();
//        m.setRotationQuaternion(q);
        m.setTransform(v, new Vector3f(1f, 1f, 1f), rotMatrix);
        q = m.toRotationQuat();
//        Quat4f q2 = new Quat4f(q.getX(), q.getY(), q.getZ(), q.getW());
        v = m.toTranslationVector();
        trans2.loadIdentity();
        trans2.setRotationQuaternion(q);
        trans2.setTranslation(v);

        Matrix4f m2 = new Matrix4f();
        m2.loadIdentity();
        if (bone != null) {
            m2.setTransform(bone.getModelSpacePosition(), bone.getModelSpaceScale(),
                    bone.getModelSpaceRotation().toRotationMatrix());
        } else {
//            // center bone
            Bone centerBone = pmdNode.getSkeleton().getBone("センター");
            m2.setTransform(centerBone.getModelSpacePosition(), centerBone.getModelSpaceScale(),
                    centerBone.getModelSpaceRotation().toRotationMatrix());
//            bone = centerBone;
        }
        m2.mult(m, m);
        q = m.toRotationQuat();
//        q2 = new Quat4f(q.getX(), q.getY(), q.getZ(), q.getW());
        v = m.toTranslationVector();
        trans.loadIdentity();
        trans.setRotationQuaternion(q);
        trans.setTranslation(v);
        CollisionShape cs;
        float margin = 0.00f;
        switch (fileRigidBody.getShapeType()) {
            case 0:
                cs = new SphereCollisionShape(fileRigidBody.getShapeW() - margin);
                break;
            case 1:
                cs = new BoxCollisionShape(new Vector3f(fileRigidBody.getShapeW() - margin,
                        fileRigidBody.getShapeH() - margin,
                        fileRigidBody.getShapeD() - margin));
                break;
            case 2:
                cs = new CapsuleCollisionShape(fileRigidBody.getShapeW() - margin, fileRigidBody.getShapeH() - margin);
                break;
            default:
                throw new PMDException("Invalid getShapeType:" + fileRigidBody.getRigidBodyName() + " "
                        + fileRigidBody.getShapeType());
        }
        cs.setMargin(0.1f);
        if (fileRigidBody.getRigidBodyType() != 0) {
            mass = fileRigidBody.getWeight();
            kinematic = false;
        } else {
            mass = 0f;
            kinematic = true;
        }
        if (mass != 0f) {
//            cs.calculateLocalInertia(mass, localInertia);
        }
//        MotionState ms;// = new PMDMotionState(bone, trans, trans);
//        if (fileRigidBody.getRigidBodyType() == 0) {
//            ms = new KinematicMotionState(trans2, trans, bone);
//        } else if (false && fileRigidBody.getRigidBodyType() == 2) {
//            ms = new AlignedMotionState(trans, trans, bone);
//        } else {
//            ms = new PMDMotionState(bone, trans, trans);
//        }

//        RigidBodyConstructionInfo ci = new RigidBodyConstructionInfo(mass, ms, cs, localInertia);
//        ci.linearDamping = fileRigidBody.getPosDim();
//        ci.angularDamping = fileRigidBody.getRotDim();
//        ci.restitution = fileRigidBody.getRecoil();
//        ci.friction = fileRigidBody.getFriction();
//        ci.additionalDamping = true;
//        Transform worldTrans = new Transform(trans2);
//        worldTrans.origin.add(localInertia)
//        PMDRigidBody rb = new PMDRigidBody(pmdNode, bone, trans2, trans, kinematic, ci);
        PMDRigidBody rb = new PMDRigidBody(pmdNode, bone, fileRigidBody.getRigidBodyType(), trans2.toTranslationVector(), trans2.toRotationQuat(), cs, mass);
//        rb.setPhysicsRotation(Quaternion.ZERO);
//        rb.setPhysicsLocation(Vector3f.ZERO);
        rb.updateFromBoneMatrix();
//        rb.setMass(mass * 1000f);
        rb.setDamping(fileRigidBody.getPosDim(), fileRigidBody.getRotDim());
        rb.setRestitution(fileRigidBody.getRecoil());
//        rb.setFriction(fileRigidBody.getFriction());

//        rb.setWorldTransform(worldTrans);
        if (kinematic) {
            rb.setKinematic(true);

//            System.out.println("kinematic " + fileRigidBody.getRigidBodyName());
//            rb.setCollisionFlags(rb.getCollisionFlags() | CollisionFlags.KINEMATIC_OBJECT);
//            rb.setCollisionFlags(rb.getCollisionFlags() | CollisionObject.);
        } else {
            rb.setKinematic(false);
        }
//        rb.setActivationState(RigidBody.DISABLE_DEACTIVATION);

//        rb.activate();
        return rb;
    }

    void _convPMDEuler(Matrix3f out, float x, float y, float z) {
        Quaternion q = new Quaternion();
        q.fromAngles(x, y, z);
        q.toRotationMatrix(out);
    }
    void convPMDEuler(Matrix3f out, float x, float y, float z) {
//        Matrix3f m = new Matrix3f();
//        m.loadIdentity();
//
//        out.fromAngleAxis(y, new Vector3f(0, 1, 0));
//        m.fromAngleAxis(z, new Vector3f(0, 0, 1));
//        out.multLocal(m);
//        m.loadIdentity();
//        m.fromAngleAxis(x, new Vector3f(1, 0, 0));
//        out.multLocal(m);

        Quaternion qx = new Quaternion();
        Quaternion qy = new Quaternion();
        Quaternion qz = new Quaternion();

        qx.fromAngles(x, 0, 0);
        qy.fromAngles(0, y, 0);
        qz.fromAngles(0, 0, z);

        qy.multLocal(qz);
        qy.multLocal(qx);

        qy.toRotationMatrix(out);
    }
    Vector3f convVec(javax.vecmath.Vector3f v) {
        return new Vector3f(v.x, v.y, v.z);
    }

    SixDofJoint createConstraint(PMDNode pmdNode, PMDRigidBody[] rigidBodyArray, PMDJoint pmdJoint) {
        PMDRigidBody rba = rigidBodyArray[pmdJoint.getRigidBodyA()];
        PMDRigidBody rbb = rigidBodyArray[pmdJoint.getRigidBodyB()];
        Matrix4f trans = new Matrix4f();
        trans.loadIdentity();
        Quaternion q = new Quaternion();
        Quaternion q3 = new Quaternion();

        Matrix3f m2 = new Matrix3f();
        m2.loadIdentity();
        convPMDEuler(m2, pmdJoint.getJointRot().x, pmdJoint.getJointRot().y, pmdJoint.getJointRot().z);
//        buf[0] = 0;//pmdJoint.getJointRot().x;
//        buf[1] = pmdJoint.getJointRot().y;
//        buf[2] = 0;//pmdJoint.getJointRot().z;
//        q.fromAngles(buf);
//        
//        buf[0] = 0;//pmdJoint.getJointRot().x;
//        buf[1] = 0;//pmdJoint.getJointRot().y;
//        buf[2] = pmdJoint.getJointRot().z;
//        q3.fromAngles(buf);
//        q.multLocal(q3);
//
//        buf[0] = pmdJoint.getJointRot().x;
//        buf[1] = 0;//pmdJoint.getJointRot().y;
//        buf[2] = 0;//pmdJoint.getJointRot().z;
//        q3.fromAngles(buf);
//        q.multLocal(q3);


        Matrix4f m = new Matrix4f();
        m.loadIdentity();
//        m.setRotationQuaternion(q);
        Vector3f v = new Vector3f(pmdJoint.getJointPos().x,
                pmdJoint.getJointPos().y,
                pmdJoint.getJointPos().z);
        Bone centerBone = pmdNode.getSkeleton().getBone("センター");
        m.setTransform(v, centerBone.getModelSpaceScale(), m2);
//        m.setRotationQuaternion(q);
        q = m.toRotationQuat();
//        Quat4f q2 = new Quat4f(q.getX(), q.getY(), q.getZ(), q.getW());
        trans.setRotationQuaternion(q);
        trans.setTranslation(pmdJoint.getJointPos().x,
                pmdJoint.getJointPos().y,
                pmdJoint.getJointPos().z);
//        v = m.toTranslationVector();
        Matrix4f transA = new Matrix4f();
        transA.loadIdentity();
//        rba.getWorldTransform(transA);
        transA.setRotationQuaternion(rba.getPhysicsRotation());
        transA.setTranslation(rba.getPhysicsLocation());
//        transA.inverse();
        transA.invertLocal();
        transA.multLocal(trans);
//        transA = transA.mult(trans);
        Matrix4f transB = new Matrix4f();
        transB.loadIdentity();
//        rbb.getWorldTransform(transB);
        transB.setTranslation(rbb.getPhysicsLocation());
        transB.setRotationQuaternion(rbb.getPhysicsRotation());
        transB.invertLocal();
        transB.multLocal(trans);
//        transB = transB.mult(trans);
//        Generic6DofSpringConstraint constraint = new Generic6DofSpringConstraint(rba, rbb, transA, transB, true);
//        SixDofSpringJoint constraint = new SixDofSpringJoint(rba, rbb,
//                transA.toTranslationVector(),
//                transB.toTranslationVector(),
//                transA.toRotationMatrix(),
//                transB.toRotationMatrix(),
//                true);

        Matrix4f transJ = new Matrix4f();
        transJ.loadIdentity();
        transJ.setTranslation(pmdJoint.getJointPos().x, pmdJoint.getJointPos().y, pmdJoint.getJointPos().z);
        convPMDEuler(m2, pmdJoint.getJointRot().x, pmdJoint.getJointRot().y, pmdJoint.getJointRot().z);
        q.fromRotationMatrix(m2);
//        q.fromAngles(pmdJoint.getJointRot().x, pmdJoint.getJointRot().y, pmdJoint.getJointRot().z);
        transJ.setRotationQuaternion(q);

        Matrix4f centerA = new Matrix4f();
        centerA.setRotationQuaternion(rba.getPhysicsRotation());
        centerA.setTranslation(rba.getPhysicsLocation());
        Matrix4f invCenterA = centerA.invert();
        
        Matrix4f centerB = new Matrix4f();
        centerB.setRotationQuaternion(rbb.getPhysicsRotation());
        centerB.setTranslation(rbb.getPhysicsLocation());
        Matrix4f invCenterB = centerB.invert();
        
        Matrix4f frameInA = invCenterA.mult(transJ);
        Matrix4f frameInB = invCenterB.mult(transJ);
        
        SixDofSpringJoint constraint = new SixDofSpringJoint(rba, rbb,
                frameInA.toTranslationVector(),
                frameInB.toTranslationVector(),
                frameInA.toRotationMatrix(),
                frameInB.toRotationMatrix(),
                true);
//        Generic6DofConstraint constraint = new Generic6DofConstraint(rba, rbb, transA, transB, true);
        constraint.setLinearLowerLimit(convVec(pmdJoint.getConstPos1()));
        constraint.setLinearUpperLimit(convVec(pmdJoint.getConstPos2()));
        Vector3f constRot1 = convVec(pmdJoint.getConstRot1());
        if (constRot1.getX() <= -FastMath.PI / 1.0f) {
            constRot1.setX(-FastMath.PI * 1f);
            System.out.println("constRot1 x must > -90");
        }
        if (constRot1.getY() <= -FastMath.PI / 0.5f) {
            constRot1.setY(-FastMath.PI * 0.5f);
            System.out.println("constRot1 y must > -90");
        }
        if (constRot1.getZ() <= -FastMath.PI / 1.0f) {
            constRot1.setZ(-FastMath.PI * 1f);
            System.out.println("constRot1 z must > -90");
        }
        constraint.setAngularLowerLimit(constRot1);

        Vector3f constRot2 = convVec(pmdJoint.getConstRot2());
        if (constRot2.getX() >= FastMath.PI / 1.0f) {
            constRot2.setX(FastMath.PI * 1f);
            System.out.println("constRot2 x must < 90");
        }
        if (constRot2.getY() >= FastMath.PI / 0.5f) {
            constRot2.setY(FastMath.PI * 0.5f);
            System.out.println("constRot2 y must < 90");
        }
        if (constRot2.getZ() >= FastMath.PI / 1.0f) {
            constRot2.setZ(FastMath.PI * 1f);
            System.out.println("constRot2 z must < 90");
        }

        constraint.setAngularUpperLimit(constRot2);
        constraint.setEquilibriumPoint();
//        constraint.setCollisionBetweenLinkedBodys(false);
        for (int i = 0; i < 6; i++) {
            float f = pmdJoint.getStiffness()[i];
            if (i >= 3 || f != 0f) {
                constraint.enableSpring(i, true);
                constraint.setStiffness(i, f);
            }
        }
        return constraint;
    }

    public void updateKinematicPos() {
        for (PMDRigidBody rbarray[] : rigidBodyMap.values()) {
            for (int i = 0; i < rbarray.length; i++) {
                PMDRigidBody rb = rbarray[i];
                PMDNode pmdNode = rb.getPmdNode();
                if (rb.isKinematic()) {
                    rb.updateFromBoneMatrix();
//                        Bone bone = rb.getBone();
//                        t.setIdentity();
//                        Vector3f v = bone.getModelSpacePosition();
//                        t.origin.set(v.x,v.y,v.z);
//                        Quaternion q = bone.getModelSpaceRotation();
//                        rot.set(q.getX(), q.getY(), q.getZ(), q.getW());
//                        t.setRotation(rot);
//                        t.mul(rb.getTrans());
//                        rb.setCenterOfMassTransform(t);
                }
                Node rigidBodyNode = pmdNode.getRigidBodyNode();
                if (rigidBodyNode != null) {
//                        rb.getCenterOfMassTransform(t);
//                        t.getRotation(rot);
//                        rot2.set(rot.x, rot.y, rot.z, rot.w);
                    Spatial spaital = rigidBodyNode.getChild(i);
                    spaital.setLocalRotation(rb.getPhysicsRotation());
                    spaital.setLocalTranslation(rb.getPhysicsLocation());
                }
            }
        }
    }

    void stepSimulation(float timeStep) {
//        setKinematicPos();
//        float time = timeStep;
//        for(;time>0;time-=accuracy) {
//            physicsSpace.update(accuracy, 2);
//            applyResultToBone();
//        }
//        if (time != 0) {
//            physicsSpace.update(time + accuracy, 2);
//            applyResultToBone();
//        }
        physicsSpace.update(timeStep, 10);
//        applyResultToBone();
    }
    Transform t = new Transform();
    Quaternion rot2 = new Quaternion();
    Quat4f rot = new Quat4f();

    public void applyResultToBone() {
//        for(int i=0;i<btWorld.getNumCollisionObjects();i++) {
//            CollisionObject obj = btWorld.getCollisionObjectArray().getQuick(i);
//            if (obj instanceof PMDRigidBody) {
//                PMDRigidBody rb = (PMDRigidBody)obj;
        for (PMDRigidBody rbarray[] : rigidBodyMap.values()) {
            for (int i = 0; i < rbarray.length; i++) {
                PMDRigidBody rb = rbarray[i];
                if (/*
                         * !rb.getBone().getName().equals("センター") &&
                         */rb.getBone() != null && !rb.isKinematic()) {
//                    Bone bone = rb.getBone();
//                    rb.getBoneTrans(t);
////                        rb.getCenterOfMassTransform(t);
//                    t.getRotation(rot);
//                    bone.getModelSpaceRotation().set(rot.x, rot.y, rot.z, rot.w);
//                    bone.getModelSpacePosition().set(t.origin.x, t.origin.y, t.origin.z);
////                    bone.getModelSpacePosition().set(0f,0f,0f);
                    PMDNode pmdNode = rb.getPmdNode();
                    Node rigidBodyNode = pmdNode.getRigidBodyNode();
                    rb.updateToBoneMatrix();
                    if (rigidBodyNode != null) {
//                        rb.getCenterOfMassTransform(t);
//                        t.getRotation(rot);
//                        rot2.set(rot.x, rot.y, rot.z, rot.w);
                        Spatial spaital = rigidBodyNode.getChild(i);
                        spaital.setLocalRotation(rb.getPhysicsRotation());
                        spaital.setLocalTranslation(rb.getPhysicsLocation());
                    }
                } else {
                }
            }
        }
    }

    public void resetRigidBodyPos() {
        for (PMDRigidBody rbarray[] : rigidBodyMap.values()) {
            for (int i = 0; i < rbarray.length; i++) {
                PMDRigidBody rb = rbarray[i];
                if (true) {
                    PMDNode pmdNode = rb.getPmdNode();
                    Node rigidBodyNode = pmdNode.getRigidBodyNode();
                    rb.updateFromBoneMatrix();
                    rb.setLinearVelocity(Vector3f.ZERO);
                    if (rigidBodyNode != null) {
                        Spatial spaital = rigidBodyNode.getChild(i);
                        spaital.setLocalRotation(rb.getPhysicsRotation());
                        spaital.setLocalTranslation(rb.getPhysicsLocation());
                    }
                }
            }
        }
    }

    void updateJointPosition(PMDNode pmdNode) {
//        if (pmdNode.jointNode != null) {
//            int i = 0;
//            for (Generic6DofConstraint constraint : constraintMap.get(pmdNode)) {
//                Geometry geom = (Geometry) pmdNode.jointNode.getChild(i++);
//                if (geom.getName().equals("左髪6")) {
//                    Line line = (Line) geom.getMesh();
//                    updateJointPosition(constraint, line);
//                }
//            }
//        }
    }
    Transform t1 = new Transform();
    Transform t2 = new Transform();
    Vector3f v1 = new Vector3f();
    Vector3f v2 = new Vector3f();

    void updateJointPosition(SixDofJoint constraint, Line line) {
//        constraint.getCalculatedTransformA(t1);
//        constraint.getCalculatedTransformB(t2);
//        v1.set(t1.origin.x, t1.origin.y, t1.origin.z);
//        v2.set(t2.origin.x, t2.origin.y, t2.origin.z);
//        line.updatePoints(v1, v2);
//        line.setLineWidth(3f);
//        line.setPointSize(3f);
//        System.out.println("joint "+v1+" "+v2);
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
        physicsSpace.setAccuracy(accuracy);
    }

    public PhysicsSpace getPhysicsSpace() {
        return physicsSpace;
    }

}
