package magma.tools.benchmark.model.bench.rollingballchallenge;

import hso.autonomy.util.geometry.Angle;
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
	 * time we wait to allow the player to look for the ball (in s)
	 */
	private static final double TIME_UNTIL_BENCH_STARTS = 5.0;

	/** speed below which the ball is considered in rest (in m/cycle) */
	private static final double BALL_STOPPED_SPEED = 0.01;

	private final RoboVizDraw roboVizDraw;

	/** the distance of the ball from the kick Position when stopped */
	private double distance;

	/** the deviation from straight kick */
	private double deltaY;

	/** position of ball in last cycle */
	private Vector2D oldBallPos;

	/** amount of speed of ball in last cycle */
	private double oldBallSpeed;

	private boolean ballRolling;

	private int ballNotMoving;

	public RollingBallBenchmarkReferee(IMonitorWorldModel mWorldModel, IServerCommander serverCommander,
			String serverPid, SinglePlayerLauncher launcher, float dropHeight, RunInformation runInfo,
			String roboVizServer)
	{
		// ignoring passed runtime since the check should anyhow not fire
		super(mWorldModel, serverCommander, serverPid, launcher, 20, runInfo, false);
		distance = 0;
		deltaY = 0;
		oldBallPos = new Vector2D(runInfo.getBallX(), runInfo.getBallY());
		oldBallSpeed = 0;
		roboVizDraw = new RoboVizDraw(new RoboVizParameters(true, roboVizServer, RoboVizDraw.DEFAULT_PORT, 1));
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

		roboVizDraw.drawMeterMarkers(false, Color.BLACK, (int) runInfo.getBeamX(), Angle.ZERO, 20);

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
		Vector2D playerNow = new Vector2D(posPlayer.getX(), posPlayer.getY());
		Vector2D ballNow = new Vector2D(posBall.getX(), posBall.getY());

		if (hasFallen()) {
			hasFallen = true;
		}

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
			if (currentTime < TIME_UNTIL_BENCH_STARTS) {
				oldBallPos = ballNow;
				return false;
			}

			if (!ballRolling) {
				serverCommander.beamBall((float) runInfo.getBallX(), (float) runInfo.getBallY(), 0.0f,
						(float) runInfo.getBallVelX(), (float) runInfo.getBallVelY(), (float) runInfo.getBallVelZ());
				ballRolling = true;
			} else {
				// stop if ball stopped moving
				double ballSpeed = ballNow.distance(oldBallPos);
				double avgSpeed = (ballSpeed + oldBallSpeed) / 2;
				oldBallSpeed = ballSpeed;
				if (avgSpeed < BALL_STOPPED_SPEED) {
					ballNotMoving++;
					if (ballNotMoving > 3) {
						return true;
					}
				} else {
					ballNotMoving = 0;
				}
			}

			// stop if player is moving too much
			if (posPlayer.getX() > playerInitial.getX() + 1.5) {
				return true;
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

		distance = 0;
		if (oldBallPos.getX() > start.getX()) {
			// only count forward movement of ball
			distance = start.distance(oldBallPos);
		}
		deltaY = Math.abs(oldBallPos.getY());
		state = RefereeState.STOPPED;
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
