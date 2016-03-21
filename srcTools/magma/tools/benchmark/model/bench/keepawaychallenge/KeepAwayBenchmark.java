package magma.tools.benchmark.model.bench.keepawaychallenge;

import magma.monitor.general.impl.MonitorComponentFactory;
import magma.tools.benchmark.model.BenchmarkConfiguration;
import magma.tools.benchmark.model.ISingleResult;
import magma.tools.benchmark.model.TeamConfiguration;
import magma.tools.benchmark.model.bench.BenchmarkMain;
import magma.tools.benchmark.model.bench.RunInformation;
import magma.tools.benchmark.model.bench.TeamResult;

public class KeepAwayBenchmark extends BenchmarkMain
{
	public KeepAwayBenchmark(String roboVizServer)
	{
		super(roboVizServer);
	}

	@Override
	protected ISingleResult benchmarkResults()
	{
		return null;
	}

	@Override
	protected MonitorComponentFactory createMonitorFactory(
			BenchmarkConfiguration config, TeamConfiguration teamConfig,
			RunInformation runInfo, String roboVizServer)
	{
		return null;
	}

	@Override
	protected TeamResult createTeamResult(TeamConfiguration currentTeamConfig)
	{
		return null;
	}
}
