package jmetest.renderer;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import jmetest.renderer.loader.TestMaxJmeWrite;

import com.jme.app.SimplePassGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.light.PointLight;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.pass.RenderPass;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Torus;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jmex.effects.glsl.BloomRenderPass;
import com.jmex.model.XMLparser.JmeBinaryReader;
import com.jmex.model.XMLparser.Converters.MaxToJme;


/**
 * Bloom effect pass test
 *
 * @author Rikard Herlitz (MrCoder)
 */
public class TestBloom extends SimplePassGame {
	private BloomRenderPass bloomRenderPass;
	private int screenshotIndex = 0;

	public static void main(String[] args) {
		TestBloom app = new TestBloom();
		app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
		app.start();
	}

	protected void cleanup() {
		super.cleanup();
		bloomRenderPass.cleanup();
	}

	protected void simpleInitGame() {
		//Setup camera
		cam.setFrustumPerspective(55.0f, (float) display.getWidth() / (float) display.getHeight(), 1, 5000);

		//Setup lights
		PointLight light = new PointLight();
		light.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
		light.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
		light.setLocation(new Vector3f(0, 30, 0));
		light.setEnabled(true);
		lightState.attach(light);

		//Add dummy objects to rootNode
		rootNode.attachChild(createObjects());
		
		try {
            MaxToJme C1=new MaxToJme();
            ByteArrayOutputStream BO=new ByteArrayOutputStream();
            URL maxFile=TestMaxJmeWrite.class.getClassLoader().getResource("jmetest/data/model/Character.3DS");
            C1.convert(new BufferedInputStream(maxFile.openStream()),BO);
            JmeBinaryReader jbr=new JmeBinaryReader();
            jbr.setProperty("bound","box");
            Node r=jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
            r.setLocalScale(.1f);
            if (r.getChild(0).getControllers().size()!=0)
                r.getChild(0).getController(0).setSpeed(20);
            Quaternion temp=new Quaternion();
            temp.fromAngleAxis(FastMath.PI/2,new Vector3f(-1,0,0));
            r.setLocalRotation(temp);
            r.setLocalTranslation(new Vector3f(0,3,0));
            rootNode.attachChild(r);
        } catch (IOException e) {
            e.printStackTrace();
        }

		//Setup renderpasses
		RenderPass rootPass = new RenderPass();
		rootPass.add(rootNode);
		pManager.add(rootPass);

		bloomRenderPass = new BloomRenderPass(cam, 4);
		bloomRenderPass.add(rootNode);
		pManager.add(bloomRenderPass);

		RenderPass fpsPass = new RenderPass();
		fpsPass.add(fpsNode);
		pManager.add(fpsPass);

		//Initialize keybindings
		KeyBindingManager.getKeyBindingManager().set("1", KeyInput.KEY_1);
		KeyBindingManager.getKeyBindingManager().set("2", KeyInput.KEY_2);
		KeyBindingManager.getKeyBindingManager().set("3", KeyInput.KEY_3);
		KeyBindingManager.getKeyBindingManager().set("4", KeyInput.KEY_4);
		KeyBindingManager.getKeyBindingManager().set("5", KeyInput.KEY_5);
		KeyBindingManager.getKeyBindingManager().set("6", KeyInput.KEY_6);
		KeyBindingManager.getKeyBindingManager().set("7", KeyInput.KEY_7);
		KeyBindingManager.getKeyBindingManager().set("8", KeyInput.KEY_8);
		KeyBindingManager.getKeyBindingManager().set("9", KeyInput.KEY_9);
		KeyBindingManager.getKeyBindingManager().set("0", KeyInput.KEY_0);

		KeyBindingManager.getKeyBindingManager().set("shot", KeyInput.KEY_F4);
	}

	protected void simpleUpdate() {
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("1", false)) {
			bloomRenderPass.setEnabled(!bloomRenderPass.isEnabled());
		}

