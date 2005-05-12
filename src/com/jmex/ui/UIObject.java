/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding
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

/*
 * Created on Jun 8, 2004
 *
 */
package com.jmex.ui;

import java.util.*;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.shape.*;
import com.jme.scene.state.*;
import com.jme.scene.*;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;

/**
 * @author schustej
 *
 */
public abstract class UIObject extends Node {

    /*
     * Some flag settings for components
     */
    public static final int BORDER = 1;
    public static final int INVERSE_BORDER = 2;
    
    /*
     * Mutually exclusive with border settings, you can do textures
     * or you can do border based controls
     */
    public static final int TEXTURE = 0x00004;
    
    /*
     * just some defaults
     */
    protected int _x = 200;
    protected int _y = 200;
    protected int _width = 0;
    protected int _height = 0;
    
    /*
     * The color scheme to use
     */
    UIColorScheme _scheme = null;
    
    /*
     * The flag settings 
     */
    int _flags = 0;
    
    /*
     * If using image based rendering, these are the texture states.
     */
    protected Vector _textureStates = null;

    /*
     * The base quad to use when creating image based
     * controls
     */
    protected Quad _quad = null;
    
    /*
     * Use for creating the border for the ui component
     */
    protected Line _topborder = null;
    protected Line _rightborder = null;
    protected Line _bottomborder = null;
    protected Line _leftborder = null;

    public UIObject(String name, 
            int x, int y, 
            int width, int height,
            UIColorScheme scheme, int flags) {
        super(name);
        
        /*
         * Set the local variables
         */
        _x = x;
        _y = y;
        _width = width;
        _height = height;
        _scheme = scheme;
        _flags = flags;

        /*
         * Check for invalids
         */
        if( _x < 0 || _y < 0 || _width < 0 || _height < 0) {
            throw new JmeException( "Invalid size or location for UI component");
        }
        
        if( usingBorders() && usingTexture() || 
            usingStdBorder() && usingInverseBorder() ) {
            throw new JmeException( "Invalid flag settings");
        }

        _textureStates = new Vector();
        
    }

    /**
     * This is used in all extention constructors.
     *
     * Note that a Quad's vertexes are based on the center of the quad, so we have to
     * account for that in the translations. However, the hitarea is from the lower left corner.
     *
     */
    protected void setup() {

        /*
         * Create the alpha state to allow black to equal transparent
         */
        AlphaState as1 = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
        as1.setBlendEnabled(true);
        as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as1.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
        as1.setTestEnabled(true);
        as1.setTestFunction(AlphaState.TF_GREATER);

        this.setRenderState(as1);
        
        /*
         * Create with border or not
         */
        if( usingTexture()) {
            
            /*
             * WITH TEXTURES 
             */
            
            //System.out.println( "With Texture: " + name + " x: " + _x + " y: " + _y + " w: " + _width + " h:" + _height);
            
	        _quad = new Quad( name+"_quad", _width, _height);
        
	        /*
	         * Use the default texture state if there is one right now...
	         * we can always add one later.
	         */
	        if( _textureStates != null && _textureStates.size() > 0 && _textureStates.elementAt(0) != null) {
	            _quad.setRenderState( ((TextureState) _textureStates.elementAt(0)));
	        }
	        
	        this.attachChild( _quad);
	        
        } else if( usingBorders() ){
            
            /*
             * WITH BORDER 
             */
            
            //System.out.println( "With Border: " + name + " x: " + _x + " y: " + _y + " w: " + _width + " h:" + _height);
            
	        /*
	         * Create the base quad
	         */
	        _quad = new Quad( name+"_quad", (_width - 1), (_height - 1));
	        _quad.setColor( 0, _scheme._backgroundcolor);
	        _quad.setColor( 1, _scheme._backgroundcolor);
	        _quad.setColor( 2, _scheme._backgroundcolor);
	        _quad.setColor( 3, _scheme._backgroundcolor);
        

	        /*
	         * Create the upper part of the border
	         */
	        
	        _topborder = createTBorder();
	        _topborder.setLocalTranslation( new Vector3f( _x + (_width / 2), _y + (_height / 2), 0.0f) );
	        this.attachChild( _topborder);
	        
	        /*
	         * Create right part of the border
	         */
	        _rightborder = createRBorder();
	        _rightborder.setLocalTranslation( new Vector3f( _x + (_width / 2), _y + (_height / 2), 0.0f) );
	        this.attachChild( _rightborder);
	        
	        /*
	         * Create bottom part of the border
	         */
	        _bottomborder = createBBorder();
	        _bottomborder.setLocalTranslation( new Vector3f( _x + (_width / 2), _y + (_height / 2), 0.0f) );
	        this.attachChild( _bottomborder);
	        
	        /*
	         * Create left part of the border
	         */
	        _leftborder = createLBorder();
	        _leftborder.setLocalTranslation( new Vector3f( _x + (_width / 2), _y + (_height / 2), 0.0f) );
	        this.attachChild( _leftborder);

	        this.attachChild( _quad);
	        
        }
        
        
        /*
         * because quads are created based on their center...
         */
        if( _quad != null) {
            _quad.setLocalTranslation( new Vector3f( _x + (_width / 2), _y + (_height / 2), 0.0f) );
        }
        
        setRenderQueueMode( Renderer.QUEUE_ORTHO);
    }

