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

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.bounding.OrientedBoundingBox;
import com.jme.math.*;
import com.jme.scene.TriMesh;

/**
 * <code>Intersection</code> provides functional methods for calculating the
 * intersection of some objects. All the methods are static to allow for quick
 * and easy calls. <code>Intersection</code> relays requests to specific classes
 * to handle the actual work. By providing checks to just <code>BoundingVolume</code>
 * the client application need not worry about what type of bounding volume is
 * being used.
 * @author Mark Powell
 * @version $Id: Intersection.java,v 1.17 2004-08-28 20:31:53 cep21 Exp $
 */
public class Intersection {

    private Intersection(){}
    /**
     * EPSILON represents the error buffer used to denote a hit.
     */
    public static final double EPSILON = 1e-12;

    /**
     *
     * <code>intersection</code> determines if a ray has intersected a given
     * bounding volume. This method actually delegates the work to another
     * method depending on what type of bounding volume has been passed.
     * @param ray the ray to test.
     * @param volume the bounding volume to test.
     * @return true if they intersect, false otherwise.
     */
    public static boolean intersection(Ray ray, BoundingVolume volume) {
        if (volume instanceof BoundingSphere) {
            return IntersectionSphere.intersection(ray, (BoundingSphere) volume);
        } else if (volume instanceof BoundingBox) {
            return IntersectionBox.intersection(ray, (BoundingBox) volume);
        } else if (volume instanceof OrientedBoundingBox) {
            return IntersectionOBB.intersection(ray, (OrientedBoundingBox) volume);
        }
        return false;
    }


    /**
     *
     * <code>intersection</code> compares two bounding volumes for intersection.
     * If any part of the volumes touch, true is returned, otherwise false is
     * returned.
     *
     * @param vol1 the first volume to check.
     * @param vol2 the second volume to check.
     * @return true if an intersection occurs, false otherwise.
     */
    public static boolean intersection(
        BoundingVolume vol1,
        BoundingVolume vol2) {
        if (vol1 instanceof BoundingSphere) {
            if (vol2 instanceof BoundingSphere) {
                return IntersectionSphere.intersection(
                    (BoundingSphere) vol1,
                    (BoundingSphere) vol2);
            } else {
                return false;
            }
        } else if (vol1 instanceof BoundingBox) {
            if (vol2 instanceof BoundingBox) {
                return IntersectionBox.intersection(
                    (BoundingBox) vol1,
                    (BoundingBox) vol2);
            } else {
                return false;
            }
        } else if (vol1 instanceof OrientedBoundingBox) {
            return IntersectionOBB.intersection(
                (OrientedBoundingBox) vol1,
                vol2);
        } else {
            return false;
        }
    }

    /**
     * This is a <b>VERY</b> brute force method of detecting if two TriMesh objects intersect.
     * @param a The first TriMesh.
     * @param b The second TriMesh.
     * @return True if they intersect, false otherwise.
     */
    public static boolean meshIntersection(TriMesh a,TriMesh b){

        int [] indexA=a.getIndices();
        int [] indexB=b.getIndices();
        TransformMatrix aTransform=new TransformMatrix();
        aTransform.setRotationQuaternion(a.getWorldRotation());
        aTransform.setTranslation(a.getWorldTranslation());
        aTransform.setScale(a.getWorldScale());

        TransformMatrix bTransform=new TransformMatrix();
        bTransform.setRotationQuaternion(b.getWorldRotation());
        bTransform.setTranslation(b.getWorldTranslation());
        bTransform.setScale(b.getWorldScale());

        Vector3f[] vertA=new Vector3f[a.getVertices().length];
        for (int i=0;i<vertA.length;i++){
            vertA[i]=aTransform.multPoint(new Vector3f(a.getVertices()[i]));
        }
        Vector3f[] vertB=new Vector3f[b.getVertices().length];
        for (int i=0;i<vertB.length;i++){
            vertB[i]=bTransform.multPoint(new Vector3f(b.getVertices()[i]));
        }

        for (int i=0;i<a.getTriangleQuantity();i++){
            for (int j=0;j<b.getTriangleQuantity();j++){
                if (intersection(
                        vertA[indexA[i*3+0]],
                        vertA[indexA[i*3+1]],
                        vertA[indexA[i*3+2]],
                        vertB[indexB[j*3+0]],
                        vertB[indexB[j*3+1]],
                        vertB[indexB[j*3+2]]))
                    return true;
            }
        }
        return false;
    }

