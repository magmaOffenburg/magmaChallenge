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

package magma.tools.benchmark.model.bench.runchallenge;

import magma.tools.benchmark.model.bench.SingleResult;

/**
 *
 * @author kdorer
 */
public class RunBenchmarkSingleResult extends SingleResult
{
	private final double speed;

	private final double offGround;

	private final double oneLeg;

	private final double twoLegs;

	public RunBenchmarkSingleResult(boolean valid, double speed, double offGround, double oneLeg, double twoLegs,
			boolean fallen, boolean penalty, String statusText)
	{
		super(calculateScore(speed, offGround), valid, fallen, penalty, statusText);
		this.speed = speed;
		this.offGround = offGround;
		this.oneLeg = oneLeg;
		this.twoLegs = twoLegs;
	}

	private static double calculateScore(double speed, double offGround)
	{
		return speed + offGround;
	}

	public double getSpeed()
	{
		return speed;
	}

	public double getOffGround()
	{
		return offGround;
	}

	public double getOneLeg()
	{
		return oneLeg;
	}

	public double getTwoLegs()
	{
		return twoLegs;
	}
}
