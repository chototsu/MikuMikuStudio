/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the Mojo Monkey Coding, jME, jMonkey Engine, nor the
 * names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.jme.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import javax.imageio.ImageIO;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;

import com.jme.image.BitmapHeader;
import com.jme.image.Texture;
import com.jme.system.DisplaySystem;
import com.jme.scene.state.TextureState;

/**
 *
 * <code>TextureManager</code> provides static methods for building a
 * <code>Texture</code> object. Typically, the information supplied is the
 * filename and the texture properties.
 *
 * @author Mark Powell
 * @author Joshua Slack -- cache code
 * @version $Id: TextureManager.java,v 1.27 2004-09-08 17:40:09 renanse Exp $
 */
final public class TextureManager {

  private static HashMap m_tCache = new HashMap();

  private TextureManager() {}

  /**
   * <code>loadTexture</code> loads a new texture defined by the parameter
   * string. Filter parameters are used to define the filtering of the
   * texture. Whether the texture is to be mipmapped or not is denoted by the
   * isMipmapped boolean flag. If there is an error loading the file, null is
   * returned.
   *
   * @param file
   *            the filename of the texture image.
   * @param minFilter
   *            the filter for the near values.
   * @param magFilter
   *            the filter for the far values.
   * @param isMipMapped
   *            determines if we will load the texture mipmapped or not. True
   *            load the texture mipmapped, false do not.
   *
   * @return the loaded texture. If there is a problem loading the texture,
   *         null is returned.
   */
  public static com.jme.image.Texture loadTexture(String file, int minFilter,
                                                  int magFilter,
                                                  boolean isMipMapped) {
    return loadTexture(file, minFilter, magFilter, 1.0f, isMipMapped, true);
  }

  /**
   * <code>loadTexture</code> loads a new texture defined by the parameter
   * string. Filter parameters are used to define the filtering of the
   * texture. Whether the texture is to be mipmapped or not is denoted by the
   * isMipmapped boolean flag. If there is an error loading the file, null is
   * returned.
   *
   * @param file
   *            the filename of the texture image.
   * @param minFilter
   *            the filter for the near values.
   * @param magFilter
   *            the filter for the far values.
   * @param isMipmapped
   *            determines if we will load the texture mipmapped or not. True
   *            load the texture mipmapped, false do not.
   * @param flipped
   *            If true, the images Y values are flipped.
   *
   * @return the loaded texture. If there is a problem loading the texture,
   *         null is returned.
   */
  public static com.jme.image.Texture loadTexture(String file, int minFilter,
                                                  int magFilter,
                                                  float anisoLevel,
                                                  boolean isMipmapped,
                                                  boolean flipped) {
    URL url = null;
    try {
      url = new URL("file:" + file);
    }
    catch (MalformedURLException e) {
      e.printStackTrace();
    }
    return loadTexture(url, minFilter, magFilter, anisoLevel, isMipmapped, flipped);
  }

  /**
   * <code>loadTexture</code> loads a new texture defined by the parameter
   * url. Filter parameters are used to define the filtering of the texture.
   * Whether the texture is to be mipmapped or not is denoted by the
   * isMipmapped boolean flag. If there is an error loading the file, null is
   * returned.
   *
   * @param file
   *            the url of the texture image.
   * @param minFilter
   *            the filter for the near values.
   * @param magFilter
   *            the filter for the far values.
   * @param isMipMapped
   *            determines if we will load the texture mipmapped or not. True
   *            load the texture mipmapped, false do not.
   *
   * @return the loaded texture. If there is a problem loading the texture,
   *         null is returned.
   */
  public static com.jme.image.Texture loadTexture(URL file, int minFilter,
                                                  int magFilter,
                                                  boolean isMipMapped) {
    return loadTexture(file, minFilter, magFilter, 1.0f, isMipMapped, true);
  }

