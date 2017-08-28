package magma.tools.benchmark.model.bench.runchallenge;

import magma.monitor.command.IServerCommander;
import magma.monitor.general.impl.FactoryParameter;
import magma.monitor.general.impl.MonitorComponentFactory;
import magma.monitor.referee.IReferee;
import magma.monitor.worldmodel.IMonitorWorldModel;
import magma.tools.benchmark.ChallengeType;
import magma.tools.benchmark.model.bench.RunInformation;
import magma.tools.benchmark.model.bench.SinglePlayerLauncher;
import magma.tools.benchmark.model.proxy.BenchmarkAgentProxyServer;

public class RunBenchmarkMonitorComponentFactory extends MonitorComponentFactory
{
	private final RunInformation runInfo;

	private final boolean isGazebo;

	private final BenchmarkAgentProxyServer proxy;

	public RunBenchmarkMonitorComponentFactory(
			FactoryParameter parameterObject, RunInformation runInfo, boolean isGazebo, BenchmarkAgentProxyServer proxy)
	{
		super(parameterObject);
		this.runInfo = runInfo;
		this.isGazebo = isGazebo;
		this.proxy = proxy;
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
	public IReferee createReferee(IMonitorWorldModel worldModel, IServerCommander serverCommander, int refereeID)
	{
		SinglePlayerLauncher launcher = new SinglePlayerLauncher(params.getServerIP(), params.getAgentPort(),
				params.getTeam1Jar(), params.getTeam2Name(), ChallengeType.RUN.startScriptArgument, isGazebo);
		return new RunBenchmarkReferee(worldModel, serverCommander, params.getServerPid(), launcher,
				params.getPlayersPerTeam(), params.getDropHeight(), runInfo, isGazebo, proxy);
	}
}
