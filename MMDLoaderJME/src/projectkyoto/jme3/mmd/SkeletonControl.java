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
package projectkyoto.jme3.mmd;

import com.jme3.animation.Skeleton;
import com.jme3.asset.AssetManager;
import com.jme3.export.Savable;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix4f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.util.TempVars;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import com.jme3.scene.VertexBuffer.*;
import com.jme3.scene.debug.SkeletonWire;
import com.jme3.shader.VarType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import projectkyoto.mmd.file.PMDSkinVertData;
import projectkyoto.mmd.file.PMDVertex;

/**
 *
 * @author kobayasi
 */
public class SkeletonControl extends AbstractControl implements Savable, Cloneable {

    /**
     * List of targets which this controller effects.
     */
    PMDMesh[] targets;
    PMDSkinMesh[] skinTargets;
    javax.vecmath.Vector3f skinPosArray[];
    javax.vecmath.Vector3f skinNormalArray[];
    javax.vecmath.Vector3f skinPosArrayOrig[];
    javax.vecmath.Vector3f skinNormalArrayOrig[];
    float skinBoneWeightArray[];
    int skinBoneArray[];
    AssetManager assetManager;
    Map<String, Skin> skinMap = new HashMap<String, Skin>();
//    Material material;
    Matrix4f[] offsetMatrices;
    /**
     * Skeleton object must contain corresponding data for the targets' weight
     * buffers.
     */
    Skeleton skeleton;
    Node model;
    Node skeletonLineNode;
    Node boneNode;

    public SkeletonControl(Node model, PMDMesh[] targets, PMDSkinMesh[] skinTargets, List<PMDVertex> skinVertexList, Skin[] skinAray, Skeleton skeleton, AssetManager assetManager) {
//        super(model);
        this.targets = targets;
        this.skinTargets = skinTargets;
        this.skeleton = skeleton;
        this.model = model;
        this.assetManager = assetManager;
        Geometry geom;
        geom = (Geometry) model.getChild("geom1");
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
//        material = geom.getMaterial();
//        textureBuf = ByteBuffer.allocateDirect(4*512*4*4);
//        textureBuf.order(ByteOrder.nativeOrder());
//        textureBuf.position(0);
//        floatTextureBuf = textureBuf.asFloatBuffer();
//        image = new Image(Image.Format.RGB32F, 4, 512, textureBuf);
//        image = new Image();
//        image.setFormat(Image.Format.RGB32F);
//        image.setData(textureBuf);
//        image.setWidth(4);
//        image.setHeight(512);
//        image.addData(textureBuf);
////        texture = new Texture2D(image);
//        texture = new Texture2D(4,512, Image.Format.RGBA32F);
//        texture.setImage(image);
//        texture.setMagFilter(Texture.MagFilter.Nearest);
//        texture.setMinFilter(Texture.MinFilter.NearestNoMipMaps);

    }

    @Override
    protected void controlUpdate(float tpf) {
        skeleton.reset(); // reset skeleton to bind pose

//        Quaternion q = new Quaternion();
//        q = q.fromAngleNormalAxis((float) Math.PI / 2, new Vector3f(0, 0, 1));
//        for (int i = 0; i < skeleton.getBoneCount(); i++) {
//            Bone bone = skeleton.getBone(i);
//            if (bone.getName().equals("左ひじ")) {
//                bone.setUserControl(true);
//                bone.setUserTransforms(new Vector3f(0f, 0f, 0f), q, new Vector3f(5, 0, 0));
//                bone.setUserTransforms(new Vector3f(0f, 0f, 0f), q, Vector3f.ZERO);
//                System.out.println("setBone 左ひじ");
//            }
//        }

        skeleton.updateWorldVectors();
        // here update the targets verticles if no hardware skinning supported

        offsetMatrices = skeleton.computeSkinningMatrices();
        {
            int i = 0;
            for (Matrix4f m : offsetMatrices) {
                for (int columnCount = 0; columnCount < 4; columnCount++) {
                    for (int rowCount = 0; rowCount < 4; rowCount++) {
//                        matrix4array[i++] = m.get(rowCount, columnCount);
                    }
                }
            }
        }
        //material.setParam("BoneMatrices", VarType.Matrix4Array, offsetMatrices);
//        material.setParam("BoneMatrices", VarType.Matrix4Array, offsetMatrices);
        for (int i = 0; i < targets.length; i++) {
            // only update targets with bone-vertex assignments
//            if (targets[i].getBuffer(Type.BoneIndex) != null)
//                softwareSkinUpdate(targets[i], offsetMatrices);
        }
        for (int i = 0; i < offsetMatrices.length; i++) {
            Matrix4f m = offsetMatrices[i];
            //m = new Matrix4f();
//            m.loadIdentity();
//            m.zero();
//            m.fillFloatBuffer(floatTextureBuf);
        }
        for (Spatial s : model.getChildren()) {
            if (s instanceof Geometry) {
                Geometry g = (Geometry) s;
                Material m = g.getMaterial();
                if (g.getMesh() instanceof PMDMesh) {
                    PMDMesh pmdMesh = (PMDMesh) g.getMesh();
                    for (int i = 0; i < pmdMesh.getBoneIndexArray().length; i++) {
                        pmdMesh.getBoneMatrixArray()[i].set(offsetMatrices[pmdMesh.getBoneIndexArray()[i]]);
                    }
                    //                m.setTexture("BoneParameter", texture);
                    //                System.out.println("setBoneParameter");
                    m.setParam("BoneMatrices", VarType.Matrix4Array, pmdMesh.getBoneMatrixArray());
                } else {
                    //g.setMesh(g.getMesh());
//                    g.updateModelBound();
                }
            }
        }
        updateSkinMesh(skinTargets[0]);
        for (PMDSkinMesh sm : skinTargets) {
//            sm.updateBound();
        }
//        model.updateModelBound();
//        model.updateGeometricState();
    }

