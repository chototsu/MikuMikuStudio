/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this 
 * list of conditions and the following disclaimer. 
 * 
 * Redistributions in binary form must reproduce the above copyright notice, 
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution. 
 * 
 * Neither the name of the Mojo Monkey Coding, jME, jMonkey Engine, nor the 
 * names of its contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package test.general;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.lwjgl.Display;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL;
import org.lwjgl.vector.Vector3f;
import jme.AbstractGame;
import jme.entity.Entity;
import jme.entity.effects.ParticleEmitter;
import jme.geometry.model.md3.Md3Model;
import jme.geometry.primitive.Pyramid;
import jme.geometry.hud.SplashScreen;
import jme.geometry.hud.text.Font2D;
import jme.lighting.SlopeLighting;
import jme.locale.external.Geomipmap;
import jme.locale.external.data.AbstractHeightMap;
import jme.locale.external.data.FaultFractalHeightMap;
import jme.locale.external.feature.SkyDome;
import jme.locale.external.feature.WaterPlane;
import jme.physics.mobile.LandMobility;
import jme.system.DisplaySystem;
import jme.entity.camera.Camera;
import jme.texture.ProceduralTexture;
import jme.texture.TextureManager;
import jme.utility.LoggingSystem;
import jme.utility.Timer;
import jme.world.World;
/**
 * <code>TestMain.java</code>
 * @author Mark Powell
 * @version 0.1.0
 */
public class TestMain extends AbstractGame {
    private int currentAnimation;
    private LandMobility physics;
    private ProceduralTexture pt;
    private SlopeLighting sl;
    private AbstractHeightMap hm1;
    private AbstractHeightMap hm2;
    private AbstractHeightMap hm3;
    private World world;
    private Geomipmap l;
    private int texid;
    private Camera camera = null;
    private Font2D font = null;
    private float rtri = 0.0f;
    private float rquad = 0.0f;
    private float cnt1;
    private float cnt2;
    private Pyramid object;
    Entity e;
    private TestController cc = null;
    protected Logger log = null;
    private Timer timer;
    private ParticleEmitter pe;
    SplashScreen ss;

    private static int fogMode[] = { GL.EXP, GL.EXP2, GL.LINEAR };
    // Storage For Three Types Of Fog
    private static int fogfilter = 0; // Which Fog Mode To Use 
    private static float fogColor[] = { 0.5f, 0.5f, 0.5f, 1.0f }; // Fog Color

    static {
        if (GL.WGL_EXT_swap_control) {
            GL.wglSwapIntervalEXT(1);
        }
    }

    int tex1, tex2, tex3;

    static float wrap = 0;

    protected void update() {
        if (!cc.update(timer.getFrameRate())) {
            finish();
        }

        world.update(1 / timer.getFrameRate());
        timer.update();

        if (e.getPosition().x > 4000) {
            e.getPosition().x = 4000;
        }

        if (e.getPosition().z > 4000) {
            e.getPosition().z = 4000;
        }

        if (e.getPosition().x < 200) {
            e.getPosition().x = 200;
        }

        if (e.getPosition().z < 200) {
            e.getPosition().z = 200;
        }
        e.getPosition().y =
            hm1.getInterpolatedHeight(
                e.getPosition().x / 4,
                e.getPosition().z / 4)
                + 3;

        if (camera.getPosition().x > 4000) {
            camera.getPosition().x = 4000;
        }

        if (camera.getPosition().z > 4000) {
            camera.getPosition().z = 4000;
        }

        if (camera.getPosition().x < 200) {
            camera.getPosition().x = 200;
        }

        if (camera.getPosition().z < 200) {
            camera.getPosition().z = 200;
        }

        //      update animation....
        if (physics.getCurrentVelocity() < -1 && currentAnimation != 4) {
            currentAnimation = 4;
            ((Md3Model)e.getGeometry()).setLegsAnimation("LEGS_BACK");
        } else if (
            physics.getCurrentVelocity() < 1
                && physics.getCurrentVelocity() > -1) {
            if ((physics.getCurrentTurningVel() != 0)
                && currentAnimation != 0) {
                currentAnimation = 0;
                ((Md3Model)e.getGeometry()).setLegsAnimation("LEGS_TURN");
            } else if (
                physics.getCurrentTurningVel() == 0 && currentAnimation != 3) {
                currentAnimation = 3;
                ((Md3Model)e.getGeometry()).setLegsAnimation("LEGS_IDLE");
            }
        } else if (
            physics.getCurrentVelocity() > 1
                && physics.getCurrentVelocity() < 20
                && currentAnimation != 1) {
            currentAnimation = 1;
            ((Md3Model)e.getGeometry()).setLegsAnimation("LEGS_WALK");
        } else if (
            physics.getCurrentVelocity() > 20 && currentAnimation != 2) {
            currentAnimation = 2;
            ((Md3Model)e.getGeometry()).setLegsAnimation("LEGS_RUN");
        }

    }
    protected void render() {
        gl.clear(GL.COLOR_BUFFER_BIT | GL.DEPTH_BUFFER_BIT);
        gl.loadIdentity();
        cc.render();
        world.render();
        font.print(
            1,
            1,
            "FPS - "
                + timer.getFrameRate()
                + " : "
                + timer.getMinFrameRate()
                + " - "
                + timer.getMaxFrameRate(),
            0);
        font.print(1, 20, "Entity Location - " + e.getPosition().toString(), 0);
        font.print(
            1,
            40,
            "Patches rendered "
                + l.getNumPatchesRendered()
                + " / "
                + l.getNumPatches(),
            0);
        font.print(
            1,
            60,
            "Entities rendered "
                + world.getNumRenderedEntities()
                + " / "
                + world.getTotalEntities(),
            0);

    }
    private void initLogger() {
        log = LoggingSystem.getLoggingSystem().getLogger();
    }
    private void initCamera() {
        camera = new Camera(1, 850, 100, 850, 451, 100, 450, 0, 1, 0);
        camera.getFrustum().setBuffer(3.0f);

    }
    private void initDisplay() {
        DisplaySystem.createDisplaySystem(
            "test",
            "jme/data/Images/Monkey.jpg",
            true);
    }

