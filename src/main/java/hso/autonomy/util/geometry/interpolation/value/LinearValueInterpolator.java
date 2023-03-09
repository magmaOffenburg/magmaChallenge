/* Copyright 2008 - 2017 Hochschule Offenburg
 * For a list of authors see README.md
 * This software of HSOAutonomy is released under MIT License (see LICENSE).
 */
package hso.autonomy.util.geometry.interpolation.value;

import hso.autonomy.util.geometry.interpolation.progress.LinearProgress;
import hso.autonomy.util.geometry.interpolation.progress.ProgressFunction;

/**
 * @author Stefan Glaser
 */
public class LinearValueInterpolator extends ValueInterpolatorBase
{
	public LinearValueInterpolator()
	{
		this(new LinearProgress());
	}

	public LinearValueInterpolator(ProgressFunction progress)
	{
		super(progress);
	}

	@Override
	protected double calculateInterpolationValue(double initial, double target, float t)
	{
		return initial + ((target - initial) * t);
	}
}
