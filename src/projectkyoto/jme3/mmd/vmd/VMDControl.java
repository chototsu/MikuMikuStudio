/*
 * Copyright (c) 2011 Kazuhiko Kobayashi All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p/>
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
package projectkyoto.jme3.mmd.vmd;

import com.jme3.animation.Bone;
import com.jme3.animation.Skeleton;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import projectkyoto.jme3.mmd.PMDNode;
import projectkyoto.jme3.mmd.Skin;
import projectkyoto.jme3.mmd.ik.IKControl;
import projectkyoto.jme3.mmd.nativebullet.PhysicsControl;
import projectkyoto.mmd.file.PMDBone;
import projectkyoto.mmd.file.PMDIKData;
import projectkyoto.mmd.file.VMDFile;
import projectkyoto.mmd.file.VMDMotion;
import projectkyoto.mmd.file.VMDSkin;


/**
 *
 * @author kobayasi
 */
public class VMDControl extends AbstractControl {

    final PMDNode pmdNode;
    final VMDFile vmdFile;
    int currentFrameNo = 0;
    final Map<String, BoneMotionList> motionMap = new HashMap<String, BoneMotionList>();
    BoneMotionList[] boneMotionListArray;
    final Map<String, SkinList> skinMap = new HashMap<String, SkinList>();
    SkinList[] skinListArray;
    int lastFrameNo = 0;
    boolean pause = false;
    static final VMDMotionComparator vmc = new VMDMotionComparator();
    static final VMDSkinComparator vmsc = new VMDSkinComparator();
    final PhysicsControl physicsControl;
    int boneEnabled[];
    final IKControl ikControl;
    TickListener tl = new TickListener();
    float currentTime = 0f;

    public VMDControl(PMDNode pmdNode, VMDFile vmdFile) {
        this.pmdNode = pmdNode;
        this.vmdFile = vmdFile;
        initMotionMap();
        physicsControl = new PhysicsControl(pmdNode);
//         physicsControl.getWorld().getPhysicsSpace().addTickListener(tl);
        ikControl = new IKControl(pmdNode);
        boneEnabled = new int[pmdNode.getSkeleton().getBoneCount()];
        for(int i=0;i<boneEnabled.length;i++) {
            boneEnabled[i] = 1;
        }
        for (PMDIKData ikData : pmdNode.getPmdModel().getIkList().getPmdIKData()) {
            int targetBoneIndex = ikData.getIkTargetBoneIndex();
            for(projectkyoto.mmd.file.PMDRigidBody rb : pmdNode.getPmdModel().getRigidBodyList().getRigidBodyArray()) {
                if (rb.getRelBoneIndex() == targetBoneIndex && rb.getRigidBodyType() != 0) {
                    boneEnabled[targetBoneIndex] = 0;
                    break;
                }
            }
        }
        ikControl.setBoneEnabled(boneEnabled);
        for(int i=pmdNode.getSkeleton().getBoneCount()-1;i>=0;i--) {
            if (boneEnabled[i] == 1) {
                Bone bone = pmdNode.getSkeleton().getBone(i);
                bone.setUserControl(true);
            }
        }
    }

