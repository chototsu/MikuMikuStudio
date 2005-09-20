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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jmex.model.ModelCloneCreator;
import com.jmex.model.XMLparser.JmeBinaryReader;
import com.jmex.model.XMLparser.Converters.MaxToJme;
import com.jmex.model.XMLparser.Converters.Md2ToJme;
import com.jmex.model.XMLparser.Converters.MilkToJme;
import com.jmex.model.animation.KeyframeController;

/**
 * Started Date: Sep 18, 2004<br><br>
 *
 * @author Jack Lindamood
 */
public class TestModelClones extends SimpleGame {
    public static void main(String[] args) {
        FastMath.USE_FAST_TRIG = true;
        TestModelClones app = new TestModelClones();
        app.setDialogBehaviour(SimpleGame.ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }




    protected void simpleInitGame() {
        putMilkModels();
        putMaxModels();
        putMd2Models();
    }

    private void putMd2Models() {
        Md2ToJme converter=new Md2ToJme();
        ByteArrayOutputStream BO=new ByteArrayOutputStream();

        URL textu=TestMd2JmeWrite.class.getClassLoader().getResource("jmetest/data/model/drfreak.jpg");
        URL freak=TestMd2JmeWrite.class.getClassLoader().getResource("jmetest/data/model/drfreak.md2");
        Node freakmd2=null;
        JmeBinaryReader jbr=new JmeBinaryReader();

        try {
            converter.convert(freak.openStream(),BO);
            freakmd2=jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
        } catch (IOException e) {
            System.out.println("damn exceptions:" + e.getMessage());
        }

        TextureState ts = display.getRenderer().createTextureState();
        ts.setTexture(
        TextureManager.loadTexture(
            textu,
            Texture.MM_LINEAR,
            Texture.FM_LINEAR));
        freakmd2.setRenderState(ts);

        freakmd2.setLocalScale(.1f);

        ModelCloneCreator cc=new ModelCloneCreator(freakmd2);
        cc.addProperty("colors");
        cc.addProperty("texcoords");
        cc.addProperty("indices");
        cc.addProperty("keyframecontroller");
        cc.addProperty("vboinfo");

        for (int i=0;i<3;i++){
            Spatial s=cc.createCopy();
            s.setLocalTranslation(new Vector3f(3*i,4*(i+1),i));
            KeyframeController controller = Md2ToJme.findController((Node) s);
            if(controller != null) {
                controller.setSpeed((i+1.0f)*4);
                controller.setModelUpdate(false);
            }
            s.setCullMode(Spatial.CULL_NEVER);
            rootNode.attachChild(s);
        }
    }

    private void putMilkModels(){
        URL MSFile=TestMilkJmeWrite.class.getClassLoader().getResource(
            "jmetest/data/model/msascii/run.ms3d");
        MilkToJme mtj=new MilkToJme();
        ByteArrayOutputStream BO=new ByteArrayOutputStream();
        Node r=null;

        try {
            mtj.convert(MSFile.openStream(),BO);
            JmeBinaryReader jbr=new JmeBinaryReader();
            jbr.setProperty("texurl",MSFile);
            r=jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        r.setLocalScale(.1f);

        ModelCloneCreator cc=new ModelCloneCreator(r);
        cc.addProperty("colors");
        cc.addProperty("texcoords");
        cc.addProperty("indices");
        cc.addProperty("jointcontroller");
        cc.addProperty("vboinfo");

        for (int i=0;i<3;i++){
            Spatial s=cc.createCopy();
            s.setLocalTranslation(new Vector3f(3*i,0,i));
            if(MilkToJme.findController((Node) s) != null) {
                MilkToJme.findController((Node) s).setSpeed((i+1.0f)/4);
                MilkToJme.findController((Node) s).setModelUpdate(false);
            }
            s.setCullMode(Spatial.CULL_NEVER);
            rootNode.attachChild(s);
        }
    }

    private void putMaxModels() {
        Node r=null;
        try {
            MaxToJme C1=new MaxToJme();
            ByteArrayOutputStream BO=new ByteArrayOutputStream();
            URL maxFile=TestMaxJmeWrite.class.getClassLoader().getResource("jmetest/data/model/Character.3DS");
            C1.convert(new BufferedInputStream(maxFile.openStream()),BO);
            JmeBinaryReader jbr=new JmeBinaryReader();
            jbr.setProperty("bound","box");
            r=jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
            r.setLocalScale(.1f);
            if (r.getChild(0).getControllers().size()!=0)
                r.getChild(0).getController(0).setSpeed(20);
            Quaternion temp=new Quaternion();
            temp.fromAngleAxis(FastMath.PI/2,new Vector3f(-1,0,0));
            r.setLocalRotation(temp);
//            rootNode.attachChild(r);
        } catch (IOException e) {
            System.out.println("Damn exceptions:"+e);
            e.printStackTrace();
        }

        ModelCloneCreator cc=new ModelCloneCreator(r);
        cc.addProperty("colors");
        cc.addProperty("texcoords");
        cc.addProperty("vertices");
        cc.addProperty("normals");
        cc.addProperty("indices");
        cc.addProperty("spatialcontroller");
        cc.addProperty("vboinfo");
        for (int i=0;i<3;i++){
            Spatial s=cc.createCopy();
            s.setLocalTranslation(new Vector3f(0,0,i*3+1));
            MaxToJme.findController((Node) s).setSpeed(i*4);
            rootNode.attachChild(s);
        }
    }
}
