/* Copyright 2008 - 2017 Hochschule Offenburg
 * For a list of authors see README.md
 * This software of HSOAutonomy is released under MIT License (see LICENSE).
 */
package hso.autonomy.util.geometry.interpolation;

import hso.autonomy.util.geometry.Pose6D;
import java.io.Serializable;

/**
 * Interface for a pose interpolator.
 *
 * @author Stefan Glaser
 */
public interface IPoseInterpolator extends Serializable
{
	/**
	 * @param initialPose: the initial pose
	 * @param targetPose: the target pose
	 * @param t: the interpolation progress (0...1)
	 * @return the interpolated pose
	 */
	Pose6D interpolate(Pose6D initialPose, Pose6D targetPose, float t);
}
