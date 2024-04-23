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

package magma.tools.benchmark.model.bench;

import magma.tools.benchmark.model.ISingleResult;

/**
 * Common base class for all results of a single benchmark run
 * @author kdorer
 */
public class SingleResult implements ISingleResult
{
	private final double score;

	private final boolean fallen;

	private final boolean penalty;

	private final boolean valid;

	private final String statusText;

	public SingleResult(double score, boolean valid, boolean fallen, boolean penalty, String statusText)
	{
		this.score = score;
		this.valid = valid;
		this.fallen = fallen;
		this.penalty = penalty;
		this.statusText = statusText;
	}

	@Override
	public boolean isFallen()
	{
		return fallen;
	}

	@Override
	public boolean isValid()
	{
		return valid;
	}

	@Override
	public String getStatusText()
	{
		return statusText;
	}

	@Override
	public boolean hasPenalty()
	{
		return penalty;
	}

	@Override
	public double getScore()
	{
		return score;
	}
}
