package jmetest.ogrexml;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.jme.app.SimpleGame;
import com.jme.input.InputHandler;
import com.jme.scene.Node;
import com.jme.util.resource.RelativeResourceLocator;
import com.jme.util.resource.ResourceLocatorTool;
import com.jmex.model.ModelFormatException;
import com.jmex.model.ogrexml.MaterialLoader;
import com.jmex.model.ogrexml.OgreEntityNode;
import com.jmex.model.ogrexml.OgreLoader;
import com.jmex.model.ogrexml.anim.AnimationChannel;
import com.jmex.model.ogrexml.anim.MeshAnimation;
import com.jmex.model.ogrexml.anim.MeshAnimationController;
import com.jmex.model.ogrexml.anim.MeshAnimationWrapper;
import com.jmex.model.ogrexml.anim.PoseTrack.PoseFrame;

/**
 * 
 * demonstration of dynamic pose-based Animations.
 * 
 * 
 * @author ttrocha
 *
 */
public class TestFacialAnimation extends SimpleGame {

	private OgreEntityNode face;
	private MeshAnimationController animContr;
	private MeshAnimation manualMeshAnimation;
	private boolean playAnimation;
	private AnimationChannel boneAnimationChannel;
	private AnimationChannel meshAnimationChannel;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new TestFacialAnimation().start();
	}

	@Override
	protected void simpleInitGame() {
		input=new InputHandler();
		
		face = (OgreEntityNode)loadMeshModel("jmetest/data/model/ogrexml/Human.mesh.xml","jmetest/data/model/ogrexml/Human.material");
		face.setLocalScale(3.5f);
		face.getLocalTranslation().addLocal(0,-8f,0);
		
		
		rootNode.attachChild(face);
		// at the moment we need a MeshAnimationController at the ogre-entity-node
		// that means we need at least an skeletal-animation or a pose-animation
		// shouldn't be too hard to create a MeshAnimationController on your own...
		animContr = (MeshAnimationController)face.getController(0);

		// in order to create pose-animation programmatically you have to use the
		// MeshAnimationWrapper. 
		MeshAnimationWrapper manualAnimationWrapper = new MeshAnimationWrapper(face);
		// create a pose at time 0 for Pose "Frown_L" with weight 0
		manualAnimationWrapper.addPoseKeyFrame("Frown_L", 0f, 0);
		// create a pose at time 2.5 for Pose "Frown_L" with weight 1
		manualAnimationWrapper.addPoseKeyFrame("Frown_L", 2.5f, 1);
		manualAnimationWrapper.addPoseKeyFrame("Frown_L", 4f, 1);
		manualAnimationWrapper.addPoseKeyFrame("Frown_L", 6f, 1);
		manualAnimationWrapper.addPoseKeyFrame("Smile_L", 0f, 0);
		manualAnimationWrapper.addPoseKeyFrame("Smile_L", 6f, 0);
		manualAnimationWrapper.addPoseKeyFrame("Smile_L", 8f, 1);
		manualAnimationWrapper.addPoseKeyFrame("Smile_L", 12f, 1);
		manualAnimationWrapper.addPoseKeyFrame("Smile_L", 16f, 0);
		manualAnimationWrapper.addPoseKeyFrame("LoLidUp_L", 0f, 0);
		manualAnimationWrapper.addPoseKeyFrame("LoLidUp_L", 6f, 0.5f);
		manualAnimationWrapper.addPoseKeyFrame("LoLidUp_L", 8f, 0);
		manualAnimationWrapper.addPoseKeyFrame("MouthOpen", 2f,0);
		manualAnimationWrapper.addPoseKeyFrame("MouthOpen", 4f,1);
		manualAnimationWrapper.addPoseKeyFrame("MouthOpen", 4.5f,1);
		manualAnimationWrapper.addPoseKeyFrame("MouthOpen", 5f,0);
		// once we created all keyframes we can create an the meshAnimation with the
		// specidifed name and register it to meshAnim (which means that you can use it by name
		// e.g.: meshAnimController.setAnimation("animation");
		MeshAnimation meshAnimation = manualAnimationWrapper.createMeshAnimation("animation",animContr);
		
		// create a second animation that will be used for manual-control 
		// therefore we create one keyframe for all assigned poses 
		manualAnimationWrapper = new MeshAnimationWrapper(face);
		// go through all poses and create a keyframe at time 0
		for (String poseName : face.getPoseNames())
		{
			manualAnimationWrapper.addPoseKeyFrame(poseName, 0, 0);
		}

		// create AnimationChannels to combine Mesh- and Skeletal Animation
		boneAnimationChannel = animContr.getAnimationChannel();
		meshAnimationChannel = animContr.getAnimationChannel();
		
		// create the animation and register it to the MeshAnimationController
		manualMeshAnimation = manualAnimationWrapper.createMeshAnimation("manual",animContr);

		// start playing the programmatically build animation
		animContr.setAnimation(meshAnimationChannel,"animation");
		animContr.setActive(true);

		// start skeletal-animation
		animContr.setAnimation(boneAnimationChannel,"Action");
		
		// show the manual-gui
		// this is a bit buggy and updates not properly (should have used nifty :D )
		FacialGUI.showGUI(this);

	}
	
	 // load ogre-mesh (taken from jmetest...)
	 protected Node loadMeshModel(String modelPath, String materialPath){
	        OgreLoader loader = new OgreLoader();
	        MaterialLoader matLoader = new MaterialLoader();

	        try {
	            URL matURL = TestFacialAnimation.class.getClassLoader().getResource(materialPath);
	            URL meshURL = TestFacialAnimation.class.getClassLoader().getResource(modelPath);

	            if (meshURL == null)
	                throw new IllegalStateException(
	                        "ModelPath invalid ");
	            if (matURL == null)
	                throw new IllegalStateException(
	                        "Material-Path invalid");
	            try {
	                ResourceLocatorTool.addResourceLocator(
	                        ResourceLocatorTool.TYPE_TEXTURE,
	                        new RelativeResourceLocator(matURL));
	                  // This causes relative references in the .material file to
	                  // resolve to the same dir as the material file.
	                  // Don't have to set up a relative locator for TYPE_MODEL
	                  // here, because OgreLoader.loadModel() takes care of that.
	            } catch (URISyntaxException use) {
	                // Since we're generating the URI from a URL we know to be
	                // good, we won't get here.  This is just to satisfy the
	                // compiler.
	                throw new RuntimeException(use);
	            }
	            matLoader.load(matURL.openStream());
	            if (matLoader.getMaterials().size() > 0)
	                loader.setMaterials(matLoader.getMaterials());

	            return (Node) loader.loadModel(meshURL);
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        } catch (ModelFormatException mfe) {
	            mfe.printStackTrace();
	        }
	        return null;
	    }

	@Override
	protected void simpleUpdate() {
		super.simpleUpdate();
		FacialGUI.updateGUI();
	}

	public OgreEntityNode getFace() {
		return face;
	}

	// toggle between manual face and animation
	public void playAnimation(boolean b) {
		this.playAnimation = b;
		if (b==true)
		{
			animContr.setAnimation(meshAnimationChannel,"animation");
		}
		else
		{
			animContr.setAnimation(meshAnimationChannel,"manual");
		}
	}
	 

	public MeshAnimation getManualMeshAnimation() {
		return manualMeshAnimation;
	}
	 
}
