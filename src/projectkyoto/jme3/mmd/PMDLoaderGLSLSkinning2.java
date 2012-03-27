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
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
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
import com.jme3.system.JmeSystem;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import com.jme3.util.TempVars;
import com.sun.jmx.remote.util.OrderClassLoaders;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import org.lwjgl.opengl.GL11;
import projectkyoto.mmd.file.*;
import projectkyoto.mmd.file.pmn.PMNData;
import projectkyoto.mmd.file.util2.MeshConverter;
import projectkyoto.mmd.file.util2.MeshData;

/**
 *
 * @author kobayasi
 */
public class PMDLoaderGLSLSkinning2 implements AssetLoader{

    PMDModel model;
    PMDNode node;
    MeshConverter meshConverter;
    int meshCount = 1;
    AssetManager assetManager;
    String folderName;
    List<PMDMesh> meshList = new ArrayList<PMDMesh>();
    List<PMDSkinMesh> skinMeshList = new ArrayList<PMDSkinMesh>();
    VertexBuffer skinvb;
    VertexBuffer skinvb2;
    VertexBuffer skinnb;
    VertexBuffer skinnb2;
    VertexBuffer skintb;
    VertexBuffer skinbib;
    VertexBuffer skinwb;
    int[] skinIndexArray;
    Skin skinArray[];
    SkeletonControl skeletonControl;
    HashMap<String, Texture> textureMap = new HashMap<String, Texture>();
    public PMDLoaderGLSLSkinning2() {
    }
    public PMDLoaderGLSLSkinning2(AssetManager assetManager, PMDModel model) {
        this.assetManager = assetManager;
        this.model = model;
        folderName = "/Model/";
//        System.out.println("vertexCount = " + model.getVertCount());
//        System.out.println("faceVertCount = " + model.getFaceVertCount());
//        assetManager.registerLoader(com.jme3.texture.plugins.AWTLoader.class, "sph", "spa");
    }
    public PMDLoaderGLSLSkinning2(AssetManager assetManager, MeshConverter mc) {
        this.assetManager = assetManager;
        this.model = mc.getModel();
        this.meshConverter = mc;
        folderName = "/Model/";
//        System.out.println("vertexCount = " + model.getVertCount());
//        System.out.println("faceVertCount = " + model.getFaceVertCount());
//        assetManager.registerLoader(com.jme3.texture.plugins.AWTLoader.class, "sph", "spa");
    }
    public void init() {
//        model = null;
        node = null;
        if (meshConverter == null) {
            meshConverter = new MeshConverter(model);
            meshConverter.convertMesh();
        }
        meshCount = 1;
//        assetManager = null;
//        folderName = null;
        meshList.clear();
        skinMeshList.clear();
        skinvb = null;
        skinvb2 = null;
        skinnb = null;
        skinnb2 = null;
        skintb = null;
        skeletonControl = null;
        skinArray = null;
    }

    public PMDNode createNode(String name) {
        init();
        node = new PMDNode(name, model, assetManager);
        meshCount = 1;
//        System.out.println("child size = "+node.getChildren().size()+" "+meshList.size()+" "+skinMeshList.size());
        node.pmdGeometryArray = new PMDGeometry[meshConverter.getMeshDataList().size()];
        int pmdGeometryIndex = 0;
//        GeometryOptimizer go = GeometryOptimizer.createNewInstance();
        for(int i=0;i<meshConverter.getMeshDataList().size();i++) {
            MeshData md = meshConverter.getMeshDataList().get(i);
            PMDMesh mesh = createMesh_old(md);
            PMDGeometry geom = new PMDGeometry("geom" + meshCount++);
            geom.setMesh(mesh);
            PMDMaterial pmdMaterial = md.getMaterial();
            setupMaterial(pmdMaterial, geom);
            node.attachChild(geom);
            meshList.add(mesh);
            node.pmdGeometryArray[pmdGeometryIndex++] = geom;
            meshConverter.getMeshDataList().set(i, null);
//            go.add(mesh);
//            mesh.setInterleaved();    
        }
//        go.optimize3();
        createSkinCommonVertData();
        int numBones = meshConverter.getMaxBoneSize();
        if (meshConverter.getSkinMeshData().getBoneList().size() > numBones) {
            if (meshConverter.getSkinMeshData().getBoneList().size() > 56) {
                throw new TooManyBonesException(Integer.toString(meshConverter.getSkinMeshData().getBoneList().size()));
            }
            numBones = meshConverter.getSkinMeshData().getBoneList().size();
        }
        for (PMDMaterial pmdMaterial : meshConverter.getSkinMeshData().getIndexMap().keySet()) {
            PMDSkinMesh mesh = createSkinMesh(pmdMaterial);
            PMDGeometry geom = new PMDGeometry("geom" + meshCount++);
            geom.setMesh(mesh);
            setupMaterial(pmdMaterial, geom);
            geom.getMaterial().setInt("NumBones", numBones);
            node.attachChild(geom);
            skinMeshList.add(mesh);
        }
//        System.out.println("child size = "+node.getChildren().size()+" "+meshList.size()+" "+skinMeshList.size());
        createSkinArray();
        createSkeleton();
        node.setSkinData(skinMeshList.toArray(new PMDSkinMesh[skinMeshList.size()]), meshConverter.getSkinMeshData().getVertexList(), skinArray);
        node.targets = meshList.toArray(new PMDMesh[meshList.size()]);
        meshConverter = null;
        model.setVertexBuffer(null);
//        go.optimize();
        FloatBuffer fb = (FloatBuffer)skinvb.getData();
        node.skinPosBuffer = BufferUtils.createFloatBuffer(fb.limit());
        projectkyoto.jme3.mmd.nativelib.SkinUtil.copy(fb, node.skinPosBuffer, fb.limit() * 4);
        node.init();
        node.calcOffsetMatrices();
        node.update();
        return node;
    }

