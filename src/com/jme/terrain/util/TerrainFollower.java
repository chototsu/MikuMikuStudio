package com.jme.terrain.util;

import com.jme.scene.Spatial;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.math.Vector3f;
import com.jme.math.FastMath;
import com.jme.util.MemPool;
import com.jme.util.LoggingSystem;

import java.util.logging.Level;

/**
 * Started Date: Aug 17, 2004<br><br>
 *
 * This class takes either a trimesh or a node of trimesh objects and builds 2D
 * float[][] of terrain Y values along the mesh's X/Z axis.  These values are polled and
 * an approximate Y value on the terrain is produced, which can be used for anything from
 * updating an object's location to adjusting the camera.  This class is not an all purpose terrain
 * follower.  It assumes the terrain can't have more than one Y value for any X/Z.  It
 * also produces only approximates of distances above the terrain.  Finally, it assumes the terrain
 * is along the X/Z values with Y going up/down.  If this class is used with a Node passed in
 * its constructor, users should first update that Node and it's children's worldTranslations,rotation,scale
 * before passing it to the TerrainFollower.<br>
 *
 * @author Jack Lindamood
 */
public class TerrainFollower {

    private float minX;
    private float maxX;
    private float minZ;
    private float maxZ;
    private float deltaX;
    private float deltaZ;
    private float [][] terrainValues;
    private int xGridLen;
    private int zGridLen;
    private float gridDist;
    /**
     * Creates a new TerrainFollower.  The Spatial must be either a Node or a TriMesh.  All
     * other spatial types are ignored.  High X and Y division values are needed for greater
     * accuracy, but they require more memory.
     * @param terrain The terrain this class should follow.
     * @param divisionsx The x divisions in this class's terrain values array.
     * @param divisionsy The y divisions in this class's terrain values array.
     */
    public TerrainFollower(Spatial terrain,int divisionsx,int divisionsy){
        xGridLen=divisionsx;
        zGridLen=divisionsy;
        terrainValues=new float[divisionsx][];
        for (int i=0;i<divisionsy;i++){
            terrainValues[i]=new float[divisionsy];
            for (int j=0;j<divisionsx;j++)
                terrainValues[i][j]=Float.NaN;
        }
        minX=minZ=Float.MAX_VALUE;
        maxX=maxZ=Float.MIN_VALUE;
        findLimits(terrain);
        deltaX=maxX-minX;
        deltaZ=maxZ-minZ;
        gridDist=(deltaX/xGridLen)*(deltaX/xGridLen) +
                (deltaZ/zGridLen)*(deltaZ/zGridLen);
        fillValues(terrain);

    }

    /**
     *
     * Given a terrain, it's Triangles are polled and used to fill the terrainValues[][]
     * array.
     */
    private void fillValues(Spatial terrain) {
        if (terrain instanceof Node){
            Node parent=(Node) terrain;
            for (int i=parent.getQuantity()-1;i>=0;i--){
                LoggingSystem.getLogger().log(Level.INFO,
                    "Begining to fill " + parent.getChild(i).getName());
                fillValues(parent.getChild(i));
            }
            return;
        }
        if (!(terrain instanceof TriMesh)) return;
        Vector3f []verts=((TriMesh)terrain).getVertices();
        int[] indexes=((TriMesh)terrain).getIndices();
        Vector3f tri0=MemPool.v3a,tri1=MemPool.v3b,tri2=MemPool.v3c;
        for (int tri=0;tri<indexes.length;tri+=3){

            terrain
                .getWorldRotation()
                .mult(verts[indexes[tri+0]], tri0)
                .multLocal(terrain.getWorldScale())
                .addLocal(terrain.getWorldTranslation());
            terrain
                .getWorldRotation()
                .mult(verts[indexes[tri+1]], tri1)
                .multLocal(terrain.getWorldScale())
                .addLocal(terrain.getWorldTranslation());
            terrain
                .getWorldRotation()
                .mult(verts[indexes[tri+2]], tri2)
                .multLocal(terrain.getWorldScale())
                .addLocal(terrain.getWorldTranslation());
            float maxX,minX,maxZ,minZ;
            maxX=tri0.x;
            if (tri1.x>maxX) maxX=tri1.x;
            if (tri2.x>maxX) maxX=tri2.x;

            maxZ=tri0.z;
            if (tri1.z>maxZ) maxZ=tri1.z;
            if (tri2.z>maxZ) maxZ=tri2.z;

            minX=tri0.x;
            if (tri1.x<minX) minX=tri1.x;
            if (tri2.x<minX) minX=tri2.x;

            minZ=tri0.z;
            if (tri1.z<minZ) minZ=tri1.z;
            if (tri2.z<minZ) minZ=tri2.z;

            // Now I have my box , now get my terrain resolution box.
            fillForTriangle(tri0,tri1,tri2,
                    smallestX(minX),smallestZ(minZ),
                    biggestX(maxX),biggestZ(maxZ));
        }
    }

