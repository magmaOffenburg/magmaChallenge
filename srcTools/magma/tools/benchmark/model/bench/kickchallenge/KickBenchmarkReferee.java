package magma.tools.benchmark.model.bench.kickchallenge;

import magma.monitor.command.IServerCommander;
import magma.monitor.referee.impl.SinglePlayerLauncher;
import magma.monitor.worldmodel.IMonitorWorldModel;
import magma.tools.benchmark.model.bench.BenchmarkRefereeBase;
import magma.tools.benchmark.model.bench.RunInformation;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class KickBenchmarkReferee extends BenchmarkRefereeBase
{
	/** the time we wait the player to cross the line before we start counting */
	private static final double TIME_UNTIL_BENCH_STARTS = 3.0;

	/** distance to ball below which time starts to count */
	private static final double START_LINE_DISTANCE = 0.4;

	/** distance to ball above which run ends */
	private static final double MAX_BALL_DISTANCE = 2.0;

	private double distanceError;

	private int startCycleCount;

	private RunInformation runInfo;

	public KickBenchmarkReferee(IMonitorWorldModel mWorldModel,
			IServerCommander serverCommander, String serverPid,
			SinglePlayerLauncher launcher, float runTime, float dropHeight,
			RunInformation runInfo)
	{
		super(mWorldModel, serverCommander, serverPid, launcher, runTime,
				dropHeight);
		this.runInfo = runInfo;
		distanceError = 0;
		startCycleCount = 0;
	}

	/**
	 * 
	 */
	@Override
	protected boolean onStartBenchmark()
	{
		startCycleCount++;
		state = RefereeState.CONNECTED;

		// determine ball position

		if (startCycleCount >= 50) {
			String msg = "(playMode PlayOn)(ball (pos " + runInfo.getBallX() + " "
					+ runInfo.getBallY() + " 0.042) (vel 0 0 0))";
			serverCommander.sendMessage(msg);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 */
	@Override
	protected boolean onDuringBenchmark()
	{
		float time = worldModel.getTime();
		float currentTime = time - startTime;
		Vector3D posPlayer = getAgent().getPosition();
		Vector3D posBall = getBall().getPosition();
		Vector2D playerInitial = new Vector2D(runInfo.getBeamX(),
				runInfo.getBeamY());
		Vector2D ballInitial = new Vector2D(runInfo.getBallX(),
				runInfo.getBallY());
		Vector2D playerNow = new Vector2D(posPlayer.getX(), posPlayer.getY());
		Vector2D ballNow = new Vector2D(posBall.getX(), posBall.getY());

		if (currentTime > runTime) {
			// finished this run
			runTime = currentTime;
			return true;
		}

		if (state == RefereeState.CONNECTED
				&& playerNow.distance(playerInitial) < 0.1) {
			state = RefereeState.BEAMED;
		}

		if (state == RefereeState.BEAMED && startTime < 0) {
			if (playerNow.distance(ballNow) < START_LINE_DISTANCE) {
				// player has crossed the start line
				startTime = time;
				state = RefereeState.STARTED;
			} else if (time > TIME_UNTIL_BENCH_STARTS) {
				// 2 seconds to start are over
				startTime = time;
				state = RefereeState.STARTED;
			}
		}

		if (state == RefereeState.STARTED) {
			if (playerNow.distance(ballInitial) > MAX_BALL_DISTANCE) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 
	 */
	@Override
	protected void onStopBenchmark()
	{
		Vector3D position = getBall().getPosition();
		distanceError = position.getNorm();
		state = RefereeState.STOPPED;
	}

	/**
	 * @return the score as distance to destination position
	 */
	public double getDistanceError()
	{
		return distanceError;
	}
}
