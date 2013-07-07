/*  Copyright (c) 2009-2011  Nagoya Institute of Technology          */
/*                           Department of Computer Science          */
/*                2011       Kazuhiko Kobayashi                      */
/*                                                                   */
/* All rights reserved.                                              */
/*                                                                   */
/* Redistribution and use in source and binary forms, with or        */
/* without modification, are permitted provided that the following   */
/* conditions are met:                                               */
/*                                                                   */
/* - Redistributions of source code must retain the above copyright  */
/*   notice, this list of conditions and the following disclaimer.   */
/* - Redistributions in binary form must reproduce the above         */
/*   copyright notice, this list of conditions and the following     */
/*   disclaimer in the documentation and/or other materials provided */
/*   with the distribution.                                          */
/* - Neither the name of the MMDLoaderJME3 project team nor the names of  */
/*   its contributors may be used to endorse or promote products     */
/*   derived from this software without specific prior written       */
/*   permission.                                                     */
/*                                                                   */
/* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND            */
/* CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,       */
/* INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF          */
/* MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE          */
/* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS */
/* BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,          */
/* EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED   */
/* TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,     */
/* DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON */
/* ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,   */
/* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY    */
/* OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE           */
/* POSSIBILITY OF SUCH DAMAGE.                                       */
/* ----------------------------------------------------------------- */


package projectkyoto.jme3.mmd.ik;

import com.jme3.animation.Bone;
import com.jme3.animation.Skeleton;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.util.ArrayList;
import projectkyoto.jme3.mmd.BoneUtil;
import projectkyoto.jme3.mmd.PMDNode;
import projectkyoto.mmd.file.PMDIKData;
import projectkyoto.mmd.file.PMDModel;

/**
 *
 * @author kobayasi
 */
public class IKControl extends AbstractControl{
    PMDNode pmdNode;
    int boneEnabled[];

