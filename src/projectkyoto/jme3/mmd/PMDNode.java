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

import com.jme3.animation.Bone;
import com.jme3.animation.Skeleton;
import com.jme3.asset.AssetManager;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.TempVars;
import java.io.IOException;
import java.nio.FloatBuffer;
import projectkyoto.mmd.file.PMDModel;
import com.jme3.scene.VertexBuffer.*;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.debug.SkeletonWire;
import com.jme3.scene.shape.Box;
import com.jme3.shader.VarType;
import com.jme3.util.BufferUtils;
import java.nio.Buffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import projectkyoto.mmd.file.PMDBone;
import projectkyoto.mmd.file.PMDException;
import projectkyoto.mmd.file.PMDSkinData;
import projectkyoto.mmd.file.PMDSkinVertData;
import projectkyoto.mmd.file.PMDVertex;
import projectkyoto.mmd.file.util2.SavableUtil;

/**
 *
 * @author kobayasi
 */
public class PMDNode extends Node {

    PMDModel pmdModel;
    Skeleton skeleton;
    PMDMesh[] targets;
    PMDSkinMesh[] skinTargets;
    PMDGeometry[] pmdGeometryArray;
    Map<String, Skin> skinMap = new HashMap<String, Skin>();
    Skin[] skinArray = new Skin[0];
    FloatBuffer skinPosBuffer;
    AssetManager assetManager;
    Matrix4f[] offsetMatrices;
    FloatBuffer offsetMatrixbuffer;
    boolean updateNeeded = true;
    boolean skinUpdateNeeded = true;
    boolean wireFrame = false;
    float edgeSize = 1.0f;
    boolean skeletonWireVisible = false;
    Geometry skeletonWireGeom;
    boolean bonePositionVisible = false;
    Node bonePositionNode;
    Geometry bonePositionGeomArray[];
    Node rigidBodyNode;
    Node jointNode;
    boolean glslSkinning = true;
    private PMDNode original = null;

    @Override
    public void read(JmeImporter e) throws IOException {
        super.read(e);
        InputCapsule c = e.getCapsule(this);
        pmdModel = (PMDModel)SavableUtil.read(c, "pmdModel", null);
        skeleton = (Skeleton)c.readSavable("skeleton", null);
        skinMap = (Map<String, Skin>)c.readStringSavableMap("skinMap", new HashMap<String, Savable>());
//        skinBoneWeightArray = c.readFloatArray("skinBoneWeightArray", new float[0]);
//        skinBoneArray = c.readIntArray("skinBoneArray", new int[0]);
        edgeSize = c.readFloat("edgeSize", 1.0f);
        int pmdGeometryArrayLength = c.readInt("pmdGeometryArrayLength", 0);
        pmdGeometryArray = new PMDGeometry[pmdGeometryArrayLength];
        targets = new PMDMesh[pmdGeometryArrayLength];
        int skinTargetsLength = c.readInt("skinTargetsLength", 0);
        skinTargets = new PMDSkinMesh[skinTargetsLength];
        VertexBuffer skinvb = (VertexBuffer)c.readSavable("skinvb", null);
        VertexBuffer skinnb = (VertexBuffer)c.readSavable("skinnb", null);
        VertexBuffer skintb = (VertexBuffer)c.readSavable("skintb", null);
        VertexBuffer skinvb2 = skinvb.clone();
        VertexBuffer skinnb2 = skinnb.clone();
        int meshCount = 0;
        int skinMeshCount = 0;
            for(Spatial sp : getChildren()) {
                Spatial newSp = sp;//.clone();
//                newPMDNode.attachChild(newSp);
                if (sp instanceof PMDGeometry) {
                    Mesh mesh = ((Geometry)newSp).getMesh();
                    if (mesh instanceof PMDMesh) {
                        PMDMesh pmdMesh = (PMDMesh)mesh;
                        pmdMesh.setVbBackup(pmdMesh.getBuffer(Type.Position));
                        pmdMesh.setNbBackup(pmdMesh.getBuffer(Type.Normal));
                        pmdGeometryArray[meshCount] = (PMDGeometry)sp;
                        targets[meshCount++] = (PMDMesh)mesh;
                    } else if (mesh instanceof PMDSkinMesh) {
//                        mesh.setMode(Mesh.Mode.Triangles);
                        PMDSkinMesh skinMesh = (PMDSkinMesh)mesh;
                        if (skinMeshCount != 0) {
                            skinMesh.setBuffer(skinvb);
                            skinMesh.setSkinvb2(skinvb2);
                            skinMesh.setBuffer(skinnb);
//                            skinMesh.setSkinnb2(skinnb2);
                            skinMesh.setBuffer(skintb);
                        } else {
                            skinMesh.setBuffer(skinvb);
                            skinMesh.setSkinvb2(skinvb2);
                            skinMesh.setBuffer(skinnb);
//                            skinMesh.setSkinnb2(skinnb2);
                            skinMesh.setBuffer(skintb);
                        }
                        skinTargets[skinMeshCount++] = (PMDSkinMesh)mesh;
                    }
                }
            }
            calcOffsetMatrices();
            Savable[] sa = c.readSavableArray("skinArray", new Skin[0]);
            skinArray = new Skin[sa.length];
            for(int i=0;i<sa.length;i++) {
                Skin skin = (Skin)sa[i];
                skinArray[i] = skin;
                skin.pmdNode = this;
                l2:
                for(int i2=0;i2<pmdModel.getSkinCount();i2++){
                    if (pmdModel.getSkinData()[i2].getSkinName().equals(skin.getSkinName())) {
//                        skin.skinData = pmdModel.getSkinData()[i2];
                        break l2;
                    }
                }
                skin.setWeight(0f);
                skin.setUpdateNeeded(true);
                skinMap.put(skin.skinName, skin);
            }
//            skinPosArray = (javax.vecmath.Vector3f[])SavableUtil.read(c, "skinPosArray", null);
//            skinPosArrayOrig = new javax.vecmath.Vector3f[skinPosArray.length];
//            for(int i=0;i<skinPosArray.length;i++) {
//                skinPosArrayOrig[i] = new javax.vecmath.Vector3f(skinPosArray[i]);
//            }
//            skinNormalArray = (javax.vecmath.Vector3f[])SavableUtil.read(c, "skinNormalArray", null);
//            skinNormalArrayOrig = new javax.vecmath.Vector3f[skinNormalArray.length];
//            for(int i=0;i<skinNormalArray.length;i++) {
//                skinNormalArrayOrig[i] = new javax.vecmath.Vector3f(skinNormalArray[i]);
//            }
//            skinBoneArray = c.readIntArray("skinBoneArray", skinBoneArray);
//            skinBoneWeightArray = c.readFloatArray("skinBoneWeightArray", skinBoneWeightArray);
    }

