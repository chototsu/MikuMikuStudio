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
package jmetest.intersection;

import com.jme.input.Mouse;
import com.jme.input.action.MouseInputAction;
import com.jme.intersection.Pick;
import com.jme.intersection.PickResults;
import com.jme.math.Ray;
import com.jme.renderer.Camera;
import com.jme.scene.Node;
import com.jme.scene.Text;

/**
 * <code>MousePick</code>
 * @author Mark Powell
 * @version
 */
public class MousePick implements MouseInputAction {

    private Mouse mouse;
    private Camera camera;
    private Node scene;
    private float shotTime = 0;
    private int hits = 0;
    private int shots = 0;
    private Text text;
    private String hitItems;

    public MousePick(Camera camera, Node scene, Text text) {
        this.camera = camera;
        this.scene = scene;
        this.text = text;
    }
    /* (non-Javadoc)
     * @see com.jme.input.action.MouseInputAction#performAction(float)
     */
    public void performAction(float time) {
        shotTime += time;
        if(mouse.getMouseInput().isButtonDown(0) && shotTime > 0.1f) {
            shotTime = 0;
            Ray ray = new Ray(camera.getLocation(), camera.getDirection());
            PickResults results = new PickResults();

            Pick.doPick(scene,ray,results);


            hits += results.getNumber();
            hitItems = "";
            if(results.getNumber() > 0) {
                for(int i = 0; i < results.getNumber(); i++) {
                    hitItems += results.getGeometry(i).getName();
                    if(i != results.getNumber() -1) {
                        hitItems += ", ";
                    }
                }
            }
            shots++;
            results.clear();
            text.print("Hits: " + hits + " Shots: " + shots + " : " + hitItems);
        }
    }

    /* (non-Javadoc)
     * @see com.jme.input.action.MouseInputAction#setSpeed(float)
     */
    public void setSpeed(float speed) {

    }

    /* (non-Javadoc)
     * @see com.jme.input.action.MouseInputAction#setMouse(com.jme.input.Mouse)
     */
    public void setMouse(Mouse mouse) {
        this.mouse = mouse;
    }

}
