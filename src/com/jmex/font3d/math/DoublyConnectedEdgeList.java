package com.jmex.font3d.math;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.jme.math.Vector3f;



/**
 * This class represents as its name indicates a planar subdivision.
 * 
 * Its uses are many, but to name a few, its good for triangulation of complex polygons 
 * (those with holes in them....).
 * 
 * To make a triangulation it is assumed that some subset of the edges form a closed polygon around
 * the rest of the triangulation. These points must be connected in counter-clockwise order,
 * that is the interior of the polygon they form lies to the left of every edge in it.
 * The internal representation of the planar subdivision does allow though to traverse the edges backwards,
 * since we use DCEL representation.
 * 
 * NOTE:
 *  - At the moment the planar subdivision does not accept anything but 1 or two manifold vertices. If you
 *    add more edges than that, stuff will break :-)
 * 
 * @author emanuel
 *
 */
public abstract class DoublyConnectedEdgeList<Vertex extends PlanarVertex, Edge extends PlanarEdge>
{
    private static final Logger logger = Logger
            .getLogger(DoublyConnectedEdgeList.class.getName());
    
	// These are the points in the glyph
	ArrayList<Vertex> vertices  = new ArrayList<Vertex>();
	// These are the edges of the glyph
	ArrayList<Edge> edges       = new ArrayList<Edge>();

	public abstract Vertex createVertex(int index, Vector3f p);
	public abstract Edge   createEdge(Vertex origin, boolean real);

	public Vertex addVertex(Vector3f p)
	{
		Vertex point = createVertex(vertices.size(), p);
		vertices.add(point);
		return point;
	}
	
	public Edge addEdge(int src_i, int dst_i)
	{
		Vertex src = vertices.get(src_i);
		Vertex dst = vertices.get(dst_i);
		
		// Test that the edge does not already exist
		Edge src_e = (Edge) src.getEdge(dst);
		boolean new_src_e = false;
		if(src_e == null)
		{
			src_e = createEdge(src, true);
			new_src_e = true;
		}
		else
		{
			src_e.realedge = true;
			logger.info("Added an duplicate edge: ("+src_i+" -> "+dst_i+")");
			//throw new RuntimeException("POWER UP !!!");
		}
		Edge dst_e = (Edge) dst.getEdge(src);
		boolean new_dst_e = false;
		if(dst_e == null)
		{
			dst_e = createEdge(dst, false);
			new_dst_e = true;
		}
		else
		{
			logger.info("Added a duplicate edge (TWIN): ("+dst_i+" -> "+src_i+")");
		}
		
		// Bind the two half-edges
		src_e.setTwin(dst_e);
		// Bind that edge as the outgoing from where they are outgoing
		if(new_src_e)
			src.addOutgoingEdge(src_e);
		else if(new_dst_e)
			throw new RuntimeException("Damng, created a twin to an existing edge, that can never happen");
		//if(new_dst_e)
		//	dst.addOutgoingEdge(dst_e);
		
		//logger.info("Added edge: "+src_i+" -> "+dst_i);
		// Only add the "forward edge" to make sure we can get the original orientation.
		edges.add(src_e);
		edges.add(dst_e);
		
		return src_e;
	}
	
	public ArrayList<Vertex> getVertices()
	{
		return vertices;
	}
}
