package jmetest.TutorialGuide;

import com.jme.app.SimpleGame;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;
import com.jme.scene.Node;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.LightState;
import com.jme.math.Vector3f;
import com.jme.util.TextureManager;
import com.jme.image.Texture;
import com.jme.renderer.ColorRGBA;
import com.jme.light.PointLight;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;

import java.net.URL;


/**
 * Started Date: Jul 20, 2004<br><br>
 *
 * Demonstrates using RenderStates with jME.
 * 
 * @author Jack Lindamood
 */
public class HelloStates extends SimpleGame {
    public static void main(String[] args) {
        HelloStates app = new HelloStates();
        app.setDialogBehaviour(SimpleGame.ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    protected void simpleInitGame() {

        // Create our objects.  Nothing new here.
        Box b=new Box("my box",new Vector3f(1,1,1),new Vector3f(2,2,2));
        b.setModelBound(new BoundingBox());
        b.updateModelBound();
        Sphere s=new Sphere("My sphere",15,15,1);
        s.setModelBound(new BoundingSphere());
        s.updateModelBound();
        Node n=new Node("My root node");

        // Get a URL that points to the texture we're going to load
        URL monkeyLoc;
        monkeyLoc=HelloStates.class.getClassLoader().getResource("jmetest/data/images/Monkey.tga");

        // Get a TextureState
        TextureState ts=display.getRenderer().createTextureState();
        // Use the TextureManager to load a texture
        Texture t=TextureManager.loadTexture(monkeyLoc,Texture.MM_LINEAR,Texture.FM_LINEAR,true);
        // Assign the texture to the TextureState
        ts.setTexture(t);

        // Get a MaterialState
        MaterialState ms=display.getRenderer().createMaterialState();
        // Give the MaterialState an emissive tint
        ms.setEmissive(new ColorRGBA(0f,.2f,0f,1));

        // Create a point light
        PointLight l=new PointLight();
        // Give it a location
        l.setLocation(new Vector3f(0,10,5));
        // Make it a red light
        l.setDiffuse(ColorRGBA.red);
        // Enable it
        l.setEnabled(true);

        // Create a LightState to put my light in
        LightState ls=display.getRenderer().createLightState();
        // Attach the light
        ls.attach(l);


        // Signal that b should use renderstate ts
        b.setRenderState(ts);
        // Signal that n should use renderstate ms
        n.setRenderState(ms);
        // Detach all the default lights made by SimpleGame
        lightState.detachAll();
        // Make my light effect everything below node n
        n.setRenderState(ls);

        // Attach b and s to n, and n to rootNode.
        n.attachChild(b);
        n.attachChild(s);
        rootNode.attachChild(n);
    }
}