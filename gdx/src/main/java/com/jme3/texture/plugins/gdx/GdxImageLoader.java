package com.jme3.texture.plugins.gdx;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.math.FastMath;
import com.jme3.texture.Image;
import com.jme3.util.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: kobayasi
 * Date: 13/10/10
 * Time: 23:54
 * To change this template use File | Settings | File Templates.
 */
public class GdxImageLoader implements AssetLoader{
    public GdxImageLoader() {
    }

    @Override
    public Object load(AssetInfo assetInfo) throws IOException {
        Pixmap pixmap = null;
        InputStream is = null;

        try {
            is = assetInfo.openStream();
            byte[] buf = new byte[is.available()];
            System.err.println(assetInfo.getKey()+" buf length = "+buf.length);
            int i = 0;
            while ( i < buf.length) {
                int len = is.read(buf, i, buf.length - i);
                if (len < 0) {
                    throw new IOException();
                }
                i += len;
            }
            is.close();
            is = null;
            pixmap = new Pixmap(buf, 0, buf.length);
            Image.Format format;
            ByteBuffer bb  = BufferUtils.clone(pixmap.getPixels());
            int height = pixmap.getHeight();
            int width = pixmap.getWidth();
            switch(pixmap.getGLFormat()) {
                case GL20.GL_RGBA:
                    format = Image.Format.RGBA8;
                    ByteBuffer bb2 = pixmap.getPixels();
                    for(int y = 0;y<height;y++) {
                        bb.position((y * width)*4);
                        bb2.position(((height - y - 1) * width)*4);
                        for(int i2=0;i2<width * 4;i2++) {
                            bb.put(bb2.get());
                        }
                    }
                    break;
                case GL20.GL_RGB:
                    format = Image.Format.RGB8;
                    bb2 = pixmap.getPixels();
                    for(int y = 0;y<height;y++) {
                        bb.position((y * width)*3);
                        bb2.position(((height - y-1) * width)*3);
                        for(int i2=0;i2<width * 3;i2++) {
                            bb.put(bb2.get());
                        }
                    }
                    break;
                default:
                    throw new IOException("Invalid format "+pixmap.getGLFormat());
            }
            if (pixmap.getWidth() != FastMath.nearestPowerOfTwo(pixmap.getWidth())
                    || pixmap.getHeight() != FastMath.nearestPowerOfTwo(pixmap.getHeight())) {
                if (pixmap.getGLFormat() == GL20.GL_RGBA) {
                    bb = resize(pixmap.getWidth(), pixmap.getHeight(), FastMath.nearestPowerOfTwo(pixmap.getWidth()),FastMath.nearestPowerOfTwo(pixmap.getHeight()),bb);
                } else {
                    bb = resize2(pixmap.getWidth(), pixmap.getHeight(), FastMath.nearestPowerOfTwo(pixmap.getWidth()),FastMath.nearestPowerOfTwo(pixmap.getHeight()),bb);
                }
            }
            Image image = new Image(format, FastMath.nearestPowerOfTwo(pixmap.getWidth()),FastMath.nearestPowerOfTwo(pixmap.getHeight()), bb, null);
            return image;  //To change body of implemented methods use File | Settings | File Templates.
        } finally {
            if (pixmap != null) {
                pixmap.dispose();
            }
            if (is != null) {
                is.close();
            }
        }
    }
    private static ByteBuffer resize(int w1,int h1, int w2, int h2, ByteBuffer bb) {
//        w2 = w1;
//        h2 = h1;
        if (w1 == w2 && h1 == h2) {
            return bb;
        }
        ByteBuffer out = BufferUtils.createByteBuffer(w2 * h2 * 4);
        float f1 = ((float)w1) / (float)w2;
        float f2 = ((float)h1) / (float)h2;
        for(int x = 0;x<w2;x++) {
            for(int y = 0;y<h2;y++) {
                int index = (int)(((int)(f2 * (float)y)) * (float)w1 + f1 * (float)x);
                if (index >= w1 * h1) {
                    index = 0;
                }
                for(int i=0;i<4;i++) {
                    out.put((x + y * w2)*4+i,bb.get(index * 4 + i));
                }
            }
        }
        return out;
    }
    private static ByteBuffer resize2(int w1,int h1, int w2, int h2, ByteBuffer bb) {
//        w2 = w1;
//        h2 = h1;
        if (w1 == w2 && h1 == h2) {
            //return buf;
        }
        ByteBuffer out = BufferUtils.createByteBuffer(w2 * h2 * 3);
        float f1 = ((float)w1) / (float)w2;
        float f2 = ((float)h1) / (float)h2;
        for(int x = 0;x<w2;x++) {
            for(int y = 0;y<h2;y++) {
                int index = (int)(((int)(f2 * (float)y)) * (float)w1 + f1 * (float)x);
                if (index >= w1 * h1) {
                    index = 0;
                }
                for(int i=0;i<3;i++) {
                    out.put((x + y * w2)*3+i,bb.get(index * 3 + i));
                }
            }
        }
        return out;
    }
}