    @Override
    public void write(JmeExporter e) throws IOException {
        super.write(e);
        OutputCapsule c = e.getCapsule(this);
        c.write(1, "version", 1);
        SavableUtil.write(c, pmdModel, "pmdModel");
        c.write(skeleton, "skeleton", new Skeleton());
        c.writeStringSavableMap(skinMap, "skinMap", new HashMap<String, Skin>());
//        c.write(skinBoneWeightArray, "skinBoneWeightArray", null);
//        c.write(skinBoneArray, "skinBoneArray", null);
        c.write(edgeSize, "edgeSize", 1.0f);
        c.write(pmdGeometryArray.length, "pmdGeometryArrayLength",0);
        c.write(skinTargets.length, "skinTargetsLength",0);
        if (skinTargets != null && skinTargets.length > 0) {
            PMDSkinMesh mesh = skinTargets[0];
            c.write(mesh.getSkinvb2(),"skinvb",null);
//            c.write(mesh.getSkinnb2(),"skinnb",null);
            c.write(mesh.getBuffer(Type.TexCoord),"skintb",null);
        }
        c.write(skinArray, "skinArray", null);
//        SavableUtil.write(c, skinPosArrayOrig,"skinPosArray");
//        SavableUtil.write(c, skinNormalArrayOrig, "skinNormalArray");
//        c.write(skinBoneArray, "skinBoneArray", null);
//        c.write(skinBoneWeightArray, "skinBoneWeightArray", null);
        
    }

    public PMDNode(String name, PMDModel pmdModel, AssetManager assetManager) {
        super(name);
        this.pmdModel = pmdModel;
        this.assetManager = assetManager;
    }

    public PMDNode() {
    }

    public void init() {
//        initBtWorld();
//        initRigidBodyArray();
        initMaterials();
        resetToBind();
    }

    void setSkinData(PMDSkinMesh[] skinTargets, List<PMDVertex> skinVertexList, Skin[] skinAray) {
        this.skinTargets = skinTargets;
        for (Skin skin : skinAray) {
            skinMap.put(skin.getSkinName(), skin);
        }
    }

    public PMDModel getPmdModel() {
        return pmdModel;
    }

    public void setPmdModel(PMDModel pmdModel) {
        this.pmdModel = pmdModel;
    }

