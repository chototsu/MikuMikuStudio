package com.jme.scene.model.ms3d;

import com.jme.scene.TriMesh;
import com.jme.math.Vector3f;
import com.jme.math.Vector2f;

/**
 * Helper class for MilkLoader that holds the geometry loaded from
 * a .ms3d file.  These are attached to the returned Node from
 * <code>MilkLoader.load</code>
 *
 * @author Jack Lindamood
 */
class MilkshapeGroup extends TriMesh{
    int numTriangles;
    int[] triangleIndices;
    byte materialIndex;
    Vector3f[] myNormals;
    Vector3f[] myVertexes;
    Vector2f[] texCoords;
    int[] indicies;

    MilkshapeGroup(String name) {
        super(name);
    }

    public String toString() {
        return "MilkshapeGroup{" +
                "name='" + name + '\'' +
                ", numTriangles=" + numTriangles +
                ", triangleIndices=" + triangleIndices +
                ", materialIndex=" + materialIndex +
                '}';
    }

    public void buildGeo(MilkshapeTriangle[] selectableTriangles,Vector3f[] indexedVertexes) {
        myNormals=new Vector3f[numTriangles*3];
        myVertexes=new Vector3f[numTriangles*3];
        texCoords=new Vector2f[numTriangles*3];
        indicies=new int[numTriangles*3];
        for (int i=0;i<numTriangles;i++){
            for (int j=0;j<3;j++){
                myNormals[i*3+j]=selectableTriangles[triangleIndices[i]].vertexNormals[j];
                myVertexes[i*3+j]=indexedVertexes[selectableTriangles[triangleIndices[i]].vertexIndices[j]];
                texCoords[i*3+j]=selectableTriangles[triangleIndices[i]].texCoords[j];
                indicies[i*3+j]=i*3+j;
            }
        }
        this.reconstruct(myVertexes,myNormals, null,texCoords,indicies);
    }
    private void getGeo(MilkshapeGroup copy){
//        this.reconstruct(copy.myVertexes,copy.myNormals,null,copy.texCoords,copy.indices);
        this.setVertices(copy.myVertexes);
        this.setNormals(copy.myNormals);
//        this.setTextures(copy.texCoords);
//        this.setIndices(copy.indices);
        this.setTextureBuffer(copy.getTextureAsFloatBuffer());
        this.setIndexBuffer(copy.getIndexAsBuffer());
    }


    MilkshapeGroup(MilkshapeGroup copy){
        super(copy.name);
        this.numTriangles=copy.numTriangles;
        this.triangleIndices=copy.triangleIndices;
        this.materialIndex=copy.materialIndex;
        getGeo(copy);
    }



}
