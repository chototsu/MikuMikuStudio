package jmetest.renderer.loader;

import com.jme.app.SimpleGame;
import com.jme.scene.model.XMLparser.JmeBinaryReader;
import com.jme.scene.model.XMLparser.Converters.AseToJme;
import com.jme.scene.Node;

import java.net.URL;
import java.io.*;

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
        URL statue=TestASEJmeWrite.class.getClassLoader().getResource("jmetest/data/model/Statue.ase");
        if (statue==null){
            System.out.println("Unable to find statue file, did you include jme-test.jar in classpath?");
            System.exit(0);
        }
        AseToJme i=new AseToJme();
        ByteArrayOutputStream BO=new ByteArrayOutputStream();
        try {
            i.writeFiletoStream(statue,BO);
            JmeBinaryReader jbr=new JmeBinaryReader();
            Node file=jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
            rootNode.attachChild(file);
        } catch (IOException e) {
            System.out.println("Damn exceptions:"+e);
        }


    }
}
