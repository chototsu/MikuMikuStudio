package jmetest.renderer.loader;

import com.jme.app.SimpleGame;
import com.jme.scene.model.XMLparser.MaxToJme;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;


/**
 * Started Date: Jun 26, 2004<br><br>
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
            C1.convert(new FileInputStream("box.3ds"),BO);
        } catch (IOException e) {
            System.out.println("damn exceptions:" + e);
        }
    }
}
