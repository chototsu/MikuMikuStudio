package com.jme.scene.model.ms3d;

import com.jme.math.Vector3f;
import com.jme.math.Vector2f;


/**
 * Simple helper structure for MilkFile to help it store .ms3d
 * triangles in a fashion similar to how they are stored in the file
 * itself. 
 *
*/
class MilkshapeTriangle {
    int[] vertexIndices=new int[3];
    final Vector3f[] vertexNormals={new Vector3f(),new Vector3f(),new Vector3f()};
    final Vector2f[] texCoords={new Vector2f(),new Vector2f(),new Vector2f()};
}