    /**
     * This function test for the intersection between two triangles defined by their vertexes.  Converted
     * to java from C code found at http://www.acm.org/jgt/papers/Moller97/tritri.html
     * @param V0 First triangle's first vertex.
     * @param V1 First triangle's second vertex.
     * @param V2 First triangle's third vertex.
     * @param U0 Second triangle's first vertex.
     * @param U1 Second triangle's second vertex.
     * @param U2 Second triangle's third vertex.
     * @return True if the two triangles intersect, false otherwise.
     */
    public static boolean intersection(Vector3f V0,Vector3f V1,Vector3f V2 ,
                                       Vector3f U0,Vector3f U1,Vector3f U2){
//        float E1[3],E2[3];
        Vector3f E1=new Vector3f();
        Vector3f E2=new Vector3f();
//        float N1[3],N2[3],d1,d2;
        Vector3f N1=new Vector3f();
        Vector3f N2=new Vector3f();
        float d1,d2;
        float du0,du1,du2,dv0,dv1,dv2;
//        float D[3];
        Vector3f D=new Vector3f();
//        float isect1[2], isect2[2];
        float[] isect1=new float[2];
        float[] isect2=new float[2];
        float du0du1,du0du2,dv0dv1,dv0dv2;
        short index;
        float vp0,vp1,vp2;
        float up0,up1,up2;
        float bb,cc,max;
        float xx,yy,xxyy,tmp;

        /* compute plane equation of triangle(V0,V1,V2) */
//        SUB(E1,V1,V0);
        V1.subtract(V0,E1);

//        SUB(E2,V2,V0);
        V2.subtract(V0,E2);

//        CROSS(N1,E1,E2);
        E1.cross(E2,N1);

//        d1=-DOT(N1,V0);
        d1=-N1.dot(V0);
        /* plane equation 1: N1.X+d1=0 */

        /* put U0,U1,U2 into plane equation 1 to compute signed distances to the plane*/
//        du0=DOT(N1,U0)+d1;
        du0=N1.dot(U0)+d1;

//        du1=DOT(N1,U1)+d1;
        du1=N1.dot(U1)+d1;

//        du2=DOT(N1,U2)+d1;
        du2=N1.dot(U2)+d1;

        /* coplanarity robustness check */
//        #if USE_EPSILON_TEST==TRUE
        if(FastMath.abs(du0)<EPSILON) du0=0.0f;
        if(FastMath.abs(du1)<EPSILON) du1=0.0f;
        if(FastMath.abs(du2)<EPSILON) du2=0.0f;
//        #endif
        du0du1=du0*du1;
        du0du2=du0*du2;

        if(du0du1>0.0f && du0du2>0.0f) /* same sign on all of them + not equal 0 ? */
            return false;                    /* no intersection occurs */

        /* compute plane of triangle (U0,U1,U2) */
//        SUB(E1,U1,U0);
        U1.subtract(U0,E1);

//        SUB(E2,U2,U0);
        U2.subtract(U0,E2);

//        CROSS(N2,E1,E2);
        E1.cross(E2,N2);

//        d2=-DOT(N2,U0);
        d2=-N2.dot(U0);
        /* plane equation 2: N2.X+d2=0 */

        /* put V0,V1,V2 into plane equation 2 */
//        dv0=DOT(N2,V0)+d2;
        dv0=N2.dot(V0)+d2;

//        dv1=DOT(N2,V1)+d2;
        dv1=N2.dot(V1)+d2;

//        dv2=DOT(N2,V2)+d2;
        dv2=N2.dot(V2)+d2;

//        #if USE_EPSILON_TEST==TRUE
        if(FastMath.abs(dv0)<EPSILON) dv0=0.0f;
        if(FastMath.abs(dv1)<EPSILON) dv1=0.0f;
        if(FastMath.abs(dv2)<EPSILON) dv2=0.0f;
//        #endif

        dv0dv1=dv0*dv1;
        dv0dv2=dv0*dv2;

        if(dv0dv1>0.0f && dv0dv2>0.0f) /* same sign on all of them + not equal 0 ? */
            return false;                    /* no intersection occurs */

        /* compute direction of intersection line */
//        CROSS(D,N1,N2);
        N1.cross(N2,D);

        /* compute and index to the largest component of D */
        max=(float)FastMath.abs(D.x);
        index=0;
        bb=(float)FastMath.abs(D.y);
        cc=(float)FastMath.abs(D.z);
        if(bb>max){
            max=bb;
            index=1;
        }
        if(cc>max){
            max=cc;
//            index=2;
            vp0=V0.z;
            vp1=V1.z;
            vp2=V2.z;

            up0=U0.z;
            up1=U1.z;
            up2=U2.z;

        } else if (index==1){
            vp0=V0.y;
            vp1=V1.y;
            vp2=V2.y;

            up0=U0.y;
            up1=U1.y;
            up2=U2.y;
        } else{
            vp0=V0.x;
            vp1=V1.x;
            vp2=V2.x;

            up0=U0.x;
            up1=U1.x;
            up2=U2.x;
        }

        /* this is the simplified projection onto L*/
//        vp0=V0[index];
//        vp1=V1[index];
//        vp2=V2[index];

//        up0=U0[index];
//        up1=U1[index];
//        up2=U2[index];

        /* compute interval for triangle 1 */
        Vector3f abc=new Vector3f();
        Vector2f x0x1=new Vector2f();
        if (NEWCOMPUTE_INTERVALS(vp0,vp1,vp2,dv0,dv1,dv2,dv0dv1,dv0dv2,abc,x0x1))
            return coplanar_tri_tri(N1,V0,V1,V2,U0,U1,U2);

        /* compute interval for triangle 2 */
        Vector3f def=new Vector3f();
        Vector2f y0y1=new Vector2f();
        if (NEWCOMPUTE_INTERVALS(up0,up1,up2,du0,du1,du2,du0du1,du0du2,def,y0y1))
            return coplanar_tri_tri(N1,V0,V1,V2,U0,U1,U2);

        xx=x0x1.x*x0x1.y;
        yy=y0y1.x*y0y1.y;
        xxyy=xx*yy;

        tmp=abc.x*xxyy;
        isect1[0]=tmp+abc.y*x0x1.y*yy;
        isect1[1]=tmp+abc.z*x0x1.x*yy;

        tmp=def.x*xxyy;
        isect2[0]=tmp+def.y*xx*y0y1.y;
        isect2[1]=tmp+def.z*xx*y0y1.x;

        SORT(isect1);
        SORT(isect2);

        if(isect1[1]<isect2[0] || isect2[1]<isect1[0]) return false;
        return true;
    }

