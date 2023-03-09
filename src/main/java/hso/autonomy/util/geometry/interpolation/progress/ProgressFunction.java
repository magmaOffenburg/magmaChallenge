/* Copyright 2008 - 2017 Hochschule Offenburg
 * For a list of authors see README.md
 * This software of HSOAutonomy is released under MIT License (see LICENSE).
 */
package hso.autonomy.util.geometry.interpolation.progress;

import java.io.Serializable;

/**
 * Represents a interpolation progress function. Progress functions can be used
 * to change the interpolation rate dynamically for other interpolators and
 * thereby alter their interpolation behavior.
 *
 * @author Stefan Glaser
 */
public abstract class ProgressFunction implements Serializable
{
	/**
	 * @param t: the interpolation progress (0...1)
	 * @return the transformed interpolation progress (0...1)
	 */
	public float getProgress(float t)
	{
		float progress = calculateProgress(t);

		if (progress < 0) {
			progress = 0;
		} else if (progress > 1) {
			progress = 1;
		}

		return progress;
	}

	/**
	 * @param t: the interpolation progress (0...1)
	 * @return the transformed interpolation progress (0...1)
	 */
	protected abstract float calculateProgress(float t);
}
