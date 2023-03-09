package magma.monitor.worldmodel.impl;

import magma.monitor.worldmodel.ISimulationObject;
import magma.util.scenegraph.IBaseNode;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public abstract class SimulationObject implements ISimulationObject
{
	protected IBaseNode graphRoot;

	protected Vector3D position;

	public SimulationObject()
	{
		position = Vector3D.ZERO;
	}

	public SimulationObject(IBaseNode graphRoot)
	{
		this.graphRoot = graphRoot;
	}

	public void setGraphRoot(IBaseNode graphRoot)
	{
		this.graphRoot = graphRoot;
		refresh(0);
	}

	@Override
	public IBaseNode getGraphRoot()
	{
		return graphRoot;
	}

	@Override
	public Vector3D getPosition()
	{
		return position;
	}

	public abstract void refresh(float deltaT);
}