    public Matrix4f[] calcOffsetMatrices() {
        offsetMatrices = skeleton.computeSkinningMatrices();
        if (offsetMatrixbuffer == null) {
            offsetMatrixbuffer = BufferUtils.createFloatBuffer(offsetMatrices.length * 16);
        }
        offsetMatrixbuffer.position(0);
        for(Matrix4f m : offsetMatrices) {
            m.fillFloatBuffer(offsetMatrixbuffer, true);
        }
        return offsetMatrices;
    }
boolean setBoneMatricesFlag = true;
    public void update() {
//        skeleton.reset(); // reset skeleton to bind pose
        if (true /*
                 * updateNeeded
                 */) {
            updateNeeded = false;
//            skeleton.updateWorldVectors();
//            updateIKBoneRotation();
            // here update the targets verticles if no hardware skinning supported

//            offsetMatrices = skeleton.computeSkinningMatrices();
            for(PMDGeometry g : pmdGeometryArray) {
                Material m = g.getMaterial();
                PMDMesh pmdMesh = (PMDMesh)g.getMesh();
                int boneIndexArray[] = pmdMesh.getBoneIndexArray();
                Matrix4f[] boneMatrixArray = pmdMesh.getBoneMatrixArray();
//                for (int i = pmdMesh.getBoneIndexArray().length-1; i >=0; i--) {
//                    boneMatrixArray[i] = (offsetMatrices[boneIndexArray[i]]);
//                }
                
                if (glslSkinning) {
//                    if (pmdMesh.boneMatricesParamIndex < 0) {
//                        m.setParam("BoneMatrices", VarType.Matrix4Array, pmdMesh.getBoneMatrixArray());
//                        pmdMesh.boneMatricesParamIndex = g.getMaterial().getParamIndex("BoneMatrices");
//                    } else {
//                        m.setParam(pmdMesh.boneMatricesParamIndex, VarType.Matrix4Array, pmdMesh.getBoneMatrixArray());
//                    }
                    FloatBuffer fb = pmdMesh.getBoneMatrixBuffer();
                    fb.position(0);
//                    for(int i=0;i<pmdMesh.getBoneIndexArray().length;i++) {
//                        offsetMatrices[pmdMesh.getBoneIndexBuffer().get(i)].fillFloatBuffer(fb, true);
////                        pmdMesh.getBoneMatrixArray()[i].fillFloatBuffer(fb, true);
//                    }
                    projectkyoto.jme3.mmd.nativelib.SkinUtil.copyBoneMatrix(offsetMatrixbuffer, fb, pmdMesh.getBoneIndexBuffer());

//                    fb.position(0);
                    if (pmdMesh.boneMatricesParamIndex < 0) {
                        m.setParam("BoneMatrices", VarType.Matrix4Array, pmdMesh.getBoneMatrixBuffer());
                        pmdMesh.boneMatricesParamIndex = g.getMaterial().getParamIndex("BoneMatrices");
                    } else {
                        m.setParam(pmdMesh.boneMatricesParamIndex, VarType.Matrix4Array, pmdMesh.getBoneMatrixBuffer());
                    }
                }
            }
            FloatBuffer fb = null;
            for(int i=getChildren().size()-1;i>=0;i--) {
                Spatial sp = getChild(i);
                if (sp instanceof PMDGeometry) {
                    PMDGeometry g = (PMDGeometry) sp;
                    Mesh mesh = g.getMesh();
                    if (mesh instanceof PMDSkinMesh) {
                        PMDSkinMesh skinMesh = (PMDSkinMesh)mesh;
                        Material m = g.getMaterial();
                        int boneIndexArray[] = skinMesh.getBoneIndexArray();
//                        Matrix4f[] boneMatrixArray = skinMesh.getBoneMatrixArray();
//                        for (int i2 = skinMesh.getBoneIndexArray().length-1; i2 >=0; i2--) {
//                            boneMatrixArray[i2] = (offsetMatrices[boneIndexArray[i2]]);
//                        }
                        if (fb == null) {
                            fb = skinMesh.getBoneMatrixBuffer();
                            fb.position(0);
                            projectkyoto.jme3.mmd.nativelib.SkinUtil.copyBoneMatrix(offsetMatrixbuffer, fb, skinMesh.getBoneIndexBuffer());
                        }
                        if (glslSkinning) {
                            if (skinMesh.boneMatricesParamIndex < 0) {
                                m.setParam("BoneMatrices", VarType.Matrix4Array, fb);
                                skinMesh.boneMatricesParamIndex = g.getMaterial().getParamIndex("BoneMatrices");
                            } else {
                                m.setParam(skinMesh.boneMatricesParamIndex, VarType.Matrix4Array, fb);
                            }
                        }
                    }
                }
            }
            if (!glslSkinning) {
                for (PMDMesh mesh : targets) {
                    softwareSkinUpdate(mesh);
                }
            }
//            updateSkinMesh(skinTargets[0]);
//            if (skinUpdateNeeded) {
//                updateSkinBackData();
//            }
            swapSkinMesh();
            if (skeletonWireGeom != null) {
                ((SkeletonWire) skeletonWireGeom.getMesh()).updateGeometry();
            }
            if (bonePositionVisible) {
                for (int i = 0; i < bonePositionGeomArray.length; i++) {
                    Geometry bonePosGeom = bonePositionGeomArray[i];
//                    Mesh mesh = bonePosGeom.getMesh();
//                    VertexBuffer vb = mesh.getBuffer(Type.Position);
//                    FloatBuffer posBuf = (FloatBuffer)vb.getData();
//                    posBuf.clear();
                    Bone bone = skeleton.getBone(i);
                    Vector3f bonePos = bone.getModelSpacePosition();
//                    posBuf.position(0);
//                    posBuf.put(bonePos.x);
//                    posBuf.put(bonePos.y);
//                    posBuf.put(bonePos.z);
//                    vb.setUpdateNeeded();
//                    mesh.updateCounts();
//                    mesh.updateBound();
//                    bonePosGeom.setModelBound(new BoundingBox(bonePos, 10f,10f,10f));
//                    bonePosGeom.updateModelBound();
//                    bonePosGeom.setMesh(new Box(bonePos, 0.1f,0.1f,0.1f));
                    bonePosGeom.setLocalTranslation(bonePos);
                }
            }
        }
    }
    private void swapSkinMesh() {
        if (skinTargets.length == 0) {
            return;
        }
        VertexBuffer vb = skinTargets[0].getBuffer(VertexBuffer.Type.Position);
//        VertexBuffer nb = skinTargets[0].getBuffer(VertexBuffer.Type.Normal);
        skinTargets[0].skinvb2.setUpdateNeeded();
//        skinTargets[0].skinnb2.setUpdateNeeded();
        VertexBuffer skinvb2 = skinTargets[0].skinvb2;
        for(PMDSkinMesh skinMesh : skinTargets) {
//            skinMesh.clearBuffer(Type.Position);
//            skinMesh.clearBuffer(Type.Normal);
            skinMesh.setBuffer(skinvb2);
//            skinMesh.setBuffer(skinTargets[0].getSkinnb2());
        }
        skinTargets[0].skinvb2 = vb;
//        skinTargets[0].skinnb2 = nb;
//        vb = skinTargets[0].getBuffer(VertexBuffer.Type.Position);
//        nb = skinTargets[0].getBuffer(VertexBuffer.Type.Normal);
//        vb.setUpdateNeeded();
//        nb.setUpdateNeeded();
    }
    private void softwareSkinUpdate(PMDMesh mesh){
        int maxWeightsPerVert = 2;//mesh.getMaxNumWeights();
        int fourMinusMaxWeights = 4 - maxWeightsPerVert;
//        Matrix4f[] offsetMatrices = mesh.getBoneMatrixArray();

        // NOTE: This code assumes the vertex buffer is in bind pose
        // resetToBind() has been called this frame
        resetToBind(mesh);
        VertexBuffer vb = mesh.getBuffer(VertexBuffer.Type.Position);
        FloatBuffer fvb = (FloatBuffer) vb.getData();
        fvb.rewind();

        VertexBuffer nb = mesh.getBuffer(VertexBuffer.Type.Normal);
        FloatBuffer fnb = (FloatBuffer) nb.getData();
        fnb.rewind();
        
        FloatBuffer fvb2 = (FloatBuffer)mesh.getVbBackup().getData();
        fvb2.rewind();
        FloatBuffer fnb2 = (FloatBuffer)mesh.getNbBackup().getData();
        fnb2.rewind();
        
        // get boneIndexes and weights for mesh
        ShortBuffer ib = (ShortBuffer) mesh.getBuffer(VertexBuffer.Type.BoneIndex).getData();
        FloatBuffer wb = (FloatBuffer) mesh.getBuffer(VertexBuffer.Type.BoneWeight).getData();

        ib.rewind();
        wb.rewind();

//        float[] weights = wb.array();
//        short[] indices = ib.array();
        int idxWeights = 0;

        TempVars vars = TempVars.get();
        float[] posBuf = vars.skinPositions;
        float[] normBuf = vars.skinNormals;

        int iterations = (int) FastMath.ceil(fvb.capacity() / ((float)posBuf.length));
        int bufLength = posBuf.length * 3;
        for (int i = iterations-1; i >= 0; i--){
            // read next set of positions and normals from native buffer
            bufLength = Math.min(posBuf.length, fvb.remaining());
            fvb2.get(posBuf, 0, bufLength);
            fnb2.get(normBuf, 0, bufLength);
            int verts = bufLength / 3;
            int idxPositions = 0;

            // iterate vertices and apply skinning transform for each effecting bone
            for (int vert = verts - 1; vert >= 0; vert--){
                float nmx = normBuf[idxPositions];
                float vtx = posBuf[idxPositions++];
                float nmy = normBuf[idxPositions];
                float vty = posBuf[idxPositions++];
                float nmz = normBuf[idxPositions];
                float vtz = posBuf[idxPositions++];

                float rx=0, ry=0, rz=0, rnx=0, rny=0, rnz=0;

                for (int w = maxWeightsPerVert - 1; w >= 0; w--){
                    float weight = wb.get(idxWeights); //weights[idxWeights];
                    Matrix4f mat = mesh.getBoneMatrixArray()[ib.get(idxWeights++)];//offsetMatrices[indices[idxWeights++]];

                    rx += (mat.m00 * vtx + mat.m01 * vty + mat.m02 * vtz + mat.m03) * weight;
                    ry += (mat.m10 * vtx + mat.m11 * vty + mat.m12 * vtz + mat.m13) * weight;
                    rz += (mat.m20 * vtx + mat.m21 * vty + mat.m22 * vtz + mat.m23) * weight;

                    rnx += (nmx * mat.m00 + nmy * mat.m01 + nmz * mat.m02) * weight;
                    rny += (nmx * mat.m10 + nmy * mat.m11 + nmz * mat.m12) * weight;
                    rnz += (nmx * mat.m20 + nmy * mat.m21 + nmz * mat.m22) * weight;
                }

                idxWeights += fourMinusMaxWeights;

                idxPositions -= 3;
                normBuf[idxPositions] = rnx;
                posBuf[idxPositions++] = rx;
                normBuf[idxPositions] = rny;
                posBuf[idxPositions++] = ry;
                normBuf[idxPositions] = rnz;
                posBuf[idxPositions++] = rz;
            }


//            fvb.position(fvb2.position()-bufLength);
            fvb.put(posBuf, 0, bufLength);
//            fnb.position(fnb2.position()-bufLength);
            fnb.put(normBuf, 0, bufLength);
        }
        vb.setUpdateNeeded();
        nb.setUpdateNeeded();
        vars.release();
        
//        mesh.updateBound();
    }
    public void updateSkinBackData() {
        if (skinTargets.length == 0) {
            return;
        }
        PMDSkinMesh skinMesh = skinTargets[0];
        VertexBuffer vb = skinMesh.getSkinvb2(); //.getBuffer(Type.Position);
        FloatBuffer fvb = (FloatBuffer) vb.getData();
        fvb.position(0);
//        float[] floatBuf = skinPosBuffer.array();
        int length = fvb.capacity();
//        for(int i=0;i<length;i++) {
//            fvb.put(floatBuf[i]);
//        }
        projectkyoto.jme3.mmd.nativelib.SkinUtil.copy(skinPosBuffer, fvb, fvb.limit() * 4);
        for (Skin skin : skinArray) {
            if (true || skin.isUpdateNeeded()) {
                float weight = skin.getWeight();
                if (weight != 0f) {
//                    for (PMDSkinVertData svd : skin.getSkinData().getSkinVertData()) {
//                        javax.vecmath.Vector3f svp = svd.getSkinVertPos();
//                        javax.vecmath.Vector3f svop = skinPosArrayOrig[svd.getSkinVertIndex()];
//                        fvb.position(svd.getSkinVertIndex() * 3);
//                        fvb.put(svp.x*weight+svop.x).put(svp.y*weight+svop.y).put(svp.z*weight+svop.z);
//                    }
                    projectkyoto.jme3.mmd.nativelib.SkinUtil.setSkin(fvb, skin.getIndexBuf(), skin.getSkinBuf(), weight);
                    
                }
                skin.setUpdateNeeded(false);
            }
        }

        vb.setUpdateNeeded();
//        nb.setUpdateNeeded();
        skinUpdateNeeded = false;
    }


