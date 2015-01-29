package magma.tools.benchmark.model.bench.kickchallenge;

import magma.monitor.command.IServerCommander;
import magma.monitor.general.impl.FactoryParameter;
import magma.monitor.general.impl.MonitorComponentFactory;
import magma.monitor.referee.IReferee;
import magma.monitor.referee.impl.SinglePlayerLauncher;
import magma.monitor.worldmodel.IMonitorWorldModel;

public class KickBenchmarkMonitorComponentFactory extends
		MonitorComponentFactory
{
	protected int currentRun;

	private long randomSeed;

	/**
	 * @param parameterObject TODO
	 */
	public KickBenchmarkMonitorComponentFactory(
			FactoryParameter parameterObject, long randomSeed, int currentRun)
	{
		super(parameterObject);
		this.randomSeed = randomSeed;
		this.currentRun = currentRun;
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
				params.getServerIP(), params.getAgentPort(), params.getTeam1Name(),
				params.getTeam1Jar(), params.getTeam2Name());
		return new KickBenchmarkReferee(worldModel, serverCommander,
				params.getServerPid(), launcher, params.getPlayersPerTeam(),
				params.getDropHeight(), randomSeed, currentRun);
	}
}
