/*
 * Copyright (c) 2003-2009 jMonkeyEngine
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


package com.jmex.model.ogrexml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import com.jme.scene.Node;
import com.jmex.model.ogrexml.anim.Pose;


/**
 * An ogreloader-specific com.jme.scene.Node.
 * <P>
 * This adds no behavior or data beyond that of com.jme.scene.Node.
 * It just allows coders to use OO Java mechanisms to differentiate ogreloader
 * nodes.
 * </P>
 *
 ** @author Blaine Simpson (blaine dot simpson at admc dot com)
 */
public class OgreEntityNode extends Node {
    static final long serialVersionUID = -7387168389329790518L;
    private static final Logger logger = Logger.getLogger(OgreEntityNode.class
            .getName());
    
    private HashMap<String,List<Pose>> poseMap;
    
    public OgreEntityNode(String name) {
        super(name);
    }

    public OgreEntityNode() {
    }

    /**
     * 
     * method to keep track of poses. as you can assign one pose
     * e.g. in blender to a mesh that will be devided into submeshes
     * this method collects all that submesh-poses that are part of the
     * mesh-pose.
     * 
     * Usually this is called not manually but automatically in OgreLoader.java
     * 
     * @param name
     * @param pose
     */
	public void addPose(String name,Pose pose) {
		if (poseMap==null)
			poseMap = new HashMap<String, List<Pose>>();
		
		List<Pose> tempPoseList = poseMap.get(name);
		if (tempPoseList==null)
		{
			tempPoseList = new ArrayList<Pose>();
			poseMap.put(name,tempPoseList);
		}
		
		tempPoseList.add(pose);
	}
	
	/**
	 * 
	 * returns all pose-names registered to this OgreMesh
	 * 
	 * @return Set<String>
	 */
	public Set<String> getPoseNames()
	{
		if (poseMap==null)
		{
			logger.warning("Tried to get posenames for OgreEntity("+getName()+") but got nothing to return");
		}
		return poseMap.keySet();
	}
	
	/**
	 * 
	 * returns Poses for all submeshes that are linked to specified poseName
	 * 
	 * @param name
	 * @return
	 */
	public List<Pose> getPose(String name)
	{
		List<Pose> result = poseMap.get(name);
		if (poseMap==null || result == null)
		{
			logger.warning("Tried to get posenames for OgreEntity("+getName()+") but got nothing to return");
		}
		return result;
	}
	
	
	
}
