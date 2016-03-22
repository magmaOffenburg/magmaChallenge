package magma.tools.benchmark.model.bench.keepawaychallenge;

import magma.monitor.general.impl.MonitorComponentFactory;
import magma.monitor.referee.IReferee;
import magma.tools.benchmark.model.BenchmarkConfiguration;
import magma.tools.benchmark.model.ISingleResult;
import magma.tools.benchmark.model.TeamConfiguration;
import magma.tools.benchmark.model.bench.BenchmarkMain;
import magma.tools.benchmark.model.bench.RunInformation;
import magma.tools.benchmark.model.bench.SingleResult;
import magma.tools.benchmark.model.bench.TeamResult;

public class KeepAwayBenchmark extends BenchmarkMain
{
	public static final int PLAYERS = 4;

	public KeepAwayBenchmark(String roboVizServer)
	{
		super(roboVizServer);
		allowedPlayers = PLAYERS;
		allowPlayerBeaming = true;
	}

	@Override
	protected ISingleResult benchmarkResults()
	{
		boolean valid = false;
		if (monitor != null) {
			KeepAwayBenchmarkReferee referee = (KeepAwayBenchmarkReferee) monitor
					.getReferee();
			if (referee.getState() == IReferee.RefereeState.STOPPED) {
				valid = true;
			} else {
				statusText += referee.getStatusText();
			}
		}

		return new SingleResult(valid, false, false, statusText);
	}

	@Override
	protected MonitorComponentFactory createMonitorFactory(
			BenchmarkConfiguration config, TeamConfiguration teamConfig,
			RunInformation runInfo, String roboVizServer)
	{
		return new KeepAwayMonitorComponentFactory(
				createFactoryParameter(config, teamConfig), runInfo, roboVizServer);
	}

	@Override
	protected TeamResult createTeamResult(TeamConfiguration currentTeamConfig)
	{
		return new TeamResult(currentTeamConfig.getName()) {
			@Override
			public float getAverageScore()
			{
				return 0;
			}
		};
	}
}
