/*
 * Copyright (c) 2003-2006 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme.scene.state.lwjgl;

import java.util.Arrays;
import java.util.Stack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GLContext;

import com.jme.light.DirectionalLight;
import com.jme.light.Light;
import com.jme.light.PointLight;
import com.jme.light.SpotLight;
import com.jme.renderer.RenderContext;
import com.jme.scene.SceneElement;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.lwjgl.records.LightRecord;
import com.jme.scene.state.lwjgl.records.LightStateRecord;
import com.jme.scene.state.lwjgl.records.StateRecord;
import com.jme.system.DisplaySystem;

/**
 * <code>LWJGLLightState</code> subclasses the Light class using the LWJGL API
 * to access OpenGL for light processing.
 * 
 * @author Mark Powell
 * @author Joshua Slack - reworked for StateRecords.
 * @version $Id: LWJGLLightState.java,v 1.24 2006-11-20 22:57:27 nca Exp $
 */
public class LWJGLLightState extends LightState {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor instantiates a new <code>LWJGLLightState</code>.
	 */
	public LWJGLLightState() {
		super();
	}

	/**
	 * <code>set</code> iterates over the light queue and processes each
	 * individual light.
	 * 
	 * @see com.jme.scene.state.RenderState#apply()
	 */
	public void apply() {
		RenderContext context = DisplaySystem.getDisplaySystem()
				.getCurrentContext();
		LightStateRecord record = (LightStateRecord) context
				.getStateRecord(RS_LIGHT);
        context.currentStates[RS_LIGHT] = this;

		if (isEnabled()) {
			setLightEnabled(true, record);
			setTwoSided(twoSidedOn, record);
			setLocalViewer(localViewerOn, record);
			if (GLContext.getCapabilities().OpenGL12) {
				setSpecularControl(separateSpecularOn, record);
			}

			for (int i = 0, max = getQuantity(); i < max; i++) {
				int index = GL11.GL_LIGHT0 + i;

				Light light = get(i);

				if (light == null) {
					setSingleLightEnabled(false, i, record);
				} else {
					if (light.isEnabled()) {
						setLight(index, light, record);
					} else {
						setSingleLightEnabled(false, i, record);
					}
				}
			}

			for (int i = getQuantity(); i < MAX_LIGHTS_ALLOWED; i++) {
				setSingleLightEnabled(false, i, record);
			}

			if ((lightMask & MASK_GLOBALAMBIENT) == 0) {
				setModelAmbient(record, globalAmbient);
			} else {
				setDefaultModel(record, 0, 0, 0, 1);
			}

		} else {
			setLightEnabled(false, record);
		}
	}

	private void setLight(int index, Light light, LightStateRecord record) {
		setSingleLightEnabled(true, index - GL11.GL_LIGHT0, record);

		if ((lightMask & MASK_AMBIENT) == 0
				&& (light.getLightMask() & MASK_AMBIENT) == 0) {
			setAmbient(index, record, light.getAmbient().r,
					light.getAmbient().g, light.getAmbient().b, light
							.getAmbient().a);
		} else {
			setDefaultAmbient(index, record, 0, 0, 0, 0);
		}

		if ((lightMask & MASK_DIFFUSE) == 0
				&& (light.getLightMask() & MASK_DIFFUSE) == 0) {

			setDiffuse(index, record, light.getDiffuse().r,
					light.getDiffuse().g, light.getDiffuse().b, light
							.getDiffuse().a);
		} else {
			setDefaultDiffuse(index, record, 0, 0, 0, 0);
		}

		if ((lightMask & MASK_SPECULAR) == 0
				&& (light.getLightMask() & MASK_SPECULAR) == 0) {

			setSpecular(index, record, light.getSpecular().r, light
					.getSpecular().g, light.getSpecular().b, light
					.getSpecular().a);
		} else {
			setDefaultSpecular(index, record, 0, 0, 0, 0);
		}

		if (light.isAttenuate()) {
			setAttenuate(true, index, light, record);

		} else {
			setAttenuate(false, index, light, record);

		}

		switch (light.getType()) {
            case Light.LT_DIRECTIONAL: {
                DirectionalLight pkDL = (DirectionalLight) light;

                setPosition(index, record, -pkDL.getDirection().x, -pkDL
                        .getDirection().y, -pkDL.getDirection().z, 0);
                break;
            }
            case Light.LT_POINT:
            case Light.LT_SPOT: {
                PointLight pointLight = (PointLight) light;
                setPosition(index, record, pointLight.getLocation().x,
                        pointLight.getLocation().y, pointLight.getLocation().z,
                        1);
                break;
            }
        }

		if (light.getType() == Light.LT_SPOT) {
			SpotLight spot = (SpotLight) light;
			setSpotCutoff(index, record, spot.getAngle());
			setSpotDirection(index, record, spot.getDirection().x, spot
					.getDirection().y, spot.getDirection().z, 0);
			setSpotExponent(index, record, spot.getExponent());
		} else {
			setSpotDirection(index, record, 0, 0, -1, 0);
			setSpotExponent(index, record, 0);
			setSpotCutoff(index, record, 180);
		}
	}

