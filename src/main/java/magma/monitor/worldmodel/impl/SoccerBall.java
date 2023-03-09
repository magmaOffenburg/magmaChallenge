package magma.monitor.worldmodel.impl;

import magma.monitor.worldmodel.ISoccerBall;
import magma.util.scenegraph.NodeType;
import magma.util.scenegraph.impl.TransformNode;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class SoccerBall extends SimulationObject implements ISoccerBall
{
	private float radius;

	private float mass;

	public SoccerBall()
	{
	}

	@Override
	public float getRadius()
	{
		return radius;
	}

	@Override
	public float getMass()
	{
		return mass;
	}

	public void setRadius(float radius)
	{
		this.radius = radius;
	}

	public void setMass(float mass)
	{
		this.mass = mass;
	}

	@Override
	public void refresh(float deltaT)
	{
		if (graphRoot != null && graphRoot.getNodeType() == NodeType.TRANSFORM) {
			float[] matrix = ((TransformNode) graphRoot).getLocalTransformation();

			position = new Vector3D(matrix[12], matrix[13], matrix[14]);
		}
	}
}