  /**
   * <code>loadTexture</code> loads a new texture defined by the parameter
   * url. Filter parameters are used to define the filtering of the texture.
   * Whether the texture is to be mipmapped or not is denoted by the
   * isMipmapped boolean flag. If there is an error loading the file, null is
   * returned.
   *
   * @param file
   *            the url of the texture image.
   * @param minFilter
   *            the filter for the near values.
   * @param magFilter
   *            the filter for the far values.
   * @param isMipmapped
   *            determines if we will load the texture mipmapped or not. True
   *            load the texture mipmapped, false do not.
   * @param flipped
   *            If true, the images Y values are flipped.
   *
   * @return the loaded texture. If there is a problem loading the texture,
   *         null is returned.
   */
  public static com.jme.image.Texture loadTexture(URL file, int minFilter,
                                                  int magFilter,
                                                  float anisoLevel,
                                                  boolean isMipmapped,
                                                  boolean flipped) {
    if (null == file) {
      System.err.println("Could not load image...  URL was null.");
      return null;
    }
    String fileName = file.getFile();
    if (fileName == null)
      return null;

    TextureKey tkey = new TextureKey(file, minFilter, magFilter, anisoLevel, flipped);
    Texture texture = (Texture) m_tCache.get(tkey);

    if (texture != null) {
      // Uncomment if you want to see when this occurs.
//      System.err.println("******** REUSING TEXTURE ********");
      Texture tClone = texture.createSimpleClone();
      return tClone;
    }

    /*
     * // Debugging code; rename every texture request as a TGA file instead
     *
     * try { String URLstring = file.toString(); URLstring =
     * URLstring.substring(0,URLstring.lastIndexOf('.')) + ".tga"; file =
     * new URL(URLstring); fileName = file.getFile(); } catch(Exception e) {
     * e.printStackTrace(); }
     *  // Debugging code; validate existence of file as a seperate step
     *
     * try { InputStream fileStream = file.openStream(); fileStream.close(); }
     * catch(IOException e) { LoggingSystem.getLogger().log(Level.WARNING,
     * "File existence check failed: " + file); return null; }
     */
    // Changed: It's the responsibility of each case below to ultimately
    // create the com.jme.image.Image imageData object.
    // Changed: Exact the file extension only once.
    // TODO: Some types currently require making a java.awt.Image object as
    // an
    // intermediate step. Rewrite each type to avoid AWT at all costs.
    com.jme.image.Image imageData = null;
    try {
      String fileExt = fileName.substring(fileName.lastIndexOf('.'));
      if (".TGA".equalsIgnoreCase(fileExt)) { // TGA, direct to imageData
        imageData = TGALoader.loadImage(file.openStream());
      } else if (".BMP".equalsIgnoreCase(fileExt)) // BMP, awtImage to
      // imageData
      {
        java.awt.Image image = loadBMPImage(file.openStream());
        imageData = loadImage(image, flipped);
      } else { // Anything else
        java.awt.Image image = ImageIO.read(file);
        imageData = loadImage(image, flipped);
      }
    }
    catch (IOException e) {
      e.printStackTrace();
      LoggingSystem.getLogger().log(Level.WARNING,
                                    "(IOException) Could not load: " + file);
      return null;
    }
    if (null == imageData) {
      LoggingSystem.getLogger().log(Level.WARNING,
                                    "(image null) Could not load: " + file);
      return null;
    }
    texture = new Texture(anisoLevel);
    texture.setCorrection(Texture.CM_PERSPECTIVE);
    texture.setFilter(magFilter);
    texture.setImage(imageData);
    texture.setMipmapState(minFilter);
    texture.setImageLocation(file.toString());

    // apply new texture in a state so it will setup the OpenGL id.
    TextureState state = DisplaySystem.getDisplaySystem().getRenderer().
        createTextureState();
    state.setTexture(texture);
    state.apply();

    m_tCache.put(tkey, texture);
    return texture;
  }

  public static com.jme.image.Texture loadTexture(java.awt.Image image,
                                                  int minFilter, int magFilter,
                                                  boolean isMipmapped,
                                                  boolean flipped) {
    com.jme.image.Image imageData = loadImage(image, flipped);
    Texture texture = new Texture();
    texture.setCorrection(Texture.CM_PERSPECTIVE);
    texture.setFilter(magFilter);
    texture.setImage(imageData);
    texture.setMipmapState(minFilter);
    return texture;
  }

