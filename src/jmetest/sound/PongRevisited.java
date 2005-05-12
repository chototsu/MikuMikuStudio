/*
 * Created on 4 august 2004
 */
package jmetest.sound;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jmex.effects.ParticleManager;
import com.jmex.sound.fmod.SoundSystem;
import com.jmex.ui.UIColorScheme;
import com.jmex.ui.UIFonts;
import com.jmex.ui.UIText;

/**
 * @author Arman OZCELIK
 *  
 */
public class PongRevisited extends SimpleGame {

    private Box player;

    private Box computer;

    private Sphere ball;

    private Box lowerWall;

    private Box upperWall;

    private Node particleNode;

    private ParticleManager manager, bmanager;

    private int snode;

    private int ballSound;
    private int explodeSound;

    private static final int BOUNCE_EVENT = 1;

    private static final int WALL_BOUNCE_EVENT = 2;

    private static final int MISS_EVENT = 3;

    private float ballXSpeed = -1f;

    private float ballYSpeed = 0.0f;

    private int difficulty = 10;

    private UIText playerScoreText, computerScoreText;

    private int computerScore, playerScore;

    private Node uiNode;
    
    

    protected void simpleInitGame() {
        SoundSystem.init(display.getRenderer().getCamera(), SoundSystem.OUTPUT_DEFAULT);
        snode = SoundSystem.createSoundNode();
        uiNode = new Node("UINODE");
        
        String[] names = { "main", "nice" };
        String[] locs = { fontLocation, "jmetest/data/font/conc_font.png" };

        UIFonts _fonts = new UIFonts(names, locs);
        UIColorScheme _scheme = new UIColorScheme();
        
        _scheme._foregroundcolor = ColorRGBA.white;
        
        playerScoreText = new UIText("UINODE", _fonts, "nice", "Player : 0", 600, 0,
                50.0f, 0.0f, 30, 0, _scheme, 0);
        
        computerScoreText = new UIText( "UINODE", _fonts, "nice", "Computer : 0", 50, 0, 
                50.0f, 0.0f, 30, 0, _scheme, 0);
        
        //playerScoreText.setText("Player : 0");
        //computerScoreText.setText("Computer : 0");
        
        uiNode.attachChild(playerScoreText);
        uiNode.attachChild(computerScoreText);

        display.getRenderer().setBackgroundColor(new ColorRGBA(0, 0, 0, 1));

        cam.setFrustum(1f, 5000F, -0.55f, 0.55f, 0.4125f, -0.4125f);

        Vector3f loc = new Vector3f(0, 0, -850);

        Vector3f left = new Vector3f(1, 0, 0);
        Vector3f up = new Vector3f(0, 1, 0f);
        Vector3f dir = new Vector3f(0, 0, 1);
        cam.setFrame(loc, left, up, dir);

        display.getRenderer().setCamera(cam);

        player = new Box("Player", new Vector3f(0, 0, 0), 5f, 50f, 5f);
        player.getLocalTranslation().x = -400;
        player.setModelBound(new BoundingBox());
        player.updateModelBound();

        computer = new Box("Computer", new Vector3f(0, 0, 0), 5f, 50f, 5f);
        computer.getLocalTranslation().x = 400;
        computer.setModelBound(new BoundingBox());
        computer.updateModelBound();

        ball = new Sphere("Ball", 15, 15, 5f);
        ball.setModelBound(new BoundingBox());
        ball.getLocalTranslation().z = -00f;
        ball.updateModelBound();

        ballSound = SoundSystem.create3DSample("D:/eclipse/workspace/JMonkeyEngine/src/jmetest/data/sound/turn.wav");
        explodeSound = SoundSystem.create3DSample("D:/eclipse/workspace/JMonkeyEngine/src/jmetest/data/sound/explosion.wav");
        
        SoundSystem.bindEventToSample(ballSound, WALL_BOUNCE_EVENT);
        SoundSystem.bindEventToSample(explodeSound, MISS_EVENT);
        SoundSystem.setSampleMaxAudibleDistance(ballSound, 5000);
        SoundSystem.addSampleToNode(ballSound, snode);
        SoundSystem.addSampleToNode(explodeSound, snode);
        
       

        lowerWall = new Box("Left Wall", new Vector3f(0, -300, 0), 450f, 5f, 5f);
        lowerWall.setModelBound(new BoundingBox());
        lowerWall.updateModelBound();

        upperWall = new Box("Right Wall", new Vector3f(0, 300, 0), 450f, 5f, 5f);
        upperWall.setModelBound(new BoundingBox());
        upperWall.updateModelBound();
        
        
        manager = new ParticleManager(300, display.getRenderer().getCamera());
        manager.setGravityForce(new Vector3f(0.0f, 0.0f, 0.0f));
        manager.setEmissionDirection(new Vector3f(0.0f, 1.0f, 0.0f));
        manager.setEmissionMaximumAngle(3.1415927f);
        manager.setSpeed(1.4f);
        manager.setParticlesMinimumLifeTime(1000.0f);
        manager.setStartSize(5.0f);
        manager.setEndSize(5.0f);
        manager.setStartColor(new ColorRGBA(1.0f, 0.312f, 0.121f, 1.0f));
        manager.setEndColor(new ColorRGBA(1.0f, 0.24313726f, 0.03137255f, 0.0f));
        manager.setRandomMod(0.0f);
        manager.setControlFlow(false);
        manager.setReleaseRate(300);
        manager.setReleaseVariance(0.0f);
        manager.setInitialVelocity(1.0f);
        manager.setParticleSpinSpeed(0.0f);

        manager.warmUp(1000);
        manager.getParticles().addController(manager);
        
        
        
        bmanager = new ParticleManager(100, display.getRenderer().getCamera());
        bmanager.setGravityForce(new Vector3f(0.0f, 0.0f, 0.0f));
        bmanager.setEmissionDirection(new Vector3f(-1.0f, 0.0f, 0.0f));
        bmanager.setEmissionMaximumAngle(0.1f);
        bmanager.setSpeed(0.4f);
        bmanager.setParticlesMinimumLifeTime(100.0f);
        bmanager.setStartSize(5.0f);
        bmanager.setEndSize(5.0f);
        bmanager.setStartColor(new ColorRGBA(1.0f, 0.312f, 0.121f, 1.0f));
        bmanager.setEndColor(new ColorRGBA(1.0f, 0.24313726f, 0.03137255f, 0.0f));
        bmanager.setRandomMod(0.0f);
        bmanager.setControlFlow(false);
        bmanager.setReleaseRate(300);
        bmanager.setReleaseVariance(0.0f);
        bmanager.setInitialVelocity(0.3f);
        bmanager.setParticleSpinSpeed(-0.5f);

        bmanager.warmUp(1000);
        bmanager.getParticles().addController(bmanager);

       
        
        
        
        AlphaState as1 = display.getRenderer().createAlphaState();
        as1.setBlendEnabled(true);
        as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as1.setDstFunction(AlphaState.DB_ONE);
        as1.setTestEnabled(true);
        as1.setTestFunction(AlphaState.TF_GREATER);
        as1.setEnabled(true);

        TextureState ts = display.getRenderer().createTextureState();
        ts.setTexture(
            TextureManager.loadTexture(
            PongRevisited.class.getClassLoader().getResource(
            "jmetest/data/texture/flaresmall.jpg"),
            Texture.MM_LINEAR_LINEAR,
            Texture.FM_LINEAR));
        ts.setEnabled(true);
        manager.setRepeatType(Controller.RT_CLAMP);
        Node myNode = new Node("Particle Nodes");
        myNode.setRenderState(as1);
        myNode.setRenderState(ts);
        Node mybNode = new Node("Particle Nodes");
        mybNode.setRenderState(as1);
        mybNode.setRenderState(ts);
        
        mybNode.attachChild(bmanager.getParticles());
        myNode.attachChild(manager.getParticles());
        
        rootNode.attachChild(myNode);
        rootNode.attachChild(mybNode);
        
        rootNode.attachChild(player);
        lightState.setEnabled(!lightState.isEnabled());
        rootNode.attachChild(computer);
        rootNode.attachChild(lowerWall);
        rootNode.attachChild(upperWall);
        rootNode.attachChild(ball);
        rootNode.attachChild(uiNode);

        input.setMouseSpeed(0);
        input.setKeySpeed(40);

        input.addKeyboardAction("moveUp", KeyInput.KEY_UP, new MoveUpAction());
        input.addKeyboardAction("moveDown", KeyInput.KEY_DOWN,
                new MoveDownAction());
        //KeyBindingManager.getKeyBindingManager().remove("forward");
        //KeyBindingManager.getKeyBindingManager().remove("backward");
        KeyBindingManager.getKeyBindingManager().remove("lookUp");
        KeyBindingManager.getKeyBindingManager().remove("lookDown");

    }