    void createSkinCommonVertData() {
        skinvb = new VertexBuffer(VertexBuffer.Type.Position);
        FloatBuffer skinvfb = BufferUtils.createFloatBuffer(meshConverter.getSkinMeshData().getVertexList().size() * 3);
        skinvb.setupData(VertexBuffer.Usage.Dynamic, 3, VertexBuffer.Format.Float, skinvfb);

        skinvb2 = new VertexBuffer(VertexBuffer.Type.Position);
        FloatBuffer skinvfb2 = BufferUtils.createFloatBuffer(meshConverter.getSkinMeshData().getVertexList().size() * 3);
        skinvb2.setupData(VertexBuffer.Usage.Dynamic, 3, VertexBuffer.Format.Float, skinvfb2);
        
        skinnb = new VertexBuffer(VertexBuffer.Type.Normal);
        FloatBuffer skinnfb = BufferUtils.createFloatBuffer(meshConverter.getSkinMeshData().getVertexList().size() * 3);
        skinnb.setupData(VertexBuffer.Usage.Static, 3, VertexBuffer.Format.Float, skinnfb);

        skinnb2 = new VertexBuffer(VertexBuffer.Type.Normal);
        FloatBuffer skinnfb2 = BufferUtils.createFloatBuffer(meshConverter.getSkinMeshData().getVertexList().size() * 3);
        skinnb2.setupData(VertexBuffer.Usage.Static, 3, VertexBuffer.Format.Float, skinnfb2);
        
        skintb = new VertexBuffer(VertexBuffer.Type.TexCoord);
        FloatBuffer skintfb = BufferUtils.createFloatBuffer(meshConverter.getSkinMeshData().getVertexList().size() * 2);
        skintb.setupData(VertexBuffer.Usage.Static, 2, VertexBuffer.Format.Float, skintfb);

        skinbib = new VertexBuffer(VertexBuffer.Type.BoneIndex);
        ShortBuffer skinbisb = BufferUtils.createShortBuffer(meshConverter.getSkinMeshData().getVertexList().size() * 2);
        skinbib.setupData(VertexBuffer.Usage.Static, 2, VertexBuffer.Format.UnsignedShort, skinbisb);

        skinwb = new VertexBuffer(VertexBuffer.Type.BoneWeight);
        FloatBuffer wfb = BufferUtils.createFloatBuffer(meshConverter.getSkinMeshData().getVertexList().size() * 2);
        skinwb.setupData(VertexBuffer.Usage.Static, 2, VertexBuffer.Format.Float, wfb);

        for (PMDVertex v : meshConverter.getSkinMeshData().getVertexList()) {
            skinvfb.put(v.getPos().x).put(v.getPos().y).put(v.getPos().z);
            skinnfb.put(v.getNormal().x).put(v.getNormal().y).put(v.getNormal().z);
                float f1 = v.getUv().getU();
                float f2 = v.getUv().getV();
//                tfb.put(v.getUv().getU()).put(1f - v.getUv().getV());
                f1 = f1 - FastMath.floor(f1);
                f2 = f2 - FastMath.floor(f2);
                f2 = 1 - f2;
                skintfb.put(f1).put(f2);
//            skintfb.put(v.getUv().getU()).put(1f - v.getUv().getV());
//            skinbisb.put((short) meshConverter.getSkinMeshData()
//                    .getBoneList().indexOf(v.getBoneNum1()))
//                    .put((short) meshConverter.getSkinMeshData()
//                    .getBoneList().indexOf(v.getBoneNum2()));
            short b1 = (short)meshConverter.getSkinMeshData().getBoneList().indexOf(v.getBoneNum1());
            short b2 = (short)meshConverter.getSkinMeshData().getBoneList().indexOf(v.getBoneNum2());
            if (b1 < 0) b1 = 0;
            if (b2 < 0) b2 = 0;
            skinbisb.put(b1).put(b2);
            float weight = (float) v.getBoneWeight() / 100.0f;
            wfb.put(weight).put(1f - weight);
        }
        skinvfb.position(0);
        skinvfb2.position(0);
        skinvfb2.put(skinvfb);
        skinnfb.position(0);
        skinnfb2.position(0);
        skinnfb2.put(skinnfb);
        skinIndexArray = new int[meshConverter.getSkinMeshData().getBoneList().size()];
        for (int i = 0; i < skinIndexArray.length; i++) {
            if (i < meshConverter.getSkinMeshData().getBoneList().size()) {
                skinIndexArray[i] = meshConverter.getSkinMeshData().getBoneList().get(i).shortValue();
            } else {
                skinIndexArray[i] = 0;
            }
        }
    }

