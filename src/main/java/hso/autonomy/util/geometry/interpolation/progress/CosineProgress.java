/* Copyright 2008 - 2017 Hochschule Offenburg
 * For a list of authors see README.md
 * This software of HSOAutonomy is released under MIT License (see LICENSE).
 */
package hso.autonomy.util.geometry.interpolation.progress;

/**
 * @author Stefan Glaser
 */
public class CosineProgress extends ProgressFunction
{
	@Override
	protected float calculateProgress(float t)
	{
		return (float) (-1 * ((Math.cos(t * Math.PI) - 1) / 2));
	}
}
