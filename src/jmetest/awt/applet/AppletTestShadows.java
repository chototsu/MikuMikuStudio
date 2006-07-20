package jmetest.awt.applet;

import javax.swing.ImageIcon;

import jmetest.renderer.TestShadowPass;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.ChaseCamera;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.light.DirectionalLight;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.pass.BasicPassManager;
import com.jme.renderer.pass.RenderPass;
import com.jme.renderer.pass.ShadowedRenderPass;
import com.jme.scene.Node;
import com.jme.scene.VBOInfo;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.PQTorus;
import com.jme.scene.state.CullState;
import com.jme.scene.state.FogState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.awt.applet.SimpleJMEApplet;
import com.jmex.terrain.TerrainPage;
import com.jmex.terrain.util.FaultFractalHeightMap;
import com.jmex.terrain.util.ProceduralTextureGenerator;

public class AppletTestShadows extends SimpleJMEApplet {
    private static final long serialVersionUID = 1L;
    private Node m_character;
    private Node occluders;
    private ChaseCamera chaser;
    private TerrainPage page;
    private FogState fs;
    private Vector3f normal = new Vector3f();
    private static ShadowedRenderPass sPass = new ShadowedRenderPass();
    protected BasicPassManager pManager;
    
    public void simpleAppletUpdate() {
        pManager.updatePasses(this.getTimePerFrame());     
    }
    
    public void simpleAppletRender() {
        pManager.renderPasses(getRenderer());
    }
    
    public void simpleAppletSetup() {
        DisplaySystem.getDisplaySystem().setTitle("jME - Shadow Volume Test : X - enable/disable shadows");
        DisplaySystem.getDisplaySystem().getRenderer().setBackgroundColor(ColorRGBA.gray);

        pManager = new BasicPassManager();
        
        setupCharacter();
        setupTerrain();
        setupChaseCamera();
        //setupInput();
        setupOccluders();
        
        getRootNode().setRenderQueueMode(Renderer.QUEUE_OPAQUE);
        
        /** Assign key X to action "toggle_shadows". */
        KeyBindingManager.getKeyBindingManager().set("toggle_shadows",
                KeyInput.KEY_X);
 
        
        sPass.add(getRootNode());
        sPass.addOccluder(m_character);
        sPass.addOccluder(occluders);
        sPass.setRenderShadows(true);
        sPass.setLightingMethod(ShadowedRenderPass.ADDITIVE);
        pManager.add(sPass);
        
        RenderPass rPass = new RenderPass();
        rPass.add(this.getFPSNode());
        pManager.add(rPass);

    }
    
    private void setupCharacter() {
        PQTorus b = new PQTorus("torus - target", 2, 3, 2.0f, 1.0f, 64, 12);
        b.setModelBound(new BoundingBox());
        b.updateModelBound();
        b.setVBOInfo(new VBOInfo(true));
        m_character = new Node("char node");
        getRootNode().attachChild(m_character);
        m_character.attachChild(b);
        m_character.updateWorldBound(); // We do this to allow the camera setup access to the world bound in our setup code.

        TextureState ts = getRenderer().createTextureState();
        ts.setEnabled(true);
        ts.setTexture(
            TextureManager.loadTexture(
            TestShadowPass.class.getClassLoader().getResource(
            "jmetest/data/images/Monkey.jpg"),
            Texture.MM_LINEAR,
            Texture.FM_LINEAR));
        m_character.setRenderState(ts);
    }
    
