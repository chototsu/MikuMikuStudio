/*
 * Copyright (c) 2011 Kazuhiko Kobayashi All rights reserved. <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. <p/> *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution. <p/> * Neither the
 * name of 'MMDLoaderJME' nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific
 * prior written permission. <p/> THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package projectkyoto.jme3.mmd.vmd;

import com.jme3.animation.Bone;
import com.jme3.animation.Skeleton;
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
import projectkyoto.jme3.mmd.ik.IKControl;
import projectkyoto.jme3.mmd.nativebullet.PhysicsControl;
import projectkyoto.mmd.file.PMDBone;
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
    float timeFromCurrentFrameNo = 0f;
    final Map<String, BoneMotionList> motionMap = new HashMap<String, BoneMotionList>();
    final Map<String, SkinList> skinMap = new HashMap<String, SkinList>();
    int lastFrameNo = 0;
    boolean pause = false;
    static final VMDMotionComparator vmc = new VMDMotionComparator();
    static final VMDSkinComparator vmsc = new VMDSkinComparator();
    final PhysicsControl physicsControl;
    final IKControl ikControl;

    public VMDControl(PMDNode pmdNode, VMDFile vmdFile) {
        this.pmdNode = pmdNode;
        this.vmdFile = vmdFile;
        initMotionMap();
        physicsControl = new PhysicsControl(pmdNode);
        ikControl = new IKControl(pmdNode);
    }

    private void initMotionMap() {
        for (VMDMotion m : vmdFile.getMotionArray()) {
            BoneMotionList motionList = motionMap.get(m.getBoneName());
            if (motionList == null) {
                motionList = new BoneMotionList();
                motionList.boneName = m.getBoneName();
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
        }
        for (VMDSkin skin : vmdFile.getSkinArray()) {
            SkinList skinList = skinMap.get(skin.getSkinName());
            if (skinList == null) {
                skinList = new SkinList();
                skinList.skinName = skin.getSkinName();
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
    }
    Quat4f tmpq1 = new Quat4f();
    Quat4f tmpq2 = new Quat4f();
    Point3f tmpp1 = new Point3f();
    Point3f tmpp2 = new Point3f();
    int i = 0;

    @Override
    protected void controlUpdate(float tpf) {
        float time = tpf;
//        for(;time > 0; time -= physicsControl.getWorld().accuracy) {
//            controlUpdate2(physicsControl.getWorld().accuracy);
//        }
        if (time != 0) {
            controlUpdate2(time);
        }
    }

    protected void controlUpdate2(float tpf) {
        if (!pause) {
            if (currentFrameNo < lastFrameNo) {
                timeFromCurrentFrameNo += tpf;
                if (timeFromCurrentFrameNo >= 1f / 30) {
                    int i = (int) (timeFromCurrentFrameNo * 30f);
                    currentFrameNo += i;
                    timeFromCurrentFrameNo -= (float) i / 30f;
                }
            }
            calcBonePosition(currentFrameNo, pmdNode.getSkeleton());
            physicsControl.update(tpf);

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

    void calcBonePosition(int frameNo, Skeleton skeleton) {
        boneLoop:
        for (BoneMotionList bml : motionMap.values()) {
            Bone bone = pmdNode.getSkeleton().getBone(bml.boneName);
            if (bone != null) {
                bone.setUserControl(true);
//                if (bone.getName().equals("左足") || bone.getName().contains("ひざ")) {
//                    continue;
//                } else
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
                        VMDMotion m = bml.get(count);
                        if (m.getFrameNo() > currentFrameNo) {
                            if (bml.currentCount == 0) {
                                m1 = m;
                            } else {
                                m1 = bml.get(count - 1);
                            }
                            m2 = m;
                            bml.setCurrentCount(count);
                            break;
                        }
                        count++;
                        if (count >= bml.size()) {
                            m1 = m;
                            m2 = m;
                            bml.setCurrentCount(bml.size() - 1);
                            break;
                        }
                    }
                    float f = (float) (m2.getFrameNo() - m1.getFrameNo()) * 1f / 30;
                    assert f >= 0;
                    if (f == 0f) {
                        tmpq1.set(m1.getRotation());
                        tmpp1.set(m1.getLocation());
                    } else {
                        float f2 = (float) (currentFrameNo - m1.getFrameNo()) * 1f / 30 + timeFromCurrentFrameNo;
                        assert (f2 >= 0);
                        float f3 = f2 / f;
                        float fx = IPUtil.calcIp(bml, f3, 0);//calcIp(m2.getInterpolation(), f3, 0);
                        float fy = IPUtil.calcIp(bml, f3, 1); //calcIp(m2.getInterpolation(), f3, 1);
                        float fz = IPUtil.calcIp(bml, f3, 2); //calcIp(m2.getInterpolation(), f3, 2);
                        float fr = IPUtil.calcIp(bml, f3, 3); //calcIp(m2.getInterpolation(), f3, 3);
//                        float fx = f3;
//                        float fy = f3;
//                        float fz = f3;
//                        float fr = f3;
                        tmpq1.interpolate(m1.getRotation(), m2.getRotation(), fr);
//                        Quat4f q = m1.getRotation();
//                        tmpp1.interpolate(m1.getLocation(), m2.getLocation(), f3);
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
        ikControl.updateIKBoneRotation();
        for (SkinList skinList : skinMap.values()) {
            float w1 = 0f, w2 = 0f;
            int c1 = 0, c2 = 0;
            for (int i = skinList.currentCount; i < skinList.size(); i++) {
                skinList.currentCount = i;
                VMDSkin skin = skinList.get(i);
                if (skin.getFlameNo() > frameNo) {
                    w2 = skin.getWeight();
                    c2 = skin.getFlameNo();
                    if (i > 0) {
                        skin = skinList.get(i - 1);
                        w1 = skin.getWeight();
                        c1 = skin.getFlameNo();
                    } else {
                        w1 = 0f;
                        c1 = 0;
                    }
                    break;
                }
            }
            float f1 = (float) (c2 - c1) * 1f / 30;
            float f2 = (float) (frameNo - c1) * 1f / 30 + timeFromCurrentFrameNo;
            float weight;
            if (f1 > 0) {
                weight = w1 + (w2 - w1) * f2 / f1;
            } else {
                weight = w2;
            }
            pmdNode.setSkinWeight(skinList.skinName, weight);
        }
    }

    public void resetSkins() {
        for (String skinName : pmdNode.getSkinSet()) {
            pmdNode.setSkinWeight(skinName, 0f);
        }
    }

    public void setFrameNo(int frameNo) {
        for (BoneMotionList bml : motionMap.values()) {
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
        timeFromCurrentFrameNo = 0f;
        calcBonePosition(currentFrameNo, pmdNode.getSkeleton());
        for (SkinList skinList : skinMap.values()) {
            skinList.currentCount = skinList.size() - 1;
            for (int i = 0; i < skinList.size(); i++) {
                VMDSkin skin = skinList.get(i);
                if (skin.getFlameNo() > frameNo) {
                    skinList.currentCount = i;
                    break;
                }
            }
        }
        resetSkins();
        calcBonePosition(currentFrameNo, pmdNode.getSkeleton());
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
}

class BoneMotionList extends ArrayList<VMDMotion> {

    static final int IPTABLESIZE = 16;
    String boneName;
    int currentCount;
    int boneType;
    final float ipTable[][] = new float[4][IPTABLESIZE];
    int frame1, frame2;
    final float val1[] = new float[4];
    final float val2[] = new float[4];

    void setCurrentCount(int newCount) {
        if (currentCount != newCount) {
            currentCount = newCount;
            if (newCount >= 0 && newCount < size()) {
                IPUtil.createInterpolationTable(get(currentCount).getInterpolation(), IPTABLESIZE, ipTable);
            }
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
