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
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Spatial.LightCombineMode;
import com.jme.scene.Spatial.TextureCombineMode;
import com.jme.scene.shape.Box;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.jmex.effects.particles.ParticleFactory;
import com.jmex.effects.particles.ParticleMesh;
import com.radakan.jme.mxml.anim.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestMeshLoading extends SimpleGame {

    private static final Logger logger = Logger.getLogger(TestMeshLoading.class.getName());
    
    private Node model;
    
    public static void main(String[] args){
        TestMeshLoading app = new TestMeshLoading();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }
    
    protected void loadMeshModel(){
        OgreLoader loader = new OgreLoader();
        MaterialLoader matLoader = new MaterialLoader();
        
        try {
            URL matURL = TestMeshLoading.class.getClassLoader().getResource("com/radakan/jme/mxml/data/Example.material");
            URL meshURL = TestMeshLoading.class.getClassLoader().getResource("com/radakan/jme/mxml/data/ninja.mesh.xml");
            
            if (matURL != null){
                matLoader.load(matURL.openStream());
                if (matLoader.getMaterials().size() > 0)
                    loader.setMaterials(matLoader.getMaterials());
            }
            
            model = (Node) loader.loadModel(meshURL);
        } catch (IOException ex) {
            Logger.getLogger(TestMeshLoading.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    protected Spatial loadParticle(){
        BlendState as1 = display.getRenderer().createBlendState();
        as1.setBlendEnabled(true);
        as1.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        as1.setDestinationFunction(BlendState.DestinationFunction.One);
        as1.setTestEnabled(true);
        as1.setTestFunction(BlendState.TestFunction.GreaterThan);
        as1.setEnabled(true);

        TextureState ts = display.getRenderer().createTextureState();
        ts.setTexture(
            TextureManager.loadTexture("flaresmall.jpg",
            Texture.MinificationFilter.Trilinear,
            Texture.MagnificationFilter.Bilinear));
        ts.setEnabled(true);

        ParticleMesh manager = ParticleFactory.buildParticles("particles", 200);
        manager.setEmissionDirection(new Vector3f(0.0f, 1.0f, 0.0f));
        manager.setMaximumAngle(0.20943952f);
        manager.getParticleController().setSpeed(1.0f);
        manager.setMinimumLifeTime(150.0f);
        manager.setMaximumLifeTime(225.0f);
        manager.setStartSize(8.0f);
        manager.setEndSize(4.0f);
        manager.setStartColor(new ColorRGBA(1.0f, 0.312f, 0.121f, 1.0f));
        manager.setEndColor(new ColorRGBA(1.0f, 0.312f, 0.121f, 0.0f));
        manager.getParticleController().setControlFlow(false);
        manager.setInitialVelocity(0.12f);
        //manager.setGeometry((Geometry)(i.getChild(0)));
        
        manager.warmUp(60);
        manager.setRenderState(ts);
        manager.setRenderState(as1);
        manager.setLightCombineMode(LightCombineMode.Off);
        manager.setTextureCombineMode(TextureCombineMode.Replace);
        manager.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
        
        ZBufferState zstate = display.getRenderer().createZBufferState();
        zstate.setEnabled(true);
        zstate.setWritable(false);
        manager.setRenderState(zstate);
        
        return manager;
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
        
        Logger.getLogger("com.jme.scene.state.lwjgl").setLevel(Level.SEVERE);
        
        DisplaySystem.getDisplaySystem().setTitle("Test Mesh Instancing");
        display.getRenderer().setBackgroundColor(ColorRGBA.darkGray);
        ((FirstPersonHandler)input).getKeyboardLookHandler().setMoveSpeed(300);
        cam.setFrustumFar(20000f);
        loadMeshModel();
        //MeshCloner.setVBO(model);
        
        int modelN = 0;
        for (int x = 0; x < 1; x++){
            for (int y = 0; y < 1; y++){
                Node clone = MeshCloner.cloneMesh(model);
                clone.setLocalTranslation(75 * x,  0,  75 * y);
                rootNode.attachChild(clone);
                
                if (clone.getControllerCount() > 0){
                    MeshAnimationController animControl = (MeshAnimationController) clone.getController(0);
                    animControl.setAnimation("Walk");
                    animControl.setTime(animControl.getAnimationLength("Walk") * FastMath.nextRandomFloat());
                    //clone.addController(new MeshLodController((animControl)));
                    
                    Bone b = animControl.getBone("Joint22");
                    Node attachNode = b.getAttachmentsNode();
                    clone.attachChild(attachNode);
                    Spatial particle = loadParticle();
                    attachNode.attachChild(particle);
                    
                    b = animControl.getBone("Joint27");
                    attachNode = b.getAttachmentsNode();
                    clone.attachChild(attachNode);
                    particle = loadParticle();
                    attachNode.attachChild(particle);
                    
                    b = animControl.getBone("Joint17");
                    attachNode = b.getAttachmentsNode();
                    clone.attachChild(attachNode);
                    particle = new Box("stick", new Vector3f(0, 0, -25), 2, 2, 30);
                    attachNode.attachChild(particle);
                }
            }
        }
        
        rootNode.updateGeometricState(0, true);
        rootNode.updateRenderState();
    }

    
    
}
