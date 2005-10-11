package com.jme.app;

import com.jme.scene.state.ZBufferState;
import com.jme.scene.Node;
import com.jme.system.DisplaySystem;

/**
 * @author Irrisor
 */
public abstract class StandardGameStateDefaultCamera extends BasicGameState {
    public StandardGameStateDefaultCamera( String name ) {
        super( name );

        initZBuffer();

        // Update geometric and rendering information for the rootNode.
        rootNode.updateGeometricState(0.0f, true);
        rootNode.updateRenderState();
    }

    public Node getRootNode()
    {
        return rootNode;
    }

    /**
	 * Creates a ZBuffer to display pixels closer to the camera above
	 * farther ones.
	 */
	protected void initZBuffer() {
		ZBufferState buf = DisplaySystem.getDisplaySystem().
			getRenderer().createZBufferState();
		buf.setEnabled(true);
		buf.setFunction(ZBufferState.CF_LEQUAL);
		rootNode.setRenderState(buf);
	}

    /**
	 * Overwritten to appropriately call switchTo() or switchFrom().
	 *
	 * @see GameState#setActive(boolean)
	 */
	public void setActive(boolean active) {
		if (active) onActivate();
		else onDeactivate();
		super.setActive(active);
	}

    /**
	 * Calls stateUpdate(float), then updates the geometric state of the
	 * rootNode.
	 *
	 * @param tpf The elapsed time since last frame.
	 * @see GameState#update(float)
	 * @see StandardGameState#stateUpdate(float)
	 */
	public final void update(float tpf) {
		stateUpdate(tpf);
		super.update(tpf);
	}

    /**
	 * Calls stateRender(float), then renders the rootNode.
	 *
	 * @param tpf The elapsed time since last frame.
	 * @see GameState#render(float)
	 * @see StandardGameState#stateRender(float)
	 */
	public final void render(float tpf) {
		stateRender(tpf);
		super.render(tpf);
	}

    /**
	 * This is where derived classes are supposed to put their game logic.
	 * Gets called between the input.update and
	 * rootNode.updateGeometricState calls.
	 *
	 * <p>
	 * Much like the structure of <code>SimpleGame</code>.
	 * </p>
	 *
	 * @param tpf The time since the last frame.
	 */
	protected void stateUpdate(float tpf) {
	}

    /**
	 * This is where derived classes are supposed to put their render logic.
	 * Gets called before the rootNode gets rendered.
	 *
	 * <p>
	 * Much like the structure of <code>SimpleGame</code>.
	 * </p>
	 *
	 * @param tpf The time since the last frame.
	 */
	protected void stateRender(float tpf) {
	}

    /**
	 * Points the renderers camera to the one contained by this state. Derived
	 * classes can put special actions they want to perform when activated here.
	 */
	protected abstract void onActivate();

    /**
	 * Derived classes can put special actions they want to perform when
	 * deactivated here.
	 */
	protected void onDeactivate() {
	}
}