    public IKControl(PMDNode pmdNode) {
        this.pmdNode = pmdNode;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        updateIKBoneRotation();
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    private static final float kMinDistance = 0.0001f;
    private static final float kMinAngle = 0.00000001f;
    private static final float kMinAxis = 0.0000001f;
    private static final float kMinRotSum = 0.002f;
    private static final float kMinRotation = 0.00001f;
    float buf[] = new float[3];
    Vector3f tmpV1 = new Vector3f();
    Vector3f tmpV2 = new Vector3f();
    Vector3f tmpV3 = new Vector3f();
    Vector3f tmpV4 = new Vector3f();
    Vector3f tmpV5 = new Vector3f();
    Matrix4f tmpM41 = new Matrix4f();
    Matrix3f tmpM31 = new Matrix3f();
    Quaternion tmpQ1 = new Quaternion();

    public void updateIKBoneRotation() {
        final PMDModel pmdModel = pmdNode.getPmdModel();
        final Skeleton skeleton = pmdNode.getSkeleton();
//        skeleton.updateWorldVectors();
        l1:
        for (PMDIKData ikData : pmdModel.getIkList().getPmdIKData()) {
            if (boneEnabled != null && boneEnabled[ikData.getIkBoneIndex()] != 1) {
                continue l1;
            }
            final Bone ikBone = skeleton.getBone(ikData.getIkBoneIndex());
            final Bone targetBone = skeleton.getBone(ikData.getIkTargetBoneIndex());
            final int iterations = ikData.getIterations();
            l2:
            for (int iterationCount = 0; iterationCount < iterations; iterationCount++) {
                final int ikChainLength = ikData.getIkChainLength();
                final int[] ikChildBoneIndex = ikData.getIkChildBoneIndex();
                l3:
                for (int boneCount = 0; boneCount < ikChainLength; boneCount++) {
                    Bone currentBone = skeleton.getBone(ikChildBoneIndex[boneCount]);
                    Vector3f effectorModelPos = tmpV1.set(targetBone.getModelSpacePosition());
                    Vector3f targetModelPos = tmpV2.set(ikBone.getModelSpacePosition());
                    boolean hizaFlag = pmdModel.getBoneList().getBones()[ikData.getIkChildBoneIndex()[boneCount]].isHiza();
                    if (hizaFlag) {
                        if (ikData.getIkChainLength() < 2) {
                            pmdModel.getBoneList().getBones()[ikData.getIkChildBoneIndex()[boneCount]].setHiza(false);
                        } else {
                            hizaIK(ikData);
                            break l2;
                        }
                    }
//                    hizaFlag = false;
//                    if (hizaFlag)
//                    System.out.println("ひざ");
//                    if (!calcIk(ikData, currentBone, effectorModelPos,
//                            targetModelPos, iterationCount, hizaFlag)) {
//                        break l2;
//                    }
                    {
                        Matrix4f m = BoneUtil.getModelToBoneMatrix(currentBone, tmpM41, tmpM31);
                        Vector3f targetBonePos = tmpV3;
                        m.mult(targetModelPos, targetBonePos);
                        Vector3f effectorBonePos = tmpV4;
                        m.mult(effectorModelPos, effectorBonePos);

                        if (targetBonePos.distanceSquared(effectorBonePos) < kMinDistance) {
//                            System.out.println("iteration = "+iterationCount);
                            break l2;
                        }
                        Quaternion rot = tmpQ1;
                        {
                            targetBonePos.normalizeLocal();
                            effectorBonePos.normalizeLocal();
                            float dot = targetBonePos.dot(effectorBonePos);
//        System.out.println("dot = " + dot);
                            if (dot > 1.0f) {
                                continue l3;
                            }
                            float angle = FastMath.acos(dot);
//                            System.out.println(currentBone.getName()+ " angle = "+angle);
                            if (FastMath.abs(angle) >= kMinAngle) {
//            System.out.println("angle = " + angle);
                                if (angle < -ikData.getControlWeight()) {
                                    angle = -ikData.getControlWeight();
                                } else if (angle > ikData.getControlWeight()) {
                                    angle = ikData.getControlWeight();
                                }

                                Vector3f axis = effectorBonePos.cross(targetBonePos, tmpV5);
//                                Vector3f axis = targetBonePos.cross(effectorBonePos);
                                if (axis.lengthSquared() < kMinAxis && iterationCount > 0) {
                                    continue l3;
                                }
                                axis.normalizeLocal();
//            System.out.println("axis = " + axis);
                                rot.fromAngleNormalAxis(angle, axis);
//            rot.normalize();
                                if (hizaFlag) {
                                    if (iterationCount == -1) {
                                        if (angle < 0.0f) {
                                            angle = -angle;
                                            rot.fromAngleAxis(angle, axis);
//                                            rot.toAngles(buf);
//                                            rot.fromAngles(buf[0],0f,0f);
                                        }
////                                        rot.fromAngles(FastMath.PI/2f,0f,0f);
//                                        currentBone.getLocalRotation().set(rot);
//                                        updateWorldVectors(currentBone);
//                                        continue;
//                                        rot.fromAngleAxis(angle, axis);
//                                        rot.toAngles(buf);
//                                        if (buf[0] < 0.0f) {
//                                            buf[0] = -buf[0];
//                                        }
//                                        currentBone.getLocalRotation().fromAngles(FastMath.PI*0.1f,0f,0f);

//                                        updateWorldVectors(currentBone);
//                                        continue;                                        
                                    } else {
                                        rot.toAngles(buf);
                                        float x = buf[0];
                                        Quaternion currentRot = currentBone.getLocalRotation();
                                        currentRot.toAngles(buf);
                                        float x2 = buf[0];
                                        if (x2 + x > FastMath.PI) {
                                            x = FastMath.PI - x2;
                                        }
                                        if (kMinRotSum > x + x2) {
                                            x = kMinRotSum - x2;
                                        }
                                        if (x + x2 < 0) {
                                            x = -(x + x2) - x2;
                                        } else if (x < -ikData.getControlWeight()) {
                                            x = -ikData.getControlWeight();
                                        } else if (x > ikData.getControlWeight()) {
                                            x = ikData.getControlWeight();
                                        }
                                        if (FastMath.abs(x) < kMinRotation) {
                                            continue l3;
                                        }
                                        rot.fromAngles(x, 0f, 0f);
                                    }
                                }
                            } else {
                                continue l3;
                            }
                        }
//                        if (hizaFlag) {
//                        rot.multLocal(currentBone.getLocalRotation());
                        currentBone.getLocalRotation().multLocal(rot);
//                        if (hizaFlag) {
//                            rot.toAngles(buf);
//                            if (buf[0] < 0f) {
//                                buf[0] = -buf[0];
//                            }
//                            rot.fromAngles(buf[0],0f,0f);
//                        }
//                        currentBone.getLocalRotation().set(rot);
//                        }
//                        skeleton.updateWorldVectors();
                    }
                    updateWorldVectors(currentBone);
                }
            }
        }
    }

    void hizaIK(PMDIKData ikData) {
        Skeleton skeleton = pmdNode.getSkeleton();
        Bone ikBone = skeleton.getBone(ikData.getIkBoneIndex());
        Bone targetBone = skeleton.getBone(ikData.getIkTargetBoneIndex());
        Bone hizaBone = skeleton.getBone(ikData.getIkChildBoneIndex()[0]);
        Bone ashiBone = skeleton.getBone(ikData.getIkChildBoneIndex()[1]);
        hizaBone.getLocalRotation().fromAngles(0f, 0f, 0f);
        updateWorldVectors(hizaBone);
        Vector3f effectorModelPos = tmpV1.set(targetBone.getModelSpacePosition());
        Vector3f targetModelPos = tmpV2.set(ikBone.getModelSpacePosition());
        float aSquared = hizaBone.getModelSpacePosition().distanceSquared(ashiBone.getModelSpacePosition());
        float bSquared = hizaBone.getModelSpacePosition().distanceSquared(effectorModelPos);
        float cSquared = targetModelPos.distanceSquared(ashiBone.getModelSpacePosition());

        float a = FastMath.sqrt(aSquared);
        float b = FastMath.sqrt(bSquared);
        float c = FastMath.sqrt(cSquared);
//        System.out.println("targetBone = "+targetBone.getName()+" ikBone = "+ikBone.getName()+" ashiBone = "+ashiBone.getName()+" a = "+ a + " b = "+b + " c = "+c);
        if (a + b < c) {
//            System.out.println("hizaIK case 1");
            hizaBone.getLocalRotation().toAngles(buf);
            hizaBone.getLocalRotation().fromAngles(0f, buf[1], buf[2]);
            updateWorldVectors(hizaBone);

            effectorModelPos = tmpV1.set(targetBone.getModelSpacePosition());
            Matrix4f m = BoneUtil.getModelToBoneMatrix(ashiBone, tmpM41, tmpM31);
            Vector3f targetBonePos = tmpV3;
            m.mult(targetModelPos, targetBonePos);
            Vector3f effectorBonePos = tmpV4;
            m.mult(effectorModelPos, effectorBonePos);
            if (targetBonePos.distanceSquared(effectorBonePos) < kMinDistance) {
                return;
            }
            targetBonePos.normalizeLocal();
            effectorBonePos.normalizeLocal();
            float dot = effectorBonePos.dot(targetBonePos);
            float angle = FastMath.acos(dot);
            Vector3f axis = effectorBonePos.cross(targetBonePos, tmpV5);
            axis.normalizeLocal();
            Quaternion rot = tmpQ1;
            rot.fromAngleAxis(angle, axis);
//            rot.multLocal(ashiBone.getLocalRotation());
//            ashiBone.getLocalRotation().set(rot);
            ashiBone.getLocalRotation().multLocal(rot);
            updateWorldVectors(ashiBone);
        } else if (FastMath.abs(a - b) < c) {
//            System.out.println("hizaIK case 2");
            if (true) {
                Quaternion rot = tmpQ1;
                float angle = FastMath.PI - FastMath.acos((cSquared - aSquared - bSquared) / (-2f * a * b));
//                System.out.println("angle = "+(angle / FastMath.PI * 180));
                rot.fromAngles(angle, 0f, 0f);
                hizaBone.getLocalRotation().set(rot);
                updateWorldVectors(hizaBone);
            }
            {
                effectorModelPos = tmpV1.set(targetBone.getModelSpacePosition());
                Matrix4f m = BoneUtil.getModelToBoneMatrix(ashiBone, tmpM41, tmpM31);
                Vector3f targetBonePos = tmpV3;
                //
                m.mult(ashiBone.getModelSpacePosition(), targetBonePos);
//                System.out.println("ashi model = "+targetBonePos);
                //

                m.mult(targetModelPos, targetBonePos);
                Vector3f effectorBonePos = tmpV4;
                m.mult(effectorModelPos, effectorBonePos);
                if (targetBonePos.distanceSquared(effectorBonePos) < kMinDistance) {
                    return;
                }
                targetBonePos.normalizeLocal();
                effectorBonePos.normalizeLocal();
                float dot = effectorBonePos.dot(targetBonePos);
                float angle = FastMath.acos(dot);
                Vector3f axis = effectorBonePos.cross(targetBonePos, tmpV5);
                axis.normalizeLocal();
                Quaternion rot = tmpQ1;
                rot.fromAngleNormalAxis(angle, axis);
//                rot.multLocal(ashiBone.getLocalRotation());
//                ashiBone.getLocalRotation().set(rot);
                ashiBone.getLocalRotation().multLocal(rot);
                updateWorldVectors(ashiBone);
            }
        } else {
//            System.out.println("hizaIK case 3");
            {
                Quaternion rot = tmpQ1;
                float angle = 0;
                rot.fromAngles(angle, 0f, 0f);
                hizaBone.getLocalRotation().set(rot);
                updateWorldVectors(hizaBone);
            }
            {
                effectorModelPos = tmpV1.set(targetBone.getModelSpacePosition());
                Matrix4f m = BoneUtil.getModelToBoneMatrix(ashiBone, tmpM41, tmpM31);
                Vector3f targetBonePos = tmpV3;
                m.mult(targetModelPos, targetBonePos);
                Vector3f effectorBonePos = tmpV4;
                m.mult(effectorModelPos, effectorBonePos);
                if (targetBonePos.distanceSquared(effectorBonePos) < kMinDistance) {
                    return;
                }
                targetBonePos.normalizeLocal();
                effectorBonePos.normalizeLocal();
                float dot = effectorBonePos.dot(targetBonePos);
                float angle = FastMath.acos(dot);
                Vector3f axis = effectorBonePos.cross(targetBonePos, tmpV5);
                axis.normalizeLocal();
                Quaternion rot = tmpQ1;
                rot.fromAngleAxis(angle, axis);
//                rot.multLocal(ashiBone.getLocalRotation());
//                ashiBone.getLocalRotation().set(rot);
                ashiBone.getLocalRotation().multLocal(rot);
                updateWorldVectors(ashiBone);
            }
        }
    }

    void updateWorldVectors(Bone bone) {
//        pmdNode.getSkeleton().updateWorldVectors();
        bone.updateWorldVectors();
        final ArrayList<Bone> children = bone.getChildren();
        final int size = children.size();
        for(int i=0;i<size;i++) {
            Bone childBone = children.get(i);
            updateWorldVectors(childBone);
        }
    }

    public int[] getBoneEnabled() {
        return boneEnabled;
    }

    public void setBoneEnabled(int[] boneEnabled) {
        this.boneEnabled = boneEnabled;
    }
    
}
