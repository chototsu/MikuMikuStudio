package jmetest.TutorialGuide;

import com.jme.app.SimpleGame;
import com.jme.terrain.TerrainBlock;
import com.jme.terrain.util.MidPointHeightMap;
import com.jme.terrain.util.ImageBasedHeightMap;
import com.jme.terrain.util.ProceduralTextureGenerator;
import com.jme.math.Vector3f;
import com.jme.bounding.BoundingBox;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jme.image.Texture;

import java.net.URL;

import javax.swing.*;

/**
 * Started Date: Aug 19, 2004<br><br>
 *
 * This program introduces jME's terrain utility classes and how they are used.  It
 * goes over ProceduralTextureGenerator, ImageBasedHeightMap, MidPointHeightMap, and
 * TerrainBlock.
 * 
 * @author Jack Lindamood
 */
public class HelloTerrain extends SimpleGame {
    public static void main(String[] args) {
        HelloTerrain app = new HelloTerrain();
        app.setDialogBehaviour(SimpleGame.ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    protected void simpleInitGame() {
        // First a hand made terrain
        homeGrownHeightMap();
        // Next an automatically generated terrain with a texture
        generatedHeightMap();
        // Finally a terrain loaded from a greyscale image with fancy textures on it.
        complexTerrain();
    }


    private void homeGrownHeightMap() {
        // The map for our terrain.  Each value is a height on the terrain
        int[] map=new int[]{
            1,2,3,4,
            2,1,2,3,
            3,2,1,2,
            4,3,2,1
        };

        // Create a terrain block.  Our integer height values will scale on the map 2x larger x,
        //   and 2x larger z.  Our map's origin will be the regular origin, and it won't create an
        //   AreaClodMesh from it.
        TerrainBlock tb=new TerrainBlock("block",4,
                new Vector3f(2,1,2),
                map,
                new Vector3f(0,0,0),
                false);

        // Give the terrain a bounding box.
        tb.setModelBound(new BoundingBox());
        tb.updateModelBound();

        // Attach the terrain TriMesh to our rootNode
        rootNode.attachChild(tb);
    }


    private void generatedHeightMap() {
        // This will be the texture for the terrain.
        URL grass=HelloTerrain.class.getClassLoader().getResource(
            "jmetest/data/texture/grassb.png");

        //  Use the helper class to create a terrain for us.  The terrain will be 64x64
        MidPointHeightMap mph=new MidPointHeightMap(64,1.5f);
        // Create a terrain block from the created terrain map.
        TerrainBlock tb=new TerrainBlock("midpoint block",mph.getSize(),
                new Vector3f(1,.11f,1),
                mph.getHeightMap(),
                new Vector3f(0,-25,0),false);

        // Add the texture
        TextureState ts=display.getRenderer().createTextureState();
        ts.setTexture(
                TextureManager.loadTexture(grass,Texture.MM_LINEAR,Texture.FM_LINEAR)
        );
        tb.setRenderState(ts);

        // Give the terrain a bounding box.
        tb.setModelBound(new BoundingBox());
        tb.updateModelBound();

        // Attach the terrain TriMesh to rootNode
        rootNode.attachChild(tb);
    }

    private void complexTerrain() {
        // This grayscale image will be our terrain
        URL grayScale=HelloTerrain.class.getClassLoader().getResource("jmetest/data/texture/bubble.jpg");

        // These will be the textures of our terrain.
        URL waterImage=HelloTerrain.class.getClassLoader().getResource("jmetest/data/texture/water.png");
        URL dirtImage=HelloTerrain.class.getClassLoader().getResource("jmetest/data/texture/dirt.jpg");
        URL highest=HelloTerrain.class.getClassLoader().getResource("jmetest/data/texture/highest.jpg");


        //  Create an image height map based on the gray scale of our image.
        ImageBasedHeightMap ib=new ImageBasedHeightMap(
                new ImageIcon(grayScale).getImage()
        );
        // Create a terrain block from the image's grey scale
        TerrainBlock tb=new TerrainBlock("image icon",ib.getSize(),
                new Vector3f(.5f,.05f,.5f),ib.getHeightMap(),
                new Vector3f(0,0,0),false);

        //  Create an object to generate textured terrain from the image based height map.
        ProceduralTextureGenerator pg=new ProceduralTextureGenerator(ib);
        //  Look like water from height 0-60 with the strongest "water look" at 30
        pg.addTexture(new ImageIcon(waterImage),0,30,60);
        //  Look like dirt from height 40-120 with the strongest "dirt look" at 80
        pg.addTexture(new ImageIcon(dirtImage),40,80,120);
        //  Look like highest (pure white) from height 110-256 with the strongest "white look" at 130
        pg.addTexture(new ImageIcon(highest),110,130,256);

        //  Tell pg to create a texture from the ImageIcon's it has recieved.
        pg.createTexture(256);
        TextureState ts=display.getRenderer().createTextureState();
        // Load the texture and assign it.
        ts.setTexture(
                TextureManager.loadTexture(
                        pg.getImageIcon().getImage(),
                        Texture.MM_LINEAR_LINEAR,
                        Texture.FM_LINEAR,
                        true
                )
        );
        tb.setRenderState(ts);

        // Give the terrain a bounding box
        tb.setModelBound(new BoundingBox());
        tb.updateModelBound();

        // Move the terrain in front of the camera
        tb.setLocalTranslation(new Vector3f(0,0,-50));

        // Attach the terrain to our rootNode.
        rootNode.attachChild(tb);
    }
}