package com.jme.scene.shape;

import com.jme.scene.TriMesh;
import com.jme.math.Vector3f;
import com.jme.math.Vector2f;
import com.jme.renderer.ColorRGBA;

/**
 * Started Date: Aug 22, 2004<br><br>
 * This primitive represents a box that has options to orient it acording to its X/Y/Z axis.
 * It is used to create an OrientedBoundingBox mostly.
 * @author Jack Lindamood
 */
public class OrientedBox extends TriMesh{
    /** Center of the Oriented Box. */
    protected Vector3f center;
    /** X axis of the Oriented Box.*/
    protected Vector3f xAxis=new Vector3f(1,0,0);
    /** Y axis of the Oriented Box.*/
    protected Vector3f yAxis=new Vector3f(0,1,0);
    /** Z axis of the Oriented Box.*/
    protected Vector3f zAxis=new Vector3f(0,0,1);
    /** Extents of the box along the x,y,z axis.*/
    protected Vector3f extent=new Vector3f(0,0,0);
    /** Per vertex color of the drawn OrientedBox on computeInformation calls*/
    protected ColorRGBA meshColor;
    /** Texture coordintae values for the corners of the box. */
    protected Vector2f texTopRight,texTopLeft,texBotRight,texBotLeft;
    /** Vector array used to store the array of 8 corners the box has.*/
    protected Vector3f [] vectorStore;
    /** Vector array used to store the box's normals. */
    protected Vector3f [] normalStore;
    /** If true, the box's vectorStore array correctly represnts the box's corners.*/
    protected boolean correctCorners;

    /**
     * Creates a new OrientedBox with the given name.
     * @param name The name of the new box.
     */
    public OrientedBox(String name){
        super(name);
        vectorStore=new Vector3f[8];
        for (int i=0;i<vectorStore.length;i++){
            vectorStore[i]=new Vector3f();
        }
        normalStore=new Vector3f[6];
        for (int i=0;i<normalStore.length;i++){
            normalStore[i]=new Vector3f();
        }
        meshColor=new ColorRGBA(ColorRGBA.white);
        texTopRight=new Vector2f(1,1);
        texTopLeft=new Vector2f(1,0);
        texBotRight=new Vector2f(0,1);
        texBotLeft=new Vector2f(0,0);
        center=new Vector3f(0,0,0);
        correctCorners=false;
    }

    /**
     * Takes the plane and center information and creates
     * the correct vertex,normal,color,texture,index information to represent
     * the OrientedBox.
     */
    public void computeInformation(){
        setVertexData();
        setNormalData();
        setColorData();
        setTextureData();
        setIndexData();
    }

    /**
     * Sets the correct indices array for the box.
     */
    private void setIndexData() {
        if (indices==null || indices.length!=36){
            indices=new int[36];
            setIndexBuffer(null);
        }
        for (int i=0;i<6;i++){
            indices[i*6+0]=i*4+0;
            indices[i*6+1]=i*4+1;
            indices[i*6+2]=i*4+3;
            indices[i*6+3]=i*4+1;
            indices[i*6+4]=i*4+2;
            indices[i*6+5]=i*4+3;
        }
        updateIndexBuffer();
    }

    /**
     * Sets the correct texture array for the box.
     */
    private void setTextureData() {
        if (texture==null || texture[0]==null || texture[0].length!=24){
            texture[0]=new Vector2f[24];
            texBuf[0]=null;
            for (int i=0;i<24;i++)
                texture[0][i]=new Vector2f();
        }
        texture[0][0]=texture[0][4]=texture[0][8]=
                texture[0][12]=texture[0][16]=texture[0][20]=texTopRight;
        texture[0][1]=texture[0][5]=texture[0][9]=
                texture[0][13]=texture[0][17]=texture[0][21]=texTopLeft;
        texture[0][2]=texture[0][6]=texture[0][10]=
                texture[0][14]=texture[0][18]=texture[0][22]=texBotLeft;
        texture[0][3]=texture[0][7]=texture[0][11]=
                texture[0][15]=texture[0][19]=texture[0][23]=texBotRight;
        updateTextureBuffer();
    }

    /**
     * Sets the correct color array for the box.
     */
    private void setColorData() {
        if (color==null || color.length!=24){
            color=new ColorRGBA[24];
            colorBuf=null;
            for (int i=0;i<24;i++)
                color[i]=new ColorRGBA();
        }
        for (int i=0;i<24;i++)
            color[i]=meshColor;
        updateColorBuffer();
    }

    /**
     * Sets the correct normal array for the box.
     */
    private void setNormalData() {
        computeNormals();
        if (normal==null || normal.length!=24){
            normal = new Vector3f[24];
            for (int i=0;i<24;i++)
                normal[i]=new Vector3f();
            normBuf=null;
        }

        // top
        normal[0].set(normal[1].set(normal[2].set(normal[3].set(yAxis))));
        // right
        normal[4].set(normal[5].set(normal[6].set(normal[7].set(xAxis))));
        // left
        normal[8].set(normal[9].set(normal[10].set(normal[11].set(xAxis).negateLocal())));
        // bottom
        normal[12].set(normal[13].set(normal[14].set(normal[15].set(yAxis).negateLocal())));
        // back
        normal[16].set(normal[17].set(normal[18].set(normal[19].set(zAxis).negateLocal())));
        // front
        normal[20].set(normal[21].set(normal[22].set(normal[23].set(zAxis))));
        updateNormalBuffer();
    }

