package jmetest.renderer.loader;

import com.jme.app.SimpleGame;
import com.jme.scene.model.ase.ASEModel;
import com.jme.scene.model.XMLparser.JmeBinaryWriter;
import com.jme.scene.model.XMLparser.JmeBinaryReader;
import com.jme.scene.model.XMLparser.BinaryToXML;
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
        ASEModel mod=new ASEModel("statue");
        URL statue=TestASEJmeWrite.class.getClassLoader().getResource("jmetest/data/model/Statue.ase");
//        URL statueDIR=TestASEJmeWrite.class.getClassLoader().getResource("");
        if (statue==null){
            System.out.println("Unable to find statue file, did you include jme-test.jar in classpath?");
            System.exit(0);
        }
        mod.load(statue,"jmetest/data/model/");
        JmeBinaryWriter jbw=new JmeBinaryWriter();
        JmeBinaryReader jbr=new JmeBinaryReader();
        ByteArrayOutputStream BO=new ByteArrayOutputStream();
        Node stateBinary=null;
        try {
            jbw.writeScene(mod,BO);
            stateBinary=jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
        } catch (IOException e) {
            System.out.println("damn exceptions:" + e);
        }
        rootNode.attachChild(stateBinary);


    }
}
