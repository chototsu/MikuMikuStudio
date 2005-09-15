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

package jmetest.renderer.loader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import com.jme.app.SimpleGame;
import com.jme.scene.Node;
import com.jmex.model.XMLparser.BinaryToXML;
import com.jmex.model.XMLparser.JmeBinaryReader;
import com.jmex.model.XMLparser.JmeBinaryWriter;
import com.jmex.model.XMLparser.XMLtoBinary;

/**
 * Started Date: Jun 23, 2004<br><br>
 *
 * This class test XMLtoBinary, JmeBinaryReader, and JmeBinaryWriter.  
 *
 * @author Jack Lindamood
 */
public class TestBinaryXML extends SimpleGame{
    public static void main(String[] args) {
        TestBinaryXML app=new TestBinaryXML();
        app.setDialogBehaviour(SimpleGame.FIRSTRUN_OR_NOCONFIGFILE_SHOW_PROPS_DIALOG);
        app.start();
    }

    protected void simpleInitGame() {
        try {
            doSimple();
        } catch (IOException e) {
            System.out.println("Send error: " + e.getMessage());
            System.exit(0);
        }
    }

    private void doSimple() throws IOException{

        // Send XML file to jME binary
        XMLtoBinary c1=new XMLtoBinary();
        URL xmldoc=TestBinaryXML.class.getClassLoader().getResource("jmetest/data/XML documents/newSampleScene.xml");
        ByteArrayOutputStream BO1=new ByteArrayOutputStream();
        c1.sendXMLtoBinary(xmldoc.openStream(),BO1);

        // Send jME binary to XML
        BinaryToXML btx=new BinaryToXML();
        btx.sendBinarytoXML(new ByteArrayInputStream(BO1.toByteArray()),new PrintWriter(System.out));

        // Send jME binary to a jME Scene Graph
        JmeBinaryReader jbr=new JmeBinaryReader();
        jbr.setProperty("texclasspath","jmetest/data/images/");
        Node fileScene=jbr.loadBinaryFormat(new ByteArrayInputStream(BO1.toByteArray()));

        // Send a jME SceneGraph to jME Binary
        JmeBinaryWriter jbw=new JmeBinaryWriter();
        ByteArrayOutputStream BO2=new ByteArrayOutputStream();
        long time=System.currentTimeMillis();
        jbw.writeScene(fileScene,BO2);
        System.out.println("Finished Writting time:" + (System.currentTimeMillis()-time));

        // Send the new jME binary to a jME SceneGraph and attach it.
        fileScene=jbr.loadBinaryFormat(new ByteArrayInputStream(BO2.toByteArray()));
        rootNode.attachChild(fileScene);
    }
}
