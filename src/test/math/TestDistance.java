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
package test.math;

import jme.math.Distance;
import jme.math.Line;
import jme.math.Rectangle;
import jme.math.Vector;

/**
 * 
 * Test for all Distance methods.
 * @author Mark Powell
 *
 */
public class TestDistance {
    
    public static void testPointPoint() {
        System.out.println("TESTING POINT TO POINT:");
        Vector pt1 = new Vector();
        Vector pt2 = new Vector();
        float dis = Distance.distancePointPoint(pt1,pt2);
        System.out.println("Distance should be 0: " + dis);
        
        pt1.x = 10.0f;
        
        dis = Distance.distancePointPoint(pt1, pt2);
        System.out.println("Distance should be 10.0: " + dis);
        
        pt1.y = 10.0f;
        dis = Distance.distancePointPoint(pt1, pt2);
        System.out.println("Distance should be about 14.1: " + dis);
        
        pt1.z = 10.0f;
        dis = Distance.distancePointPoint(pt1, pt2);
        System.out.println("Distance should be about 17.5: " + dis);
        
        pt2.x = 4.0f;
        dis = Distance.distancePointPoint(pt1, pt2);
        System.out.println("Distance should be about 15.4: " + dis);
    }
    
    public static void testLineLine() {
        System.out.println("TESTING LINE TO LINE:");
        Line line1 = new Line();
        Line line2 = new Line();
        
        line1.setOrigin(new Vector(1,2,2));
        line1.setDirection(new Vector(4,3,2));
        line2.setOrigin(new Vector(1,0,-3));
        line2.setDirection(new Vector(4,-6,-1));
        float[] s = new float[1];
        float[] t = new float[1];
        float dis = Distance.distanceLineLineSquared(line1, line2, s, t);
        System.out.println("s: " + s[0] + " t: " + t[0]);
        System.out.println("Distance squared should be about 16: " + dis);
        System.out.println("Distance should be about 4: " + Math.sqrt(dis));
    }
    
    public static void testPointLine() {
        System.out.println("TESTING POINT TO LINE:");
        Vector pt = new Vector(2,-1,2);
        Line line = new Line();
        line.setOrigin(new Vector(-1,0,7));
        line.setDirection(new Vector(4,1,-2));
        
        float[] s = new float[1];
        float dis = Distance.distancePointLineSquared(pt, line,s);
        System.out.println("s: " + s[0]);
        System.out.println("Distance squared should be about 14 " + dis);
        System.out.println("Distance should be about 3.74 " + Math.sqrt(dis));
    }
    
    public static void testPointRay() {
        System.out.println("TESTING POINT TO RAY:");
        Vector pt = new Vector(0,0,0);
        Line line = new Line();
        line.setOrigin(new Vector(10,10,10));
        line.setDirection(new Vector(12, 18, 20));
        
        float[] s = new float[1];
        float dis = Distance.distancePointRaySquared(pt, line, s);
        System.out.println("s: " + s[0]);
        System.out.println("Distance squared should be about 300 " + dis);
        System.out.println("Distance should be about 17.3 " + Math.sqrt(dis));
        
        line.setOrigin(new Vector(-1,0,7));
        line.setDirection(new Vector(4,1,-2));
        pt.x = 2;
        pt.y = -1;
        pt.z = 2;

        s = new float[1];
        dis = Distance.distancePointRaySquared(pt, line,s);
        System.out.println("s: " + s[0]);
        System.out.println("Distance squared should be about 14 " + dis);
        System.out.println("Distance should be about 3.74 " + Math.sqrt(dis));
    }
    
    public static void testPointSegment() {
        System.out.println("TESTING POINT TO SEGMENT:");
        Vector pt = new Vector(0,0,0);
        Line line = new Line();
        line.setOrigin(new Vector(10,10,10));
        line.setDirection(new Vector(12, 18, 20));
        
        float[] s = new float[1];
        float dis = Distance.distancePointSegmentSquared(pt, line, s);
        System.out.println("s: " + s[0]);
        System.out.println("Distance squared should be about 300 " + dis);
        System.out.println("Distance should be about 17.3 " + Math.sqrt(dis));
        
        line.setOrigin(new Vector(-1,0,7));
        line.setDirection(new Vector(4,1,-2));
        pt.x = 2;
        pt.y = -1;
        pt.z = 2;

        dis = Distance.distancePointSegmentSquared(pt, line,s);
        System.out.println("s: " + s[0]);
        System.out.println("Distance squared should be about 14 " + dis);
        System.out.println("Distance should be about 3.74 " + Math.sqrt(dis));
        
        pt.x = 5;
        pt.y = 2;
        pt.z = -1;
        
        dis = Distance.distancePointSegmentSquared(pt, line,s);
        System.out.println("s: " + s[0]);
        System.out.println("Distance squared should be about 41 " + dis);
        System.out.println("Distance should be about 6.5 " + Math.sqrt(dis));
    }
    
    public static void testPointRectangle() {
        System.out.println("TESTING POINT TO RECTANGLE:");
        Vector pt = new Vector(5,10,5);
        Rectangle rect = new Rectangle();
        rect.setOrigin(new Vector());
        rect.setFirstEdge(new Vector(10,0,0));
        rect.setSecondEdge(new Vector(0,0,10));
        float dis = Distance.distancePointRectangle(pt, rect);
        System.out.println("Distance should be about 10 " + dis);
    }
    
    public static void testLineRectangle() {
        System.out.println("TESTING LINE TO RECTANGLE:");
        Line line = new Line();
        Rectangle rect = new Rectangle();
        rect.setOrigin(new Vector());
        rect.setFirstEdge(new Vector(10,0,0));
        rect.setSecondEdge(new Vector(0,0,10));
        line.setOrigin(new Vector(0,5,0));
        line.setDirection(new Vector(10, 15, 5));
        
        float dis = Distance.distanceLineRectangle(line, rect);
        System.out.println("Distance should be about 3 " + dis);
    }
    
