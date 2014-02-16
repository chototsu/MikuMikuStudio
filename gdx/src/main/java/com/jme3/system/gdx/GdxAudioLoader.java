package com.jme3.system.gdx;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by kobayasi on 2013/12/29.
 */
public class GdxAudioLoader implements AssetLoader {
    @Override
    public Object load(AssetInfo assetInfo) throws IOException
    {

        InputStream in = assetInfo.openStream();
        if (in != null)
        {
            in.close();
        }
        GdxAudioData result = new GdxAudioData();
        result.setAssetKey( assetInfo.getKey() );
        return result;
    }
}
