/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding
 * All rights reserved.
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

package jme.texture;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import javax.swing.ImageIcon;

import jme.exception.MonkeyGLException;
import jme.exception.MonkeyRuntimeException;
import jme.system.DisplaySystem;
import jme.utility.LoggingSystem;

import org.lwjgl.Sys;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLU;

/**
 * <code>TextureManager</code> maintains all textures within the running
 * application. It is a singleton class, and therefore can be used anywhere
 * in the program. <code>TextureManager</code> maintains a list of texture
 * id's with the name of the file as the key. <br>
 * <p/>To create a new texture invoke
 * the <code>loadTexture</code> method with the file name and the filter parameters.
 * It takes a boolean value of as the last parameter to determine to use
 * mipmapping or not. If the file is already loaded, the texture is NOT
 * reloaded, but the corresponding id is returned instead. 
 * <br><p/>
 * <code>TextureManager</code> also takes care of retrieving id's, binding
 * a texture and deleting textures. 
 * <br><p/>
 * The supported texture types are: PNG, JPG, GIF, TGA(Uncompressed), BMP
 * 
 * @author Mark Powell
 * @version 0.1.0
 */
public class TextureManager {
    //singleton instance
    private static TextureManager instance = null;

    //GL objects
    private GL gl;
    private GLU glu;

    //List of texture ids with filename as the key.
    private HashMap textureList = null;
    private ArrayList keyList = null;
    private ArrayList previousKeys = null;

    private int boundID = -1;

    // For TGA loading
    private static final int NO_TRANSPARENCY = 255;
    private static final int FULL_TRANSPARENCY = 0;

    private static short idLength;
    private static short colorMapType;
    private static short imageType;
    private static int cMapStart;
    private static int cMapLength;
    private static short cMapDepth;
    private static int xOffset;
    private static int yOffset;
    private static int width;
    private static int height;
    private static short pixelDepth;
    private static short imageDescriptor;
    private static DirectColorModel cm;
    private static int[] pixels;

    /**
     * private constructor is called by the <code>getTextureManager</code>
     * method and initializes the texture list. It also sets the gl and glu
     * objects to the systems version.
     * @throws MonkeyGLException if OpenGL context has not been created.
     */
    private TextureManager() {
        gl = DisplaySystem.getDisplaySystem().getGL();
        glu = DisplaySystem.getDisplaySystem().getGLU();
        
        if(null == gl || null == glu) {
        	throw new MonkeyGLException("GL/GLU must be initialized before " +        		"calling TextureManager.");
        }
        textureList = new HashMap();
        keyList = new ArrayList();
        previousKeys = new ArrayList();
    }

    /**
     * <code>reload</code> reloads all loaded textures after making a call
     * to <code>saveKeys</code>. This is useful for
     * reloading textures if the GL object has been recreated.
     * 
     */
    public void reload() {
        saveKeys();
        deleteAll();

        TextureData tempData;
        for (int i = 0; i < previousKeys.size(); i++) {
            tempData = (TextureData)previousKeys.get(i);
            
            if(tempData.name.endsWith(".tga")) {
            	loadTexture(tempData.name,tempData.minFilter,
            			tempData.magFilter,tempData.mipmapped);
            }
            
            loadImage(
            	tempData.name,
                tempData.image,
                tempData.minFilter,
                tempData.magFilter,
                tempData.mipmapped);
        }
    }

    /**
     * <code>batchLoad</code> loads a collection of textures defined by an
     * <code>ArrayList</code>. 
     * 
     * @param keys the list of files to open.
     */
    public void batchLoad(ArrayList keys) {

        TextureData tempData;
        for (int i = 0; i < keys.size(); i++) {
            tempData = (TextureData)keys.get(i);
            loadImage(
            	tempData.name,
				tempData.image,
                tempData.minFilter,
                tempData.magFilter,
                tempData.mipmapped);
        }
    }

    /**
     * <code>saveKeys</code> makes a copy of all the texture keys (file names).
     * This is useful for reloading prior to deleting all textures. 
     * 
     * @return an <code>ArrayList</code> of the keys.
     */
    public ArrayList saveKeys() {
        previousKeys = (ArrayList)keyList.clone();
        return previousKeys;
    }