    public void resetToBind() {
        for (int i = 0; i < skeleton.getBoneCount(); i++) {
            Bone bone = skeleton.getBone(i);
            PMDBone pmdBone = pmdModel.getBoneList().getBones()[i];
            if (pmdBone.getParentBoneIndex() < skeleton.getBoneCount()) {
                Bone parentBone = skeleton.getBone(pmdBone.getParentBoneIndex());
                PMDBone parentPMDBone = pmdModel.getBoneList().getBones()[pmdBone.getParentBoneIndex()];
//                parentBone.addChild(bone);
                Vector3f v1 = new Vector3f();
                Vector3f v2 = new Vector3f();
                v1.set(pmdBone.getBoneHeadPos().x, pmdBone.getBoneHeadPos().y, pmdBone.getBoneHeadPos().z);
                v2.set(parentPMDBone.getBoneHeadPos().x, parentPMDBone.getBoneHeadPos().y, parentPMDBone.getBoneHeadPos().z);
                v1.subtractLocal(v2);

                bone.setBindTransforms(v1, Quaternion.IDENTITY, new Vector3f(1, 1, 1));
            } else {
                Vector3f v1 = new Vector3f();
                v1.set(pmdBone.getBoneHeadPos().x, pmdBone.getBoneHeadPos().y, pmdBone.getBoneHeadPos().z);
                bone.setBindTransforms(v1, Quaternion.IDENTITY, new Vector3f(1, 1, 1));
            }
        }
        for (PMDMesh mesh : targets) {
            resetToBind(mesh);
        }
        for (Skin skin : skinArray) {
            skin.setWeight(0f);
        }
        setUpdateNeeded(true);
    }

