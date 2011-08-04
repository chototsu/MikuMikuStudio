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
import com.jme3.asset.AssetNotFoundException;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.debug.SkeletonPoints;
import com.jme3.scene.debug.SkeletonWire;
import com.jme3.scene.shape.Box;
import com.jme3.shader.VarType;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import com.jme3.util.TempVars;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import projectkyoto.mmd.file.*;
import projectkyoto.mmd.file.util2.MeshConverter;
import projectkyoto.mmd.file.util2.MeshData;

/**
 *
 * @author kobayasi
 */
public class PMDLoaderGLSLSkinning2 {

    PMDModel model;
    PMDNode node;
    MeshConverter meshConverter;
    int meshCount = 1;
    AssetManager assetManager;
    List<PMDMesh> meshList = new ArrayList<PMDMesh>();
    List<PMDSkinMesh> skinMeshList = new ArrayList<PMDSkinMesh>();
    VertexBuffer skinvb;
    VertexBuffer skinvb2;
    VertexBuffer skinnb;
    VertexBuffer skinnb2;
    VertexBuffer skintb;
    Skin skinArray[];
    SkeletonControl skeletonControl;

    public PMDLoaderGLSLSkinning2(AssetManager assetManager, PMDModel model) {
        this.assetManager = assetManager;
        this.model = model;
//        System.out.println("vertexCount = " + model.getVertCount());
//        System.out.println("faceVertCount = " + model.getFaceVertCount());
        meshConverter = new MeshConverter(model);
        assetManager.registerLoader(com.jme3.texture.plugins.AWTLoader.class, "sph", "spa");
    }

    public PMDNode createNode(String name) {
        node = new PMDNode(name, model, assetManager);
        meshCount = 1;
        meshConverter.convertMesh();
        for (MeshData md : meshConverter.getMeshDataList()) {
            PMDMesh mesh = createMesh(md);
            PMDGeometry geom = new PMDGeometry("geom" + meshCount++);
            geom.setMesh(mesh);
            PMDMaterial pmdMaterial = md.getMaterial();
            setupMaterial(pmdMaterial, geom);
            node.attachChild(geom);
            meshList.add(mesh);
        }
        createSkinCommonVertData();
        for (PMDMaterial pmdMaterial : meshConverter.getSkinMeshData().getIndexMap().keySet()) {
            PMDSkinMesh mesh = createSkinMesh(pmdMaterial);
            PMDGeometry geom = new PMDGeometry("geom" + meshCount++);
            geom.setMesh(mesh);
            setupMaterial(pmdMaterial, geom);
            node.attachChild(geom);
            skinMeshList.add(mesh);
        }
        createSkinArray();
        createSkeleton();
        node.setSkinData(skinMeshList.toArray(new PMDSkinMesh[skinMeshList.size()]), meshConverter.getSkinMeshData().getVertexList(), skinArray);
        node.targets = meshList.toArray(new PMDMesh[meshList.size()]);
        node.init();
        return node;
    }

    void createSkinCommonVertData() {
        skinvb = new VertexBuffer(VertexBuffer.Type.Position);
        FloatBuffer skinvfb = BufferUtils.createFloatBuffer(meshConverter.getSkinMeshData().getVertexList().size() * 3);
        skinvb.setupData(VertexBuffer.Usage.Static, 3, VertexBuffer.Format.Float, skinvfb);

        skinvb2 = new VertexBuffer(VertexBuffer.Type.Position);
        FloatBuffer skinvfb2 = BufferUtils.createFloatBuffer(meshConverter.getSkinMeshData().getVertexList().size() * 3);
        skinvb2.setupData(VertexBuffer.Usage.Static, 3, VertexBuffer.Format.Float, skinvfb2);
        
        skinnb = new VertexBuffer(VertexBuffer.Type.Normal);
        FloatBuffer skinnfb = BufferUtils.createFloatBuffer(meshConverter.getSkinMeshData().getVertexList().size() * 3);
        skinnb.setupData(VertexBuffer.Usage.Static, 3, VertexBuffer.Format.Float, skinnfb);

        skinnb2 = new VertexBuffer(VertexBuffer.Type.Normal);
        FloatBuffer skinnfb2 = BufferUtils.createFloatBuffer(meshConverter.getSkinMeshData().getVertexList().size() * 3);
        skinnb2.setupData(VertexBuffer.Usage.Static, 3, VertexBuffer.Format.Float, skinnfb2);
        
        skintb = new VertexBuffer(VertexBuffer.Type.TexCoord);
        FloatBuffer skintfb = BufferUtils.createFloatBuffer(meshConverter.getSkinMeshData().getVertexList().size() * 2);
        skintb.setupData(VertexBuffer.Usage.Static, 2, VertexBuffer.Format.Float, skintfb);
        for (PMDVertex v : meshConverter.getSkinMeshData().getVertexList()) {
            skinvfb.put(v.getPos().x).put(v.getPos().y).put(v.getPos().z);
            skinnfb.put(v.getNormal().x).put(v.getNormal().y).put(v.getNormal().z);
            skintfb.put(v.getUv().getU()).put(1f - v.getUv().getV());
        }
        skintb.setUpdateNeeded();
    }

