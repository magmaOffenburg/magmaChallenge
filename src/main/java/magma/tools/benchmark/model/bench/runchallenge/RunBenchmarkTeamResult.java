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

import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

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

	public double getAverage(ToDoubleFunction<? super RunBenchmarkSingleResult> method)
	{
		return results.stream().map(obj -> (RunBenchmarkSingleResult) obj)
				.collect(Collectors.summarizingDouble(method)).getAverage();
	}
	
	@Override
	public ISingleResult createSingleResult()
	{
		double speed = getAverage(RunBenchmarkSingleResult::getSpeed);
		double offGround = getAverage(RunBenchmarkSingleResult::getOffGround);
		double oneLeg = getAverage(RunBenchmarkSingleResult::getOneLeg);
		double twoLegs = getAverage(RunBenchmarkSingleResult::getTwoLegs);
		
		return new RunBenchmarkSingleResult(isValid(), speed, offGround, oneLeg, twoLegs,  
				isFallen(), hasPenalty(), getStatusText());
	}
}
