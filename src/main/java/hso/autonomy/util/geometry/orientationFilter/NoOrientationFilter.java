/* Copyright 2008 - 2017 Hochschule Offenburg
 * For a list of authors see README.md
 * This software of HSOAutonomy is released under MIT License (see LICENSE).
 */
package hso.autonomy.util.geometry.orientationFilter;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;

public class NoOrientationFilter implements IOrientationFilter
{
	public NoOrientationFilter()
	{
	}

	@Override
	public Rotation filterOrientation(Rotation newOrientation)
	{
		return newOrientation;
	}

	@Override
	public void reset()
	{
	}
}
