package magma.tools.benchmark.model.bench.keepawaychallenge;

import java.awt.Color;
import java.nio.file.Paths;

import magma.common.challenge.KeepAwayArea;
import magma.common.spark.PlayMode;
import magma.monitor.command.IServerCommander;
import magma.monitor.worldmodel.IMonitorWorldModel;
import magma.tools.benchmark.model.bench.BenchmarkRefereeBase;
import magma.tools.benchmark.model.bench.RunInformation;
import magma.tools.benchmark.model.bench.SinglePlayerLauncher;
import magma.util.geometry.Area2D;
import magma.util.roboviz.RoboVizDraw;
import magma.util.roboviz.RoboVizParameters;

public class KeepAwayBenchmarkReferee extends BenchmarkRefereeBase
{
	private static final String ROBOVIZ_GROUP = "keepAwayChallenge.area";

	private final SinglePlayerLauncher opponentLauncher;

	private boolean opponentLaunched = false;

	private int delay = 10;

	public KeepAwayBenchmarkReferee(IMonitorWorldModel mWorldModel,
			IServerCommander serverCommander, String serverPid,
			SinglePlayerLauncher launcher, float runTime, float dropHeight,
			RunInformation runInfo, String roboVizServer, String serverIP,
			int agentPort)
	{
		super(mWorldModel, serverCommander, serverPid, launcher, runTime,
				runInfo);

		RoboVizDraw.initialize(new RoboVizParameters(true, roboVizServer,
				RoboVizDraw.DEFAULT_PORT));
		drawArea();

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

		// give the opponent time to beam before starting
		if (delay > 0) {
			delay--;
			return false;
		}

		serverCommander.setPlaymode(PlayMode.PLAY_ON);
		state = RefereeState.STARTED;
		return true;
	}

	@Override
	protected boolean onDuringBenchmark()
	{
		if (state == RefereeState.STARTED
				&& worldModel.getPlayMode() == PlayMode.GAME_OVER) {
			return true;
		}

		drawArea();
		return false;
	}

	private void drawArea()
	{
		Area2D.Float area = KeepAwayArea.calculate(worldModel.getTime());
		RoboVizDraw.drawArea(ROBOVIZ_GROUP, area, 2, Color.RED);
	}

	@Override
	protected void onStopBenchmark()
	{
		state = RefereeState.STOPPED;
	}

	public float getTime()
	{
		return worldModel.getTime();
	}
}
