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

package jme.geometry.primitive;

import java.util.logging.Level;

import jme.exception.MonkeyRuntimeException;
import jme.math.Vector;
import jme.utility.LoggingSystem;


/**
 * <code>Disk</code> defines a disk geometry. The disk is defined by two radii.
 * That of the inner circle and that of the outer circle. The inner radius can
 * be zero creating a solid disk. Any value greater than zero for the inner
 * radius will result in a hole of the given radius. The number of slices 
 * determine the number of subdivisions around the z axis. While the loops
 * determine the number of concentric rings around the center.
 * 
 * @author Mark Powell
 * @version $Id: Disk.java,v 1.4 2003-09-08 20:29:27 mojomonkey Exp $
 */
public class Disk extends Quadric {
    
    //Attributes that define the Disk.
    protected double innerRadius;
    protected double outerRadius;
    protected int slices;
    protected int loops;
    
    
    /**
     * Constructor creates a new Disk geometry object. The disk is defined by
     * the given parameters, innerRadius, outerRadius, slices and loops. The 
     * inner radius can be zero creating a solid disk. Any value greater than 
     * zero for the inner radius will result in a hole of the given radius. The 
     * number of slices determine the number of subdivisions around the z axis. 
     * While the loops determine the number of concentric rings around the 
     * center.
     * 
     * @param innerRadius the inner radius of the disk.
     * @param outerRadius the outer raidus of the disk.
     * @param slices the subdivisions of the disk.
     * @param loops the concentric rings aroudn the disk.
     * 
     * @throws MonkeyRuntimeException if any parameter is negative and all but
     *      innerRadius is zero.
     */
    public Disk(double innerRadius, double outerRadius, int slices, 
            int loops) {
                
        if(innerRadius < 0 || outerRadius <= 0 || slices <= 0 || loops <= 0) {
            throw new MonkeyRuntimeException("No disk value may be less than" +
                    " zero, and only innerRadius may be zero");
        }
        
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        this.slices = slices;
        this.loops = loops;
        
        super.initialize();
        
        LoggingSystem.getLoggingSystem().getLogger().log(Level.INFO,
                "Disk created.");
    }

    /**
     * <code>render</code> handles rendering the sphere to the view context.
     */
    public void render() {
        super.preRender();
        //glu.disk(quadricPointer, innerRadius, outerRadius, slices, loops);
        super.clean();
    }
    
    /**
     * <code>setInnerRadius</code> sets the inner radius of the disk.
     * 
     * @param innerRadius the new inner radius.
     * 
     * @throws MonkeyRuntimeException if innerRadius is less than zero.
     */
    public void setInnerRadius(double innerRadius) {
        if(innerRadius < 0) {
            throw new MonkeyRuntimeException("Inner Radius must be zero or " + 
                    "greater");
        }
        
        this.innerRadius = innerRadius;
    }
    
    /**
     * <code>setOuterRadius</code> sets the outer radius of the disk.
     * 
     * @param outerRadius the new outer radius.
     * 
     * @throws MonkeyRuntimeException if outerRadius is less than or equal
     *      to zero.
     */
    public void setOuterRadius(double outerRadius) {
        if(outerRadius <= 0) {
            throw new MonkeyRuntimeException("Outer Radius must be greater " +
                    "than zero");
        }
        
        this.outerRadius = outerRadius;
    }
    
    /**
     * <code>setSlices</code> sets the slices of the disk.
     * 
     * @param slices the new slices value.
     * 
     * @throws MonkeyRuntimeException if slices is less than or equal to zero.
     */
    public void setSlices(int slices) {
        if(slices <= 0) {
            throw new MonkeyRuntimeException("Slices must be greater " +
                    "than zero");
        }
        
        this.slices = slices;
    }
    
    /**
     * <code>setLoops</code> sets the number of loops for the disk.
     * 
     * @param loops the new loops value.
     * 
     * @throws MonkeyRuntimeException if loops are less than or equal to zero.
     */
    public void setLoops(int loops) {
        if(loops <= 0) {
            throw new MonkeyRuntimeException("Loops must be greater " +
                    "than zero");
        }
        
        this.loops = loops;
    }
    
    public Vector[] getPoints() {
        return null;
    }

}