    private void initMotionMap() {
        for (VMDMotion m : vmdFile.getMotionArray()) {
            BoneMotionList motionList = motionMap.get(m.getBoneName());
            if (motionList == null) {
                motionList = new BoneMotionList();
                motionList.boneName = m.getBoneName();
                for(int i=0;i<pmdNode.getSkeleton().getBoneCount();i++) {
                    if (pmdNode.getSkeleton().getBone(i).getName().equals(m.getBoneName())) {
                        motionList.boneIndex = i;
                        break;
                    }
                }
                motionMap.put(m.getBoneName(), motionList);
            }
            motionList.add(m);
            if (m.getFrameNo() > lastFrameNo) {
                lastFrameNo = m.getFrameNo();
            }
        }
        Iterator<BoneMotionList> it = motionMap.values().iterator();
        while (it.hasNext()) {
            BoneMotionList ml = it.next();
            for (PMDBone pmdBone : pmdNode.getPmdModel().getBoneList().getBones()) {
                if (pmdBone.getBoneName().equals(ml.boneName)) {
                    ml.boneType = pmdBone.getBoneType();
                }
            }
        }
        while (it.hasNext()) {
            BoneMotionList ml = it.next();
            for (PMDBone pmdBone : pmdNode.getPmdModel().getBoneList().getBones()) {
                if (pmdBone.getBoneName().equals(ml.boneName)) {
                }
            }
        }
        for (BoneMotionList ml : motionMap.values()) {
            Collections.sort(ml, vmc);
            ml.setCurrentCount(0);
        }
        for (VMDSkin skin : vmdFile.getSkinArray()) {
            SkinList skinList = skinMap.get(skin.getSkinName());
            if (skinList == null) {
                skinList = new SkinList();
                skinList.skinName = skin.getSkinName();
                skinList.skin = pmdNode.getSkinMap().get(skin.getSkinName());
                skinMap.put(skinList.skinName, skinList);
            }
            skinList.add(skin);
            if (skin.getFlameNo() > lastFrameNo) {
                lastFrameNo = skin.getFlameNo();
            }
        }
        for (SkinList skinList : skinMap.values()) {
            Collections.sort(skinList, vmsc);
        }
        boneMotionListArray = motionMap.values().toArray(new BoneMotionList[motionMap.size()]);
        skinListArray = skinMap.values().toArray(new SkinList[skinMap.size()]);
    }
    Quat4f tmpq1 = new Quat4f();
    Quat4f tmpq2 = new Quat4f();
    Point3f tmpp1 = new Point3f();
    Point3f tmpp2 = new Point3f();
    float prevTpf = 0;
//    float stepTime = 1f/30f;
//    @Override
//    protected void controlUpdate(float tpf) {
//        float time = tpf;
////        for(;time > 0; time -= physicsControl.getWorld().accuracy) {
////            controlUpdate2(physicsControl.getWorld().accuracy);
////        }
//        if (time != 0 && !pause) {
////            controlUpdate2(time);
//        physicsControl.update(tpf);
////            physicsControl.getWorld().applyResultToBone();
//        }
//    }
//
//    protected void controlUpdate2(float tpf) {
//        if (!pause) {
//            if (currentFrameNo < lastFrameNo) {
//                timeFromCurrentFrameNo += tpf;
//                if (timeFromCurrentFrameNo >= 1f / 30) {
//                    int i = (int) (timeFromCurrentFrameNo * 30f);
//                    currentFrameNo += i;
//                    timeFromCurrentFrameNo -= (float) i / 30f;
//                }
//            }
//            calcBonePosition(currentFrameNo, pmdNode.getSkeleton());
//            physicsControl.getWorld().updateKinematicPos();
////            pmdNode.getSkeleton().updateWorldVectors();
//            // physicsControl.update(tpf);
//
////            timeFromCurrentFrameNo = 0;
////            i++;
////            if (i > 30) {
////            currentFrameNo++;
////            i=0;
////            }
////            System.out.println("currentFrameNo = "+currentFrameNo);
////            calcBonePosition(currentFrameNo, pmdNode.getSkeleton());
////            if (i == 0)
////            physicsControl.update(1f/30);
//        } else {
////            pmdNode.update();
//        }
//    }
    float accuracy = 1f/120f;
    @Override
    protected void controlUpdate(float tpf) {
//        for(;time > 0; time -= physicsControl.getWorld().accuracy) {
//            controlUpdate2(physicsControl.getWorld().accuracy);
//        }
//        tpf = stepTime;
//        tpf = 1f/15f;
        boolean needUpdateSkin = true;
        if (tpf != 0 && !pause) {
            tpf += prevTpf;
            for(;tpf > accuracy ; tpf -= accuracy ) {
                controlUpdate2(accuracy);
                physicsControl.update(accuracy);
                needUpdateSkin = true;
            }
            physicsControl.getWorld().applyResultToBone();
            prevTpf = tpf;
            if (needUpdateSkin) {
//                resetSkins();
                calcSkins();
            }
        }
    }

    protected void controlUpdate2(float tpf) {
        if (!pause) {
            currentTime += tpf;
            currentFrameNo = (int)(currentTime * 30f);
            calcBonePosition();
            physicsControl.getWorld().updateKinematicPos();
//            pmdNode.getSkeleton().updateWorldVectors();
            // physicsControl.update(tpf);

//            timeFromCurrentFrameNo = 0;
//            i++;
//            if (i > 30) {
//            currentFrameNo++;
//            i=0;
//            }
//            System.out.println("currentFrameNo = "+currentFrameNo);
//            calcBonePosition(currentFrameNo, pmdNode.getSkeleton());
//            if (i == 0)
//            physicsControl.update(1f/30);
        } else {
//            pmdNode.update();
        }
    }

