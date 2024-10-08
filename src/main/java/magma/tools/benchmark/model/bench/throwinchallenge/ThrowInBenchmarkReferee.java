package magma.tools.benchmark.model.bench.throwinchallenge;

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

public class ThrowInBenchmarkReferee extends BenchmarkRefereeBase
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

	/** speed below which the ball is considered in rest (in m/cycle) */
	private static final double BALL_STOPPED_SPEED = 0.001;

	/** minimal height from where the ball needs to be thrown to be considered a throw */
	private static final double MIN_THROW_HEIGHT = 0.2;
	
	/** threshold for ball velocity where a throw is considered a kick */
	private static final double KICK_VELOCITY_THRESHOLD = 0.4;
	
	/** minimal distance the ball needs to cover before counting as a throw*/
	private static final double MIN_THROW_DISTANCE = 0.25;

	private final String roboVizServer;

	/** the distance of the ball from the target when stopped */
	private double distanceError;

	/** position of ball in last cycle */
	private Vector2D oldBallPos;

	/** position of ball in last cycle */
	private Vector3D oldBallPos3D;

	/** flag for checking if the ball was above MIN_TROW_HEIGHT */
	private boolean wasThrown;

	public ThrowInBenchmarkReferee(IMonitorWorldModel mWorldModel, IServerCommander serverCommander, String serverPid,
			SinglePlayerLauncher launcher, float dropHeight, RunInformation runInfo, String roboVizServer)
	{
		// ignoring passed runtime since the check should anyhow not fire
		super(mWorldModel, serverCommander, serverPid, launcher, 20, runInfo, false);
		this.roboVizServer = roboVizServer;
		distanceError = 0;
		oldBallPos = new Vector2D(runInfo.getBallX(), runInfo.getBallY());
		oldBallPos3D = new Vector3D(runInfo.getBallX(), runInfo.getBallY(), 0);
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

		wasThrown = false;

		serverCommander.setPlaymode(PlayMode.PLAY_ON);
		serverCommander.beamBall((float) runInfo.getBallX(), (float) runInfo.getBallY());

		RoboVizDraw roboVizDraw =
				new RoboVizDraw(new RoboVizParameters(true, roboVizServer, RoboVizDraw.DEFAULT_PORT, 1));
		roboVizDraw.drawCircle("kickChallenge.penaltyCircle", new Vector3D(runInfo.getBallX(), runInfo.getBallY(), 0),
				(float) MAX_BALL_DISTANCE, 5, new Color(0xFF1e7711));
		roboVizDraw.drawCircle("kickChallenge.targetPosition", Vector3D.ZERO, 0.1f, 3, new Color(0xFFd2d2d2));
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
			double velocity = posBall.distance(oldBallPos3D);
			
			if (posBall.getZ() >= MIN_THROW_HEIGHT && velocity < KICK_VELOCITY_THRESHOLD) {
				wasThrown = true;
			}else if (wasThrown && posBall.getZ() < MIN_THROW_HEIGHT && playerNow.distance(ballNow) < MIN_THROW_DISTANCE) {
				wasThrown = false;
			}
			
			// stop if player runs too far
			if (playerNow.distance(ballInitial) > MAX_BALL_DISTANCE) {
				return true;
			}	
		
			// stop if ball has left radius and has stopped			
			if (ballNow.distance(ballInitial) > MAX_BALL_DISTANCE && ballNow.distance(oldBallPos) < BALL_STOPPED_SPEED) {
				return true;
			}
			// stop if the ball did not leave the circle for too long
			if (ballNow.distance(ballInitial) < MAX_BALL_DISTANCE && time - startTime > TIME_BALL_HAS_TO_LEAVE_CIRCLE) {
				return true;
			}
			
			// stop if playmode changes (e.g. because someone scored an own goal)
			if (worldModel.getPlayMode() != PlayMode.PLAY_ON) {
				return true;
			}
		}

		oldBallPos = ballNow;
		oldBallPos3D = posBall;
		return false;
	}

	@Override
	protected void onStopBenchmark()
	{
		state = RefereeState.STOPPED;

		Vector3D posPlayer = getAgent().getPosition();
		Vector2D playerNow = new Vector2D(posPlayer.getX(), posPlayer.getY());
		Vector2D ballInitial = new Vector2D(runInfo.getBallX(), runInfo.getBallY());

		distanceError = oldBallPos.distance(ballInitial);

		// we give a penalty if player left circle around ball
		if (playerNow.distance(ballInitial) > MAX_BALL_DISTANCE) {
			distanceError = 0;
			hasPenalty = true;
		}

		// we give a penalty if player did not threw the ball
		if (!wasThrown) {
			distanceError = 0;
			hasPenalty = true;
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