    PMDSkinMesh createSkinMesh(PMDMaterial pmdMaterial) {
        boolean textureFlag = true;
        if (pmdMaterial.getTextureFileName().length() == 0) {
            textureFlag = false;
        }
        PMDSkinMesh mesh = new PMDSkinMesh();
        List<Integer> indexList = meshConverter.getSkinMeshData().getIndexMap().get(pmdMaterial);
        mesh.setMode(Mesh.Mode.Triangles);
        mesh.setBuffer(skinvb);
        mesh.setSkinvb2(skinvb2);
        mesh.setBuffer(skinnb);
        mesh.setSkinnb2(skinnb2);
        if (textureFlag) {
            mesh.setBuffer(skintb);
        }
        mesh.setBuffer(skinbib);
        mesh.setBuffer(skinwb);
        VertexBuffer ib = new VertexBuffer(VertexBuffer.Type.Index);
        ShortBuffer isb = BufferUtils.createShortBuffer(indexList.size());
        for (Integer index : indexList) {
            isb.put(index.shortValue());
        }
        ib.setupData(VertexBuffer.Usage.Static, 1, VertexBuffer.Format.UnsignedShort, isb);
        mesh.setBuffer(ib);
        mesh.setBoneIndexArray(skinIndexArray);
        mesh.setBoneMatrixArray(new Matrix4f[skinIndexArray.length]);
        for (int i = 0; i < mesh.getBoneMatrixArray().length; i++) {
            mesh.getBoneMatrixArray()[i] = new Matrix4f();
            mesh.getBoneMatrixArray()[i].loadIdentity();
        }
        return mesh;
    }

