package com.jme.scene.model.XMLparser.Converters;

import com.jme.util.LittleEndien;
import com.jme.scene.Node;
import com.jme.scene.model.XMLparser.Converters.TDSChunkingFiles.TDSFile;
import com.jme.scene.model.XMLparser.Converters.TDSChunkingFiles.MaxChunkIDs;
import com.jme.scene.model.XMLparser.JmeBinaryWriter;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Stack;
import java.util.HashMap;

/**
 * Started Date: Jun 26, 2004<br><br>
 *
 * Converts .3ds files into jME binary
 *
 * @author Jack Lindamood
 */
public class MaxToJme extends FormatConverter {
    private LittleEndien myIn;

    private Stack s=new Stack();
    private TDSFile chunkedTDS=null;

    /**
     * Converts a .3ds file (represented by the InputStream) to jME format.
     * @param max The .3ds file as an InputStream
     * @param bin The place to put the jME format
     * @throws IOException If read/write goes wrong.
     */
    public void convert(InputStream max,OutputStream bin) throws IOException {
        s.clear();
        myIn=new LittleEndien(max);
        chunkedTDS=new TDSFile(myIn);
        Node toReturn=chunkedTDS.buildScene();
        new JmeBinaryWriter().writeScene(toReturn,bin);
    }
}