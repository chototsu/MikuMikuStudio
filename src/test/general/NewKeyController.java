/*
 * Created on Jun 26, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package test.general;

import org.lwjgl.input.Keyboard;
import jme.controller.BaseFPSController;
import jme.entity.*;
import jme.entity.camera.Camera;

public class NewKeyController extends BaseFPSController { 
	private texture app;
	
	public NewKeyController(Entity entity, texture app) {
		super((Camera)entity); //just send it to BaseFPSController to handle. 
		this.app = app;
		setKeys();
	}
   
   public void setKeys() {
	key.set("sphere", Keyboard.KEY_1);
	key.set("box", Keyboard.KEY_2);
	key.set("pyramid", Keyboard.KEY_3);
   }
   
   protected boolean checkAdditionalKeys() {
   	
	if(isKeyDown("exit")) {
		return false;
	}
    
	if(isKeyDown("sphere")) {
		app.setRenderObject(1);
		return true;
	}
    
	if(isKeyDown("box")) {
		app.setRenderObject(2);
		return true;
	}
    
	if(isKeyDown("pyramid")) {
		app.setRenderObject(3);
		return true;
	}
    
	return true;
   }
}

