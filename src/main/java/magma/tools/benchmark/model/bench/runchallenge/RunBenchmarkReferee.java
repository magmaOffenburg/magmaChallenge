package magma.tools.benchmark.model.bench.runchallenge;

import magma.common.spark.PlayMode;
import magma.monitor.command.IServerCommander;
import magma.monitor.worldmodel.IMonitorWorldModel;
import magma.tools.benchmark.model.bench.BenchmarkRefereeBase;
import magma.tools.benchmark.model.bench.RunInformation;
import magma.tools.benchmark.model.bench.SinglePlayerLauncher;
import magma.tools.benchmark.model.proxy.BenchmarkAgentProxyServer;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class RunBenchmarkReferee extends BenchmarkRefereeBase
{
	/** the time we wait the player to cross the line before we start counting */
	private static double TIME_UNTIL_BENCH_STARTS = 4.0;

	/**
	 * the distance the player has to move until the challenge is started
	 * automatically
	 */
	private static double START_LINE_X_OFFSET = 0.5;

	private final boolean isGazebo;

	private final BenchmarkAgentProxyServer proxy;

	private float averageSpeed;

	private final float startX;

	private int startCycleCount;

	public RunBenchmarkReferee(IMonitorWorldModel monitorWorldModel, IServerCommander serverCommander, String serverPid,
			SinglePlayerLauncher launcher, float runTime, float dropHeight, RunInformation runInfo, boolean isGazebo,
			BenchmarkAgentProxyServer proxy)
	{
		super(monitorWorldModel, serverCommander, serverPid, launcher, runTime, runInfo, isGazebo);
		this.isGazebo = isGazebo;
		this.proxy = proxy;
		averageSpeed = 0;
		startX = (float) runInfo.getBeamX();
		startCycleCount = 0;
		if (isGazebo) {
			// in Gazebo agents need more time to get ready since they can't move
			// before PLAY_ON
			TIME_UNTIL_BENCH_STARTS = 10.0;
			// agents in Gazebo walk more slowly
			START_LINE_X_OFFSET = 0.2;
		}
	}

	@Override
	protected boolean onStartBenchmark()
	{
		startCycleCount++;
		state = RefereeState.CONNECTED;
		if (startCycleCount >= 200) {
			serverCommander.setPlaymode(PlayMode.PLAY_ON);
			serverCommander.beamBall((float) runInfo.getBallX(), (float) runInfo.getBallY());
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean onDuringBenchmark()
	{
		if (startTime < 0) {
			startTime = getTime();
		}

		Vector3D position = getAgentPosition();
		float currentTime = getTime() - startTime;

		switch (state) {
		case CONNECTED:
			if (Math.abs(position.getX() - startX) < 0.1 && Math.abs(position.getY()) < 0.1) {
				state = RefereeState.BEAMED;
			}
			break;
		case BEAMED:
			// player has crossed the start line or 2 seconds to start are over
			if (position.getX() > startX + START_LINE_X_OFFSET || currentTime > TIME_UNTIL_BENCH_STARTS) {
				startTime = getTime();
				state = RefereeState.STARTED;
				System.out.println("Starting run challenge");
			}
			break;
		case STARTED:
			float FARTHEST_DISTANCE_ALLOWED = 14.5f;
			if (currentTime > runTime || position.getX() >= FARTHEST_DISTANCE_ALLOWED) {
				// finished this run
				runTime = currentTime;
				return true;
			}
			break;
		}

		return hasFallen();
	}

	private float getTime()
	{
		if (isGazebo) {
			return proxy.getTime();
		}
		return worldModel.getTime();
	}

	@Override
	protected void onStopBenchmark()
	{
		Vector3D position = getAgentPosition();
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

	@Override
	protected Vector3D getAgentPosition()
	{
		if (isGazebo) {
			return proxy.getGroundTruthPosition();
		}
		return super.getAgentPosition();
	}

	@Override
	protected Rotation getAgentRotation()
	{
		if (isGazebo) {
			return Rotation.IDENTITY;
		}
		return super.getAgentRotation();
	}

	@Override
	protected boolean hasFallen()
	{
		if (isGazebo) {
			return getAgentPosition().getZ() < 0.25;
		}
		return super.hasFallen();
	}
}