    PMDSkinMesh createSkinMesh(PMDMaterial pmdMaterial) {
        PMDSkinMesh mesh = new PMDSkinMesh();
        List<Integer> indexList = meshConverter.getSkinMeshData().getIndexMap().get(pmdMaterial);
        mesh.setMode(Mesh.Mode.Triangles);
        mesh.setBuffer(skinvb);
        mesh.setSkinvb2(skinvb2);
        mesh.setBuffer(skinnb);
        mesh.setSkinnb2(skinnb2);
        mesh.setBuffer(skintb);
        VertexBuffer ib = new VertexBuffer(VertexBuffer.Type.Index);
        ShortBuffer isb = BufferUtils.createShortBuffer(indexList.size());
        for (Integer index : indexList) {
            isb.put(index.shortValue());
        }
        ib.setupData(VertexBuffer.Usage.Static, 1, VertexBuffer.Format.UnsignedShort, isb);
        mesh.setBuffer(ib);
        return mesh;
    }

    PMDMesh createMesh(MeshData md) {
        PMDMesh mesh = new PMDMesh();
        mesh.setMode(Mesh.Mode.Triangles);
        VertexBuffer vb = new VertexBuffer(VertexBuffer.Type.Position);
        FloatBuffer vfb = BufferUtils.createFloatBuffer(md.getVertexList().size() * 3);
        VertexBuffer nb = new VertexBuffer(VertexBuffer.Type.Normal);
        FloatBuffer nfb = BufferUtils.createFloatBuffer(md.getVertexList().size() * 3);

        VertexBuffer bvb = new VertexBuffer(VertexBuffer.Type.BindPosePosition);
        FloatBuffer bvfb = BufferUtils.createFloatBuffer(md.getVertexList().size() * 3);
        VertexBuffer bnb = new VertexBuffer(VertexBuffer.Type.BindPoseNormal);
        FloatBuffer bnfb = BufferUtils.createFloatBuffer(md.getVertexList().size() * 3);

        VertexBuffer tb = new VertexBuffer(VertexBuffer.Type.TexCoord);

        FloatBuffer tfb = BufferUtils.createFloatBuffer(md.getVertexList().size() * 2);
        VertexBuffer wb = new VertexBuffer(VertexBuffer.Type.BoneWeight);
        FloatBuffer wfb = BufferUtils.createFloatBuffer(md.getVertexList().size() * 4);
        VertexBuffer ib = new VertexBuffer(VertexBuffer.Type.Index);
        ShortBuffer isb = BufferUtils.createShortBuffer(md.getIndexList().size());
        VertexBuffer bib = new VertexBuffer(VertexBuffer.Type.BoneIndex);
        ShortBuffer bisb = BufferUtils.createShortBuffer(md.getIndexList().size() * 4);
        for (PMDVertex v : md.getVertexList()) {
            vfb.put(v.getPos().x).put(v.getPos().y).put(v.getPos().z);
            nfb.put(v.getNormal().x).put(v.getNormal().y).put(v.getNormal().z);

            bvfb.put(v.getPos().x).put(v.getPos().y).put(v.getPos().z);
            bnfb.put(v.getNormal().x).put(v.getNormal().y).put(v.getNormal().z);

            tfb.put(v.getUv().getU()).put(1f - v.getUv().getV());
            float weight = (float) v.getBoneWeight() / 100.0f;
            wfb.put(weight).put(1f - weight).put(0).put(0);
            bisb.put((short) md.getBoneList().indexOf(v.getBoneNum1())).put((short) md.getBoneList().indexOf(v.getBoneNum2())).put((short) 0).put((short) 0);
        }
        for (Integer index : md.getIndexList()) {
            isb.put(index.shortValue());
//            System.out.println("index = "+index);
        }
//        System.out.println("isb.capacity() = " + isb.capacity());
//        System.out.println("isb.capacity() = " + md.getIndexList().size());
        vb.setupData(VertexBuffer.Usage.Static, 3, VertexBuffer.Format.Float, vfb);
        nb.setupData(VertexBuffer.Usage.Static, 3, VertexBuffer.Format.Float, nfb);

        bvb.setupData(VertexBuffer.Usage.CpuOnly, 3, VertexBuffer.Format.Float, bvfb);
        bnb.setupData(VertexBuffer.Usage.CpuOnly, 3, VertexBuffer.Format.Float, bnfb);

        tb.setupData(VertexBuffer.Usage.Static, 2, VertexBuffer.Format.Float, tfb);
        wb.setupData(VertexBuffer.Usage.Static, 4, VertexBuffer.Format.Float, wfb);
        ib.setupData(VertexBuffer.Usage.Static, 1, VertexBuffer.Format.UnsignedShort, isb);
        bib.setupData(VertexBuffer.Usage.Static, 4, VertexBuffer.Format.Short, bisb);
        mesh.setBuffer(vb);
        mesh.setBuffer(nb);

        mesh.setBuffer(bvb);
        mesh.setBuffer(bnb);

        mesh.setBuffer(tb);
        mesh.setBuffer(wb);
        mesh.setBuffer(ib);
        mesh.setBuffer(bib);
        short[] indexArray = new short[meshConverter.getMaxBoneSize()];
        for (int i = 0; i < indexArray.length; i++) {
            if (i < md.getBoneList().size()) {
                indexArray[i] = md.getBoneList().get(i).shortValue();
            } else {
                indexArray[i] = 0;
            }
        }
        mesh.setBoneIndexArray(indexArray);
        mesh.setBoneMatrixArray(new Matrix4f[indexArray.length]);
        for (int i = 0; i < mesh.getBoneMatrixArray().length; i++) {
            mesh.getBoneMatrixArray()[i] = new Matrix4f();
            mesh.getBoneMatrixArray()[i].loadIdentity();
        }
        return mesh;
    }

