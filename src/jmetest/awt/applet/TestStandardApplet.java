package jmetest.awt.applet;


import java.util.concurrent.Callable;

import com.jme.bounding.BoundingSphere;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Box;
import com.jme.util.GameTaskQueueManager;
import com.jmex.awt.applet.StandardApplet;
import com.jmex.game.state.BasicGameState;
import com.jmex.game.state.DebugGameState;
import com.jmex.game.state.GameState;
import com.jmex.game.state.GameStateManager;

/**
 * Displaying a Box inside a Applet.<br>
 * SPACE switched between Fullscreen and Windowed Mode.<br>
 * This used the lwjgl2 kind of Applets.
 */
public class TestStandardApplet extends StandardApplet {

	private static final long serialVersionUID = 1L;

	public void init() {
		System.setProperty("jme.stats", "set");
		setBackgroundColor(ColorRGBA.blue.clone());
		setSize(640, 480);
		super.init();
		
		try {
			GameTaskQueueManager.getManager().update(new AppletCallable(this) {
				public Void call() throws Exception {
					GameState state = new DebugGameState();
					// Activate the game state
					state.setActive(true);
					// Add it to the manager
					GameStateManager.getInstance().attachChild(state);
					
					state = new TestGameState(applet);
					// Activate the game state
					state.setActive(true);
					// Add it to the manager
					GameStateManager.getInstance().attachChild(state);
					return null;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
			
	}
}

abstract class AppletCallable implements Callable<Void> {
	StandardApplet applet;
	public AppletCallable(StandardApplet applet) {
		this.applet = applet;
	}
}

class TestGameState extends BasicGameState {
	StandardApplet applet;
	public TestGameState(StandardApplet applet) {
		super("test gs");
		KeyBindingManager.getKeyBindingManager().add("switch", KeyInput.KEY_SPACE);
		this.applet = applet;
		
		Box box = new Box("my box", new Vector3f(0, 0, 0), 2, 2, 2);
		box.setModelBound(new BoundingSphere());
		box.updateModelBound();
		// We had to add the following line because the render thread is already running
		// Anytime we add content we need to updateRenderState or we get funky effects
		rootNode.attachChild(box);
		
		rootNode.updateRenderState();
	}
	
	@Override
	public void update(float tpf) {
		
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("switch", false)) {
			applet.toggleFullscreen();
			KeyInput.get().clear();
		}
		
		super.update(tpf);
	}
	
}
