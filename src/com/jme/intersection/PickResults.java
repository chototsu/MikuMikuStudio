/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding
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

import java.util.ArrayList;

import com.jme.scene.Geometry;

/**
 * <code>PickResults</code> contains information resulting from a pick test.
 * The results will contain a list of every node that was "struck" during a
 * pick test.
 * @author Mark Powell
 * @version $Id: PickResults.java,v 1.3 2004-04-22 22:26:37 renanse Exp $
 */
public class PickResults {

    private ArrayList nodeList;

    /**
     * Constructor instantiates a new <code>PickResults</code> object.
     *
     */
    public PickResults() {
        nodeList = new ArrayList();
    }

    /**
     *
     * <code>addNode</code> places a new <code>Geometry</code> node into the
     * results list.
     * @param node the node to be placed in the results list.
     */
    public void addNode(Geometry node) {
        nodeList.add(node);
    }

    /**
     *
     * <code>getNumber</code> retrieves the number of nodes that have been
     * placed in the results.
     * @return the number of nodes in the list.
     */
    public int getNumber() {
        return nodeList.size();
    }

    /**
     *
     * <code>getNode</code> retrieves a node from a specific index.
     * @param i the index requested.
     * @return the node at the specified index.
     */
    public Geometry getNode(int i) {
        return (Geometry)nodeList.get(i);
    }

    /**
     *
     * <code>clear</code> clears the list of all nodes.
     *
     */
    public void clear() {
        nodeList.clear();
    }
}