    /**
     * <code>loadTexture</code> loads a new texture defined by a loaded 
     * ImageIcon. If a texture with the same filename has previously been loaded,
     * that id is returned rather than reloading. Filter parameters are used
     * to define the filtering of the texture. Whether the texture is to be
     * mipmapped or not is denoted by the isMipmapped boolean flag. If there
     * is an error loading the file, -1 is returned.
     * 
     * @param image the ImageIcon of the texture image.
     * @param minFilter the filter for the near values.
     * @param magFilter the filter for the far values.
     * @param isMipmapped determines if we will load the texture mipmapped
     *      or not. True load the texture mipmapped, false do not.
     * 
     * @return an integer for the loaded texture id. If there is a problem
     *      loading the texture -1 is returned.
     */
    public int loadTexture(
        ImageIcon image,
        int minFilter,
        int magFilter,
        boolean isMipmapped) {
        
        //check if the texture is already loaded.  
        Object obj = textureList.get(image.getDescription());
        if (obj != null) {
            //was previously loaded, so return it.
            return ((Integer)obj).intValue();
        }    
    
        return loadImage(
            image.getDescription(),
            image.getImage(),
            minFilter,
            magFilter,
            isMipmapped);

    }
    
    /**
     * <code>loadTexture</code> loads a new texture defined by the parameter
     * string. If a texture with the same filename has previously been loaded,
     * that id is returned rather than reloading. Filter parameters are used
     * to define the filtering of the texture. Whether the texture is to be
     * mipmapped or not is denoted by the isMipmapped boolean flag. If there
     * is an error loading the file, -1 is returned.
     * 
     * @param file the filename of the texture image.
     * @param minFilter the filter for the near values.
     * @param magFilter the filter for the far values.
     * @param isMipmapped determines if we will load the texture mipmapped
     *      or not. True load the texture mipmapped, false do not.
     * 
     * @return an integer for the loaded texture id. If there is a problem
     *      loading the texture -1 is returned.
     */
    public int loadTexture(
        String file,
        int minFilter,
        int magFilter,
        boolean isMipmapped) {

        //check if the texture is already loaded.  
        Object obj = textureList.get(file);
        if (obj != null) {
            //was previously loaded, so return it.
            return ((Integer)obj).intValue();
        }

        Image image = null;

        if (".TGA".equalsIgnoreCase(file.substring(file.indexOf('.')))) {
            //Load the TGA file
            image = loadTGAImage(file);
        } else if(".BMP".equalsIgnoreCase(file.substring(file.indexOf('.')))) {
        	image = loadBMPImage(file);
        } else {
            //Load the new image.
            image = (new javax.swing.ImageIcon(file)).getImage();
        }

        if (null == image) {
            LoggingSystem.getLoggingSystem().getLogger().log(
                Level.WARNING,
                "Could not load" + file);
            return -1;
        }

        return loadImage(file, image, minFilter, magFilter, isMipmapped);

    }

    /**
     * <code>getTexture</code> retrieves a texture id based on the filename. 
     * Use this method if you are certain that the file has previously been
     * loaded using <code>loadTexture</code>. If the texture has not been 
     * loaded previously -1 is returned as the id.
     * 
     * @param file the filename to retrieve the id of.
     * 
     * @return the id of the given filename. If the given filename is not
     *      loaded, -1 is returned.
     */
    public int getTexture(String file) {
        //check for the id in the hashmap using file as the key
        Object obj = textureList.get(file);
        if (obj != null) {
            return ((Integer)obj).intValue();
        }

        //error
        return -1;
    }

    /**
     * <code>bind</code> sets the current texture to that defined by the 
     * filename key. The filename must have been previously loaded using
     * the <code>loadTexture</code> method. If the texture is bound correctly,
     * true is returned, otherwise false is returned.
     * 
     * @param file the filename key to bind.
     * @return true is the bind was successful false otherwise.
     */
    public boolean bind(String file) {
        //check for texture
        Object obj = textureList.get(file);
        if (obj != null) {
            //bind it
            int id = ((Integer)obj).intValue();

            if (id != boundID) {
            	boundID = id;
                gl.bindTexture(GL.TEXTURE_2D, id);
            }

            return true;
        }

        //error
        return false;
    }

    /**
     * <code>bind</code> binds a texture based on a given id. It is up to the
     * client to insure that the id is valid. If unsure about the id value, use
     * <code>bind(String)</code> with the texture filename as the key. If the
     * id is valid, the texture is bound, otherwise it is not. There is not
     * success check.
     * 
     * 
     * @param id the texture id to bind.
     */
    public void bind(int id) {
        if (id != boundID) {
        	boundID = id;
            gl.bindTexture(GL.TEXTURE_2D, id);
        }
    }

