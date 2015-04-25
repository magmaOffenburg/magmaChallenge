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

package magma.tools.benchmark.model.bench.kickchallenge;

import java.util.Random;

import magma.monitor.general.impl.FactoryParameter;
import magma.monitor.general.impl.MonitorComponentFactory;
import magma.monitor.referee.IReferee.RefereeState;
import magma.tools.benchmark.model.BenchmarkConfiguration;
import magma.tools.benchmark.model.ISingleResult;
import magma.tools.benchmark.model.TeamConfiguration;
import magma.tools.benchmark.model.bench.BenchmarkMain;
import magma.tools.benchmark.model.bench.RunInformation;
import magma.tools.benchmark.model.bench.TeamResult;

/**
 * 
 * @author kdorer
 */
public class KickBenchmark extends BenchmarkMain
{
	/** how many attempts each team has */
	static final int BENCHMARK_RUNS = 10;

	/** x-axis delta of player to ball (in m) */
	static final double DISTANCE_BEHIND_BALL = 0.6;

	/** half opening angle of ball positions (in degrees) */
	static final double BEAM_ANGLE = 30;

	/** noise in player beam position (in m) */
	static final double BEAM_NOISE = 0.1;

	public KickBenchmark(String roboVizServer)
	{
		super(roboVizServer);
	}

	@Override
	protected ISingleResult benchmarkResults()
	{
		float avgScore = 0;
		boolean fallen = false;
		boolean penalty = false;
		boolean valid = false;
		if (monitor != null) {
			KickBenchmarkReferee referee = (KickBenchmarkReferee) monitor
					.getReferee();
			if (referee.getState() == RefereeState.STOPPED) {
				avgScore = (float) referee.getDistanceError();
				fallen = referee.isHasFallen();
				penalty = referee.hasPenalty();
				valid = true;
			} else {
				statusText += referee.getStatusText();
			}
		}
		return new KickBenchmarkSingleResult(valid, fallen, penalty, statusText,
				avgScore);
	}

	@Override
	protected MonitorComponentFactory createMonitorFactory(
			BenchmarkConfiguration config, TeamConfiguration teamConfig,
			RunInformation runInfo, String roboVizServer)
	{
		return new KickBenchmarkMonitorComponentFactory(new FactoryParameter(
				null, config.getServerIP(), config.getAgentPort(),
				teamConfig.getName(), teamConfig.getPath(), teamConfig.getLaunch(),
				null, config.getRuntime(), teamConfig.getDropHeight()), runInfo,
				roboVizServer);
	}

	@Override
	protected TeamResult createTeamResult(TeamConfiguration currentTeamConfig)
	{
		return new KickBenchmarkTeamResult(currentTeamConfig.getName());
	}

	@Override
	protected RunInformation createRunInformation(Random rand, int runID)
	{
		double radius = (runID + 3);
		double angle = Math.toRadians(noise(rand, BEAM_ANGLE));
		double ballX = -radius * Math.cos(angle);
		double ballY = radius * Math.sin(angle);
		double beamX = ballX - DISTANCE_BEHIND_BALL + noise(rand, BEAM_NOISE);
		double beamY = ballY + noise(rand, BEAM_NOISE);
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
