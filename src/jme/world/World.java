/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this 
 * list of conditions and the following disclaimer. 
 * 
 * Redistributions in binary form must reproduce the above copyright notice, 
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution. 
 * 
 * Neither the name of the Mojo Monkey Coding, jME, jMonkey Engine, nor the 
 * names of its contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package jme.world;

import java.util.ArrayList;
import java.util.logging.Level;

import org.lwjgl.opengl.GL;

import jme.entity.EntityInterface;
import jme.exception.MonkeyRuntimeException;
import jme.locale.Locale;
import jme.locale.external.feature.Water;
import jme.entity.camera.Camera;
import jme.entity.effects.ParticleSystem;
import jme.system.DisplaySystem;
import jme.utility.LoggingSystem;

/**
 * <code>World</code> maintains the game world, by handling the locale and
 * list of entities. <code>World</code> takes care of updating all game
 * items and the locale. 
 * 
 * @author Mark Powell
 * @version 0.1.0
 */
public class World {

    private GL gl;
	//Entities and locale objects.
    private Locale locale;
    private ArrayList entityList;
    private Camera camera;
    
    private boolean checkEntityVisibility = false;
    
    private int numRenderedEntities = 0;
    private int totalEntities = 0;
	/**
	 * <code>water</code> the water representation.
	 */
	private Water water;

    /**
     * Constructor builds a default <code>World</code> object that does not
     * contain a locale nor any entities.
     *
     */
    public World() {
        entityList = new ArrayList();
        gl = DisplaySystem.getDisplaySystem().getGL();
        LoggingSystem.getLoggingSystem().getLogger().log(Level.INFO,
                "Created World");
    }
    
    /**
     * Constructor builds a <code>World</code> object with an initial
     * <code>Locale</code>.
     * 
     * @param locale the game locale.
     * 
     * @throws MonkeyRuntimeException if locale is null.
     */
    public World(Locale locale) {
        if(null == locale) {
            throw new MonkeyRuntimeException("Locale cannot be null");
        }
        this.locale = locale;
        entityList = new ArrayList();
        
        LoggingSystem.getLoggingSystem().getLogger().log(Level.INFO,
                        "Created World");
    }
    
    /**
     * Constructor builds a <code>World</code> object with an initial
     * <code>Locale</code> and entity list.
     * @param locale the game locale.
     * @param entities the list of game entities.
     * 
     * @throws MonkeyRuntimeException if either locale or entities are null.
     */
    public World(Locale locale, ArrayList entities) {
        if(null == locale || null == entities) {
            throw new MonkeyRuntimeException("Neither locale nor entities may" +
                " be null");
        }
        this.locale = locale;
        this.entityList = entities;
        
        LoggingSystem.getLoggingSystem().getLogger().log(Level.INFO,
                        "Created World");
    }
    
    /**
     * <code>setLocale</code> sets the locale of this world.
     * @param locale the game locale.
     * 
     * @throws MonkeyRuntimeException if local is null.
     */
    public void setLocale(Locale locale) {
        if(null == locale) {
            throw new MonkeyRuntimeException("locale may not be null");
        }
        this.locale = locale;
    }
    
	/**
	 * <code>setWater</code> sets the water representation of the terrain. This
	 * takes an <code>Water</code> subclass. The client is reponsible
	 * for calling the water's render method during the terrain's render call.
	 */
	public void setWater(Water water) {
		this.water = water;
	}

    /**
     * <code>setCamera</code> sets the camera for the current world.
     * @param camera the new camera.
     */
    public void setCamera(Camera camera) {
        this.camera = camera;
    }
    
    public void setEntityVisibility(boolean value) {
        this.checkEntityVisibility = value;
    }
    
    /**
     * <code>addEntity</code> adds an entity to the entity list.
     * @param entity the entity to add to the list.
     * 
     * @throws MonkeyRuntimeException if enitity is null.
     */
    public void addEntity(EntityInterface entity){
        if(null == entity) {
            throw new MonkeyRuntimeException("Entity may not be null");
        }
        entityList.add(entity);
    }
    
    /**
	 * <code>getTotalEntities</code> returns the total number of
	 * entities within the world.
	 * @return the number of entities in the world.
	 */
	public int getTotalEntities() {
		return totalEntities;
	}
	
	/**
	 * <code>getNumRenderedEntities</code> returns the number of entities that
	 * were rendered this frame.
	 * @return the number of rendered entities this frame.
	 */
	public int getNumRenderedEntities() {
		return numRenderedEntities;
	}
    
    /**
     * <code>update</code> updates the locale and any <code>Entities</code>
     * that are within the entities list.
     *
     */
    public void update(float time) {
        if(null != locale) {
            locale.update(time);
        }
        
		  if(null != water) {
			  water.update(time);
		  }
        
        for(int i = 0; i < entityList.size(); i++){
            Object o = entityList.get(i);
            if(o instanceof EntityInterface) {
                if(null != camera && checkEntityVisibility) {
                    ((EntityInterface)o).checkVisibility(camera.getFrustum());
                }
                ((EntityInterface)o).update(time);
            }
        }
    }
    
    /**
     * <code>render</code> renders the entities in turn and then the locale.
     */
    public void render() {
    	numRenderedEntities = 0;
    	totalEntities = entityList.size();
    	//render terrain
		if(null != locale) {
			locale.render();
		}
		
        for(int i = 0; i < totalEntities; i++) {
		    Object o = entityList.get(i);
            if(o instanceof EntityInterface) {
                if(((EntityInterface)entityList.get(i)).isVisible() || !checkEntityVisibility) {
                    
                    if(null != locale) {
                    	if(o instanceof ParticleSystem && locale.useDistanceFog()) {
                    		gl.disable(GL.FOG);
                    	} else if(locale.useDistanceFog()) {
                    		gl.enable(GL.FOG);
                    	}
                    }
                    
                    numRenderedEntities++;
                    ((EntityInterface)entityList.get(i)).render();
                    
                    if(null != locale) {
                    	if(o instanceof ParticleSystem && locale.useDistanceFog()) {
							gl.enable(GL.FOG);
						} else if(locale.useDistanceFog()) {
							gl.disable(GL.FOG);
						}
                    }
                }
            }
        }
        //render water plane
		if(null != water) {
			water.render();
		}
        
    }
    
    /**
     * <code>toString</code> returns the string representation of this object
     * in the format:<br><br>
     * jme.world.World@11a64ed<br>
     * Locale: {LOCALE}<br>
     * Entities: {ENTITY}<br> 
     * @return the string representation of this object.
     */
    public String toString() {
        String string = super.toString();
        string += "\nLocale: " + locale.toString();
        string += "\nEntities: ";
        for(int i = 0; i < entityList.size(); i++){
            string += "\n" + i + " " + ((EntityInterface)entityList.get(i)).toString();
        }
        
        return string;
    }
}