    private static void SORT(float[] f) {
        if (f[0] > f[1]){
            float c=f[0];
            f[0]=f[1];
            f[1]=c;
        }
    }

//#define NEWCOMPUTE_INTERVALS(VV0,VV1,VV2,D0,D1,D2,D0D1,D0D2,A,B,C,X0,X1)
    private static boolean NEWCOMPUTE_INTERVALS(float VV0, float VV1, float VV2,
                                             float D0, float D1, float D2, 
                                             float D0D1, float D0D2, 
                                             Vector3f ABC, Vector2f X0X1) {
        if(D0D1>0.0f){
                /* here we know that D0D2<=0.0 */
            /* that is D0, D1 are on the same side, D2 on the other or on the plane */
                ABC.x=VV2; ABC.y=(VV0-VV2)*D2; ABC.z=(VV1-VV2)*D2; X0X1.x=D2-D0; X0X1.y=D2-D1;
        }else if(D0D2>0.0f){
                /* here we know that d0d1<=0.0 */ 
            ABC.x=VV1; ABC.y=(VV0-VV1)*D1; ABC.z=(VV2-VV1)*D1; X0X1.x=D1-D0; X0X1.y=D1-D2;
        }else if(D1*D2>0.0f || D0!=0.0f){
                /* here we know that d0d1<=0.0 or that D0!=0.0 */
                ABC.x=VV0; ABC.y=(VV1-VV0)*D0; ABC.z=(VV2-VV0)*D0; X0X1.x=D0-D1; X0X1.y=D0-D2;
        }else if(D1!=0.0f){
                ABC.x=VV1; ABC.y=(VV0-VV1)*D1; ABC.z=(VV2-VV1)*D1; X0X1.x=D1-D0; X0X1.y=D1-D2;
        }else if(D2!=0.0f){
                ABC.x=VV2; ABC.y=(VV0-VV2)*D2; ABC.z=(VV1-VV2)*D2; X0X1.x=D2-D0; X0X1.y=D2-D1;
        }else{
                /* triangles are coplanar */
                return true;
        }
        return false;
    }