    void resetToBind(PMDMesh mesh) {
    }
    void _resetToBind(PMDMesh mesh) {
        VertexBuffer vb = mesh.getBuffer(VertexBuffer.Type.Position);
        FloatBuffer vfb = (FloatBuffer) vb.getData();
        VertexBuffer nb = mesh.getBuffer(VertexBuffer.Type.Normal);
        FloatBuffer nfb = (FloatBuffer) nb.getData();

        VertexBuffer bvb = mesh.getBuffer(VertexBuffer.Type.BindPosePosition);
        FloatBuffer bvfb = (FloatBuffer) bvb.getData();
        VertexBuffer bnb = mesh.getBuffer(VertexBuffer.Type.BindPoseNormal);
        FloatBuffer bnfb = (FloatBuffer) bnb.getData();

        for (int i = 0; i < vfb.capacity(); i++) {
            vfb.put(i, bvfb.get(i));
        }
        for (int i = 0; i < nfb.capacity(); i++) {
            nfb.put(i, bnfb.get(i));
        }
        vb.setUpdateNeeded();
        nb.setUpdateNeeded();
    }
    void resetToBindSkinBackData(PMDSkinMesh mesh) {
        VertexBuffer vb = mesh.getSkinvb2(); // mesh.getBuffer(VertexBuffer.Type.Position);
        FloatBuffer vfb = (FloatBuffer) vb.getData();
//        VertexBuffer nb = mesh.getSkinnb2(); //mesh.getBuffer(VertexBuffer.Type.Normal);
//        FloatBuffer nfb = (FloatBuffer) nb.getData();

        VertexBuffer bvb = mesh.getBuffer(VertexBuffer.Type.BindPosePosition);
        FloatBuffer bvfb = (FloatBuffer) bvb.getData();
        VertexBuffer bnb = mesh.getBuffer(VertexBuffer.Type.BindPoseNormal);
        FloatBuffer bnfb = (FloatBuffer) bnb.getData();

        for (int i = 0; i < vfb.capacity(); i++) {
            vfb.put(i, bvfb.get(i));
        }
//        for (int i = 0; i < nfb.capacity(); i++) {
//            nfb.put(i, bnfb.get(i));
//        }
        vb.setUpdateNeeded();
//        nb.setUpdateNeeded();
    }
    public Set<String> getSkinSet() {
        return skinMap.keySet();
    }
    public Map<String, Skin> getSkinMap() {
        return skinMap;
    }

