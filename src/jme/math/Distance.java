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
package jme.math;


/**
 * <code>Distance</code> is a static class that provides commonly used math
 * functions.
 * @author Mark Powell
 *
 */
public class Distance {
	
	/**
	 * <code>distance</code> calculates the distance between two points. These
	 * points are described as a <code>Vector</code> object.
	 * @param point1 the first point.
	 * @param point2 the second point.
	 * @return the distance between point1 and point2.
	 */
	public static double distance(Vector point1, Vector point2) {
		return Math.sqrt(((point1.x - point2.x) * (point1.x - point2.x)) +
						 ((point1.y - point2.y) * (point1.y - point2.y)) +
						 ((point1.z - point2.z) * (point1.z - point2.z)));
	}
	
	/**
	 * <code>distanceSquared</code> calculates the distance squared
	 * between two points. These points are described as a 
	 * <code>Vector</code> object.
	 * @param point1 the first point.
	 * @param point2 the second point.
	 * @return the distance squared between point1 and point2.
	 */
	public static float distanceSquared(Vector point1, Vector point2) {
		return ((point1.x - point2.x) * (point1.x - point2.x)) +
		((point1.y - point2.y) * (point1.y - point2.y)) +
		((point1.z - point2.z) * (point1.z - point2.z));
	}
	
	/**
	 * <code>distanceSquared</code> calculates the distance squared
	 * between a point and a line. 
	 * @param point the point to check.
	 * @param line the line to check.
	 * @return the distance squared between a point and line.
	 */
	public static float distanceSquared(Vector point, Line line) {
		Vector diff = point.subtract(line.getOrigin());
		float squareLen = line.getDirection().lengthSquared();
		float t = diff.dot(line.getDirection())/squareLen;
		diff = diff.subtract(line.getDirection().mult(t));

		return diff.lengthSquared();
	}
}
