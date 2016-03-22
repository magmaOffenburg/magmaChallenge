package magma.tools.benchmark.model.bench.keepawaychallenge;

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
	public KeepAwayBenchmarkReferee(IMonitorWorldModel mWorldModel,
			IServerCommander serverCommander, String serverPid,
			SinglePlayerLauncher launcher, float runTime, float dropHeight,
			RunInformation runInfo, String roboVizServer)
	{
		super(mWorldModel, serverCommander, serverPid, launcher, runTime,
				dropHeight, runInfo);
		RoboVizDraw.initialize(new RoboVizParameters(true, roboVizServer,
				RoboVizDraw.DEFAULT_PORT));
	}

	@Override
	protected boolean onDuringLaunching()
	{
		return super.onDuringLaunching();
	}

	@Override
	protected boolean onStartBenchmark()
	{
		state = RefereeState.CONNECTED;

		if (worldModel.getSoccerAgents().size() < KeepAwayBenchmark.PLAYERS) {
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
