package jmetest.terrain;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import jmetest.renderer.TestSkybox;

import com.jme.app.VariableTimestepGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.InputHandler;
import com.jme.input.NodeHandler;
import com.jme.light.AmbientLight;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.CameraNode;
import com.jme.scene.Node;
import com.jme.scene.Skybox;
import com.jme.scene.state.CullState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.terrain.TerrainBlock;
import com.jme.terrain.util.ImageBasedHeightMap;
import com.jme.terrain.util.ProceduralTextureGenerator;
import com.jme.util.TextureManager;

public class Scenic2 extends VariableTimestepGame { 

	   public static void main(String[] args) { 
	      Scenic2 app = new Scenic2(); 
	      app.start(); 
	   } 
	   private Camera cam; 
	   private Node valley = null, scene = null, root = null, sky = null, player = null; 
	   private CameraNode cnode = null; 
	   private LightState lts = null; 
	   private ImageBasedHeightMap hm = null; 
	   private TerrainBlock tb = null; 
	   private InputHandler input = null; 
	   private BufferedImage land; 
	   Skybox m_skybox; 
	    
	   protected void update(float interpolation) 
	   { 
	       input.update(interpolation * 1000); 
	        root.updateGeometricState(interpolation * 10, true); 
	        sky.setLocalTranslation(cam.getLocation()); 
	        sky.updateGeometricState(interpolation, true); 
	        cam.update(); 
	         
	       float height = tb.getHeight(cnode.getLocalTranslation().x, cnode.getLocalTranslation().z); 
	       if (!Float.isNaN(height)) { 
	       cnode.getLocalTranslation().y = height + 5000; 
	       } 
	   } 
	    
	   protected void render(float interpolation) 
	   { 
	      display.getRenderer().clearBuffers(); 
	       display.getRenderer().draw(root); 
	   } 
	    
