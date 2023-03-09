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

import java.util.Random;

import hso.autonomy.util.geometry.Angle;
import hso.autonomy.util.geometry.Pose2D;
import magma.monitor.general.impl.MonitorComponentFactory;
import magma.monitor.referee.IReferee.RefereeState;
import magma.tools.benchmark.model.BenchmarkConfiguration;
import magma.tools.benchmark.model.ISingleResult;
import magma.tools.benchmark.model.TeamConfiguration;
import magma.tools.benchmark.model.bench.BenchmarkMain;
import magma.tools.benchmark.model.bench.RunInformation;
import magma.tools.benchmark.model.bench.TeamResult;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 *
 * @author kdorer
 */
public class RollingBallBenchmark extends BenchmarkMain
{
	/** how many attempts each team has */
	static final int BENCHMARK_RUNS = 10;

	/** x-axis delta of player to ball (in m) */
	static final double DISTANCE_BEHIND_BALL = 0.6;

	/** half opening angle of ball positions (in degrees) */
	static final double BEAM_ANGLE = 30;

	/** noise in player beam position (in m) */
	static final double BEAM_NOISE = 0.1;

	public RollingBallBenchmark(String roboVizServer)
	{
		super(roboVizServer, false);
	}

	@Override
	protected ISingleResult benchmarkResults()
	{
		float distance = 0;
		float deltaY = 0;
		boolean fallen = false;
		boolean penalty = false;
		boolean valid = false;
		if (monitor != null) {
			RollingBallBenchmarkReferee referee = (RollingBallBenchmarkReferee) monitor.getReferee();
			if (referee.getState() == RefereeState.STOPPED) {
				distance = (float) referee.getDistance();
				deltaY = (float) referee.getDeltaY();
				fallen = referee.isHasFallen();
				penalty = referee.hasPenalty();
				valid = true;
			} else {
				statusText += referee.getStatusText();
			}
		}
		return new RollingBallBenchmarkSingleResult(valid, fallen, penalty, statusText, distance, deltaY);
	}

	@Override
	protected MonitorComponentFactory createMonitorFactory(
			BenchmarkConfiguration config, TeamConfiguration teamConfig, RunInformation runInfo, String roboVizServer)
	{
		return new RollingBallBenchmarkMonitorComponentFactory(
				createFactoryParameter(config, teamConfig), runInfo, roboVizServer);
	}

	@Override
	protected TeamResult createTeamResult(TeamConfiguration currentTeamConfig)
	{
		return new RollingBallBenchmarkTeamResult(currentTeamConfig.getName());
	}

	@Override
	protected RunInformation createRunInformation(Random rand, int runID)
	{
		// define how far and at what angle the ball may be
		double radius = rand.nextDouble() * 3 + 1;
		Angle angle = Angle.deg(rand.nextDouble() * 180 - 90);
		float speedFactor = (float) radius * 2;
		double beamX = -12;
		double beamY = 0;
		Pose2D beamPose = new Pose2D(beamX, beamY);
		
		Vector2D direction = angle.applyTo(new Vector2D(1, 0));
		Vector2D destinationBallPosition = beamPose.getPosition().add(new Vector2D(0.15 + noise(rand, 0.05), 0 + noise(rand, 0.05)));
		Vector2D ballStart = destinationBallPosition.add(direction.scalarMultiply(radius));
		double ballX = ballStart.getX();
		double ballY = ballStart.getY();
		
		double speedX = -direction.getX() * speedFactor;
		double speedY = -direction.getY() * speedFactor;
		
		return new RunInformation(runID, beamX, beamY, ballX, ballY, speedX, speedY, 0);
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
