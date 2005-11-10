package com.jmex.awt.swingui;

import java.awt.Graphics2D;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.system.DisplaySystem;
import com.jme.system.lwjgl.LWJGLDisplaySystem;

/**
 * This abstract class provides methods to paint on a {@link com.jme.image.Image} via the awt {@link Graphics2D}.
 */
public abstract class ImageGraphics extends Graphics2D {

    /**
     * @param width of the image
     * @param height of the image
     * @param paintedMipMapCount number of mipmaps that are painted, rest is drawn by image copying, 0 for no mipmaps,
     *                           1 for a single image painted and mipmaps copied, higher values respective
     * @return a new instance of ImageGraphics matching the display system.
     */
    public static ImageGraphics createInstance( int width, int height, int paintedMipMapCount ) {
        if ( DisplaySystem.getDisplaySystem() instanceof LWJGLDisplaySystem ) {
            return new LWJGLImageGraphics( width, height, paintedMipMapCount );
        }
        else {
            throw new UnsupportedOperationException( "No ImageGraphics implementation " +
                    "for display system '" + DisplaySystem.getDisplaySystem() + "' found!" );
        }
    }

    /**
     * where painting in {@link #update()} goes to.
     */
    protected final com.jme.image.Image image;

    /**
     * Protected ctor for subclasses.
     *
     * @param image where painting in {@link #update()} goes to.
     */
    protected ImageGraphics( Image image ) {
        this.image = image;
    }

    /**
     * @return image where painting in {@link #update()} goes to
     * @see #update()
     */
    public com.jme.image.Image getImage() {
        return image;
    }

    /**
     * Update a texture that contains the image from {@link #getImage()}. Only dirty areas are updated. The texture must
     * have mipmapping turned off ({@link Texture#MM_NONE}). The whole area is cleaned (dirty markers removed).
     *
     * @param texture texture to be updated
     */
    public void update( Texture texture ) {
        update( texture, true );
    }

    /**
     * Update a texture that contains the image from {@link #getImage()}. Only dirty areas are updated. The texture must
     * have mipmapping turned off ({@link Texture#MM_NONE}).
     *
     * @param texture texture to be updated
     * @param clean   true to mark whole area as clean after updating, false to keep dirty area for updating more textures
     */
    public abstract void update( Texture texture, boolean clean );

    /**
     * Updates the image data.
     *
     * @see #getImage()
     */
    public abstract void update();

    /**
     * @return true if image/texture needs update
     */
    public abstract boolean isDirty();
}