    public float getSkinWeight(String skinName) {
        return skinMap.get(skinName).getWeight();
    }

    public void setSkinWeight(String skinName, float weight) {
        Skin skin = skinMap.get(skinName);
        if (skin != null) {
            skin.setWeight(weight);
            skinUpdateNeeded = true;
//            for (PMDSkinVertData svd : skin.getSkinData().getSkinVertData()) {
//                javax.vecmath.Vector3f dist = skinPosArray[svd.getSkinVertIndex()];
//                dist.set(svd.getSkinVertPos());
//                dist.scale(weight);
//                dist.add(skinPosArrayOrig[svd.getSkinVertIndex()]);
//            }
        }
    }

    public boolean isUpdateNeeded() {
        return updateNeeded;
    }

    public void setUpdateNeeded(boolean updateNeeded) {
        this.updateNeeded = updateNeeded;
    }

    public Skeleton getSkeleton() {
        return skeleton;
    }

    public void setSkeleton(Skeleton skeleton) {
        this.skeleton = skeleton;
    }

    public void setWireFrame(boolean wireFrame) {
        for (Spatial sp : getChildren()) {
            if (sp instanceof Geometry) {
                Geometry geom = (Geometry) sp;
                geom.getMaterial().getAdditionalRenderState().setWireframe(wireFrame);
            }
        }
        this.wireFrame = wireFrame;
    }

    public boolean isWireFrame() {
        return wireFrame;
    }

    public void setEdgeWidth(float edgeSize) {
        for (Spatial sp : getChildren()) {
            if (sp instanceof PMDGeometry) {
                PMDGeometry geom = (PMDGeometry) sp;
                if (geom.getPmdMaterial().getEdgeFlag() != 0) {
                    geom.getMaterial().setFloat("EdgeSize", edgeSize);
                }
            }
        }
        this.edgeSize = edgeSize;
    }

    public void setSkeletonWireVisible(boolean skeletonWireVisible) {
        if (skeletonWireVisible) {
            if (skeletonWireGeom == null) {
                SkeletonWire skeletonWire = new SkeletonWire(skeleton);
                skeletonWireGeom = new Geometry("skeletonWire", skeletonWire);
                Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mat.setColor("Color", ColorRGBA.Green);
                mat.getAdditionalRenderState().setDepthTest(false);
                mat.getAdditionalRenderState().setDepthWrite(false);
                skeletonWireGeom.setMaterial(mat);
                skeletonWireGeom.setQueueBucket(Bucket.Transparent);
                attachChild(skeletonWireGeom);
                skeletonWire.updateGeometry();
            }
        } else {
            if (skeletonWireGeom != null) {
                skeletonWireGeom.removeFromParent();
                skeletonWireGeom = null;
            }
        }
        this.skeletonWireVisible = skeletonWireVisible;
    }

