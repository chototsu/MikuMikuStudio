package jmetest.TutorialGuide;

import com.jme.app.SimpleGame;
import com.jme.scene.Text;
import com.jme.scene.Skybox;
import com.jme.scene.state.TextureState;
import com.jme.scene.shape.Sphere;
import com.jme.math.Vector3f;
import com.jme.util.TextureManager;
import com.jme.image.Texture;
import com.jme.bounding.BoundingSphere;

import java.net.URL;

/**
 * Started Date: Jul 24, 2004<br><br>
 *
 * Demonstrates intersection testing, sound, and making your own controller.
 *
 * @author Jack Lindamood
 */
public class HelloIntersection extends SimpleGame {
    public static void main(String[] args) {
        HelloIntersection app = new HelloIntersection();
        app.setDialogBehaviour(SimpleGame.ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    protected void simpleInitGame() {
        Text cross = new Text("Crosshairs", "+");
          // 8 is half the width of a font char
        cross.setLocalTranslation(new Vector3f( display.getWidth()/2f -8f,
                                                display.getHeight()/2f-8f,
                                                0));
        fpsNode.attachChild(cross);
        Sphere s=new Sphere("my sphere",15,15,1);
        s.setModelBound(new BoundingSphere());
        s.updateModelBound();
        rootNode.attachChild(s);
        Skybox sb=new Skybox("skybox",200,200,200);
        URL monkeyLoc=HelloIntersection.class.getClassLoader().getResource("jmetest/data/texture/clouds.png");
        TextureState ts=display.getRenderer().getTextureState();
        ts.setTexture(
            TextureManager.loadTexture(monkeyLoc,Texture.MM_LINEAR,Texture.FM_LINEAR,true)
        );
        ts.setEnabled(true);
        sb.setRenderState(ts);
        rootNode.attachChild(sb);
    }
}