    public void simpleUpdate() {
        manager.setRepeatType(Controller.RT_CLAMP);
        if (checkPlayer()) {
            SoundSystem.setSamplePosition(ballSound, cam.getLocation().x + 5, cam.getLocation().y,
                    cam.getLocation().z);
            SoundSystem.onEvent(WALL_BOUNCE_EVENT);
            
        }
        if (checkComputer()) {
            SoundSystem.setSamplePosition(ballSound, cam.getLocation().x - 5, cam.getLocation().y,
                    cam.getLocation().z);
            SoundSystem.onEvent(WALL_BOUNCE_EVENT);
        }

        if (checkWalls()) {
            SoundSystem.setSamplePosition(ballSound, cam.getLocation().x + 5, cam.getLocation().y,
                    cam.getLocation().z);
            SoundSystem.onEvent(WALL_BOUNCE_EVENT);
        }
        moveBall();
        moveComputer();

        if (ball.getLocalTranslation().x < player.getWorldTranslation().x) {

            SoundSystem.setSamplePosition(explodeSound, cam.getLocation().x + 5, cam.getLocation().y,
                    cam.getLocation().z);
            SoundSystem.onEvent(MISS_EVENT);
            computerScoreText.setText("Computer : " + (++computerScore));
            manager.setRepeatType(Controller.RT_WRAP);
            manager.forceRespawn();
            manager.getParticles().setLocalTranslation(new Vector3f(ball.getLocalTranslation().x, ball.getLocalTranslation().y, ball.getLocalTranslation().z));
            reset();
            
            
        }
        if (ball.getLocalTranslation().x > computer.getWorldTranslation().x) {

            SoundSystem.setSamplePosition(explodeSound, cam.getLocation().x - 5, cam.getLocation().y,
                    cam.getLocation().z);
            SoundSystem.onEvent(MISS_EVENT);
            playerScoreText.setText("Player : " + (++playerScore));
            manager.setRepeatType(Controller.RT_WRAP);
            manager.forceRespawn();
            manager.getParticles().setLocalTranslation(new Vector3f(ball.getLocalTranslation().x, ball.getLocalTranslation().y, ball.getLocalTranslation().z));
            if(difficulty>2) difficulty--;
            reset();
            
        }
        fps.print("");
        SoundSystem.update(tpf);

        super.simpleUpdate();

    }