    void setupMaterial(PMDMaterial m, PMDGeometry geom) {
        Material mat;
        if (geom.getMesh() instanceof PMDSkinMesh) {
//            mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
            mat = createMaterial(m, false);
            geom.setMaterial(mat);
            geom.setGlslSkinningMaterial(mat);
            geom.setNoSkinningMaterial(mat);
        } else {
            mat = createMaterial(m, true);
            geom.setMaterial(mat);
            geom.setGlslSkinningMaterial(mat);
            mat = createMaterial(m, false);
            geom.setNoSkinningMaterial(mat);
        }
        geom.setPmdMaterial(m);
        if (m.getMaterial().getFaceColor().getAlpha() < 1f) {
            geom.setQueueBucket(Bucket.Transparent);
        } else {
            geom.setQueueBucket(Bucket.Inherit);
        }
    }

    Material createMaterial(PMDMaterial m, boolean skinning) {
        Material mat;
        if (m.getMaterial().getFaceColor().getAlpha() < 1f) {
            if (!skinning) {
                mat = new Material(assetManager, "MatDefs/pmd/pmd_no_skinning_alpha.j3md");
            } else {
                mat = new Material(assetManager, "MatDefs/pmd/pmd_alpha.j3md");
            }
        } else {
            if (!skinning) {
                mat = new Material(assetManager, "MatDefs/pmd/pmd_no_skinning.j3md");
            } else {
                mat = new Material(assetManager, "MatDefs/pmd/pmd.j3md");
            }
        }
        float alpha = m.getMaterial().getFaceColor().getAlpha();
        if (alpha > 0.99f) {
            alpha = 1f;
        }
        ColorRGBA ambientColor = new ColorRGBA(m.getMaterial().getAmbientColor().getRed(),
                m.getMaterial().getAmbientColor().getGreen(), m.getMaterial().getAmbientColor().getBlue(), alpha);
        ColorRGBA diffuseColor = new ColorRGBA(m.getMaterial().getFaceColor().getRed(),
                m.getMaterial().getFaceColor().getGreen(), m.getMaterial().getFaceColor().getBlue(), alpha);
        ColorRGBA ambientAndDiffuseColor = ambientColor.add(diffuseColor);
        ambientAndDiffuseColor.multLocal(0.5f);
        ambientAndDiffuseColor.a = alpha;
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Ambient", ambientAndDiffuseColor);
        mat.setColor("Specular", new ColorRGBA(m.getMaterial().getSpecularColor().getRed(),
                m.getMaterial().getSpecularColor().getGreen(), m.getMaterial().getSpecularColor().getBlue(), alpha));
        mat.setColor("Diffuse", ambientAndDiffuseColor);
        mat.setFloat("Shininess", m.getMaterial().getPower());
        if (m.getTextureFileName().length() > 0) {
            StringTokenizer st = new StringTokenizer(m.getTextureFileName(), "*");
            System.out.println("m.getTextureFileName() = " + m.getTextureFileName());
            while (st.hasMoreElements()) {
                String fileName = st.nextToken();
                System.out.println("fileName = " + fileName);
                String s = fileName.substring(fileName.indexOf('.') + 1);
                Texture texture = assetManager.loadTexture("Model/" + fileName /*
                         * m.getTextureFileName()
                         */);
                s = s.toLowerCase();
                if (s.equals("spa")) {
                    texture.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
                    mat.setTexture("SphereMap_A", texture);
                } else if (s.equals("sph")) {
                    texture.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
                    mat.setTexture("SphereMap_H", texture);
                } else {
//                    texture.setWrap(Texture.WrapMode.Repeat);
                    mat.setTexture("DiffuseMap", texture);
                }
            }
        }
        int toonIndex = m.getToonIndex();
        Texture toonTexture = null;
        if (toonIndex >= 0) {
            String extToonName = model.getToonTextureList().getToonFileName()[toonIndex];
            try {
                toonTexture = assetManager.loadTexture("/Model/" + extToonName);
            } catch (AssetNotFoundException ex) {
                String toonname = null;
                switch (toonIndex) {
                    case 0:
                        toonname = "toon01.bmp";
                        break;
                    case 1:
                        toonname = "toon02.bmp";
                        break;
                    case 2:
                        toonname = "toon03.bmp";
                        break;
                    case 3:
                        toonname = "toon04.bmp";
                        break;
                    case 4:
                        toonname = "toon05.bmp";
                        break;
                    case 5:
                        toonname = "toon06.bmp";
                        break;
                    case 6:
                        toonname = "toon07.bmp";
                        break;
                    case 7:
                        toonname = "toon08.bmp";
                        break;
                    case 8:
                        toonname = "toon09.bmp";
                        break;
                    case 9:
                        toonname = "toon10.bmp";
                        break;
                }
                if (toonname != null) {
                    toonTexture = assetManager.loadTexture("toon/" + toonname);
                }
            }
        }
        if (toonTexture != null) {
            toonTexture.setWrap(Texture.WrapAxis.S, Texture.WrapMode.EdgeClamp);
            toonTexture.setWrap(Texture.WrapAxis.T, Texture.WrapMode.EdgeClamp);
            mat.setTexture("ColorRamp", toonTexture);
        }
        if (m.getEdgeFlag() != 0 /*
                 * && !(geom.getMesh() instanceof PMDSkinMesh)
                 */) {
//            mat.setFloat("EdgeSize", 0.02f);
            mat.setFloat("EdgeSize", 0.01f);
        } else {
            mat.setFloat("EdgeSize", 0f);
        }
//        mat.setParam("VertexLighting", VarType.Int, new Integer(1));
//        geom.setMaterial(mat);
//        geom.setPmdMaterial(m);
//        mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Back);
//        mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Back);
//        mat.getAdditionalRenderState().setWireframe(true);
        if (m.getMaterial().getFaceColor().getAlpha() < 1f) {
            mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
            mat.getAdditionalRenderState().setAlphaTest(true);
//                    mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Back);
        } else {
            mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
//                    mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Back);
//                    mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
            mat.getAdditionalRenderState().setAlphaTest(true);
        }
        return mat;
    }

