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
import java.nio.FloatBuffer;
import projectkyoto.mmd.file.PMDModel;
import com.jme3.scene.VertexBuffer.*;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.debug.SkeletonWire;
import com.jme3.scene.shape.Box;
import com.jme3.shader.VarType;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import projectkyoto.mmd.file.PMDBone;
import projectkyoto.mmd.file.PMDSkinVertData;
import projectkyoto.mmd.file.PMDVertex;

/**
 *
 * @author kobayasi
 */
public class PMDNode extends Node {

    PMDModel pmdModel;
    Skeleton skeleton;
    PMDMesh[] targets;
    PMDSkinMesh[] skinTargets;
    Map<String, Skin> skinMap = new HashMap<String, Skin>();
    javax.vecmath.Vector3f skinPosArray[];
    javax.vecmath.Vector3f skinNormalArray[];
    javax.vecmath.Vector3f skinPosArrayOrig[];
    javax.vecmath.Vector3f skinNormalArrayOrig[];
    float skinBoneWeightArray[];
    int skinBoneArray[];
    AssetManager assetManager;
    Matrix4f[] offsetMatrices;
    boolean updateNeeded = true;
    boolean wireFrame = false;
    float edgeSize = 1.0f;
    boolean skeletonWireVisible = false;
    Geometry skeletonWireGeom;
    boolean bonePositionVisible = false;
    Node bonePositionNode;
    Geometry bonePositionGeomArray[];
    Node rigidBodyNode;
    Node jointNode;
    boolean glslSkinning = false;

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
        int skinVertSize = skinVertexList.size();
        skinPosArray = new javax.vecmath.Vector3f[skinVertSize];
        skinNormalArray = new javax.vecmath.Vector3f[skinVertSize];
        skinPosArrayOrig = new javax.vecmath.Vector3f[skinVertSize];
        skinNormalArrayOrig = new javax.vecmath.Vector3f[skinVertSize];
        skinBoneWeightArray = new float[skinVertSize];
        skinBoneArray = new int[skinVertSize * 2];
        for (int i = 0; i < skinVertSize; i++) {
            PMDVertex v = skinVertexList.get(i);
            skinPosArrayOrig[i] = v.getPos();
            skinPosArray[i] = new javax.vecmath.Vector3f(v.getPos());
            skinNormalArrayOrig[i] = v.getNormal();
            skinNormalArray[i] = new javax.vecmath.Vector3f(v.getNormal());
            skinBoneWeightArray[i] = (float) v.getBoneWeight() / 100f;
            skinBoneArray[i * 2] = v.getBoneNum1();
            skinBoneArray[i * 2 + 1] = v.getBoneNum2();
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
        return offsetMatrices;
    }