    /**
     * Stores into normalStore the current axis values.
     */
    private void computeNormals() {
        normalStore[0].set(xAxis);
        normalStore[1].set(xAxis).negateLocal();
        normalStore[2].set(yAxis);
        normalStore[3].set(yAxis).negateLocal();
        normalStore[4].set(zAxis);
        normalStore[5].set(zAxis).negateLocal();
    }

    /**
     * Sets the correct vertex information for the box.
     */
    private void setVertexData() {
        computeCorners();
        if (vertex==null || vertex.length!=24){
            vertex=new Vector3f[24];
            vertQuantity=24;
            vertBuf=null;
        }
        // top
        vertex[0]=vectorStore[0];
        vertex[1]=vectorStore[1];
        vertex[2]=vectorStore[5];
        vertex[3]=vectorStore[3];

        // right
        vertex[4]=vectorStore[0];
        vertex[5]=vectorStore[3];
        vertex[6]=vectorStore[6];
        vertex[7]=vectorStore[2];

        // left
        vertex[8]=vectorStore[5];
        vertex[9]=vectorStore[1];
        vertex[10]=vectorStore[4];
        vertex[11]=vectorStore[7];

        // bottom
        vertex[12]=vectorStore[6];
        vertex[13]=vectorStore[7];
        vertex[14]=vectorStore[4];
        vertex[15]=vectorStore[2];

        // back
        vertex[16]=vectorStore[3];
        vertex[17]=vectorStore[5];
        vertex[18]=vectorStore[7];
        vertex[19]=vectorStore[6];

        // front
        vertex[20]=vectorStore[1];
        vertex[21]=vectorStore[4];
        vertex[22]=vectorStore[2];
        vertex[23]=vectorStore[0];
        updateVertexBuffer();
    }

    /**
     * Sets the vectorStore information to the 8 corners of the box.
     */
    protected void computeCorners() {
        correctCorners=true;
        Vector3f yCrossZmulX=yAxis.cross(zAxis).multLocal(extent.x);
        Vector3f zCrossXmulY=zAxis.cross(xAxis).multLocal(extent.y);
        Vector3f xCrossYmulZ=xAxis.cross(yAxis).multLocal(extent.z);
        float xDotYcrossZ=xAxis.dot(yAxis.cross(zAxis));
        vectorStore[0].set(
                yCrossZmulX.add(
                    zCrossXmulY.add(
                        xCrossYmulZ))).
                divideLocal(xDotYcrossZ).addLocal(center);

        vectorStore[1].set(
                yCrossZmulX.mult(-1).addLocal(
                    zCrossXmulY.add(
                        xCrossYmulZ))).
                divideLocal(xDotYcrossZ).addLocal(center);

        vectorStore[2].set(
                yCrossZmulX.add(
                    zCrossXmulY.mult(-1).addLocal(
                        xCrossYmulZ))).
                divideLocal(xDotYcrossZ).addLocal(center);

        vectorStore[3].set(
                yCrossZmulX.add(
                    zCrossXmulY.add(
                        xCrossYmulZ.mult(-1)))).
                divideLocal(xDotYcrossZ).addLocal(center);

        vectorStore[4].set(
                yCrossZmulX.mult(-1).addLocal(
                    zCrossXmulY.mult(-1).addLocal(
                        xCrossYmulZ))).
                divideLocal(xDotYcrossZ).addLocal(center);

        vectorStore[5].set(
                yCrossZmulX.mult(-1).addLocal(
                    zCrossXmulY.add(
                        xCrossYmulZ.mult(-1)))).
                divideLocal(xDotYcrossZ).addLocal(center);

        vectorStore[6].set(
                yCrossZmulX.add(
                    zCrossXmulY.mult(-1).addLocal(
                        xCrossYmulZ.mult(-1)))).
                divideLocal(xDotYcrossZ).addLocal(center);

        vectorStore[7].set(
                yCrossZmulX.mult(-1).addLocal(
                    zCrossXmulY.mult(-1).addLocal(
                        xCrossYmulZ.mult(-1)))).
                divideLocal(xDotYcrossZ).addLocal(center);
    }

    /**
     * Returns the center of the box.
     * @return The box's center.
     */
    public Vector3f getCenter() {
        return center;
    }

    /**
     * Sets the box's center to the given value.  Shallow copy only.
     * @param center The box's new center.
     */
    public void setCenter(Vector3f center) {
        this.center = center;
    }

    /**
     * Returns the box's extent vector along the x,y,z.
     * @return The box's extent vector.
     */
    public Vector3f getExtent() {
        return extent;
    }

    /**
     * Sets the box's extent vector to the given value.  Shallow copy only.
     * @param extent The box's new extent.
     */
    public void setExtent(Vector3f extent) {
        this.extent = extent;
    }
}