    void createSkeleton() {
        TempVars temp = TempVars.get();
        Bone[] boneArray = new Bone[model.getBoneList().getBoneCount()];
        int boneIndex = 0;
        for (PMDBone pmdBone : model.getBoneList().getBones()) {
            Bone bone = new Bone(pmdBone.getBoneName());
            bone.setUserControl(true);
            Vector3f translation = new Vector3f(pmdBone.getBoneHeadPos().x, pmdBone.getBoneHeadPos().y, pmdBone.getBoneHeadPos().z);
            Quaternion rotation = new Quaternion();
            boneArray[boneIndex++] = bone;
        }
        boneIndex = 0;
        for (PMDBone pmdBone : model.getBoneList().getBones()) {
            Bone bone = boneArray[boneIndex];
            if (pmdBone.getParentBoneIndex() < boneArray.length) {
                Bone parent = boneArray[pmdBone.getParentBoneIndex()];
                PMDBone parentPMDBone = model.getBoneList().getBones()[pmdBone.getParentBoneIndex()];
                parent.addChild(bone);
                Vector3f v1 = temp.vect1; // new Vector3f();
                Vector3f v2 = temp.vect2; //new Vector3f();
                v1.set(pmdBone.getBoneHeadPos().x, pmdBone.getBoneHeadPos().y, pmdBone.getBoneHeadPos().z);
                v2.set(parentPMDBone.getBoneHeadPos().x, parentPMDBone.getBoneHeadPos().y, parentPMDBone.getBoneHeadPos().z);
                v1.subtractLocal(v2);

                bone.setBindTransforms(v1, Quaternion.IDENTITY, new Vector3f(1, 1, 1));
            } else {
                Vector3f v1 = temp.vect1; //new Vector3f();
                v1.set(pmdBone.getBoneHeadPos().x, pmdBone.getBoneHeadPos().y, pmdBone.getBoneHeadPos().z);
                bone.setBindTransforms(v1, Quaternion.IDENTITY, new Vector3f(1, 1, 1));
            }

            boneIndex++;
        }

        Skeleton skeleton = new Skeleton(boneArray);
        PMDMesh meshes[] = meshList.toArray(new PMDMesh[meshList.size()]);

        Quaternion q = new Quaternion();
        q = q.fromAngleNormalAxis((float) Math.PI / 8, new Vector3f(0, 0, 1));
        node.skeleton = skeleton;
    }