    void updateSkinMesh(PMDSkinMesh skinMesh) {
        VertexBuffer vb = skinMesh.getBuffer(Type.Position);
        FloatBuffer fvb = (FloatBuffer) vb.getData();
        VertexBuffer nb = skinMesh.getBuffer(Type.Normal);
        FloatBuffer fnb = (FloatBuffer) nb.getData();

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
        }
        vb.setUpdateNeeded();
        nb.setUpdateNeeded();
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void softwareSkinUpdate(Mesh mesh, Matrix4f[] offsetMatrices) {
        int maxWeightsPerVert = mesh.getMaxNumWeights();
        int fourMinusMaxWeights = 4 - maxWeightsPerVert;

        // NOTE: This code assumes the vertex buffer is in bind pose
        // resetToBind() has been called this frame
        VertexBuffer vb = mesh.getBuffer(VertexBuffer.Type.Position);
        FloatBuffer fvb = (FloatBuffer) vb.getData();
        fvb.rewind();

        VertexBuffer nb = mesh.getBuffer(VertexBuffer.Type.Normal);
        FloatBuffer fnb = (FloatBuffer) nb.getData();
        fnb.rewind();

        // get boneIndexes and weights for mesh
        ByteBuffer ib = (ByteBuffer) mesh.getBuffer(VertexBuffer.Type.BoneIndex).getData();
        FloatBuffer wb = (FloatBuffer) mesh.getBuffer(VertexBuffer.Type.BoneWeight).getData();

        ib.rewind();
        wb.rewind();

        float[] weights = wb.array();
        byte[] indices = ib.array();
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
                    float weight = weights[idxWeights];
                    Matrix4f mat = offsetMatrices[indices[idxWeights++]];

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

//        mesh.updateBound();
    }

    public void resetToBind() {
        for (int i = 0; i < targets.length; i++) {
            Mesh mesh = targets[i];
            if (targets[i].getBuffer(Type.BindPosePosition) != null) {
//                VertexBuffer bi = mesh.getBuffer(Type.BoneIndex);
//                ByteBuffer bib = (ByteBuffer) bi.getData();
//                if (!bib.hasArray())
//                    mesh.prepareForAnim(true); // prepare for software animation

                VertexBuffer bindPos = mesh.getBuffer(Type.BindPosePosition);
                VertexBuffer bindNorm = mesh.getBuffer(Type.BindPoseNormal);
                VertexBuffer pos = mesh.getBuffer(Type.Position);
                VertexBuffer norm = mesh.getBuffer(Type.Normal);
                FloatBuffer pb = (FloatBuffer) pos.getData();
                FloatBuffer nb = (FloatBuffer) norm.getData();
                FloatBuffer bpb = (FloatBuffer) bindPos.getData();
                FloatBuffer bnb = (FloatBuffer) bindNorm.getData();
                pb.clear();
                nb.clear();
                bpb.clear();
                bnb.clear();
                pb.put(bpb).clear();
                nb.put(bnb).clear();
            }
        }
    }

    public Set<String> getSkinSet() {
        return skinMap.keySet();
    }

    public float getSkinWeight(String skinName) {
        return skinMap.get(skinName).getWeight();
    }

//    public void setSkinWeight(String skinName, float weight) {
//        Skin skin = skinMap.get(skinName);
//        skin.setWeight(weight);
//        for (PMDSkinVertData svd : skin.getSkinData().getSkinVertData()) {
//            javax.vecmath.Vector3f dist = skinPosArray[svd.getSkinVertIndex()];
////            dist.set(skinPosArrayOrig[svd.getSkinVertIndex()]);
////            dist.interpolate(svd.getSkinVertPos(), weight);
//            dist.set(svd.getSkinVertPos());
//            dist.scale(weight);
//            dist.add(skinPosArrayOrig[svd.getSkinVertIndex()]);
//        }
//    }

    public Matrix4f[] getOffsetMatrices() {
        return offsetMatrices;
    }

    public void showSkeleton(boolean flag) {
        if (flag) {
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", ColorRGBA.Green);
            mat.getAdditionalRenderState().setDepthTest(false);
            SkeletonWire sw = new SkeletonWire(skeleton);
            Geometry skeletonWireGeom = new Geometry("skeletonWire", sw);
            model.attachChild(skeletonWireGeom);
            skeletonWireGeom.setMaterial(mat);
            skeletonWireGeom.setQueueBucket(Bucket.Transparent);
        } else {
            if (skeletonLineNode != null) {
                skeletonLineNode.removeFromParent();
                skeletonLineNode = null;
            }
        }
    }
}
