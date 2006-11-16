package jmetest.renderer;

import com.jme.animation.Bone;
import com.jme.animation.SkinNode;
import com.jme.app.AbstractGame;
import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.NodeHandler;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.util.BoneDebugger;
import com.jme.util.TextureManager;

public class TestSimpleBoneAnimation extends SimpleGame {

    SkinNode mySkin;
    Bone theBone, theBone2;

    public static void main(String[] args) {
        TestSimpleBoneAnimation game = new TestSimpleBoneAnimation();
        game.setDialogBehaviour(AbstractGame.ALWAYS_SHOW_PROPS_DIALOG);
        game.start();
    }

    protected void simpleRender() {
        BoneDebugger.drawBones(rootNode, display.getRenderer(), true);
    }

    protected void simpleUpdate() {
    }

    protected void simpleInitGame() {
        Node modelNode = new Node("model");
        Box b = new Box("test", new Vector3f(0, 0, 0), 2f, .5f, .5f);
        b.setModelBound(new BoundingBox());
        b.updateModelBound();
        mySkin = new SkinNode("test skin");
        mySkin.setSkin(b);
        modelNode.attachChild(mySkin);

        TextureState ts = display.getRenderer().createTextureState();
        ts.setTexture(TextureManager.loadTexture(TestSimpleBoneAnimation.class
                .getClassLoader().getResource(
                        "test/data/model/Player/trex-eye.tga"),
                Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, 1.0f, true));
        b.setRenderState(ts);

        MaterialState ms = display.getRenderer().createMaterialState();
        ms.setSpecular(new ColorRGBA(0.9f, 0.9f, 0.9f, 1));
        ms.setShininess(10);
        b.setRenderState(ms);

        theBone = new Bone("Bone01");
        int[] verts = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
                14, 15, 16, 17, 18, 19, 20, 21, 22, 23 };
        float[] weights = new float[] { 0.463532f, 1, 1, 0.399379f, 1, 1, 1, 1,
                1, 0.039898f, 0.038145f, 1, 0.039898f, 0.463532f, 0.399379f,
                0.038145f, 1, 1, 0.038145f, 0.399379f, 0.463532f, 0.039898f, 1,
                1 };

        for (int x = 0; x < verts.length; x++) {
            mySkin.addBoneInfluence(0, verts[x], theBone, weights[x]);
        }

        modelNode.attachChild(theBone);

        theBone2 = new Bone("Bone02");
        int[] verts2 = new int[] { 0, 13, 20, 3, 14, 19, 9, 12, 21, 10, 15, 18 };
        float[] weights2 = new float[] { 0.536468f, 0.536468f, 0.536468f,
                0.600621f, 0.600621f, 0.600621f, 0.960102f, 0.960102f,
                0.960102f, 0.961855f, 0.961855f, 0.961855f };

        for (int x = 0; x < verts2.length; x++) {
            mySkin.addBoneInfluence(0, verts2[x], theBone2, weights2[x]);
        }

        theBone.attachChild(theBone2);
        theBone.updateGeometricState(0, true);
        //mySkin.setBones(new Bone[] { theBone, theBone2 });

        Quaternion b1Q1 = new Quaternion().fromAngleAxis(-0.00123886f,
                new Vector3f(0, 0, 1));
        Quaternion b1Q2 = new Quaternion().fromAngleAxis(-86.3343f,
                new Vector3f(0, 1, 0));
        Quaternion b1Q3 = new Quaternion().fromAngleAxis(90.0012f,
                new Vector3f(1, 0, 0));
        theBone.setLocalRotation(b1Q1.mult(b1Q2).mult(b1Q3));
        theBone.setLocalTranslation(new Vector3f(0.0168828f, -2.66517e-009f,
                0.060972f));

        Quaternion b2Q1 = new Quaternion().fromAngleAxis(-13.8716f,
                new Vector3f(0, 0, 1));
        Quaternion b2Q2 = new Quaternion().fromAngleAxis(2.66225e-012f,
                new Vector3f(0, 1, 0));
        Quaternion b2Q3 = new Quaternion().fromAngleAxis(5.51267e-013f,
                new Vector3f(1, 0, 0));
        theBone2.setLocalRotation(b2Q1.mult(b2Q2).mult(b2Q3));
        theBone2.setLocalTranslation(new Vector3f(0.0639104f, -1.04178f,
                4.47038e-008f));

        theBone.updateGeometricState(0, true);
        mySkin.normalizeWeights();
        mySkin.regenInfluenceOffsets();

        rootNode.attachChild(modelNode);

        this.input = new NodeHandler(modelNode, 10, 10);
    }

}