    public void setBonePositionVisible(boolean bonePositionVisible) {
        if (bonePositionVisible) {
            if (bonePositionNode == null) {
                bonePositionNode = new Node("bonePositionNode");
                bonePositionGeomArray = new Geometry[pmdModel.getBoneList().getBoneCount()];
                Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mat.setColor("Color", ColorRGBA.Red);
                mat.getAdditionalRenderState().setDepthTest(false);
                mat.getAdditionalRenderState().setDepthWrite(false);
                Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mat2.setColor("Color", ColorRGBA.Blue);
                mat2.getAdditionalRenderState().setDepthTest(false);
                mat2.getAdditionalRenderState().setDepthWrite(false);
                Material mat3 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mat3.setColor("Color", ColorRGBA.Green);
                mat3.getAdditionalRenderState().setDepthTest(false);
                mat3.getAdditionalRenderState().setDepthWrite(false);
                for (int i = 0; i < bonePositionGeomArray.length; i++) {
                    Mesh mesh = new Mesh();
//                    mesh.setMode(Mesh.Mode.Points);
//                    VertexBuffer pb = new VertexBuffer(Type.Position);
//                    FloatBuffer fpb = BufferUtils.createFloatBuffer(3);
//                    fpb.put(0f).put(0f).put(0f);
//                    pb.setupData(Usage.Static, 3, Format.Float, fpb);
//                    mesh.setBuffer(pb);
//                    mesh.setPointSize(7);
//                    mesh.updateCounts();
                    Geometry geom = new Geometry(pmdModel.getBoneList().getBones()[i].getBoneName(), new Box(0.1f, 0.1f, 0.0f));
                    geom.setMaterial(mat);
                    geom.setQueueBucket(Bucket.Transparent);
                    bonePositionGeomArray[i] = geom;
                    bonePositionNode.attachChild(geom);
                    geom.addControl(new BillboardControl());
                    if (pmdModel.getBoneList().getBones()[i].getBoneType() == 2) {
                        geom.setMaterial(mat2);
                    }
                    if (pmdModel.getBoneList().getBones()[i].getBoneType() == 6) {
                        geom.setMaterial(mat3);
                    }
                }
                attachChild(bonePositionNode);
            }

        } else {
            if (bonePositionNode != null) {
                bonePositionNode.removeFromParent();
                bonePositionNode = null;
                bonePositionGeomArray = null;
            }
        }
        setUpdateNeeded(true);
        this.bonePositionVisible = bonePositionVisible;
    }

    public Node getBonePositionNode() {
        return bonePositionNode;
    }

    public Matrix4f[] getOffsetMatrices() {
        return offsetMatrices;
    }

    public void setRigidBodyVisible(boolean flag) {
        if (flag) {
            if (rigidBodyNode != null) {
                return;
            }
            RigidBodyConverter rbc = new RigidBodyConverter(pmdModel, assetManager);
            rigidBodyNode = rbc.convert("rigidBody");
            attachChild(rigidBodyNode);
        } else {
            if (rigidBodyNode != null) {
                detachChild(rigidBodyNode);
                rigidBodyNode = null;
            }
        }
    }

    public Node getRigidBodyNode() {
        return rigidBodyNode;
    }
    // bullet physics
//    PMDRigidBody rigidBodyArray[];

    void initMaterials() {
        for (Spatial sp : getChildren()) {
            if (sp instanceof PMDGeometry) {
                PMDGeometry geom = (PMDGeometry) sp;
                Mesh mesh = geom.getMesh();
                if (mesh instanceof PMDMesh) {
                    if (glslSkinning) {
                        geom.setMaterial(geom.getGlslSkinningMaterial());
                    } else {
                        geom.setMaterial(geom.getNoSkinningMaterial());
                    }
                }
            }
        }
    }

    public Node getJointNode() {
        return jointNode;
    }

    public void setJointNode(Node jointNode) {
        this.jointNode = jointNode;
    }

//    PMDRigidBody createRigidBody(projectkyoto.mmd.file.PMDRigidBody fileRigidBody, Bone bone) {
//        return null;
//    }
    public boolean isGlslSkinning() {
        return glslSkinning;
    }

    public void setGlslSkinning(boolean glslSkinning) {
//        this.glslSkinning = glslSkinning;
//        for (Spatial sp : getChildren()) {
//            if (sp instanceof PMDGeometry) {
//                Mesh mesh = ((PMDGeometry) sp).getMesh();
//                if (mesh instanceof PMDMesh) {
//                    PMDMesh pmdMesh = (PMDMesh)mesh;
//                    resetToBind(pmdMesh);
//                    if (glslSkinning) {
//                        pmdMesh.releaseSoftwareSkinningBufferes();
//                        mesh.getBuffer(Type.Position).setUsage(Usage.Static);
//                        mesh.getBuffer(Type.Normal).setUsage(Usage.Static);
//                    } else {
//                        pmdMesh.createSoftwareSkinningBuffers();
//                        mesh.getBuffer(Type.Position).setUsage(Usage.Dynamic);
//                        mesh.getBuffer(Type.Normal).setUsage(Usage.Dynamic);
//                    }
//                }
//            }
//        }
//        initMaterials();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return pmdModel.getModelName().hashCode();
    }