	public RenderState extract(Stack stack, SceneElement spat) {
		int mode = spat.getLightCombineMode();
		if (mode == REPLACE || (mode != OFF && stack.size() == 1)) // todo: use
			// dummy
			// state if
			// off?
			return (LWJGLLightState) stack.peek();

		// accumulate the lights in the stack into a single LightState object
		LWJGLLightState newLState = new LWJGLLightState();
		Object states[] = stack.toArray();
		boolean foundEnabled = false;
		switch (mode) {
		case COMBINE_CLOSEST:
		case COMBINE_RECENT_ENABLED:
			for (int iIndex = states.length - 1; iIndex >= 0; iIndex--) {
				LWJGLLightState pkLState = (LWJGLLightState) states[iIndex];
				if (!pkLState.isEnabled()) {
					if (mode == COMBINE_RECENT_ENABLED)
						break;

					continue;
				}

				foundEnabled = true;
				if (pkLState.twoSidedOn)
					newLState.setTwoSidedLighting(true);
				if (pkLState.localViewerOn)
					newLState.setLocalViewer(true);
				if (pkLState.separateSpecularOn)
					newLState.setSeparateSpecular(true);
				for (int i = 0, maxL = pkLState.getQuantity(); i < maxL; i++) {
					Light pkLight = pkLState.get(i);
					if (pkLight != null) {
						newLState.attach(pkLight);
					}
				}
			}
			break;
		case COMBINE_FIRST:
			for (int iIndex = 0, max = states.length; iIndex < max; iIndex++) {
				LWJGLLightState pkLState = (LWJGLLightState) states[iIndex];
				if (!pkLState.isEnabled())
					continue;

				foundEnabled = true;
				if (pkLState.twoSidedOn)
					newLState.setTwoSidedLighting(true);
				if (pkLState.localViewerOn)
					newLState.setLocalViewer(true);
				if (pkLState.separateSpecularOn)
					newLState.setSeparateSpecular(true);
				for (int i = 0, maxL = pkLState.getQuantity(); i < maxL; i++) {
					Light pkLight = pkLState.get(i);
					if (pkLight != null) {
						newLState.attach(pkLight);
					}
				}
			}
			break;
		case OFF:
			break;
		}
		newLState.setEnabled(foundEnabled);
		return newLState;
	}

	private void setSingleLightEnabled(boolean enable, int index,
			LightStateRecord record) {
		if (record.getLightEnabled()[index] != enable) {
			if (enable) {
				GL11.glEnable(GL11.GL_LIGHT0 + index);
			} else {
				GL11.glDisable(GL11.GL_LIGHT0 + index);
			}

			record.getLightEnabled()[index] = enable;
		}
	}

	private void setLightEnabled(boolean enable, LightStateRecord record) {
		if (record.isEnabled() != enable) {
			if (enable) {
				GL11.glEnable(GL11.GL_LIGHTING);
			} else {
				GL11.glDisable(GL11.GL_LIGHTING);
			}
			record.setEnabled(enable);
		}
	}

	private void setTwoSided(boolean twoSided, LightStateRecord record) {
		if (record.isTwoSidedOn() != twoSided) {
			if (twoSided) {
				GL11.glLightModeli(GL11.GL_LIGHT_MODEL_TWO_SIDE, GL11.GL_TRUE);
			} else {
				GL11.glLightModeli(GL11.GL_LIGHT_MODEL_TWO_SIDE, GL11.GL_FALSE);
			}
			record.setTwoSidedOn(twoSided);
		}
	}

