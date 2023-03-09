/* Copyright 2008 - 2017 Hochschule Offenburg
 * For a list of authors see README.md
 * This software of HSOAutonomy is released under MIT License (see LICENSE).
 */
package hso.autonomy.util.geometry.positionFilter;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class LinearWeightedPositionFilter extends PositionFilterBase
{
	public LinearWeightedPositionFilter()
	{
		super();
	}

	/**
	 * Constructor
	 *
	 * @param i Internal buffer size for past position values
	 */
	public LinearWeightedPositionFilter(int i)
	{
		super(i);
	}

	@Override
	protected Vector3D calculateNewPosition()
	{
		Vector3D sum = new Vector3D(0, 0, 0);
		int i = filterBuffer.size();
		for (Vector3D v : filterBuffer) {
			sum = sum.add(v.scalarMultiply(i));
			i--;
		}

		return sum.scalarMultiply(1f / ((filterBuffer.size() * (filterBuffer.size() + 1)) / 2));
	}
}