  /**
   *
   * <code>loadImage</code> sets the image data.
   *
   * @param image
   *            The image data.
   * @param flipImage
   *            if true will flip the image's y values.
   * @return the loaded image.
   */
  public static com.jme.image.Image loadImage(java.awt.Image image,
                                              boolean flipImage) {
    boolean hasAlpha = hasAlpha(image);
    //      Obtain the image data.
    BufferedImage tex = null;
    try {
      tex = new BufferedImage(image.getWidth(null),
                              image.getHeight(null), hasAlpha
                              ? BufferedImage.TYPE_4BYTE_ABGR
                              : BufferedImage.TYPE_3BYTE_BGR);
    }
    catch (IllegalArgumentException e) {
      LoggingSystem.getLogger().log(Level.WARNING,
                                    "Problem creating buffered Image: " +
                                    e.getMessage());
      return null;
    }
    Graphics2D g = (Graphics2D) tex.getGraphics();
    g.drawImage(image, null, null);
    g.dispose();
    if (flipImage) {
      //Flip the image
      AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
      tx.translate(0, -image.getHeight(null));
      AffineTransformOp op = new AffineTransformOp(tx,
          AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
      tex = op.filter(tex, null);
    }

    //Get a pointer to the image memory
    ByteBuffer scratch = ByteBuffer.allocateDirect(4 * tex.getWidth()
        * tex.getHeight());
    byte data[] = (byte[]) tex.getRaster().getDataElements(0, 0,
        tex.getWidth(), tex.getHeight(), null);
    scratch.clear();
    scratch.put(data);
    scratch.rewind();
    com.jme.image.Image textureImage = new com.jme.image.Image();
    textureImage.setType(hasAlpha
                         ? com.jme.image.Image.RGBA8888
                         : com.jme.image.Image.RGB888);
    textureImage.setWidth(tex.getWidth());
    textureImage.setHeight(tex.getHeight());
    textureImage.setData(scratch);
    return textureImage;
  }

  /**
   * <code>loadBMPImage</code> because bitmap is not directly supported by
   * Java, we must load it manually. The requires opening a stream to the file
   * and reading in each byte. After the image data is read, it is used to
   * create a new <code>Image</code> object. This object is returned to be
   * used for normal use.
   *
   * @param fs
   *            The bitmap file stream.
   *
   * @return <code>Image</code> object that contains the bitmap information.
   */
  private static java.awt.Image loadBMPImage(InputStream fs) {
    try {
      DataInputStream dis = new DataInputStream(fs);
      BitmapHeader bh = new BitmapHeader();
      byte[] data = new byte[dis.available()];
      dis.readFully(data);
      dis.close();
      bh.read(data);
      if (bh.bitcount == 24) {
        return (bh.readMap24(data));
      }
      if (bh.bitcount == 32) {
        return (bh.readMap32(data));
      }
      if (bh.bitcount == 8) {
        return (bh.readMap8(data));
      }
    }
    catch (IOException e) {
      LoggingSystem.getLogger().log(Level.WARNING,
                                    "Error while loading bitmap texture.");
    }
    return null;
  }

  /**
   * <code>hasAlpha</code> returns true if the specified image has
   * transparent pixels
   *
   * @param image
   *            Image to check
   * @return true if the specified image has transparent pixels
   */
  public static boolean hasAlpha(java.awt.Image image) {
    if (null == image) {
      return false;
    }
    if (image instanceof BufferedImage) {
      BufferedImage bufferedImage = (BufferedImage) image;
      return bufferedImage.getColorModel().hasAlpha();
    }
    PixelGrabber pixelGrabber = new PixelGrabber(image, 0, 0, 1, 1, false);
    try {
      pixelGrabber.grabPixels();
      ColorModel colorModel = pixelGrabber.getColorModel();
      if (colorModel != null) {
        return colorModel.hasAlpha();
      } else {
        return false;
      }
    }
    catch (InterruptedException e) {
      System.err.println("Unable to determine alpha of image: " + image);
    }
    return false;
  }

  public static boolean releaseTexture(Texture texture) {
    Collection c = m_tCache.keySet();
    Iterator it = c.iterator();
    TextureKey key;
    Texture next;
    while (it.hasNext()) {
      key = (TextureKey) it.next();
      next = (Texture) m_tCache.get(key);
      if (texture.equals(next)) {
        return releaseTexture(key);
      }
    }
    return false;
  }

  public static boolean releaseTexture(TextureKey tKey) {
    return m_tCache.remove(tKey) != null;
  }

  public static void clearCache() {
    m_tCache.clear();
  }
}
