package magma.tools.benchmark.model.bench;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import magma.monitor.command.IServerCommander;
import magma.monitor.referee.impl.RefereeBase;
import magma.monitor.worldmodel.IMonitorWorldModel;
import magma.monitor.worldmodel.ISoccerAgent;
import magma.monitor.worldmodel.ISoccerBall;

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

	protected boolean hasPenalty;

	/** the setup information for this run of the benchmark */
	protected RunInformation runInfo;

	public BenchmarkRefereeBase(IMonitorWorldModel mWorldModel,
			IServerCommander serverCommander, String serverPid,
			SinglePlayerLauncher launcher, float runTime, float dropHeight,
			RunInformation runInfo)
	{
		super(mWorldModel, serverCommander, serverPid);
		this.runInfo = runInfo;

		stopBenchmarkCalled = false;
		timer = null;
		this.launcher = launcher;
		launching = (launcher == null) ? false : true;
		decisionCount = 0;
		cycleCount = 0;
		startTime = -1;
		this.runTime = runTime;
		hasFallen = false;
		hasPenalty = false;
		statusText = "";
	}

	@Override
	public boolean decide()
	{
		decisionCount++;
		boolean stop = false;
		if (cycleCount < 1) {
			if (launching) {
				if (onDuringLaunching()) {
					return true;
				}
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
	 * Called during the launch phase of a player. The default implementation
	 * launches a single player and stops if a timeout is reached
	 * @return true if a timeout is reached
	 */
	protected boolean onDuringLaunching()
	{
		if (decisionCount > 300) {
			// timeout, launching did not work
			state = RefereeState.FAILED;
			statusText = "Timeout when launching player\n"
					+ launcher.getStatusText();
			return true;
		}
		launching = launcher.launchPlayer(runInfo, getNumberOfPlayers());
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

	protected abstract void onStopBenchmark();

	protected boolean hasFallen()
	{
		double zOfUpVector = getAgent().getTorsoOrientation().getMatrix()[2][2];
		if (zOfUpVector < 0.6) {
			return true;
		}

		Vector3D position = getAgent().getPosition();
		return position.getZ() < 0.25;
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
	 * @return true if a penalty has been assigned
	 */
	public boolean hasPenalty()
	{
		return hasPenalty;
	}

	public String getStatusText()
	{
		return statusText;
	}

	public float getRunTime()
	{
		return runTime;
	}
}
