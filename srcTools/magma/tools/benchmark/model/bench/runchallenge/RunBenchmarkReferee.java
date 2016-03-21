package magma.tools.benchmark.model.bench.runchallenge;

import magma.common.spark.PlayMode;
import magma.monitor.command.IServerCommander;
import magma.monitor.worldmodel.IMonitorWorldModel;
import magma.tools.benchmark.model.bench.BenchmarkRefereeBase;
import magma.tools.benchmark.model.bench.RunInformation;
import magma.tools.benchmark.model.bench.SinglePlayerLauncher;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class RunBenchmarkReferee extends BenchmarkRefereeBase
{
	/** the time we wait the player to cross the line before we start counting */
	private static final double TIME_UNTIL_BENCH_STARTS = 4.0;

	private float averageSpeed;

	private float startX;

	private int startCycleCount;

	public RunBenchmarkReferee(IMonitorWorldModel mWorldModel,
			IServerCommander serverCommander, String serverPid,
			SinglePlayerLauncher launcher, float runTime, float dropHeight,
			RunInformation runInfo)
	{
		super(mWorldModel, serverCommander, serverPid, launcher, runTime,
				dropHeight, runInfo);
		averageSpeed = 0;
		startX = (float) runInfo.getBeamX();
		startCycleCount = 0;
	}

	@Override
	protected boolean onStartBenchmark()
	{
		startCycleCount++;
		state = RefereeState.CONNECTED;
		if (startCycleCount >= 200) {
			serverCommander.setPlaymode(PlayMode.PLAY_ON);
			serverCommander.beamBall((float) runInfo.getBallX(),
					(float) runInfo.getBallY());
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean onDuringBenchmark()
	{
		float FARTHEST_DISTANCE_ALLOWED = 14.5f;
		float time = worldModel.getTime();
		float currentTime = time - startTime;
		Vector3D position = getAgent().getPosition();
		if (currentTime > runTime
				|| position.getX() >= FARTHEST_DISTANCE_ALLOWED) {
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

		return hasFallen();
	}

	@Override
	protected void onStopBenchmark()
	{
		Vector3D position = getAgent().getPosition();
		double endX = position.getX();
		double realStartX = startX + 0.5;
		if (hasFallen()) {
			endX -= 2;
			hasFallen = true;
			hasPenalty = true;
		}
		if (endX < realStartX) {
			endX = realStartX;
		}
		averageSpeed = (float) (endX - realStartX) / runTime;
		state = RefereeState.STOPPED;
	}

	public float getAverageSpeed()
	{
		return averageSpeed;
	}
}
