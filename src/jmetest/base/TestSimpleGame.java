package jmetest.base;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;
import com.jme.renderer.ColorRGBA;

public class TestSimpleGame extends SimpleGame {

  public static void main(String[] args) {
      TestSimpleGame app = new TestSimpleGame();
      app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
      app.start();
  }

  protected void simpleInitGame() {
    display.setTitle("A Simple Test");
    Box box = new Box("my box", new Vector3f(0,0,0), 2,2,2);
    box.setModelBound(new BoundingBox());
    box.updateModelBound();
    rootNode.attachChild(box);
  }
}
