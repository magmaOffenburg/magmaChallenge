package magma.tools.benchmark.model.bench.kickchallenge;

import magma.common.spark.PlayMode;
import magma.monitor.command.IServerCommander;
import magma.monitor.referee.impl.SinglePlayerLauncher;
import magma.monitor.worldmodel.IMonitorWorldModel;
import magma.tools.benchmark.model.bench.BenchmarkRefereeBase;
import magma.tools.benchmark.model.bench.RunInformation;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class KickBenchmarkReferee extends BenchmarkRefereeBase
{
	/**
	 * the time we wait the player to cross the line before we start counting (in
	 * s)
	 */
	private static final double TIME_UNTIL_BENCH_STARTS = 3.0;

	/** distance to ball below which time starts to count (in m) */
	private static final double START_LINE_DISTANCE = 0.4;

	/** distance to ball above which run ends (in m) */
	private static final double MAX_BALL_DISTANCE = 2.0;

	/** penalty assigned if player leaves circle */
	private static final double PENALTY_LEAVING_CIRCLE = 5.0;

	/** speed below which the ball is considered in rest (in m/cycle) */
	private static final double BALL_STOPPED_SPEED = 0.001;

	/** the distance of the ball from the target when stopped */
	private double distanceError;

	/** position of ball in last cycle */
	private Vector2D oldBallPos;

	public KickBenchmarkReferee(IMonitorWorldModel mWorldModel,
			IServerCommander serverCommander, String serverPid,
			SinglePlayerLauncher launcher, float runTime, float dropHeight,
			RunInformation runInfo)
	{
		super(mWorldModel, serverCommander, serverPid, launcher, runTime,
				dropHeight, runInfo);
		distanceError = 0;
		oldBallPos = new Vector2D(runInfo.getBallX(), runInfo.getBallY());
	}

	/**
	 * Beams the ball to the next kick start position
	 */
	@Override
	protected boolean onDuringLaunching()
	{
		// we beam the ball early
		String msg = "(ball (pos " + runInfo.getBallX() + " "
				+ runInfo.getBallY() + " 0.042) (vel 0 0 0))";
		serverCommander.sendMessage(msg);
		return super.onDuringLaunching();
	}

	/**
	 * 
	 */
	@Override
	protected boolean onStartBenchmark()
	{
		state = RefereeState.CONNECTED;

		String msg = "(playMode PlayOn)(ball (pos " + runInfo.getBallX() + " "
				+ runInfo.getBallY() + " 0.042) (vel 0 0 0))";
		serverCommander.sendMessage(msg);
		return true;
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
			// stop if player runs too far
			if (playerNow.distance(ballInitial) > MAX_BALL_DISTANCE) {
				return true;
			}
			// stop if ball has left radius and has stopped
			if (ballNow.distance(ballInitial) > MAX_BALL_DISTANCE) {
				if (ballNow.distance(oldBallPos) < BALL_STOPPED_SPEED) {
					return true;
				}
			}
			// stop if playmode changes (e.g. because someone scored an own goal)
			if (worldModel.getPlayMode() != PlayMode.PLAY_ON) {
				return true;
			}
		}

		oldBallPos = ballNow;
		return false;
	}

	/**
	 * 
	 */
	@Override
	protected void onStopBenchmark()
	{
		// evaluation function
		distanceError = oldBallPos.getNorm();
		state = RefereeState.STOPPED;

		// we give a penalty if player left circle around ball
		Vector3D posPlayer = getAgent().getPosition();
		Vector2D playerNow = new Vector2D(posPlayer.getX(), posPlayer.getY());
		Vector2D ballInitial = new Vector2D(runInfo.getBallX(),
				runInfo.getBallY());
		if (playerNow.distance(ballInitial) > MAX_BALL_DISTANCE) {
			distanceError += PENALTY_LEAVING_CIRCLE;
		}
	}

	/**
	 * @return the score as distance to destination position
	 */
	public double getDistanceError()
	{
		return distanceError;
	}
}