    PMDMesh createMesh_old(MeshData md) {
        boolean textureFlag = true;
        if (md.getMaterial().getTextureFileName().length() == 0) {
            textureFlag = false;
        }
        PMDMesh mesh = new PMDMesh();
        mesh.setMode(Mesh.Mode.Triangles);
        VertexBuffer vb = new VertexBuffer(VertexBuffer.Type.Position);
        FloatBuffer vfb = BufferUtils.createFloatBuffer(md.getVertIndexList().size() * 3);
        VertexBuffer nb = new VertexBuffer(VertexBuffer.Type.Normal);
        FloatBuffer nfb = BufferUtils.createFloatBuffer(md.getVertIndexList().size() * 3);

//        VertexBuffer bvb = new VertexBuffer(VertexBuffer.Type.BindPosePosition);
//        FloatBuffer bvfb = BufferUtils.createFloatBuffer(md.getVertexList().size() * 3);
//        VertexBuffer bnb = new VertexBuffer(VertexBuffer.Type.BindPoseNormal);
//        FloatBuffer bnfb = BufferUtils.createFloatBuffer(md.getVertexList().size() * 3);

        VertexBuffer tb = new VertexBuffer(VertexBuffer.Type.TexCoord);

        FloatBuffer tfb = null;
        if (textureFlag ) {
            tfb = BufferUtils.createFloatBuffer(md.getVertIndexList().size() * 2);
        }
        VertexBuffer wb = new VertexBuffer(VertexBuffer.Type.BoneWeight);
        FloatBuffer wfb = BufferUtils.createFloatBuffer(md.getVertIndexList().size() * 2);
        VertexBuffer ib = new VertexBuffer(VertexBuffer.Type.Index);
        ShortBuffer isb = BufferUtils.createShortBuffer(md.getIndexList().size()/*md.getMaterial().getFaceVertCount()*/);
        VertexBuffer bib = new VertexBuffer(VertexBuffer.Type.BoneIndex);
        ShortBuffer bisb = BufferUtils.createShortBuffer(md.getVertIndexList().size() * 2);
        PMDVertex v = new PMDVertex();
        for (Integer vertIndex : md.getVertIndexList()) {
            model.getVertex(vertIndex, v);
            vfb.put(v.getPos().x).put(v.getPos().y).put(v.getPos().z);
            nfb.put(v.getNormal().x).put(v.getNormal().y).put(v.getNormal().z);

//            bvfb.put(v.getPos().x).put(v.getPos().y).put(v.getPos().z);
//            bnfb.put(v.getNormal().x).put(v.getNormal().y).put(v.getNormal().z);
            if (textureFlag) {
                float f1 = v.getUv().getU();
                float f2 = v.getUv().getV();
//                tfb.put(v.getUv().getU()).put(1f - v.getUv().getV());
                f1 = f1 - FastMath.floor(f1);
                f2 = f2 - FastMath.floor(f2);
                f2 = 1 - f2;
                tfb.put(f1).put(f2);
            }
            float weight = (float) v.getBoneWeight() / 100.0f;
            wfb.put(weight).put(1f - weight);
            short b1 = (short)md.getBoneList().indexOf(v.getBoneNum1());
            short b2 = (short)md.getBoneList().indexOf(v.getBoneNum2());
            if (b1 < 0) b1 = 0;
            if (b2 < 0) b2 = 0;
            bisb.put(b1).put(b2);
//            bisb.put((short) md.getBoneList().indexOf(v.getBoneNum1())).put((short) md.getBoneList().indexOf(v.getBoneNum2()));
//            if (( weight != 0 && md.getBoneList().indexOf(v.getBoneNum1()) < 0)
//                    || (weight != 1 && md.getBoneList().indexOf(v.getBoneNum2())<0)){
//                System.out.println("ERROR!! "+v.getBoneNum1()+" "+v.getBoneNum2());
//                System.out.println(""+md.getBoneList().indexOf(v.getBoneNum1())+" "+md.getBoneList().indexOf(v.getBoneNum2()));
//                System.out.println("weight = "+weight);
//            }
        }
        for (Integer index : md.getIndexList()) {
            isb.put(index.shortValue());
//            System.out.println("index = "+index);
        }
//        System.out.println("isb.capacity() = " + isb.capacity());
//        System.out.println("isb.capacity() = " + md.getIndexList().size());
        vb.setupData(VertexBuffer.Usage.Static, 3, VertexBuffer.Format.Float, vfb);
        nb.setupData(VertexBuffer.Usage.Static, 3, VertexBuffer.Format.Float, nfb);

//        bvb.setupData(VertexBuffer.Usage.CpuOnly, 3, VertexBuffer.Format.Float, bvfb);
//        bnb.setupData(VertexBuffer.Usage.CpuOnly, 3, VertexBuffer.Format.Float, bnfb);
        if (textureFlag) {
            tb.setupData(VertexBuffer.Usage.Static, 2, VertexBuffer.Format.Float, tfb);
        }
        wb.setupData(VertexBuffer.Usage.Static, 2, VertexBuffer.Format.Float, wfb);
        ib.setupData(VertexBuffer.Usage.Static, 1, VertexBuffer.Format.UnsignedShort, isb);
        bib.setupData(VertexBuffer.Usage.Static, 2, VertexBuffer.Format.UnsignedShort, bisb);
        mesh.setBuffer(vb);
        mesh.setBuffer(nb);
        
        mesh.setVbBackup(vb);
        mesh.setNbBackup(nb);

//        mesh.setBuffer(bvb);
//        mesh.setBuffer(bnb);
        if (textureFlag) {
            mesh.setBuffer(tb);
        }
        mesh.setBuffer(wb);
        mesh.setBuffer(ib);
        mesh.setBuffer(bib);
        int[] indexArray = new int[md.getBoneList().size()];
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
        if (false /*geom.getMesh() instanceof PMDSkinMesh*/) {
//            mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
            mat = createMaterial(m, false);
            geom.setMaterial(mat);
            geom.setGlslSkinningMaterial(mat);
            geom.setNoSkinningMaterial(mat);
        } else {
//            PMDMesh mesh = (PMDMesh)geom.getMesh();
            mat = createMaterial(m, true);
            geom.setMaterial(mat);
            geom.setGlslSkinningMaterial(mat);
//            mat.setInt("NumBones", mesh.boneIndexArray.length);
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
        if (skinning) {
            mat.setInt("NumBones", meshConverter.getMaxBoneSize());
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
            StringTokenizer st = new StringTokenizer(m.getTextureFileName().replace("\\", "/"), "*");
            System.out.println("m.getTextureFileName() = " + m.getTextureFileName());
            while (st.hasMoreElements()) {
                String fileName = st.nextToken();
//                System.out.println("fileName = " + fileName);
                String s = fileName.substring(fileName.indexOf('.') + 1);
                Texture texture = loadTexture(folderName + fileName /*
                         * m.getTextureFileName()
                         */);
                s = s.toLowerCase();
                if (s.equals("spa")) {
//                    texture.setWrap(Texture.WrapMode.Repeat);
                    texture.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
                    mat.setTexture("SphereMap_A", texture);
                } else if (s.equals("sph")) {
//                    texture.setWrap(Texture.WrapMode.Repeat);
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
                toonTexture = loadTexture(folderName + extToonName);
            } catch (Exception ex) {
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
                    toonTexture = loadTexture("toon/" + toonname);
                }
            }
        }
        if (toonTexture != null) {
            toonTexture.setWrap(Texture.WrapAxis.S, Texture.WrapMode.EdgeClamp);
            toonTexture.setWrap(Texture.WrapAxis.T, Texture.WrapMode.EdgeClamp);
            toonTexture.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
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
        mat.setParam("VertexLighting", VarType.Boolean, true);
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
//            mat.getAdditionalRenderState().setAlphaTest(true);
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
        temp.release();
    }

    void createSkinArray() {
        List<Skin> skinList = new ArrayList<Skin>();
        for (int i = 0; i < model.getSkinCount(); i++) {
            PMDSkinData pmdSkinData = model.getSkinData()[i];
            if (pmdSkinData.getSkinType() != 0) {
                Skin skin = new Skin(node, pmdSkinData.getSkinName());
                skin.setIndexBuf(pmdSkinData.getIndexBuf());
                skin.setSkinBuf(pmdSkinData.getSkinBuf());
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

    @Override
    public Object load(AssetInfo ai) throws IOException {
        boolean errFlag = false;
        for(;;) {
            try {
                PMDLoaderGLSLSkinning2 loader = new PMDLoaderGLSLSkinning2();
                return loader.load2(ai);
            }catch(OutOfMemoryError ex) {
                if (errFlag) {
                    throw ex;
                }
                errFlag = true;
                if (ai.getManager() instanceof DesktopAssetManager) {
                    ((DesktopAssetManager)ai.getManager()).clearCache();
                }
                System.gc();
                System.runFinalization();
            }
        }
    }
    private Object load2(AssetInfo ai) throws IOException {
        this.assetManager = ai.getManager();
        model = new PMDModel(ai.openStream());
        folderName = ai.getKey().getFolder();
        meshConverter = new MeshConverter(model);
        meshConverter.convertMesh();
        PMNData pmdData = meshConverter.createPMNData();
//        model.setVertexList(null);
        model.setFaceVertIndex(null);
        PMDNode pmdNode = createNode(ai.getKey().getName());
        if (JmeSystem.getFullName().indexOf("Android") == -1) {
            try {
                String vendor = GL11.glGetString(GL11.GL_VENDOR);
                if (vendor != null && vendor.toLowerCase().contains("intel")) {
                    pmdNode.setGlslSkinning(false);
                } else {
                    pmdNode.setGlslSkinning(true);
                }
            } catch(Exception ex) {
                pmdNode.setGlslSkinning(false);
            }
        }
        return pmdNode;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName() {
        return folderName;
    }
    Texture loadTexture(String name) {
        Texture tex = textureMap.get(name);
        if (tex == null) {
            tex = assetManager.loadTexture(name);
            textureMap.put(name, tex);
        }
        return tex;
    }
}
