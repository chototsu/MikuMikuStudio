/*
 * Created on Apr 6, 2004
 */
package com.jme.effects.transients;
import com.jme.scene.Node;
/**
 * @author Ahmed
 */
public class Transient extends Node {
	private int maxNumOfStages, currentStage;
	public Transient(String name) {
		super(name);
	}
	public int getMaxNumOfStages() {
		return maxNumOfStages;
	}
	public void setMaxNumOfStages(int s) {
		maxNumOfStages = s;
	}
	public int getCurrentStage() {
		return currentStage;
	}
	public void setCurrentStage(int s) {
		if (s < getMaxNumOfStages()) {
			currentStage = s;
		} else {
			currentStage = getMaxNumOfStages();
		}
	}
}