/*
 * Copyright (c) 2003-2005 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package jmetest.TutorialGuide;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.logging.Level;

import com.jme.app.SimpleGame;
import com.jme.scene.Node;
import com.jme.util.LoggingSystem;
import com.jmex.model.XMLparser.BinaryToXML;
import com.jmex.model.XMLparser.JmeBinaryReader;
import com.jmex.model.XMLparser.Converters.FormatConverter;
import com.jmex.model.XMLparser.Converters.ObjToJme;

/**
 * Started Date: Jul 22, 2004<br><br>
 *
 * Demonstrates loading formats.
 * 
 * @author Jack Lindamood
 */
public class HelloModelLoading extends SimpleGame {
    public static void main(String[] args) {
        HelloModelLoading app = new HelloModelLoading();
        app.setDialogBehaviour(SimpleGame.ALWAYS_SHOW_PROPS_DIALOG);
        // Turn the logger off so we can see the XML later on
        LoggingSystem.getLogger().setLevel(Level.OFF);
        app.start();
    }

    protected void simpleInitGame() {
        // Point to a URL of my model
        URL model=HelloModelLoading.class.getClassLoader().getResource("jmetest/data/model/maggie.obj");

        // Create something to convert .obj format to .jme
        FormatConverter converter=new ObjToJme();
        // Point the converter to where it will find the .mtl file from
        converter.setProperty("mtllib",model);

        // This byte array will hold my .jme file
        ByteArrayOutputStream BO=new ByteArrayOutputStream();
        // This will read the .jme format and convert it into a scene graph
        JmeBinaryReader jbr=new JmeBinaryReader();
        // Use this to visualize the .obj file in XML
        BinaryToXML btx=new BinaryToXML();
        try {
            // Use the format converter to convert .obj to .jme
            converter.convert(model.openStream(), BO);
            // Send the .jme binary to System.out as XML
            btx.sendBinarytoXML(new ByteArrayInputStream(BO.toByteArray()),new OutputStreamWriter(System.out));

            // Tell the binary reader to use bounding boxes instead of bounding spheres
            jbr.setProperty("bound","box");
            
            // Load the binary .jme format into a scene graph
            Node maggie=jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
            // shrink this baby down some
            maggie.setLocalScale(.1f);
            // Put her on the scene graph
            rootNode.attachChild(maggie);
        } catch (IOException e) {   // Just in case anything happens
            System.out.println("Damn exceptions!" + e);
            e.printStackTrace();
            System.exit(0);
        }
    }
}