    /**
     * For the given triangle (first three points, the grid is polled for points inside that triangle.  Any
     * point inside that triangle then updates terrainValues[][] with it's Y intersect.
     */
    private void fillForTriangle(Vector3f tri0, Vector3f tri1, Vector3f tri2, int x0, int z0, int x1, int z1) {
        float realX,realZ;
        if (x0<0) x0=0;
        if (z0<0) z0=0;
        if (x1>=xGridLen) x1=xGridLen-1;
        if (z1>=zGridLen) z1=zGridLen-1;
        MemPool.v2a.set(tri0.x,tri0.z);
        MemPool.v2b.set(tri1.x,tri1.z);
        MemPool.v2c.set(tri2.x,tri2.z);
        for (int x=x0;x<x1;x++){
            realX=minX+(deltaX*x)/xGridLen;
            MemPool.v2d.x=realX;
            for (int z=z0;z<z1;z++){
                realZ=minZ+(deltaZ*z)/zGridLen;
                MemPool.v2d.y=realZ;
                int i=FastMath.pointInsideTriangle(MemPool.v2a,MemPool.v2b,MemPool.v2c,MemPool.v2d);
                if (i!=0){
                    float f=PlaneLineIntersection(tri0,tri1,tri2,
                            MemPool.v3d.set(realX,0,realZ),MemPool.v3e.set(realX,1,realZ));
                    if (Float.isNaN(terrainValues[x][z]) || f>terrainValues[x][z])
                        terrainValues[x][z]=f;
                }
            }
        }
    }

    /**
     *
     * Returns the Y value that the given plane (defined by the first 3 points) and the given line
     * (defined by the last 2 points) intersect.
     *
     */
    private float PlaneLineIntersection(Vector3f x1, Vector3f x2, Vector3f x3, Vector3f x4, Vector3f x5) {
        float denominator=FastMath.determinant(
                1   ,1   ,   1,        0,
                x1.x,x2.x,x3.x,x5.x-x4.x,
                x1.y,x2.y,x3.y,x5.y-x4.y,
                x1.z,x2.z,x3.z,x5.z-x4.z);
        float numerator=FastMath.determinant(
                1   ,1   ,   1,   1,
                x1.x,x2.x,x3.x,x4.x,
                x1.y,x2.y,x3.y,x4.y,
                x1.z,x2.z,x3.z,x4.z);
        float t=numerator/denominator;
        return x4.y+(x4.y-x5.y)*t;
    }

    /**
     * Returns the real world X less than or equal to a grid value.
     */
    private int smallestX(float gridValue){
        return (int) FastMath.floor(((gridValue-minX)*xGridLen)/deltaX);
    }

    /**
     * Returns the real world Z less than or equal to a grid value.
     */
    private int smallestZ(float gridValue){
        return (int) FastMath.floor(((gridValue-minZ)*zGridLen)/deltaZ);
    }

    /**
     * Returns the real world X greater than or equal to a grid value.
     */
    private int biggestX(float gridValue){
        return (int) FastMath.ceil(((gridValue-minX)*xGridLen)/deltaX);
    }

