package magma.tools.benchmark.model.bench.rollingballchallenge;

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

public class RollingBallBenchmarkReferee extends BenchmarkRefereeBase
{
	/**
	 * time we wait the player to cross the line before we start counting (in s)
	 */
	private static final double TIME_UNTIL_BENCH_STARTS = 3.0;

	/** if after this time the ball is still in the 2m circle we stop (in s) */
	private static final double TIME_BALL_HAS_TO_LEAVE_CIRCLE = 7.0;

	/** distance to ball below which time starts to count (in m) */
	private static final double START_LINE_DISTANCE = 0.4;

	/** distance to ball above which run ends (in m) */
	private static final double MAX_BALL_DISTANCE = 2.0;

	/** penalty assigned if player leaves circle */
	private static final double PENALTY_LEAVING_CIRCLE = 5.0;

	/** speed below which the ball is considered in rest (in m/cycle) */
	private static final double BALL_STOPPED_SPEED = 0.001;

	private final String roboVizServer;

	/** the distance of the ball from the kick Position when stopped */
	private double distance;

	/** the deviation from straight kick */
	private double deltaY;

	/** position of ball in last cycle */
	private Vector2D oldBallPos;

	private boolean ballRolling;

	public RollingBallBenchmarkReferee(IMonitorWorldModel mWorldModel, IServerCommander serverCommander, String serverPid,
			SinglePlayerLauncher launcher, float dropHeight, RunInformation runInfo, String roboVizServer)
	{
		// ignoring passed runtime since the check should anyhow not fire
		super(mWorldModel, serverCommander, serverPid, launcher, 20, runInfo, false);
		this.roboVizServer = roboVizServer;
		distance = 0;
		deltaY = 0;
		oldBallPos = new Vector2D(runInfo.getBallX(), runInfo.getBallY());
	}

	/**
	 * Beams the ball to the next kick start position
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
		ballRolling = false;

		serverCommander.setPlaymode(PlayMode.PLAY_ON);
		serverCommander.beamBall((float) runInfo.getBallX(), (float) runInfo.getBallY());
//		serverCommander.moveRotatePlayer(Team.LEFT, worldModel.getSoccerAgents().get(0).getPlayerID(), 
//				(float) runInfo.getBeamX(), (float) runInfo.getBeamY(), 0.4f, (float) 0 - 90);

		RoboVizDraw roboVizDraw =
				new RoboVizDraw(new RoboVizParameters(true, roboVizServer, RoboVizDraw.DEFAULT_PORT, 1));
		roboVizDraw.drawCircle("rollingBallChallenge.penaltyCircle", new Vector3D(runInfo.getBeamX(), runInfo.getBeamY(), 0),
				(float) MAX_BALL_DISTANCE, 5, new Color(0xFF1e7711));
		return true;
	}

	@Override
	protected boolean onDuringBenchmark()
	{
		float time = worldModel.getTime();
		float currentTime = time - startTime;
		Vector3D posPlayer = getAgent().getPosition();
		Vector3D posBall = getBall().getPosition();
		Vector2D playerInitial = new Vector2D(runInfo.getBeamX(), runInfo.getBeamY());
		Vector2D ballInitial = new Vector2D(runInfo.getBallX(), runInfo.getBallY());
		Vector2D playerNow = new Vector2D(posPlayer.getX(), posPlayer.getY());
		Vector2D ballNow = new Vector2D(posBall.getX(), posBall.getY());

		if (currentTime > runTime) {
			// finished this run
			runTime = currentTime;
			return true;
		}

		if (state == RefereeState.CONNECTED && playerNow.distance(playerInitial) < 0.1) {
			state = RefereeState.BEAMED;
		}

		if (state == RefereeState.BEAMED && startTime < 0) {
			startTime = time;
			state = RefereeState.STARTED;
		}

		if (state == RefereeState.STARTED) {
			// wait few seconds to allow the player to find the ball
			if (currentTime < 5) {
				oldBallPos = ballNow;
				return false;
			}
			
			if (!ballRolling) {
				serverCommander.beamBall((float) runInfo.getBallX(), (float) runInfo.getBallY(), 0.0f,
						(float) runInfo.getBallVelX(), (float) runInfo.getBallVelY(), (float) runInfo.getBallVelZ());
				ballRolling = true;
			}
			
			// stop if player runs too far
			if (playerNow.distance(ballInitial) > MAX_BALL_DISTANCE) {
				return true;
			}
			// stop if ball has left radius and has stopped
			if (ballNow.distance(ballInitial) > MAX_BALL_DISTANCE) {
				if (ballNow.distance(oldBallPos) < BALL_STOPPED_SPEED) {
					return true;
				}
			} else {
				// stop if the ball did not leave the circle for too long
				if (time - startTime > TIME_BALL_HAS_TO_LEAVE_CIRCLE) {
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

	@Override
	protected void onStopBenchmark()
	{
		// evaluation function
		Vector2D start = new Vector2D(runInfo.getBeamX(), runInfo.getBeamY());
		
		// TODO: set distance to 0 if ball not kicked
		
		distance = start.distance(oldBallPos);
		deltaY = Math.abs(oldBallPos.getY());
		state = RefereeState.STOPPED;

		// we give a penalty if player left circle around ball
		Vector3D posPlayer = getAgent().getPosition();
		Vector2D playerNow = new Vector2D(posPlayer.getX(), posPlayer.getY());
		Vector2D ballInitial = new Vector2D(runInfo.getBallX(), runInfo.getBallY());
		if (playerNow.distance(ballInitial) > MAX_BALL_DISTANCE) {
			distance += PENALTY_LEAVING_CIRCLE;
			hasPenalty = true;
		}
	}

	/**
	 * @return the distance of the ball from kick position
	 */
	public double getDistance()
	{
		return distance;
	}

	/**
	 * @return the deviation from straight kick
	 */
	public double getDeltaY()
	{
		return deltaY;
	}
}