    /**
     * Creates the upper border via the Line geometry
     * @return
     */
    private Line createTBorder() {
        
        Vector3f[] verts = new Vector3f[3];
        Vector3f[] norms = new Vector3f[3];
        ColorRGBA[] colors = new ColorRGBA[3];

        verts[0] = new Vector3f( -_width/2f, _height/2f, 0);
        verts[1] = new Vector3f( _width/2f, _height/2f, 0);
        
        norms[0] = new Vector3f(0,0,1);
        norms[1] = new Vector3f(0,0,1);
        
        if( (BORDER & _flags) == BORDER) {
            colors[0] = _scheme._borderlightcolor;
            colors[1] = _scheme._borderlightcolor;
        } else {
            colors[0] = _scheme._borderdarkcolor;
            colors[1] = _scheme._borderdarkcolor;
        }
        
        return new Line( name+"_tborder", verts, norms, colors, null);
    }
    
    /**
     * Creates the upper border via the Line geometry
     * @return
     */
    private Line createLBorder() {
        
        Vector3f[] verts = new Vector3f[3];
        Vector3f[] norms = new Vector3f[3];
        ColorRGBA[] colors = new ColorRGBA[3];

        verts[0] = new Vector3f( -_width/2f, _height/2f, 0);
        verts[1] = new Vector3f( -_width/2f, -_height/2f, 0);
        
        norms[0] = new Vector3f(0,0,1);
        norms[1] = new Vector3f(0,0,1);
        
        if( (BORDER & _flags) == BORDER) {
            colors[0] = _scheme._borderlightcolor;
            colors[1] = _scheme._borderlightcolor;
        } else {
            colors[0] = _scheme._borderdarkcolor;
            colors[1] = _scheme._borderdarkcolor;
        }
        
        return new Line( name+"_lborder", verts, norms, colors, null);
    }
    
    /**
     * Creates the lower right border via the Line geometry
     * @return
     */
    private Line createBBorder() {
        
        Vector3f[] verts = new Vector3f[3];
        Vector3f[] norms = new Vector3f[3];
        ColorRGBA[] colors = new ColorRGBA[3];

        verts[0] = new Vector3f( -_width/2f, -_height/2f, 0);
        verts[1] = new Vector3f( _width/2f, -_height/2f, 0);
        
        norms[0] = new Vector3f(0,0,1);
        norms[1] = new Vector3f(0,0,1);
        
        if( (BORDER & _flags) == BORDER) {
            colors[0] = _scheme._borderdarkcolor;
            colors[1] = _scheme._borderdarkcolor;
        } else {
            colors[0] = _scheme._borderlightcolor;
            colors[1] = _scheme._borderlightcolor;
        }
        
        return new Line( name+"_bborder", verts, norms, colors, null);
    }
    
    /**
     * Creates the lower right border via the Line geometry
     * @return
     */
    private Line createRBorder() {
        
        Vector3f[] verts = new Vector3f[3];
        Vector3f[] norms = new Vector3f[3];
        ColorRGBA[] colors = new ColorRGBA[3];

        verts[0] = new Vector3f( _width/2f, -_height/2f, 0);
        verts[1] = new Vector3f( _width/2f, _height/2f, 0);
        
        norms[0] = new Vector3f(0,0,1);
        norms[1] = new Vector3f(0,0,1);
        
        if( (BORDER & _flags) == BORDER) {
            colors[0] = _scheme._borderdarkcolor;
            colors[1] = _scheme._borderdarkcolor;
        } else {
            colors[0] = _scheme._borderlightcolor;
            colors[1] = _scheme._borderlightcolor;
        }
        
        return new Line( name+"_rborder", verts, norms, colors, null);
    }
    
    /**
     * gets the width of the control, before scaling
     * @return
     */
    public int getWidth() {
        return _width;
    }

    /**
     * gets the height of the control, before scaling
     * @return
     */
    public int getHeight() {
        return _height;
    }

    /**
     * Moves the quad around the screen, properly setting local params at the
     * same time.
     */
    public void setLocation(int x, int y) {
      _x = x;
      _y = y;
      setLocalTranslation(new Vector3f(_x + _width / 2, _y + _height / 2,
                                       0.0f));
    }
    
    /**
     * Places the center of the control at the given screen location
     * @return
     */
    public void centerAt(int x, int y) {
        this.localTranslation.set( x, y, 0);

        _x = (int) (x - _width / 2);
        _y = (int) (y - _height / 2);
    }

    public boolean usingTexture() {
        return ( (TEXTURE & _flags) == TEXTURE);
    }
    
    public boolean usingBorders() {
        return ( ((BORDER & _flags) == BORDER) || (INVERSE_BORDER & _flags) == INVERSE_BORDER);
    }

    public boolean usingStdBorder() {
        return ( ((BORDER & _flags) == BORDER));
    }
    
    public boolean usingInverseBorder() {
        return ( (INVERSE_BORDER & _flags) == INVERSE_BORDER);
    }
}