    private void initGL() {
        gl = DisplaySystem.getDisplaySystem().getGL();
        glu = DisplaySystem.getDisplaySystem().getGLU();
        gl.shadeModel(GL.SMOOTH);
        gl.clearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.clearDepth(1.0);
        gl.enable(GL.DEPTH_TEST);
        gl.depthFunc(GL.LESS);
        gl.matrixMode(GL.PROJECTION);
        gl.loadIdentity();
        // Calculate The Aspect Ratio Of The Window
        glu.perspective(
            45.0f,
            (float)Display.getWidth() / (float)Display.getHeight(),
            0.1f,
            750.0f);
        gl.matrixMode(GL.MODELVIEW);
        gl.hint(GL.PERSPECTIVE_CORRECTION_HINT, GL.NICEST);
        gl.blendFunc(GL.SRC_ALPHA, GL.ONE);
        DisplaySystem.getDisplaySystem().cullMode(GL.BACK, true);

    }
    protected void reinit() {
        Keyboard.destroy();
        Mouse.destroy();
        try {
            Keyboard.create();
            Mouse.create();
            initGL();
            initTimer();
            TextureManager.getTextureManager().reload();
            font = new Font2D("jme/data/Font/font.png");

        } catch (Exception e) {
            e.printStackTrace();
        }

        float[] color = { 0.5f, 0.5f, 0.5f, 1.0f };
        l.setFogAttributes(GL.LINEAR, color, 0.35f, 100.0f, 750.0f);
        l.setDistanceFog(true);
    }
    protected void initSystem() {
        initDisplay();
        font = new Font2D("jme/data/Font/font.png");
        initLogger();
        initGL();
        initCamera();

        initTimer();

        SplashScreen ss3 = new SplashScreen();
        ss3.setTexture("jme/data/Images/tdemo.jpg");

        addSplashScreen(ss3);

    }

