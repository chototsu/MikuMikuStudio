package jmetest.terrain;

import java.util.logging.Logger;

import com.jme.app.SimplePassGame;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.pass.RenderPass;
import com.jme.scene.PassNode;
import com.jme.scene.PassNodeState;
import com.jme.scene.Spatial;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jmex.terrain.TerrainPage;
import com.jmex.terrain.util.RawHeightMap;

/**
 * TestTerrainSplatting shows multipass texturesplatting(6 passes) through usage of the PassNode
 * 
 * @author Heightmap and textures originally from Jadestone(but heavily downsampled)
 * @author Rikard Herlitz (MrCoder)
 */
public class TestTerrainSplatting extends SimplePassGame {
    private static final Logger logger = Logger
            .getLogger(TestTerrainSplatting.class.getName());

    private float globalSplatScale = 90.0f;

    public static void main(String[] args) {
        TestTerrainSplatting app = new TestTerrainSplatting();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    protected void simpleInitGame() {
        display.setTitle("Test Terrainsplatting");

        setupEnvironment();

        rootNode.attachChild(createTerrain());

        RenderPass rootPass = new RenderPass();
        rootPass.add(rootNode);
        pManager.add(rootPass);

        RenderPass fpsPass = new RenderPass();
        fpsPass.add(fpsNode);
        pManager.add(fpsPass);
    }

    private Spatial createTerrain() {
        RawHeightMap heightMap = new RawHeightMap(TestTerrainSplatting.class
                .getClassLoader().getResource(
                        "jmetest/data/texture/terrain/heights.raw").getFile(), 129,
                RawHeightMap.FORMAT_16BITLE, false);

        Vector3f terrainScale = new Vector3f(5, 0.003f, 6);
        heightMap.setHeightScale(0.001f);
        TerrainPage page = new TerrainPage("Terrain", 33, heightMap.getSize(),
                terrainScale, heightMap.getHeightMap(), false);
        page.getLocalTranslation().set(0, 10, 0);
        page.setDetailTexture(1, 1);

        // create some interesting texturestates for splatting
        TextureState ts1 = createSplatTextureState(
                "jmetest/data/texture/terrain/baserock.jpg", null);

        TextureState ts2 = createSplatTextureState(
                "jmetest/data/texture/terrain/darkrock.jpg",
                "jmetest/data/texture/terrain/darkrockalpha.png");

        TextureState ts3 = createSplatTextureState(
                "jmetest/data/texture/terrain/deadgrass.jpg",
                "jmetest/data/texture/terrain/deadalpha.png");

        TextureState ts4 = createSplatTextureState(
                "jmetest/data/texture/terrain/nicegrass.jpg",
                "jmetest/data/texture/terrain/grassalpha.png");

        TextureState ts5 = createSplatTextureState(
                "jmetest/data/texture/terrain/road.jpg",
                "jmetest/data/texture/terrain/roadalpha.png");

        TextureState ts6 = createLightmapTextureState("jmetest/data/texture/terrain/lightmap.jpg");

        // alpha used for blending the passnodestates together
        AlphaState as = display.getRenderer().createAlphaState();
        as.setBlendEnabled(true);
        as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
        as.setTestEnabled(true);
        as.setTestFunction(AlphaState.TF_GREATER);
        as.setEnabled(true);

        // alpha used for blending the lightmap
        AlphaState as2 = display.getRenderer().createAlphaState();
        as2.setBlendEnabled(true);
        as2.setSrcFunction(AlphaState.SB_DST_COLOR);
        as2.setDstFunction(AlphaState.DB_SRC_COLOR);
        as2.setTestEnabled(true);
        as2.setTestFunction(AlphaState.TF_GREATER);
        as2.setEnabled(true);

        // //////////////////// PASS STUFF START
        // try out a passnode to use for splatting
        PassNode splattingPassNode = new PassNode("SplatPassNode");
        splattingPassNode.attachChild(page);

        PassNodeState passNodeState = new PassNodeState();
        passNodeState.setPassState(ts1);
        splattingPassNode.addPass(passNodeState);

        passNodeState = new PassNodeState();
        passNodeState.setPassState(ts2);
        passNodeState.setPassState(as);
        splattingPassNode.addPass(passNodeState);

        passNodeState = new PassNodeState();
        passNodeState.setPassState(ts3);
        passNodeState.setPassState(as);
        splattingPassNode.addPass(passNodeState);

        passNodeState = new PassNodeState();
        passNodeState.setPassState(ts4);
        passNodeState.setPassState(as);
        splattingPassNode.addPass(passNodeState);

        passNodeState = new PassNodeState();
        passNodeState.setPassState(ts5);
        passNodeState.setPassState(as);
        splattingPassNode.addPass(passNodeState);

        passNodeState = new PassNodeState();
        passNodeState.setPassState(ts6);
        passNodeState.setPassState(as2);
        splattingPassNode.addPass(passNodeState);
        ////////////////////// PASS STUFF END

        // lock some things to increase the performance
        splattingPassNode.lockBounds();
        splattingPassNode.lockTransforms();
        splattingPassNode.lockShadows();

        return splattingPassNode;
    }

    private void setupEnvironment() {
        cam.setFrustumPerspective(50.0f, (float) display.getWidth()
                / (float) display.getHeight(), 1f, 1000f);
        cam.setLocation( new Vector3f( -270, 180, -270 ) );
        cam.lookAt( new Vector3f( 0, 0, 0 ), Vector3f.UNIT_Y );
        cam.update();

        CullState cs = display.getRenderer().createCullState();
        cs.setCullMode(CullState.CS_BACK);
        rootNode.setRenderState(cs);

        rootNode.setLightCombineMode(LightState.OFF);
    }

    private void addAlphaSplat(TextureState ts, String alpha) {
        Texture t1 = TextureManager.loadTexture(TestTerrainSplatting.class
                .getClassLoader().getResource(alpha), Texture.MM_LINEAR_LINEAR,
                Texture.FM_LINEAR);
        t1.setWrap(Texture.WM_WRAP_S_WRAP_T);
        t1.setApply(Texture.AM_COMBINE);
        t1.setCombineFuncRGB(Texture.ACF_REPLACE);
        t1.setCombineSrc0RGB(Texture.ACS_PREVIOUS);
        t1.setCombineOp0RGB(Texture.ACO_SRC_COLOR);
        t1.setCombineFuncAlpha(Texture.ACF_REPLACE);
        ts.setTexture(t1, ts.getNumberOfSetTextures());
    }

    private TextureState createSplatTextureState(String texture, String alpha) {
        TextureState ts = display.getRenderer().createTextureState();

        Texture t0 = TextureManager.loadTexture(TestTerrainSplatting.class
                .getClassLoader().getResource(texture),
                Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR);
        t0.setWrap(Texture.WM_WRAP_S_WRAP_T);
        t0.setApply(Texture.AM_MODULATE);
        t0.setScale(new Vector3f(globalSplatScale, globalSplatScale, 1.0f));
        ts.setTexture(t0, 0);

        if (alpha != null) {
            addAlphaSplat(ts, alpha);
        }

        return ts;
    }

    private TextureState createLightmapTextureState(String texture) {
        TextureState ts = display.getRenderer().createTextureState();

        Texture t0 = TextureManager.loadTexture(TestTerrainSplatting.class
                .getClassLoader().getResource(texture),
                Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR);
        t0.setWrap(Texture.WM_WRAP_S_WRAP_T);
        ts.setTexture(t0, 0);

        return ts;
    }
}
