/*
 * Created on 18 oct. 2003
 *
 */
package com.jme.sound;

import java.util.Hashtable;



/**
 * @author Arman Ozcelik
 *
 */
public class SoundBindingManager {
	
	private static SoundBindingManager instance;
	private Hashtable buffs=new Hashtable();
	private Hashtable sources=new Hashtable();
	
	private SoundBindingManager(){
		
	}
	/**
	 * 
	 */
	public synchronized static SoundBindingManager getInstance(){
		if(instance==null){
			instance=new SoundBindingManager();
		}
		return instance;		
	}
	
	public void bind(String name, Integer bufferNumber, Integer source){
		buffs.put(name, bufferNumber);
		sources.put(name, source);
	}
	
	public int getByName(String name){
		return ((Integer)buffs.get(name)).intValue();
	}
	
	public int getSourceByName(String name){
		return ((Integer)sources.get(name)).intValue();
	}

}
