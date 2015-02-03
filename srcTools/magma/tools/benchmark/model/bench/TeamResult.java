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

import java.util.ArrayList;
import java.util.List;

import magma.tools.benchmark.model.ISingleResult;
import magma.tools.benchmark.model.ITeamResult;

/**
 * 
 * @author kdorer
 */
public abstract class TeamResult implements ITeamResult
{
	private String name;

	protected List<ISingleResult> results;

	/**
	 * @param name
	 */
	public TeamResult(String name)
	{
		this.name = name;
		results = new ArrayList<ISingleResult>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * magma.tools.benchmark.model.ITeamResult#addResult(magma.tools.benchmark
	 * .model.SingleResult)
	 */
	@Override
	public void addResult(ISingleResult result)
	{
		results.add(result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see magma.tools.benchmark.model.ITeamResult#getAverageScore()
	 */
	@Override
	public abstract float getAverageScore();

	/*
	 * (non-Javadoc)
	 * 
	 * @see magma.tools.benchmark.model.ITeamResult#getFallenCount()
	 */
	@Override
	public int getFallenCount()
	{
		if (results.isEmpty()) {
			return 0;
		}
		int fallen = 0;
		for (ISingleResult result : results) {
			if (result.isFallen()) {
				fallen++;
			}
		}
		return fallen;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see magma.tools.benchmark.model.ITeamResult#isValid()
	 */
	@Override
	public boolean isValid()
	{
		if (results.isEmpty()) {
			return false;
		}
		for (ISingleResult result : results) {
			if (!result.isValid()) {
				return false;
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see magma.tools.benchmark.model.ITeamResult#size()
	 */
	@Override
	public int size()
	{
		return results.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see magma.tools.benchmark.model.ITeamResult#getName()
	 */
	@Override
	public String getName()
	{
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see magma.tools.benchmark.model.ITeamResult#getStatusText()
	 */
	@Override
	public String getStatusText()
	{
		if (results.isEmpty()) {
			return "No results.";
		}
		StringBuffer buffer = new StringBuffer(1000);
		int i = 0;
		for (ISingleResult result : results) {
			if (!result.getStatusText().isEmpty()) {
				buffer.append(i + 1).append(": ");
				buffer.append(result.getStatusText()).append("\n");
			}
			i++;
		}
		String result = buffer.toString();
		if (result.isEmpty()) {
			result = "No problems.";
		}
		return result;
	}

	@Override
	public boolean isFallen()
	{
		return getFallenCount() > 0;
	}

	@Override
	public ISingleResult getResult(int n)
	{
		return results.get(n);
	}
}
