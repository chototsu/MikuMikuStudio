package com.jme.scene.model.XMLparser;

import com.jme.scene.TriMesh;
import com.jme.math.Vector3f;
import com.jme.math.Vector2f;
import com.jme.renderer.ColorRGBA;

/**
 * Started Date: Jun 14, 2004<br><br>
 * This class is for use with jME's loader system.  It is the same as a TriMesh but doesn't update its floatbuffers for
 * compact storage.  It is <b>NOT</b> ment to be attached to a scenegraph and is for internal use only.
 * 
 * @author Jack Lindamood
 */
class EmptyTriMesh extends TriMesh{
    public EmptyTriMesh(){
        this.texture=new Vector2f[1][];
    }
    public void setVertices(Vector3f[] storageArray){
        this.vertex=storageArray;
    }
    public void setNormals(Vector3f[] storageArray){
        this.normal=storageArray;
    }
    public void setColors(ColorRGBA[] storageArray){
        this.color=storageArray;
    }
    public void setTextures(Vector2f[] storageArray){
        this.texture[0]=storageArray;
    }
    public void setIndices(int[] storageArray){
        this.indices=storageArray;
    }
}
