package magma.tools.benchmark.model.bench.goaliechallenge;

import java.awt.Color;
import magma.common.spark.PlayMode;
import magma.monitor.command.IServerCommander;
import magma.monitor.worldmodel.IMonitorWorldModel;
import magma.tools.benchmark.model.bench.BenchmarkRefereeBase;
import magma.tools.benchmark.model.bench.RunInformation;
import magma.tools.benchmark.model.bench.SinglePlayerLauncher;
import magma.util.roboviz.RoboVizDraw;
import magma.util.roboviz.RoboVizParameters;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class GoalieBenchmarkReferee extends BenchmarkRefereeBase
{
	/**
	 * time we wait the player to get into penalty area before we start counting
	 * (in s)
	 */
	private static final double TIME_UNTIL_BENCH_STARTS = 3.0;

	/** penalty assigned if player leaves penalty area */
	private static final double PENALTY_LEAVING_AREA = 2.0;

	/** speed below which the ball is considered in rest (in m/cycle) */
	private static final double BALL_STOPPED_SPEED = 0.001;

	private final String roboVizServer;

	/** position of ball in last cycle */
	private Vector2D oldBallPos;

	/** score of this attempt (goal/not goal) */
	private boolean goal = false;

	public GoalieBenchmarkReferee(IMonitorWorldModel mWorldModel, IServerCommander serverCommander, String serverPid,
			SinglePlayerLauncher launcher, float dropHeight, RunInformation runInfo, String roboVizServer)
	{
		// ignoring passed runtime since the check should anyhow not fire
		super(mWorldModel, serverCommander, serverPid, launcher, 15, runInfo, false);
		this.roboVizServer = roboVizServer;
		oldBallPos = new Vector2D(runInfo.getBallX(), runInfo.getBallY());
	}

	/**
	 * Beams the ball to the next kick position
	 */
	@Override
	protected boolean onDuringLaunching()
	{
		// we beam the ball early
		serverCommander.beamBall((float) runInfo.getBallX(), (float) runInfo.getBallY());
		return super.onDuringLaunching();
	}

	@Override
	protected boolean onStartBenchmark()
	{
		state = RefereeState.CONNECTED;

		serverCommander.setPlaymode(PlayMode.PLAY_ON);
		serverCommander.beamBall((float) runInfo.getBallX(), (float) runInfo.getBallY());

		// RoboVizDraw roboVizDraw =
		//		new RoboVizDraw(new RoboVizParameters(true, roboVizServer,
		// RoboVizDraw.DEFAULT_PORT, 1));
		// roboVizDraw.drawCircle("goalieChallenge.penaltyCircle", new
		// Vector3D(runInfo.getBallX(), runInfo.getBallY(), 0), 		(float)
		// MAX_BALL_DISTANCE, 5, new Color(0xFF1e7711));
		// roboVizDraw.drawCircle("goalieChallenge.targetPosition", Vector3D.ZERO,
		// 0.1f, 3, new Color(0xFFd2d2d2));
		return true;
	}

	boolean ballKicked = false;

	@Override
	protected boolean onDuringBenchmark()
	{
		float time = worldModel.getTime();
		float currentTime = time - startTime;
		Vector3D posPlayer = getAgent().getPosition();
		Vector3D posBall = getBall().getPosition();
		Vector2D ballInitial = new Vector2D(runInfo.getBallX(), runInfo.getBallY());
		Vector2D playerNow = new Vector2D(posPlayer.getX(), posPlayer.getY());
		Vector2D ballNow = new Vector2D(posBall.getX(), posBall.getY());

		if (currentTime > runTime) {
			// finished this run
			runTime = currentTime;
			return true;
		}

		if (state == RefereeState.CONNECTED) {
			ballKicked = false;
			state = RefereeState.STARTED;
			startTime = time;
		}

		if (state == RefereeState.STARTED) {
			if (time > startTime + 5 && !ballKicked) {
				serverCommander.beamBall((float) runInfo.getBallX(), (float) runInfo.getBallY(), (float) 0.04,
						(float) runInfo.getBallVelX(), (float) runInfo.getBallVelY(), (float) runInfo.getBallVelZ());
				ballKicked = true;
			}
			// stop if player runs too far
			if (playerNow.getX() > -13.2 || Math.abs(playerNow.getY()) > 3) {
				goal = true; // if keeper leaves area, a goal is automatically scored
				return true;
			}
			// stop if ball has left radius and has stopped
			// if (ballNow.distance(ballInitial) > MAX_BALL_DISTANCE) {
			//		if (ballNow.distance(oldBallPos) < BALL_STOPPED_SPEED) {
			//			return true;
			//		}
			//	} else {
			//		// stop if the ball did not leave the circle for too
			// long 		if (time - startTime > TIME_BALL_HAS_TO_LEAVE_CIRCLE) {
			//			return true;
			//		}
			//	}
			// stop if playmode changes (e.g. because someone scored an own goal)
			if (worldModel.getPlayMode() != PlayMode.PLAY_ON) {
				if (worldModel.getPlayMode() == PlayMode.GOAL_RIGHT) {
					System.err.println("Goal!!!!!");
					goal = true;
				}
				return true;
			}
		}

		oldBallPos = ballNow;
		return false;
	}

	@Override
	protected void onStopBenchmark()
	{
		state = RefereeState.STOPPED;
	}

	/**
	 * @return the score
	 */
	public double getScore()
	{
		double score = 0.0;

		if (!goal)
			score += 1.0;

		return score;
	}
}
