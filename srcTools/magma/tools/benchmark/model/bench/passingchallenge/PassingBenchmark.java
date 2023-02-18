package magma.tools.benchmark.model.bench.passingchallenge;

import magma.monitor.general.impl.MonitorComponentFactory;
import magma.monitor.referee.IReferee;
import magma.tools.benchmark.model.BenchmarkConfiguration;
import magma.tools.benchmark.model.ISingleResult;
import magma.tools.benchmark.model.TeamConfiguration;
import magma.tools.benchmark.model.bench.BenchmarkMain;
import magma.tools.benchmark.model.bench.RunInformation;
import magma.tools.benchmark.model.bench.TeamResult;

public class PassingBenchmark extends BenchmarkMain
{
	public static final int PLAYERS = 4;

	public static final int AVG_OUT_OF_BEST = 3;

	public PassingBenchmark(String roboVizServer)
	{
		super(roboVizServer, false);
		allowedPlayers = PLAYERS;
		allowPlayerBeaming = true;
	}

	@Override
	protected ISingleResult benchmarkResults()
	{
		float time = 0;
		boolean valid = false;
		if (monitor != null) {
			PassingBenchmarkReferee referee = (PassingBenchmarkReferee) monitor.getReferee();
			if (referee.getState() == IReferee.RefereeState.STOPPED) {
				valid = true;
				time = referee.getTime();
			} else {
				statusText += referee.getStatusText();
			}
		}
		return new PassingBenchmarkSingleResult(valid, false, false, statusText, time);
	}

	@Override
	protected MonitorComponentFactory createMonitorFactory(
			BenchmarkConfiguration config, TeamConfiguration teamConfig, RunInformation runInfo, String roboVizServer)
	{
		return new PassingBenchmarkMonitorComponentFactory(
				createFactoryParameter(config, teamConfig), runInfo, roboVizServer);
	}

	@Override
	protected TeamResult createTeamResult(TeamConfiguration currentTeamConfig)
	{
		return new PassingBenchmarkTeamResult(currentTeamConfig.getName());
	}
}
