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
package com.jme.intersection;

import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;

/**
 * <code>CollisionDetection</code> provides a system for calculating collisions
 * based on given criteria. 
 * @author Mark Powell
 * @version $Id: CollisionDetection.java,v 1.2 2003-12-09 20:38:27 mojomonkey Exp $
 */
public class CollisionDetection {

    /**
     * 
     * <code>hasCollision</code> determines if a static test Spatial is 
     * colliding with any scene objects. 
     * @param test the node to test for collisions.
     * @param scene the world to test the node against.
     * @param results the list of collisions.
     */
    public static void hasCollision(
        Spatial test,
        Spatial scene,
        CollisionResults results) {
            
        if(test.equals(scene)) {
            return;
        }
            
        if (Intersection
            .intersection(test.getWorldBound(), scene.getWorldBound())) {
            if ((scene instanceof Node)) {
                Node parent = (Node) scene;
                for (int i = 0; i < parent.getQuantity(); i++) {
                    hasCollision(test, parent.getChild(i), results);
                }
            } else {
                //find the triangle that is being hit.
                //add this node and the triangle to the PickResults list.
                results.addNode((Geometry) scene);
            }
        }
    }
}
