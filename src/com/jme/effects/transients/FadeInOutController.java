/*
 * Created on Apr 6, 2004
 */
package com.jme.effects.transients;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;

/**
 * @author Ahmed
 */
public class FadeInOutController extends Controller {
	
	private FadeInOut fio;
	private ColorRGBA color;
	
	public FadeInOutController(FadeInOut f) {
		fio = f;
		color = (ColorRGBA)fio.getFadeColor().clone();
	}

	public void update(float timeF) {
		System.out.println("Alpha: " + fio.getFadeColor().a);
		float time = timeF * fio.getSpeed();
		color = fio.getFadeColor();
		if (fio.getCurrentStage() == 0) {
			color.a -= time;
			fio.setFadeColor(color);
			if (fio.getFadeColor().a <= 0.0f) {
				fio.detachChild(fio.getOutQuad());
				fio.attachChild(fio.getInQuad());
				fio.setCurrentStage(fio.getCurrentStage() + 1);
			}
		}else if (fio.getCurrentStage() == 1) {
			color.a += time;
			fio.setFadeColor(color);
			if (fio.getFadeColor().a >= 1.0f) {
				fio.setCurrentStage(fio.getCurrentStage() + 1);
			}
		}
	}
}