    /**
     *  
     */
    private void moveBall() {
        ball.getLocalTranslation().x += ballXSpeed;
        ball.getLocalTranslation().y += ballYSpeed;
        bmanager.getParticles().getLocalTranslation().x=ball.getLocalTranslation().x;
        bmanager.getParticles().getLocalTranslation().y=ball.getLocalTranslation().y;
        if(ballXSpeed < 0){
            bmanager.setEmissionDirection(new Vector3f(1,ballYSpeed, 0));
        }else{
            bmanager.setEmissionDirection(new Vector3f(-1,-ballYSpeed, 0));
        }
    }

    private void moveComputer() {
        float dist = Math.abs(ball.getLocalTranslation().y
                - computer.getLocalTranslation().y);
        if (ballXSpeed > 0) {
            if (computer.getLocalTranslation().y < ball.getLocalTranslation().y) {
                computer.getLocalTranslation().y += Math
                        .rint(dist / difficulty);
            } else {
                computer.getLocalTranslation().y -= Math
                        .rint(dist / difficulty);
            }
        } else {
            if (computer.getLocalTranslation().y < 0)
                computer.getLocalTranslation().y += 2;
            else if (computer.getLocalTranslation().y > 0)
                computer.getLocalTranslation().y -= 2;
        }

    }

    private boolean checkPlayer() {
        if (ball.getWorldBound().intersects(player.getWorldBound())) {

            ballXSpeed = 0 - ballXSpeed;

            float racketHit = (ball.getLocalTranslation().y - (player
                    .getLocalTranslation().y));

            ballYSpeed += (racketHit / 4);
            return true;
        }
        return false;
    }

    public boolean checkComputer() {
        if (ball.getWorldBound().intersects(computer.getWorldBound())) {
            ballXSpeed = 0 - ballXSpeed;
            SoundSystem.setSamplePosition(ballSound, cam.getLocation().x - 5, cam.getLocation().y,
                    cam.getLocation().z);
            
            float racketHit = (ball.getLocalTranslation().y - (computer
                    .getLocalTranslation().y));
            
            ballYSpeed += (racketHit / 4);
            return true;

        }
        return false;
    }

    public boolean checkWalls() {
        if (ball.getWorldBound().intersects(lowerWall.getWorldBound())) {
            ballYSpeed = 0 - ballYSpeed;
            return true;
        }
        if (ball.getWorldBound().intersects(upperWall.getWorldBound())) {
            ballYSpeed = 0 - ballYSpeed;
            return true;
        }
        return false;
    }

    public void simpleRender() {

        SoundSystem.draw(snode);
        display.getRenderer().draw(particleNode);

        super.simpleRender();
    }

    public static void main(String[] args) {
        PongRevisited app = new PongRevisited();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    private void reset() {
        player.getLocalTranslation().x = -400;
        player.getLocalTranslation().y = 0;
        player.getLocalTranslation().z = 0;

        computer.getLocalTranslation().x = 400;
        computer.getLocalTranslation().y = 0;
        computer.getLocalTranslation().z = 0;

        ball.getLocalTranslation().x = 0;
        ball.getLocalTranslation().y = 0;
        ball.getLocalTranslation().z = 0;

        ballXSpeed = 1f;
        ballYSpeed = 0;
    }

    class MoveUpAction extends KeyInputAction {
        int numBullets;

        MoveUpAction() {
            setAllowsRepeats(true);
        }

        public void performAction(InputActionEvent evt) {
            player.getLocalTranslation().y += 5;

        }
    }

    class MoveDownAction extends KeyInputAction {
        int numBullets;

        MoveDownAction() {
            setAllowsRepeats(true);
        }

        public void performAction(InputActionEvent evt) {
            player.getLocalTranslation().y -= 5;

        }
    }

}