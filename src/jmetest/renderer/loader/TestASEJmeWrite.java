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
import java.io.InputStream;
import java.net.URL;

import com.jme.app.SimpleGame;
import com.jme.scene.Node;
import com.jmex.model.XMLparser.JmeBinaryReader;
import com.jmex.model.XMLparser.Converters.AseToJme;

/**
 * Started Date: Jun 26, 2004<br><br>
 * 
 * @author Jack Lindamood
 */
public class TestASEJmeWrite extends SimpleGame{
    public static void main(String[] args) {
        TestASEJmeWrite app=new TestASEJmeWrite();
        app.setDialogBehaviour(SimpleGame.FIRSTRUN_OR_NOCONFIGFILE_SHOW_PROPS_DIALOG);
        app.start();
    }
    protected void simpleInitGame() {
        InputStream statue=TestASEJmeWrite.class.getClassLoader().getResourceAsStream("jmetest/data/model/Statue.ase");
        URL stateTextureDir=TestASEJmeWrite.class.getClassLoader().getResource("jmetest/data/model/");
        if (statue==null){
            System.out.println("Unable to find statue file, did you include jme-test.jar in classpath?");
            System.exit(0);
        }
        AseToJme i=new AseToJme();
        ByteArrayOutputStream BO=new ByteArrayOutputStream();
        try {
            i.convert(statue,BO);
            JmeBinaryReader jbr=new JmeBinaryReader();
            jbr.setProperty("texurl",stateTextureDir);
            Node file=jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
            rootNode.attachChild(file);
        } catch (IOException e) {
            System.out.println("Damn exceptions:"+e);
        }


    }
}