    void calcBonePosition() {
        for(int i=pmdNode.getSkeleton().getBoneCount()-1;i>=0;i--) {
            if (boneEnabled[i] == 1) {
                Bone bone = pmdNode.getSkeleton().getBone(i);
                bone.getLocalRotation().loadIdentity();
            }
        }
        boneLoop:
        for (BoneMotionList bml : boneMotionListArray) {
            if (bml.size() == 0) {
                continue;
            }
            Bone bone = pmdNode.getSkeleton().getBone(bml.boneIndex);
            if (bone != null && boneEnabled[bml.boneIndex] == 1) {
                if (bml.size() - 1 < bml.currentCount) {
                    VMDMotion m1 = bml.get(bml.size() - 1);
                    Quat4f q = m1.getRotation();
                    if (true || bml.boneType == 0 || bml.boneType == 2 || bml.boneType == 1 || bml.boneType == 4) {
                        bone.getLocalRotation().set(q.x, q.y, q.z, q.w);
                    }
                    Point3f p = m1.getLocation();
                    Vector3f v = bone.getWorldBindPosition();
                    if (true || bml.boneType == 1 || bml.boneType == 2) {
                        bone.getLocalPosition().set(v.x + p.x, v.y + p.y, v.z + p.z);
                    }
                } else {
                    VMDMotion m1, m2;
                    int count = bml.currentCount;
                    for (;;) {
                        VMDMotion m = bml.m2;
                        if (m.getFrameNo() > currentFrameNo) {
                            m1 = bml.m1;
                            m2 = bml.m2;
                            break;
                        }
                        if (count >= bml.size()) {
                            m1 = bml.m1;
                            m2 = bml.m2;
                            break;
                        }
                        count++;
                        bml.setCurrentCount(count);
                    }
                    if (m1.getFrameNo() > currentFrameNo) {
                        tmpq1.set(m1.getRotation());
                        tmpp1.set(m1.getLocation());
                        continue;
                    }
                    if (m2.getFrameNo() == m1.getFrameNo()) {
                        tmpq1.set(m1.getRotation());
                        tmpp1.set(m1.getLocation());
                    } else {
                        float f = (float) (m2.getFrameNo() - m1.getFrameNo()) * 1f / 30f;
                        assert f >= 0;
//                        float f2 = (float) (currentFrameNo - m1.getFrameNo()) * 1f / 30 + timeFromCurrentFrameNo;
                        float f2 = currentTime - m1.getFrameNo() / 30f;
                        assert (f2 >= 0);
                        float f3 = f2 / f;
                        float fx = IPUtil.calcIp(bml, f3, 0);//calcIp(m2.getInterpolation(), f3, 0);
                        float fy = IPUtil.calcIp(bml, f3, 1); //calcIp(m2.getInterpolation(), f3, 1);
                        float fz = IPUtil.calcIp(bml, f3, 2); //calcIp(m2.getInterpolation(), f3, 2);
                        float fr = IPUtil.calcIp(bml, f3, 3); //calcIp(m2.getInterpolation(), f3, 3);
                        tmpq1.interpolate(m1.getRotation(), m2.getRotation(), fr);
                        tmpp1.x = m1.getLocation().x + (m2.getLocation().x - m1.getLocation().x) * fx;
                        tmpp1.y = m1.getLocation().y + (m2.getLocation().y - m1.getLocation().y) * fy;
                        tmpp1.z = m1.getLocation().z + (m2.getLocation().z - m1.getLocation().z) * fz;
                    }
                    if (true || bml.boneType == 0 || bml.boneType == 2 || bml.boneType == 1 || bml.boneType == 4) {
                        bone.getLocalRotation().set(tmpq1.x, tmpq1.y, tmpq1.z, tmpq1.w);
                    }
                    Point3f p = tmpp1;
                    Vector3f v = bone.getWorldBindPosition();
                    if (true || bml.boneType == 1 || bml.boneType == 2) {
                        bone.getLocalPosition().set(v.x + p.x, v.y + p.y, v.z + p.z);
                    }
                }
            }
        }
        pmdNode.getSkeleton().updateWorldVectors();
        ikControl.updateIKBoneRotation();
        for(int i=0;i<pmdNode.getPmdModel().getBoneList().getBoneCount();i++) {
            PMDBone pmdBone = pmdNode.getPmdModel().getBoneList().getBones()[i];
            if (pmdBone.getBoneType() == 5 && boneEnabled[i] == 1) {
                // under-rotation
                Bone bone = pmdNode.getSkeleton().getBone(i);
//                if (motionMap.get(pmdBone.getBoneName()) == null) {
//                    bone.getLocalRotation().loadIdentity();
//                }
                Bone targetBone = pmdNode.getSkeleton().getBone(pmdBone.getTargetBone());
                bone.getLocalRotation().multLocal(targetBone.getLocalRotation());
                bone.updateWorldVectors();
            }
        }
//        calcSkins(currentFrameNo);
    }
    public void calcSkins() {
        for (SkinList skinList : skinListArray) {
            float w1 = 0f, w2 = 0f;
            int c1 = 0, c2 = 0;
            int skinListSize = skinList.size();
            for (; skinList.currentCount < skinListSize; skinList.currentCount++) {
                VMDSkin skin = skinList.get(skinList.currentCount);
                if (skin.getFlameNo() > currentFrameNo) {
                    w2 = skin.getWeight();
                    c2 = skin.getFlameNo();
                    if (skinList.currentCount > 0) {
                        skin = skinList.get(skinList.currentCount - 1);
                        w1 = skin.getWeight();
                        c1 = skin.getFlameNo();
                    } else {
                        w1 = 0f;
                        c1 = 0;
                    }
                    break;
                }
            }
            float weight;
            if (skinList.currentCount == skinListSize) {
                weight = skinList.get(skinList.currentCount-1).getWeight();
            } else {
                float f1 = ((float) (c2 - c1)) * 1f / 30f;
//                float f2 = ((float) (currentFrameNo - c1)) * 1f / 30f;
                float f2 = currentTime - (float)c1 / 30f;
                if (f1 > 0) {
                    weight = w1 + (w2 - w1) * f2 / f1;
                } else {
                    weight = w2;
                }
            }
//            pmdNode.setSkinWeight(skinList.skinName, weight);
            if (skinList.skin != null) {
                skinList.skin.setWeight(weight);
            }
        }
    }        
    public void resetSkins() {
        for (String skinName : pmdNode.getSkinSet()) {
            pmdNode.setSkinWeight(skinName, 0f);
        }
    }