    private static boolean coplanar_tri_tri(Vector3f N,
                                            Vector3f V0, Vector3f V1, Vector3f V2,
                                            Vector3f U0, Vector3f U1, Vector3f U2) {
//        float A[3];
        Vector3f A=new Vector3f();
        short i0,i1;
        /* first project onto an axis-aligned plane, that maximizes the area */
        /* of the triangles, compute indices: i0,i1. */
//        A[0]=fabs(N[0]);
        A.x=FastMath.abs(N.x);

//        A[1]=fabs(N[1]);
        A.y=FastMath.abs(N.y);

//        A[2]=fabs(N[2]);
        A.z=FastMath.abs(N.z);

        if(A.x>A.y)
        {
          if(A.x>A.z)
          {
             i0=1;      /* A[0] is greatest */
             i1=2;
          }
          else
          {
              i0=0;      /* A[2] is greatest */
              i1=1;
          }
        }
        else   /* A[0]<=A[1] */
        {
          if(A.z>A.y)
          {
              i0=0;      /* A[2] is greatest */
              i1=1;
          }
          else
          {
              i0=0;      /* A[1] is greatest */
              i1=2;
          }
        }

        /* test all edges of triangle 1 against the edges of triangle 2 */
        float[] V0f=new float[3];
        V0.toArray(V0f);
        float[] V1f=new float[3];
        V1.toArray(V1f);
        float[] V2f=new float[3];
        V2.toArray(V2f);
        float[] U0f=new float[3];
        U0.toArray(U0f);
        float[] U1f=new float[3];
        U1.toArray(U1f);
        float[] U2f=new float[3];
        U2.toArray(U2f);
        if (EDGE_AGAINST_TRI_EDGES(V0f,V1f,U0f,U1f,U2f,i0,i1))
            return true;

        if (EDGE_AGAINST_TRI_EDGES(V1f,V2f,U0f,U1f,U2f,i0,i1))
            return true;
        if (EDGE_AGAINST_TRI_EDGES(V2f,V0f,U0f,U1f,U2f,i0,i1))
            return true;

        /* finally, test if tri1 is totally contained in tri2 or vice versa */
        POINT_IN_TRI(V0f,U0f,U1f,U2f,i0,i1);
        POINT_IN_TRI(U0f,V0f,V1f,V2f,i0,i1);

        return false;
    }

    private static boolean POINT_IN_TRI(float[] V0, float[] U0, float[] U1, float[] U2,
                                        int i0,int i1) {
        float a,b,c,d0,d1,d2;
        /* is T1 completly inside T2? */
        /* check if V0 is inside tri(U0,U1,U2) */
        a=U1[i1]-U0[i1];
        b=-(U1[i0]-U0[i0]);
        c=-a*U0[i0]-b*U0[i1];
        d0=a*V0[i0]+b*V0[i1]+c;

        a=U2[i1]-U1[i1];
        b=-(U2[i0]-U1[i0]);
        c=-a*U1[i0]-b*U1[i1];
        d1=a*V0[i0]+b*V0[i1]+c;

        a=U0[i1]-U2[i1];
        b=-(U0[i0]-U2[i0]);
        c=-a*U2[i0]-b*U2[i1];
        d2=a*V0[i0]+b*V0[i1]+c;
        if(d0*d1>0.0 && d0*d2>0.0)
            return true;
        else
            return false;
    }

    private static boolean EDGE_AGAINST_TRI_EDGES(float[] V0, float[] V1, float[] U0, float[] U1, float[] U2,
                                                  int i0,int i1) {
        float Ax,Ay;
        Ax=V1[i0]-V0[i0];
        Ay=V1[i1]-V0[i1];
        /* test edge U0,U1 against V0,V1 */
        if (EDGE_EDGE_TEST(V0,U0,U1,i0,i1,Ax,Ay))
            return true;
        /* test edge U1,U2 against V0,V1 */
        if (EDGE_EDGE_TEST(V0,U1,U2,i0,i1,Ax,Ay))
            return true;
        /* test edge U2,U1 against V0,V1 */
        if (EDGE_EDGE_TEST(V0,U2,U0,i0,i1,Ax,Ay))
            return true;
        return false;
    }

    private static boolean EDGE_EDGE_TEST(float[] V0, float[] U0, float[] U1,
                                          int i0, int i1,float Ax,float Ay){
        float Bx=U0[i0]-U1[i0];
        float By=U0[i1]-U1[i1];
        float Cx=V0[i0]-U0[i0];
        float Cy=V0[i1]-U0[i1];
        float f=Ay*Bx-Ax*By;
        float d=By*Cx-Bx*Cy;
        if((f>0 && d>=0 && d<=f) || (f<0 && d<=0 && d>=f)){
            float e=Ax*Cy-Ay*Cx;
            if(f>0){
                if(e>=0 && e<=f) return true;
            } else {
                if(e<=0 && e>=f) return true;
            }
        }
        return false;
    }
}