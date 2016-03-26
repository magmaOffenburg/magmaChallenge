package magma.tools.benchmark.model.bench.keepawaychallenge;

import java.awt.Color;
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
	private static final float AREA_CENTER_X = 0;

	private static final float AREA_CENTER_Y = 0;

	private static final float AREA_WIDTH = 20;

	private static final float AREA_LENGTH = 20;

	private static final float WIDTH_REDUCTION_RATE = 4;

	private static final float LENGTH_REDUCTION_RATE = 4;

	private static final String ROBOVIZ_GROUP = "keepAwayChallenge.area";

	private SinglePlayerLauncher opponentLauncher;

	private boolean opponentLaunched = false;

	private int delay = 10;

	public KeepAwayBenchmarkReferee(IMonitorWorldModel mWorldModel,
			IServerCommander serverCommander, String serverPid,
			SinglePlayerLauncher launcher, float runTime, float dropHeight,
			RunInformation runInfo, String roboVizServer, String serverIP,
			int agentPort)
	{
		super(mWorldModel, serverCommander, serverPid, launcher, runTime,
				dropHeight, runInfo);

		RoboVizDraw.automaticBufferSwap = false;
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

	protected void drawArea()
	{
		float time = worldModel.getTime() / 60f;
		float widthReduction = WIDTH_REDUCTION_RATE / 2.0f * time;
		float lengthReduction = LENGTH_REDUCTION_RATE / 2.0f * time;

		float areaMinX = AREA_CENTER_X - AREA_LENGTH / 2.0f + lengthReduction;
		float areaMaxX = AREA_CENTER_X + AREA_LENGTH / 2.0f - lengthReduction;
		float areaMinY = AREA_CENTER_Y - AREA_WIDTH / 2.0f + widthReduction;
		float areaMaxY = AREA_CENTER_Y + AREA_WIDTH / 2.0f - widthReduction;

		drawRedLine(areaMinX, areaMinY, areaMinX, areaMaxY);
		drawRedLine(areaMinX, areaMaxY, areaMaxX, areaMaxY);
		drawRedLine(areaMaxX, areaMaxY, areaMaxX, areaMinY);
		drawRedLine(areaMaxX, areaMinY, areaMinX, areaMinY);
		RoboVizDraw.swapBuffer(ROBOVIZ_GROUP);
	}

	protected void drawRedLine(float x1, float y1, float x2, float y2)
	{
		RoboVizDraw.drawLine(ROBOVIZ_GROUP, x1, y1, 0, x2, y2, 0, 2, Color.RED);
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
