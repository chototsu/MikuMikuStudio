package jmetest.TutorialGuide;

import com.jme.app.SimpleGame;
import com.jme.scene.TriMesh;
import com.jme.math.Vector3f;
import com.jme.math.Vector2f;
import com.jme.renderer.ColorRGBA;
import com.jme.bounding.BoundingBox;

/**
 * Started Date: Jul 20, 2004<br><br>
 *
 * Demonstrates making a new TriMesh object from scratch.
 * 
 * @author Jack Lindamood
 */
public class HelloTriMesh extends SimpleGame {
    public static void main(String[] args) {
        HelloTriMesh app = new HelloTriMesh();
        app.setDialogBehaviour(SimpleGame.ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    protected void simpleInitGame() {
        // TriMesh is what most of what is drawn in jME actually is
        TriMesh m=new TriMesh("My Mesh");

        // Vertex positions for the mesh
        Vector3f[] vertexes={
            new Vector3f(0,0,0),
            new Vector3f(1,0,0),
            new Vector3f(0,1,0),
            new Vector3f(1,1,0)
        };

        // Normal directions for each vertex position
        Vector3f[] normals={
            new Vector3f(0,0,1),
            new Vector3f(0,0,1),
            new Vector3f(0,0,1),
            new Vector3f(0,0,1)
        };

        // Color for each vertex position
        ColorRGBA[] colors={
            new ColorRGBA(1,0,0,1),
            new ColorRGBA(1,0,0,1),
            new ColorRGBA(0,1,0,1),
            new ColorRGBA(0,1,0,1)
        };

        // Texture Coordinates for each position
        Vector2f[] texCoords={
            new Vector2f(0,0),
            new Vector2f(1,0),
            new Vector2f(0,1),
            new Vector2f(1,1)
        };

        // The indexes of Vertex/Normal/Color/TexCoord sets.  Every 3 makes a triangle.
        int[] indexes={
            0,1,2,1,2,3
        };

        // Feed the information to the TriMesh
        m.reconstruct(vertexes,normals,colors,texCoords,indexes);

        // Create a bounds
        m.setModelBound(new BoundingBox());
        m.updateModelBound();

        // Attach the mesh to my scene graph
        rootNode.attachChild(m);
    }
}