    @Override
    public PMDNode clone() {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO,"clone PMDNode "+pmdModel.getModelName());
//        if (true) {
//            PMDNode newPMDNode = (PMDNode)super.clone();
//            return newPMDNode;
//        }
        try {
            PMDNode newPMDNode = (PMDNode)super.clone();
//            newPMDNode.pmdModel = pmdModel;
            newPMDNode.skeleton = new Skeleton(skeleton);
            for(int i=0;i<skeleton.getBoneCount();i++) {
                Bone newBone = newPMDNode.skeleton.getBone(i);
                Bone bone = skeleton.getBone(i);
                newBone.getLocalPosition().set(bone.getLocalPosition());
                newBone.getLocalRotation().set(bone.getLocalRotation());
                newBone.getLocalScale().set(bone.getLocalScale());
            }
            newPMDNode.pmdGeometryArray = new PMDGeometry[pmdGeometryArray.length];
            newPMDNode.targets = new PMDMesh[targets.length];
            newPMDNode.skinTargets = new PMDSkinMesh[skinTargets.length];
            int meshCount=0;
            int skinMeshCount = 0;
            for(Spatial sp : newPMDNode.getChildren()) {
                Spatial newSp = sp;//.clone();
//                newPMDNode.attachChild(newSp);
                if (sp instanceof PMDGeometry) {
                    Mesh mesh = ((Geometry)newSp).getMesh();
                    if (mesh instanceof PMDMesh) {
                       newPMDNode.pmdGeometryArray[meshCount] = (PMDGeometry)sp;
                       newPMDNode.targets[meshCount++] = (PMDMesh)mesh;
                    } else if (mesh instanceof PMDSkinMesh) {
                        mesh.setMode(Mesh.Mode.Triangles);
                        if (skinMeshCount != 0) {
                            PMDSkinMesh skinMesh = (PMDSkinMesh)mesh;
                            PMDSkinMesh skinMesh0 = newPMDNode.skinTargets[0];
                            skinMesh.setBuffer(skinMesh0.getBuffer(Type.Position));
                            skinMesh.setSkinvb2(skinMesh0.getSkinvb2());
                            skinMesh.setBuffer(skinMesh0.getBuffer(Type.Normal));
//                            skinMesh.setSkinnb2(skinMesh0.getSkinnb2());
                            if (skinMesh0.getBuffer(Type.TexCoord) != null)
                                skinMesh.setBuffer(skinMesh0.getBuffer(Type.TexCoord));
                        } else {
                            PMDSkinMesh skinMesh = (PMDSkinMesh)mesh;
                            PMDSkinMesh skinMesh0 = skinTargets[0];
                            skinMesh.setBuffer(skinMesh0.getBuffer(Type.Position).clone());
                            skinMesh.setSkinvb2(skinMesh0.getSkinvb2().clone());
                            skinMesh.setBuffer(skinMesh0.getBuffer(Type.Normal));
//                            skinMesh.setSkinnb2(skinMesh0.getSkinnb2());
                            if (skinMesh0.getBuffer(Type.TexCoord) != null)
                                skinMesh.setBuffer(skinMesh0.getBuffer(Type.TexCoord));
                        }
                        newPMDNode.skinTargets[skinMeshCount++] = (PMDSkinMesh)mesh;
                    }
                }
            }
            newPMDNode.skinMap = new HashMap<String, Skin>();
            for(String skinName : skinMap.keySet()) {
                Skin skin = skinMap.get(skinName);
                skin = skin.clone();
                skin.pmdNode = newPMDNode;
                newPMDNode.skinMap.put(skinName, skin);
            }
            newPMDNode.skinArray = newPMDNode.skinMap.values().toArray(new Skin[newPMDNode.skinMap.size()]);
//            newPMDNode.skinPosArray = new javax.vecmath.Vector3f[skinPosArray.length];
//            for(int i=0;i<skinPosArray.length;i++) {
//                newPMDNode.skinPosArray[i] = new javax.vecmath.Vector3f(skinPosArray[i]);
//            }
//            newPMDNode.skinNormalArray = new javax.vecmath.Vector3f[skinNormalArray.length];
//            for(int i=0;i<skinNormalArray.length;i++) {
//                newPMDNode.skinNormalArray[i] = new javax.vecmath.Vector3f(skinNormalArray[i]);
//            }
//            newPMDNode.offsetMatrices = new Matrix4f[offsetMatrices.length];
//            newPMDNode.setGlslSkinning(newPMDNode.glslSkinning);
            newPMDNode.skeleton.updateWorldVectors();
            newPMDNode.offsetMatrixbuffer = null;
            newPMDNode.calcOffsetMatrices();
            newPMDNode.updateSkinBackData();
            newPMDNode.update();
            newPMDNode.updateSkinBackData();
            newPMDNode.update();
            if (original != null) {
                newPMDNode.original = original;
            } else {
                newPMDNode.original = this;
            }
            return newPMDNode;
        } catch(CloneNotSupportedException ex) {
            throw new PMDException(ex);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (pmdModel != null) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO,"finalize PMDNode "+pmdModel.getModelName());
//            System.out.println("finalize PMDNode "+pmdModel.getModelName());
        } else {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO,"finalize PMDNode");
        }
    }
    
}
