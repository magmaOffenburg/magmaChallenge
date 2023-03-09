package hso.autonomy.util.misc;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class PrioritizedPosition
{
	private Vector3D position;

	private float priority;

	public PrioritizedPosition(Vector3D position, float priority)
	{
		this.position = position;
		this.priority = priority;
	}

	public Vector3D getPosition()
	{
		return position;
	}

	public float getPriority()
	{
		return priority;
	}
}
