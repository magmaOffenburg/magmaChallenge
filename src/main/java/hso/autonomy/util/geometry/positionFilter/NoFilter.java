/* Copyright 2008 - 2017 Hochschule Offenburg
 * For a list of authors see README.md
 * This software of HSOAutonomy is released under MIT License (see LICENSE).
 */
package hso.autonomy.util.geometry.positionFilter;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 *
 * @author kdorer
 */
public class NoFilter extends BaseFilter
{
	@Override
	public Vector3D filterPosition(Vector3D newPosition, Vector3D oldPosition, Vector3D speed)
	{
		return newPosition;
	}
}