    /**
     * <code>deleteTexture</code> removes the texture from video memory. The
     * texture removed is defined by the filename key. If the texture file
     * has not been loaded or has previously been deleted false is returned. 
     * True is returned if the texture was successfully removed.
     * 
     * @param file the texture filename to remove.
     * 
     * @return true is successful false otherwise.
     */
    public boolean deleteTexture(String file) {
        Object obj = textureList.get(file);
        if (obj != null) {
            int id = ((Integer)obj).intValue();

            IntBuffer buf =
                ByteBuffer
                    .allocateDirect(4)
                    .order(ByteOrder.nativeOrder())
                    .asIntBuffer();
            buf.put(id);
            int bufPtr = Sys.getDirectBufferAddress(buf);

            gl.deleteTextures(1, bufPtr);
            textureList.remove(file);
            return true;
        }

        keyList.remove(file);

        return false;
    }

    /**
     * <code>deleteAll</code> removes all textures from the video memory as
     * well as the <code>TextureManager</code> list. This effectively "resets"
     * the manager.
     */
    public void deleteAll() {
        LoggingSystem.getLoggingSystem().getLogger().log(
            Level.INFO,
            "Deleting All Textures");
        int id;
        String key;
        for (int i = 0; i < keyList.size(); i++) {
            key = ((TextureData)keyList.get(i)).name;
            id = ((Integer)textureList.get(key)).intValue();
            if (gl.isTexture(id)) {
                IntBuffer buf =
                    ByteBuffer
                        .allocateDirect(4)
                        .order(ByteOrder.nativeOrder())
                        .asIntBuffer();
                buf.put(id);
                int bufPtr = Sys.getDirectBufferAddress(buf);

                gl.deleteTextures(1, bufPtr);
            }
        }

        textureList.clear();
        keyList.clear();
    }

    /**
     * <code>getNumberOfTextures</code> returns how many textures are currently
     * being maintained by the <code>TextureManager</code>.
     * 
     * @return the number of textures being maintained.
     */
    public int getNumberOfTextures() {
        return textureList.size();
    }

    /**
     * <code>getTextureManager</code> retrieves the instance of 
     * <code>TextureManager</code>.
     * 
     * @return the single instance of <code>TextureManager</code>.
     */
    public static TextureManager getTextureManager() {
        if (null == instance) {
            instance = new TextureManager();
            return instance;
        } else {
            return instance;
        }
    }

    /**
     * <code>reset</code> clears all textures from the list and sets the 
     * instance to null. This will guarantee that the next call to 
     * <code>getTexutreManager</code> will create a new instance of 
     * <code>TextureManager</code>
     */
    public static void reset() {
        if (null != instance) {
            instance.deleteAll();
            instance = null;
        }
    }

