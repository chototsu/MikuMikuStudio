/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding All rights reserved.
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
package com.jme.effects;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.BoundingSphere;
import com.jme.scene.TriMesh;

/*
 * NOTE: 2/24/2004 - Particle now handles own initialization. -MP
 *       2/27/2004 - Documentation - MP
 */

/**
 * <code>Particle</code> defines a single particle of a particle system. This
 * particle is a subclass of TriMesh and is therefore rendered directly.
 * The particle is billboarded insuring that it is always facing the camera,
 * giving it a three dimesional look while in reality being two dimensional.
 * <br>
 * The Particle maintains some properties which are altered by the
 * ParticleController that is maintaining it. The fade value determines how
 * much life is removed per update, while life stores how much life is left.
 * Size gives a scalar value to multiply the particle by. Color maintains the
 * particles color tint. Position of the particle is defined by the super
 * classes Local Translation vector. The position is updated by the velocity
 * of the particle and the gravity of the particle system.
 *
 * @author Ahmed
 * @version $Id: Particle.java,v 1.7 2004-03-02 03:56:49 renanse Exp $
 */
public class Particle extends TriMesh {

    /**
     * defines how fast the particle "dies".
     */
    public float fade;
    /**
     * keeps track of the particle's current life.
     */
    public float life;
    /**
     * defines the scalar for the particle's size.
     */
    public float size;
    /**
     * defines how much to update the particle's local translation.
     */
    public Vector3f velocity;
    /**
     * defines the color tint of the particle.
     */
    public ColorRGBA color;

    //holds the colors for the particle corners.
    private ColorRGBA[] cornerColors;

    /**
     * Constructor instantiates a default particle with no defined
     * attributes.
     * @param name the name of the particle.
     */
    public Particle(String name) {
        super(name);
    }

    /**
     * Constructor instantiates a new <code>Particle</code> object with
     * provided attributes. The particle is then ready for rendering.
     * @param name the name of the particle.
     * @param vertices the vertices that make up the particle quad.
     * @param normal the normal of the particle.
     * @param color the color of the particle.
     * @param texture the texture coordinates of the particle.
     * @param indices the indices of the quad.
     */
    public Particle(
        String name,
        Vector3f[] vertices,
        Vector3f[] normal,
        ColorRGBA[] color,
        Vector2f[] texture,
        int[] indices) {
        super(name, vertices, normal, color, texture, indices);

        cornerColors = new ColorRGBA[color.length];
        size = life = 0.0f;
        fade = 1.0f;
        velocity = new Vector3f(0, 0, 0);
        this.color = new ColorRGBA(0, 0, 0, 0);

        BoundingSphere sphere = new BoundingSphere();
        sphere.setCenter(getLocalTranslation());
        sphere.setRadius(1.0f);
        setModelBound(sphere);
        updateModelBound();
    }

    /**
     *
     * <code>setAverageSize</code> sets the average size of the particle. This
     * is used for the bounding volume and is used a good estimate of the
     * boundings for the life time of the particle.
     * @param size the size of the particle.
     */
    public void setAverageSize(float size) {
        ((BoundingSphere)getModelBound()).setRadius(size);
    }

    /**
     *
     * <code>updateColor</code> updates the colors of the quad based on
     * the current color of the particle.
     *
     */
    public void updateColor() {
        for (int i = 0; i < cornerColors.length; i++) {
            cornerColors[i] = this.color;
        }
        super.setColors(cornerColors);
    }

}
