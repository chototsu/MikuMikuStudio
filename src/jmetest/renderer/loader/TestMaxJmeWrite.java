package jmetest.renderer.loader;

import com.jme.app.SimpleGame;
import com.jme.scene.model.XMLparser.MaxToJme;


import java.io.IOException;
import java.io.ByteArrayOutputStream;
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
        MaxToJme C1=new MaxToJme();
        try {
            ByteArrayOutputStream BO=new ByteArrayOutputStream();
            URL box=TestMaxJmeWrite.class.getClassLoader().getResource("jmetest/data/model/box.3DS");
            C1.convert(box.openStream(),BO);
        } catch (IOException e) {
            System.out.println("damn exceptions:" + e);
        }
    }
}
