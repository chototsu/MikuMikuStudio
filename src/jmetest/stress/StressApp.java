package jmetest.stress;

import com.jme.app.SimpleGame;
import com.jme.scene.Text;
import com.jme.scene.state.*;
import com.jme.util.TextureManager;
import com.jme.image.Texture;

/**
 * Superclass for all stress tests. Providing some methods used in all the tests.
 */
public abstract class StressApp extends SimpleGame {
    /**
     * Create a line of text.
     * @param string displayed text
     * @return Text
     */
    protected Text createText( final String string ) {
        // -- FPS DISPLAY
        // First setup alpha state
        /** This allows correct blending of text and what is already rendered below it*/
        AlphaState as1 = display.getRenderer().createAlphaState();
        as1.setBlendEnabled(true);
        as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as1.setDstFunction(AlphaState.DB_ONE);
        as1.setTestEnabled(true);
        as1.setTestFunction(AlphaState.TF_GREATER);
        as1.setEnabled(true);

        // Now setup font texture
        TextureState font = display.getRenderer().createTextureState();
        /** The texture is loaded from fontLocation */
        font.setTexture(
                TextureManager.loadTexture(
                        SimpleGame.class.getClassLoader().getResource(
                                fontLocation),
                        Texture.MM_LINEAR,
                        Texture.FM_LINEAR));
        font.setEnabled(true);

        Text text = new Text("hint", string);
        text.setForceView(true);
        text.setTextureCombineMode(TextureState.REPLACE);

        text.setRenderState(font);
        text.setRenderState(as1);
        text.setForceView(true);
        text.setLightCombineMode( LightState.OFF );
        return text;
    }
}
