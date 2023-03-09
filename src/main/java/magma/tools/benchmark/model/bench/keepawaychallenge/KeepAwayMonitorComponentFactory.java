package magma.tools.benchmark.model.bench.keepawaychallenge;

import magma.monitor.command.IServerCommander;
import magma.monitor.general.impl.FactoryParameters;
import magma.monitor.general.impl.MonitorComponentFactory;
import magma.monitor.referee.IReferee;
import magma.monitor.worldmodel.IMonitorWorldModel;
import magma.tools.benchmark.model.bench.BenchmarkFactoryParameters;
import magma.tools.benchmark.model.bench.RunInformation;
import magma.tools.benchmark.model.bench.SinglePlayerLauncher;

public class KeepAwayMonitorComponentFactory extends MonitorComponentFactory
{
	private final RunInformation runInfo;

	private final String roboVizServer;

	public KeepAwayMonitorComponentFactory(
			FactoryParameters parameterObject, RunInformation runInfo, String roboVizServer)
	{
		super(parameterObject);
		this.runInfo = runInfo;
		this.roboVizServer = roboVizServer;
	}

	@Override
	public IReferee createReferee(IMonitorWorldModel worldModel, IServerCommander serverCommander)
	{
		BenchmarkFactoryParameters params = (BenchmarkFactoryParameters) this.params;
		SinglePlayerLauncher launcher = new SinglePlayerLauncher(params.getServerIP(), params.getAgentPort(),
				params.getTeamPath(), params.getStartScriptName(), "KeepAwayChallenge", false);
		return new KeepAwayBenchmarkReferee(worldModel, serverCommander, params.getServerPid(), launcher,
				params.getRuntime(), params.getDropHeight(), runInfo, roboVizServer, params.getServerIP(),
				params.getAgentPort());
	}
}
