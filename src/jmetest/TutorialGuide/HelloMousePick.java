package jmetest.TutorialGuide;

import com.jme.app.SimpleGame;
import com.jme.scene.shape.Box;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.AlphaState;
import com.jme.math.Vector3f;
import com.jme.bounding.BoundingBox;
import com.jme.input.AbsoluteMouse;
import com.jme.input.InputSystem;
import com.jme.image.Texture;
import com.jme.util.TextureManager;

import java.net.URL;

/**
 * Started Date: Jul 22, 2004<br><br>
 * 
 * @author Jack Lindamood
 */
public class HelloMousePick extends SimpleGame {
    AbsoluteMouse am;
    public static void main(String[] args) {
        HelloMousePick app = new HelloMousePick();
        app.setDialogBehaviour(SimpleGame.ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    protected void simpleRender(){
        display.getRenderer().draw(am);
    }


    protected void simpleInitGame() {
        {
            am=new AbsoluteMouse("The Mouse",display.getWidth(),display.getHeight());
            TextureState ts=display.getRenderer().getTextureState();
            URL cursorLoc;
            cursorLoc=HelloMousePick.class.getClassLoader().getResource("jmetest/data/cursor/cursor1.png");
            Texture t=TextureManager.loadTexture(cursorLoc,Texture.MM_LINEAR, Texture.FM_LINEAR, true);
            ts.setTexture(t);
            ts.setEnabled(true);
            am.setRenderState(ts);

            AlphaState as=display.getRenderer().getAlphaState();
            as.setBlendEnabled(true);
            as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
            as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
            as.setTestEnabled(true);
            as.setTestFunction(AlphaState.TF_GREATER);
            as.setEnabled(true);

            am.setRenderState(as);
            am.setMouseInput(InputSystem.getMouseInput());
            am.setLocalTranslation(new Vector3f(display.getWidth()/2,display.getHeight()/2,0));

            am.updateRenderState();
            input.setMouse(am);
        }
        {
            Box b=new Box("My Box", new Vector3f(-1,-1,-1),new Vector3f(1,1,1));
            b.setModelBound(new BoundingBox());
            b.updateModelBound();
            rootNode.attachChild(b);
        }
    }
}