    public static void testRayRectangle() {
        System.out.println("TESTING RAY TO RECTANGLE:");
        Line line = new Line();
        Rectangle rect = new Rectangle();
        rect.setOrigin(new Vector());
        rect.setFirstEdge(new Vector(10,0,0));
        rect.setSecondEdge(new Vector(0,0,10));
        line.setOrigin(new Vector(0,5,0));
        line.setDirection(new Vector(10, 15, 5));

        float dis = Distance.distanceRayRectangle(line, rect);
        System.out.println("Distance should be about 5 " + dis);
    }
    
    public static void testSegmentRectangle() {
        System.out.println("TESTING SEGMENT TO RECTANGLE:");
        Line line = new Line();
        Rectangle rect = new Rectangle();
        rect.setOrigin(new Vector());
        rect.setFirstEdge(new Vector(10,0,0));
        rect.setSecondEdge(new Vector(0,0,10));
        line.setOrigin(new Vector(0,5,0));
        line.setDirection(new Vector(10, 15, 5));

        float dis = Distance.distanceSegmentRectangle(line, rect);
        System.out.println("Distance should be about 5 " + dis);
    }
    
    public static void testLineRay() {
        System.out.println("TESTING LINE TO RAY:");
        Line line = new Line();
        Line ray = new Line();
        line.setOrigin(new Vector());
        line.setDirection(new Vector(10,10,0));
        ray.setOrigin(new Vector(0,15,0));
        ray.setDirection(new Vector(0,15,5));
        float dis = Distance.distanceLineRay(line, ray);
        System.out.println("Distance should be about 10 " + dis);
    }
    
    public static void testLineSegment() {
        System.out.println("TESTING LINE TO SEGMENT:");
        Line line = new Line();
        Line seg = new Line();
        line.setOrigin(new Vector());
        line.setDirection(new Vector(10,10,0));
        seg.setOrigin(new Vector(10,15,0));
        seg.setDirection(new Vector(0,15,5));
        float dis = Distance.distanceLineSegment(line, seg);
        System.out.println("Distance should be about 3.5 " + dis);
    }
    
    public static void testRectangleRectangle() {
        System.out.println("TESTING RECTANGLE TO RECTANGLE:");
        Rectangle rect1 = new Rectangle();
        Rectangle rect2 = new Rectangle();
        rect1.setOrigin(new Vector());
        rect1.setFirstEdge(new Vector(10, 0, 0));
        rect1.setSecondEdge(new Vector(0,10,0));
        rect2.setOrigin(new Vector(0,0,15));
        rect2.setFirstEdge(new Vector(5, 3, 15));
        rect2.setSecondEdge(new Vector(-5, -3, 15));
        float dis = Distance.distanceRectangleRectangle(rect1, rect2);
        System.out.println("Distance should be about 15 " + dis);
    }
    
    public static void testRayRay() {
        System.out.println("TESTING RAY TO RAY:");
        Line line1 = new Line();
        Line line2 = new Line();

        line1.setOrigin(new Vector(1,2,2));
        line1.setDirection(new Vector(4,3,2));
        line2.setOrigin(new Vector(1,0,-3));
        line2.setDirection(new Vector(4,-6,-1));
        float[] s = new float[1];
        float[] t = new float[1];
        float dis = Distance.distanceRayRaySquared(line1, line2, s, t);
        System.out.println("s: " + s[0] + " t: " + t[0]);
        System.out.println("Distance squared should be about 29: " + dis);
        System.out.println("Distance should be about 5.4: " + Math.sqrt(dis));
    }
    
    public static void testSegmentSegment() {
        System.out.println("TESTING SEGMENT TO SEGMENT:");
        Line line1 = new Line();
        Line line2 = new Line();

        line1.setOrigin(new Vector(1,2,2));
        line1.setDirection(new Vector(4,3,2));
        line2.setOrigin(new Vector(1,0,-3));
        line2.setDirection(new Vector(4,-6,-1));
        float[] s = new float[1];
        float[] t = new float[1];
        float dis = Distance.distanceSegmentSegmentSquared(line1, line2, s, t);
        System.out.println("s: " + s[0] + " t: " + t[0]);
        System.out.println("Distance squared should be about 29: " + dis);
        System.out.println("Distance should be about 5.4: " + Math.sqrt(dis));
    }
    
    public static void testRaySegment() {
        System.out.println("TESTING RAY TO SEGMENT:");
        Line line1 = new Line();
        Line line2 = new Line();

        line1.setOrigin(new Vector(1,2,2));
        line1.setDirection(new Vector(4,3,2));
        line2.setOrigin(new Vector(1,0,-3));
        line2.setDirection(new Vector(4,-6,-1));
        float[] s = new float[1];
        float[] t = new float[1];
        float dis = Distance.distanceRaySegmentSquared(line1, line2, s, t);
        System.out.println("s: " + s[0] + " t: " + t[0]);
        System.out.println("Distance squared should be about 29: " + dis);
        System.out.println("Distance should be about 5.4: " + Math.sqrt(dis));
    }
    
    public static void main(String[] args) {
        testPointPoint();
        testLineLine();
        testPointLine();
        testPointRay();
        testPointSegment();
        testPointRectangle();
        testLineRectangle();
        testRayRectangle();
        testSegmentRectangle();
        testLineRay();
        testLineSegment();
        testRectangleRectangle();
        testRayRay();
        testSegmentSegment();
        testRaySegment();
    }
}
