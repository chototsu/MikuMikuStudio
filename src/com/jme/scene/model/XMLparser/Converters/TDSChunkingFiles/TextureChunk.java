package com.jme.scene.model.XMLparser.Converters.TDSChunkingFiles;

import com.jme.image.Texture;

import java.io.DataInput;
import java.io.IOException;

/**
 * Started Date: Jul 2, 2004<br><br>
 *
 * parent=afff=MAT_BLOCK
 * type=0xA200 - 0xA34C=various
 *
 * @author Jack Lindamood
 */
class TextureChunk extends ChunkerClass{

    float percent;
    String texName;
    int flags;
    float textureBlur;
    float bumpPercentage;
    float vScale;
    float uScale;
    public TextureChunk(DataInput myIn, ChunkHeader header) throws IOException {
        super(myIn, header);
    }

    protected boolean processChildChunk(ChunkHeader i) throws IOException {
            switch (i.type){
                case PRCT_INT_FRMT:
                    percent=myIn.readShort()/100f;
                    if (DEBUG) System.out.println("Texture percent:"+percent);
                    return true;
                case MAT_TEXNAME:
                    texName=readcStr(i.length);
                    return true;
                case MAT_TEX_FLAGS:
                    flags=myIn.readUnsignedShort();
                    return true;
                case MAT_TEX_BLUR:
                    textureBlur=myIn.readFloat();
                    return true;
                case MAT_TEX_BUMP_PER:
                    bumpPercentage=myIn.readShort()/100f;
                    if (DEBUG) System.out.println("Texture bump percent:"+bumpPercentage);
                    return true;
                case TEXTURE_V_SCALE:
                    vScale=myIn.readFloat();
                    return true;
                case TEXTURE_U_SCALE:
                    uScale=myIn.readFloat();
                    return true;
                default:
                    return false;
            }
    }
}
