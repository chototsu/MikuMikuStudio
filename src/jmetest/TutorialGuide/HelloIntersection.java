package jmetest.TutorialGuide;

import com.jme.app.SimpleGame;
import com.jme.scene.*;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.shape.Sphere;
import com.jme.math.Vector3f;
import com.jme.util.TextureManager;
import com.jme.image.Texture;
import com.jme.bounding.BoundingSphere;
import com.jme.input.KeyInput;
import com.jme.input.action.AbstractInputAction;
import com.jme.renderer.ColorRGBA;
import com.jme.intersection.Intersection;
import com.jme.sound.SoundAPIController;
import com.jme.sound.scene.SphericalSound;


import java.net.URL;
import java.util.Random;

/**
 * Started Date: Jul 24, 2004<br><br>
 *
 * Demonstrates intersection testing, sound, and making your own controller.
 *
 * @author Jack Lindamood
 */
public class HelloIntersection extends SimpleGame {

    MaterialState bulletMaterial;
    Sphere target;
    URL laserURL;
    Random r=new Random();
    SphericalSound laserSound;
    public static void main(String[] args) {
        HelloIntersection app = new HelloIntersection();
        app.setDialogBehaviour(SimpleGame.ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    protected void simpleInitGame() {
        laserURL=HelloIntersection.class.getClassLoader().getResource("jmetest/data/sound/laser.ogg");
        if (laserURL==null){
            System.out.println("Couldn't find laser URL.  Did you include jmetest-data.jar ??");
            System.exit(0);
        }
        SoundAPIController.getSoundSystem(properties.getRenderer());
        SoundAPIController.getRenderer().setCamera(cam);

        laserSound=new SphericalSound(laserURL);
        laserSound.setGain(.5f);

        Text cross = new Text("Crosshairs", "+");
          // 8 is half the width of a font char
        cross.setLocalTranslation(new Vector3f( display.getWidth()/2f -8f,
                                                display.getHeight()/2f-8f,
                                                0));
        fpsNode.attachChild(cross);
        target=new Sphere("my sphere",15,15,1);
        target.setModelBound(new BoundingSphere());
        target.updateModelBound();
        rootNode.attachChild(target);
        Skybox sb=new Skybox("skybox",200,200,200);
        URL monkeyLoc=HelloIntersection.class.getClassLoader().getResource("jmetest/data/texture/clouds.png");
        TextureState ts=display.getRenderer().getTextureState();
        ts.setTexture(
            TextureManager.loadTexture(monkeyLoc,Texture.MM_LINEAR,Texture.FM_LINEAR,true)
        );
        sb.setRenderState(ts);
        rootNode.attachChild(sb);

        input.addKeyboardAction("firebullet",KeyInput.KEY_F,new FireBullet());

        bulletMaterial=display.getRenderer().getMaterialState();
        bulletMaterial.setEmissive(ColorRGBA.green);
        rootNode.setForceView(true);
    }

    class FireBullet extends AbstractInputAction{
        int numBullets;

        FireBullet(){
            setAllowsRepeats(false);
        }
        public void performAction(float time) {
            System.out.println("BANG");
            Sphere bullet=new Sphere("bullet"+numBullets++,8,8,.25f);
            bullet.setModelBound(new BoundingSphere());
            bullet.updateModelBound();
            bullet.setLocalTranslation(new Vector3f(cam.getLocation()));
            bullet.setRenderState(bulletMaterial);
            bullet.updateGeometricState(0,true);
            bullet.addController(new BulletMover(bullet,new Vector3f(cam.getDirection())));
            rootNode.attachChild(bullet);
            rootNode.updateRenderState();
            if (laserSound.isPlaying()){
                laserSound.stop();
            }
                laserSound.play();
        }
    }
    class BulletMover extends Controller{
        TriMesh bullet;
        Vector3f direction;
        float speed=10;
        float lifeTime=5;
        BulletMover(TriMesh bullet,Vector3f direction){
            this.bullet=bullet;
            this.direction=direction;
            this.direction.normalizeLocal();
        }
        public void update(float time) {
            lifeTime-=time;
            if (lifeTime<0){
                rootNode.detachChild(bullet);
                bullet.removeController(this);
                return;
            }
            Vector3f bulletPos=bullet.getLocalTranslation();
            bulletPos.addLocal(direction.mult(time*speed));
            bullet.setLocalTranslation(bulletPos);
            if (Intersection.intersection(bullet.getWorldBound(),target.getWorldBound())){
                System.out.println("OWCH!!!");
                target.setLocalTranslation(new Vector3f(r.nextFloat()*10,r.nextFloat()*10,r.nextFloat()*10));
                lifeTime=0;
            }
        }
    }
}