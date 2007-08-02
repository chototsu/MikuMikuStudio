package com.jmex.font3d.math;

import java.util.logging.Logger;

import com.jme.math.Vector3f;

public class PlanarVertex
{
    private static final Logger logger = Logger.getLogger(PlanarVertex.class
            .getName());
    
	int index;
	Vector3f point;
	private PlanarEdge arb_outgoing;
	PlanarVertex(int i, Vector3f p)
	{
		this.index = i;
		this.point = p;
	}
	void addOutgoingEdge(PlanarEdge outedge)
	{
		if(arb_outgoing == null)
		{
			// First one to leave the vertex
			arb_outgoing = outedge;
			outedge.getTwin().setNext(outedge);
			//outedge.prev = outedge.twin;
		}
		else
		{
			// Bind to it's neighbor at this vertex
			PlanarEdge neighbor = clockWiseOf(outedge);
			
			neighbor.getPrev().setNext(outedge);
			outedge.getTwin().setNext(neighbor);
		}
		
		// Bind to its neighbor at the destination vertex (only if it already has outgoing edges
		if(outedge.getTwin().getOrigin().arb_outgoing == null)
		{
			outedge.getTwin().getOrigin().arb_outgoing = outedge.getTwin();
			outedge.setNext(outedge.getTwin());
		}
		else
		{
			PlanarEdge neighbor = outedge.getTwin().getOrigin().clockWiseOf(outedge.getTwin());
			neighbor.getPrev().setNext(outedge.getTwin());
			outedge.setNext(neighbor);
		}
	}

	
	/**
	 * Returns the first outgoing edge clockwise of the given edge (can be the edge it self).
	 * 
	 * @param edge
	 * @return
	 */
	private PlanarEdge clockWiseOf(PlanarEdge edge)
	{
		// Find the
		PlanarEdge result = arb_outgoing;
		PlanarEdge next = result; 
		float angle = next.angleCounterClockWise(edge);
		do
		{
			if(next.getOrigin() != this)
				throw new RuntimeException("We get an edge that does not orginate from this vertex !!!");
			next = next.getTwin().getNext();
			float nangle = next.angleCounterClockWise(edge);
			
			// Always prefer the same type as 'edge.isRealEdge()'
			if(nangle == angle && result != next)
			{
				// If we have the same angle, we must get the one that is topologically most counter-clockwise
				if(next.getTwin().getNext() == result)
				{
					result = next;
				}
				else if(next.isRealEdge() && !result.isRealEdge())
				{
					result = next;
				}
				else if(result.getTwin().getNext() == next)
				{
					// ok, the result is good
				}
				else
				{
					logger.warning("Error: (nangle == angle && "+result+" != "+next+")");
				}
			}
			else if(nangle < angle)
			{
				result = next;
				angle = nangle;
			}
		}
		while(next != arb_outgoing); // Only one round
		/*
		logger.info("\nEdge: "+FastMath.atan2(edge.getDX(), edge.getDY()));
		logger.info("Result: "+FastMath.atan2(result.getDX(), result.getDY()));
		logger.info("Angle: "+angle);
		*/
		return result;
	}
	
	/**
	 * Retuns the edge from this vertex to the given, or null if none exists.
	 * 
	 * @param v
	 * @return
	 */
	PlanarEdge getEdge(PlanarVertex v)
	{
		if(arb_outgoing == null)
			return null;
		PlanarEdge next = arb_outgoing; 
		do
		{
			if(next.getTwin().getOrigin() == v)
				return next;
			next = next.getTwin().getNext();
		}
		while(next != arb_outgoing); // Only one round
		return null;
	}

	
	public int getIndex()
	{
		return index;
	}
	PlanarEdge getFirstEdge()
	{
		return arb_outgoing;
	}
	
	@Override
	public String toString()
	{
		return "[indx:"+index+",("+point.x+","+point.y+")]";
	}
	public Vector3f getPoint()
	{
		return point;
	}
	public void printEdges()
	{
		if(arb_outgoing == null)
		{
			logger.info("I HAVE NOT EDGES !");
			return;
		}
		PlanarEdge next = arb_outgoing; 
		do
		{
			logger.info("Edge:"+next);
			next = next.getTwin().getNext();
		}
		while(next != arb_outgoing); // Only one round
	}
}
