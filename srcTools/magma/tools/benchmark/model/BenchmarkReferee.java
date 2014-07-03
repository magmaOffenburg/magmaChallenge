package magma.tools.benchmark.model;

import magma.monitor.command.IServerCommander;
import magma.monitor.referee.impl.RefereeBase;
import magma.monitor.referee.impl.SinglePlayerLauncher;
import magma.monitor.worldmodel.IMonitorWorldModel;
import magma.monitor.worldmodel.ISoccerAgent;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class BenchmarkReferee extends RefereeBase
{
	/** the time we wait the player to cross the line before we start counting */
	private static final double TIME_UNTIL_BENCH_STARTS = 4.0;

	/** flag to prevent printing the score multiple times */
	private boolean stopBenchmarkCalled;

	private boolean launching;

	private SinglePlayerLauncher launcher;

	private int decisionCount;

	private int cycleCount;

	private float averageSpeed;

	private boolean hasFallen;

	float startTime;

	private float startX;

	/** time for a run (in seconds) */
	private float runTime;

	private String statusText;

	// private float dropHeight;

	private int startCycleCount;

	public BenchmarkReferee(IMonitorWorldModel mWorldModel,
			IServerCommander serverCommander, String serverPid,
			SinglePlayerLauncher launcher, float runTime, float dropHeight)
	{
		super(mWorldModel, serverCommander, serverPid);
		// this.dropHeight = dropHeight;

		stopBenchmarkCalled = false;
		timer = null;
		this.launcher = launcher;
		launching = (launcher == null) ? false : true;
		decisionCount = 0;
		cycleCount = 0;
		averageSpeed = 0;
		startTime = -1;
		startX = -13.5f;
		this.runTime = runTime;
		hasFallen = false;
		statusText = "";
		startCycleCount = 0;
	}

	@Override
	public boolean decide()
	{
		decisionCount++;
		boolean stop = false;
		if (cycleCount < 1) {
			if (launching) {
				if (decisionCount > 300) {
					// timeout, launching did not work
					state = RefereeState.FAILED;
					statusText = "Timeout when launching player\n"
							+ launcher.getStatusText();
					return true;
				}
				launching = launcher.launchPlayer(getNumberOfPlayers());
			} else {
				// game is not running, so lets start it
				boolean finishedStarting = onStartBenchmark();
				if (finishedStarting) {
					cycleCount++;
				}
			}
		} else {
			stop = onDuringBenchmark();
			cycleCount++;
		}
		// setupTimer(7000);

		// System.out.println("new game time: " + gameTime);
		if (stop && !stopBenchmarkCalled) {
			onStopBenchmark();
			stopBenchmarkCalled = true;
			stopTimer();
			launcher.stopPlayer();
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	protected boolean onStartBenchmark()
	{
		startCycleCount++;
		// int playerID = getAgent().getPlayerID();
		// serverCommander.setPlaymode(PlayMode.PLAY_ON);
		// serverCommander.beamBall(14.75f, 0f);
		// serverCommander.moveRotatePlayer(Team.LEFT, playerID, startX, 0f,
		// dropHeight, -90);
		state = RefereeState.CONNECTED;
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
	private void onStopBenchmark()
	{
		Vector3D position = getAgent().getPosition();
		double endX = position.getX();
		double realStartX = startX + 0.5;
		if (hasFallen()) {
			endX -= 2;
			hasFallen = true;
		}
		if (endX < realStartX) {
			endX = realStartX;
		}
		averageSpeed = (float) (endX - realStartX) / runTime;
		state = RefereeState.STOPPED;
	}

	protected boolean hasFallen()
	{
		double zOfUpVector = getAgent().getTorsoOrientation().getMatrix()[2][2];
		if (zOfUpVector < 0.6) {
			return true;
		}

		Vector3D position = getAgent().getPosition();
		if (position.getZ() < 0.25) {
			return true;
		}
		return false;
	}

	/**
	 * @return
	 */
	private ISoccerAgent getAgent()
	{
		return worldModel.getSoccerAgents().get(0);
	}

	/**
	 * @return the averageSpeed
	 */
	public float getAverageSpeed()
	{
		return averageSpeed;
	}

	/**
	 * @return the hasFallen
	 */
	public boolean isHasFallen()
	{
		return hasFallen;
	}

	/**
	 * @return
	 */
	public String getStatusText()
	{
		return statusText;
	}

	/**
	 * @return
	 */
	public float getRunTime()
	{
		return runTime;
	}
}