	private void setLocalViewer(boolean localViewer, LightStateRecord record) {
		if (record.isLocalViewer() != localViewer) {
			if (localViewer) {
				GL11.glLightModeli(GL11.GL_LIGHT_MODEL_LOCAL_VIEWER,
						GL11.GL_TRUE);
			} else {
				GL11.glLightModeli(GL11.GL_LIGHT_MODEL_LOCAL_VIEWER,
						GL11.GL_FALSE);
			}
			record.setLocalViewer(localViewer);
		}
	}

	private void setSpecularControl(boolean separateSpecularOn,
			LightStateRecord record) {
		if (record.isSeparateSpecular() != separateSpecularOn) {
			if (separateSpecularOn) {
				GL11.glLightModeli(GL12.GL_LIGHT_MODEL_COLOR_CONTROL,
						GL12.GL_SEPARATE_SPECULAR_COLOR);
			} else {
				GL11.glLightModeli(GL12.GL_LIGHT_MODEL_COLOR_CONTROL,
						GL12.GL_SINGLE_COLOR);
			}
			record.setSeparateSpecular(separateSpecularOn);
		}
	}

	private void setModelAmbient(LightStateRecord record, float... ambient) {
		if (!isArrayEqual(record.getGlobalAmbient(), ambient)) {
			record.lightBuffer.clear();
            record.lightBuffer.put(ambient);
            record.lightBuffer.flip();
			GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, record.lightBuffer);
			record.setGlobalAmbient(ambient);
		}
	}

	private void setDefaultModel(LightStateRecord record, float... ambient) {
		if (!isArrayEqual(record.getGlobalAmbient(), ambient)) {
			GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, zeroBuffer);
			record.setGlobalAmbient(ambient);
		}
	}

	private void setAmbient(int index, LightStateRecord record,
			float... ambient) {
		LightRecord lr = record.getLightRecord(index);
		if (lr == null) {
			lr = new LightRecord();
		}
		if (!isArrayEqual(lr.getAmbient(), ambient)) {
            record.lightBuffer.clear();
            record.lightBuffer.put(ambient);
            record.lightBuffer.flip();
			GL11.glLight(index, GL11.GL_AMBIENT, record.lightBuffer);
			lr.setAmbient(ambient);
			record.setLightRecord(lr, index);
		}
	}

	private void setDefaultAmbient(int index, LightStateRecord record,
			float... ambient) {
		LightRecord lr = record.getLightRecord(index);
		if (lr == null) {
			lr = new LightRecord();
		}
		if (!isArrayEqual(lr.getAmbient(), ambient)) {
			GL11.glLight(index, GL11.GL_AMBIENT, zeroBuffer);
			lr.setAmbient(ambient);
			record.setLightRecord(lr, index);
		}
	}

	private void setDiffuse(int index, LightStateRecord record,
			float... diffuse) {
		LightRecord lr = record.getLightRecord(index);
		if (lr == null) {
			lr = new LightRecord();
		}
		if (!isArrayEqual(lr.getDiffuse(), diffuse)) {
            record.lightBuffer.clear();
            record.lightBuffer.put(diffuse);
            record.lightBuffer.flip();
			GL11.glLight(index, GL11.GL_DIFFUSE, record.lightBuffer);
			lr.setDiffuse(diffuse);
			record.setLightRecord(lr, index);
		}
	}

	private void setDefaultDiffuse(int index, LightStateRecord record,
			float... diffuse) {
		LightRecord lr = record.getLightRecord(index);
		if (lr == null) {
			lr = new LightRecord();
		}
		if (!isArrayEqual(lr.getDiffuse(), diffuse)) {
			GL11.glLight(index, GL11.GL_DIFFUSE, zeroBuffer);
			lr.setDiffuse(diffuse);
			record.setLightRecord(lr, index);
		}
	}

	private void setPosition(int index, LightStateRecord record, float... position) {
		// From OpenGL Docs:
		// The light position is transformed by the contents of the current top
		// of the ModelView matrix stack when you specify the light position
		// with a call to glLightfv(GL_LIGHT_POSITION,…). If you later change
		// the ModelView matrix, such as when the view changes for the next
		// frame, the light position isn't automatically retransformed by the
		// new contents of the ModelView matrix. If you want to update the
		// light’s position, you must again specify the light position with a
		// call to glLightfv(GL_LIGHT_POSITION,…).
        record.lightBuffer.clear();
        record.lightBuffer.put(position);
        record.lightBuffer.flip();
		GL11.glLight(index, GL11.GL_POSITION, record.lightBuffer);

	}

	private void setSpotDirection(int index, LightStateRecord record, float... direction) {
		// From OpenGL Docs:
		// The light position is transformed by the contents of the current top
		// of the ModelView matrix stack when you specify the light position
		// with a call to glLightfv(GL_LIGHT_POSITION,…). If you later change
		// the ModelView matrix, such as when the view changes for the next
		// frame, the light position isn't automatically retransformed by the
		// new contents of the ModelView matrix. If you want to update the
		// light’s position, you must again specify the light position with a
		// call to glLightfv(GL_LIGHT_POSITION,…).
        record.lightBuffer.clear();
        record.lightBuffer.put(direction);
        record.lightBuffer.flip();
		GL11.glLight(index, GL11.GL_SPOT_DIRECTION, record.lightBuffer);
	}

	private void setDefaultSpecular(int index, LightStateRecord record,
			float... specular) {
		LightRecord lr = record.getLightRecord(index);
		if (lr == null) {
			lr = new LightRecord();
		}
		if (!isArrayEqual(lr.getSpecular(), specular)) {
			GL11.glLight(index, GL11.GL_SPECULAR, zeroBuffer);
			lr.setSpecular(specular);
			record.setLightRecord(lr, index);
		}
	}

	private void setSpecular(int index, LightStateRecord record,
			float... specular) {
		LightRecord lr = record.getLightRecord(index);
		if (lr == null) {
			lr = new LightRecord();
		}
		if (!isArrayEqual(lr.getSpecular(), specular)) {
            record.lightBuffer.clear();
            record.lightBuffer.put(specular);
            record.lightBuffer.flip();
			GL11.glLight(index, GL11.GL_SPECULAR, record.lightBuffer);
			lr.setSpecular(specular);
			record.setLightRecord(lr, index);
		}
	}

	private boolean isArrayEqual(float[] f1, float[] f2) {
		return Arrays.equals(f1, f2);
	}

	private void setConstant(int index, float constant, LightRecord lr) {
		if (constant != lr.getConstant()) {
			GL11.glLightf(index, GL11.GL_CONSTANT_ATTENUATION, constant);
			lr.setConstant(constant);
		}
	}

	private void setLinear(int index, float linear, LightRecord lr) {
		if (linear != lr.getLinear()) {
			GL11.glLightf(index, GL11.GL_LINEAR_ATTENUATION, linear);
			lr.setLinear(linear);
		}
	}

	private void setQuadratic(int index, float quad, LightRecord lr) {
		if (quad != lr.getQuadratic()) {
			GL11.glLightf(index, GL11.GL_QUADRATIC_ATTENUATION, quad);
			lr.setQuadratic(quad);
		}
	}

	private void setAttenuate(boolean attenuate, int index, Light light,
			LightStateRecord record) {
		LightRecord lr = record.getLightRecord(index);
		if (lr == null) {
			lr = new LightRecord();
		}
		if (lr.isAttenuate() != attenuate) {
			if (attenuate) {
				setConstant(index, light.getConstant(), lr);
				setLinear(index, light.getLinear(), lr);
				setQuadratic(index, light.getQuadratic(), lr);
			} else {
				setConstant(index, 1, lr);
				setLinear(index, 0, lr);
				setQuadratic(index, 0, lr);
			}
			lr.setAttenuate(attenuate);
			record.setLightRecord(lr, index);
		}
	}

	private void setSpotExponent(int index, LightStateRecord record,
			float exponent) {
		LightRecord lr = record.getLightRecord(index);
		if (lr == null) {
			lr = new LightRecord();
			record.setLightRecord(lr, index);
		}
		if (lr.getSpotExponent() != exponent) {
			GL11.glLightf(index, GL11.GL_SPOT_EXPONENT, exponent);
			lr.setSpotExponent(exponent);
		}
	}

	private void setSpotCutoff(int index, LightStateRecord record, float cutoff) {
		LightRecord lr = record.getLightRecord(index);
		if (lr == null) {
			lr = new LightRecord();
			record.setLightRecord(lr, index);
		}
		if (lr.getSpotCutoff() != cutoff) {
			GL11.glLightf(index, GL11.GL_SPOT_CUTOFF, cutoff);
			lr.setSpotCutoff(cutoff);
		}
	}

	@Override
	public StateRecord createStateRecord() {
		return new LightStateRecord();
	}
}