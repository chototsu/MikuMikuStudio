/*
 * Copyright (c) 2010-2014, Kazuhiko Kobayashi
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package projectkyoto.jme3.mmd.nativebullet;

import com.jme3.animation.Bone;
import com.jme3.animation.Skeleton;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import projectkyoto.jme3.mmd.PMDNode;
import projectkyoto.mmd.file.PMDModel;

/**
 *
 * @author kobayasi
 */
public class PhysicsControl extends AbstractControl {

    PMDNode pmdNode;
    PMDPhysicsWorld world;
    public PhysicsControl() {
        world = new PMDPhysicsWorld();
    }
    public PhysicsControl(PMDNode pmdNode) {
        this.pmdNode = pmdNode;
        world = new PMDPhysicsWorld();
        world.addPMDNode(pmdNode);
    }

    @Override
    protected void controlUpdate(float tpf) {
//        pmdNode.getSkeleton().updateWorldVectors();
//        world.updateJointPosition(pmdNode);
        world.stepSimulation(tpf);
//        world.applyResultToBone();
//        pmdNode.calcOffsetMatrices();
//        pmdNode.update();
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    PMDRigidBody rigidBodyArray[];

    void initRigidBodyArray() {
        Skeleton skeleton = pmdNode.getSkeleton();
        PMDModel pmdModel = pmdNode.getPmdModel();
        rigidBodyArray = new PMDRigidBody[pmdModel.getRigidBodyList().getRigidBodyArray().length];
        for (int i = 0; i < pmdModel.getRigidBodyList().getRigidBodyArray().length; i++) {
            projectkyoto.mmd.file.PMDRigidBody fileRigidBody =
                    pmdModel.getRigidBodyList().getRigidBodyArray()[i];
            Bone bone = skeleton.getBone(fileRigidBody.getRelBoneIndex());
            PMDRigidBody rb = createRigidBody(fileRigidBody, bone);
            rigidBodyArray[i] = rb;
        }
    }

    PMDRigidBody createRigidBody(projectkyoto.mmd.file.PMDRigidBody fileRigidBody, Bone bone) {
        return null;
    }

    void setKinematicPos() {
    }
//    void stepSimulation(float timeStep) {
//        setKinematicPos();
//        btWorld.stepSimulation(timeStep);
//        for(int i=0;i<btWorld.getNumCollisionObjects();i++) {
//            CollisionObject obj = btWorld.getCollisionObjectArray().getQuick(i);
//            if (obj instanceof PMDRigidBody) {
//                PMDRigidBody rb = (PMDRigidBody)obj;
//            }
//        }
//    }

    public PMDPhysicsWorld getWorld() {
        return world;
    }
}
