/* Copyright 2008 - 2017 Hochschule Offenburg
 * For a list of authors see README.md
 * This software of HSOAutonomy is released under MIT License (see LICENSE).
 */
package hso.autonomy.util.geometry.positionFilter;

import java.util.Iterator;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * Interpolate a more reliable position value for an object from a list of past,
 * less reliable values, but accept high position changes unfiltered
 *
 * @author Klaus Dorer
 */
public class LowFrequencyPositionFilter extends PositionFilter
{
	/** time when the last reset happened */
	private float lastResetTime = -3;
	/** amount of time that has to pass to allow next filter reset */
	private float resetLockTime;
	/** delta of new position from filtered to trigger a reset */
	private float resetOffset;

	/**
	 * Constructor
	 *
	 * @param filterCycles Internal buffer size for past position values
	 */
	public LowFrequencyPositionFilter(int filterCycles, float resetLockTime, float resetOffset)
	{
		super(filterCycles);
		this.resetLockTime = resetLockTime;
		this.resetOffset = resetOffset;
	}

	/**
	 * Execute a filter pass: takes the current position of an object, adds it to
	 * the internal buffer, and interpolates a (hopefully) more reliable
	 * estimate.
	 *
	 * @return Filtered position
	 */
	public Vector3D filterPosition(Vector3D newPosition, Vector3D oldPosition, float time)
	{
		if (newPosition == null)
			return null;

		Vector3D speed = getAverageSpeed();
		if (filterBuffer.size() > 1) {
			if (time - lastResetTime > resetLockTime) {
				// only reset every second
				Vector3D oldPos = filterBuffer.peek();
				double delta = newPosition.subtract(oldPos.add(speed)).getNorm();
				if (delta > resetOffset) {
					// we accept very high changes
					// System.out.println("clear filterbuffer: " + delta);
					reset();
					lastResetTime = time;
					filterBuffer.push(oldPos);
				}
			} else {
				// System.out.println("prevented reset");
			}
		}
		return super.filterPosition(newPosition, oldPosition);
	}

	private Vector3D getAverageSpeed()
	{
		Vector3D speed = new Vector3D(0, 0, 0);
		if (filterBuffer.size() < 2) {
			return speed;
		}

		Iterator<Vector3D> filterIterator = filterBuffer.iterator();
		Vector3D newPosition = filterIterator.next();
		while (filterIterator.hasNext()) {
			Vector3D oldPosition = filterIterator.next();
			speed = speed.add(newPosition.subtract(oldPosition));
			newPosition = oldPosition;
		}
		return speed.scalarMultiply(1f / (filterBuffer.size() - 1));
	}
}
