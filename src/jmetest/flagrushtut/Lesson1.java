package jmetest.flagrushtut;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

/**
 * First example class shows how to create a window/application using 
 * SimpleGame. This will do nothing but display a Sphere in the center.
 * For Flag Rush Tutorial Series.
 * @author mark powell
 *
 */
public class Lesson1 extends SimpleGame {
	Sphere s;

	/**
	 * Main method is the entry point for this lesson. It creates a 
	 * SimpleGame and tells the dialog to always appear. It then 
	 * starts the main loop.
	 * @param args
	 */
	public static void main(String[] args) {
		Lesson1 app = new Lesson1();
	    app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
	    app.start();
	}

	/**
	   * sets the title of the window, creates a sphere and textures it
	   * with the monkey.
	   * @see com.jme.app.SimpleGame#initGame()
	   */
	  protected void simpleInitGame() {
	    display.setTitle("Tutorial 1");

	    s = new Sphere("Sphere", 30, 30, 25);
	    s.setLocalTranslation(new Vector3f(0,0,-40));
	    s.setModelBound(new BoundingBox());
	    s.updateModelBound();
	    rootNode.attachChild(s);

	    TextureState ts = display.getRenderer().createTextureState();
	    ts.setEnabled(true);
	    ts.setTexture(
	        TextureManager.loadTexture(
	        Lesson1.class.getClassLoader().getResource(
	        "jmetest/data/images/Monkey.jpg"),
	        Texture.MM_LINEAR_LINEAR,
	        Texture.FM_LINEAR));

	    rootNode.setRenderState(ts);
	  }

}
