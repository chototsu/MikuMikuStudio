package jmetest.renderer.loader;

import com.jme.app.SimpleGame;
import com.jme.scene.model.XMLparser.JmeBinaryReader;
import com.jme.scene.model.XMLparser.BinaryToXML;
import com.jme.scene.model.XMLparser.Converters.MaxToJme;
import com.jme.scene.Node;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.scene.shape.Box;
import com.jme.math.Vector3f;
import com.jme.math.Quaternion;
import com.jme.math.FastMath;
import com.jme.bounding.BoundingBox;
import com.jme.renderer.ColorRGBA;



import java.io.*;
import java.net.URL;


/**
 * Started Date: Jun 26, 2004<br><br>
 *
 * This class test the ability to save adn write .3ds files
 * 
 * @author Jack Lindamood
 */
public class TestMaxJmeWrite extends SimpleGame{
    public static void main(String[] args) {

        TestMaxJmeWrite app=new TestMaxJmeWrite();
        app.setDialogBehaviour(SimpleGame.FIRSTRUN_OR_NOCONFIGFILE_SHOW_PROPS_DIALOG);
        app.start();
    }

    protected void simpleInitGame() {
        try {
            MaxToJme C1=new MaxToJme();
            ByteArrayOutputStream BO=new ByteArrayOutputStream();
            URL maxFile=TestMaxJmeWrite.class.getClassLoader().getResource("jmetest/data/model/Character.3DS");
            C1.convert(new BufferedInputStream(maxFile.openStream()),BO);
            JmeBinaryReader jbr=new JmeBinaryReader();
            jbr.setProperty("bound","box");
            Node r=jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
            r.setLocalScale(.1f);
            if (r.getChild(0).getControllers().size()!=0)
                r.getChild(0).getController(0).setSpeed(20);
            Quaternion temp=new Quaternion();
            temp.fromAngleAxis(FastMath.PI/2,new Vector3f(-1,0,0));
            rootNode.setLocalRotation(temp);
            rootNode.attachChild(r);
        } catch (IOException e) {
            System.out.println("Damn exceptions:"+e);
            e.printStackTrace();
        }
    }
}