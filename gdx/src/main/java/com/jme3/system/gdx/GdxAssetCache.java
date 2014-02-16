package com.jme3.system.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by kobayasi on 2013/12/29.
 */
public class GdxAssetCache {
    public static FileHandle getFileHandle(String path) {
        FileHandle fileHandle = Gdx.files.local("gdxtemp/"+path);
        if (!fileHandle.exists()) {
            InputStream is = GdxAssetCache.class.getClassLoader().getResourceAsStream(path);
            fileHandle.write(is, false);
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileHandle;
    }
}