    public void setFrameNo(int frameNo) {
        for (BoneMotionList bml : boneMotionListArray) {
            int count = bml.size() - 1;
            for (int i = 0; i < bml.size(); i++) {
                VMDMotion m = bml.get(i);
                if (m.getFrameNo() > frameNo) {
                    count = i;
                    break;
                }
            }
            bml.setCurrentCount(count);
        }
        currentFrameNo = frameNo;
        currentTime = frameNo / 30f;
        calcBonePosition();
        for (SkinList skinList : skinListArray) {
            skinList.currentCount = skinList.size() - 1;
            for (int i = 0; i <skinList.size(); i++) {
                VMDSkin skin = skinList.get(i);
                if (skin.getFlameNo() > frameNo) {
                    skinList.currentCount = i;
                    break;
                }
            }
        }
        calcSkins();
//        resetSkins();
//        calcBonePosition(currentFrameNo, pmdNode.getSkeleton());
        physicsControl.getWorld().resetRigidBodyPos();
//        pmdNode.update();
    }

    public int getFrameNo() {
        return currentFrameNo;
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public int getLastFrameNo() {
        return lastFrameNo;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    /* ipfunc: t->value for 4-point (3-dim.) bezier curve */

    public PhysicsControl getPhysicsControl() {
        return physicsControl;
    }
    class TickListener implements PhysicsTickListener {

        @Override
        public void prePhysicsTick(PhysicsSpace ps, float f) {
            controlUpdate2(f);
        }

        @Override
        public void physicsTick(PhysicsSpace ps, float f) {
            physicsControl.getWorld().applyResultToBone();
        }
        
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
        physicsControl.getWorld().setAccuracy(accuracy);
    }
    
}
class BoneMotionList extends ArrayList<VMDMotion> {

    static final int IPTABLESIZE = 64;
    String boneName;
    int boneIndex;
    int currentCount;
    VMDMotion m1,m2;
    int boneType;
    final float ipTable[][][] = new float[4][IPTABLESIZE][2];
    int ipTableIndex = 0;
    int frame1, frame2;
    final float val1[] = new float[4];
    final float val2[] = new float[4];

    void setCurrentCount(int newCount) {
        if (currentCount != newCount) {
            currentCount = newCount;
            if (newCount >= 0 && newCount < size()) {
                IPUtil.createInterpolationTable(get(currentCount).getInterpolation(), ipTable);
            }
        }
        ipTableIndex = 0;
        if (newCount == 0) {
            m1 = get(0);
            m2 = m1;
        } else if (newCount < size()) {
            m1 = get(newCount-1);
            m2 = get(newCount);
        } else if (size() > 0) {
            m1 = get(size()-1);
            m2 = m1;
        } else {
            m1 = null;
            m2 = null;
        }
    }
}

class VMDMotionComparator implements Comparator<VMDMotion> {

    @Override
    public int compare(VMDMotion o1, VMDMotion o2) {
        if (o1.getFrameNo() < o2.getFrameNo()) {
            return -1;
        } else if (o1.getFrameNo() < o2.getFrameNo()) {
            return 0;
        } else {
            return 1;
        }
    }
}

class SkinList extends ArrayList<VMDSkin> {

    String skinName;
    Skin skin; 
    int currentCount;
}

class VMDSkinComparator implements Comparator<VMDSkin> {

    @Override
    public int compare(VMDSkin o1, VMDSkin o2) {
        if (o1.getFlameNo() < o2.getFlameNo()) {
            return -1;
        } else if (o1.getFlameNo() < o2.getFlameNo()) {
            return 0;
        } else {
            return 1;
        }
    }
}
