package jmetest.renderer.loader;

import com.jme.app.SimpleGame;
import com.jme.scene.model.XMLparser.Md2ToXML;
import com.jme.scene.model.XMLparser.SAXReader;
import com.jme.scene.model.md2.Md2Model;
import com.jme.scene.Node;
import com.jme.scene.state.TextureState;
import com.jme.image.Texture;
import com.jme.util.TextureManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.net.URL;

/**
 * Started Date: Jun 14, 2004<br><br>
 * Test class to test the ability to load and save .md2 files to XML format
 * 
 * @author Jack Lindamood
 */
public class TestMd2XMLWrite extends SimpleGame{
    float totalFPS;
    long totalCounts;
    public static void main(String[] args) {
        new TestMd2XMLWrite().start();
    }
    protected void simpleUpdate() {
        totalFPS+=timer.getFrameRate();
        totalCounts++;
        if (totalCounts%1000==0){
            System.out.println("FPS: " + (totalFPS/totalCounts));
            totalFPS = totalCounts = 0;
        }
    }
    protected void simpleInitGame1() {  // New one
        Md2ToXML converter=new Md2ToXML();
        ByteArrayOutputStream BO=new ByteArrayOutputStream();
        URL freak=TestMd2XMLWrite.class.getClassLoader().getResource("jmetest/data/model/drfreak.md2");
        URL textu=TestMd2XMLWrite.class.getClassLoader().getResource("jmetest/data/model/drfreak.jpg");
        try {
            converter.writeFiletoStream(freak,BO);
        } catch (IOException e) {
            System.out.println("Damn exceptions!");
        }
        SAXReader sr=new SAXReader();
        Node freakmd2=sr.loadXML(new ByteArrayInputStream(BO.toByteArray()));
        TextureState ts = display.getRenderer().getTextureState();
        ts.setEnabled(true);
        ts.setTexture(
        TextureManager.loadTexture(
            textu,
            Texture.MM_LINEAR,
            Texture.FM_LINEAR,
            true));
        freakmd2.setRenderState(ts);
        freakmd2.setLocalScale(.5f);
        freakmd2.getChild(0).getController(0).setSpeed(10);
        rootNode.attachChild(freakmd2);
    }
    protected void simpleInitGame2() {  // Old one
        URL freak=TestMd2XMLWrite.class.getClassLoader().getResource("jmetest/data/model/drfreak.md2");
        URL textu=TestMd2XMLWrite.class.getClassLoader().getResource("jmetest/data/model/drfreak.jpg");
        Md2Model freakmd2=new Md2Model("freak");
        long time=System.currentTimeMillis();
        freakmd2.load(freak);
        System.out.println("Load time: " + (System.currentTimeMillis()-time));
        TextureState ts = display.getRenderer().getTextureState();
        ts.setEnabled(true);
        ts.setTexture(
        TextureManager.loadTexture(
            textu,
            Texture.MM_LINEAR,
            Texture.FM_LINEAR,
            true));
        freakmd2.setRenderState(ts);
        freakmd2.setLocalScale(.5f);
        rootNode.attachChild(freakmd2);
    }

    protected void simpleInitGame() {
        simpleInitGame1();
    }
}

/*
For old loader:
FPS: 381.7512
FPS: 428.86276
FPS: 478.78525
FPS: 479.9176
FPS: 476.76544
FPS: 478.65497
FPS: 482.09097
FPS: 480.20566
FPS: 480.8201
Load time: 4246

For new loader:
FPS: 397.99054
FPS: 450.28546
FPS: 489.75085
FPS: 491.8328
FPS: 489.68756
FPS: 488.69788
FPS: 490.11353
FPS: 492.1093
Total load time: 3104
*/