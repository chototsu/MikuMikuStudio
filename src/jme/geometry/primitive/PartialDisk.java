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

import jme.geometry.bounding.BoundingBox;
import jme.geometry.bounding.BoundingSphere;
import jme.math.Vector;


/**
 * <code>PartialDisk</code> defines a sliver of a disk geometry. The partial 
 * disk is defined by two radii. That of the inner circle and that of the outer 
 * circle. The inner radius can be zero creating a solid disk. Any value greater
 * than zero for the inner radius will result in a hole of the given radius. 
 * The number of slices determine the number of subdivisions around the z axis. 
 * While the loops determine the number of concentric rings around the center. 
 * The angles define what the "slice" of the disk will consist of. Where 0 
 * degrees is along the positive y-axis, 90 degrees is along the positive 
 * x-axis, 180 degrees is along the negative y-axis, and 270 degrees is along 
 * the negative x-axis
 * 
 * @author Mark Powell
 * @version $Id: PartialDisk.java,v 1.3 2003-09-03 16:20:51 mojomonkey Exp $
 */
public class PartialDisk extends Disk {
    
    //attributes of the PartialDisk
    private double startAngle;
    private double endAngle;
    
    /**
     * Constructor builds a new <code>PartialDisk</code> using the passed 
     * parameters. The innerRadius, outerRadius, slices and loops are 
     * maintained by the super class <code>Disk</code>. <code>PartialDisk</code>
     * maintains the angle values.
     * 
     * @param innerRadius the inner radius of the disk.
     * @param outerRadius the outer radius of the disk.
     * @param slices the number of slices for the disk.
     * @param loops the number of loops for the disk.
     * @param startAngle the beginning angle of the "slice".
     * @param endAngle the last angle of the "slice".
     */
    public PartialDisk(double innerRadius, double outerRadius, int slices,
            int loops, double startAngle, double endAngle) {
        
        super(innerRadius, outerRadius, slices, loops);
        
        this.startAngle = startAngle;
        this.endAngle = endAngle;
        
        //set up bounding volumes
        boundingBox = new BoundingBox(new Vector(), new Vector(-(float)outerRadius,-(float)outerRadius,-(float)outerRadius),
        	new Vector((float)outerRadius,(float)outerRadius,(float)outerRadius));
        boundingSphere = new BoundingSphere((float)outerRadius, null);
    }
     
    /**
     * <code>render</code> handles rendering the sphere to the view context.
     */      
    public void render() {
        super.preRender();
        
//        glu.partialDisk(quadricPointer, innerRadius, outerRadius, slices,
//                loops, startAngle, endAngle);
                
        super.clean();
    }
    
    /**
     * <code>setStartAngle</code> sets the initial angle for the slice.
     * 
     * @param startAngle the new start angle of this partial disk.
     */
    public void setStartAngle(double startAngle) {
        this.startAngle = startAngle;
    }
    
    /**
     * <code>setEndAngle</code> sets the ending angle for the slice.
     * 
     * @param endAngle the new end angle of this partial disk.
     */
    public void setEndAngle(double endAngle) {
        this.endAngle = endAngle;
    }
}
