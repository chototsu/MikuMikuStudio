package test.general; 
import jme.utility.Timer;
import jme.controller.BaseFPSController;
import jme.geometry.hud.text.Font2D;
import jme.geometry.hud.SplashScreen;
import jme.geometry.primitive.Box;
import jme.entity.Entity;
import org.lwjgl.opengl.GL; 
import org.lwjgl.vector.Vector3f;

import jme.AbstractGame; 
import jme.system.DisplaySystem;
import jme.entity.camera.Camera;

public class Tutorial2 extends AbstractGame { 

	// Initiate a timer
	Timer timer;
	
	// Initiate a font
	Font2D font;
	
	// Initiate a camera
	Camera camera;
	
	// Initiate a box object
	Box cube;
	
	Entity e0;
	
	// Id for texture
	int id, entity_id0 = 1;
	
	
	float rtri;
	float rquad;
	
	// Initiate a base controller
	BaseFPSController controller;
	
   protected void update() {
	if(!controller.update(timer.getFrameRate())) { 
		finish(); 
	}
	timer.update();
   	
   }

   protected void render() { 
	  gl.clear(GL.COLOR_BUFFER_BIT | GL.DEPTH_BUFFER_BIT); 
	  gl.loadIdentity();
	  
	  // modify the matrix by rendering the matrix view
	  controller.render();
	  
		
	// enable texturing
	gl.enable(GL.TEXTURE_2D);
	  
	//-----------------------------------	  
	// Begin code for the cube
	//-----------------------------------
	
	  // Push the matrix for the cube
	  gl.pushMatrix();

	  // move right 3 units
	  // gl.translatef(0.0f, 0.0f, -5.0f);
	  
	  // Rotate the quad on the X-axis
	  // gl.rotatef(rquad,1.0f,0.0f,0.0f);
	  
	  // Bind the texture
	  // TextureManager.getTextureManager().bind(id);
	  

	  
	  //cube.initialize();
	  //cube.render();
	  e0.render();
	  
	e0.setRoll(rquad);
	  e0.setPitch(rquad);
	  e0.setYaw(rquad);
	 
	  gl.end();
	
	gl.popMatrix();
	
	rtri+=0.2f;
	rquad-=0.15f;
	
	// Print out for FPS diagnostics
	font.print(1,1,"Frame Rate: " + timer.getFrameRate(), 0);
 
   }
   
   protected void initSystem() { 
	  //create the window 
	  DisplaySystem.createDisplaySystem("test","jme/data/nmsulogo.gif",
true); 
	  //create the OpenGL bindings 
	  gl = DisplaySystem.getDisplaySystem().getGL(); 
	  glu = DisplaySystem.getDisplaySystem().getGLU(); 
	  //Define what the clear color will be (black) 
	  gl.clearColor(0.0f, 0.0f, 0.0f, 0.0f);
	  gl.matrixMode(GL.PROJECTION);
	  gl.loadIdentity();
	  
	  // Calculate The Aspect Ratio Of The Window
	  glu.perspective(
		45.0f,
		(float)gl.getWidth() / (float)gl.getHeight(),
		0.01f,
		750.0f);
		gl.matrixMode(GL.MODELVIEW);
		gl.hint(GL.PERSPECTIVE_CORRECTION_HINT, GL.NICEST);
	
		SplashScreen Splash = new SplashScreen();
		Splash.setTexture("jme/data/Monkey.jpg");
		Splash.setDelay(100);
	
		addSplashScreen(Splash);
		
   }

   protected void initGame() {
	// instantiate timer
	timer = Timer.getTimer();
		
	// instantiate font
	font = new Font2D("jme/data/font.png");
		
	// blend font together so it doesn't chop the letters off
	gl.blendFunc(GL.SRC_ALPHA, GL.ONE);
		
	// instantiate a new camera
	camera = new Camera(1,0,0,10,0,0,0,0,1,0);
		
	// instantiate a new controller
	controller = new BaseFPSController(camera);
	
	// size of the cube
	int size = 3;
	
	// initialize the cube
	cube = new Box(size);
	cube.setTexture("jme/data/Monkey.gif");
	
	// initialize the entity
	e0 = new Entity(entity_id0);
	e0.setVisibilityType(Entity.VISIBILITY_CUBE);
	e0.setPosition(new Vector3f(0.0f, 0.0f, 0.0f));
	e0.setGeometry(cube);
	//e0.setPitch(45);
	//e0.setRoll(10);
	//e0.setYaw(30);
	
	

	
	// Load a texture using the TextureManager
	//id = TextureManager.getTextureManager().loadTexture("textures/Monkey.jpg",
//GL.LINEAR_MIPMAP_LINEAR, GL.LINEAR, true);
   }

   protected void reinit() { 
	  //nothing here... YET! 
   } 

   protected void cleanup() { 
	  //clean up the OpenGL resources 
	  gl.destroy(); 
   } 
    
   public static void main(String[] args) { 
	  Tutorial2 app = new Tutorial2();
	  app.start(); 
   } 

}
