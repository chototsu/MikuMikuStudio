package com.jmex.font3d.effects;

import java.net.URL;

import com.jme.image.Texture;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.font3d.Font3D;

/**
 * This class will apply a texture to a font. The reason this
 * has to be done this way is because of some locking/unlocking and other
 * internal things of the Font3D/Text3D.
 * 
 * @author emanuel
 *
 */
public class Font3DTexture implements Font3DEffect
{
	private TextureState ts = null;
	
	public Font3DTexture()
	{
		ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
	}
	
	public Font3DTexture(TextureState ts)
	{
		this.ts = ts;
	}

	public Font3DTexture(Texture tex)
	{
		this();
		ts.setTexture(tex);
	}
	
	public Font3DTexture(URL texurl)
	{
		this();
		Texture tex = TextureManager.loadTexture(texurl, Texture.MM_LINEAR, Texture.FM_LINEAR);
        tex.setWrap(Texture.WM_WRAP_S_WRAP_T);
        ts.setTexture(tex);
	}

	public void applyEffect(Font3D font)
	{
		boolean mesh_locked = font.isMeshLocked();
		if(mesh_locked)
		{
    		font.unlockMesh();
		}
		
		// Apply the texture state
		font.getRenderTriMesh().setRenderState(ts);
		
		if(mesh_locked)
		{
    		font.lockMesh();
		}
	}
}
