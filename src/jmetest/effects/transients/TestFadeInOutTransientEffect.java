/*
 * Created on Apr 6, 2004
 */
package jmetest.effects.transients;
import com.jme.app.AbstractGame;
import com.jme.app.SimpleGame;
import com.jme.effects.transients.FadeInOut;
import com.jme.effects.transients.FadeInOutController;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.TextureRenderer;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;
import com.jme.util.Timer;
/**
 * @author Ahmed
 */
public class TestFadeInOutTransientEffect extends SimpleGame {
	private Camera cam;
	private FadeInOut fio;
	private FadeInOutController fioC;
	private Node rootNode, fadeOutNode, fadeInNode;
	private Text fps;
	private Timer timer;

	private TextureRenderer tRen;
	private Texture fadeInT, fadeOutT;

	private Quad fadeIn, fadeOut;

	protected void update(float interpolation) {
		fps.print("FPS: " + (int) timer.getFrameRate() + ", "
				+ display.getRenderer().getStatistics() + ", Memory Usage:"
				+ (Runtime.getRuntime().freeMemory()/(1024*1024)) + "Mb/"
				+ (Runtime.getRuntime().totalMemory()/(1024*1024)) + "Mb");

		rootNode.updateWorldData(timer.getTimePerFrame() * 10);
	}
	protected void render(float interpolation) {
		display.getRenderer().clearBuffers();
		display.getRenderer().draw(rootNode);
		display.getRenderer().clearStatistics();
	}
	protected void initSystem() {
		try {
			display = DisplaySystem.getDisplaySystem(properties.getRenderer());
			display.createWindow(properties.getWidth(), properties.getHeight(),
					properties.getDepth(), properties.getFreq(), properties
							.getFullscreen());
			display.setTitle("FadeInOut Test");
			cam = display.getRenderer().getCamera(properties.getWidth(),
					properties.getHeight());
		} catch (JmeException e) {
			e.printStackTrace();
			System.exit(1);
		}
		display.getRenderer().setBackgroundColor(new ColorRGBA(0, 0, 0, 0));
		cam.setFrustum(1f, 1000f, -0.55f, 0.55f, 0.4125f, -0.4125f);
		Vector3f loc = new Vector3f(0, 0, 20);
		Vector3f left = new Vector3f(-1, 0, 0);
		Vector3f up = new Vector3f(0, 1, 0);
		Vector3f dir = new Vector3f(0, 0, -1);
		cam.setFrame(loc, left, up, dir);
		display.getRenderer().setCamera(cam);
		display.getRenderer().enableStatistics(true);
		timer = Timer.getTimer(properties.getRenderer());
	}
	protected void initGame() {
		rootNode = new Node("Scene Graph Root Node");
		fadeOutNode = new Node("Fade Out Node");
		fadeInNode = new Node("Fade In Node");
		AlphaState fontAS = display.getRenderer().getAlphaState();
		fontAS.setBlendEnabled(true);
		fontAS.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		fontAS.setDstFunction(AlphaState.DB_ONE);
		fontAS.setTestEnabled(true);
		fontAS.setTestFunction(AlphaState.TF_GREATER);
		fontAS.setEnabled(true);
		TextureState fontTS = display.getRenderer().getTextureState();
		fontTS.setTexture(TextureManager.loadTexture(
				TestFadeInOutTransientEffect.class.getClassLoader()
						.getResource("jmetest/data/font/font.png"),
				Texture.MM_LINEAR, Texture.FM_LINEAR, true));
		fontTS.setEnabled(true);
		fps = new Text("FPS", "");
		fps.setRenderState(fontAS);
		fps.setRenderState(fontTS);

		fadeOut = new Quad("Fade Out");
		fadeOut.initialize(5, 5);
		fadeOut.setRenderState(fontTS);
		fadeOutNode.attachChild(fadeOut);

		fadeIn = new Quad("Fade In");
		fadeIn.initialize(5, 5);
		fadeInNode.attachChild(fadeIn);

		tRen = display.createTextureRenderer(512, 512, false, true, false, false, TextureRenderer.RENDER_TEXTURE_2D, 0);
		fadeInT = tRen.setupTexture();
		fadeOutT = tRen.setupTexture();
		tRen.render(fadeInNode, fadeInT);
		tRen.render(fadeOutNode, fadeOutT);

		TextureState fadeOutTS = display.getRenderer().getTextureState();
		fadeOutTS.setEnabled(true);
		fadeOutTS.setTexture(fadeOutT);

		TextureState fadeInTS = display.getRenderer().getTextureState();
		fadeInTS.setEnabled(true);
		fadeOutTS.setTexture(fadeInT);

		fio = new FadeInOut("FadeInOut", fadeOutTS, fadeInTS, new ColorRGBA(1, 0, 0, 0), 0.01f);
		fioC = new FadeInOutController(fio);
		fio.addController(fioC);

		rootNode.attachChild(fps);
		rootNode.attachChild(fio);
                rootNode.updateRenderState();
	}
	protected void reinit() {
	}
	protected void cleanup() {
	}
	public static void main(String[] args) {
		TestFadeInOutTransientEffect app = new TestFadeInOutTransientEffect();
		app.setDialogBehaviour(AbstractGame.ALWAYS_SHOW_PROPS_DIALOG);
		app.start();
	}
}
