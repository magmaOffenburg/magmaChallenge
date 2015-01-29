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
	private boolean fallen;

	private boolean valid;

	private String statusText;

	public SingleResult(boolean valid, boolean fallen, String statusText)
	{
		this.valid = valid;
		this.fallen = fallen;
		this.statusText = statusText;
	}

	/* (non-Javadoc)
	 * @see magma.tools.benchmark.model.bench.ISingleResult#isFallen()
	 */
	@Override
	public boolean isFallen()
	{
		return fallen;
	}

	/* (non-Javadoc)
	 * @see magma.tools.benchmark.model.bench.ISingleResult#isValid()
	 */
	@Override
	public boolean isValid()
	{
		return valid;
	}

	/* (non-Javadoc)
	 * @see magma.tools.benchmark.model.bench.ISingleResult#getStatusText()
	 */
	@Override
	public String getStatusText()
	{
		return statusText;
	}
}