	   protected void initSystem() 
	   { 
	      try{ 
	          display = DisplaySystem.getDisplaySystem(properties.getRenderer()); 
	            display.createWindow(1024,768,32,85,true); 
	            cam = display.getRenderer().createCamera(1024, 768); 
	           } 
	         catch (JmeException e) 
	         { 
	               e.printStackTrace(); 
	               System.exit(1); 
	         } 

	         // setup camera - can increase rendering view from 1000 to 100000 
	         cam.setFrustum(1.0f, 100000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f); 
	         Vector3f loc = new Vector3f(0, 0, 0); 
	         Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f); 
	         Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f); 
	         Vector3f dir = new Vector3f(0.0f, 0.0f, -1.0f); 
	         cam.setFrame(loc, left, up, dir); 
	         display.getRenderer().setCamera(cam); 
	           
	         cnode = new CameraNode("Camera Node", cam); 
	         input = new NodeHandler(this, cnode, "LWJGL"); 
	       
	   } 
	    
	   protected void initGame() 
	   { 
	       
	       root = new Node("root"); 
	        scene = new Node("3D Scene Node"); 
	        valley = new Node("Valley"); 
	        sky = new Node("sb"); 
	         
	        input.setKeySpeed(1000f); 
	        input.setMouseSpeed(.06f); 

	        CullState cs = display.getRenderer().createCullState(); 
	        cs.setCullMode(CullState.CS_NONE); 
	        cs.setEnabled(true); 
	        root.setRenderState(cs); 
	         
	        //Light 
	        PointLight light = new PointLight(); 
	        light.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 10.0f)); 
	        light.setAmbient(new ColorRGBA(1.0f, 0.85f, 0.95f, 0.55f)); 
	        light.setLocation(new Vector3f(7500, 1000,50000 )); 
	        light.setEnabled(true); 

	        AmbientLight am = new AmbientLight(); 
	        am.setDiffuse(new ColorRGBA(1.0f, 0.85f, 0.95f, 1.0f)); 
	        am.setAmbient(new ColorRGBA(1.0f, 0.95f, 1.0f, 0.5f)); 
	        lts = display.getRenderer().createLightState(); 
	        lts.attach(am); 
	        lts.attach(light); 
	        lts.setEnabled(true); 
	        am.setEnabled(true); 

	        lts.setTwoSidedLighting(true); 
	        scene.setRenderState(lts); 

	        ZBufferState buf = display.getRenderer().createZBufferState(); 
	        buf.setEnabled(true); 
	        buf.setFunction(ZBufferState.CF_LEQUAL); 
	        root.setRenderState(buf); 
	         
	         
	        //my Terrain 
	        try 
	        { 
	           land = ImageIO.read(new File("f:/program files/eclipse/workspace/mgx/bin/res/cave.png")); 
	        } 
	        catch(IOException e) 
	        { 
	           System.out.println(e); 
	        } 
	         
	        ImageBasedHeightMap hm = new ImageBasedHeightMap(land); 
	        Vector3f terrainScale = new Vector3f(300000,5000,800000); 
	        tb = new TerrainBlock("Terrain", hm.getSize(), terrainScale, 
	                                           hm.getHeightMap(), 
	                                           new Vector3f(0,0,0),true); 
	        tb.setDetailTexture(1,5); 
	        tb.setModelBound(new BoundingBox()); 
	        tb.updateModelBound(); 
	        valley.setLocalScale(1); 
	        tb.setRenderQueueMode(Renderer.QUEUE_OPAQUE); 
	        valley.attachChild(tb); 
	        valley.setRenderState(cs); 
	         
	        ProceduralTextureGenerator pt = new ProceduralTextureGenerator( 
	          hm); 
	        pt.addTexture(new ImageIcon(Scenic2.class.getClassLoader() 
	                                    .getResource("res/dirt.jpg")), 
	                -128, 64, 128); 
	        pt.addTexture(new ImageIcon(Scenic2.class.getClassLoader() 
	                                    .getResource("res/grass.jpg")), 
	                   128, 192, 255); 
	        pt.addTexture(new ImageIcon(Scenic2.class.getClassLoader() 
	                                    .getResource("res/grassb.png")), 
	                      0, 516, 
	                      1024); 

	        pt.createTexture(1024); 

	        TextureState ts = display.getRenderer().createTextureState(); 
	        ts.setEnabled(true); 
	        Texture t1 = TextureManager.loadTexture( 
	            pt.getImageIcon().getImage(), 
	            Texture.MM_LINEAR, 
	            Texture.FM_LINEAR, true, true); 
	        ts.setTexture(t1, 0); 

	        Texture t2 = TextureManager.loadTexture( 
	            Scenic2.class.getClassLoader().getResource( 
	            "res/Detail.jpg"), 
	            Texture.MM_LINEAR, 
	            Texture.FM_LINEAR, true); 

	        ts.setTexture(t2, 1); 
	        t2.setWrap(Texture.WM_WRAP_S_WRAP_T); 

	        t1.setApply(Texture.AM_COMBINE); 
	        t1.setCombineFuncRGB(Texture.ACF_MODULATE); 
	        t1.setCombineSrc0RGB(Texture.ACS_TEXTURE); 
	        t1.setCombineOp0RGB(Texture.ACO_SRC_COLOR); 
	        t1.setCombineSrc1RGB(Texture.ACS_PRIMARY_COLOR); 
	        t1.setCombineOp1RGB(Texture.ACO_SRC_COLOR); 
	        t1.setCombineScaleRGB(1); 

	        t2.setApply(Texture.AM_COMBINE); 
	        t2.setCombineFuncRGB(Texture.ACF_ADD_SIGNED); 
	        t2.setCombineSrc0RGB(Texture.ACS_TEXTURE); 
	        t2.setCombineOp0RGB(Texture.ACO_SRC_COLOR); 
	        t2.setCombineSrc1RGB(Texture.ACS_PREVIOUS); 
	        t2.setCombineOp1RGB(Texture.ACO_SRC_COLOR); 
	        t2.setCombineScaleRGB(1); 
	        valley.setRenderState(ts); 
	        //valley.updateGeometricState(0.0f, true); 
	        valley.setLocalTranslation(new Vector3f(0,-100,0)); 
	        scene.attachChild(valley); 
	         
	        m_skybox = new Skybox("skybox",200,200, 200); 
	         
	        Texture north = TextureManager.loadTexture( 
	            TestSkybox.class.getClassLoader().getResource( 
	            "res/north.jpg"), 
	            Texture.MM_LINEAR, 
	            Texture.FM_LINEAR, 
	            true); 
	        Texture south = TextureManager.loadTexture( 
	            TestSkybox.class.getClassLoader().getResource( 
	            "res/south.jpg"), 
	            Texture.MM_LINEAR, 
	            Texture.FM_LINEAR, 
	            true); 
	        Texture east = TextureManager.loadTexture( 
	            TestSkybox.class.getClassLoader().getResource( 
	            "res/east.jpg"), 
	            Texture.MM_LINEAR, 
	            Texture.FM_LINEAR, 
	            true); 
	        Texture west = TextureManager.loadTexture( 
	            TestSkybox.class.getClassLoader().getResource( 
	            "res/west.jpg"), 
	            Texture.MM_LINEAR, 
	            Texture.FM_LINEAR, 
	            true); 
	        Texture up = TextureManager.loadTexture( 
	            TestSkybox.class.getClassLoader().getResource( 
	            "res/top.jpg"), 
	            Texture.MM_LINEAR, 
	            Texture.FM_LINEAR, 
	            true); 
	        Texture down = TextureManager.loadTexture( 
	            TestSkybox.class.getClassLoader().getResource( 
	            "res/dirt.jpg"), 
	            Texture.MM_LINEAR, 
	            Texture.FM_LINEAR, 
	            true); 

	        m_skybox.setTexture(Skybox.NORTH, north); 
	        m_skybox.setTexture(Skybox.WEST, west); 
	        m_skybox.setTexture(Skybox.SOUTH, south); 
	        m_skybox.setTexture(Skybox.EAST, east); 
	        m_skybox.setTexture(Skybox.UP, up); 
	        m_skybox.setTexture(Skybox.DOWN, down); 
	        sky.attachChild(m_skybox); 
	        sky.setRenderQueueMode(Renderer.QUEUE_OPAQUE); 
	        sky.setForceView(true); 
	        root.attachChild(sky); 
	        cnode.setLocalTranslation(new Vector3f(80000, tb.getHeight(5000,10000) + 5000, 100000)); 
	        scene.attachChild(cnode); 
	        root.updateGeometricState(0.0f, true); 
	        root.attachChild(scene); 
	        root.updateRenderState(); 
	        root.setForceView(true);    
	         
	   }

	/* (non-Javadoc)
	 * @see com.jme.app.VariableTimestepGame#reinit()
	 */
	protected void reinit() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.jme.app.VariableTimestepGame#cleanup()
	 */
	protected void cleanup() {
		// TODO Auto-generated method stub
		
	}    
}