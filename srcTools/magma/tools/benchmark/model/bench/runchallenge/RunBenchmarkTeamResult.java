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

import magma.tools.benchmark.model.ISingleResult;
import magma.tools.benchmark.model.bench.TeamResult;

/**
 *
 * @author kdorer
 */
public class RunBenchmarkTeamResult extends TeamResult
{
	public RunBenchmarkTeamResult(String name)
	{
		super(name);
	}

	@Override
	public float getAverageScore()
	{
		return getAverageSpeed() + getAverageOffGround();
	}

	public float getAverageSpeed()
	{
		if (results.isEmpty()) {
			return 0.0f;
		}
		float avg = 0;
		for (ISingleResult result : results) {
			if (result instanceof RunBenchmarkTeamResult) {
				avg += ((RunBenchmarkTeamResult) result).getAverageSpeed();
			} else {
				avg += ((RunBenchmarkSingleResult) result).getSpeed();
			}
		}
		return avg / results.size();
	}

	public float getAverageOffGround()
	{
		if (results.isEmpty()) {
			return 0.0f;
		}
		float avg = 0;
		for (ISingleResult result : results) {
			if (result instanceof RunBenchmarkTeamResult) {
				avg += ((RunBenchmarkTeamResult) result).getAverageOffGround();
			} else {
				avg += ((RunBenchmarkSingleResult) result).getOffGround();
			}
		}
		return avg / results.size();
	}

	public float getAverageOneLeg()
	{
		if (results.isEmpty()) {
			return 0.0f;
		}
		float avg = 0;
		for (ISingleResult result : results) {
			if (result instanceof RunBenchmarkTeamResult) {
				avg += ((RunBenchmarkTeamResult) result).getAverageOneLeg();
			} else {
				avg += ((RunBenchmarkSingleResult) result).getOneLeg();
			}
		}
		return avg / results.size();
	}

	public float getAverageTwoLegs()
	{
		if (results.isEmpty()) {
			return 0.0f;
		}
		float avg = 0;
		for (ISingleResult result : results) {
			if (result instanceof RunBenchmarkTeamResult) {
				avg += ((RunBenchmarkTeamResult) result).getAverageTwoLegs();
			} else {
				avg += ((RunBenchmarkSingleResult) result).getTwoLegs();
			}
		}
		return avg / results.size();
	}
}
