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
public class RunBenchmark extends BenchmarkMain
{
	public RunBenchmark(String roboVizServer)
	{
		super(roboVizServer);
	}

	@Override
	protected ISingleResult benchmarkResults()
	{
		float avgSpeed = 0;
		float bothLegsOffGround = 0;
		float oneLegOffGround = 0;
		float noLegOffGround = 0;
		boolean fallen = false;
		boolean penalty = false;
		boolean valid = false;
		if (monitor != null) {
			RunBenchmarkReferee referee = (RunBenchmarkReferee) monitor
					.getReferee();
			if (referee.getState() == RefereeState.STOPPED) {
				avgSpeed = referee.getAverageSpeed();
				bothLegsOffGround = proxy.getBothLegsOffGround()
						/ (referee.getRunTime() * 50f);
				oneLegOffGround = proxy.getOneLegOffGround()
						/ (referee.getRunTime() * 50f);
				noLegOffGround = proxy.getNoLegOffGround()
						/ (referee.getRunTime() * 50f);
				fallen = referee.isHasFallen();
				penalty = referee.hasPenalty();
				valid = true;
			} else {
				statusText += referee.getStatusText();
			}
		}
		return new RunBenchmarkSingleResult(valid, avgSpeed, bothLegsOffGround,
				oneLegOffGround, noLegOffGround, fallen, penalty, statusText);
	}

	@Override
	protected MonitorComponentFactory createMonitorFactory(
			BenchmarkConfiguration config, TeamConfiguration teamConfig,
			RunInformation runInfo, String roboVizServer)
	{
		return new RunBenchmarkMonitorComponentFactory(new FactoryParameter(null,
				config.getServerIP(), config.getAgentPort(), teamConfig.getName(),
				teamConfig.getPath(), teamConfig.getLaunch(), null,
				config.getRuntime(), teamConfig.getDropHeight()), runInfo,
				roboVizServer);
	}

	@Override
	protected TeamResult createTeamResult(TeamConfiguration currentTeamConfig)
	{
		return new RunBenchmarkTeamResult(currentTeamConfig.getName());
	}
}
