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
package com.jme.animation;

import com.jme.math.Matrix4f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.model.JointMesh;

/**
 * <code>DeformationJointController</code> defines a controller that
 * controls the animation of a mesh via a collection of joints. The 
 * controller uses these joints to modify specific vertices of the mesh
 * in essense deforming the original mesh. So model types require a 
 * call to <code>setupJointAnimations</code> to properly position and
 * initialize the joints (Milkshape). 
 * @author Mark Powell
 * @version $Id: DeformationJointController.java,v 1.2 2004-02-01 17:29:57 mojomonkey Exp $
 */
public class DeformationJointController extends Controller {
	//keyframe information
	private int totalFrames;
	private float currentFrame;
	//joints and meshes
	private DeformationJoint[] joints;
	private JointMesh[] meshes;
	//going forward or backward?
	private int modifier = 1;
	
	private boolean doBoundsUpdate;

	/**
	 * Constructor creates a new <code>DeformationJointController</code>.
	 *
	 */
	public DeformationJointController() {
		super();
	}
	
	public void setUpdateModelBounds(boolean value) {
		doBoundsUpdate = value;
	}

	/**
	 * <code>setMeshes</code> sets the collection of meshes that the 
	 * joints will alter.
	 * @param meshes the animated meshes.
	 */
	public void setMeshes(JointMesh[] meshes) {
		this.meshes = meshes;
	}

	/**
	 * <code>setJoints</code> sets the collection of joints used by 
	 * the controller to animate the meshes.
	 * @param joints the joints used to animate the mesh.
	 */
	public void setJoints(DeformationJoint[] joints) {
		this.joints = joints;
	}

	/**
	 * <code>getNumberOfJoints</code> returns the number of joints assigned
	 * to this controller.
	 * @return the number of joints assigned to this controller.
	 */
	public int getNumberOfJoints() {
		return joints.length;
	}

	/**
	 * <code>setJoint</code> sets a specific joint of the controller.
	 * @param index the index of the joint array.
	 * @param joint the joint to set.
	 */
	public void setJoint(int index, DeformationJoint joint) {
		if(index < 0 || index > joints.length) {
			return;
		}
		joints[index] = joint;
	}

	/**
	 * <code>getJoint</code> gets a joint from the specified index. If
	 * the index is invalid, null is returned.
	 * @param index the index to retrieve.
	 * @return the joint at the given index.
	 */
	public DeformationJoint getJoint(int index) {
		if(index < 0 || index > joints.length) {
			return null;
		}
		return joints[index];
	}

	
	/**
	 * <code>setCurrentFrame</code> sets the frame that the animation is 
	 * current set to.
	 * @param currentFrame the keyframe that is the current state of the
	 * 		animation.
	 */
	public void setCurrentFrame(float currentFrame) {
		this.currentFrame = currentFrame;
	}

	/**
	 * <code>setTotalFrames</code> sets the total number of frames
	 * that makes up this controllers animation sequence.
	 * @param totalFrames the number of frames that make up the total 
	 * 		animation.
	 */
	public void setTotalFrames(int totalFrames) {
		this.totalFrames = totalFrames;
	}

