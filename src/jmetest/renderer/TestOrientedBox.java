package jmetest.renderer;

import com.jme.app.SimpleGame;
import com.jme.scene.model.XMLparser.JmeBinaryReader;
import com.jme.scene.model.XMLparser.Converters.MaxToJme;
import com.jme.scene.Node;
import com.jme.math.Vector3f;
import com.jme.math.Quaternion;
import com.jme.math.FastMath;


import java.io.*;
import java.net.URL;


/**
 * Started Date: Jun 26, 2004<br><br>
 *
 * This class test the ability to use OBB.
 *
 * @author Jack Lindamood
 */
public class TestOrientedBox extends SimpleGame{
    public static void main(String[] args) {

        TestOrientedBox app=new TestOrientedBox();
        app.setDialogBehaviour(SimpleGame.FIRSTRUN_OR_NOCONFIGFILE_SHOW_PROPS_DIALOG);
        app.start();
    }

    protected void simpleInitGame() {
        try {
            MaxToJme C1=new MaxToJme();
            ByteArrayOutputStream BO=new ByteArrayOutputStream();
            URL maxFile=TestOrientedBox.class.getClassLoader().getResource("jmetest/data/model/Character.3DS");
            C1.convert(new BufferedInputStream(maxFile.openStream()),BO);
            JmeBinaryReader jbr=new JmeBinaryReader();
            jbr.setProperty("bound","obb");
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