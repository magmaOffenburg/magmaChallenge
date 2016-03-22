package magma.tools.benchmark.model.bench.keepawaychallenge;

import java.nio.file.Paths;

import magma.common.spark.PlayMode;
import magma.monitor.command.IServerCommander;
import magma.monitor.worldmodel.IMonitorWorldModel;
import magma.tools.benchmark.model.bench.BenchmarkRefereeBase;
import magma.tools.benchmark.model.bench.RunInformation;
import magma.tools.benchmark.model.bench.SinglePlayerLauncher;
import magma.util.roboviz.RoboVizDraw;
import magma.util.roboviz.RoboVizParameters;

public class KeepAwayBenchmarkReferee extends BenchmarkRefereeBase
{
	private SinglePlayerLauncher opponentLauncher;

	private boolean opponentLaunched = false;

	public KeepAwayBenchmarkReferee(IMonitorWorldModel mWorldModel,
			IServerCommander serverCommander, String serverPid,
			SinglePlayerLauncher launcher, float runTime, float dropHeight,
			RunInformation runInfo, String roboVizServer, String serverIP,
			int agentPort)
	{
		super(mWorldModel, serverCommander, serverPid, launcher, runTime,
				dropHeight, runInfo);
		RoboVizDraw.initialize(new RoboVizParameters(true, roboVizServer,
				RoboVizDraw.DEFAULT_PORT));
		opponentLauncher = new SinglePlayerLauncher(serverIP, agentPort,
				Paths.get("config/keepaway").toAbsolutePath().toString(),
				"startOpponent.sh", "KeepAwayChallenge");
	}

	@Override
	protected boolean onStartBenchmark()
	{
		state = RefereeState.CONNECTED;

		int players = worldModel.getSoccerAgents().size();
		if (players < KeepAwayBenchmark.PLAYERS - 1 && !opponentLaunched) {
			opponentLauncher.launchPlayer(new RunInformation(),
					KeepAwayBenchmark.PLAYERS - 1);
			opponentLaunched = true;
			return false;
		} else if (players < KeepAwayBenchmark.PLAYERS) {
			return false;
		}

		serverCommander.setPlaymode(PlayMode.PLAY_ON);
		return true;
	}

	@Override
	protected boolean onDuringBenchmark()
	{
		return false;
	}

	@Override
	protected void onStopBenchmark()
	{

	}
}
