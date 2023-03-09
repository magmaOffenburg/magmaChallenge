/* Copyright 2009 Hochschule Offenburg
 * Klaus Dorer, Mathias Ehret, Stefan Glaser, Thomas Huber,
 * Simon Raffeiner, Srinivasa Ragavan, Thomas Rinklin,
 * Joachim Schilling, Rajit Shahi
 *
 * This file is part of magmaOffenburg.
 *
 * magmaOffenburg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * magmaOffenburg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with magmaOffenburg. If not, see <http://www.gnu.org/licenses/>.
 */

package magma.tools.benchmark.model.bench.rollingballchallenge;

import magma.tools.benchmark.model.bench.SingleResult;

/**
 *
 * @author kdorer
 */
public class RollingBallBenchmarkSingleResult extends SingleResult
{
	private final double distance;
	private final double deltaY;

	public RollingBallBenchmarkSingleResult(boolean valid, boolean fallen, boolean penalty, String statusText, 
			double distance, double deltaY)
	{
		super(calculateScore(distance, deltaY, fallen), valid, fallen, penalty, statusText);
		this.distance = distance;
		this.deltaY = deltaY;
	}

	private static double calculateScore(double distance, double deltaY, boolean fallen)
	{
		return distance - deltaY - 3 * ((fallen) ?1 :0);
	}

	public double getDistance()
	{
		return distance;
	}

	public double getDeltaY()
	{
		return deltaY;
	}
}
