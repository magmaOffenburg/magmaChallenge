/* Copyright 2008 - 2017 Hochschule Offenburg
 * For a list of authors see README.md
 * This software of HSOAutonomy is released under MIT License (see LICENSE).
 */
package hso.autonomy.util.geometry.interpolation;

import java.io.Serializable;

/**
 * Interface for a value interpolator.
 *
 * @author Stefan Glaser
 */
public interface IValueInterpolator extends Serializable
{
	/**
	 * @param initial: the initial value
	 * @param target: the target value
	 * @param t: the interpolation progress (0...1)
	 * @return the interpolated value
	 */
	double interpolate(double initial, double target, float t);
}
