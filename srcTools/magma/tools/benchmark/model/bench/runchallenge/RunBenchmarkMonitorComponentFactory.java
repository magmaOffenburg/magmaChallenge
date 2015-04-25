package magma.tools.benchmark.model.bench.runchallenge;

import magma.monitor.command.IServerCommander;
import magma.monitor.general.impl.FactoryParameter;
import magma.monitor.general.impl.MonitorComponentFactory;
import magma.monitor.referee.IReferee;
import magma.monitor.worldmodel.IMonitorWorldModel;
import magma.tools.benchmark.model.bench.RunInformation;
import magma.tools.benchmark.model.bench.SinglePlayerLauncher;

public class RunBenchmarkMonitorComponentFactory extends
		MonitorComponentFactory
{
	private RunInformation runInfo;

	public RunBenchmarkMonitorComponentFactory(FactoryParameter parameterObject,
			RunInformation runInfo, String roboVizServer)
	{
		super(parameterObject);
		this.runInfo = runInfo;
	}

	/**
	 * Create a Referee
	 * 
	 * @param worldModel - the world model of the monitor
	 * @param serverCommander - the command interface to send server commands
	 * @param refereeID - 0 (default) for dummy referee, 1 for standard game
	 *        referee
	 * @return New Referee
	 */
	@Override
	public IReferee createReferee(IMonitorWorldModel worldModel,
			IServerCommander serverCommander, int refereeID)
	{
		SinglePlayerLauncher launcher = new SinglePlayerLauncher(
				params.getServerIP(), params.getAgentPort(), params.getTeam1Jar(),
				params.getTeam2Name(), "RunChallenge");
		return new RunBenchmarkReferee(worldModel, serverCommander,
				params.getServerPid(), launcher, params.getPlayersPerTeam(),
				params.getDropHeight(), runInfo);
	}
}