    /**
     * Returns the real world Z greater than or equal to a grid value.
     */
    private int biggestZ(float gridValue){
        return (int) FastMath.ceil(((gridValue-minZ)*zGridLen)/deltaZ);
    }

    /**
     * Finds the X,Z limits of the given terrain
     */
    private void findLimits(Spatial terrain) {
        if (terrain instanceof Node){
            Node parent=(Node) terrain;
            for (int i=parent.getQuantity()-1;i>=0;i--){
               findLimits(parent.getChild(i));
            }
            return ;
        }
        if (!(terrain instanceof TriMesh)) return;
        Vector3f[] verts=((TriMesh)terrain).getVertices();
        Vector3f tempRef=MemPool.v3a;
        for (int i=0;i<verts.length;i++){
            tempRef=
                terrain
                .getWorldRotation()
                .mult(verts[i], MemPool.v3b)
                .multLocal(terrain.getWorldScale())
                .addLocal(terrain.getWorldTranslation());
            if (tempRef.x<minX) minX=tempRef.x;
            if (tempRef.x>maxX) maxX=tempRef.x;
            if (tempRef.z<minZ) minZ=tempRef.z;
            if (tempRef.z>maxZ) maxZ=tempRef.z;
        }
    }

    /**
     * Given an X and Z value, this function returns the correct Y value above the given real world
     * X,Z value of the terrain.  The function returns Float.NaN if the given value can't be produced, or
     * doesn't appear to be above the terrain.
     * @param x The X coordinate above the terrain.
     * @param z The Z coordinate above the terrain.
     * @return Either the approximate Y value on the terrain for that given X/Z pair, or NaN.
     */
    public float terrainPosition(float x,float z){
        if (x>maxX || x < minX || z > maxZ || z < minZ) return Float.NaN;
        float xInGrid=realToGridX(x);
        float zInGrid=realToGridZ(z);

        int topX=(int) FastMath.ceil(xInGrid);
        if (topX>=xGridLen) return Float.NaN;
        float topXreal=gridToRealX(topX);

        int bottomX=(int) FastMath.floor(xInGrid);
        if (bottomX<0) return Float.NaN;
        float bottomXreal=gridToRealX(bottomX);

        int topZ=(int) FastMath.ceil(zInGrid);
        if (topZ>=zGridLen) return Float.NaN;
        float topZreal=gridToRealZ(topZ);

        int bottomZ=(int) FastMath.floor(zInGrid);
        if (bottomZ<0) return Float.NaN;
        float bottomZreal=gridToRealZ(bottomZ);

        float distSquare1=gridDist-distSquare(x,z,topXreal,topZreal);
        float distSquare2=gridDist-distSquare(x,z,topXreal,bottomZreal);
        float distSquare3=gridDist-distSquare(x,z,bottomXreal,topZreal);
        float distSquare4=gridDist-distSquare(x,z,bottomXreal,bottomZreal);
        float distSum=distSquare1+distSquare2+distSquare3+distSquare4;
        return terrainValues[topX][topZ]*(distSquare1/distSum)+
            terrainValues[topX][bottomZ]*(distSquare2/distSum)+
            terrainValues[bottomX][topZ]*(distSquare3/distSum)+
            terrainValues[bottomX][bottomZ]*(distSquare4/distSum);
    }

    /**
     * Squared distance from X1,Y1 to X2,Y2
     *
     */
    private float distSquare(float x1,float y1,float x2,float y2){
        return (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2);
    }

    /**
     * Converts from grid to real world X coorindates.
     *
     */
    private float gridToRealX(float i){
        return minX+(i/xGridLen)*deltaX;
    }

    /**
     * Converts from grid to real world Z coordinates.
     */
    private float gridToRealZ(float i){
        return minZ+(i/zGridLen)*deltaZ;
    }

    /**
     * Converts from real world to grid Z coordinates.
     */
    private float realToGridZ(float f){
        return ((f-minZ)*(zGridLen))/deltaZ;
    }

    /**
     * Converts from real world to grid X cooridnates.
     */
    private float realToGridX(float f){
        return ((f-minX)*(xGridLen))/deltaX;
    }
}