    public void update() {
//        skeleton.reset(); // reset skeleton to bind pose
        if (true /*
                 * updateNeeded
                 */) {
            updateNeeded = false;
//            skeleton.updateWorldVectors();
//            updateIKBoneRotation();
            // here update the targets verticles if no hardware skinning supported

            offsetMatrices = skeleton.computeSkinningMatrices();
            for (Spatial s : getChildren()) {
                if (s instanceof Geometry) {
                    Geometry g = (Geometry) s;
                    Material m = g.getMaterial();
                    if (g.getMesh() instanceof PMDMesh) {
                        PMDMesh pmdMesh = (PMDMesh) g.getMesh();
                        for (int i = 0; i < pmdMesh.getBoneIndexArray().length; i++) {
                            pmdMesh.getBoneMatrixArray()[i].set(offsetMatrices[pmdMesh.getBoneIndexArray()[i]]);
                        }
                        if (glslSkinning) {
                            m.setParam("BoneMatrices", VarType.Matrix4Array, pmdMesh.getBoneMatrixArray());
//                            m.setParam("BoneMatrices", VarType.Matrix4, pmdMesh.getBoneMatrixArray()[0]);
                        }
                    }
                }
            }
            if (!glslSkinning) {
                for (PMDMesh mesh : targets) {
                    softwareSkinUpdate(mesh);
                }
            }
            updateSkinMesh(skinTargets[0]);
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

    private void softwareSkinUpdate(PMDMesh mesh) {
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

        int iterations = (int) FastMath.ceil(fvb.capacity() / ((float) posBuf.length));
        int bufLength = posBuf.length * 3;
        for (int i = iterations - 1; i >= 0; i--) {
            // read next set of positions and normals from native buffer
            bufLength = Math.min(posBuf.length, fvb.remaining());
            fvb.get(posBuf, 0, bufLength);
            fnb.get(normBuf, 0, bufLength);
            int verts = bufLength / 3;
            int idxPositions = 0;

            // iterate vertices and apply skinning transform for each effecting bone
            for (int vert = verts - 1; vert >= 0; vert--) {
                float nmx = normBuf[idxPositions];
                float vtx = posBuf[idxPositions++];
                float nmy = normBuf[idxPositions];
                float vty = posBuf[idxPositions++];
                float nmz = normBuf[idxPositions];
                float vtz = posBuf[idxPositions++];

                float rx = 0, ry = 0, rz = 0, rnx = 0, rny = 0, rnz = 0;

                for (int w = maxWeightsPerVert - 1; w >= 0; w--) {
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


            fvb.position(fvb.position() - bufLength);
            fvb.put(posBuf, 0, bufLength);
            fnb.position(fnb.position() - bufLength);
            fnb.put(normBuf, 0, bufLength);
        }

        vb.updateData(fvb);
        nb.updateData(fnb);
        vars.release();
//        mesh.updateBound();
    }

    void updateSkinMesh(PMDSkinMesh skinMesh) {
        VertexBuffer vb = skinMesh.getBuffer(Type.Position);
        FloatBuffer fvb = (FloatBuffer) vb.getData();
        VertexBuffer nb = skinMesh.getBuffer(Type.Normal);
        FloatBuffer fnb = (FloatBuffer) nb.getData();

        for (Skin skin : skinMap.values()) {
            if (skin.isUpdateNeeded()) {
                for (PMDSkinVertData svd : skin.getSkinData().getSkinVertData()) {
                    javax.vecmath.Vector3f dist = skinPosArray[svd.getSkinVertIndex()];
                    dist.set(svd.getSkinVertPos());
                    dist.scale(skin.getWeight());
                    dist.add(skinPosArrayOrig[svd.getSkinVertIndex()]);
                }
                skin.setUpdateNeeded(false);
            }
        }

        fvb.position(0);
        fnb.position(0);
        for (int i = 0; i < skinPosArray.length; i++) {
            int idxWeights = 0;

            TempVars vars = TempVars.get();
            float[] posBuf = vars.skinPositions;
            float[] normBuf = vars.skinNormals;

            // read next set of positions and normals from native buffer
            int idxPositions = 0;

            // iterate vertices and apply skinning transform for each effecting bone
            float nmx = skinNormalArray[i].x;//normBuf[idxPositions];
            float vtx = skinPosArray[i].x;//posBuf[idxPositions++];
            float nmy = skinNormalArray[i].y;//normBuf[idxPositions];
            float vty = skinPosArray[i].y;//posBuf[idxPositions++];
            float nmz = skinNormalArray[i].z;//normBuf[idxPositions];
            float vtz = skinPosArray[i].z;//posBuf[idxPositions++];

            float rx = 0, ry = 0, rz = 0, rnx = 0, rny = 0, rnz = 0;

            for (int w = 2 - 1; w >= 0; w--) {
                float weight = skinBoneWeightArray[i];//(float) v.getBoneWeight();//weights[idxWeights];
                if (w == 1) {
                    weight = 1f - weight;
                }
                //weight = weight / 100f;

                Matrix4f mat = offsetMatrices[skinBoneArray[i * 2 + w]];

                rx += (mat.m00 * vtx + mat.m01 * vty + mat.m02 * vtz + mat.m03) * weight;
                ry += (mat.m10 * vtx + mat.m11 * vty + mat.m12 * vtz + mat.m13) * weight;
                rz += (mat.m20 * vtx + mat.m21 * vty + mat.m22 * vtz + mat.m23) * weight;

                rnx += (nmx * mat.m00 + nmy * mat.m01 + nmz * mat.m02) * weight;
                rny += (nmx * mat.m10 + nmy * mat.m11 + nmz * mat.m12) * weight;
                rnz += (nmx * mat.m20 + nmy * mat.m21 + nmz * mat.m22) * weight;
            }

            fnb.put(rnx).put(rny).put(rnz);
            fvb.put(rx).put(ry).put(rz);
            vars.release();
        }
        vb.setUpdateNeeded();
        nb.setUpdateNeeded();
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
        for (Skin skin : skinMap.values()) {
            skin.setWeight(0f);
        }
        setUpdateNeeded(true);
    }

    void resetToBind(PMDMesh mesh) {
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

    public Set<String> getSkinSet() {
        return skinMap.keySet();
    }

    public float getSkinWeight(String skinName) {
        return skinMap.get(skinName).getWeight();
    }

    public void setSkinWeight(String skinName, float weight) {
        Skin skin = skinMap.get(skinName);
        if (skin != null) {
            skin.setWeight(weight);
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
        this.glslSkinning = glslSkinning;
        for (PMDMesh mesh : targets) {
//            resetToBind(mesh);
        }
        for (Spatial sp : getChildren()) {
            if (sp instanceof PMDGeometry) {
                Mesh mesh = ((PMDGeometry) sp).getMesh();
                if (mesh instanceof PMDMesh) {
                    resetToBind((PMDMesh) mesh);
                }
            }
        }
        initMaterials();
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
}
