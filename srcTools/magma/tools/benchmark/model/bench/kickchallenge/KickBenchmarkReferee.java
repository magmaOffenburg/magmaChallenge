package magma.tools.benchmark.model.bench.kickchallenge;

import magma.monitor.command.IServerCommander;
import magma.monitor.referee.impl.SinglePlayerLauncher;
import magma.monitor.worldmodel.IMonitorWorldModel;
import magma.tools.benchmark.model.bench.BenchmarkRefereeBase;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class KickBenchmarkReferee extends BenchmarkRefereeBase
{
	/** the time we wait the player to cross the line before we start counting */
	private static final double TIME_UNTIL_BENCH_STARTS = 3.0;

	private double distanceError;

	private float startX;

	private int startCycleCount;

	/** the current run/phase of the benchmark indicating the kick distance */
	private int currentRun;

	/** random seed to use to start random number generator */
	private long randomSeed;

	public KickBenchmarkReferee(IMonitorWorldModel mWorldModel,
			IServerCommander serverCommander, String serverPid,
			SinglePlayerLauncher launcher, float runTime, float dropHeight,
			long randomSeed, int currentRun)
	{
		super(mWorldModel, serverCommander, serverPid, launcher, runTime,
				dropHeight);
		this.randomSeed = randomSeed;
		this.currentRun = currentRun;
		distanceError = 0;
		startX = 0;
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

		if (startCycleCount >= 200) {
			String msg = "(playMode PlayOn)(ball (pos 14.75 0 0.042) (vel 0 0 0))";
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
		float FARTHEST_DISTANCE_ALLOWED = 14.5f;
		float time = worldModel.getTime();
		float currentTime = time - startTime;
		Vector3D position = getAgent().getPosition();
		if (currentTime > runTime || position.getX() >= FARTHEST_DISTANCE_ALLOWED) {
			// finished this run
			runTime = currentTime;
			return true;
		}

		if (state == RefereeState.CONNECTED
				&& Math.abs(position.getX() - startX) < 0.1
				&& Math.abs(position.getY()) < 0.1) {
			// serverCommander.dropBall();
			state = RefereeState.BEAMED;
		}

		if (state == RefereeState.BEAMED && startTime < 0) {
			if (position.getX() > startX + 0.5) {
				// player has crossed the start line
				startTime = time;
				state = RefereeState.STARTED;
			} else if (time > TIME_UNTIL_BENCH_STARTS) {
				// 2 seconds to start are over
				startTime = time;
				state = RefereeState.STARTED;
			}
		}

		if (hasFallen()) {
			// agent is on ground
			return true;
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
