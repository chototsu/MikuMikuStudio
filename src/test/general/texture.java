/*
 * Created on Jun 26, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package test.general;


import javax.swing.ImageIcon;

import jme.texture.ProceduralTexture;
import jme.utility.Timer;
import jme.geometry.hud.text.Font2D;
import jme.geometry.hud.SplashScreen;
import jme.geometry.primitive.Box;
import jme.geometry.primitive.Sphere;
import jme.geometry.primitive.Pyramid;
import jme.locale.external.BruteForce;
import jme.locale.external.data.MidPointHeightMap;
import jme.entity.Entity;
import org.lwjgl.opengl.GL; 
import org.lwjgl.vector.Vector3f;

import jme.AbstractGame; 
import jme.system.DisplaySystem;
import jme.entity.camera.Camera;

public class texture extends AbstractGame { 

	// Initiate a timer
	Timer timer;
	
	// Initiate a font
	Font2D font;
	
	// Initiate a camera
	Camera camera;
	
	// Initiate a box object
	Box cube;
	
	// Initiate a sphere object
	Sphere sphere;
	
	// Initiate a pyramid object
	Pyramid pyramid;
	
	Entity e0, e1, e2;
    
    BruteForce b;
	
	// Id for texture
	int id, entity_id0 = 1, entity_id1 = 2, entity_id2 = 3;
	
	// Flag for key mapping
	int Flag;
	
	
	float rtri;
	float rquad;
	
	// Initiate a base controller
	NewKeyController controller;

	
   protected void update() {
	if(!controller.update(timer.getFrameRate())) { 
		finish(); 
	}
   	timer.update();
   	
   }
   
   protected void setRenderObject(int value) {
   	Flag = value;
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
	  // gl.pushMatrix();

	  // move right 3 units
	  // gl.translatef(0.0f, 0.0f, -5.0f);
	  
	  // Rotate the quad on the X-axis
	  // gl.rotatef(rquad,1.0f,0.0f,0.0f);
	  
	  // Bind the texture
	  // TextureManager.getTextureManager().bind(id);
	  

	  //cube.initialize();
	  //cube.render();
      b.render();
	  
	  if(Flag == 3) {
	  	gl.pushMatrix();
	  	e1.render();
	  	gl.popMatrix();
	  }
	  else if(Flag == 1) {
	  	gl.pushMatrix();
	  	e0.render();
	  	gl.popMatrix();
	  }
	  else if (Flag == 2) {
	  	gl.pushMatrix();
	  	e2.render();
	  	gl.popMatrix();
	  }
	  
	  gl.end();
	
	// gl.popMatrix();
	
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
	  gl.clearColor(1.0f, 1.0f, 1.0f, 0.0f);
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
	camera = new Camera(1,200,0,0,0,0,0,0,1,0);
		
	// instantiate a new controller
	controller = new NewKeyController(camera, this);
	
	// size of the objects
	int size = 3;
    
    MidPointHeightMap h = new MidPointHeightMap(128,1.5f);
    b= new BruteForce(h);
    ProceduralTexture pt = new ProceduralTexture(h);
    pt.addTexture(new ImageIcon("jme/data/plants15.jpg"), -128, 0, 128);
    pt.addTexture(new ImageIcon("jme/data/plants12.jpg"), 0, 128, 255);
    pt.addTexture(new ImageIcon("jme/data/highestTile.png"), 128, 255, 384);
    pt.createTexture(1024);
    b.setTexture(pt.getImageIcon());
    h.setHeightScale(0.25f);
	
	// initialize the sphere
	sphere = new Sphere(3.0f,30,30);
	sphere.setTexture("jme/data/Monkey.gif");
	
	// initialize the sphere entity
	e1 = new Entity(entity_id1);
	e1.setVisibilityType(Entity.VISIBILITY_SPHERE);
	e1.setPosition(new Vector3f(0.0f, 0.0f, 0.0f));
	e1.setGeometry(sphere);
	
	// initialize the cube
	cube = new Box(size);
	cube.setTexture("jme/data/Monkey.gif");
	
	// initialize the cube entity
	e0 = new Entity(entity_id0);
	e0.setVisibilityType(Entity.VISIBILITY_CUBE);
	e0.setPosition(new Vector3f(0.0f, 0.0f, 0.0f));
	e0.setGeometry(cube);
	
	// initialize a pyramid
	pyramid = new Pyramid(3.0f, 5.0f);
	pyramid.setTexture("jme/data/Monkey.gif");
	
	// initialize the pyramid entity
	e2 = new Entity(entity_id2);
	e2.setVisibilityType(Entity.VISIBILITY_CUBE);
	e2.setPosition(new Vector3f(0.0f, 0.0f, 0.0f));
	e2.setGeometry(pyramid);
	
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
	  texture app = new texture();
	  app.start(); 
   } 

}

