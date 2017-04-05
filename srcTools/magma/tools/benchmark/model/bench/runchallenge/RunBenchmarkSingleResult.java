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
	private final float speed;

	private final float offGround;

	private final float oneLeg;

	private final float twoLegs;

	public RunBenchmarkSingleResult(boolean valid, float speed, float offGround, float oneLeg, float twoLegs,
			boolean fallen, boolean penalty, String statusText)
	{
		super(valid, fallen, penalty, statusText);
		this.speed = speed;
		this.offGround = offGround;
		this.oneLeg = oneLeg;
		this.twoLegs = twoLegs;
	}

	public float getSpeed()
	{
		return speed;
	}

	public float getOffGround()
	{
		return offGround;
	}

	public float getOneLeg()
	{
		return oneLeg;
	}

	public float getTwoLegs()
	{
		return twoLegs;
	}
}
