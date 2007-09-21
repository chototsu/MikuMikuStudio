package com.jmex.font3d.effects;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.util.geom.BufferUtils;
import com.jmex.font3d.Font3D;
import com.jmex.font3d.Glyph3D;
import com.jmex.font3d.Glyph3DBatch;

public class Font3DGradient implements Font3DEffect
{
	private Vector3f direction = new Vector3f();
	ColorRGBA start_color = new ColorRGBA();
	ColorRGBA end_color = new ColorRGBA();
	
	public Font3DGradient()
	{
		this(Vector3f.UNIT_Y.clone(), ColorRGBA.white.clone(), ColorRGBA.red.clone());
	}
	
	public Font3DGradient(Vector3f direction, ColorRGBA start_color, ColorRGBA end_color)
	{
		this.direction.set(direction);
		this.start_color.set(start_color);
		this.end_color.set(end_color);
	}

	public void applyEffect(Font3D font)
	{
		boolean mesh_locked = font.isMeshLocked();
		if(mesh_locked)
		{
    		font.unlockMesh();
		}
		
		// We must add a material-state to use lighting and vertex-colours at the same time
		font.enableDiffuseMaterial();
		// does any of these contain any alpha ?
		if(start_color.a != 1 || end_color.a != 1)
		{
			font.enableAlphaState();
		}
		
		// Get the min and max
		for(Glyph3D g : font.getGlyphs())
		{
			if(g != null && g.getBatch() != null)
			{
				applyEffect(g.getBatch());
			}
		}
		
		
		// If it was locked, lock it again.
    	if(mesh_locked)
    	{
    		font.lockMesh();
    	}
	}

	private void applyEffect(Glyph3DBatch batch)
	{
		// Calculate the max/min of the vertices in the batch.
		Vector3f max = null,min = null;
		Vector3f[] verts = BufferUtils.getVector3Array(batch.getVertexBuffer());
		for(Vector3f v : verts)
		{
			if(max == null || direction.dot(v) > direction.dot(max))
			{
				max = v;
			}
			if(min == null || direction.dot(v) < direction.dot(min))
			{
				min = v;
			}
		}
		float max_dot = direction.dot(max), min_dot = direction.dot(min);
		float dot_dist = max_dot - min_dot;
		
		// Create a color-array
		int color_pos = 0;
		ColorRGBA[] colors = new ColorRGBA[verts.length];
		
		// Iterate through all the vertices and create the colours
		for(Vector3f v : verts)
		{
			float dot_val = direction.dot(v);
			ColorRGBA c = colors[color_pos] = new ColorRGBA(start_color);
			c.interpolate(end_color, (dot_val-min_dot)/dot_dist);
			//logger.info("c:"+c);
			color_pos++;
		}
		
		// Apply the colors
		batch.setColorBuffer(BufferUtils.createFloatBuffer(colors));
	}
}