	/**
	 * <code>update</code> changes the animation of the mesh by
	 * a specified time interval. 
	 * @see com.jme.scene.Controller#update(float)
	 */
	public void update(float time) {
		if (joints.length == 0) {
			return;
		}

		if (isActive()) {
			currentFrame += (time * modifier * getFrequency());

			//determine keyframe based on repeat type.
			if (getRepeatType() == RT_CLAMP) {
				if (currentFrame > totalFrames) {
					currentFrame = totalFrames;
				}
			}

			if (getRepeatType() == RT_WRAP) {
				if (currentFrame > totalFrames) {
					currentFrame = 0.0f;
				}
			}

			if (getRepeatType() == RT_CYCLE) {
				if (currentFrame > totalFrames || currentFrame < 0) {
					modifier *= -1;
					currentFrame += (time * modifier * getFrequency());
				}
			}

			//go through each joint, changing related meshes.
			for (int jointIndex = 0;
				jointIndex < joints.length;
				jointIndex++) {
					
				DeformationJoint joint = joints[jointIndex];
				int positionKeyframeCount = joint.numberPosistionKeyframes;
				int rotationKeyframeCount = joint.numberRotationKeyframes;
				if (positionKeyframeCount == 0 && rotationKeyframeCount == 0) {
					joints[jointIndex].finalMatrix.copy(
						joints[jointIndex].absoluteMatrix);
				} else {
					Vector3f positionVector3f = new Vector3f();
					Quaternion rotationVector3f = new Quaternion();
					Keyframe lastPositionKeyframe = null;
					Keyframe currentPositionKeyframe = null;
					for (int keyframeIndex = 0;
						keyframeIndex < positionKeyframeCount;
						keyframeIndex++) {
						Keyframe positionKeyframe =
							joint.positionKeys[keyframeIndex];
						if (positionKeyframe.time >= currentFrame) {
							currentPositionKeyframe = positionKeyframe;
							break;
						}
						lastPositionKeyframe = positionKeyframe;
					}
					if (lastPositionKeyframe != null
						&& currentPositionKeyframe != null) {
						float d =
							currentPositionKeyframe.time
								- lastPositionKeyframe.time;
						float s =
							(currentFrame - lastPositionKeyframe.time) / d;
						positionVector3f.x =
							lastPositionKeyframe.x
								+ (currentPositionKeyframe.x
									- lastPositionKeyframe.x)
									* s;
						positionVector3f.y =
							lastPositionKeyframe.y
								+ (currentPositionKeyframe.y
									- lastPositionKeyframe.y)
									* s;
						positionVector3f.z =
							lastPositionKeyframe.z
								+ (currentPositionKeyframe.z
									- lastPositionKeyframe.z)
									* s;
					} else if (lastPositionKeyframe == null) {
						currentPositionKeyframe.x = positionVector3f.x;
						currentPositionKeyframe.y = positionVector3f.y;
						currentPositionKeyframe.z = positionVector3f.z;
					} else if (currentPositionKeyframe == null) {
						lastPositionKeyframe.x = positionVector3f.x;
						lastPositionKeyframe.y = positionVector3f.y;
						lastPositionKeyframe.z = positionVector3f.z;
					}
					Matrix4f slerpedMatrix = new Matrix4f();
					Keyframe lastRotationKeyframe = null;
					Keyframe currentRotationKeyframe = null;
					for (int keyframeIndex = 0;
						keyframeIndex < rotationKeyframeCount;
						keyframeIndex++) {
						Keyframe rotationKeyframe =
							joint.rotationKeys[keyframeIndex];
						if (rotationKeyframe.time >= currentFrame) {
							currentRotationKeyframe = rotationKeyframe;
							break;
						}
						lastRotationKeyframe = rotationKeyframe;
					}
					if (lastRotationKeyframe != null
						&& currentRotationKeyframe != null) {
						float d =
							currentRotationKeyframe.time
								- lastRotationKeyframe.time;
						float s =
							(currentFrame - lastRotationKeyframe.time) / d;
						Quaternion slerpedQuaternion = new Quaternion();
						Quaternion lastRotationQuaternion = new Quaternion();
						Quaternion currentRotationQuaternion = new Quaternion();
						lastRotationQuaternion.fromAngles(
							new float[] {
								lastRotationKeyframe.x,
								lastRotationKeyframe.y,
								lastRotationKeyframe.z });
						currentRotationQuaternion.fromAngles(
							new float[] {
								currentRotationKeyframe.x,
								currentRotationKeyframe.y,
								currentRotationKeyframe.z });
						slerpedQuaternion =
							slerpedQuaternion.slerp(
								lastRotationQuaternion,
								currentRotationQuaternion,
								s);
						slerpedMatrix.set(slerpedQuaternion);
					} else if (lastRotationKeyframe == null) {
						rotationVector3f.x =
							currentRotationKeyframe.x * 180 / (float) Math.PI;
						rotationVector3f.y =
							currentRotationKeyframe.y * 180 / (float) Math.PI;
						rotationVector3f.z =
							currentRotationKeyframe.z * 180 / (float) Math.PI;
						slerpedMatrix.angleRotation(
							new Vector3f(
								rotationVector3f.x,
								rotationVector3f.y,
								rotationVector3f.z));
					} else if (currentRotationKeyframe == null) {
						rotationVector3f.x =
							lastRotationKeyframe.x * 180 / (float) Math.PI;
						rotationVector3f.y =
							lastRotationKeyframe.y * 180 / (float) Math.PI;
						rotationVector3f.z =
							lastRotationKeyframe.z * 180 / (float) Math.PI;
						slerpedMatrix.angleRotation(
							new Vector3f(
								rotationVector3f.x,
								rotationVector3f.y,
								rotationVector3f.z));
					}
					slerpedMatrix.set(0, 3, positionVector3f.x);
					slerpedMatrix.set(1, 3, positionVector3f.y);
					slerpedMatrix.set(2, 3, positionVector3f.z);
					joints[jointIndex].relativeFinalMatrix =
						joints[jointIndex].relativeMatrix.mult(slerpedMatrix);
					if (joint.parentIndex == -1) {
						joints[jointIndex].finalMatrix.copy(
							joints[jointIndex].relativeFinalMatrix);
					} else {
						joints[jointIndex].finalMatrix =
							joints[joint.parentIndex].finalMatrix.mult(
								joints[jointIndex].relativeFinalMatrix);
					}
				}

			}
			for (int i = 0; i < meshes.length; i++) {
				Vector3f[] vertices = meshes[i].getOriginalVertices();
				int[] jointIndices = meshes[i].getJointIndices();
				int[] ind = meshes[i].getIndices();
				for (int triangleIndex = 0; triangleIndex < ind.length;) {

					Vector3f vertex = vertices[ind[triangleIndex]];
					int joint = jointIndices[ind[triangleIndex]];
					if (joint != -1) {
						meshes[i].getVertices()[ind[triangleIndex]] =
							joints[joint].finalMatrix.mult(
								new Vector3f(vertex.x, vertex.y, vertex.z));
						meshes[i].getVertices()[ind[triangleIndex]].x
							+= joints[joint].finalMatrix.get(0, 3);
						meshes[i].getVertices()[ind[triangleIndex]].y
							+= joints[joint].finalMatrix.get(1, 3);
						meshes[i].getVertices()[ind[triangleIndex]].z
							+= joints[joint].finalMatrix.get(2, 3);
					}

					//get the joint, if it's not -1 it's assigned. SO
					//update the proper vertex.
					triangleIndex++;
					vertex = vertices[ind[triangleIndex]];
					joint = jointIndices[ind[triangleIndex]];

					if (joint != -1) {
						meshes[i].getVertices()[ind[triangleIndex]] =
							joints[joint].finalMatrix.mult(
								new Vector3f(vertex.x, vertex.y, vertex.z));
						meshes[i].getVertices()[ind[triangleIndex]].x
							+= joints[joint].finalMatrix.get(0, 3);
						meshes[i].getVertices()[ind[triangleIndex]].y
							+= joints[joint].finalMatrix.get(1, 3);
						meshes[i].getVertices()[ind[triangleIndex]].z
							+= joints[joint].finalMatrix.get(2, 3);
					}

					triangleIndex++;
					vertex = vertices[ind[triangleIndex]];
					joint = jointIndices[ind[triangleIndex]];

					if (joint != -1) {
						meshes[i].getVertices()[ind[triangleIndex]] =
							joints[joint].finalMatrix.mult(
								new Vector3f(vertex.x, vertex.y, vertex.z));
						meshes[i].getVertices()[ind[triangleIndex]].x
							+= joints[joint].finalMatrix.get(0, 3);
						meshes[i].getVertices()[ind[triangleIndex]].y
							+= joints[joint].finalMatrix.get(1, 3);
						meshes[i].getVertices()[ind[triangleIndex]].z
							+= joints[joint].finalMatrix.get(2, 3);
						triangleIndex++;
					}

				}
				meshes[i].updateVertexBuffer();
				
				if(doBoundsUpdate) {
					meshes[i].updateModelBound();
				}
			}
		}
	}
	
