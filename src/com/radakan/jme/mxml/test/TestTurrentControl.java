/*
 * Copyright (c) 2008, OgreLoader
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     - Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     - Neither the name of the Gibbon Entertainment nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY 'Gibbon Entertainment' "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL 'Gibbon Entertainment' BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.radakan.jme.mxml.test;

import com.radakan.jme.mxml.*;
import com.jme.app.SimpleGame;
import com.jme.input.KeyInput;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.radakan.jme.mxml.anim.Bone;
import com.radakan.jme.mxml.anim.MeshAnimationController;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestTurrentControl extends SimpleGame {

    private static final Logger logger = Logger.getLogger(TestMeshLoading.class.getName());

    private Spatial model;
    private Bone turretBone;
    private float angle = 0f;
    private float angleVel = 0f;

    public static void main(String[] args){
        TestTurrentControl app = new TestTurrentControl();
        app.setConfigShowMode(ConfigShowMode.NeverShow);
        app.start();
    }

    @Override
    protected void simpleUpdate(){
        // acceleration
        if (KeyInput.get().isKeyDown(KeyInput.KEY_LEFT)){
            angleVel += tpf * 0.03f;
        }else if (KeyInput.get().isKeyDown(KeyInput.KEY_RIGHT)){
            angleVel -= tpf * 0.03f;
        }

        // drag
        if (angleVel > FastMath.ZERO_TOLERANCE){
            angleVel = Math.max(0f, angleVel - (tpf * 0.025f));
        }else if (angleVel < -FastMath.ZERO_TOLERANCE){
            angleVel = Math.min(0f, angleVel + (tpf * 0.025f));
        }

        // speed limit
        if (angleVel < -0.1f)
            angleVel = -0.1f;
        else if (angleVel > 0.1f)
            angleVel = 0.1f;

        // apply velocity
        angle += angleVel;

        Quaternion tempRot = new Quaternion();
        tempRot.fromAngleAxis(angle, Vector3f.UNIT_Y);
        turretBone.setUserTransforms(Vector3f.ZERO, tempRot, Vector3f.UNIT_XYZ);
    }

    protected void loadMeshModel(){
        OgreLoader loader = new OgreLoader();
        MaterialLoader matLoader = new MaterialLoader();

        try {
            URL matURL = TestMeshLoading.class.getClassLoader().getResource("com/radakan/jme/mxml/data/Turret.material");
            URL meshURL = TestMeshLoading.class.getClassLoader().getResource("com/radakan/jme/mxml/data/Turret.mesh.xml");

            if (matURL != null){
                matLoader.load(matURL.openStream());
                if (matLoader.getMaterials().size() > 0)
                    loader.setMaterials(matLoader.getMaterials());
            }

            model = loader.loadModel(meshURL);
            rootNode.attachChild(model);
        } catch (IOException ex) {
            Logger.getLogger(TestMeshLoading.class.getName()).log(Level.SEVERE, null, ex);
        }

        rootNode.updateGeometricState(0, true);
        rootNode.updateRenderState();
    }

    protected void setupTurretControl(){
        MeshAnimationController animControl = (MeshAnimationController) model.getController(0);

        // must set some animation otherwise user control is ignored
        animControl.setAnimation("Rotate");
        animControl.setSpeed(0.25f);

        turretBone = animControl.getBone("Turret");
        turretBone.setUserControl(true);
    }

    @Override
    protected void simpleInitGame() {
        try {
            SimpleResourceLocator locator = new SimpleResourceLocator(TestMeshLoading.class
                                                    .getClassLoader()
                                                    .getResource("com/radakan/jme/mxml/data/"));
            ResourceLocatorTool.addResourceLocator(
                    ResourceLocatorTool.TYPE_TEXTURE, locator);
            ResourceLocatorTool.addResourceLocator(
                    ResourceLocatorTool.TYPE_MODEL, locator);
        } catch (URISyntaxException e1) {
            logger.log(Level.WARNING, "unable to setup texture directory.", e1);
        }

        loadMeshModel();
        setupTurretControl();

        // disable 3rd person camera
        input.setEnabled(false);

        cam.setLocation(new Vector3f(5f, 5f, -6f));
        cam.lookAt(model.getWorldBound().getCenter(), Vector3f.UNIT_Y);

        Text t = Text.createDefaultTextLabel("Text", "Use left and right arrow keys to rotate turret");
        statNode.attachChild(t);
    }


}
