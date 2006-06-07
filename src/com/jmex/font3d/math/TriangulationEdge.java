package com.jmex.font3d.math;

public class TriangulationEdge extends PlanarEdge
{
	/** Used when making the monotone polygons. */
	TriangulationVertex helper = null;
	public boolean marked = false;
	
	TriangulationEdge(PlanarVertex orig, boolean real)
	{
		super(orig, real);
	}

	public TriangulationVertex getOtherEnd(TriangulationVertex currentvertex)
	{
		return (TriangulationVertex) (getOrigin() == currentvertex ? getTwin().getOrigin() : getOrigin());
	}

	public boolean isHelperMergeVertex()
	{
		return (helper != null && helper.getType() == TriangulationVertex.VertexType.MERGE);
	}

	@Override
	public String toString()
	{
		return "{("+(isRealEdge() ? "real  " : "unreal")+")"+
			getOrigin().getIndex()+"("+getOrigin().point.x+","+getOrigin().point.y+")->"+
			getTwin().getOrigin().getIndex()+":("+getTwin().getOrigin().point.x+","+getTwin().getOrigin().point.y+")"+
			",Helper:"+helper+
			",angle:"+getAngle()+"}";
	}
}
