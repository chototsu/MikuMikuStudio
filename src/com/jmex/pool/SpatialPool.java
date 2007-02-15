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
package com.jmex.pool;

import java.util.*;

/**
 * <code>SpatialPool</code> allows you re-use Spatials and provides features for 
 * 
 * @author Matthew D. Hicks
 */
public class SpatialPool<T> {
	public static enum Mode {
		/**
		 * Growable will increase the size of the pool to accomodate as needed
		 */
		GROWABLE,
		/**
		 * Blocking will wait until there is an available object before returning a get request.
		 */
		BLOCKING,
		/**
		 * Will immediately return null for a get request if none are available.
		 */
		NON_BLOCKING,
		/**
		 * Error will throw an exception if there is not an object available for a get request.
		 */
		ERROR
	}
	
	private ArrayList<T> spatials;
	private ArrayList<Boolean> inUse;
	private SpatialGenerator<T> generator;
	private int size;
	private Mode mode;
	
	public SpatialPool(SpatialGenerator<T> generator, int size, Mode mode, boolean preAllocate) throws Exception {
		spatials = new ArrayList<T>(size);
		inUse = new ArrayList<Boolean>(size);
		this.generator = generator;
		this.size = size;
		this.mode = mode;
		if (preAllocate) {
			for (int i = 0; i < size; i++) {
				spatials.add(generator.newInstance());
			}
		}
	}
	
	public T get() throws MaxPoolSizeException {
		for (int i = 0; i < spatials.size(); i++) {
			if (!isInUse(i)) {
				inUse.set(i, true);
				return spatials.get(i);
			}
		}
		if (spatials.size() < size) {	// We haven't reached the max size yet, so we simply add one
			synchronized(spatials) {
				if (spatials.size() < size) {	// Second check since we're now synchronized
					try {
						T t = generator.newInstance();
						spatials.add(t);
						inUse.add(true);
						return t;
					} catch(Exception exc) {
						throw new RuntimeException(exc);
					}
				}
			}
		}
		if (mode == Mode.BLOCKING) {
			// TODO wait
		} else if (mode == Mode.ERROR) {
			throw new MaxPoolSizeException("SpatialPool size is at maximum capacity (" + spatials.size() + ").");
		} else if (mode == Mode.GROWABLE) {
			try {
				T t = generator.newInstance();
				spatials.add(t);
				inUse.add(true);
				return t;
			} catch(Exception exc) {
				throw new RuntimeException(exc);
			}
		} else if (mode == Mode.NON_BLOCKING) {
			return null;
		}
		throw new RuntimeException("Invalid setting for Mode: " + mode);
	}
	
	private boolean isInUse(int index) {
		while (inUse.size() < index + 1) {
			inUse.add(false);
		}
		return inUse.get(index);
	}
	
	public boolean release(T t) {
		for (int i = 0; i < spatials.size(); i++) {
			if (t == spatials.get(i)) {
				inUse.set(i, false);
				return true;
			}
		}
		return false;
	}
	
	public int size() {
		return spatials.size();
	}
	
	public int inUse() {
		int used = 0;
		for (boolean b : inUse) {
			if (b) used++;
		}
		return used;
	}
}
