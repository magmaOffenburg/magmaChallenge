/* Copyright 2008 - 2017 Hochschule Offenburg
 * For a list of authors see README.md
 * This software of HSOAutonomy is released under MIT License (see LICENSE).
 */
package hso.autonomy.util.geometry;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * Represents a circle in 3D space.
 * @author kdorer
 */
public class Circle3D
{
	/** center of the circle */
	private final Vector3D center;

	/** normal to the plane in which the circle is */
	private final Vector3D normal;

	/** radius of the circle */
	private final double radius;

	/**
	 * For now we assume that the circle is parallel to the x-plane. So normal
	 * vector is ignored.
	 */
	public Circle3D(Vector3D center, double radius)
	{
		this.center = center;
		this.radius = radius;
		this.normal = Vector3D.PLUS_I;
	}

	/**
	 * @return the center
	 */
	public Vector3D getCenter()
	{
		return center;
	}

	/**
	 * @return the normal of the plane in which the circle is
	 */
	public Vector3D getNormal()
	{
		return normal;
	}

	/**
	 * @return the radius
	 */
	public double getRadius()
	{
		return radius;
	}

	@Override
	public String toString()
	{
		return center + " : " + radius;
	}
}
