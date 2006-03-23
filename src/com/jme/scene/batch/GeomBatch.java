package com.jme.scene.batch;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Spatial;
import com.jme.scene.VBOInfo;
import com.jme.scene.state.RenderState;
import com.jme.util.geom.BufferUtils;

public class GeomBatch {
	/** The number of vertexes in this geometry. */
	protected int vertQuantity = 0;

	/** The geometry's per vertex color information. */
	protected transient FloatBuffer colorBuf;

	/** The geometry's per vertex normal information. */
	protected transient FloatBuffer normBuf;

	/** The geometry's vertex information. */
	protected transient FloatBuffer vertBuf;

	/** The geometry's per Texture per vertex texture coordinate information. */
	protected transient ArrayList texBuf;

	/** The geometry's VBO information. */
	protected VBOInfo vboInfo;

	protected RenderState[] states = new RenderState[RenderState.RS_MAX_STATE];

	/**
	 * a place to internally save previous states setup before rendering this
	 * pass
	 */
	protected RenderState[] savedStates = new RenderState[RenderState.RS_MAX_STATE];

	protected boolean enabled = true;

	/**
	 * Non -1 values signal that drawing this scene should use the provided
	 * display list instead of drawing from the buffers.
	 */
	protected int displayListID = -1;

	public GeomBatch() {
		texBuf = new ArrayList(1);
		texBuf.add(null);
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setState(RenderState rs) {
		states[rs.getType()] = rs;
	}

	public RenderState[] getState() {
		return states;
	}

	public boolean applyStates() {
		boolean stateApplied = false;
		for (int x = RenderState.RS_MAX_STATE; --x >= 0;) {
			if (states[x] != null) {
				savedStates[x] = Spatial.enforcedStateList[x];
				
				if(Spatial.enforcedStateList[x] == null) {
					Spatial.enforcedStateList[x] = states[x];
					stateApplied = true;
				}
			}
		}
		
		return stateApplied;
	}

	public void resetStates() {
		for (int x = RenderState.RS_MAX_STATE; --x >= 0;) {
			if (states[x] != null) {
				Spatial.enforcedStateList[x] = savedStates[x];
			}
		}
	}

	public FloatBuffer getColorBuf() {
		return colorBuf;
	}

	public void setColorBuf(FloatBuffer colorBuf) {
		this.colorBuf = colorBuf;
	}

	public int getDisplayListID() {
		return displayListID;
	}

	public void setDisplayListID(int displayListID) {
		this.displayListID = displayListID;
	}

	public FloatBuffer getNormBuf() {
		return normBuf;
	}

	public void setNormBuf(FloatBuffer normBuf) {
		this.normBuf = normBuf;
	}

	public ArrayList getTexBuf() {
		return texBuf;
	}

	public void setTexBuf(ArrayList texBuf) {
		this.texBuf = texBuf;
	}

	public VBOInfo getVboInfo() {
		return vboInfo;
	}

	public void setVboInfo(VBOInfo vboInfo) {
		this.vboInfo = vboInfo;
	}

	public FloatBuffer getVertBuf() {
		return vertBuf;
	}

	public void setVertBuf(FloatBuffer vertBuf) {
		this.vertBuf = vertBuf;
		if (vertBuf != null)
			vertQuantity = vertBuf.capacity() / 3;
		else
			vertQuantity = 0;
	}

	public int getVertQuantity() {
		return vertQuantity;
	}

	public void setVertQuantity(int vertQuantity) {
		this.vertQuantity = vertQuantity;
	}

	public void clearTexBuffer() {
		texBuf.clear();

	}

	public void addTexCoordinates(FloatBuffer textureCoords) {
		texBuf.add(textureCoords);
	}

	public void resizeTextureIds(int i) {
		vboInfo.resizeTextureIds(i);
	}

	public void setVBOInfo(VBOInfo info) {
		vboInfo = info;
		if (vboInfo != null) {
			vboInfo.resizeTextureIds(texBuf.size());
		}
	}

	public void setSolidColor(ColorRGBA color) {
		if (colorBuf == null)
			colorBuf = BufferUtils.createColorBuffer(vertQuantity);

		colorBuf.rewind();
		for (int x = 0, cLength = colorBuf.remaining(); x < cLength; x += 4) {
			colorBuf.put(color.r);
			colorBuf.put(color.g);
			colorBuf.put(color.b);
			colorBuf.put(color.a);
		}
		colorBuf.flip();
	}

	public void setRandomColors() {
		if (colorBuf == null)
			colorBuf = BufferUtils.createColorBuffer(vertQuantity);

		for (int x = 0, cLength = colorBuf.capacity(); x < cLength; x += 4) {
			colorBuf.put(FastMath.nextRandomFloat());
			colorBuf.put(FastMath.nextRandomFloat());
			colorBuf.put(FastMath.nextRandomFloat());
			colorBuf.put(1);
		}
		colorBuf.flip();
	}

	public void copyTextureCoordinates(int fromIndex, int toIndex, float factor) {
		if (texBuf == null)
			return;

		if (fromIndex < 0 || fromIndex >= texBuf.size()
				|| texBuf.get(fromIndex) == null) {
			return;
		}

		if (toIndex < 0 || toIndex == fromIndex) {
			return;
		}

		if (toIndex >= texBuf.size()) {
			while (toIndex >= texBuf.size()) {
				texBuf.add(null);
			}
		}

		FloatBuffer buf = (FloatBuffer) texBuf.get(toIndex);
		FloatBuffer src = (FloatBuffer) texBuf.get(fromIndex);
		if (buf == null || buf.capacity() != src.capacity()) {
			buf = BufferUtils.createFloatBuffer(src.capacity());
			texBuf.set(toIndex, buf);
		}
		buf.clear();
		int oldLimit = src.limit();
		src.clear();
		for (int i = 0, len = buf.capacity(); i < len; i++) {
			buf.put(factor * src.get());
		}
		src.limit(oldLimit);
		buf.limit(oldLimit);

		if (vboInfo != null) {
			vboInfo.resizeTextureIds(this.texBuf.size());
		}
	}

	public FloatBuffer getTexBuf(int textureUnit) {
		if (texBuf == null)
			return null;
		if (textureUnit >= texBuf.size())
			return null;
		return (FloatBuffer) texBuf.get(textureUnit);
	}

	public void setTexBuf(FloatBuffer buff, int position) {
		if (position >= texBuf.size()) {
			while (position >= texBuf.size()) {
				texBuf.add(null);
			}
		}

		texBuf.set(position, buff);
		if (vboInfo != null) {
			vboInfo.resizeTextureIds(texBuf.size());
		}
	}

    public void translatePoints(float x, float y, float z) {
        translatePoints(new Vector3f(x,y,z));
    }

    public void translatePoints(Vector3f amount) {
        for (int x = 0; x < vertQuantity; x++) {
            BufferUtils.addInBuffer(amount, vertBuf, x);
        }
    }

    public void rotatePoints(Quaternion rotate) {
        Vector3f store = new Vector3f();
        for (int x = 0; x < vertQuantity; x++) {
            BufferUtils.populateFromBuffer(store, vertBuf, x);
            rotate.mult(store, store);
            BufferUtils.setInBuffer(store, vertBuf, x);
        }
    }
    
    public void rotateNormals(Quaternion rotate) {
        Vector3f store = new Vector3f();
        for (int x = 0; x < vertQuantity; x++) {
            BufferUtils.populateFromBuffer(store, normBuf, x);
            rotate.mult(store, store);
            BufferUtils.setInBuffer(store, normBuf, x);
        }
    }
}
