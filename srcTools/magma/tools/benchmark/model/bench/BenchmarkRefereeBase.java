package magma.tools.benchmark.model.bench;

import magma.monitor.command.IServerCommander;
import magma.monitor.referee.impl.RefereeBase;
import magma.monitor.referee.impl.SinglePlayerLauncher;
import magma.monitor.worldmodel.IMonitorWorldModel;
import magma.monitor.worldmodel.ISoccerAgent;
import magma.monitor.worldmodel.ISoccerBall;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public abstract class BenchmarkRefereeBase extends RefereeBase
{
	/** flag to prevent printing the score multiple times */
	private boolean stopBenchmarkCalled;

	private boolean launching;

	private SinglePlayerLauncher launcher;

	private int decisionCount;

	private int cycleCount;

	protected float startTime;

	/** time for a run (in seconds) */
	protected float runTime;

	private String statusText;

	protected boolean hasFallen;

	public BenchmarkRefereeBase(IMonitorWorldModel mWorldModel,
			IServerCommander serverCommander, String serverPid,
			SinglePlayerLauncher launcher, float runTime, float dropHeight)
	{
		super(mWorldModel, serverCommander, serverPid);

		stopBenchmarkCalled = false;
		timer = null;
		this.launcher = launcher;
		launching = (launcher == null) ? false : true;
		decisionCount = 0;
		cycleCount = 0;
		startTime = -1;
		this.runTime = runTime;
		hasFallen = false;
		statusText = "";
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
	 * Called once the benchmark is setup
	 */
	protected abstract boolean onStartBenchmark();

	/**
	 * Called each cycle during the benchmark
	 */
	protected abstract boolean onDuringBenchmark();

	/**
	 * 
	 */
	protected abstract void onStopBenchmark();

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
	 * @return the first player of the left team
	 */
	protected ISoccerAgent getAgent()
	{
		return worldModel.getSoccerAgents().get(0);
	}

	/**
	 * @return the ball
	 */
	protected ISoccerBall getBall()
	{
		return worldModel.getBall();
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
