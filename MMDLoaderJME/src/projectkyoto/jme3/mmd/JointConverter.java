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

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Line;
import projectkyoto.mmd.file.PMDJoint;
import projectkyoto.mmd.file.PMDModel;

/**
 *
 * @author kobayasi
 */
public class JointConverter {

    PMDModel model;
    Node node;
    AssetManager assetManager;

    public JointConverter(PMDModel model, AssetManager assetManager) {
        this.model = model;
        this.assetManager = assetManager;
    }

    public Node convert(String nodeName) {
        node = new Node(nodeName);
        for (PMDJoint joint : model.getJointList().getJointArray()) {
            createJointGeom(joint);
        }
        return node;
    }

    public void createJointGeom(PMDJoint joint) {
        Geometry geom = new Geometry(joint.getJointName());
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Mesh mesh;
        mesh = new Line(Vector3f.ZERO, Vector3f.ZERO);
        mat.setColor("Color", ColorRGBA.White);
        geom.setMesh(mesh);
        geom.setMaterial(mat);
//        mat.getAdditionalRenderState().setWireframe(true);
        mat.getAdditionalRenderState().setDepthTest(false);

        geom.setQueueBucket(Bucket.Transparent);

        node.attachChild(geom);
    }
}
