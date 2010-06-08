package com.jmex.model.ogrexml.anim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.jmex.model.ogrexml.OgreEntityNode;
import com.jmex.model.ogrexml.anim.PoseTrack.PoseFrame;

/**
 * 
 * helper class to handle MeshAnimations. As they are full with different arrays
 * this class makes it easier to handle them and create KeyFrameBased MeshAnimations
 * 
 * @author ttrocha
 *
 */
public class MeshAnimationWrapper {

	private OgreEntityNode ogreNode;
	private HashMap<Integer,HelperPoseTrack> tracks = new HashMap<Integer, HelperPoseTrack>();
	
	public MeshAnimationWrapper(OgreEntityNode ogreNode)
	{
		this.ogreNode = ogreNode;
	}

	/**
	 * 
	 * create a KeyFrame for your programmatically created MeshAnimation 
	 * 
	 * @param poseName 
	 * @param time
	 * @param weight
	 */
	public void addPoseKeyFrame(String poseName, float time, float weight)
	{
		List<Pose> subMeshPoses = ogreNode.getPose(poseName);
		for (Pose subMeshPose : subMeshPoses)
		{
			HelperPoseTrack track = tracks.get(subMeshPose.getTargetMeshIndex());
			if (track==null)
			{
				track = new HelperPoseTrack(subMeshPose.getTargetMeshIndex());
				tracks.put(subMeshPose.getTargetMeshIndex(), track);
			}
			
			// find or create frame in that track to that time
			HelperPoseFrame frame = findFrame(track,time);
			
			HelperPoseWeightPair poseWeight = frame.posesWeightPairs.get(subMeshPose);
			if (poseWeight==null)
			{
				poseWeight = new HelperPoseWeightPair();
				poseWeight.pose = subMeshPose;
				frame.posesWeightPairs.put(subMeshPose,poseWeight);
			}
			poseWeight.weight=weight;
		}
	}
	
	/**
	 * if already a frame at this time exists returns this
	 * otherwise create a new one in that track
	 * 
	 * @param track
	 * @param time
	 * @return
	 */
	public HelperPoseFrame findFrame(HelperPoseTrack track,float time)
	{
		int i=0;
		for (;i<track.frames.size();i++)
		{
			if (track.frames.get(i).time==time)
				return track.frames.get(i);
			else if (track.frames.get(i).time>time)
				break;
		}
		HelperPoseFrame newFrame = new HelperPoseFrame();
		newFrame.time=time;
		track.frames.add(i, newFrame);
		return newFrame;
	}

	/**
	 * 
	 * finally create a MeshAnimation. If you specify an MeshAnimationController
	 * the new MeshAnimation is registered at the controller as well
	 * 
	 * @param name
	 * @param meshAnimContr
	 * @return
	 */
	public MeshAnimation createMeshAnimation(String name,MeshAnimationController meshAnimContr)
	{
		PoseTrack[] pTracks = new PoseTrack[tracks.size()];
		float animLength = 0; 

		int tracksCounter = 0;
		for (HelperPoseTrack hTrack : tracks.values())
		{
			PoseFrame[] pFrames = new PoseFrame[hTrack.frames.size()];
			float[] times = new float[hTrack.frames.size()];
			int frameCounter = 0;
			for (HelperPoseFrame frame : hTrack.frames)
			{
				Pose[] poses = new Pose[frame.posesWeightPairs.size()];
				float[] weights = new float[frame.posesWeightPairs.size()];
				int pwCounter=0;
				for (HelperPoseWeightPair poseWeight : frame.posesWeightPairs.values())
				{
					poses[pwCounter]=poseWeight.pose;
					weights[pwCounter]=poseWeight.weight;
					pwCounter++;
				}
				PoseFrame pFrame = new PoseFrame(poses, weights);
				pFrames[frameCounter]=pFrame;
				times[frameCounter]=frame.time;
				frameCounter++;
				animLength=frame.time;
			}
			PoseTrack track = new PoseTrack(hTrack.submeshID, times, pFrames);
			pTracks[tracksCounter++]=track;
		}
		MeshAnimation meshAnimation = new MeshAnimation(name, animLength);
		meshAnimation.setTracks(pTracks);
		
		if (meshAnimContr!=null)
		{
			meshAnimContr.getAnimationMap().put(name, new Animation(null, meshAnimation));
		}
		return meshAnimation;
	}
	
	public class HelperPoseTrack {
		public int submeshID;
		public ArrayList<HelperPoseFrame> frames=new ArrayList<HelperPoseFrame>();
		
		public HelperPoseTrack(int submeshID){
			this.submeshID=submeshID;
		}
	}
	
	public class HelperPoseFrame {
		public float time;
		public HashMap<Pose,HelperPoseWeightPair> posesWeightPairs=new HashMap<Pose, HelperPoseWeightPair>();
	}
	
	public class HelperPoseWeightPair {
		public Pose pose;
		public float weight;
	}

}