    void createSkinArray() {
        List<Skin> skinList = new ArrayList<Skin>();
        for (int i = 0; i < model.getSkinCount(); i++) {
            PMDSkinData pmdSkinData = model.getSkinData()[i];
            if (pmdSkinData.getSkinType() != 0) {
                Skin skin = new Skin(node, pmdSkinData.getSkinName());
                skin.setSkinData(pmdSkinData);
                skinList.add(skin);
            }
        }
        skinArray = skinList.toArray(new Skin[skinList.size()]);
    }

    public Node createBoneNode() {
        Node boneNode = new Node("boneMarks");
        Spatial boneMarkArray[] = new Spatial[model.getBoneList().getBoneCount()];
        for (int i = 0; i < model.getBoneList().getBoneCount(); i++) {
            PMDBone bone = model.getBoneList().getBones()[i];
            PMDGeometry boneMark = new PMDGeometry(bone.getBoneName());
            Node boneMarkNode = new Node();
            boneMark.setMesh(new Box(4f, 4f, 0.1f));
//            boneMark.setLocalTranslation(bone.getBoneHeadPos().x,
//                    bone.getBoneHeadPos().y, bone.getBoneHeadPos().z);
//            boneMark.move(bone.getBoneHeadPos().x,
//                    bone.getBoneHeadPos().y, bone.getBoneHeadPos().z);
            boneMarkArray[i] = boneMarkNode;
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", ColorRGBA.Blue);
            boneMark.setMaterial(mat);
            boneMarkNode.attachChild(boneMark);
//            if (bone.getBoneName().contains("目")) {
            boneNode.attachChild(boneMarkNode);
//            }
        }
        PMDBoneMarkControl bmc = new PMDBoneMarkControl(skeletonControl, boneMarkArray, model.getBoneList().getBones());
        node.addControl(bmc);
        PMDGeometry skeletonGeom = new PMDGeometry();
        SkeletonPoints sp = new SkeletonPoints(skeletonControl.skeleton);
        skeletonGeom.setMesh(sp);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Green);
        mat.getAdditionalRenderState().setDepthTest(false);
        skeletonGeom.setMaterial(mat);
        node.attachChild(skeletonGeom);

        SkeletonWire sw = new SkeletonWire(skeletonControl.skeleton);
        PMDGeometry skeletonWireGeom = new PMDGeometry("skeletonWire", sw);
        node.attachChild(skeletonWireGeom);
        skeletonWireGeom.setMaterial(mat);

        sp.updateGeometry();
        sw.updateGeometry();


        return boneNode;
    }
}