	/**
	 * <code>setupJointAnimations</code> calculates the initial absolute
	 * and relative matrices for each joint in the controller.
	 */
	public void setupJointAnimations() {
		for (int jointIndex = 0; jointIndex < joints.length; jointIndex++) {
			DeformationJoint joint = joints[jointIndex];
			Vector3f rotationVector3f = new Vector3f();
			rotationVector3f.x = joint.rot.x * 180 / (float) Math.PI;
			rotationVector3f.y = joint.rot.y * 180 / (float) Math.PI;
			rotationVector3f.z = joint.rot.z * 180 / (float) Math.PI;
			joints[jointIndex].relativeMatrix.angleRotation(rotationVector3f);
			joints[jointIndex].relativeMatrix.set(0, 3, joint.pos.x);
			joints[jointIndex].relativeMatrix.set(1, 3, joint.pos.y);
			joints[jointIndex].relativeMatrix.set(2, 3, joint.pos.z);
			if (joint.parentIndex != -1) {
				joints[jointIndex].absoluteMatrix =
					joints[joint.parentIndex].absoluteMatrix.mult(
						joints[jointIndex].relativeMatrix);
				joints[jointIndex].finalMatrix.copy(
					joints[jointIndex].absoluteMatrix);
			} else {
				joints[jointIndex].absoluteMatrix.copy(
					joints[jointIndex].relativeMatrix);
				joints[jointIndex].finalMatrix.copy(
					joints[jointIndex].relativeMatrix);
			}
		}

		for (int meshIndex = 0; meshIndex < meshes.length; meshIndex++) {
			JointMesh mesh = meshes[meshIndex];
			for (int j = 0; j < mesh.getVertices().length; j++) {
				Vector3f vertex = mesh.getOriginalVertices()[j];
				if (mesh.getJointIndices()[j] != -1) {
					vertex.x
						-= joints[mesh.getJointIndices()[j]].absoluteMatrix.get(
							0,
							3);
					vertex.y
						-= joints[mesh.getJointIndices()[j]].absoluteMatrix.get(
							1,
							3);
					vertex.z
						-= joints[mesh.getJointIndices()[j]].absoluteMatrix.get(
							2,
							3);
					Vector3f inverseRotationVector3f = new Vector3f();
					inverseRotationVector3f =
						joints[mesh
							.getJointIndices()[j]]
							.absoluteMatrix
							.inverseRotate(
							new Vector3f(vertex.x, vertex.y, vertex.z));

					vertex.x = inverseRotationVector3f.x;
					vertex.y = inverseRotationVector3f.y;
					vertex.z = inverseRotationVector3f.z;
				}
			}
		}

	}
}
