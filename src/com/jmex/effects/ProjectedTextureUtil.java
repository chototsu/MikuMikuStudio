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

package com.jmex.effects;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.glu.GLU;

import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Matrix4f;
import com.jme.math.Vector3f;
import com.jme.scene.state.lwjgl.records.RendererRecord;
import com.jme.system.DisplaySystem;
import com.jme.util.geom.BufferUtils;

/**
 * <code>ProjectedTextureUtil</code>
 *
 * @author Rikard Herlitz (MrCoder)
 */
public class ProjectedTextureUtil {
	private static Matrix4f lightProjectionMatrix = new Matrix4f();
	private static Matrix4f lightViewMatrix = new Matrix4f();
	private static Matrix4f biasMatrix = new Matrix4f(
			0.5f, 0.0f, 0.0f, 0.0f,
			0.0f, 0.5f, 0.0f, 0.0f,
			0.0f, 0.0f, 0.5f, 0.0f,
			0.5f, 0.5f, 0.5f, 1.0f
	); //bias from [-1, 1] to [0, 1]

	/**
	 * Sets the provided texture up for projection use
	 * @param texture Texture to use for projection
	 * @param wrapMode Wrapping mode, any of Texture.WM_XXX
	 * @param combineMode Combine mode, any of Texture.ACF_XXX
	 */
	public static void setupProjectedTexture( Texture texture, int wrapMode, int combineMode ) {
		texture.setMatrix( new Matrix4f() );
		texture.setEnvironmentalMapMode( Texture.EM_EYE_LINEAR );
		texture.setWrap( wrapMode);

		texture.setApply( Texture.AM_COMBINE );
		texture.setCombineFuncRGB( combineMode );
		texture.setCombineSrc0RGB( Texture.ACS_TEXTURE );
		texture.setCombineOp0RGB( Texture.ACO_SRC_COLOR );
		texture.setCombineSrc1RGB( Texture.ACS_PREVIOUS );
		texture.setCombineOp1RGB( Texture.ACO_SRC_COLOR );
		texture.setCombineScaleRGB( 1.0f );
	}

	/**
	 * Updated texture matrix on the provided texture
	 * @param texture Texture to update texturematrix on
	 * @param fov Projector field of view, in angles
	 * @param aspect Projector frustum aspect ratio
	 * @param near Projector frustum near plane
	 * @param far Projector frustum far plane
	 * @param pos Projector position
	 * @param aim Projector look at position
	 */
	public static void updateProjectedTexture( Texture texture, float fov, float aspect, float near, float far, Vector3f pos, Vector3f aim, Vector3f up ) {
		matrixPerspective( fov, aspect, near, far, lightProjectionMatrix );
		matrixLookAt( pos, aim, up, lightViewMatrix );
		texture.getMatrix().set( lightViewMatrix.multLocal( lightProjectionMatrix ).multLocal( biasMatrix ) ).transposeLocal();
	}

	//UTILS
	private static final FloatBuffer tmp_FloatBuffer = org.lwjgl.BufferUtils.createFloatBuffer( 16 );
	private static Vector3f localDir = new Vector3f();
	private static Vector3f localLeft = new Vector3f();
	private static Vector3f localUp = new Vector3f();

	private static IntBuffer matrixModeBuffer = BufferUtils.createIntBuffer( 16 );
	private static int savedMatrixMode = 0;
	private static void saveMatrixMode() {
		matrixModeBuffer.rewind();
		GL11.glGetInteger( GL11.GL_MATRIX_MODE, matrixModeBuffer );
		savedMatrixMode = matrixModeBuffer.get( 0 );
	}

	private static void restoreMatrixMode() {
        RendererRecord matRecord = (RendererRecord) DisplaySystem.getDisplaySystem().getCurrentContext().getRendererRecord();
        matRecord.switchMode(savedMatrixMode);
	}

	public static void matrixLookAt( Vector3f location, Vector3f at, Vector3f up, Matrix4f result ) {
		localDir.set( at ).subtractLocal( location ).normalizeLocal();
		localDir.cross( up, localLeft );
		localLeft.cross( localDir, localUp );

		saveMatrixMode();

		// set view matrix
        RendererRecord matRecord = (RendererRecord) DisplaySystem.getDisplaySystem().getCurrentContext().getRendererRecord();
        matRecord.switchMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GLU.gluLookAt(
				location.x,
				location.y,
				location.z,
				at.x,
				at.y,
				at.z,
				localUp.x,
				localUp.y,
				localUp.z );

		if( result != null ) {
			tmp_FloatBuffer.rewind();
			GL11.glGetFloat( GL11.GL_MODELVIEW_MATRIX, tmp_FloatBuffer );
			tmp_FloatBuffer.rewind();
			result.readFloatBuffer( tmp_FloatBuffer );
		}

		GL11.glPopMatrix();
		restoreMatrixMode();
	}

	public static void matrixPerspective( float fovY, float aspect, float near, float far, Matrix4f result ) {
		saveMatrixMode();

		// set view matrix
        RendererRecord matRecord = (RendererRecord) DisplaySystem.getDisplaySystem().getCurrentContext().getRendererRecord();
        matRecord.switchMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GLU.gluPerspective( fovY, aspect, near, far );

		if( result != null ) {
			tmp_FloatBuffer.rewind();
			GL11.glGetFloat( GL11.GL_MODELVIEW_MATRIX, tmp_FloatBuffer );
			tmp_FloatBuffer.rewind();
			result.readFloatBuffer( tmp_FloatBuffer );
		}

		GL11.glPopMatrix();
		restoreMatrixMode();
	}

	public static void matrixProjection( float fovY, float aspect, float near, float far, Matrix4f result ) {
		float h = FastMath.tan( fovY * FastMath.DEG_TO_RAD ) * near * .5f;
		float w = h * aspect;
		float frustumLeft = -w;
		float frustumRight = w;
		float frustumBottom = -h;
		float frustumTop = h;
		float frustumNear = near;
		float frustumFar = far;

		saveMatrixMode();
        RendererRecord matRecord = (RendererRecord) DisplaySystem.getDisplaySystem().getCurrentContext().getRendererRecord();
        matRecord.switchMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glFrustum(
				frustumLeft,
				frustumRight,
				frustumBottom,
				frustumTop,
				frustumNear,
				frustumFar );

		if( result != null ) {
			tmp_FloatBuffer.rewind();
			GL11.glGetFloat( GL11.GL_PROJECTION_MATRIX, tmp_FloatBuffer );
			tmp_FloatBuffer.rewind();
			result.readFloatBuffer( tmp_FloatBuffer );
		}

		GL11.glPopMatrix();
		restoreMatrixMode();
	}
}
