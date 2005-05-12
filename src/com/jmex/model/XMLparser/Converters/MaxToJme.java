package com.jmex.model.XMLparser.Converters;

import com.jme.util.LittleEndien;
import com.jme.animation.SpatialTransformer;
import com.jme.scene.Node;
import com.jmex.model.XMLparser.JmeBinaryWriter;
import com.jmex.model.XMLparser.Converters.TDSChunkingFiles.TDSFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * Started Date: Jun 26, 2004<br><br>
 *
 * Converts .3ds files into jME binary
 *
 * @author Jack Lindamood
 */
public class MaxToJme extends FormatConverter {
    private LittleEndien myIn;

    private TDSFile chunkedTDS=null;

    /**
     * Converts a .3ds file to .jme via command prompt
     * @param args The array of parameters.  args="file1.3ds file2.jme" will convert file1.3ds to jme and save it to file2.jme.
     */
    public static void main(String[] args){
        new DummyDisplaySystem();
        new MaxToJme().attemptFileConvert(args);
    }

    /**
     * Converts a .3ds file (represented by the InputStream) to jME format.
     * @param max The .3ds file as an InputStream
     * @param bin The place to put the jME format
     * @throws IOException If read/write goes wrong.
     */
    public void convert(InputStream max,OutputStream bin) throws IOException {
        myIn=new LittleEndien(max);
        chunkedTDS=new TDSFile(myIn);
        Node toReturn=chunkedTDS.buildScene();
        chunkedTDS=null;
        myIn=null;
        new JmeBinaryWriter().writeScene(toReturn,bin);
    }

    /**
     * This function returns the controller of a loaded 3ds model.  Will return
     * null if a correct SpatialTransformer could not be found, or if one does not exist.
     * @param model The model that was loaded.
     * @return The controller for that 3ds model.
     */
    public static SpatialTransformer findController(Node model) {
        if (model.getQuantity()==0 ||
                model.getChild(0).getControllers().size()==0 ||
                !(model.getChild(0).getController(0) instanceof SpatialTransformer))
            return null;
        return (SpatialTransformer) (model.getChild(0)).getController(0);
    }
}