    /**
     * 
     * <code>loadImage</code> sets the image data to a texture using
     * OpenGL's cababilities.
     * 
     * @param file The name of the texture (filename usually).
     * @param image The image data.
     * @param minFilter minimum filter
     * @param magFilter maximum filter
     * @param isMipmapped whether the texture should be mipmapped or not.
     * @return the assigned id of the texture.
     */
    private int loadImage(
        String file,
        Image image,
        int minFilter,
        int magFilter,
        boolean isMipmapped) {
        //      Obtain the image data.
        BufferedImage tex = null;
        try {
            tex =
	            new BufferedImage(
	                image.getWidth(null),
	                image.getHeight(null),
	                BufferedImage.TYPE_3BYTE_BGR);
        } catch(IllegalArgumentException e) {
        	LoggingSystem.getLoggingSystem().getLogger().log(Level.WARNING,
        			"Could not load image file " + file);
        	return -1;
        }
        Graphics2D g = (Graphics2D)tex.getGraphics();
        g.drawImage(image, null, null);
        g.dispose();

        //Flip the image
        AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
        tx.translate(0, -image.getHeight(null));
        AffineTransformOp op =
            new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        tex = op.filter(tex, null);

        //Get a pointer to the image memory
        ByteBuffer scratch =
            ByteBuffer.allocateDirect(4 * tex.getWidth() * tex.getHeight());
        int dataAddress = Sys.getDirectBufferAddress(scratch);

        byte data[] =
            (byte[])tex.getRaster().getDataElements(
                0,
                0,
                tex.getWidth(),
                tex.getHeight(),
                null);
        scratch.clear();
        scratch.put(data);

        //Create A IntBuffer For Image Address In Memory     
        IntBuffer buf =
            ByteBuffer
                .allocateDirect(4)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer();
        int bufPtr = Sys.getDirectBufferAddress(buf);

        //Create the texture
        gl.genTextures(1, bufPtr);

        gl.bindTexture(GL.TEXTURE_2D, buf.get(0));

        // Linear Filtering
        gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MIN_FILTER, minFilter);
        // Linear Filtering
        gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MAG_FILTER, magFilter);

        if (isMipmapped) {
            //generate the mipmaps
            glu.build2DMipmaps(
                GL.TEXTURE_2D,
                3,
                tex.getWidth(),
                tex.getHeight(),
                GL.RGB,
                GL.UNSIGNED_BYTE,
                dataAddress);
        } else {
            // Generate The Texture
            gl.texImage2D(
                GL.TEXTURE_2D,
                0,
                GL.RGB,
                tex.getWidth(),
                tex.getHeight(),
                0,
                GL.RGB,
                GL.UNSIGNED_BYTE,
                dataAddress);
        }

        LoggingSystem.getLoggingSystem().getLogger().log(
            Level.INFO,
            "Successfully loaded " + file);
        //add to our lists.
        textureList.put(file, new Integer(buf.get(0)));
        keyList.add(new TextureData(image, file, minFilter, magFilter, isMipmapped));

        return buf.get(0);

    }
    
    /**
    * <code>loadBMPImage</code> because bitmap is not directly supported by
    * Java, we must load it manually. The requires opening a stream to the
    * file and reading in each byte. After the image data is read, it is
    * used to create a new <code>Image</code> object. This object is
    * returned to be used for normal use.
    * 
    * @param file the name of the bitmap file.
    * 
    * @return <code>Image</code> object that contains the bitmap information.
    */
	private Image loadBMPImage(String file) {
		
			try {
				FileInputStream fs = new FileInputStream(file);
				BitmapHeader bh = new BitmapHeader();
				bh.read(fs);

				if (bh.bitcount == 24)
					return (bh.readMap24(fs, bh));

				if (bh.bitcount == 32)
					return (bh.readMap32(fs, bh));

				if (bh.bitcount == 8)
					return (bh.readMap8(fs, bh));

				fs.close();
			} catch (IOException e) {
				System.err.println("Error while loading " + file);
				System.exit(1);
			}

			return null;
		}

    /**
     * <code>loadTGAImage</code> because targa is not directly supported by
     * Java, we must load it manually. The requires opening a stream to the
     * file and reading in each byte. After the image data is read, it is
     * used to create a new <code>Image</code> object. This object is
     * returned to be used for normal use.
     * 
     * @param file the name of the targa file.
     * 
     * @return <code>Image</code> object that contains the targa information.
     */
    private Image loadTGAImage(String file) {
        try {
            int red = 0;
            int green = 0;
            int blue = 0;
            int srcLine = 0;
            int alpha = FULL_TRANSPARENCY;

            //open a stream to the file
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis, 8192);
            DataInputStream dis = new DataInputStream(bis);

            //Read the TGA header
            idLength = (short)dis.read();
            colorMapType = (short)dis.read();
            imageType = (short)dis.read();
            cMapStart = (int)flipEndian(dis.readShort());
            cMapLength = (int)flipEndian(dis.readShort());
            cMapDepth = (short)dis.read();
            xOffset = (int)flipEndian(dis.readShort());
            yOffset = (int)flipEndian(dis.readShort());
            width = (int)flipEndian(dis.readShort());
            height = (int)flipEndian(dis.readShort());
            pixelDepth = (short)dis.read();

            if (pixelDepth == 24) {
                cm = new DirectColorModel(24, 0xFF0000, 0xFF00, 0xFF);
            } else if (pixelDepth == 32) {
                cm =
                    new DirectColorModel(
                        32,
                        0xFF000000,
                        0xFF0000,
                        0xFF00,
                        0xFF);
            }

            imageDescriptor = (short)dis.read();

            //Skip image ID
            if (idLength > 0) {
                bis.skip(idLength);
            }

            //create the buffer for the image data.
            pixels = new int[width * height];

            //read the pixel data.
            for (int i = (height - 1); i >= 0; i--) {
                srcLine = i * width;

                for (int j = 0; j < width; j++) {
                    blue = bis.read() & 0xFF;
                    green = bis.read() & 0xFF;
                    red = bis.read() & 0xFF;

                    if (pixelDepth == 32) {
                        alpha = bis.read() & 0xFF;
                        pixels[srcLine + j] =
                            alpha << 24 | red << 16 | green << 8 | blue;
                    } else {
                        pixels[srcLine + j] = red << 16 | green << 8 | blue;
                    }
                }
            }

            //Close the file, we are done.
            fis.close();
        } catch (IOException e) {
            LoggingSystem.getLoggingSystem().getLogger().log(
                Level.WARNING,
                "Unable to load " + file);
            throw new MonkeyRuntimeException("Could not load targa file");
        }

        //create the Image object and return it.
        return Toolkit.getDefaultToolkit().createImage(
            new MemoryImageSource(width, height, cm, pixels, 0, width));
    }

    /**
     * <code>flipEndian</code> is used to flip the endian bit of the header file.
     * @param signedShort the bit to flip.
     * 
     * @return the flipped bit.
     */
    private short flipEndian(short signedShort) {
        int input = signedShort & 0xFFFF;
        return (short) (input << 8 | (input & 0xFF00) >>> 8);
    }
    
    /**
     * <code>BitmapHeader</code> contains all Bitmap image header information.
     * This class is used to load the BMP data. The class also handles 
     * retrieving the data for different depth sizes, that is, 32, 24 and 8.
     */
	private class BitmapHeader {
			public int size;
			public int bisize;
			public int width;
			public int height;
			public int planes;
			public int bitcount;
			public int compression;
			public int sizeimage;
			public int xpm;
			public int ypm;
			public int clrused;
			public int clrimp;
		
			private Image readMap32(FileInputStream fs, BitmapHeader bh)
					throws IOException {
					Image image;
					int xwidth = bh.sizeimage / bh.height;
					int ndata[] = new int[bh.height * bh.width];
					byte brgb[] = new byte[bh.width * 4 * bh.height];
					fs.read(brgb, 0, bh.width * 4 * bh.height);
					int nindex = 0;

					for (int j = 0; j < bh.height; j++) {
						for (int i = 0; i < bh.width; i++) {
							ndata[bh.width * (bh.height - j - 1) + i] =
								constructInt3(brgb, nindex);
							nindex += 4;
						}
					}

					image =
						Toolkit.getDefaultToolkit().createImage(
							new MemoryImageSource(
								bh.width,
								bh.height,
								ndata,
								0,
								bh.width));
					fs.close();
					return (image);
				}

				private Image readMap24(FileInputStream fs, BitmapHeader bh)
					throws IOException {
					Image image;
					int npad = (bh.sizeimage / bh.height) - bh.width * 3;
					int ndata[] = new int[bh.height * bh.width];
					byte brgb[] = new byte[(bh.width + npad) * 3 * bh.height];
					fs.read(brgb, 0, (bh.width + npad) * 3 * bh.height);
					int nindex = 0;

					for (int j = 0; j < bh.height; j++) {
						for (int i = 0; i < bh.width; i++) {
							ndata[bh.width * (bh.height - j - 1) + i] =
								constructInt3(brgb, nindex);
							nindex += 3;
						}
						nindex += npad;
					}

					image =
						Toolkit.getDefaultToolkit().createImage(
							new MemoryImageSource(
								bh.width,
								bh.height,
								ndata,
								0,
								bh.width));
					fs.close();
					return image;
				}

				private Image readMap8(FileInputStream fs, BitmapHeader bh)
					throws IOException {
					Image image;
					int nNumColors = 0;

					if (bh.clrused > 0) {
						nNumColors = bh.clrused;
					} else {
						nNumColors = (1 & 0xff) << bh.bitcount;
					}

					if (bh.sizeimage == 0) {
						bh.sizeimage = ((((bh.width * bh.bitcount) + 31) & ~31) >> 3);
						bh.sizeimage *= bh.height;
					}

					int npalette[] = new int[nNumColors];
					byte bpalette[] = new byte[nNumColors * 4];
					fs.read(bpalette, 0, nNumColors * 4);
					int nindex8 = 0;

					for (int n = 0; n < nNumColors; n++) {
						npalette[n] = constructInt3(bpalette, nindex8);
						nindex8 += 4;
					}

					int npad8 = (bh.sizeimage / bh.height) - bh.width;
					int ndata8[] = new int[bh.width * bh.height];
					byte bdata[] = new byte[(bh.width + npad8) * bh.height];
					fs.read(bdata, 0, (bh.width + npad8) * bh.height);
					nindex8 = 0;

					for (int j8 = 0; j8 < bh.height; j8++) {
						for (int i8 = 0; i8 < bh.width; i8++) {
							ndata8[bh.width * (bh.height - j8 - 1) + i8] =
								npalette[((int) bdata[nindex8] & 0xff)];
							nindex8++;
						}

						nindex8 += npad8;
					}

					image =
						Toolkit.getDefaultToolkit().createImage(
							new MemoryImageSource(
								bh.width,
								bh.height,
								ndata8,
								0,
								bh.width));

					return image;
				}
		
			/* Builds an int from a byte array - convert little to big endian.
				 */

				private int constructInt(byte[] in, int offset) {
					int ret = ((int) in[offset + 3] & 0xff);
					ret = (ret << 8) | ((int) in[offset + 2] & 0xff);
					ret = (ret << 8) | ((int) in[offset + 1] & 0xff);
					ret = (ret << 8) | ((int) in[offset + 0] & 0xff);
					return (ret);
				}

				/* Builds an int from a byte array - convert little to big endian 
				 * set high order bytes to 0xfff.
				 */

				private int constructInt3(byte[] in, int offset) {
					int ret = 0xff;
					ret = (ret << 8) | ((int) in[offset + 2] & 0xff);
					ret = (ret << 8) | ((int) in[offset + 1] & 0xff);
					ret = (ret << 8) | ((int) in[offset + 0] & 0xff);
					return (ret);
				}

				/* Builds an int from a byte array - convert little to big endian.
				 */

				private long constructLong(byte[] in, int offset) {
					long ret = ((long) in[offset + 7] & 0xff);
					ret |= (ret << 8) | ((long) in[offset + 6] & 0xff);
					ret |= (ret << 8) | ((long) in[offset + 5] & 0xff);
					ret |= (ret << 8) | ((long) in[offset + 4] & 0xff);
					ret |= (ret << 8) | ((long) in[offset + 3] & 0xff);
					ret |= (ret << 8) | ((long) in[offset + 2] & 0xff);
					ret |= (ret << 8) | ((long) in[offset + 1] & 0xff);
					ret |= (ret << 8) | ((long) in[offset + 0] & 0xff);
					return (ret);
				}

				/* Builds an double from a byte array - convert little to big endian.
				 */

				private double constructDouble(byte[] in, int offset) {
					long ret = constructLong(in, offset);
					return (Double.longBitsToDouble(ret));
				}

				/* Builds an short from a byte array - convert little to big endian.
				 */

				private short constructShort(byte[] in, int offset) {
					short ret = (short) ((short) in[offset + 1] & 0xff);
					ret = (short) ((ret << 8) | (short) ((short) in[offset + 0] & 0xff));
					return (ret);
				}

			protected final void read(FileInputStream fs) throws IOException {
				final int bflen = 14;
				byte bf[] = new byte[bflen];
				fs.read(bf, 0, bflen);
				final int bilen = 40;
				byte bi[] = new byte[bilen];
				fs.read(bi, 0, bilen);

				size = constructInt(bf, 2);
				bisize = constructInt(bi, 2);
				width = constructInt(bi, 4);
				height = constructInt(bi, 8);
				planes = constructShort(bi, 12);
				bitcount = constructShort(bi, 14);
				compression = constructInt(bi, 16);
				sizeimage = constructInt(bi, 20);
				xpm = constructInt(bi, 24);
				ypm = constructInt(bi, 28);
				clrused = constructInt(bi, 32);
				clrimp = constructInt(bi, 36);
			}
		}

    /**
     * <code>TextureData</code> maintains the file information for a targa
     * image file.
     * 
     */
    private class TextureData {
    	public Image image;
    	public String name;
    	public int minFilter;
        public int magFilter;
        public boolean mipmapped;

        public TextureData(Image i, String n, int min, int mag, boolean mip) {
            image = i;
            name = n;
            minFilter = min;
            magFilter = mag;
            mipmapped = mip;
        }
    }
}