    private void setupTerrain() {

        DirectionalLight dr = new DirectionalLight();
        dr.setEnabled(true);
        dr.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        dr.setAmbient(new ColorRGBA(.2f, .2f, .2f, .3f));
        dr.setDirection(new Vector3f(0.5f, -0.4f, 0).normalizeLocal());
        dr.setShadowCaster(true);

        PointLight pl = new PointLight();
        pl.setEnabled(true);
        pl.setDiffuse(new ColorRGBA(.7f, .7f, .7f, 1.0f));
        pl.setAmbient(new ColorRGBA(.25f, .25f, .25f, .25f));
        pl.setLocation(new Vector3f(0,500,0));
        pl.setShadowCaster(true);

        DirectionalLight dr2 = new DirectionalLight();
        dr2.setEnabled(true);
        dr2.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        dr2.setAmbient(new ColorRGBA(.2f, .2f, .2f, .4f));
        dr2.setDirection(new Vector3f(-0.2f, -0.3f, .2f).normalizeLocal());
        dr2.setShadowCaster(true);

        CullState cs = getRenderer().createCullState();
        cs.setCullMode(CullState.CS_BACK);
        cs.setEnabled(true);
        getRootNode().setRenderState(cs);

        getLightState().detachAll();
        getLightState().attach(dr);
        getLightState().attach(dr2);
        getLightState().attach(pl);
        getLightState().setGlobalAmbient(new ColorRGBA(0.6f, 0.6f, 0.6f, 1.0f));

        FaultFractalHeightMap heightMap = new FaultFractalHeightMap(257, 32, 0,
                255, 0.55f);
        Vector3f terrainScale = new Vector3f(10, 1, 10);
        heightMap.setHeightScale(0.001f);
        page = new TerrainPage("Terrain", 33, heightMap.getSize(),
                terrainScale, heightMap.getHeightMap(), false);

        page.setDetailTexture(1, 16);
        getRootNode().attachChild(page);

        ProceduralTextureGenerator pt = new ProceduralTextureGenerator(
                heightMap);
        pt.addTexture(new ImageIcon(TestShadowPass.class.getClassLoader()
                .getResource("jmetest/data/texture/grassb.png")), -128, 0, 128);
        pt.addTexture(new ImageIcon(TestShadowPass.class.getClassLoader()
                .getResource("jmetest/data/texture/dirt.jpg")), 0, 128, 255);
        pt.addTexture(new ImageIcon(TestShadowPass.class.getClassLoader()
                .getResource("jmetest/data/texture/highest.jpg")), 128, 255,
                384);

        pt.createTexture(512);

        TextureState ts = getRenderer().createTextureState();
        ts.setEnabled(true);
        Texture t1 = TextureManager.loadTexture(pt.getImageIcon().getImage(),
                Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, true);
        ts.setTexture(t1, 0);

        Texture t2 = TextureManager.loadTexture(TestShadowPass.class
                .getClassLoader()
                .getResource("jmetest/data/texture/Detail.jpg"),
                Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR);
        ts.setTexture(t2, 1);
        t2.setWrap(Texture.WM_WRAP_S_WRAP_T);

        t1.setApply(Texture.AM_COMBINE);
        t1.setCombineFuncRGB(Texture.ACF_MODULATE);
        t1.setCombineSrc0RGB(Texture.ACS_TEXTURE);
        t1.setCombineOp0RGB(Texture.ACO_SRC_COLOR);
        t1.setCombineSrc1RGB(Texture.ACS_PRIMARY_COLOR);
        t1.setCombineOp1RGB(Texture.ACO_SRC_COLOR);
        t1.setCombineScaleRGB(1.0f);

        t2.setApply(Texture.AM_COMBINE);
        t2.setCombineFuncRGB(Texture.ACF_ADD_SIGNED);
        t2.setCombineSrc0RGB(Texture.ACS_TEXTURE);
        t2.setCombineOp0RGB(Texture.ACO_SRC_COLOR);
        t2.setCombineSrc1RGB(Texture.ACS_PREVIOUS);
        t2.setCombineOp1RGB(Texture.ACO_SRC_COLOR);
        t2.setCombineScaleRGB(1.0f);
        getRootNode().setRenderState(ts);

        fs = getRenderer().createFogState();
        fs.setDensity(0.5f);
        fs.setEnabled(true);
        fs.setColor(new ColorRGBA(0.5f, 0.5f, 0.5f, 0.5f));
        fs.setEnd(1000);
        fs.setStart(500);
        fs.setDensityFunction(FogState.DF_LINEAR);
        fs.setApplyFunction(FogState.AF_PER_VERTEX);
        getRootNode().setRenderState(fs);
    }

    private void setupOccluders() {

        TextureState ts = getRenderer().createTextureState();
        ts.setEnabled(true);
        ts.setTexture(
            TextureManager.loadTexture(
            TestShadowPass.class.getClassLoader().getResource(
            "jmetest/data/texture/rust.png"),
            Texture.MM_LINEAR_LINEAR,
            Texture.FM_LINEAR));

        occluders = new Node("occs");
        occluders.setRenderState(ts);
        getRootNode().attachChild(occluders);
        for (int i = 0; i < 50; i++) {
            Box b = new Box("box", new Vector3f(), 8, 50, 8);
            b.setModelBound(new BoundingBox());
            b.updateModelBound();
            float x = (float) Math.random() * 2000 - 1000;
            float z = (float) Math.random() * 2000 - 1000;
            b.setLocalTranslation(new Vector3f(x, page.getHeight(x, z)+50, z));
            page.getSurfaceNormal(b.getLocalTranslation(), normal );
            if (normal != null)
                b.rotateUpTo(normal);
            occluders.attachChild(b);
        }
       occluders.lock();
    }
    
    private void setupChaseCamera() {
        Vector3f targetOffset = new Vector3f();
        targetOffset.y = ((BoundingBox) m_character.getWorldBound()).yExtent * 1.5f;
        chaser = new ChaseCamera(getCamera(), m_character);
        chaser.setTargetOffset(targetOffset);
        chaser.getMouseLook().setMinRollOut(150);
        chaser.setMaxDistance(300);
    }

//    private void setupInput() {
//        HashMap<String, Object> handlerProps = new HashMap<String, Object>();
//        handlerProps.put(ThirdPersonHandler.PROP_DOGRADUAL, "true");
//        handlerProps.put(ThirdPersonHandler.PROP_TURNSPEED, ""+(.5f * FastMath.PI));
//        handlerProps.put(ThirdPersonHandler.PROP_LOCKBACKWARDS, "true");
//        handlerProps.put(ThirdPersonHandler.PROP_CAMERAALIGNEDMOVE, "true");
//        handlerProps.put(ThirdPersonHandler.PROP_ROTATEONLY, "true");
//        getInput() = new ThirdPersonHandler(m_character, getCamera(), handlerProps);
//        input.setActionSpeed(100f);
//    }
}
