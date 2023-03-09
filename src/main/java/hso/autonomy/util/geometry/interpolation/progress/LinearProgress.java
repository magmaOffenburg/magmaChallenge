/* Copyright 2008 - 2017 Hochschule Offenburg
 * For a list of authors see README.md
 * This software of HSOAutonomy is released under MIT License (see LICENSE).
 */
package hso.autonomy.util.geometry.interpolation.progress;

/**
 * @author Stefan Glaser
 */
public class LinearProgress extends ProgressFunction
{
	@Override
	protected float calculateProgress(float t)
	{
		return t;
	}
}
