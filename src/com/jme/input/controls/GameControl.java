/*
 * Copyright (c) 2003-2006 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme.input.controls;

import java.io.*;
import java.util.*;

import com.jme.system.*;

/**
 * @author Matthew D. Hicks
 */
public class GameControl implements Serializable {
	private static final long serialVersionUID = 6266549836236136920L;

	private String name;
    private List<Binding> bindings;

    public GameControl(String name) {
        this(name, null);
    }

    public GameControl(String name, Binding binding) {
        this.name = name;
        bindings = new LinkedList<Binding>();
        addBinding(binding);
    }

    public List<Binding> getBindings() {
        return bindings;
    }
    
    public void clearBindings() {
    	bindings.clear();
    }
    
    public void addBinding(Binding binding) {
    	if (binding == null) return;
    	bindings.add(binding);
    }
    
    public void removeBinding(Binding binding) {
    	for (Binding b : bindings) {
    		if (b.toString().equals(binding.toString())) {
    			bindings.remove(b);
    			return;
    		}
    	}
    }
    
    public void replace(Binding oldBinding, Binding newBinding) {
    	if (oldBinding != null) {
    		removeBinding(oldBinding);
    	}
    	addBinding(newBinding);
    }
    
    public boolean containsBinding(Binding binding) {
    	for (Binding b : bindings) {
    		if (b.toString().equals(binding.toString())) {
    			return true;
    		}
    	}
    	return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public float getValue() {
    	float value = 0.0f;
    	for (Binding binding : bindings) {
    		if (binding.getValue() > value) {
    			value = binding.getValue();
    		}
    	}
    	return value;
    }

    public static final void save(List<GameControl> controls, GameSettings settings) {
    	settings.setObject("GameControls", controls);
    }
    
    @SuppressWarnings("unchecked")
	public static final List<GameControl> load(GameSettings settings) {
    	return (List<GameControl>)settings.getObject("GameControls", null);
    }

    public static final void replaceBindings(List<GameControl> originals, List<GameControl> replacements) {
		for (GameControl replacement : replacements) {
			for (GameControl original : originals) {
				if (original.getName().equals(replacement.getName())) {
					original.clearBindings();
					for (Binding binding : replacement.getBindings()) {
						original.addBinding(binding);
					}
				}
			}
		}
	}
    
    public static final void clearBindings(List<GameControl> controls) {
		for (GameControl original : controls) {
			original.clearBindings();
		}
    }
}