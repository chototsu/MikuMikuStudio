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

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import projectkyoto.mmd.file.PMDBone;
import projectkyoto.mmd.file.PMDModel;
import projectkyoto.mmd.file.PMDRigidBody;

/**
 *
 * @author kobayasi
 */
public class RigidBodyConverter {

    PMDModel model;
    Node node;
    AssetManager assetManager;

    public RigidBodyConverter(PMDModel model, AssetManager assetManager) {
        this.model = model;
        this.assetManager = assetManager;
    }

    public Node convert(String nodeName) {
        node = new Node(nodeName);
        for (PMDRigidBody rigidBody : model.getRigidBodyList().getRigidBodyArray()) {
            createRigidBodyGeom(rigidBody);
        }
        return node;
    }

    public void createRigidBodyGeom(PMDRigidBody rigidBody) {
        Geometry geom = new Geometry(rigidBody.getRigidBodyName());
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Mesh mesh;
        switch (rigidBody.getShapeType()) {
            case 0:
                // sphere
                mesh = new Sphere(10, 10, rigidBody.getShapeW());
//                mat.setColor("Color", ColorRGBA.Blue);
                break;
            case 1:
                // box
                mesh = new Box(rigidBody.getShapeW(), rigidBody.getShapeH(), rigidBody.getShapeD());
//                mat.setColor("Color", ColorRGBA.Red);
                break;
            case 2:
                mesh = new Sphere(10, 10, 0.5f);
                geom.scale(rigidBody.getShapeW() * 2, rigidBody.getShapeH() + rigidBody.getShapeW() * 2, rigidBody.getShapeW() * 2);
//                mat.setColor("Color", ColorRGBA.Green);
                break;
            default:
                return;
        }
        switch (rigidBody.getRigidBodyType()) {
            case 0:
                mat.setColor("Color", ColorRGBA.Blue);
                break;
            case 1:
                mat.setColor("Color", ColorRGBA.Red);
                break;
            case 2:
                mat.setColor("Color", ColorRGBA.Green);
                break;
        }
        geom.setMesh(mesh);
        geom.setMaterial(mat);
        mat.getAdditionalRenderState().setWireframe(true);
        mat.getAdditionalRenderState().setDepthTest(false);

//        geom.rotate(rigidBody.getRot().x, rigidBody.getRot().y, rigidBody.getRot().z);
        Vector3f v = new Vector3f(rigidBody.getPos().x, rigidBody.getPos().y, rigidBody.getPos().z);
        if (rigidBody.getRelBoneIndex() != 0xffff) {
            PMDBone bone = model.getBoneList().getBones()[rigidBody.getRelBoneIndex()];
            v.addLocal(bone.getBoneHeadPos().x, bone.getBoneHeadPos().y, bone.getBoneHeadPos().z);
        }
//        geom.move(v);
        geom.setQueueBucket(Bucket.Transparent);
        if (!rigidBody.getRigidBodyName().contains("もも")
                && !rigidBody.getRigidBodyName().contains("ｽｶｰﾄ")) {
//            geom.setMesh(new Mesh());
        } else {
//            System.out.println(rigidBody.getRigidBodyName()+" "+rigidBody.getRigidBodyGroupIndex()+ " "+ rigidBody.getRigidBodyGroupTarget());
        }
        node.attachChild(geom);
    }
}
