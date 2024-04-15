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

package magma.tools.benchmark.model.bench.throwinchallenge;

import java.util.Random;
import magma.monitor.general.impl.MonitorComponentFactory;
import magma.monitor.referee.IReferee.RefereeState;
import magma.tools.benchmark.model.BenchmarkConfiguration;
import magma.tools.benchmark.model.ISingleResult;
import magma.tools.benchmark.model.TeamConfiguration;
import magma.tools.benchmark.model.bench.BenchmarkMain;
import magma.tools.benchmark.model.bench.RunInformation;
import magma.tools.benchmark.model.bench.SingleResult;
import magma.tools.benchmark.model.bench.TeamResult;

/**
 *
 * @author kdorer
 */
public class ThrowInBenchmark extends BenchmarkMain
{
	/** how many attempts each team has */
	static final int BENCHMARK_RUNS = 10;

	/** x-axis delta of player to ball (in m) */
	static final double DISTANCE_BEHIND_BALL = 0.6;

	/** noise in player beam position (in m) */
	static final double BEAM_NOISE = 0.1;

	public ThrowInBenchmark(String roboVizServer)
	{
		super(roboVizServer, false);
	}

	@Override
	protected ISingleResult benchmarkResults()
	{
		float avgScore = 0;
		boolean fallen = false;
		boolean penalty = false;
		boolean valid = false;
		if (monitor != null) {
			ThrowInBenchmarkReferee referee = (ThrowInBenchmarkReferee) monitor.getReferee();
			if (referee.getState() == RefereeState.STOPPED) {
				avgScore = (float) referee.getDistanceError();
				fallen = referee.isHasFallen();
				penalty = referee.hasPenalty();
				valid = true;
			} else {
				statusText += referee.getStatusText();
			}
		}
		return new SingleResult(avgScore, valid, fallen, penalty, statusText);
	}

	@Override
	protected MonitorComponentFactory createMonitorFactory(
			BenchmarkConfiguration config, TeamConfiguration teamConfig, RunInformation runInfo, String roboVizServer)
	{
		return new ThrowInBenchmarkMonitorComponentFactory(
				createFactoryParameter(config, teamConfig), runInfo, roboVizServer);
	}

	@Override
	protected TeamResult createTeamResult(TeamConfiguration currentTeamConfig)
	{
		return new ThrowInBenchmarkTeamResult(currentTeamConfig.getName());
	}

	@Override
	protected RunInformation createRunInformation(Random rand, int runID)
	{		
		double beamX = -15;
		double beamY = 0;
		double ballX = beamX + DISTANCE_BEHIND_BALL + noise(rand, BEAM_NOISE);
		double ballY = beamY + noise(rand, BEAM_NOISE);
		
		return new RunInformation(runID, beamX, beamY, ballX, ballY);
	}

	protected double noise(Random rand, double noise)
	{
		return (rand.nextDouble() - 0.5) * 2 * noise;
	}

	/**
	 * The number of different runs or phases this benchmark has.
	 * @return 10 for kick bench
	 */
	@Override
	protected int getBenchmarkRuns()
	{
		return BENCHMARK_RUNS;
	}
}