		if(KeyBindingManager.getKeyBindingManager().isValidCommand("2", false)) {
			bloomRenderPass.setBlurSize(bloomRenderPass.getBlurSize() - 0.001f);
		}
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("3", false)) {
			bloomRenderPass.setBlurSize(bloomRenderPass.getBlurSize() + 0.001f);
		}

		if(KeyBindingManager.getKeyBindingManager().isValidCommand("4", false)) {
			bloomRenderPass.setExposurePow(bloomRenderPass.getExposurePow() - 1.0f);
		}
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("5", false)) {
			bloomRenderPass.setExposurePow(bloomRenderPass.getExposurePow() + 1.0f);
		}

		if(KeyBindingManager.getKeyBindingManager().isValidCommand("6", false)) {
			bloomRenderPass.setExposureCutoff(bloomRenderPass.getExposureCutoff() - 0.1f);
		}
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("7", false)) {
			bloomRenderPass.setExposureCutoff(bloomRenderPass.getExposureCutoff() + 0.1f);
		}

		if(KeyBindingManager.getKeyBindingManager().isValidCommand("8", false)) {
			bloomRenderPass.setBlurIntensityMultiplier(bloomRenderPass.getBlurIntensityMultiplier() - 0.1f);
		}
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("9", false)) {
			bloomRenderPass.setBlurIntensityMultiplier(bloomRenderPass.getBlurIntensityMultiplier() + 0.1f);
		}

		if(KeyBindingManager.getKeyBindingManager().isValidCommand("0", false)) {
			bloomRenderPass.resetParameters();
		}

		if(KeyBindingManager.getKeyBindingManager().isValidCommand("shot", false)) {
			display.getRenderer().takeScreenShot("shot" + screenshotIndex++);
		}
	}

	private Node createObjects() {
		Node objects = new Node("objects");

		Torus torus = new Torus("Torus", 50, 50, 10, 20);
		torus.setLocalTranslation(new Vector3f(50, -5, 20));
		TextureState ts = display.getRenderer().createTextureState();
		Texture t0 = TextureManager.loadTexture(
				TestBloom.class.getClassLoader().getResource(
						"jmetest/data/images/Monkey.jpg"),
				Texture.MM_LINEAR_LINEAR,
				Texture.FM_LINEAR);
		Texture t1 = TextureManager.loadTexture(
				TestBloom.class.getClassLoader().getResource(
						"jmetest/data/texture/north.jpg"),
				Texture.MM_LINEAR_LINEAR,
				Texture.FM_LINEAR);
		t1.setEnvironmentalMapMode(Texture.EM_SPHERE);
		ts.setTexture(t0, 0);
		ts.setTexture(t1, 1);
		ts.setEnabled(true);
		torus.setRenderState(ts);
		objects.attachChild(torus);

		ts = display.getRenderer().createTextureState();
		t0 = TextureManager.loadTexture(
				TestBloom.class.getClassLoader().getResource(
						"jmetest/data/texture/wall.jpg"),
				Texture.MM_LINEAR_LINEAR,
				Texture.FM_LINEAR);
		t0.setWrap(Texture.WM_WRAP_S_WRAP_T);
		ts.setTexture(t0);

		Box box = new Box("box1", new Vector3f(-10, -10, -10), new Vector3f(10, 10, 10));
		box.setLocalTranslation(new Vector3f(0, -7, 0));
		box.setRenderState(ts);
		objects.attachChild(box);

		box = new Box("box2", new Vector3f(-5, -5, -5), new Vector3f(5, 5, 5));
		box.setLocalTranslation(new Vector3f(15, 10, 0));
		box.setRenderState(ts);
		objects.attachChild(box);

		box = new Box("box3", new Vector3f(-5, -5, -5), new Vector3f(5, 5, 5));
		box.setLocalTranslation(new Vector3f(0, -10, 15));
		box.setRenderState(ts);
		objects.attachChild(box);

		box = new Box("box4", new Vector3f(-5, -5, -5), new Vector3f(5, 5, 5));
		box.setLocalTranslation(new Vector3f(20, 0, 0));
		box.setRenderState(ts);
		objects.attachChild(box);

		box = new Box("box5", new Vector3f(-50, -2, -50), new Vector3f(50, 2, 50));
		box.setLocalTranslation(new Vector3f(0, -15, 0));
		box.setRenderState(ts);
		box.setModelBound(new BoundingBox());
		box.updateModelBound();
		objects.attachChild(box);

		ts = display.getRenderer().createTextureState();
		t0 = TextureManager.loadTexture(
				TestBloom.class.getClassLoader().getResource(
						"jmetest/data/texture/cloud_land.jpg"),
				Texture.MM_LINEAR_LINEAR,
				Texture.FM_LINEAR);
		t0.setWrap(Texture.WM_WRAP_S_WRAP_T);
		ts.setTexture(t0);

		box = new Box("floor", new Vector3f(-1000, -10, -1000), new Vector3f(1000, 10, 1000));
		box.setLocalTranslation(new Vector3f(0, -100, 0));
		box.setRenderState(ts);
		box.setModelBound(new BoundingBox());
		box.updateModelBound();
		objects.attachChild(box);

		return objects;
	}
}
