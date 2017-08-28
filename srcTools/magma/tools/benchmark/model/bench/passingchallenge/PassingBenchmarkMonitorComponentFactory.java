package magma.tools.benchmark.model.bench.passingchallenge;

import magma.monitor.command.IServerCommander;
import magma.monitor.general.impl.FactoryParameter;
import magma.monitor.general.impl.MonitorComponentFactory;
import magma.monitor.referee.IReferee;
import magma.monitor.worldmodel.IMonitorWorldModel;
import magma.tools.benchmark.model.bench.RunInformation;
import magma.tools.benchmark.model.bench.SinglePlayerLauncher;

public class PassingBenchmarkMonitorComponentFactory extends MonitorComponentFactory
{
	private final RunInformation runInfo;

	private final String roboVizServer;

	public PassingBenchmarkMonitorComponentFactory(
			FactoryParameter parameterObject, RunInformation runInfo, String roboVizServer)
	{
		super(parameterObject);
		this.runInfo = runInfo;
		this.roboVizServer = roboVizServer;
	}

	@Override
	public IReferee createReferee(IMonitorWorldModel worldModel, IServerCommander serverCommander, int refereeID)
	{
		SinglePlayerLauncher launcher = new SinglePlayerLauncher(params.getServerIP(), params.getAgentPort(),
				params.getTeam1Jar(), params.getTeam2Name(), "Passing Challenge", false);
		return new PassingBenchmarkReferee(worldModel, serverCommander, params.getServerPid(), launcher,
				params.getDropHeight(), runInfo, roboVizServer);
	}
}
