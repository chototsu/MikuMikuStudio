/*
 * Created on Apr 6, 2004
 */
package com.jme.effects.transients;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;

/**
 * @author Ahmed
 */
public class FadeInOut extends Transient {
	
	private Quad fadeQ, outQ, inQ;
	private ColorRGBA fadeColors[];
	private float speed;
	
	public FadeInOut(String name, TextureState out, TextureState in, ColorRGBA c) {
		super(name);
		initialise(out, in, c, 0.01f);
	}
	
	public FadeInOut(String name, TextureState out, TextureState in, ColorRGBA c, float s) {
		super(name);
		initialise(out, in, c, s);
	}
	
	private void initialise(TextureState out, TextureState in, ColorRGBA c, float speed) {
		setMaxNumOfStages(2);
		setCurrentStage(0);
		setSpeed(speed);
		
		fadeColors = new ColorRGBA[4];
		for (int i = 0; i < fadeColors.length; i++) {
			fadeColors[i] = new ColorRGBA();
			fadeColors[i].r = c.r;
			fadeColors[i].g = c.g;
			fadeColors[i].b = c.b;
			fadeColors[i].a = 1;
		}
		
		Vector3f origin = new Vector3f(0, 0, 0);
		
		AlphaState as = DisplaySystem.getDisplaySystem().getRenderer().getAlphaState();
		as.setBlendEnabled(true);
		as.setEnabled(true);
		
		fadeQ = new Quad(getName() + " Fade Quad");
		fadeQ.initialize(5, 5);
		fadeQ.setColors(fadeColors);
		fadeQ.setRenderState(as);
		fadeQ.setLocalTranslation(origin);
		
		outQ = new Quad(getName() + " Out Quad");
		outQ.initialize(5, 5);
		outQ.setRenderState(out);
		fadeQ.setLocalTranslation(origin);
		
		inQ = new Quad(getName() + " In Quad");
		inQ.initialize(5, 5);
		inQ.setRenderState(in);
		inQ.setLocalTranslation(origin);
		
		this.attachChild(outQ);
		this.attachChild(fadeQ);
	}
	
	public Quad getFadeQuad() {
		return fadeQ;
	}
	public void setFadeQuad(Quad f) {
		fadeQ = f;
	}
	
	public Quad getOutQuad() {
		return outQ;
	}
	public void setOutQuad(Quad o) {
		outQ = o;
	}
	
	public Quad getInQuad() {
		return inQ;
	}
	public void setInQuad(Quad i) {
		inQ = i;
	}
	
	public ColorRGBA getFadeColor() {
		return fadeColors[0];
	}
	
	public void setFadeColor(ColorRGBA c) {
		for (int i = 0; i < fadeColors.length; i++) {
			if (fadeColors[i] == null) {
				fadeColors[i] = new ColorRGBA();
			}
			fadeColors[i] = (ColorRGBA)c.clone();
		}
	}
	
	public float getSpeed() {
		return speed;
	}
	public void setSpeed(float s) {
		speed = s;
	}
	
	public void updateWorldData(float time) {
		if (getController(0) != null) {
			getController(0).update(time);
		}
	}
	
}