    protected void initGame() {
        hm1 = new FaultFractalHeightMap(1025, 64, 0, 255, 0.15f);
        hm1.setHeightScale(1.0f);
        pt = new ProceduralTexture(hm1);
        pt.addTexture(
            new ImageIcon("jme/data/texture/plants15.jpg"),
            -128,
            0,
            128);
        pt.addTexture(
            new ImageIcon("jme/data/texture/plants12.jpg"),
            0,
            128,
            255);
        pt.addTexture(
            new ImageIcon("jme/data/texture/highestTile.png"),
            128,
            255,
            384);
        pt.createTexture(128);

        //		SkyBox sb = new SkyBox(512);
        //		String[] tex = new String[6];
        //		tex[0] = "jme/data/Top.jpg";
        //		tex[1] = "jme/data/Bottom.jpg";
        //		tex[2] = "jme/data/Right.jpg";
        //		tex[3] = "jme/data/Left.jpg";
        //		tex[4] = "jme/data/Front.jpg";
        //		tex[5] = "jme/data/Back.jpg";
        //		sb.setTextures(tex);

        SkyDome sd = new SkyDome(16, 15, 15, 1, 1);
        sd.setTexture("jme/data/texture/clouds.png");
        sd.setDomeRotation(0.250f);

        WaterPlane wp = new WaterPlane(1025 * 2, 45, 0.25f);
        wp.setColor(new Vector3f(1.0f, 1.0f, 1.0f));
        wp.setTransparency(0.60f);
        wp.setTexture("jme/data/texture/water02.jpg");
        wp.setWaveSpeed(0.05f);
        wp.setTextureAnimation(1.0f, 1.0f);

        //		WaterMesh wp = new WaterMesh(16,64,2);
        //		wp.setTexture("jme/data/water02.jpg");
        //		wp.setBaseHeight(50.0f);
        //		wp.setWindSpeed(-2);

        sl = new SlopeLighting(hm1, 1, -1, 0.1f, 0.9f, 10);
        l = new Geomipmap(hm1, 17, camera);
        l.setDetailTexture("jme/data/texture/Detail.jpg", 64);
        l.setLightMap(sl);
        camera.setSky(sd);

        e = new Entity(1);
        //MilkshapeModel msmodel = new MilkshapeModel("jme/data/tris.ms3d");
        Md3Model msmodel =
            new Md3Model("jme/data/model/Paladin", "Paladin", "railgun");
        msmodel.setTorsoAnimation("TORSO_STAND");
        msmodel.setLegsAnimation("LEGS_IDLE");

        msmodel.setScale(new Vector3f(0.15f, 0.15f, 0.15f));

        e.setGeometry(msmodel);
        e.setPosition(
            new Vector3f(
                1000,
                hm1.getScaledHeightAtPoint(1000 / 4, 1000 / 4),
                1000));
        e.setVisibilityType(Entity.VISIBILITY_SPHERE);
        camera.setView(e.getPosition());

        //Move to app specific
        physics = new LandMobility();
        physics.setMaxVelocity(40.0f);
        physics.setMinVelocity(-20.0f);
        physics.setBaseAcceleration(60.0f);
        physics.setCoastDeceleration(20.0f);
        physics.setCurrentAngle(0);
        physics.setTurningVelocity(100.0f);
        e.setPhysicsModule(physics);

        l.setXScale(4.0f);
        l.setZScale(4.0f);
        l.setTexture(pt.getImageIcon());
        world = new World();
        world.addEntity(e);

        world.setWater(wp);
        world.setLocale(l);
        float[] color = { 0.5f, 0.5f, 0.5f, 1.0f };
        l.setFogAttributes(GL.LINEAR, color, 0.35f, 50.0f, 750.0f);
        l.setDistanceFog(true);
        l.setVolumetricFog(false);
        l.setVolumetricFogDepth(100);
        object = new Pyramid(10, 30);
        object.setColor(0.5f, 0.85f, 0.5f, 0.5f);
        object.setTexture("jme/data/texture/plants15.jpg");
        object.useDisplayList(true);

        //object = new MilkshapeModel("jme/data/tree.ms3d");

        Entity[] elist = new Entity[1000];
        float x, z;
        for (int i = 0; i < 1000; i++) {
            elist[i] = new Entity(i + 1);
            elist[i].setGeometry(object);
            elist[i].setVisibilityType(Entity.VISIBILITY_POINT);
            do {
                x = (float)Math.random() * 2000;
                z = (float)Math.random() * 2000;
            } while (
                hm1.getInterpolatedHeight(x / 2, z / 2) < 75
                    || hm1.getInterpolatedHeight(x / 4, z / 4) > 200);
            elist[i].setPosition(
                new Vector3f(
                    x,
                    hm1.getInterpolatedHeight(x / 4, z / 4) + 1,
                    z));
            world.addEntity(elist[i]);
            elist[i].setVisibilityType(Entity.VISIBILITY_SPHERE);
        }
        cc = new TestController(camera, e, this);
        cc.setHeightMap(hm1);
        cc.setTrackingDistance(30f);
        world.setCamera(camera);
        world.setEntityVisibility(true);

    }
    public void rebuildTerrain() {
        hm1.load();
        sl.setHeightMap(hm1);
        sl.createLighting();
    }

    private void initTimer() {
        timer = Timer.getTimer();
    }
    protected void cleanup() {
        Keyboard.destroy();
        Mouse.destroy();
        gl.destroy();
        TextureManager.reset();
    }
    public static void main(String[] args) {
        TestMain app = new TestMain();
        app.start();
    }

    public void saveTerrain(String filename) {
        hm1.save(filename);
    }
}
