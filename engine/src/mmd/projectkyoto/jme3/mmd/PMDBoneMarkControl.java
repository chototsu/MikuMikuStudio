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

package projectkyoto.jme3.mmd;

import com.jme3.math.Matrix4f;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import projectkyoto.mmd.file.PMDBone;

/**
 *
 * @author kobayasi
 */
public class PMDBoneMarkControl extends AbstractControl {

    SkeletonControl skeletonControl;
    Spatial[] boneMarkArray;
    PMDBone[] boneArray;
    Camera cam;

    public PMDBoneMarkControl(SkeletonControl skeletonControl, Spatial[] boneMarkArray, PMDBone[] boneArray) {
        this.skeletonControl = skeletonControl;
        this.boneMarkArray = boneMarkArray;
        this.boneArray = boneArray;
    }

    @Override
    protected void controlUpdate(float tpf) {
        for (int i = 0; i < boneMarkArray.length; i++) {
            Matrix4f m = skeletonControl.getOffsetMatrices()[i].clone();
            PMDBone bone = boneArray[i];
//            if (bone.getBoneName().equals("右腕")) {
            //            Matrix4f m2 = boneMarkArray[i].getLocalToWorldMatrix(new Matrix4f()).clone();
            //            m.invertLocal();
            //            m.loadIdentity();
            Vector3f bonePos = new Vector3f(bone.getBoneHeadPos().x,
                    bone.getBoneHeadPos().y,
                    bone.getBoneHeadPos().z);
//                System.out.println("projectionMatrix = "+projectionMatrix);
//                System.out.println("bonePos1 = "+bonePos);
            m.mult(bonePos, bonePos);
//                System.out.println("bonePos2 = "+bonePos);
            Transform t = new Transform(m.toTranslationVector());
            cam.getScreenCoordinates(bonePos, bonePos);
//                System.out.println("bonePos3 = "+bonePos);
            t.setTranslation(bonePos);
            boneMarkArray[i].setLocalTransform(t);
//            }            
//            System.out.println("m2 = "+m2);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Camera getCam() {
        return cam;
    }

    public void setCam(Camera cam) {
        this.cam = cam;
    }
}
